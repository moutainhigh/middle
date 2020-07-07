package com.njwd.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.admin.mapper.DataMaintenanceMapper;
import com.njwd.admin.mapper.PrimaryPaddingMapper;
import com.njwd.admin.mapper.PrimarySystemSettingMapper;
import com.njwd.admin.service.DataMaintenanceService;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.*;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.*;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.utils.DateUtils;
import com.njwd.utils.StringUtil;
import com.njwd.utils.idworker.IdWorker;
import org.dozer.util.IteratorUtils;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Chenfulian
 * @Description 数据维护 实现类
 * @Date 2020/02/10 15:10
 * @Version 1.0
 */
@Service
public class DataMaintenanceServiceImpl implements DataMaintenanceService {

    @Resource
    private DataMaintenanceMapper dataMaintenanceMapper;
    @Resource
    private PrimarySystemSettingMapper primarySystemSettingMapper;
    @Resource
    private PrimaryPaddingMapper primaryPaddingMapper;

    @Resource
    private IdWorker idWorker;

    @Override
    public List<ControlVo> getAllControl() {
        List<ControlVo> controlVoListResult = new ArrayList<>();
        //获取所有控件
        List<ControlVo> controlVoListTemp = dataMaintenanceMapper.getAllControl();
        //将查询结果组装成树形返回
        for (ControlVo controlVo:controlVoListTemp) {
            if (StringUtil.isEmpty(controlVo.getUpControlCode())) {
                controlVoListResult.add(controlVo);
            }
            else {
                for (ControlVo controlParentVo:controlVoListResult) {
                    if (controlVo.getUpControlCode().equals(controlParentVo.getControlCode())) {
                        List<ControlVo> childrenControlVoList = controlParentVo.getChildrenControlVo();
                        if (null == childrenControlVoList) {
                            childrenControlVoList = new ArrayList<>();
                        }
                        childrenControlVoList.add(controlVo);
                        controlParentVo.setChildrenControlVo(childrenControlVoList);
                        break;
                    }
                }
            }
        }

        return controlVoListResult;
    }

    @Override
    public List<ControlFormatVo> getFormatByControlCode(String controlCode) {
        return dataMaintenanceMapper.getFormatByControlCode(controlCode);
    }

    @Override
    public List<TableAttributeVo> getLinkableFields(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        DataTypeVo dataTypeVo = new DataTypeVo(enterpriseDataTypeDto.getDataType(),null);
        //查出该数据类型所有有关表格
        List<TableObj> relaTableNameList = getRelaTable(enterpriseDataTypeDto.getDataType());
        //根据rela表名查出所有字段
        List<TableAttributeVo> attributeList = primaryPaddingMapper.getTableAllAttributeVo(relaTableNameList);
        Map<String, TableAttributeVo> attributeMap = attributeList.stream().collect(Collectors.toMap(TableAttributeVo::getColumnName,Function.identity(),(value1,value2)->value2));
        //过滤已关联的字段
        //List<TableAttribute> linkedAttributeList = dataMaintenanceMapper.getLinkedFields(enterpriseDataTypeDto);
        //查询系统控件，用于过滤
        ControlDto controlDto = new ControlDto();
        controlDto.setControlCode(AdminConstant.Character.SYSTEM_CONTROL);
        List<ControlVo> controlTypeList = dataMaintenanceMapper.getChildControlByCode(controlDto);

        Iterator<TableAttributeVo> iterator = attributeList.iterator();
        while (iterator.hasNext()) {
            TableAttributeVo tableAttributeTemp = iterator.next();
            //注释过滤已关联的字段，2020/2/19
            //for (TableAttribute tableAttributeLinked:linkedAttributeList) {
//                if ((tableAttributeTemp.getTableName().equals(tableAttributeLinked.getTableName()))
//                && (tableAttributeTemp.getColumnName().equals(tableAttributeLinked.getColumnName())
//                || tableAttributeTemp.getColumnName().equals(AdminConstant.Db.THIRD_PREFIX+tableAttributeTemp.getDataType()+AdminConstant.Db.ID_SUFFIX)
//                || tableAttributeTemp.getColumnName().equals(AdminConstant.Db.APP_ID) || tableAttributeTemp.getColumnName().equals(AdminConstant.Db.ENTE_ID))) {
            //过滤字段编码（"third_"+dataType+"_id"）,app_id,ente_id,dataType+"_id"(回写到rela表主数据id)字段
            if (tableAttributeTemp.getColumnName().equals(AdminConstant.Db.THIRD_PREFIX+tableAttributeTemp.getDataType()+AdminConstant.Db.ID_SUFFIX)
                    ||tableAttributeTemp.getColumnName().equals(tableAttributeTemp.getDataType()+AdminConstant.Db.ID_SUFFIX)
                    || tableAttributeTemp.getColumnName().equals(AdminConstant.Db.APP_ID) || tableAttributeTemp.getColumnName().equals(AdminConstant.Db.ENTE_ID)) {
                attributeMap.remove(tableAttributeTemp.getColumnName());
                continue;
            }
            //}
            for (ControlVo controlVo : controlTypeList ) {
                if(!AdminConstant.Character.FIELD_CODE.equals(controlVo.getControlCode())&&tableAttributeTemp.getColumnName().equals(controlVo.getControlCode())){
                    attributeMap.remove(tableAttributeTemp.getColumnName());
                    break;
                }
            }
        }
        List<TableAttributeVo>  resAttributeList = attributeMap.values().stream().collect(Collectors.toList());
        return resAttributeList;
    }


    @Override
    public void saveControlPropertyDefault(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        ControlProperty controlProperty = new ControlProperty();
        controlProperty.setEnterpriseId(enterpriseDataTypeDto.getEnterpriseId());
        controlProperty.setDataType(enterpriseDataTypeDto.getDataType());
        //保存字段编码属性
        controlProperty.setControlCode(AdminConstant.Character.FIELD_CODE);
        controlProperty.setUserControlName(AdminConstant.Character.FIELD_CODE_DESC);
        controlProperty.setRemark(AdminConstant.Character.FIELD_CODE_DESC);
        controlProperty.setRequiredFlag(AdminConstant.Number.ONE);
        controlProperty.setTargetTable(AdminConstant.Db.BASE_PREFIX + controlProperty.getDataType() + AdminConstant.Db.RELA_SUFFIX);
        controlProperty.setTargetColumn(AdminConstant.Db.THIRD_PREFIX + controlProperty.getDataType() + AdminConstant.Db.ID_SUFFIX);
        controlProperty.setDataControlId(idWorker.nextId());

        List<ControlProperty> controlPropertyList = new ArrayList<>();
        controlPropertyList.add(controlProperty);
        dataMaintenanceMapper.insertControlProperty(controlPropertyList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public void saveControlProperty(ControlPropertyDto controlPropertyDto) {
        //对比数据库原有控件，得到insert、update、delete的控件列表
        List<ControlProperty> insertPropertyList = new ArrayList<>();
        List<ControlProperty> updatePropertyList = new ArrayList<>();
        List<ControlProperty> deletePropertyList = new ArrayList<>();
        //控件值新增或修改或删除列表
        List<ControlValue> insertValueList = new ArrayList<>();
        List<ControlValue> updateValueList = new ArrayList<>();
        List<ControlValue> deleteValueList = new ArrayList<>();

        //新控件属性列表
        List<ControlPropertyVo> newControlPropertyList = controlPropertyDto.getControlPropertyList();
        //查出原有控件属性
        List<ControlPropertyVo> oldControlPropertyList = dataMaintenanceMapper.getControlAndProperty(controlPropertyDto);

        Iterator<ControlPropertyVo> iteratorNew= newControlPropertyList.iterator();
        while (iteratorNew.hasNext()){
            ControlProperty newProperty = iteratorNew.next();
            if(AdminConstant.Character.FIELD_CODE.equals(newProperty.getControlCode())){
                newProperty.setTargetTable(AdminConstant.Db.BASE_PREFIX+controlPropertyDto.getDataType()+AdminConstant.Db.RELA_SUFFIX);
                newProperty.setTargetColumn(AdminConstant.Db.THIRD_PREFIX+controlPropertyDto.getDataType()+AdminConstant.Db.ID_SUFFIX);
            }else if(AdminConstant.Character.CREATE_TIME.equals(newProperty.getControlCode())){
                newProperty.setTargetTable(AdminConstant.Db.BASE_PREFIX+controlPropertyDto.getDataType()+AdminConstant.Db.RELA_SUFFIX);
                newProperty.setTargetColumn(AdminConstant.Character.CREATE_TIME);
            }else if(AdminConstant.Character.UPDATE_TIME.equals(newProperty.getControlCode())){
                newProperty.setTargetTable(AdminConstant.Db.BASE_PREFIX+controlPropertyDto.getDataType()+AdminConstant.Db.RELA_SUFFIX);
                newProperty.setTargetColumn(AdminConstant.Character.UPDATE_TIME);
            }
            if (StringUtil.isEmpty(newProperty.getDataControlId())) {
                continue;
            }
            Iterator<ControlPropertyVo> iteratorOld= oldControlPropertyList.iterator();
            while (iteratorOld.hasNext()){
                ControlProperty oldProperty = iteratorOld.next();
                if (newProperty.getDataControlId().equals(oldProperty.getDataControlId())) {
                    //全等，则不需要处理
                    if (newProperty.equals(oldProperty)) {
                        iteratorNew.remove();
                        iteratorOld.remove();
                    }
                    else {
                        updatePropertyList.add(newProperty);

                        //新控件值列表
                        List<ControlValue> newControlValueList = newProperty.getControlValueList();
                        //查出原有控件值
                        List<ControlValue> oldControlValueList = oldProperty.getControlValueList();

                        Iterator<ControlValue> iteratorValueNew= newControlValueList.iterator();
                        while (iteratorValueNew.hasNext()){
                            ControlValue newValue = iteratorValueNew.next();
                            if (StringUtil.isEmpty(newValue.getValueId())) {
                                continue;
                            }
                            Iterator<ControlValue> iteratorValueOld= oldControlValueList.iterator();
                            while (iteratorValueOld.hasNext()){
                                ControlValue oldValue = iteratorValueOld.next();
                                if (newValue.getValueId().equals(oldValue.getValueId())) {
                                    //对比所有字段，有无发生字段变更
                                    if (newValue.equals(oldValue)) {
                                        iteratorValueNew.remove();
                                        iteratorValueOld.remove();
                                    }
                                    else {
                                        updateValueList.add(newValue);
                                        iteratorValueNew.remove();
                                        iteratorValueOld.remove();
                                    }
                                }
                            }
                        }
                        //处理新增控件值的id
                        if (null != newControlValueList && newControlValueList.size() >0){
                            for (ControlValue controlValueTmp:newControlValueList) {
                                String id = idWorker.nextId();
                                controlValueTmp.setValueId(id);
                            }
                        }

                        insertValueList.addAll(newControlValueList);
                        deleteValueList.addAll(oldControlValueList);

                        iteratorNew.remove();
                        iteratorOld.remove();
                    }
                }
            }
        }
        insertPropertyList.addAll(newControlPropertyList);
        deletePropertyList.addAll(oldControlPropertyList);

        //处理insertlist updatelist deletelist的id和控件值
        //对新增的控件表单，所有控件值都是新增
        for (ControlProperty controlProperty:insertPropertyList) {
            controlProperty.setDataControlId(idWorker.nextId());
            controlProperty.setEnterpriseId(controlPropertyDto.getEnterpriseId());
            controlProperty.setDataType(controlPropertyDto.getDataType());
            List<ControlValue> controlValueList = controlProperty.getControlValueList();
            if (null != controlValueList && controlValueList.size() >0){
                for (ControlValue controlValueTmp:controlValueList) {
                    String id = idWorker.nextId();
                    controlValueTmp.setValueId(id);
                    controlValueTmp.setDataControlId(controlProperty.getDataControlId());
                }
                insertValueList.addAll(controlValueList);
            }
        }
        //deletelist 全都是删除
        for (ControlProperty controlProperty:deletePropertyList) {
            List<ControlValue> controlValueList = controlProperty.getControlValueList();
            if (null != controlValueList && controlValueList.size() >0){
                deleteValueList.addAll(controlValueList);
            }
        }

        //更新控件信息
        if (insertPropertyList.size() > 0) {
            dataMaintenanceMapper.insertControlProperty(insertPropertyList);
        }
        if (updatePropertyList.size() > 0) {
            dataMaintenanceMapper.updateControlProperty(updatePropertyList);
        }
        if (deletePropertyList.size() > 0) {
            dataMaintenanceMapper.deleteControlProperty(deletePropertyList);
        }
        //更新控件值信息
        if (insertValueList.size() > 0) {
            dataMaintenanceMapper.insertControlValue(insertValueList);
        }
        if (updateValueList.size() > 0) {
            dataMaintenanceMapper.updateControlValue(updateValueList);
        }
        if (deleteValueList.size() > 0) {
            dataMaintenanceMapper.deleteControlValue(deleteValueList);
        }

    }

    @Override
    public List<ControlPropertyVo> getControlAndProperty(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        List<ControlPropertyVo> controlPropertyVoList = dataMaintenanceMapper.getControlAndProperty(enterpriseDataTypeDto);
        return controlPropertyVoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public void addThirdDataToMidPlatTotal(EnterpriseAppDataTypeDto enterpriseAppDataTypeDto) {
        //检查该主数据是否已经进行过全量同步。一个主数据，只能有一个应用进行全量同步
        checkTotalMaintenceRecord(enterpriseAppDataTypeDto);
        //添加全量同步的记录
        //id为空，新增操作。否则为修改操作
        String id = idWorker.nextId();
        dataMaintenanceMapper.addTotalMaintenceRecord(id,enterpriseAppDataTypeDto);
        //获取主数据的rela表名
        List<TableObj> relaTableNameList = getRelaTable(enterpriseAppDataTypeDto.getDataType());
        //依次根据rela表名将第三方的数据同步到中台
        String midPlatAppId = AdminConstant.Db.MID_PLAT_APP_ID;
        for (TableObj relaTableObj:relaTableNameList) {
            //查询rela表关联的所有字段（除了app_id）
            List<TableAttribute> attributeList = dataMaintenanceMapper.getLinkedFields(enterpriseAppDataTypeDto);
            //把app_id字段放在第一个。（先移除app_id字段，再添加到新的list）
            List<TableAttribute> sortedAttributeList = new ArrayList<>();
            String appId = AdminConstant.Character.APP_ID;
            sortedListTableAttribute(attributeList,appId,sortedAttributeList);
            //把third_dataType_id字段放在第二个。（先移除third_dataType_id字段，再添加到新的list）
            String thirdDataTypeId = AdminConstant.Db.THIRD_PREFIX + enterpriseAppDataTypeDto.getDataType() + AdminConstant.Db.ID_SUFFIX;
            sortedListTableAttribute(attributeList,thirdDataTypeId,sortedAttributeList);
            //把third_ente_id字段放在第三个。（先移除third_ente_id字段，再添加到新的list）
            String thirdEnteId = AdminConstant.Db.THIRD_ENTE_ID;
            sortedListTableAttribute(attributeList,thirdEnteId,sortedAttributeList);
            //把ente_id字段放在第四个。（先移除third_ente_id字段，再添加到新的list）
            String enteId = AdminConstant.Db.ENTE_ID;
            sortedListTableAttribute(attributeList,enteId,sortedAttributeList);
            //把dataType_id字段
            String dataType_id = enterpriseAppDataTypeDto.getDataType() + AdminConstant.Db.ID_SUFFIX;
            sortedListTableAttribute(attributeList,dataType_id,sortedAttributeList);

            sortedAttributeList.addAll(attributeList);

            //拼接同步sql。只有appId改为中台应用的appId
            StringBuffer syncSql = new StringBuffer();
            //insert字段，取rela表全部字段
            StringBuffer insertFiledSql = new StringBuffer();
            //select字段，取rela表除app_id的其他全部字段
            StringBuffer selectFiledSql = new StringBuffer();
            for (int i = 0; i < sortedAttributeList.size(); i++) {
                if (i == 0 ) {
                    insertFiledSql.append(sortedAttributeList.get(i).getColumnName()).append(Constant.Character.COMMA);
                }
                else if (i == 1) {
                    insertFiledSql.append(sortedAttributeList.get(i).getColumnName()).append(Constant.Character.COMMA);
                    selectFiledSql.append(AdminConstant.Character.CONCAT_START).append(AdminConstant.Character.APP_ID).append(Constant.Character.COMMA)
                            .append(AdminConstant.Character.SINGLE_QUOTE).append(Constant.Character.UNDER_LINE).append(AdminConstant.Character.SINGLE_QUOTE)
                            .append(Constant.Character.COMMA).append(thirdDataTypeId).append(AdminConstant.Character.RIGHT_PARENTHESIS).append(Constant.Character.COMMA);
                }
                else if (i != sortedAttributeList.size() -1) {
                    insertFiledSql.append(sortedAttributeList.get(i).getColumnName()).append(Constant.Character.COMMA);
                    selectFiledSql.append(sortedAttributeList.get(i).getColumnName()).append(Constant.Character.COMMA);
                }
                else if (i == sortedAttributeList.size() -1) {
                    insertFiledSql.append(sortedAttributeList.get(i).getColumnName());
                    selectFiledSql.append(sortedAttributeList.get(i).getColumnName());
                }
            }

            //拼接同步sql。只有appId改为中台应用的appId
            syncSql.append(AdminConstant.Db.INSERT_INTO).append(relaTableObj.getTableName());
            syncSql.append(AdminConstant.Character.LEFT_PARENTHESIS).append(insertFiledSql).append(AdminConstant.Character.RIGHT_PARENTHESIS);
            syncSql.append(AdminConstant.Db.SELECT).append(AdminConstant.Character.SINGLE_QUOTE).append(midPlatAppId);
            syncSql.append(AdminConstant.Character.SINGLE_QUOTE).append(Constant.Character.COMMA);
            syncSql.append(selectFiledSql).append(AdminConstant.Db.FROM).append(relaTableObj.getTableName());
            syncSql.append(AdminConstant.Db.WHERE).append(AdminConstant.Character.ON_ENTE_ID).append(AdminConstant.Character.SINGLE_QUOTE);
            syncSql.append(enterpriseAppDataTypeDto.getEnterpriseId()).append(AdminConstant.Character.SINGLE_QUOTE);
            syncSql.append(AdminConstant.Character.AND).append(AdminConstant.Character.ON_APP_ID).append(AdminConstant.Character.SINGLE_QUOTE);
            syncSql.append(enterpriseAppDataTypeDto.getAppId()).append(AdminConstant.Character.SINGLE_QUOTE);

            String syncSqlString = syncSql.toString();
            System.out.printf(syncSqlString);

            //执行全量同步sql
            dataMaintenanceMapper.executeSql(syncSqlString);
        }
    }

    private void sortedListTableAttribute(List<TableAttribute> attributeList, String sortField, List<TableAttribute> sortedAttributeList) {
        //没有该字段则添加
        boolean hasFieldFlag = false;
        Iterator<TableAttribute> iterator = attributeList.iterator();
        while (iterator.hasNext()) {
            TableAttribute tableAttribute = iterator.next();
            if (tableAttribute.getColumnName().equals(sortField)) {
                hasFieldFlag = true;
                sortedAttributeList.add(tableAttribute);
                iterator.remove();
                break;
            }
        }
        if (!hasFieldFlag && !sortField.equals(AdminConstant.Db.THIRD_ENTE_ID)) {
            TableAttribute tableAttribute = new TableAttribute();
            tableAttribute.setColumnName(sortField);
            sortedAttributeList.add(tableAttribute);
        }
    }

    @Override
    public DataMaintenceVo getThirdDataForSelect(EnterpriseAppListDataTypeDto enterpriseAppListDataTypeDto) {
        DataMaintenceVo result = new DataMaintenceVo();
        //查出表单设计的属性，作为表头信息
        List<ControlPropertyVo> controlPropertyVoList = dataMaintenanceMapper.getControlAndProperty(enterpriseAppListDataTypeDto);
        result.setTitleList(controlPropertyVoList);
        List<ControlPropertyVo> controlPropertyListTemp = new ArrayList<>();
        controlPropertyListTemp.addAll(controlPropertyVoList);
        //返回app_id,third_datatype_id,third_ente_id,ente_id
        List<ControlPropertyVo> sortedPropertyList = new ArrayList<>();

        String appId = AdminConstant.Character.APP_ID;
        sortedListControlProperty(controlPropertyListTemp,appId,sortedPropertyList);
        //把third_dataType_id字段放在第二个。（先移除third_dataType_id字段，再添加到新的list）
        String thirdDataTypeId = AdminConstant.Db.THIRD_PREFIX + enterpriseAppListDataTypeDto.getDataType() + AdminConstant.Db.ID_SUFFIX;
        sortedListControlProperty(controlPropertyListTemp,thirdDataTypeId,sortedPropertyList);
        //把third_ente_id字段放在第三个。（先移除third_ente_id字段，再添加到新的list）
        String thirdEnteId = AdminConstant.Db.THIRD_ENTE_ID;
        sortedListControlProperty(controlPropertyListTemp,thirdEnteId,sortedPropertyList);
        //把ente_id字段放在第四个。（先移除third_ente_id字段，再添加到新的list）
        String enteId = AdminConstant.Db.ENTE_ID;
        sortedListControlProperty(controlPropertyListTemp,enteId,sortedPropertyList);

        sortedPropertyList.addAll(controlPropertyListTemp);

        //拼接查询sql
        StringBuffer sqlSb = new StringBuffer();
        //拼接要查询的字段
        for (int i = 0; i < sortedPropertyList.size(); i++) {
            String targetColumn = sortedPropertyList.get(i).getTargetColumn();
            //处理日期
            if(AdminConstant.Character.CREATE_TIME.equals(targetColumn.toLowerCase())){
                targetColumn = AdminConstant.Db.DATE_FORMAT+AdminConstant.Character.LEFT_PARENTHESIS+targetColumn+AdminConstant.Character.COMMA
                        +AdminConstant.Character.SINGLE_QUOTE+AdminConstant.Db.DATE_FORMAT_STR+AdminConstant.Character.SINGLE_QUOTE+AdminConstant.Character.RIGHT_PARENTHESIS
                        +AdminConstant.Character.SPACE+AdminConstant.Character.CREATE_TIME;
            }
            if(AdminConstant.Character.UPDATE_TIME.equals(targetColumn.toLowerCase())){
                targetColumn = AdminConstant.Db.DATE_FORMAT+AdminConstant.Character.LEFT_PARENTHESIS+targetColumn+AdminConstant.Character.COMMA
                        +AdminConstant.Character.SINGLE_QUOTE+AdminConstant.Db.DATE_FORMAT_STR+AdminConstant.Character.SINGLE_QUOTE+AdminConstant.Character.RIGHT_PARENTHESIS
                        +AdminConstant.Character.SPACE+AdminConstant.Character.UPDATE_TIME;
            }
            if (i != sortedPropertyList.size() -1) {
                sqlSb.append(Constant.Character.ALIAS_R).append(Constant.Character.POINT).append(targetColumn).append(Constant.Character.COMMA);
            }
            else if (i == sortedPropertyList.size() -1) {
                sqlSb.append(Constant.Character.ALIAS_R).append(Constant.Character.POINT).append(targetColumn);
            }
        }
        //动态拼接筛选条件的sql
        HashMap<String,String> searchContent = enterpriseAppListDataTypeDto.getSearchContentList();
        String searchSql = appendSearchSql(searchContent);

        Page<LinkedHashMap> dataList = null;
        //分页查询
        Page<LinkedHashMap> page = new Page<>(enterpriseAppListDataTypeDto.getPageNum(), enterpriseAppListDataTypeDto.getPageSize());
        dataList = dataMaintenanceMapper.getThirdDataForSelect(page,enterpriseAppListDataTypeDto,sqlSb.toString(),searchSql);
        //驼峰转下划线
        if (null != dataList.getRecords()) {
            resultCamelToUnderline(dataList);
        }

        result.setDataList(dataList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public void addThirdDataToMidPlatIncremental(DataMaintenceDto dataMaintenceDto) {
        //没有做全量同步之前不能做增量同步
        int record = dataMaintenanceMapper.getTotalSyncCount(dataMaintenceDto);
        if (record == 0) {
            throw new ServiceException(ResultCode.TOTAL_SYNC_RECORD_NOT_EXITS);
        }
        //查出表单设计的属性
        List<ControlPropertyVo> sortedPropertyList = new ArrayList<>();
        List<ControlPropertyVo> controlPropertyListTemp = dataMaintenanceMapper.getControlAndProperty(dataMaintenceDto);

        String appId = AdminConstant.Character.APP_ID;
        sortedListControlProperty(controlPropertyListTemp,appId,sortedPropertyList);
        //把third_dataType_id字段放在第二个。（先移除third_dataType_id字段，再添加到新的list）
        String thirdDataTypeId = AdminConstant.Db.THIRD_PREFIX + dataMaintenceDto.getDataType() + AdminConstant.Db.ID_SUFFIX;
        sortedListControlProperty(controlPropertyListTemp,thirdDataTypeId,sortedPropertyList);
        //把third_ente_id字段放在第三个。（先移除third_ente_id字段，再添加到新的list）
        String thirdEnteId = AdminConstant.Db.THIRD_ENTE_ID;
        sortedListControlProperty(controlPropertyListTemp,thirdEnteId,sortedPropertyList);
        //把ente_id字段放在第四个。（先移除third_ente_id字段，再添加到新的list）
        String enteId = AdminConstant.Db.ENTE_ID;
        sortedListControlProperty(controlPropertyListTemp,enteId,sortedPropertyList);

        sortedPropertyList.addAll(controlPropertyListTemp);

        //根据属性顺序插入数据，应用id为中台应用的appId
        dataMaintenceDto.setAppId(AdminConstant.Db.MID_PLAT_APP_ID);
        dataMaintenanceMapper.addThirdDataToMidPlatIncremental(sortedPropertyList,dataMaintenceDto);
    }

    private void sortedListControlProperty(List<ControlPropertyVo> controlPropertyListTemp, String sortField, List<ControlPropertyVo> sortedPropertyList) {
        //没有该字段则添加
        boolean hasFieldFlag = false;
        Iterator<ControlPropertyVo> iterator = controlPropertyListTemp.iterator();
        while (iterator.hasNext()) {
            ControlPropertyVo controlPropertyVo = iterator.next();
            if (controlPropertyVo.getTargetColumn().equals(sortField)) {
                hasFieldFlag = true;
                sortedPropertyList.add(controlPropertyVo);
                iterator.remove();
                break;
            }
        }
        if (!hasFieldFlag && !sortField.equals(AdminConstant.Db.THIRD_ENTE_ID)) {
            ControlPropertyVo controlPropertyVo = new ControlPropertyVo();
            controlPropertyVo.setTargetColumn(sortField);
            sortedPropertyList.add(controlPropertyVo);
        }
    }

    @Override
    public DataMaintenceVo getMidPlatData(EnterpriseAppListDataTypeDto enterpriseAppListDataTypeDto) {
        //调用 获取第三方数据 的接口
        List<String> appList = new ArrayList<>();
        appList.add(AdminConstant.Db.MID_PLAT_APP_ID);
        enterpriseAppListDataTypeDto.setAppIdList(appList);
        return getThirdDataForSelect(enterpriseAppListDataTypeDto);
    }

    @Override
    public void addMidPlatDataManual(LinkedHashMap dataMap) {
        //查出控件列表
        EnterpriseDataTypeDto enterpriseDataTypeDto = new EntePrimaryPaddingDto();
        enterpriseDataTypeDto.setEnterpriseId((String) dataMap.get(AdminConstant.Character.ENTERPRISE_ID));
        enterpriseDataTypeDto.setDataType((String) dataMap.get(AdminConstant.Character.DATA_TYPE_CAMEL));

        //控件列表排序
        List<ControlPropertyVo> sortedPropertyList = new ArrayList<>();
        List<ControlPropertyVo> controlPropertyListTemp = dataMaintenanceMapper.getControlAndProperty(enterpriseDataTypeDto);

        String appId = AdminConstant.Character.APP_ID;
        sortedListControlProperty(controlPropertyListTemp,appId,sortedPropertyList);
        //把third_dataType_id字段放在第二个。（先移除third_dataType_id字段，再添加到新的list）
        String thirdDataTypeId = AdminConstant.Db.THIRD_PREFIX + enterpriseDataTypeDto.getDataType() + AdminConstant.Db.ID_SUFFIX;
        sortedListControlProperty(controlPropertyListTemp,thirdDataTypeId,sortedPropertyList);
        //把third_ente_id字段放在第三个。（先移除third_ente_id字段，再添加到新的list）
        String thirdEnteId = AdminConstant.Db.THIRD_ENTE_ID;
        sortedListControlProperty(controlPropertyListTemp,thirdEnteId,sortedPropertyList);
        //把ente_id字段放在第四个。（先移除third_ente_id字段，再添加到新的list）
        String enteId = AdminConstant.Db.ENTE_ID;
        sortedListControlProperty(controlPropertyListTemp,enteId,sortedPropertyList);

        sortedPropertyList.addAll(controlPropertyListTemp);

        //第三方id生成
        dataMap.put(AdminConstant.Db.THIRD_PREFIX + dataMap.get(AdminConstant.Character.DATA_TYPE_CAMEL) + AdminConstant.Db.ID_SUFFIX,idWorker.nextId());
        //根据属性顺序插入数据，应用id为中台应用的appId,第三方企业id为当前企业id
        dataMap.put(AdminConstant.Db.APP_ID,AdminConstant.Db.MID_PLAT_APP_ID);
        String thirdEnteIdVal = (dataMap.get(AdminConstant.Character.THIRD_ENTE_ID)!=null && !"".equals(dataMap.get(AdminConstant.Character.THIRD_ENTE_ID).toString())) ? dataMap.get(AdminConstant.Character.THIRD_ENTE_ID).toString():enterpriseDataTypeDto.getEnterpriseId();
        dataMap.put(AdminConstant.Db.THIRD_ENTE_ID,thirdEnteIdVal);
        dataMap.put(AdminConstant.Db.ENTE_ID,enterpriseDataTypeDto.getEnterpriseId());
        dataMaintenanceMapper.addDataToMidPlatManual(sortedPropertyList,dataMap);
    }

    @Override
    public void updateMidPlatDataManual(LinkedHashMap dataMap) {
        //查出控件列表
        EnterpriseDataTypeDto enterpriseDataTypeDto = new EntePrimaryPaddingDto();
        enterpriseDataTypeDto.setEnterpriseId((String) dataMap.get(AdminConstant.Character.ENTERPRISE_ID));
        enterpriseDataTypeDto.setDataType((String) dataMap.get(AdminConstant.Character.DATA_TYPE_CAMEL));
        List<ControlPropertyVo> controlPropertyList = dataMaintenanceMapper.getControlAndProperty(enterpriseDataTypeDto);

        //appId取中台应用的id
        dataMap.put(AdminConstant.JOB_PARAM_KEY.APP_ID,AdminConstant.Db.MID_PLAT_APP_ID);
        dataMaintenanceMapper.updateMidPlatDataManual(controlPropertyList,dataMap);
    }

    @Override
    public void deleteMidPlatDataManual(LinkedHashMap dataMap) {
        //appId取中台应用的id
        dataMap.put(AdminConstant.JOB_PARAM_KEY.APP_ID,AdminConstant.Db.MID_PLAT_APP_ID);
        dataMaintenanceMapper.deleteMidPlatDataManual(dataMap);
    }

    @Override
    public Integer getIsFullSync(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        //appId取中台应用的id
        int record = dataMaintenanceMapper.getTotalSyncCount(enterpriseDataTypeDto);
        return record;
    }

    @Override
    public DataMaintenanceRecordVo getDataMaintenanceByDataType(DataMaintenanceRecordDto dataMaintenanceRecordDto) {
        DataMaintenanceRecordVo dataMaintenanceRecordVo = null;
        dataMaintenanceRecordDto.setSyncType(AdminConstant.Character.TOTAL);
        List<DataMaintenanceRecordVo> dataMaintenanceRecordVoList = dataMaintenanceMapper.getDataMaintenanceByDataType(dataMaintenanceRecordDto);
        if(dataMaintenanceRecordVoList!=null && dataMaintenanceRecordVoList.size()>AdminConstant.Number.ZERO){
            dataMaintenanceRecordVo = dataMaintenanceRecordVoList.get(0);
        }
        return dataMaintenanceRecordVo;
    }

    private void resultCamelToUnderline(Page<LinkedHashMap> dataList) {
        List<LinkedHashMap> newRecords = new ArrayList<>();
        List<LinkedHashMap> records = dataList.getRecords();
        for (LinkedHashMap record:records) {
            LinkedHashMap newRecord = new LinkedHashMap();
            Set<String> keySet = record.keySet();
            for (String key:keySet) {
                String camelToUnderKey = StringUtil.camelToUnderline(key);
                String value = record.get(key)!=null?record.get(key).toString():"";
//                if((camelToUnderKey.toLowerCase().equals(AdminConstant.Character.CREATE_TIME)
//                        ||camelToUnderKey.toLowerCase().equals(AdminConstant.Character.UPDATE_TIME))&&!"".equals(value)){
//                }
                newRecord.put(camelToUnderKey,value);
            }
            newRecords.add(newRecord);
        }
        dataList.setRecords(newRecords);
    }

    private LinkedHashMap mapCamelToUnderline(LinkedHashMap dataMap) {
        LinkedHashMap newMap = new LinkedHashMap();
        Set<String> keySet = dataMap.keySet();
        for (String key:keySet) {
            newMap.put(StringUtil.camelToUnderline(key),dataMap.get(key));
        }
        return newMap;
    }

    private String appendSearchSql(HashMap<String, String> searchContent) {
        StringBuffer searchSql = new StringBuffer();
        if (null != searchContent) {
            Set<String> keySet = searchContent.keySet();
            for (String key:keySet) {
                searchSql.append(AdminConstant.Character.AND).append(key).append(AdminConstant.Character.LIKE);
                searchSql.append(AdminConstant.Character.SINGLE_QUOTE).append(AdminConstant.Character.PERCENT)
                        .append(searchContent.get(key)).append(AdminConstant.Character.PERCENT).append(AdminConstant.Character.SINGLE_QUOTE);
            }
        }
        return searchSql.toString();
    }

    private void checkTotalMaintenceRecord(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        //查询全量同步的次数
        int record = dataMaintenanceMapper.getTotalSyncCount(enterpriseDataTypeDto);
        if (record > AdminConstant.Number.ZERO) {
            throw new ServiceException(ResultCode.TOTAL_SYNC_RECORD_EXITS);
        }
    }

    private List<TableObj> getRelaTable(String dataType) {
        DataTypeVo dataTypeVo = new DataTypeVo(dataType,null);
        //查出该数据类型所有有关表格
        List<TableObj> tableNameList = primarySystemSettingMapper.getTableByDataType(new ArrayList<DataTypeVo>(Arrays.asList(dataTypeVo)));
        //过滤base表，只剩下rela表
        Iterator<TableObj> tableObjIterator = tableNameList.iterator();
        while (tableObjIterator.hasNext()) {
            TableObj tableObj = tableObjIterator.next();
            if (!tableObj.getTableName().endsWith(AdminConstant.Db.RELA_SUFFIX)) {
                tableObjIterator.remove();
            }
        }
        return tableNameList;
    }
}

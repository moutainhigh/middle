package com.njwd.admin.service.impl;

import com.njwd.admin.mapper.PrimaryPaddingMapper;
import com.njwd.admin.mapper.PrimarySystemSettingMapper;
import com.njwd.admin.service.PrimaryPaddingService;
import com.njwd.admin.service.PrimarySystemSettingService;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.OneToManySql;
import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.TableObj;
import com.njwd.entity.admin.dto.EntePrimaryPaddingDto;
import com.njwd.entity.admin.dto.EnterpriseAppDataTypeDto;
import com.njwd.entity.admin.dto.EnterpriseDataTypeDto;
import com.njwd.entity.admin.dto.PrimarySystemDto;
import com.njwd.entity.admin.vo.AppTableAttributeVo;
import com.njwd.entity.admin.vo.DataTypeVo;
import com.njwd.entity.admin.vo.PrimaryPaddingVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.utils.AdminUtil;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: middle-data
 * @description: 数据填充实现类
 * @author: Chenfulian
 * @create: 2019-11-20 10:37
 **/
@Service
public class PrimaryPaddingServiceImpl implements PrimaryPaddingService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PrimaryPaddingServiceImpl.class);

    @Resource
    PrimarySystemSettingService primarySystemSettingService;

    @Resource
    private PrimaryPaddingMapper primaryPaddingMapper;

    @Resource
    private PrimarySystemSettingMapper primarySystemSettingMapper;

    /**
     * @Author Chenfulian
     * @Description 查询主系统可以修改填充的字段信息，即默认填充规则
     * @Date  2019/11/20 10:55
     * @Param [enterpriseDataTypeDto 企业id,数据类型]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.TableAttribute>>
     */
    @Override
    public List<PrimaryPaddingVo> getPrimarySysFields(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        //查询正常情况下 根据base、rela表相同的字段作为可修改字段
        String baseTableName = AdminUtil.getBaseTableByDataType(enterpriseDataTypeDto.getDataType());
        String relaTableName = AdminUtil.getRelaTableByDataType(enterpriseDataTypeDto.getDataType());
        List<PrimaryPaddingVo> tableAttributeList = primaryPaddingMapper.getModifiableField(enterpriseDataTypeDto.getEnterpriseId(),enterpriseDataTypeDto.getDataType(),baseTableName,relaTableName);
        //查询是否存在一对多的依赖关联表。如user依赖于role，一个user可对应于多个role，关联关系存在base_user_role base_user_role_rela
        //查询依赖存放的所有base表格
        List<String> baseTableList = primaryPaddingMapper.getAllRelyBaseTableName(enterpriseDataTypeDto.getDataType());
        String relaRelyTable;
        for (String baseRelyTable:baseTableList) {
            if (baseRelyTable.equals(baseTableName)) {
                continue;
            }
            relaRelyTable = AdminUtil.getRelaTableByBaseTable(baseRelyTable);
            //查出一对多依赖的可填充字段
            List<PrimaryPaddingVo> relyTableAttributeList = primaryPaddingMapper.getModifiableField(enterpriseDataTypeDto.getEnterpriseId(),enterpriseDataTypeDto.getDataType(),baseRelyTable,relaRelyTable);
            tableAttributeList.removeAll(relyTableAttributeList);
            tableAttributeList.addAll(relyTableAttributeList);
        }

        //该主数据的中台id、该主数据的第三方id 字段不能修改填充
        String baseId = AdminUtil.getBaseIdByDataType(enterpriseDataTypeDto.getDataType());
        String thirdId = AdminUtil.getThirdIdByDataType(enterpriseDataTypeDto.getDataType());
        Iterator<PrimaryPaddingVo> iterator = tableAttributeList.iterator();
        while (iterator.hasNext()) {
            PrimaryPaddingVo primaryPaddingVo = iterator.next();
            //将该主数据的企业id、中台id、该主数据的第三方id从可填充列表中移除
            if (primaryPaddingVo.getBaseColumn().equals(AdminConstant.Db.ENTE_ID) || primaryPaddingVo.getBaseColumn().equals(baseId) || primaryPaddingVo.getBaseColumn().equals(thirdId)) {
                iterator.remove();
                continue;
            }
            //将依赖主数据third_id移除，只留依赖主数据的中台id
            if (primaryPaddingVo.getBaseColumn().startsWith(AdminConstant.Db.THIRD_PREFIX) && primaryPaddingVo.getBaseColumn().endsWith(AdminConstant.Db.ID_SUFFIX)) {
                iterator.remove();
                continue;
            }
        }

        //依次检查每个字段，是否为融合规则字段
        List<String> jointRuleList = primaryPaddingMapper.getJointRuleByEnteDataType(enterpriseDataTypeDto);
        for (String jointRule:jointRuleList) {
            //拆分融合规则里的中台字段
            String[] fieldArray =  jointRule.trim().split(AdminConstant.Character.SEMICOLON);
            if (fieldArray != null && fieldArray.length > 0) {
                for (String field : fieldArray) {
                    String[] rules = field.split(Constant.Character.COMMA);
                    // 规则中中台数据字段
                    String baseField = rules[0];
                    Iterator<PrimaryPaddingVo> iteratorTemp = tableAttributeList.iterator();
                    while (iteratorTemp.hasNext()) {
                        PrimaryPaddingVo primaryPaddingVo = iteratorTemp.next();
                        //标记融合规则字段
                        if (primaryPaddingVo.getBaseColumn().equals(baseField)) {
                            primaryPaddingVo.setJointFlag("1");
                        }
                    }
                }
            }
        }
        return tableAttributeList;
    }

    /**
     * @Author Chenfulian
     * @Description 查询某企业某主数据的填充规则
     * @Date  2019/11/21 15:17
     * @Param [enterpriseAppDataTypeDto]
     * @return com.njwd.support.Result
     */
    @Override
    public List<PrimaryPaddingVo> getPrimayPadding(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        //查询主系统可修改字段
        List<PrimaryPaddingVo> primaryPaddingVoList = getPrimarySysFields(enterpriseDataTypeDto);
        //查询填充规则表，即由非主系统来的字段
        List<PrimaryPaddingVo> paddingRuleList = primaryPaddingMapper.getPrimayPadding(enterpriseDataTypeDto.getEnterpriseId(),enterpriseDataTypeDto.getDataType());
        //对比主系统可修改字段和已选择的填充规则，用填充字段替换主系统字段的应用字段来源，整理出实际上的填充规则
        for (PrimaryPaddingVo paddingRuleTemp:paddingRuleList) {
            for (PrimaryPaddingVo primaryFieldTemp:primaryPaddingVoList) {
                if (paddingRuleTemp.getBaseColumn().equals(primaryFieldTemp.getBaseColumn())) {
                    primaryFieldTemp.setPaddingId(paddingRuleTemp.getPaddingId());
                    primaryFieldTemp.setSourceAppId(paddingRuleTemp.getSourceAppId());
                    primaryFieldTemp.setSourceAppName(paddingRuleTemp.getSourceAppName());
                    primaryFieldTemp.setSourceColumn(paddingRuleTemp.getSourceColumn());
                    primaryFieldTemp.setSourceColumnDesc(paddingRuleTemp.getSourceColumnDesc());
                    break;
                }
            }
        }

        //根据页面搜索框输入值 返回结果
        if (StringUtil.isNotEmpty(enterpriseDataTypeDto.getSearchContent())) {
            Iterator<PrimaryPaddingVo> iterator = primaryPaddingVoList.iterator();
            while (iterator.hasNext()) {
                PrimaryPaddingVo primaryFieldTemp = iterator.next();
                //过滤不包含输入值的内容
                if ((!primaryFieldTemp.getBaseColumn().contains(enterpriseDataTypeDto.getSearchContent()))
                        && (!primaryFieldTemp.getBaseColumnDesc().contains(enterpriseDataTypeDto.getSearchContent()))) {
                    iterator.remove();
                }
            }

        }
        //处理填充规则的fieldSource字段，返回给前端展示
        dealPaddingFieldSource(primaryPaddingVoList);
        return primaryPaddingVoList;
    }

    /**
     * @Author Chenfulian
     * @Description 查询可行用于填充的应用
     * @Date  2019/11/21 9:43
     * @Param [enterpriseDataTypeDto]
     * @return com.njwd.support.Result
     */
    @Override
    public List<App> getSelectableApp(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        //查询该企业该数据类型 所有数据融合的应用
        List<App> jointAppList = primaryPaddingMapper.getJointAppByDataType(enterpriseDataTypeDto.getEnterpriseId(),enterpriseDataTypeDto.getDataType());
        return jointAppList;
    }

    /**
     * @Author Chenfulian
     * @Description 获取第三方应用字段，用于填充中台字段
     * @Date  2019/11/21 13:35
     * @Param [enterpriseAppDataTypeDto]
     * @return com.njwd.support.Result
     */
    @Override
    public List<AppTableAttributeVo> getAppField(EnterpriseAppDataTypeDto enterpriseAppDataTypeDto) {
        //获取所有有关表名（包括一对一的base/rela表、一对多的base/rela表）
        DataTypeVo dataTypeVo = new DataTypeVo(enterpriseAppDataTypeDto.getDataType(),null);
        List<TableObj> tableNameList = primarySystemSettingMapper.getTableByDataType(new ArrayList<DataTypeVo>(Arrays.asList(dataTypeVo)));
        //过滤base表，只剩下rela表
        Iterator<TableObj> tableObjIterator = tableNameList.iterator();
        while (tableObjIterator.hasNext()) {
            TableObj tableObj = tableObjIterator.next();
            if (!tableObj.getTableName().endsWith(AdminConstant.Db.RELA_SUFFIX)) {
                tableObjIterator.remove();
            }
        }
        //根据rela表名查出所有字段
        List<TableAttribute> attributeList = primaryPaddingMapper.getTableAllAttribute(tableNameList);
        //查询已填充的规则
        List<PrimaryPaddingVo> existsPaddingRuleList = getPrimayPadding(enterpriseAppDataTypeDto);

        Iterator<TableAttribute> iterator = attributeList.iterator();
        while (iterator.hasNext()) {
            TableAttribute attributeTemp = iterator.next();
            //过滤企业id，应用id，第三方原始id，该主数据的中台id
            String baseId = AdminUtil.getBaseIdByDataType(enterpriseAppDataTypeDto.getDataType());
            if (AdminConstant.Db.ENTE_ID.equals(attributeTemp.getColumnName()) || AdminConstant.Db.APP_ID.equals(attributeTemp.getColumnName()) || baseId.equals(attributeTemp.getColumnName())) {
                iterator.remove();
                continue;
            }
            if (attributeTemp.getColumnName().startsWith(AdminConstant.Db.THIRD_PREFIX) && attributeTemp.getColumnName().endsWith(AdminConstant.Db.ID_SUFFIX)) {
                iterator.remove();
                continue;
            }

            //过滤已用于填充的字段
            boolean continueFlag = false;
            for (PrimaryPaddingVo primaryPaddingVo:existsPaddingRuleList) {
                if (primaryPaddingVo.getSourceAppId().equals(enterpriseAppDataTypeDto.getAppId()) && primaryPaddingVo.getSourceColumn().equals(attributeTemp.getColumnName())) {
                    iterator.remove();
                    continueFlag = true;
                    break;
                }
            }
            if (continueFlag) {
                continue;
            }


            //根据页面搜索框输入值 返回结果
            if (StringUtil.isNotEmpty(enterpriseAppDataTypeDto.getSearchContent())) {
                //过滤不包含输入值的内容
                if ((!attributeTemp.getColumnName().contains(enterpriseAppDataTypeDto.getSearchContent()))
                        && (!attributeTemp.getColumnDesc().contains(enterpriseAppDataTypeDto.getSearchContent()))) {
                    iterator.remove();
                }
            }
        }
        //处理应用名称和id
        List<AppTableAttributeVo> appTableAttributeVos = new ArrayList<>();
        Iterator<TableAttribute> iteratorTemp = attributeList.iterator();
        while (iteratorTemp.hasNext()) {
            TableAttribute attributeTemp = iteratorTemp.next();

            AppTableAttributeVo appTableAttributeVo = new AppTableAttributeVo();
            appTableAttributeVo.setAppId(enterpriseAppDataTypeDto.getAppId());
            appTableAttributeVo.setAppName(enterpriseAppDataTypeDto.getAppName());

            appTableAttributeVo.setTableName(attributeTemp.getTableName());
            appTableAttributeVo.setColumnName(attributeTemp.getColumnName());
            appTableAttributeVo.setColumnDesc(attributeTemp.getColumnDesc());
            appTableAttributeVo.setTableName(attributeTemp.getTableName());

            appTableAttributeVos.add(appTableAttributeVo);
        }

        return appTableAttributeVos;
    }

    /**
     * @Author Chenfulian
     * @Description 更新填充规则
     * @Date  2019/11/21 16:00
     * @Param [primaryPaddingVoList]
     * @return com.njwd.support.Result
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updatePrimayPadding(EntePrimaryPaddingDto entePrimaryPaddingDto) {
        //检查任务启用状态和任务执行状态
        String checkType = AdminConstant.Task.PRIMARY_PADDING_SUFFIX;
        primarySystemSettingService.checkReadyForUpdate(entePrimaryPaddingDto, checkType);

        //对比之前保存的填充规则 和 deleteList/addList，得出应该保存的填充规则
        List<PrimaryPaddingVo> newPrimaryPaddingRule = compareAndGetNewPaddingRule(entePrimaryPaddingDto);
        entePrimaryPaddingDto.setPrimaryPaddingVoList(newPrimaryPaddingRule);
        //获取主系统信息
        PrimarySystemDto primarySystemDto = primarySystemSettingMapper.getPrimarySystem(entePrimaryPaddingDto);
        //第三方idList
        List<PrimaryPaddingVo> thirdPrimaryPaddingList = new ArrayList<>();
        //过滤主系统的填充字段，主系统的默认填充字段不写到填充规则表;第三方id需要自己处理添加
        dealThirdIdAndPrimarySysField(primarySystemDto,entePrimaryPaddingDto.getPrimaryPaddingVoList(),thirdPrimaryPaddingList);
        entePrimaryPaddingDto.getPrimaryPaddingVoList().addAll(thirdPrimaryPaddingList);

        //查询之前的填充规则表，即由非主系统来的字段
        List<PrimaryPaddingVo> paddingRuleList = primaryPaddingMapper.getPrimayPadding(entePrimaryPaddingDto.getEnterpriseId(),entePrimaryPaddingDto.getDataType());
        //对比之前的填充规则，如有非主系统字段改为主系统字段，将历史字段改回主系统字段
        List<PrimaryPaddingVo> resetPaddingList = new ArrayList<>();
        getResetPaddingList(paddingRuleList,entePrimaryPaddingDto.getPrimaryPaddingVoList(),resetPaddingList,primarySystemDto);

        if (!resetPaddingList.isEmpty()) {
            //一对一的base表，直接用update更新对应字段的数据
            Map<String,String> one2OneSqlMap = new HashMap<>();
            //一对多的base表，先清除多余数据，再用replace into更新对应字段的数据
            Map<String, OneToManySql> one2ManySqlMap = new HashMap<>();

            appendSqlGroupByTable(entePrimaryPaddingDto,resetPaddingList,one2OneSqlMap,one2ManySqlMap);
            if (!one2OneSqlMap.isEmpty()) {
                primaryPaddingMapper.executeOneToOnePadding(one2OneSqlMap);
            }
            if (!one2ManySqlMap.isEmpty()) {
                primaryPaddingMapper.executeOneToManyDelete(one2ManySqlMap);
                primaryPaddingMapper.executeOneToManyInsert(one2ManySqlMap);
            }
        }

        //填充规则写到填充表。先清除，再重新写入
        primaryPaddingMapper.delPaddingRule(entePrimaryPaddingDto.getEnterpriseId(),entePrimaryPaddingDto.getDataType());
        if (entePrimaryPaddingDto.getPrimaryPaddingVoList().size() > AdminConstant.Number.ZERO) {
            primaryPaddingMapper.insertPaddingRule(entePrimaryPaddingDto);
        }
        return true;
    }

    /**
     * 对比之前保存的填充规则 和 deleteList/addList，得出应该保存的填充规则
     * @author Chenfulian
     * @date 2019/12/18 16:51
     * @param 
     * @return 
     */
    private List<PrimaryPaddingVo> compareAndGetNewPaddingRule(EntePrimaryPaddingDto entePrimaryPaddingDto) {
        //查询上次的填充规则
        List<PrimaryPaddingVo> lastPrimaryPaddingRule = getPrimayPadding(entePrimaryPaddingDto);
        //根据deleteList/addList更新填充规则
        for (PrimaryPaddingVo deleteVo:entePrimaryPaddingDto.getDeleteList()) {
            for (PrimaryPaddingVo addVo:entePrimaryPaddingDto.getAddList()) {
                //更新=删除+新增
                if (addVo.getBaseColumn().equals(deleteVo.getBaseColumn())) {
                    for (PrimaryPaddingVo lastVo:lastPrimaryPaddingRule) {
                        //修改新的填充信息
                        if (lastVo.getBaseColumn().equals(deleteVo.getBaseColumn())) {
                            lastVo.setSourceAppId(addVo.getFieldSource().getAppId());
                            lastVo.setSourceAppName(addVo.getFieldSource().getAppName());
                            lastVo.setSourceColumn(addVo.getFieldSource().getColumnName());
                            lastVo.setSourceColumnDesc(addVo.getFieldSource().getColumnDesc());
                            break;
                        }
                    }
                    break;
                }
            }
        }

        //删除第三方id
        Iterator<PrimaryPaddingVo> iterator = lastPrimaryPaddingRule.iterator();
        while (iterator.hasNext()) {
            PrimaryPaddingVo paddingVoTemp = iterator.next();
            //处理id字段
            if (paddingVoTemp.getBaseColumn().startsWith(AdminConstant.Db.THIRD_PREFIX) && paddingVoTemp.getBaseColumn().endsWith(AdminConstant.Db.ID_SUFFIX)) {
                iterator.remove();
            }
        }
        return lastPrimaryPaddingRule;
    }

    /**
    * @Author Chenfulian
    * @Description 
    * @Date  2019/11/26 17:01
    * @Param 对比之前的填充规则，将改为主系统的取出来
    * @return 
    */
    private void getResetPaddingList(List<PrimaryPaddingVo> lastPaddingRuleList, List<PrimaryPaddingVo> nextPaddingRuleList, List<PrimaryPaddingVo> resetPaddingList, PrimarySystemDto primarySystemDto) {
        Iterator<PrimaryPaddingVo> iterator = lastPaddingRuleList.iterator();
        //除去被填充的字段，剩下的是默认取主系统的字段
        while (iterator.hasNext()) {
            PrimaryPaddingVo lastPaddingVo = iterator.next();
            for(PrimaryPaddingVo nextPaddingVo:nextPaddingRuleList) {
                if (lastPaddingVo.getBaseColumn().equals(nextPaddingVo.getBaseColumn())) {
                    iterator.remove();
                }
            }
        }
        resetPaddingList.addAll(lastPaddingRuleList);

        //设置来源系统为主系统
        for (PrimaryPaddingVo resetPaddingVo:resetPaddingList) {
            resetPaddingVo.setSourceAppId(primarySystemDto.getAppId());
            resetPaddingVo.setSourceAppName(AdminConstant.Character.PRIMARY_SYS);
            resetPaddingVo.setSourceColumn(resetPaddingVo.getBaseColumn());
        }

    }

    /**
    * @Author Chenfulian
    * @Description 
    * @Date  2019/11/26 16:44
    * @Param 处理第三方id，过滤主系统的默认的填充字段，主系统的填充字段不写到填充规则表
    * @return 
    */
    private void dealThirdIdAndPrimarySysField(PrimarySystemDto primarySystemDto, List<PrimaryPaddingVo> primaryPaddingVoList, List<PrimaryPaddingVo> thirdPrimaryPaddingList) {
        Iterator<PrimaryPaddingVo> iterator = primaryPaddingVoList.iterator();
        while (iterator.hasNext()) {
            PrimaryPaddingVo paddingVoTemp = iterator.next();
            //过滤主系统的默认的填充字段，即app id=主系统 且 来源字段=目标字段
            if (paddingVoTemp.getSourceAppId().equals(primarySystemDto.getAppId()) && paddingVoTemp.getBaseColumn().equals(paddingVoTemp.getSourceColumn())) {
                iterator.remove();
                continue;
            }
            //处理id字段
            if (paddingVoTemp.getBaseColumn().endsWith(AdminConstant.Db.ID_SUFFIX)) {
                //检查依赖的中台id的来源字段是否是同样的中台id。如role_id只能来自于其他系统的role_id
                if (!paddingVoTemp.getBaseColumn().equals(paddingVoTemp.getSourceColumn())) {
                    throw new ServiceException(String.format(ResultCode.PADDING_BASE_ID_SOURCE_ERROR.message,paddingVoTemp.getBaseColumn()),ResultCode.PADDING_BASE_ID_SOURCE_ERROR);
                }
                //如果是依赖的中台id，则加上对应的third_id字段
                PrimaryPaddingVo newPaddingRule = new PrimaryPaddingVo();
                newPaddingRule.setPaddingId(StringUtil.genUniqueKey());
                newPaddingRule.setSourceAppId(paddingVoTemp.getSourceAppId());
                newPaddingRule.setSourceColumn(AdminUtil.getThirdIdByBaseId(paddingVoTemp.getSourceColumn()));
                newPaddingRule.setBaseColumn(newPaddingRule.getSourceColumn());
                newPaddingRule.setTargetTable(paddingVoTemp.getTargetTable());
                thirdPrimaryPaddingList.add(newPaddingRule);
            }
            //生成填充规则的主键id
            String paddingId = StringUtil.genUniqueKey();
            paddingVoTemp.setPaddingId(paddingId);
        }
    }

    /**
     * @Author Chenfulian
     * @Description 查询填充规则，并且拼接sql
     * @Date  2019/11/25 19:06
     * @Param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealPrimaryPadding(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        //获取填充规则
        List<PrimaryPaddingVo> primaryPaddingVoList = primaryPaddingMapper.getPrimayPadding(enterpriseDataTypeDto.getEnterpriseId(),enterpriseDataTypeDto.getDataType());
        if (primaryPaddingVoList == null || primaryPaddingVoList.size() == 0) {
            return;
        }

        //一对一的base表，直接用update更新对应字段的数据
        Map<String,String> one2OneSqlMap = new HashMap<>();
        //一对多的base表，先清除多余数据，再用replace into更新对应字段的数据
        Map<String, OneToManySql> one2ManySqlMap = new HashMap<>();

        appendSqlGroupByTable(enterpriseDataTypeDto,primaryPaddingVoList, one2OneSqlMap, one2ManySqlMap);
        if (!one2OneSqlMap.isEmpty()) {
            primaryPaddingMapper.executeOneToOnePadding(one2OneSqlMap);
        }
        if (!one2ManySqlMap.isEmpty()) {
            primaryPaddingMapper.executeOneToManyDelete(one2ManySqlMap);
            primaryPaddingMapper.executeOneToManyInsert(one2ManySqlMap);
        }

    }

    /**
    * @Author Chenfulian
    * @Description 拼接sql,根据表格区分
     * 一对一的base表，直接用update更新对应字段的数据
     * 一对多的base表，先清除多余数据，再用replace into更新对应字段的数据
    * @Date  2019/11/26 16:56
    * @Param
    * @return 
    */
    private void appendSqlGroupByTable(EnterpriseDataTypeDto enterpriseDataTypeDto, List<PrimaryPaddingVo> primaryPaddingVoList, Map<String, String> one2OneSqlMap, Map<String, OneToManySql> one2ManySqlMap) {
        //按目标表格名称分
        Map<String,List<PrimaryPaddingVo>> paddingRuleByTableMap = groupByTable(primaryPaddingVoList);
        Set<String> keyTableSet = paddingRuleByTableMap.keySet();
        for (String keyTable:keyTableSet) {
            //获取一对一表名
            String one2OneTable = AdminUtil.getBaseTableByDataType(enterpriseDataTypeDto.getDataType());
            List<PrimaryPaddingVo> ruleByTableList = paddingRuleByTableMap.get(keyTable);
            if (keyTable.equals(one2OneTable)) {
                //一对一的base表，拼接update更新对应字段的数据的sql
                String updateSql = getOneToOneUpdateSql(enterpriseDataTypeDto,keyTable,ruleByTableList);
                one2OneSqlMap.put(keyTable,updateSql);
            } else {
                //一对多的base表，只需要看要填充的中台id，不需要看第三方id
                Iterator<PrimaryPaddingVo> iterator = ruleByTableList.iterator();
                while (iterator.hasNext()) {
                    PrimaryPaddingVo primaryPaddingVo = iterator.next();
                    if (primaryPaddingVo.getBaseColumn().startsWith(AdminConstant.Db.THIRD_PREFIX) && primaryPaddingVo.getBaseColumn().endsWith(AdminConstant.Db.ID_SUFFIX)) {
                        iterator.remove();
                    }
                }
                //一对多的base表，拼接两个sql:1.清除多余数据，2.replace into更新对应字段的数据
                OneToManySql oneToManySql = new OneToManySql();
                String deleteSql = getOneToManyDeleteSql(enterpriseDataTypeDto,keyTable,ruleByTableList.get(0));
                String insertSql = getOneToManyInsertSql(enterpriseDataTypeDto,keyTable,ruleByTableList.get(0));
                oneToManySql.setDeleteSql(deleteSql);
                oneToManySql.setInsertSql(insertSql);
                one2ManySqlMap.put(keyTable,oneToManySql);
            }

        }
    }

    /**
     *  拼接插入语句的left join 部分。即拼接 replace into table 后面的部分，包括插入字段、表连接
     * @param enterpriseDataTypeDto
     * @param keyTable
     * @param
     * @return
     */
    private String getOneToManyInsertSql(EnterpriseDataTypeDto enterpriseDataTypeDto, String keyTable, PrimaryPaddingVo primaryPaddingVo) {
        StringBuffer sb = new StringBuffer();
        //中台表别名 table0
        String baseTableAlias = AdminConstant.Db.TABLE + AdminConstant.Number.ZERO;
        //rela表别名 table1
        String relaTableAlias = AdminConstant.Db.TABLE + AdminConstant.Number.ONE;
        //本主数据的中台id
        String baseId = AdminUtil.getBaseIdByDataType(enterpriseDataTypeDto.getDataType());
        //一对多的数据类型的中台id
        String oneToManyId = primaryPaddingVo.getBaseColumn();
        //一对多的数据类型的第三方id
        String oneToManyThirdId = AdminUtil.getThirdIdByBaseId(oneToManyId);

        //查询待插入表格的所有字段
        TableObj tableObj = new TableObj();
        tableObj.setTableName(keyTable);
        List<TableAttribute> attributeList = primaryPaddingMapper.getTableAllAttribute(new ArrayList<TableObj>(Arrays.asList(tableObj)));
        //循环拼接要插入的字段
        StringBuffer targetFieldSb = new StringBuffer();
        for (int i = 0; i < attributeList.size(); i++) {
            if (i == attributeList.size() - 1) {
                targetFieldSb.append(attributeList.get(i).getColumnName());
            } else {
                targetFieldSb.append(attributeList.get(i).getColumnName()).append(Constant.Character.COMMA);
            }
        }

        //开始拼接sql.拼接插入的字段
        sb.append(AdminConstant.Character.SPACE).append(AdminConstant.Character.LEFT_PARENTHESIS);
        sb.append(targetFieldSb).append(AdminConstant.Character.RIGHT_PARENTHESIS);
        //select部分
        sb.append(AdminConstant.Db.SELECT).append(relaTableAlias).append(Constant.Character.POINT).append(Constant.Character.ASTERISK).append(AdminConstant.Character.SPACE).append(AdminConstant.Db.FROM);

        //数据来源 一对多的rela表
        sb.append(AdminConstant.Character.LEFT_PARENTHESIS).append(AdminConstant.Db.SELECT).append(targetFieldSb).append(AdminConstant.Character.SPACE);
        sb.append(AdminConstant.Db.FROM).append(AdminUtil.getRelaTableByBaseTable(keyTable)).append(AdminConstant.Character.SPACE);
        //来源字段的企业和app
        sb.append(AdminConstant.Db.WHERE);
        sb.append(AdminConstant.Db.ENTE_ID).append(Constant.Character.EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(enterpriseDataTypeDto.getEnterpriseId()).append(AdminConstant.Character.SINGLE_QUOTE);
        sb.append(AdminConstant.Character.AND).append(AdminConstant.Db.APP_ID).append(Constant.Character.EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(primaryPaddingVo.getSourceAppId()).append(AdminConstant.Character.SINGLE_QUOTE);
        sb.append(AdminConstant.Character.RIGHT_PARENTHESIS);
        sb.append(AdminConstant.Character.SPACE).append(relaTableAlias).append(AdminConstant.Character.SPACE);

        //left join目标表
        sb.append(AdminConstant.Character.LEFT_JOIN);
        sb.append(AdminConstant.Character.LEFT_PARENTHESIS).append(AdminConstant.Db.SELECT).append(targetFieldSb).append(AdminConstant.Character.SPACE);
        sb.append(AdminConstant.Db.FROM).append(keyTable).append(AdminConstant.Character.SPACE);
        sb.append(AdminConstant.Db.WHERE);
        sb.append(AdminConstant.Db.ENTE_ID).append(Constant.Character.EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(enterpriseDataTypeDto.getEnterpriseId()).append(AdminConstant.Character.SINGLE_QUOTE);
        sb.append(AdminConstant.Character.RIGHT_PARENTHESIS).append(AdminConstant.Character.SPACE).append(baseTableAlias).append(AdminConstant.Character.SPACE);

        //on 条件，本主数据的中台id,一对多的数据的中台id,一对多的数据的第三方id
        sb.append(AdminConstant.Character.ON).append(baseTableAlias).append(Constant.Character.POINT).append(baseId).append(Constant.Character.EQUALS).append(relaTableAlias).append(Constant.Character.POINT).append(baseId);
        sb.append(AdminConstant.Character.AND).append(baseTableAlias).append(Constant.Character.POINT).append(oneToManyId).append(Constant.Character.EQUALS).append(relaTableAlias).append(Constant.Character.POINT).append(oneToManyId);
        sb.append(AdminConstant.Character.AND).append(baseTableAlias).append(Constant.Character.POINT).append(oneToManyThirdId).append(Constant.Character.EQUALS).append(relaTableAlias).append(Constant.Character.POINT).append(oneToManyThirdId);
        sb.append(AdminConstant.Character.SPACE);
        //插入的where条件：企业id+left join得到的结果 rela表中台id不为空且第三方id不为空，base表中为空。即在rela表存在，但是base表不存在的记录
        sb.append(AdminConstant.Db.WHERE).append(relaTableAlias).append(Constant.Character.POINT).append(baseId).append(AdminConstant.Character.NOT_EQUALS);
        sb.append(AdminConstant.Character.SINGLE_QUOTE).append(AdminConstant.Character.SINGLE_QUOTE);
        sb.append(AdminConstant.Character.AND).append(relaTableAlias).append(Constant.Character.POINT).append(oneToManyThirdId).append(AdminConstant.Character.NOT_EQUALS);
        sb.append(AdminConstant.Character.SINGLE_QUOTE).append(AdminConstant.Character.SINGLE_QUOTE);
        sb.append(AdminConstant.Character.AND).append(baseTableAlias).append(Constant.Character.POINT).append(baseId).append(AdminConstant.Db.IS).append(AdminConstant.Db.NULL);

        return sb.toString();
    }

    /**
     *  拼接delete语句的left join 部分
     * @param enterpriseDataTypeDto
     * @param keyTable
     * @param
     * @return
     */
    private String getOneToManyDeleteSql(EnterpriseDataTypeDto enterpriseDataTypeDto, String keyTable, PrimaryPaddingVo primaryPaddingVo) {
        StringBuffer sb = new StringBuffer();
        //中台表别名 table0
        String baseTableAlias = AdminConstant.Db.TABLE + AdminConstant.Number.ZERO;
        //rela表别名 table1
        String relaTableAlias = AdminConstant.Db.TABLE + AdminConstant.Number.ONE;
        //本主数据的中台id
        String baseId = AdminUtil.getBaseIdByDataType(enterpriseDataTypeDto.getDataType());
        //一对多的数据类型的中台id
        String oneToManyId = primaryPaddingVo.getBaseColumn();
        //一对多的数据类型的第三方id
        String oneToManyThirdId = AdminUtil.getThirdIdByBaseId(oneToManyId);
        //left join
        sb.append(AdminConstant.Character.SPACE).append(AdminConstant.Character.LEFT_JOIN);
        //select 本主数据的中台id,一对多的数据的中台id,一对多的数据的第三方id
        sb.append(AdminConstant.Character.LEFT_PARENTHESIS).append(AdminConstant.Db.SELECT);
        sb.append(baseId).append(Constant.Character.COMMA).append(oneToManyId);
        sb.append(Constant.Character.COMMA).append(oneToManyThirdId);
        sb.append(AdminConstant.Db.FROM).append(AdminUtil.getRelaTableByBaseTable(keyTable));
        //来源字段的企业和app
        sb.append(AdminConstant.Db.WHERE);
        sb.append(AdminConstant.Db.ENTE_ID).append(Constant.Character.EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(enterpriseDataTypeDto.getEnterpriseId()).append(AdminConstant.Character.SINGLE_QUOTE);
        sb.append(AdminConstant.Character.AND).append(AdminConstant.Db.APP_ID).append(Constant.Character.EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(primaryPaddingVo.getSourceAppId()).append(AdminConstant.Character.SINGLE_QUOTE);
        sb.append(AdminConstant.Character.RIGHT_PARENTHESIS);
        sb.append(AdminConstant.Character.SPACE).append(relaTableAlias);
        //on 部分，与select部分一致
        sb.append(AdminConstant.Character.ON).append(baseTableAlias).append(Constant.Character.POINT).append(baseId).append(Constant.Character.EQUALS).append(relaTableAlias).append(Constant.Character.POINT).append(baseId);
        sb.append(AdminConstant.Character.AND).append(baseTableAlias).append(Constant.Character.POINT).append(oneToManyId).append(Constant.Character.EQUALS).append(relaTableAlias).append(Constant.Character.POINT).append(oneToManyId);
        sb.append(AdminConstant.Character.AND).append(baseTableAlias).append(Constant.Character.POINT).append(oneToManyThirdId).append(Constant.Character.EQUALS).append(relaTableAlias).append(Constant.Character.POINT).append(oneToManyThirdId);
        sb.append(AdminConstant.Character.SPACE);
        //delete的where条件：企业id+left join得到的结果 中台id为空，即在base表存在，但是rela表不存在的记录
        sb.append(AdminConstant.Db.WHERE).append(baseTableAlias).append(Constant.Character.POINT).append(AdminConstant.Db.ENTE_ID).append(Constant.Character.EQUALS);
        sb.append(AdminConstant.Character.SINGLE_QUOTE).append(enterpriseDataTypeDto.getEnterpriseId()).append(AdminConstant.Character.SINGLE_QUOTE);
        sb.append(AdminConstant.Character.AND).append(relaTableAlias).append(Constant.Character.POINT).append(baseId).append(AdminConstant.Db.IS).append(AdminConstant.Db.NULL);

        return sb.toString();
    }

    /**
     * 根据表名和填充规则，拼接一对一base表的update语句
     * @param enterpriseDataTypeDto 企业id，数据类型
     * @param keyTable 表名
     * @param ruleByTableList
     * @return
     */
    private String getOneToOneUpdateSql(EnterpriseDataTypeDto enterpriseDataTypeDto, String keyTable, List<PrimaryPaddingVo> ruleByTableList) {
        //left join sql
        StringBuffer joinSb = new StringBuffer();
        //set sql
        StringBuffer setSb = new StringBuffer();

        //按字段来源的应用进行细分
        Map<String,List<PrimaryPaddingVo>> paddingRuleBySourceAppMap = groupBySourceApp(ruleByTableList);
        Set<String> keyAppSet = paddingRuleBySourceAppMap.keySet();
        int i = AdminConstant.Number.ZERO;
        for (String keyApp:keyAppSet) {
            List<PrimaryPaddingVo> ruleByAppList = paddingRuleBySourceAppMap.get(keyApp);
            //一个应用为一个表.
            String appIndex = AdminConstant.Db.TABLE + String.valueOf(i+AdminConstant.Number.ONE);
            String baseId = AdminUtil.getBaseIdByDataType(enterpriseDataTypeDto.getDataType());
            //从字段来源表选取的字段。ente_id，中台id，填充来源字段
            StringBuffer srcTableFieldSb = new StringBuffer();
            srcTableFieldSb.append(AdminConstant.Db.ENTE_ID).append(Constant.Character.COMMA).append(baseId);
            //left join部分。ente_id与对应的中台相等
            joinSb.append(AdminConstant.Character.SPACE).append(AdminConstant.Character.LEFT_JOIN);

            // 字段来源表，根据ente_id,app_id查出数据
            joinSb.append(AdminConstant.Character.LEFT_PARENTHESIS).append(AdminConstant.Db.SELECT);
            for (int j = AdminConstant.Number.ZERO; j < ruleByAppList.size() ; j++) {
                PrimaryPaddingVo paddingVo = ruleByAppList.get(j);
                srcTableFieldSb.append(Constant.Character.COMMA).append(paddingVo.getSourceColumn());
            }
            joinSb.append(srcTableFieldSb).append(AdminConstant.Db.FROM).append(AdminUtil.getRelaTableByBaseTable(keyTable));
            joinSb.append(AdminConstant.Db.WHERE);
            //来源字段的企业和app
            joinSb.append(AdminConstant.Db.ENTE_ID).append(Constant.Character.EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(enterpriseDataTypeDto.getEnterpriseId()).append(AdminConstant.Character.SINGLE_QUOTE);
            joinSb.append(AdminConstant.Character.AND).append(AdminConstant.Db.APP_ID).append(Constant.Character.EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(keyApp).append(AdminConstant.Character.SINGLE_QUOTE);
            joinSb.append(AdminConstant.Character.RIGHT_PARENTHESIS);
            joinSb.append(AdminConstant.Character.SPACE).append(appIndex);

            //on条件
            joinSb.append(AdminConstant.Character.ON).append(AdminConstant.Db.TABLE).append(AdminConstant.Number.ZERO).append(Constant.Character.POINT).append(AdminConstant.Db.ENTE_ID).append(Constant.Character.EQUALS);
            joinSb.append(appIndex).append(Constant.Character.POINT).append(AdminConstant.Db.ENTE_ID);
            joinSb.append(AdminConstant.Character.AND).append(AdminConstant.Db.TABLE).append(AdminConstant.Number.ZERO).append(Constant.Character.POINT);
            joinSb.append(baseId).append(Constant.Character.EQUALS).append(appIndex).append(Constant.Character.POINT).append(baseId);

            for (int j = AdminConstant.Number.ZERO; j < ruleByAppList.size() ; j++) {
                PrimaryPaddingVo paddingVo = ruleByAppList.get(j);
                //set部分。set base.中台字段 = rela.来源字段
                if (i == AdminConstant.Number.ZERO && j == AdminConstant.Number.ZERO) {
                    setSb.append(AdminConstant.Db.SET);
                } else {
                    setSb.append(Constant.Character.COMMA);
                }
                setSb.append(AdminConstant.Db.TABLE).append(AdminConstant.Number.ZERO).append(Constant.Character.POINT).append(paddingVo.getBaseColumn());
                setSb.append(Constant.Character.EQUALS).append(appIndex).append(Constant.Character.POINT).append(paddingVo.getSourceColumn());
            }

            //处理完一个应用来源，加1
            i++;
        }
        joinSb.append(AdminConstant.Character.SPACE).append(setSb);
        joinSb.append(AdminConstant.Db.WHERE).append(AdminConstant.Db.TABLE).append(AdminConstant.Number.ZERO).append(Constant.Character.POINT);
        joinSb.append(AdminConstant.Db.ENTE_ID).append(Constant.Character.EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(enterpriseDataTypeDto.getEnterpriseId()).append(AdminConstant.Character.SINGLE_QUOTE);
        return joinSb.toString();
    }

    /**
     * @Author Chenfulian
     * @Description 根据来源应用 对填充规则分组
     * @Date  2019/11/25 11:41
     * @Param
     * @return
     */
    private Map<String, List<PrimaryPaddingVo>> groupBySourceApp(List<PrimaryPaddingVo> primaryPaddingVoList) {
        Map result = new HashMap<String,ArrayList<PrimaryPaddingVo>>();
        for (PrimaryPaddingVo primaryPaddingVo:primaryPaddingVoList) {
            if (result.get(primaryPaddingVo.getSourceAppId()) == null) {
                List<PrimaryPaddingVo> paddingListByTable = new ArrayList<>();
                paddingListByTable.add(primaryPaddingVo);
                result.put(primaryPaddingVo.getSourceAppId(),paddingListByTable);
            } else {
                List<PrimaryPaddingVo> primaryPaddingTmp = (List<PrimaryPaddingVo>) result.get(primaryPaddingVo.getSourceAppId());
                primaryPaddingTmp.add(primaryPaddingVo);
            }
        }
        return result;
    }

    /**
     * @Author Chenfulian
     * @Description 根据目标表 对填充规则分组
     * @Date  2019/11/25 11:41
     * @Param
     * @return
     */
    private Map<String, List<PrimaryPaddingVo>> groupByTable(List<PrimaryPaddingVo> primaryPaddingVoList) {
        Map result = new HashMap<String,ArrayList<PrimaryPaddingVo>>();
        for (PrimaryPaddingVo primaryPaddingVo:primaryPaddingVoList) {
            if (result.get(primaryPaddingVo.getTargetTable()) == null) {
                List<PrimaryPaddingVo> paddingListByTable = new ArrayList<>();
                paddingListByTable.add(primaryPaddingVo);
                result.put(primaryPaddingVo.getTargetTable(),paddingListByTable);
            } else {
                List<PrimaryPaddingVo> primaryPaddingTmp = (List<PrimaryPaddingVo>) result.get(primaryPaddingVo.getTargetTable());
                primaryPaddingTmp.add(primaryPaddingVo);
            }
        }
        return result;
    }

    /**
     * 处理填充规则的fieldSource字段，返回给前端展示
     * @author Chenfulian
     * @date 2019/12/18 16:45
     * @param
     * @return
     */
    private void dealPaddingFieldSource(List<PrimaryPaddingVo> primaryPaddingVoList) {
        for (PrimaryPaddingVo primaryFieldTemp:primaryPaddingVoList) {
            AppTableAttributeVo fieldSource = new AppTableAttributeVo();

            fieldSource.setAppId(primaryFieldTemp.getSourceAppId());
            fieldSource.setAppName(primaryFieldTemp.getSourceAppName());
            fieldSource.setColumnName(primaryFieldTemp.getSourceColumn());
            fieldSource.setColumnDesc(primaryFieldTemp.getSourceColumnDesc());

            primaryFieldTemp.setFieldSource(fieldSource);
        }
    }


}

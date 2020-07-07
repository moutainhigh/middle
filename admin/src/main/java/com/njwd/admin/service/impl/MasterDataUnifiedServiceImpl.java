package com.njwd.admin.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.admin.mapper.MasterDataUnifiedMapper;
import com.njwd.admin.mapper.PrimaryJointMapper;
import com.njwd.admin.mapper.RecordChangeMapper;
import com.njwd.admin.service.MasterDataUnifiedService;
import com.njwd.admin.service.PrimarySystemSettingService;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.PrimaryJoint;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.*;
import com.njwd.utils.StringUtil;
import com.njwd.utils.idworker.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author XiaFq
 * @Description EnterpriseAppServiceImpl 企业应用service实现类
 * @Date 2019/11/11 9:48 上午
 * @Version 1.0
 */
@Service
public class MasterDataUnifiedServiceImpl implements MasterDataUnifiedService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MasterDataUnifiedServiceImpl.class);

    @Resource
    private PrimarySystemSettingService primarySystemSettingService;

    @Resource
    MasterDataUnifiedMapper masterDataUnifiedMapper;

    @Resource
    PrimaryJointMapper primaryJointMapper;

    @Resource
    private IdWorker idWorker;

    @Resource
    RecordChangeMapper recordChangeMapper;

    /**
     * @return List<MasterDataAppVo>
     * @Author XiaFq
     * @Description 根据数据类型获取企业主数据统一的应用列表
     * @Date 2019/11/18 5:29 下午
     * @Param masterDataAppDto
     */
    @Override
    public List<MasterDataAppVo> getMasterDataAppList(MasterDataAppDto masterDataAppDto) {

        List<MasterDataAppVo> masterDataAppVoList = masterDataUnifiedMapper.getMasterDataAppList(masterDataAppDto);
        MasterDataAppVo baseVo = new MasterDataAppVo();
        baseVo.setKey(AdminConstant.Character.BASE);
        baseVo.setEnterpriseId(masterDataAppDto.getEnterpriseId());
        baseVo.setAppName(AdminConstant.Character.BASE_NAME);
        baseVo.setDataType(masterDataAppDto.getDataType());
        masterDataAppVoList.add(AdminConstant.Number.ZERO, baseVo);
        return masterDataAppVoList;
    }

    /**
     * 查询主数据统一预览页面列表
     *
     * @param masterDataUnifiedDto
     * @return Page<LinkedHashMap < String, LinkedHashMap < String, String>>>
     * @author XiaFq
     * @date 2019/12/16 11:57 上午
     */
    @Override
    public Page<LinkedHashMap<String, LinkedHashMap<String, String>>> getMasterDataList(MasterDataUnifiedDto masterDataUnifiedDto) {
        Page<LinkedHashMap<String, String>> dataList = null;
        List<String> keyList = new ArrayList<>();
        keyList.add("Base");
        Page<LinkedHashMap<String, LinkedHashMap<String, String>>> newPageList = new Page<>();
        List<LinkedHashMap<String, LinkedHashMap<String, String>>> newList = new ArrayList<>();
        try {
            // 查询需要主数据统一的应用
            MasterDataAppDto masterDataAppDto = new MasterDataAppDto();
            masterDataAppDto.setDataType(masterDataUnifiedDto.getDataType());
            masterDataAppDto.setEnterpriseId(masterDataUnifiedDto.getEnterpriseId());
            List<MasterDataAppVo> masterDataAppVoList = masterDataUnifiedMapper.getMasterDataAppList(masterDataAppDto);

            // 拼接需要查询的字段值
            StringBuffer selectFields = new StringBuffer();
            // base表 中台数据表
            masterDataUnifiedDto.setAliasName(AdminConstant.Character.BASE);
            String baseTableName = AdminConstant.Character.BASE_TABLE_START + masterDataAppDto.getDataType();
            masterDataUnifiedDto.setTableName(baseTableName);

            initQueryColumn(masterDataUnifiedDto, selectFields, null);
            String idQuery = masterDataUnifiedDto.getDataType() + AdminConstant.Character.END_ID;
            StringBuffer orderByStrBuffer = new StringBuffer();
            if (masterDataAppVoList != null && masterDataAppVoList.size() > 0) {
                StringBuffer leftJoinStr = new StringBuffer();
                int j = 0;
                for (int i = 0; i < masterDataAppVoList.size(); i++) {
                    MasterDataAppVo masterDataAppVo = masterDataAppVoList.get(i);
                    keyList.add(masterDataAppVo.getKey());
                    // 主数据
                    String relaTableName = AdminConstant.Character.BASE_TABLE_START + masterDataAppDto.getDataType() + AdminConstant.Character.RELA_TABLE_END;
                    masterDataUnifiedDto.setTableName(relaTableName);
                    if (StringUtil.isNotEmpty(masterDataAppVo.getSourceId())) {
                        j = 0;
                        masterDataUnifiedDto.setMasterDataAppId(masterDataAppVo.getAppId());
                        masterDataUnifiedDto.setAliasName(AdminConstant.Character.MASTER_DATA);
                        selectFields.append(Constant.Character.COMMA);
                        initQueryColumn(masterDataUnifiedDto, selectFields, null);
                        String orderByStr = AdminConstant.Character.BASE + Constant.Character.POINT + masterDataUnifiedDto.getDataType()
                                + Constant.Character.UNDER_LINE + AdminConstant.Character.ID + AdminConstant.Character.ORDER_DESC;
                        if (masterDataAppVoList.size() > 1) {
                            orderByStr += Constant.Character.COMMA;
                        }
                        orderByStrBuffer.append(orderByStr);
                    } else {
                        // 去掉主数据的下标，从1开始
                        j++;
                        String aliasName = AdminConstant.Character.RELA + j;
                        masterDataUnifiedDto.setAliasName(aliasName);
                        // 查询融合规则 拼接sql
                        PrimaryJointDto primaryJointDto = new PrimaryJointDto();
                        primaryJointDto.setEnterpriseId(masterDataAppVo.getEnterpriseId());
                        primaryJointDto.setDataType(masterDataAppVo.getDataType());
                        primaryJointDto.setAppId(masterDataAppVo.getAppId());
                        StringBuffer relaLeftJoinStr = new StringBuffer();

                        selectFields.append(Constant.Character.COMMA);
                        initQueryColumn(masterDataUnifiedDto, selectFields, null);

                        relaLeftJoinStr.append(AdminConstant.Character.LEFT_JOIN).append(relaTableName).append(AdminConstant.Character.SPACE).append(aliasName);
                        relaLeftJoinStr.append(AdminConstant.Character.ON).append(AdminConstant.Character.SPACE).append(AdminConstant.Character.BASE).append(Constant.Character.POINT).
                                append(idQuery).append(AdminConstant.Character.EQUALS_SPACE).append(aliasName).append(Constant.Character.POINT).append(idQuery);
                        relaLeftJoinStr.append(AdminConstant.Character.AND).append(aliasName).append(Constant.Character.POINT).append(AdminConstant.Character.APP_ID).append(AdminConstant.Character.EQUALS_SPACE).
                                append(AdminConstant.Character.SINGLE_QUOTE).append(masterDataAppVo.getAppId()).append(AdminConstant.Character.SINGLE_QUOTE);
                        relaLeftJoinStr.append(AdminConstant.Character.AND).append(aliasName).append(Constant.Character.POINT).append(AdminConstant.Character.ENTE_ID).append(AdminConstant.Character.EQUALS_SPACE).
                                append(AdminConstant.Character.SINGLE_QUOTE).append(masterDataAppVo.getEnterpriseId()).append(AdminConstant.Character.SINGLE_QUOTE);
                        leftJoinStr.append(relaLeftJoinStr);
                        masterDataUnifiedDto.setLeftJoinSql(leftJoinStr.toString());
                        String orderByStr = aliasName + Constant.Character.POINT + masterDataUnifiedDto.getDataType()
                                + Constant.Character.UNDER_LINE + AdminConstant.Character.ID + AdminConstant.Character.ORDER_DESC;
                        if (j < masterDataAppVoList.size() - 1) {
                            orderByStr += Constant.Character.COMMA;
                        }
                        orderByStrBuffer.append(orderByStr);
                    }

                }
            } else {
                String orderByStr = masterDataUnifiedDto.getAliasName() + Constant.Character.POINT + masterDataUnifiedDto.getDataType()
                        + Constant.Character.UNDER_LINE + AdminConstant.Character.ID + AdminConstant.Character.ORDER_DESC;
                orderByStrBuffer.append(orderByStr);
            }
            masterDataUnifiedDto.setSelectFields(selectFields.toString());
            masterDataUnifiedDto.setOrderByStr(orderByStrBuffer.toString());
            // 查询
            Page<LinkedHashMap<String, String>> page = masterDataUnifiedDto.getPage();
            page.setOptimizeCountSql(false);
            page.setSearchCount(false);
            // 构造搜索条件
            StringBuffer queryConditionBuffer = new StringBuffer();
            String queryCondition = masterDataUnifiedDto.getQueryCondition();
            String queryKey = masterDataUnifiedDto.getQueryKey();
            if (StringUtil.isNoneBlank(queryCondition) && StringUtil.isNotEmpty(queryKey)) {
                getQueryCondition(queryConditionBuffer, masterDataUnifiedDto.getSelectFields(), queryCondition);
                String condition = AdminConstant.Character.AND + AdminConstant.Character.LEFT_PARENTHESIS + queryConditionBuffer.toString() + AdminConstant.Character.RIGHT_PARENTHESIS + AdminConstant.Character.SPACE;
                condition = condition.replace(AdminConstant.Character.BASE + Constant.Character.POINT, queryKey + Constant.Character.POINT);
                masterDataUnifiedDto.setQueryConditionStr(condition);
            }
            dataList = masterDataUnifiedMapper.getMasterDataList(page, masterDataUnifiedDto);
            Long count = masterDataUnifiedMapper.getMasterDataListCount(masterDataUnifiedDto);
            dataList.setTotal(count);
            // 查询列表下拉展示的信息
            List<Map<String, String>> mapList = masterDataUnifiedMapper.getColumnDisplayDescList(masterDataUnifiedDto);
            Map<String, String> tipsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(mapList)) {
                mapList.stream().forEach(map -> {
                    tipsMap.put(map.get(AdminConstant.Character.COLUMN_ALIAS),map.get(AdminConstant.Character.COLUMN_DESC));
                });
            }
            // 处理数据
            dealData(dataList, keyList, newList, newPageList, tipsMap);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("getMasterDataList error,{}", e.getMessage());
        }

        return newPageList;
    }

    /**
     * 处理返回的分页数据
     *
     * @param dataList,
     * @param keyList,
     * @param newList,
     * @param newPageList
     * @return void
     * @author XiaFq
     * @date 2019/12/16 11:56 上午
     */
    private void dealData(Page<LinkedHashMap<String, String>> dataList, List<String> keyList, List<LinkedHashMap<String, LinkedHashMap<String, String>>> newList, Page<LinkedHashMap<String, LinkedHashMap<String, String>>> newPageList, Map<String, String> tipsMap) {
        if (dataList != null && dataList.getRecords() != null && dataList.getSize() > 0) {
            List<LinkedHashMap<String, String>> oldList = dataList.getRecords();
            oldList.stream().forEach(old -> {
                Iterator iter = old.entrySet().iterator();
                LinkedHashMap<String, LinkedHashMap<String, String>> newMap = new LinkedHashMap<>();
                ListIterator<String> keyIterator = keyList.listIterator();
                while (keyIterator.hasNext()) {
                    String keyName = keyIterator.next();
                    LinkedHashMap<String, String> newLinkedHashMap = newMap.get(keyName);
                    if (newLinkedHashMap == null) {
                        newLinkedHashMap = new LinkedHashMap<>();
                    }
                    String displayColumn = Constant.Character.NULL_VALUE;
                    JSONObject tipsJson = new JSONObject(new LinkedHashMap());
                    if (newLinkedHashMap.get(AdminConstant.Character.TIPS) != null) {
                        JSONObject tipsJsonOld = JSONObject.parseObject(newLinkedHashMap.get(AdminConstant.Character.TIPS));
                        // 为了排序所以重新赋值
                        tipsJson.putAll(tipsJsonOld);

                    }
                    int count = 0;
                    if (newLinkedHashMap.get(AdminConstant.Character.DISPLAY_COLUMN) != null) {
                        count++;
                        displayColumn = newLinkedHashMap.get(AdminConstant.Character.DISPLAY_COLUMN);
                    }

                    newMap.put(keyName,newLinkedHashMap);
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        if (key != null && key.toString().endsWith(keyName)) {
                            count++;
                            if (value == null) {
                                value = Constant.Character.NULL_VALUE;
                            }
                            newLinkedHashMap.put(key.toString(), value.toString());
                            if (count < 4) {
                                if (!value.equals(Constant.Character.NULL_VALUE) && !displayColumn.equals(Constant.Character.NULL_VALUE)) {
                                    //处理连接符（逗号）
                                    displayColumn += Constant.Character.COMMA;
                                }
                                displayColumn += value;
                                String tipName = key.toString().replace(keyName,Constant.Character.NULL_VALUE);
                                tipsJson.put(tipsMap.get(tipName), value);
                                newLinkedHashMap.put(AdminConstant.Character.DISPLAY_COLUMN, displayColumn);
                                newLinkedHashMap.put(AdminConstant.Character.TIPS, tipsJson.toString());
                            }
                            newMap.put(keyName, newLinkedHashMap);
                        } else {
                            newLinkedHashMap = new LinkedHashMap<>();
                            displayColumn = Constant.Character.NULL_VALUE;
                            tipsJson = new JSONObject(new LinkedHashMap());
                            if (value == null) {
                                value = Constant.Character.NULL_VALUE;
                            }
                            newLinkedHashMap.put(key.toString(), value.toString());
                            if (!value.equals(Constant.Character.NULL_VALUE) && !displayColumn.equals(Constant.Character.NULL_VALUE)) {
                                //处理连接符（逗号）
                                displayColumn += Constant.Character.COMMA;
                            }
                            displayColumn += value;
                            newLinkedHashMap.put(AdminConstant.Character.DISPLAY_COLUMN, displayColumn);
                            // 先获取下一个值，构造map
                            if (keyIterator.hasNext()) {
                                String nextKeyName = keyIterator.next();
                                String tipName = key.toString().replace(nextKeyName,Constant.Character.NULL_VALUE);
                                tipsJson.put(tipsMap.get(tipName), value);
                                newLinkedHashMap.put(AdminConstant.Character.TIPS, tipsJson.toString());
                                newMap.put(nextKeyName, newLinkedHashMap);
                                // 上一步迭代过了，往上走一步，回到正常迭代位置
                                keyIterator.previous();
                            }
                            break;
                        }
                    }
                }
                newList.add(newMap);
            });
            newPageList.setRecords(newList);
            newPageList.setCurrent(dataList.getCurrent());
            newPageList.setSize(dataList.getSize());
            newPageList.setTotal(dataList.getTotal());
            newPageList.setPages(dataList.getPages());
        }
    }

    /**
     * @return int -1 操作失败 0 操作成功
     * @Author XiaFq
     * @Description 保存或者更新数据统一规则
     * @Date 2019/11/20 9:46 上午
     * @Param [primaryJointDtoList]
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveOrUpdatePrimaryJoint(PrimaryJointDto primaryJointDto) {
        //检查任务启用状态和任务执行状态
        EnterpriseDataTypeDto enterpriseDataTypeDto = new EnterpriseDataTypeDto();
        enterpriseDataTypeDto.setEnterpriseId(primaryJointDto.getEnterpriseId());
        enterpriseDataTypeDto.setDataType(primaryJointDto.getDataType());
        String checkType = AdminConstant.Task.PRIMARY_JOINT_SUFFIX;
        primarySystemSettingService.checkReadyForUpdate(enterpriseDataTypeDto, checkType);

        try {
            // 先判断操作数据是否存在，存在更新，不存在插入
            PrimaryJoint oldPojo = primaryJointMapper.selectPrimaryJoint(primaryJointDto);
            if (oldPojo != null) {
                // 如果传入的expression为空，则没有融合规则就删除
                if (StringUtil.isEmpty(primaryJointDto.getExpression())) {
                    primaryJointMapper.deletePrimaryJoint(oldPojo.getJointId());
                } else {
                    primaryJointDto.setExpression(primaryJointDto.getExpression());
                    primaryJointDto.setJointId(oldPojo.getJointId());
                    primaryJointMapper.updatePrimaryJoint(primaryJointDto);
                }
            } else {
                // 新增的话，只有当表达式不为空才会保存融合规则
                if (StringUtil.isNotEmpty(primaryJointDto.getExpression())) {
                    String jointId = idWorker.nextId();
                    primaryJointDto.setJointId(jointId);
                    primaryJointDto.setCreateTime(new Date());
                    primaryJointMapper.insertPrimaryJoint(primaryJointDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("saveOrUpdatePrimaryJoint error,{}", e.getMessage());
            return AdminConstant.Number.ADD_FAILED;
        }
        return AdminConstant.Number.ADD_SUCCESS;
    }

    /**
     * @return java.util.List<com.njwd.entity.admin.vo.PrimaryJointVo>
     * @Author XiaFq
     * @Description 去新增或者编辑数据统一规则
     * @Date 2019/11/20 1:39 下午
     * @Param [primaryJointDto]
     */
    @Override
    public List<PrimaryJointVo> toSaveOrUpdatePrimaryJoint(PrimaryJointDto primaryJointDto) {
        PrimaryJoint primaryJoint = primaryJointMapper.selectPrimaryJoint(primaryJointDto);
        List<PrimaryJointVo> primaryJointVoList = new ArrayList<>();
        if (primaryJoint != null) {
            String expression = primaryJoint.getExpression();
            // 才分表达式
            if (StringUtil.isNotEmpty(expression)) {
                String[] logicExpression = expression.split(AdminConstant.Character.SEMICOLON);
                if (logicExpression != null && logicExpression.length > 0) {
                    for (String e : logicExpression) {
                        PrimaryJointVo primaryJointVo = new PrimaryJointVo();
                        primaryJointVo.setEnterpriseId(primaryJoint.getEnterpriseId());
                        primaryJointVo.setAppId(primaryJoint.getAppId());
                        primaryJointVo.setDataType(primaryJoint.getDataType());
                        primaryJointVo.setJointId(primaryJoint.getJointId());
                        String[] fields = e.split(Constant.Character.COMMA);
                        if (fields != null && fields.length > 0) {
                            String baseField = fields[0];
                            primaryJointVo.setBaseField(baseField);
                            String relaField = fields[1];
                            if (relaField != null && relaField.indexOf(AdminConstant.Character.LOGIC_AND) > 0) {
                                primaryJointVo.setLogicalRelationship(AdminConstant.Character.LOGIC_AND);
                            }

                            if (relaField != null && relaField.indexOf(AdminConstant.Character.LOGIC_OR) > 0) {
                                primaryJointVo.setLogicalRelationship(AdminConstant.Character.LOGIC_OR);
                            }
                            primaryJointVo.setRelaField(relaField.replace(AdminConstant.Character.LOGIC_AND, Constant.Character.NULL_VALUE).replace(AdminConstant.Character.LOGIC_OR, Constant.Character.NULL_VALUE));
                        }
                        primaryJointVoList.add(primaryJointVo);
                    }
                }
            }
        }
        return primaryJointVoList;
    }

    @Override
    public List<TableAttributeVo> selectPrimaryJointFields(String tableName) {
        return masterDataUnifiedMapper.selectPrimaryJointFields(tableName);
    }

    /**
     * @return com.njwd.entity.admin.vo.PrimaryJointDictVo
     * @Author XiaFq
     * @Description 查询数据统一字典数据
     * @Date 2019/11/20 2:20 下午
     * @Param [dataType]
     */
    @Override
    public PrimaryJointDictVo selectPrimaryJointDict(String dataType) {
        String baseTableName = AdminConstant.Character.BASE_TABLE_START + dataType;
        String relaTableName = AdminConstant.Character.BASE_TABLE_START + dataType + AdminConstant.Character.RELA_TABLE_END;
        // 查询中台下拉字段列表
        List<TableAttributeVo> baseFieldList = masterDataUnifiedMapper.selectPrimaryJointFields(baseTableName);
        List<TableAttributeVo> relaFieldList = masterDataUnifiedMapper.selectPrimaryJointFields(relaTableName);
        PrimaryJointDictVo primaryJointDictVo = new PrimaryJointDictVo();
        primaryJointDictVo.setBaseColumnList(baseFieldList);
        primaryJointDictVo.setRelaColumnList(relaFieldList);
        primaryJointDictVo.setLogicMap(AdminConstant.Logic.LOGIC_MAP);
        return primaryJointDictVo;
    }

    /**
     * @return java.util.List<java.util.HashMap < java.lang.String, java.lang.String>>
     * @Author XiaFq
     * @Description 根据视角查询匹配数据
     * @Date 2019/11/20 4:36 下午
     * @Param [dataMatchDto]
     */
    @Override
    public Page<LinkedHashMap<String,Object>> getDataListByPerspective(DataMatchDto dataMatchDto) {
        Page<LinkedHashMap<String, String>> dataList = null;
        Page<LinkedHashMap<String,Object>> newPageList = new Page<>();
        String master = Constant.Character.NULL_VALUE;
        String slave = Constant.Character.NULL_VALUE;
        try {
            // 必须先配置了融合规则才能进行数据匹配
            String perspective = dataMatchDto.getPerspective();
            StringBuffer queryColumnStr = new StringBuffer();
            String masterTableName = null;
            String slaveTableName = null;
            // 设置主表别名
            if (AdminConstant.Character.MID_PLAT_PERSPECTIVE.equals(perspective)) {
                // 中台视角
                dataMatchDto.setAliasName(AdminConstant.Character.BASE);
                // 设置主表名
                masterTableName = AdminConstant.Character.BASE_TABLE_START + dataMatchDto.getDataType();
                // 设置从表名
                slaveTableName = AdminConstant.Character.BASE_TABLE_START + dataMatchDto.getDataType() + AdminConstant.Character.RELA_TABLE_END;
                dataMatchDto.setMasterTableName(masterTableName);
                dataMatchDto.setSlaveTableName(slaveTableName);
                master = AdminConstant.Character.BASE;
                slave = AdminConstant.Character.RELA;
            } else if (AdminConstant.Character.APP_PERSPECTIVE.equals(perspective)) {
                // 应用视角
                dataMatchDto.setAliasName(AdminConstant.Character.BASE);
                // 设置主表名
                masterTableName = AdminConstant.Character.BASE_TABLE_START + dataMatchDto.getDataType() + AdminConstant.Character.RELA_TABLE_END;
                // 设置从表名
                slaveTableName = AdminConstant.Character.BASE_TABLE_START + dataMatchDto.getDataType();
                dataMatchDto.setMasterTableName(masterTableName);
                dataMatchDto.setSlaveTableName(slaveTableName);
                master = AdminConstant.Character.RELA;
                slave = AdminConstant.Character.BASE;
                dataMatchDto.setFieldAlias(master);
            }

            // 设置主表查询字段
            dataMatchDto.setTableName(masterTableName);
            initQueryColumn(dataMatchDto, queryColumnStr, perspective);
            // 查询融合规则 拼接sql
            PrimaryJointDto dto = new PrimaryJointDto();
            dto.setEnterpriseId(dataMatchDto.getEnterpriseId());
            dto.setDataType(dataMatchDto.getDataType());
            dto.setAppId(dataMatchDto.getAppId());
//            PrimaryJoint primaryJoint = primaryJointMapper.selectPrimaryJoint(dto);
            // 不需要查询，为了改代码逻辑 给一条默认的
            PrimaryJoint primaryJoint = new PrimaryJoint();
            primaryJoint.setAppId(dataMatchDto.getAppId());
            primaryJoint.setEnterpriseId(dataMatchDto.getEnterpriseId());
            primaryJoint.setDataType(dataMatchDto.getDataType());
            String id = dataMatchDto.getDataType() + AdminConstant.Character.END_ID;
            String expression = id + Constant.Character.COMMA + id;
            primaryJoint.setExpression(expression);
            StringBuffer leftJoinStr = new StringBuffer();
            StringBuffer allLeftJoinStr = new StringBuffer();
            // 从表逻辑表达式
            if (primaryJoint != null && StringUtil.isNotEmpty(primaryJoint.getExpression())) {
                queryColumnStr.append(Constant.Character.COMMA);
                dataMatchDto.setTableName(slaveTableName);
                dataMatchDto.setAliasName(AdminConstant.Character.RELA);
                if (AdminConstant.Character.APP_PERSPECTIVE.equals(perspective)) {
                    dataMatchDto.setFieldAlias(slave);
                } else {
                    dataMatchDto.setFieldAlias(AdminConstant.Character.RELA);
                }
                initQueryColumn(dataMatchDto, queryColumnStr, perspective);
                // 获取left join
                getLeftJoinStr(primaryJoint, allLeftJoinStr, leftJoinStr, AdminConstant.Character.RELA,dataMatchDto.getDataType(), dataMatchDto);
                dataMatchDto.setLeftJoinSql(leftJoinStr.toString());
            } else {
                // TODO 加入配置监控
            }

            dataMatchDto.setSelectFields(queryColumnStr.toString());
            String orderByStr = AdminConstant.Character.RELA + Constant.Character.POINT + dataMatchDto.getDataType()
                    + Constant.Character.UNDER_LINE + AdminConstant.Character.ID + AdminConstant.Character.ORDER_DESC;
            dataMatchDto.setOrderByStr(orderByStr);
            // 查询
            Page<LinkedHashMap<String, String>> page = dataMatchDto.getPage();
            // 构造搜索条件
            StringBuffer queryConditionBuffer = new StringBuffer();
            String selectFields = dataMatchDto.getSelectFields();
            String queryCondition = dataMatchDto.getQueryCondition();
            if (StringUtil.isNoneBlank(queryCondition)) {
                getQueryCondition(queryConditionBuffer, selectFields, queryCondition);
                String condition = AdminConstant.Character.AND + AdminConstant.Character.LEFT_PARENTHESIS + queryConditionBuffer.toString() + AdminConstant.Character.RIGHT_PARENTHESIS + AdminConstant.Character.SPACE;
                dataMatchDto.setQueryCondition(condition);
            }
            // 查询是否匹配
            String dataMap = dataMatchDto.getDataMap();
            if (StringUtil.isNoneBlank(dataMap)) {
                String dataMapCondition = Constant.Character.NULL_VALUE;
                // 全部匹配
                if (AdminConstant.DataMap.MAPED.equals(dataMap)) {
                    dataMapCondition = AdminConstant.Character.AND + AdminConstant.Character.RELA + Constant.Character.POINT + dataMatchDto.getDataType()
                            + AdminConstant.Character.END_ID + AdminConstant.Character.NOT_EQUALS + AdminConstant.Character.SINGLE_QUOTE + AdminConstant.Character.SINGLE_QUOTE;
                } else if (AdminConstant.DataMap.UNMAPED.equals(dataMap)) {
                    dataMapCondition = AdminConstant.Character.AND + AdminConstant.Character.RELA + Constant.Character.POINT + dataMatchDto.getDataType() + AdminConstant.Character.END_ID
                            + AdminConstant.Character.EQUALS_SPACE + AdminConstant.Character.SINGLE_QUOTE + AdminConstant.Character.SINGLE_QUOTE;
                }
                dataMatchDto.setDataMap(dataMapCondition);
            }
            dataList = masterDataUnifiedMapper.getMasterDataListByPerspective(page, dataMatchDto);
            // 处理数据
            List<LinkedHashMap<String,Object>> returnList = dealMatchData(dataList, master, slave, dataMatchDto.getDataType(), perspective);
            // 排序
            //ListUtils.sort(returnList, false, dataMatchDto.getDataType() + "IdBase");
            newPageList.setRecords(returnList);
            newPageList.setTotal(dataList.getTotal());
            newPageList.setSize(dataList.getSize());
            newPageList.setCurrent(dataList.getCurrent());
            newPageList.setPages(dataList.getPages());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("getDataListByPerspective error,{}", e.getMessage());
        }
        return newPageList;
    }

    /**
     * 获取查询条件，默认取列表的前三个字段
     * @param queryConditionBuffer
     * @param selectFields
     * @param queryCondition
     */
    private void getQueryCondition(StringBuffer queryConditionBuffer, String selectFields, String queryCondition) {
        if (StringUtil.isNoneBlank(selectFields)) {
            String[] fields = selectFields.split(Constant.Character.COMMA);
            if (fields != null && fields.length > 0) {
                String conditionFirst = fields[0];
                if (StringUtil.isNoneBlank(conditionFirst)) {
                    queryConditionBuffer.append(conditionFirst.split(AdminConstant.Character.SPACE)[0]).append(AdminConstant.Character.LIKE).append(AdminConstant.Character.SINGLE_QUOTE).append(Constant.Character.Percent).append(queryCondition).append(Constant.Character.Percent).append(AdminConstant.Character.SINGLE_QUOTE);
                }
            }
            if (fields != null && fields.length > 1) {
                String conditionSecond = fields[1];
                if (StringUtil.isNoneBlank(conditionSecond)) {
                    queryConditionBuffer.append(AdminConstant.Character.OR).append(conditionSecond.split(AdminConstant.Character.SPACE)[0]).append(AdminConstant.Character.LIKE).append(AdminConstant.Character.SINGLE_QUOTE).append(Constant.Character.Percent).append(queryCondition).append(Constant.Character.Percent).append(AdminConstant.Character.SINGLE_QUOTE);
                }
            }
            if (fields != null && fields.length > 2) {
                String conditionThird = fields[2];
                if (StringUtil.isNoneBlank(conditionThird)) {
                    queryConditionBuffer.append(AdminConstant.Character.OR).append(conditionThird.split(AdminConstant.Character.SPACE)[0]).append(AdminConstant.Character.LIKE).append(AdminConstant.Character.SINGLE_QUOTE).append(Constant.Character.Percent).append(queryCondition).append(Constant.Character.Percent).append(AdminConstant.Character.SINGLE_QUOTE);
                }
            }
        }
    }

    /**
     * 处理匹配数据
     * @param dataList
     * @param master
     * @param slave
     * @param dataType
     * @param perspective
     * @return
     */
    private List<LinkedHashMap<String,Object>> dealMatchData(Page<LinkedHashMap<String, String>> dataList, String master, String slave, String dataType, String perspective) {
        List<LinkedHashMap<String,Object>> returnList = new ArrayList<>();
        String id = Constant.Character.NULL_VALUE;
        if (AdminConstant.Character.MID_PLAT_PERSPECTIVE.equals(perspective)) {
            id = StringUtil.underlineToCamel(dataType) + AdminConstant.Character.ID_TWO + slave;
        } else {
            id = StringUtil.underlineToCamel(dataType) + AdminConstant.Character.ID_TWO + master;
        }
        if (dataList != null && dataList.getRecords() != null && dataList.getRecords().size() > 0) {
            List<LinkedHashMap<String, String>> oldList = dataList.getRecords();
            if (oldList != null && oldList.size() > 0) {
                for(LinkedHashMap<String, String> old : oldList) {
                    String idValue = old.get(id);
                    Iterator iter = old.entrySet().iterator();
                    LinkedHashMap<String, Object> newLinkedHashMap = new LinkedHashMap<>();
                    LinkedHashMap<String, String> slaveLinkedHashMap = new LinkedHashMap<>();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        if (value == null) {
                            value = Constant.Character.NULL_VALUE;
                        }
                        if (key != null) {
                            if (key.toString().endsWith(master)) {
                                newLinkedHashMap.put(key.toString(),value.toString());
                            } else if (key.toString().endsWith(slave)) {
                                // 如果查询到中台id为空就直接返回关联对象为空
                                if (StringUtil.isNoneBlank(idValue)) {
                                    slaveLinkedHashMap.put(key.toString(), value.toString());
                                }
                            }
                        }
                    }
                    newLinkedHashMap.put(slave, slaveLinkedHashMap);
                    returnList.add(newLinkedHashMap);
                }
            }
        }
        return returnList;
    }

    /**
     * @return
     * @Author XiaFq
     * @Description 中台视角批量匹配数据
     * @Date 2019/11/21 11:43 上午
     * @Param dataMatchBatchDtoList
     */
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public int dataMathBatchByMinPlat(DataMatchBatchDto dataMatchBatchDto) {
        try {
            // 当前登录用户
            String userId = dataMatchBatchDto.getUserId();
            List<DataMatchBatchIdsDto> addList = dataMatchBatchDto.getAddList();
            List<DataMatchBatchDto> newAddList = new ArrayList<>();
            // id变更记录集合
            List<RecordChangeDto> recordChangeDtoList = new ArrayList<>();

            // 处理解绑的数据
            List<DataMatchBatchIdsDto> deleteList = dataMatchBatchDto.getDeleteList();
            List<DataMatchBatchDto> newDeleteList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(deleteList)) {
                deleteList.stream().forEach(id->{
                    DataMatchBatchDto dto = new DataMatchBatchDto();
                    dto.setEnterpriseId(dataMatchBatchDto.getEnterpriseId());
                    dto.setAppId(dataMatchBatchDto.getAppId());
                    dto.setDataType(dataMatchBatchDto.getDataType());
                    dto.setMidPlatId(Constant.Character.NULL_VALUE);
                    dto.setThirdId(id.getThirdId());
                    newDeleteList.add(dto);
                });
                masterDataUnifiedMapper.dataMathBatchByMinPlat(newDeleteList);
            }

            // 处理匹配绑定的数据
            if (!CollectionUtils.isEmpty(addList)) {
                addList.stream().forEach(id->{
                    DataMatchBatchDto dto = new DataMatchBatchDto();
                    dto.setEnterpriseId(dataMatchBatchDto.getEnterpriseId());
                    dto.setAppId(dataMatchBatchDto.getAppId());
                    dto.setDataType(dataMatchBatchDto.getDataType());
                    dto.setMidPlatId(id.getMidPlatId());
                    dto.setThirdId(id.getThirdId());
                    newAddList.add(dto);
                    // 查询是否有变更中台id
                    RecordChangeDto recordChangeDto = new RecordChangeDto();
                    String changeId = idWorker.nextId();
                    recordChangeDto.setAppId(dataMatchBatchDto.getAppId());
                    recordChangeDto.setDataType(dataMatchBatchDto.getDataType());
                    recordChangeDto.setEnteId(dataMatchBatchDto.getEnterpriseId());
                    recordChangeDto.setThirdId(id.getThirdId());
                    recordChangeDto.setBaseIdNew(id.getMidPlatId());
                    recordChangeDto.setOperatorId(userId);
                    RecordChangeDto newDto = recordChangeMapper.selectRelyDataByThirdId(recordChangeDto);
                    if (newDto != null) {
                        newDto.setId(changeId);
                        newDto.setOperatorType(AdminConstant.Character.OPERATOR_TYPE);
                        recordChangeDtoList.add(newDto);
                    }
                });
                masterDataUnifiedMapper.dataMathBatchByMinPlat(newAddList);
                if (AdminConstant.Character.ZERO.equals(dataMatchBatchDto.getUpdateFlag())
                        && !CollectionUtils.isEmpty(recordChangeDtoList)) {
                    recordChangeMapper.saveRecordChangeBatch(recordChangeDtoList);
                }
            }
            return AdminConstant.Number.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("dataMathBatchByMinPlat error,{}", e.getMessage());
            return AdminConstant.Number.UPDATE_FAILED;
        }
    }

    /**
     * @param dataMatchDto
     * @return List<LinkedHashMap>
     * @Author XiaFq
     * @Description 查询应用未匹配数据列表
     * @Date 2019/11/21 3:00 下午
     * @Param dataMatchDto
     */
    @Override
    public List<LinkedHashMap> queryAppNotMatchDataList(DataMatchDto dataMatchDto) {
        // 初始化查询字段
        String tableName = AdminConstant.Character.BASE_TABLE_START + dataMatchDto.getDataType() + AdminConstant.Character.RELA_TABLE_END;
        dataMatchDto.setTableName(tableName);
        dataMatchDto.setAliasName(AdminConstant.Character.RELA);
        StringBuffer queryColumnStr = new StringBuffer();
        dataMatchDto.setFieldAlias(AdminConstant.Character.RELA);
        initQueryColumn(dataMatchDto, queryColumnStr, dataMatchDto.getPerspective());
        dataMatchDto.setSelectFields(queryColumnStr.toString());
        // 构造搜索条件
        StringBuffer queryConditionBuffer = new StringBuffer();
        String selectFields = dataMatchDto.getSelectFields();
        String queryCondition = dataMatchDto.getQueryCondition();
        if (StringUtil.isNoneBlank(queryCondition)) {
            getQueryCondition(queryConditionBuffer, selectFields, queryCondition);
            String condition = AdminConstant.Character.AND + AdminConstant.Character.LEFT_PARENTHESIS + queryConditionBuffer.toString()
                    + AdminConstant.Character.RIGHT_PARENTHESIS + AdminConstant.Character.SPACE;
            dataMatchDto.setQueryConditionStr(condition);
        }
        List<LinkedHashMap> dataList = masterDataUnifiedMapper.queryAppNotMatchDataList(dataMatchDto);
        return dataList;
    }

    /**
     * @param dataMatchDto
     * @return List<LinkedHashMap>
     * @Author XiaFq
     * @Description 查询中台未匹配的数据列表
     * @Date 2019/11/21 3:02 下午
     * @Param dataMatchDto
     */
    @Override
    public List<LinkedHashMap> queryMidPlatNotMatchDataList(DataMatchDto dataMatchDto) {
        // 初始化查询字段
        String tableName = AdminConstant.Character.BASE_TABLE_START + dataMatchDto.getDataType();
        dataMatchDto.setTableName(tableName);
        dataMatchDto.setAliasName(AdminConstant.Character.BASE);
        StringBuffer queryColumnStr = new StringBuffer();
        dataMatchDto.setFieldAlias(AdminConstant.Character.BASE);
        initQueryColumn(dataMatchDto, queryColumnStr, dataMatchDto.getPerspective());
        dataMatchDto.setSelectFields(queryColumnStr.toString());
        // 构造搜索条件
        StringBuffer queryConditionBuffer = new StringBuffer();
        String queryCondition = dataMatchDto.getQueryCondition();
        if (StringUtil.isNoneBlank(queryCondition)) {
            getQueryCondition(queryConditionBuffer, queryColumnStr.toString(), queryCondition);
            String condition = AdminConstant.Character.AND + AdminConstant.Character.LEFT_PARENTHESIS + queryConditionBuffer.toString() +
                    AdminConstant.Character.RIGHT_PARENTHESIS + AdminConstant.Character.SPACE;
            dataMatchDto.setQueryConditionStr(condition);
        }
        List<LinkedHashMap> dataList = masterDataUnifiedMapper.queryMidPlatNotMatchDataList(dataMatchDto);
        return dataList;
    }

    /**
     * @return int
     * @Author XiaFq
     * @Description 将rela表数据保存到中台表数据及中台id回写
     * @Date 2019/11/24 10:39 上午
     * @Param [masterDataUnifiedDto]
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public int saveMinPlatDataFromRela(MasterDataUnifiedDto masterDataUnifiedDto) {
        try {
            String tableName = AdminConstant.Character.BASE_TABLE_START + masterDataUnifiedDto.getDataType();
            String relaTableName = AdminConstant.Character.BASE_TABLE_START + masterDataUnifiedDto.getDataType() + AdminConstant.Character.RELA_TABLE_END;
            masterDataUnifiedDto.setTableName(tableName);
            masterDataUnifiedDto.setRelaTableName(relaTableName);

            // 操作主表的数据 base -> rela
            masterDataUnifiedDto.setTableType(AdminConstant.Character.SINGLE);
            ((MasterDataUnifiedService) AopContext.currentProxy()).relaData2BaseData(masterDataUnifiedDto);

            // 同步主数据依赖表数据 base -> rela
            List<PrimaryRelyVo> dependentList = masterDataUnifiedMapper.getDependentInfoByDataType(masterDataUnifiedDto);
            if (dependentList != null) {
                dependentList.stream().forEach(d -> {
                    masterDataUnifiedDto.setRelaTableName(d.getRelaTable());
                    masterDataUnifiedDto.setTableName(d.getBaseTable());
                    masterDataUnifiedDto.setTableType(AdminConstant.Character.MANY);
                    ((MasterDataUnifiedService) AopContext.currentProxy()).relaData2BaseData(masterDataUnifiedDto);
                });
            }

            // 查询依赖表中是否存在依赖该表的数据，如果存在就直接回写该id
            List<PrimaryRelyVo> primaryRelyVoList = masterDataUnifiedMapper.getBeDependentByRelyData(masterDataUnifiedDto);
            if (primaryRelyVoList != null && primaryRelyVoList.size() > 0) {
                primaryRelyVoList.stream().forEach(p -> {
                    // 回写base表数据id
                    masterDataUnifiedDto.setRelaTableName(p.getBaseTable());
                    masterDataUnifiedMapper.dataWriteBackForId(masterDataUnifiedDto);
                    // 回写rela表数据id
                    masterDataUnifiedDto.setRelaTableName(p.getRelaTable());
                    masterDataUnifiedMapper.dataWriteBackForId(masterDataUnifiedDto);

                });
            }

            return AdminConstant.Number.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("saveMinPlatDataFromRela error ,{}", e.getMessage());
            // 事务补偿回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AdminConstant.Number.UPDATE_FAILED;
        }
    }

    /**
     * @return void
     * @Author XiaFq
     * @Description 主数据同步操作 rela -> base 以及base表中的id回写到rela表中
     * @Date 2019/11/24 10:37 上午
     * @Param [masterDataUnifiedDto]
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public void relaData2BaseData(MasterDataUnifiedDto masterDataUnifiedDto) {
        // 表名
        String aliasName = AdminConstant.Character.RELA;
        MasterDataUnifiedFieldDto md = new MasterDataUnifiedFieldDto();
        md.setAliasName(aliasName);
        md.setTableName(masterDataUnifiedDto.getTableName());
        md.setDataType(masterDataUnifiedDto.getDataType());

        // 查询需要保存数据的字段
        String selectColumns = masterDataUnifiedMapper.queryDataUniformField(md);
        String insertColumns = md.getDataType() + AdminConstant.Character.END_ID + Constant.Character.COMMA + selectColumns.replace(aliasName + Constant.Character.POINT, Constant.Character.NULL_VALUE);
        String id = AdminConstant.Character.SELECT_UUID;
        StringBuffer selectColumn = new StringBuffer();
        String idStr = "";
        if (AdminConstant.Character.SINGLE.equals(masterDataUnifiedDto.getTableType())) {
            // 如果是一对一查询，已经存在id的话就直接取，没有的话自动生成
            idStr = AdminConstant.Character.CASE_WHEN + AdminConstant.Character.LEFT_PARENTHESIS + AdminConstant.Character.RELA + Constant.Character.POINT
                    + md.getDataType() + AdminConstant.Character.END_ID + AdminConstant.Character.EQUALS_SPACE + AdminConstant.Character.SINGLE_QUOTE + AdminConstant.Character.SINGLE_QUOTE
                    + AdminConstant.Character.OR + AdminConstant.Character.RELA + Constant.Character.POINT + md.getDataType() + AdminConstant.Character.END_ID + AdminConstant.Character.IS_NULL
                    + AdminConstant.Character.RIGHT_PARENTHESIS + AdminConstant.Character.THEN + id + AdminConstant.Character.ELSE + AdminConstant.Character.RELA + Constant.Character.POINT
                    + md.getDataType() + AdminConstant.Character.END_ID + AdminConstant.Character.END + masterDataUnifiedDto.getDataType() + AdminConstant.Character.END_ID;
        } else {
            idStr = AdminConstant.Character.RELA + Constant.Character.POINT + md.getDataType() + AdminConstant.Character.END_ID;
        }
        selectColumn.append(idStr).append(Constant.Character.COMMA).append(selectColumns);
        masterDataUnifiedDto.setSelectFields(selectColumn.toString());
        masterDataUnifiedDto.setTableName(masterDataUnifiedDto.getTableName());
        masterDataUnifiedDto.setRelaTableName(masterDataUnifiedDto.getRelaTableName());
        masterDataUnifiedDto.setInsertFields(insertColumns);
        // 一对一 插入数据
        LOGGER.info("MasterDataUnifiedServiceImpl.relaData2BaseData masterDataUnifiedDto is {}", JSONObject.toJSONString(masterDataUnifiedDto));
        masterDataUnifiedMapper.saveMidPlatDataFromRela(masterDataUnifiedDto);
        // id 回写
        // 如果是主数据对应的关联表 直接将主数据的id回写到关联表中
        if (AdminConstant.Character.SINGLE.equals(masterDataUnifiedDto.getTableType())) {
            LOGGER.info("MasterDataUnifiedServiceImpl.relaData2BaseData SINGLE is {}", JSONObject.toJSONString(masterDataUnifiedDto));
            masterDataUnifiedMapper.dataWriteBackForId(masterDataUnifiedDto);
        } else {
            String baseTable = AdminConstant.Character.BASE_TABLE_START + masterDataUnifiedDto.getDataType();
            String relyBaseTable = masterDataUnifiedDto.getTableName();
            String relyRelaTable = masterDataUnifiedDto.getRelaTableName();
            String appId = masterDataUnifiedDto.getAppId();
            // 重新设置主表为 主数据表
            masterDataUnifiedDto.setTableName(baseTable);
            // 回写关联表 base表id 因为base表没有app_id字段
            masterDataUnifiedDto.setAppId(null);
            masterDataUnifiedDto.setRelaTableName(relyBaseTable);
            LOGGER.info("MasterDataUnifiedServiceImpl.relaData2BaseData MANY base is {}", JSONObject.toJSONString(masterDataUnifiedDto));
            masterDataUnifiedMapper.dataWriteBackForId(masterDataUnifiedDto);
            // 回写关联表 rela表id
            masterDataUnifiedDto.setAppId(appId);
            masterDataUnifiedDto.setRelaTableName(relyRelaTable);
            LOGGER.info("MasterDataUnifiedServiceImpl.relaData2BaseData MANY rela is {}", JSONObject.toJSONString(masterDataUnifiedDto));
            masterDataUnifiedMapper.dataWriteBackForId(masterDataUnifiedDto);
        }
    }

    /**
     * @param dataMatchBatchTaskDto
     * @return int -1 添加失败 0 添加成功
     * @Author XiaFq
     * @Description 批量任务匹配主数据
     * @Date 2019/11/27 2:20 下午
     * @Param dataMatchBatchTaskDto
     */
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public int dataMatchBatch(DataMatchBatchTaskDto dataMatchBatchTaskDto) {
        try {
            // 查询所有配置融合规则的应用
            PrimaryJointDto primaryJointDto = new PrimaryJointDto();
            primaryJointDto.setEnterpriseId(dataMatchBatchTaskDto.getEnterpriseId());
            primaryJointDto.setDataType(dataMatchBatchTaskDto.getDataType());
            List<PrimaryJointVo> list = primaryJointMapper.selectPrimaryJointList(primaryJointDto);
            List<DataMatchBatchTaskDto> dataMatchBatchTaskDtos = new ArrayList<>();
            if (list != null && list.size() > 0) {
                for (PrimaryJointVo primaryJointVo : list) {
                    dataMatchBatchTaskDto.setAppId(primaryJointVo.getAppId());
                    String relaTable = AdminConstant.Character.BASE_TABLE_START + primaryJointVo.getDataType() + AdminConstant.Character.RELA_TABLE_END;
                    String baseTable = AdminConstant.Character.BASE_TABLE_START + primaryJointVo.getDataType();
                    dataMatchBatchTaskDto.setBaseTable(baseTable);
                    dataMatchBatchTaskDto.setRelaTable(relaTable);
                    String expression = primaryJointVo.getExpression();
                    // 需要更新的id字段 如 t.user_id,
                    String updateIdStr = AdminConstant.Character.T + Constant.Character.POINT + primaryJointVo.getDataType() + AdminConstant.Character.END_ID;
                    // user_id,user_id&&;user_name,user_name
                    if (StringUtil.isNotEmpty(expression)) {
                        String[] onStr = expression.split(AdminConstant.Character.SEMICOLON);
                        if (onStr != null && onStr.length > 0) {
                            // 子查询中查询的字段拼接
                            StringBuffer baseQueryColumn = new StringBuffer();
                            // left join on 后面的条件
                            StringBuffer onColumnS = new StringBuffer();
                            // 子查询中where条件
                            StringBuffer baseQueryCondition = new StringBuffer();
                            // 更新条件
                            StringBuffer relaQueryCondition = new StringBuffer();
                            for (String onColumns : onStr) {
                                String[] onColumn = onColumns.split(Constant.Character.COMMA);
                                String baseColumn = (onColumn != null && onColumn.length > 0) ? onColumn[0] : Constant.Character.NULL_VALUE;
                                String relaColumnStr = (onColumn != null && onColumn.length > 0) ? onColumn[1] : Constant.Character.NULL_VALUE;
                                // 去掉逻辑运算符
                                String relaColumn = relaColumnStr.replace(AdminConstant.Character.LOGIC_AND, Constant.Character.NULL_VALUE).replace(AdminConstant.Character.LOGIC_OR, Constant.Character.NULL_VALUE);
                                // 根据规则拼接 base表查询字段 如 t.user_id, t.user_name, t.mobile
                                String jointColumn = AdminConstant.Character.T + Constant.Character.POINT + baseColumn + Constant.Character.COMMA;
                                baseQueryColumn.append(jointColumn);
                                onColumnS.append(AdminConstant.Character.BASE).append(Constant.Character.POINT).append(baseColumn).
                                        append(Constant.Character.EQUALS).append(AdminConstant.Character.RELA).append(Constant.Character.POINT).append(relaColumn);
                                if (relaColumnStr.indexOf(AdminConstant.Character.LOGIC_AND) != -1) {
                                    onColumnS.append(AdminConstant.Character.AND);
                                }
                                if (relaColumnStr.indexOf(AdminConstant.Character.LOGIC_OR) != -1) {
                                    onColumnS.append(AdminConstant.Character.OR);
                                }
                                // t.user_id != '' and t.user_name != ''
                                baseQueryCondition.append(AdminConstant.Character.AND).append(AdminConstant.Character.T).append(Constant.Character.POINT).
                                        append(baseColumn).append(AdminConstant.Character.NOT_EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(AdminConstant.Character.SINGLE_QUOTE);
                                relaQueryCondition.append(AdminConstant.Character.AND).append(AdminConstant.Character.RELA).append(Constant.Character.POINT).
                                        append(relaColumn).append(AdminConstant.Character.NOT_EQUALS).append(AdminConstant.Character.SINGLE_QUOTE).append(AdminConstant.Character.SINGLE_QUOTE);
                            }
                            baseQueryColumn.append(updateIdStr);
                            dataMatchBatchTaskDto.setBaseQueryColumn(baseQueryColumn.toString());
                            dataMatchBatchTaskDto.setBaseQueryCondition(baseQueryCondition.toString());
                            dataMatchBatchTaskDto.setRelaQueryCondition(relaQueryCondition.toString());
                            dataMatchBatchTaskDto.setLeftJoinOnCondition(onColumnS.toString());
                        }
                    }
                    dataMatchBatchTaskDtos.add(dataMatchBatchTaskDto);
                }
                LOGGER.info("MasterDataUnifiedServiceImpl.dataMatchBatch dataMatchBatchTaskDtos is {}", JSONObject.toJSONString(dataMatchBatchTaskDtos));
                masterDataUnifiedMapper.dataMathBatchTask(dataMatchBatchTaskDtos);
                return AdminConstant.Number.UPDATE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("dataMatchBatch throw exception, {}", e.getMessage());
            return AdminConstant.Number.UPDATE_FAILED;
        }
        return AdminConstant.Number.UPDATE_SUCCESS;
    }

    /**
     * @return
     * @Author XiaFq
     * @Description 动态拼接查询字段
     * @Date 2019/11/20 10:50 上午
     * @Param masterDataUnifiedDto queryColumnStr
     */
    private void initQueryColumn(MasterDataUnifiedDto masterDataUnifiedDto, StringBuffer queryColumnStr, String perspective) {
        // 需要查询的字段
        String queryColumn = masterDataUnifiedMapper.selectColumnByDataType(masterDataUnifiedDto);
        if (StringUtil.isNotEmpty(queryColumn)) {
            if (StringUtil.isNoneBlank(perspective) && AdminConstant.Character.APP_PERSPECTIVE.equals(perspective)) {
                queryColumn = queryColumn.replace(AdminConstant.Character.START_STR, masterDataUnifiedDto.getFieldAlias());
            } else {
                queryColumn = queryColumn.replace(AdminConstant.Character.START_STR, masterDataUnifiedDto.getAliasName());
            }
            queryColumnStr.append(queryColumn);
        }
        if (StringUtil.isNoneBlank(perspective)) {
            // 查询显示字段
            String minPlatId = masterDataUnifiedDto.getDataType() + AdminConstant.Character.END_ID;
            String queryColumnMinPlatId = Constant.Character.NULL_VALUE;
            if (AdminConstant.Character.APP_PERSPECTIVE.equals(perspective)) {
                queryColumnMinPlatId = masterDataUnifiedDto.getAliasName() + Constant.Character.POINT + minPlatId + AdminConstant.Character.MINPLAT_ID + masterDataUnifiedDto.getFieldAlias();
            } else {
                queryColumnMinPlatId = masterDataUnifiedDto.getAliasName() + Constant.Character.POINT + minPlatId + AdminConstant.Character.MINPLAT_ID + masterDataUnifiedDto.getAliasName();
            }
            String name = masterDataUnifiedDto.getDataType() + AdminConstant.Character.END_NAME;
            String thirdId = AdminConstant.Character.START_THIRD + masterDataUnifiedDto.getDataType() + AdminConstant.Character.END_ID;
            String queryColumnThirdId = Constant.Character.NULL_VALUE;
            if (AdminConstant.Character.APP_PERSPECTIVE.equals(perspective)) {
                queryColumnThirdId = masterDataUnifiedDto.getAliasName() + Constant.Character.POINT + thirdId + AdminConstant.Character.THIRD_ID + masterDataUnifiedDto.getFieldAlias();
            } else {
                queryColumnThirdId = masterDataUnifiedDto.getAliasName() + Constant.Character.POINT + thirdId + AdminConstant.Character.THIRD_ID + masterDataUnifiedDto.getAliasName();
            }
            String queryDisplayColumnStr = Constant.Character.NULL_VALUE;
            queryDisplayColumnStr = AdminConstant.Character.SINGLE_QUOTE + thirdId + AdminConstant.Character.SINGLE_QUOTE + Constant.Character.COMMA + AdminConstant.Character.SINGLE_QUOTE +  name + AdminConstant.Character.SINGLE_QUOTE;
            masterDataUnifiedDto.setQueryDisplayColumnStr(queryDisplayColumnStr);
            String displayColumnStr = masterDataUnifiedMapper.selectDisplayColumn(masterDataUnifiedDto);
            if (StringUtil.isNoneBlank(displayColumnStr)) {
                displayColumnStr = displayColumnStr.replace(Constant.Character.COMMA,AdminConstant.Character.COMMAS);
                if (AdminConstant.Character.APP_PERSPECTIVE.equals(perspective)) {
                    displayColumnStr = AdminConstant.Character.CONCAT_START + displayColumnStr + AdminConstant.Character.CONCAT_END + masterDataUnifiedDto.getFieldAlias();
                } else {
                    displayColumnStr = AdminConstant.Character.CONCAT_START + displayColumnStr + AdminConstant.Character.CONCAT_END + masterDataUnifiedDto.getAliasName();
                }

                queryColumnStr.append(Constant.Character.COMMA).append(displayColumnStr).append(Constant.Character.COMMA).
                        append(queryColumnMinPlatId).append(Constant.Character.COMMA).append(queryColumnThirdId);
            }
        }

    }

    /**
     * @return void
     * @Author XiaFq
     * @Description 拼接left join 字符串
     * @Date 2019/11/20 3:59 下午
     * @Param [primaryJoint, relaLeftJoinStr, leftJoinStr, aliasName, dataType]
     */
    private void getLeftJoinStr(PrimaryJoint primaryJoint, StringBuffer relaLeftJoinStr,
                                StringBuffer leftJoinStr, String aliasName,String dataType, DataMatchDto dataMatchDto) {
        String[] fieldArray = primaryJoint.getExpression().split(AdminConstant.Character.SEMICOLON);
        if (fieldArray != null && fieldArray.length > 0) {
            for (String field : fieldArray) {
                String[] rules = field.split(Constant.Character.COMMA);
                // 规则中中台数据字段
                String baseField = rules[0];
                // 规则中 rela表中数据
                String relaFiled = rules[1];
                relaLeftJoinStr.append(AdminConstant.Character.BASE).append(Constant.Character.POINT).append(rules[0]);
                relaLeftJoinStr.append(Constant.Character.EQUALS);
                // 如果rela表字段带有&& 做and拼接 如果有 || 做OR拼接
                if (StringUtil.isNotEmpty(relaFiled)) {
                    String relaFiledNew = relaFiled.replace(AdminConstant.Character.LOGIC_AND, Constant.Character.NULL_VALUE).
                            replace(AdminConstant.Character.LOGIC_OR, Constant.Character.NULL_VALUE);
                    relaLeftJoinStr.append(aliasName).append(Constant.Character.POINT).append(relaFiledNew);
                    if (relaFiled.indexOf(AdminConstant.Character.LOGIC_AND) != -1) {
                        relaLeftJoinStr.append(AdminConstant.Character.AND);
                    } else if (relaFiled.indexOf(AdminConstant.Character.LOGIC_OR) != -1) {
                        relaLeftJoinStr.append(AdminConstant.Character.OR);
                    }

                }
            }
        }
        leftJoinStr.append(AdminConstant.Character.LEFT_JOIN);
        String relaTableName = AdminConstant.Character.BASE_TABLE_START + dataType + AdminConstant.Character.RELA_TABLE_END;
        if (dataMatchDto != null && StringUtil.isNoneBlank(dataMatchDto.getPerspective())) {
            relaTableName = dataMatchDto.getSlaveTableName();
        }
        leftJoinStr.append(relaTableName).append(AdminConstant.Character.SPACE).append(aliasName);
        leftJoinStr.append(AdminConstant.Character.ON);
        leftJoinStr.append(relaLeftJoinStr).append(AdminConstant.Character.SPACE);
        if (dataMatchDto == null || StringUtil.isEmpty(dataMatchDto.getPerspective()) || AdminConstant.Character.MID_PLAT_PERSPECTIVE.equals(dataMatchDto.getPerspective())) {
            leftJoinStr.append(AdminConstant.Character.AND).append(aliasName).
                    append(Constant.Character.POINT).append(AdminConstant.Character.ON_APP_ID).
                    append(AdminConstant.Character.SINGLE_QUOTE).append(primaryJoint.getAppId()).append(AdminConstant.Character.SINGLE_QUOTE);
        }
        leftJoinStr. append(AdminConstant.Character.AND).append(aliasName).
                append(Constant.Character.POINT).append(AdminConstant.Character.ON_ENTE_ID).
                append(AdminConstant.Character.SINGLE_QUOTE).append(primaryJoint.getEnterpriseId()).append(AdminConstant.Character.SINGLE_QUOTE);
    }
}

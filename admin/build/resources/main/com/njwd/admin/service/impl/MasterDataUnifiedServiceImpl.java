package com.njwd.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.admin.mapper.MasterDataUnifiedMapper;
import com.njwd.admin.mapper.PrimaryJointMapper;
import com.njwd.admin.service.MasterDataUnifiedService;
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
    MasterDataUnifiedMapper masterDataUnifiedMapper;

    @Resource
    PrimaryJointMapper primaryJointMapper;

    @Resource
    private IdWorker idWorker;

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
            initQueryColumn(masterDataUnifiedDto, selectFields);
            if (masterDataAppVoList != null && masterDataAppVoList.size() > 0) {
                for (int i = 0; i < masterDataAppVoList.size(); i++) {
                    MasterDataAppVo masterDataAppVo = masterDataAppVoList.get(i);
                    keyList.add(masterDataAppVo.getKey());
                    // 主数据
                    String relaTableName = AdminConstant.Character.BASE_TABLE_START + masterDataAppDto.getDataType() + AdminConstant.Character.RELA_TABLE_END;
                    masterDataUnifiedDto.setTableName(relaTableName);
                    if (StringUtil.isNotEmpty(masterDataAppVo.getSourceId())) {
                        masterDataUnifiedDto.setMasterDataAppId(masterDataAppVo.getAppId());
                        masterDataUnifiedDto.setAliasName(AdminConstant.Character.MASTER_DATA);
                        selectFields.append(Constant.Character.COMMA);
                        initQueryColumn(masterDataUnifiedDto, selectFields);
                    } else {
                        // 去掉主数据的下标，从1开始
                        int j = i - 1;
                        String aliasName = AdminConstant.Character.RELA + j;
                        masterDataUnifiedDto.setAliasName(aliasName);
                        StringBuffer leftJoinStr = new StringBuffer();
                        // 查询融合规则 拼接sql
                        PrimaryJointDto primaryJointDto = new PrimaryJointDto();
                        primaryJointDto.setEnterpriseId(masterDataAppVo.getEnterpriseId());
                        primaryJointDto.setDataType(masterDataAppVo.getDataType());
                        primaryJointDto.setAppId(masterDataAppVo.getAppId());
                        PrimaryJoint primaryJoint = primaryJointMapper.selectPrimaryJoint(primaryJointDto);
                        StringBuffer relaLeftJoinStr = new StringBuffer();
                        if (primaryJoint != null && StringUtil.isNotEmpty(primaryJoint.getExpression())) {
                            // 获取left join 
                            getLeftJoinStr(primaryJoint, relaLeftJoinStr, leftJoinStr, aliasName, masterDataUnifiedDto.getDataType());
                            masterDataUnifiedDto.setLeftJoinSql(leftJoinStr.toString());
                            selectFields.append(Constant.Character.COMMA);
                            initQueryColumn(masterDataUnifiedDto, selectFields);
                        }
                    }

                }
            }
            masterDataUnifiedDto.setSelectFields(selectFields.toString());
            String orderByStr = AdminConstant.Character.BASE + Constant.Character.POINT + masterDataUnifiedDto.getDataType()
                    + Constant.Character.UNDER_LINE + AdminConstant.Character.ID;
            masterDataUnifiedDto.setOrderByStr(orderByStr);
            // 查询
            Page<LinkedHashMap<String, String>> page = masterDataUnifiedDto.getPage();
            dataList = masterDataUnifiedMapper.getMasterDataList(page, masterDataUnifiedDto);
            // 处理数据
            dealData(dataList, keyList, newList, newPageList);
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
    private void dealData(Page<LinkedHashMap<String, String>> dataList, List<String> keyList, List<LinkedHashMap<String, LinkedHashMap<String, String>>> newList, Page<LinkedHashMap<String, LinkedHashMap<String, String>>> newPageList) {
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
                    newMap.put(keyName,newLinkedHashMap);
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        if (key != null && key.toString().endsWith(keyName)) {
                            if (value != null) {
                                newLinkedHashMap.put(key.toString(), value.toString());
                            } else {
                                newLinkedHashMap.put(key.toString(), Constant.Character.NULL_VALUE);
                            }
                            newMap.put(keyName, newLinkedHashMap);
                        } else {
                            newLinkedHashMap = new LinkedHashMap<>();
                            if (value != null) {
                                newLinkedHashMap.put(key.toString(), value.toString());
                            } else {
                                newLinkedHashMap.put(key.toString(), Constant.Character.NULL_VALUE);
                            }
                            // 先获取下一个值，构造map
                            if (keyIterator.hasNext()) {
                                String nextKeyName = keyIterator.next();
                                newMap.put(nextKeyName, newLinkedHashMap);
                                // 上一步迭代过了，网上走一步，回到正常迭代位置
                                keyIterator.previous();
                            }
                            break;
                        }
                    }
                }
                newList.add(newMap);
            });
            newPageList.setRecords(newList);
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
        try {
            // 先判断操作数据是否存在，存在更新，不存在插入
            PrimaryJoint oldPojo = primaryJointMapper.selectPrimaryJoint(primaryJointDto);
            if (oldPojo != null) {
                primaryJointDto.setExpression(primaryJointDto.getExpression());
                primaryJointDto.setJointId(oldPojo.getJointId());
                primaryJointMapper.updatePrimaryJoint(primaryJointDto);
            } else {
                String jointId = idWorker.nextId();
                primaryJointDto.setJointId(jointId);
                primaryJointDto.setCreateTime(new Date());
                primaryJointMapper.insertPrimaryJoint(primaryJointDto);
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
                dataMatchDto.setAliasName(AdminConstant.Character.RELA);
                // 设置主表名
                // 设置主表名
                masterTableName = AdminConstant.Character.BASE_TABLE_START + dataMatchDto.getDataType() + AdminConstant.Character.RELA_TABLE_END;
                // 设置从表名
                slaveTableName = AdminConstant.Character.BASE_TABLE_START + dataMatchDto.getDataType();
                dataMatchDto.setMasterTableName(masterTableName);
                dataMatchDto.setSlaveTableName(slaveTableName);
                master = AdminConstant.Character.RELA;
                slave = AdminConstant.Character.BASE;
            }

            // 设置主表查询字段
            dataMatchDto.setTableName(masterTableName);
            initQueryColumn(dataMatchDto, queryColumnStr);
            // 查询融合规则 拼接sql
            PrimaryJointDto dto = new PrimaryJointDto();
            dto.setEnterpriseId(dataMatchDto.getEnterpriseId());
            dto.setDataType(dataMatchDto.getDataType());
            dto.setAppId(dataMatchDto.getAppId());
            PrimaryJoint primaryJoint = primaryJointMapper.selectPrimaryJoint(dto);
            StringBuffer leftJoinStr = new StringBuffer();
            StringBuffer allLeftJoinStr = new StringBuffer();
            // 从表逻辑表达式
            if (primaryJoint != null && StringUtil.isNotEmpty(primaryJoint.getExpression())) {
                queryColumnStr.append(Constant.Character.COMMA);
                dataMatchDto.setTableName(slaveTableName);
                dataMatchDto.setAliasName(AdminConstant.Character.RELA);
                initQueryColumn(dataMatchDto, queryColumnStr);
                // 获取left join
                getLeftJoinStr(primaryJoint, allLeftJoinStr, leftJoinStr, AdminConstant.Character.RELA, dataMatchDto.getDataType());
                dataMatchDto.setLeftJoinSql(leftJoinStr.toString());
            } else {
                // TODO 加入配置监控
            }
            dataMatchDto.setSelectFields(queryColumnStr.toString());
            String orderByStr = AdminConstant.Character.RELA + Constant.Character.POINT + dataMatchDto.getDataType()
                    + Constant.Character.UNDER_LINE + AdminConstant.Character.ID;
            dataMatchDto.setOrderByStr(orderByStr);
            // 查询
            Page<LinkedHashMap<String, String>> page = dataMatchDto.getPage();
            dataList = masterDataUnifiedMapper.getMasterDataListByPerspective(page, dataMatchDto);
            // 处理数据
            List<LinkedHashMap<String,Object>> returnList = dealMatchData(dataList, master, slave);
            newPageList.setRecords(returnList);
            newPageList.setTotal(dataList.getTotal());
            newPageList.setPages(dataList.getPages());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("getDataListByPerspective error,{}", e.getMessage());
        }
        return newPageList;
    }

    private List<LinkedHashMap<String,Object>> dealMatchData(Page<LinkedHashMap<String, String>> dataList, String master, String slave) {
        List<LinkedHashMap<String,Object>> returnList = new ArrayList<>();
        if (dataList != null && dataList.getRecords() != null && dataList.getRecords().size() > 0) {
            List<LinkedHashMap<String, String>> oldList = dataList.getRecords();
            if (oldList != null && oldList.size() > 0) {
                for(LinkedHashMap<String, String> old : oldList) {
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
                                slaveLinkedHashMap.put(key.toString(), value.toString());
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
            List<DataMatchBatchIdsDto> ids = dataMatchBatchDto.getIds();
            List<DataMatchBatchDto> list = new ArrayList<>();
            ids.stream().forEach(id->{
                DataMatchBatchDto dto = new DataMatchBatchDto();
                dto.setEnterpriseId(dataMatchBatchDto.getEnterpriseId());
                dto.setAppId(dataMatchBatchDto.getAppId());
                dto.setDataType(dataMatchBatchDto.getDataType());
                dto.setMidPlatId(id.getMidPlatId());
                dto.setThirdId(id.getThirdId());
                list.add(dto);
            });
            masterDataUnifiedMapper.dataMathBatchByMinPlat(list);
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
        initQueryColumn(dataMatchDto, queryColumnStr);
        dataMatchDto.setSelectFields(queryColumnStr.toString());
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
        initQueryColumn(dataMatchDto, queryColumnStr);
        dataMatchDto.setSelectFields(queryColumnStr.toString());
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
            //relaData2BaseData(masterDataUnifiedDto);
            masterDataUnifiedDto.setTableType(AdminConstant.Character.SINGLE);
            ((MasterDataUnifiedService) AopContext.currentProxy()).relaData2BaseData(masterDataUnifiedDto);

            // 同步主数据依赖表数据 base -> rela
            List<PrimaryRelyVo> dependentList = masterDataUnifiedMapper.getDependentInfoByDataType(masterDataUnifiedDto);
            if (dependentList != null) {
                dependentList.stream().forEach(d -> {
                    masterDataUnifiedDto.setRelaTableName(d.getRelaTable());
                    masterDataUnifiedDto.setTableName(d.getBaseTable());
                    //relaData2BaseData(masterDataUnifiedDto);
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
                                String baseColumn = (onColumn != null && onColumn.length > 0) ? onColumn[0] : "";
                                String relaColumnStr = (onColumn != null && onColumn.length > 0) ? onColumn[1] : "";
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
                                ;
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
    private void initQueryColumn(MasterDataUnifiedDto masterDataUnifiedDto, StringBuffer queryColumnStr) {
        // 需要查询的字段
        String queryColumn = masterDataUnifiedMapper.selectColumnByDataType(masterDataUnifiedDto);
        if (StringUtil.isNotEmpty(queryColumn)) {
            queryColumn = queryColumn.replace(AdminConstant.Character.START_STR, masterDataUnifiedDto.getAliasName());
            queryColumnStr.append(queryColumn);
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
                                StringBuffer leftJoinStr, String aliasName, String dataType) {
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
        leftJoinStr.append(relaTableName).append(AdminConstant.Character.SPACE).append(aliasName);
        leftJoinStr.append(AdminConstant.Character.ON);
        leftJoinStr.append(relaLeftJoinStr);
    }
}

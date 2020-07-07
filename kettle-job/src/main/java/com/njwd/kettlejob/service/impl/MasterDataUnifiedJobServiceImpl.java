package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.common.KettleJobConstant;
import com.njwd.entity.admin.dto.DataMatchBatchTaskDto;
import com.njwd.entity.admin.dto.MasterDataUnifiedDto;
import com.njwd.entity.admin.dto.MasterDataUnifiedFieldDto;
import com.njwd.entity.admin.dto.PrimaryJointDto;
import com.njwd.entity.admin.vo.PrimaryJointVo;
import com.njwd.entity.admin.vo.PrimaryRelyVo;
import com.njwd.exception.ResultCode;
import com.njwd.kettlejob.mapper.MasterDataUnifiedJobMapper;
import com.njwd.kettlejob.mapper.PrimaryJointMapper;
import com.njwd.kettlejob.service.MasterDataUnifiedJobService;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author XiaFq
 * @Description MasterDataUnifiedJobServiceImpl 企业应用service实现类
 * @Date 2019/11/11 9:48 上午
 * @Version 1.0
 */
@Service
public class MasterDataUnifiedJobServiceImpl implements MasterDataUnifiedJobService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MasterDataUnifiedJobServiceImpl.class);

    @Resource
    MasterDataUnifiedJobMapper masterDataUnifiedJobMapper;

    @Resource
    PrimaryJointMapper primaryJointMapper;

    /**
     * @return int
     * @Author XiaFq
     * @Description 将rela表数据保存到中台表数据及中台id回写
     * @Date 2019/11/24 10:39 上午
     * @Param [masterDataUnifiedDto]
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public String doDealMinPlatDataFromRelaJob(String appId,String enteId,Map<String,String> params) {
        String dataType = params.get(AdminConstant.JOB_PARAM_KEY.DATATYPE);
        JSONObject resultJson = new JSONObject();
        if (StringUtil.isEmpty(enteId) || StringUtil.isEmpty(dataType)) {
            LOGGER.error("doDealMinPlatDataFromRelaJob enteId is null or dataType is null");
            resultJson.put("status",KettleJobConstant.KettleException.FAIL);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg", ResultCode.PARAMS_NOT.getMessage());
            return resultJson.toJSONString();
        }
        MasterDataUnifiedDto masterDataUnifiedDto = new MasterDataUnifiedDto();
        masterDataUnifiedDto.setEnterpriseId(enteId);
        masterDataUnifiedDto.setDataType(dataType);
        appId = masterDataUnifiedJobMapper.getMasterAppData(masterDataUnifiedDto);
        if (StringUtil.isEmpty(appId)) {
            LOGGER.error("doDealMinPlatDataFromRelaJob app is null ");
            resultJson.put("status",KettleJobConstant.KettleException.FAIL);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg", ResultCode.PARAMS_NOT.getMessage());
            return resultJson.toJSONString();
        }
        masterDataUnifiedDto.setAppId(appId);
        try {
            String tableName = AdminConstant.Character.BASE_TABLE_START + masterDataUnifiedDto.getDataType();
            String relaTableName = AdminConstant.Character.BASE_TABLE_START + masterDataUnifiedDto.getDataType() + AdminConstant.Character.RELA_TABLE_END;
            masterDataUnifiedDto.setTableName(tableName);
            masterDataUnifiedDto.setRelaTableName(relaTableName);

            // 操作主表的数据 base -> rela
            masterDataUnifiedDto.setTableType(AdminConstant.Character.SINGLE);
            ((MasterDataUnifiedJobService) AopContext.currentProxy()).relaData2BaseData(masterDataUnifiedDto);

            // 同步主数据依赖表数据 base -> rela
            List<PrimaryRelyVo> dependentList = masterDataUnifiedJobMapper.getDependentInfoByDataType(masterDataUnifiedDto);
            if (dependentList != null) {
                dependentList.stream().forEach(d -> {
                    masterDataUnifiedDto.setRelaTableName(d.getRelaTable());
                    masterDataUnifiedDto.setTableName(d.getBaseTable());
                    masterDataUnifiedDto.setTableType(AdminConstant.Character.MANY);
                    ((MasterDataUnifiedJobService) AopContext.currentProxy()).relaData2BaseData(masterDataUnifiedDto);
                });
            }

            // 查询依赖表中是否存在依赖该表的数据，如果存在就直接回写该id
            List<PrimaryRelyVo> primaryRelyVoList = masterDataUnifiedJobMapper.getBeDependentByRelyData(masterDataUnifiedDto);
            if (primaryRelyVoList != null && primaryRelyVoList.size() > 0) {
                primaryRelyVoList.stream().forEach(p -> {
                    // 回写base表数据id
                    masterDataUnifiedDto.setRelaTableName(p.getBaseTable());
                    String baseTable = masterDataUnifiedDto.getTableName();
                    masterDataUnifiedDto.setTableName(baseTable + AdminConstant.Character.RELA_TABLE_END);
                    if (StringUtil.isNoneBlank(masterDataUnifiedDto.getAppId())) {
                        String queryConditionStr = " and d.app_id = '" + masterDataUnifiedDto.getAppId() + "'";
                        masterDataUnifiedDto.setQueryConditionStr(queryConditionStr);
                    }
                    masterDataUnifiedJobMapper.dataWriteBackForIdMany(masterDataUnifiedDto);
                    // 回写rela表数据id
                    masterDataUnifiedDto.setRelaTableName(p.getRelaTable());
                    masterDataUnifiedDto.setTableName(baseTable);
                    masterDataUnifiedJobMapper.dataWriteBackForIdManyV2(masterDataUnifiedDto);

                });
            }
            resultJson.put("status",KettleJobConstant.KettleException.SUCCESS);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg",AdminConstant.Character.PARAM_NULL);
            return resultJson.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("saveMinPlatDataFromRela error ,{}", e.getMessage());
            // 事务补偿回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            resultJson.put("status",KettleJobConstant.KettleException.FAIL);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg",e.getMessage());
            return resultJson.toJSONString();
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
        String selectColumns = masterDataUnifiedJobMapper.queryDataUniformField(md);
        String insertColumns = md.getDataType() + AdminConstant.Character.END_ID + Constant.Character.COMMA + selectColumns.replace(aliasName + Constant.Character.POINT, Constant.Character.NULL_VALUE);
        String id = AdminConstant.Character.SELECT_UUID;
        StringBuffer selectColumn = new StringBuffer();
        String idStr = Constant.Character.NULL_VALUE;
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
        // and (Rela.${dataType}_id is null or Rela.${dataType}_id = '') 先去掉 做全表数据更新
        masterDataUnifiedJobMapper.saveMidPlatDataFromRela(masterDataUnifiedDto);
        // id 回写
        // 如果是主数据对应的关联表 直接将主数据的id回写到关联表中
        if (AdminConstant.Character.SINGLE.equals(masterDataUnifiedDto.getTableType())) {
            LOGGER.info("MasterDataUnifiedServiceImpl.relaData2BaseData SINGLE is {}", JSONObject.toJSONString(masterDataUnifiedDto));
            masterDataUnifiedJobMapper.dataWriteBackForId(masterDataUnifiedDto);
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
            masterDataUnifiedJobMapper.dataWriteBackForId(masterDataUnifiedDto);
            // 回写关联表 rela表id
            masterDataUnifiedDto.setAppId(appId);
            masterDataUnifiedDto.setRelaTableName(relyRelaTable);
            LOGGER.info("MasterDataUnifiedServiceImpl.relaData2BaseData MANY rela is {}", JSONObject.toJSONString(masterDataUnifiedDto));
            masterDataUnifiedJobMapper.dataWriteBackForId(masterDataUnifiedDto);
        }
    }

    /**
     * @param params
     * @return int -1 添加失败 0 添加成功
     * @Author XiaFq
     * @Description 批量任务匹配主数据
     * @Date 2019/11/27 2:20 下午
     * @Param dataMatchBatchTaskDto
     */
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public String doDealMatchDataBatchJob(String appId,String enteId,Map<String,String> params) {
        String dataType = params.get(AdminConstant.JOB_PARAM_KEY.DATATYPE);
        JSONObject resultJson = new JSONObject();
        FastUtils.checkParams(enteId, dataType);
        try {
            // 查询所有配置融合规则的应用
            PrimaryJointDto primaryJointDto = new PrimaryJointDto();
            primaryJointDto.setEnterpriseId(enteId);
            primaryJointDto.setDataType(dataType);
            List<PrimaryJointVo> list = primaryJointMapper.selectPrimaryJointList(primaryJointDto);
            List<DataMatchBatchTaskDto> dataMatchBatchTaskDtos = new ArrayList<>();
            if (list != null && list.size() > 0) {
                for (PrimaryJointVo primaryJointVo : list) {
                    DataMatchBatchTaskDto dataMatchBatchTaskDto = new DataMatchBatchTaskDto();
                    dataMatchBatchTaskDto.setEnterpriseId(enteId);
                    dataMatchBatchTaskDto.setDataType(dataType);
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
                                // shop_id,shop_id
                                String[] onColumn = onColumns.split(Constant.Character.COMMA);
                                // shop_id
                                String baseColumn = (onColumn != null && onColumn.length > 0) ? onColumn[0] : "";
                                // shop_id
                                String relaColumnStr = (onColumn != null && onColumn.length > 0) ? onColumn[1] : "";
                                // 去掉逻辑运算符
                                String relaColumn = relaColumnStr.replace(AdminConstant.Character.LOGIC_AND, Constant.Character.NULL_VALUE).replace(AdminConstant.Character.LOGIC_OR, Constant.Character.NULL_VALUE);
                                // 根据规则拼接 base表查询字段 如 t.user_id, t.user_name, t.mobile
                                // t.shop_id,
                                String jointColumn = AdminConstant.Character.T + Constant.Character.POINT + baseColumn + Constant.Character.COMMA;
                                baseQueryColumn.append(jointColumn);
                                // base.shop_id = rela.shop_id
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
                            // 如果查询字段中没有包含更新字段，添加
                            if (baseQueryColumn.indexOf(updateIdStr) == -1) {
                                baseQueryColumn.append(updateIdStr);
                            }
                            if (baseQueryColumn.toString().endsWith(Constant.Character.COMMA)) {
                                dataMatchBatchTaskDto.setBaseQueryColumn(baseQueryColumn.substring(0, baseQueryColumn.toString().length() -1));
                            } else {
                                dataMatchBatchTaskDto.setBaseQueryColumn(baseQueryColumn.toString());
                            }
                            dataMatchBatchTaskDto.setBaseQueryCondition(baseQueryCondition.toString());
                            dataMatchBatchTaskDto.setRelaQueryCondition(relaQueryCondition.toString());
                            dataMatchBatchTaskDto.setLeftJoinOnCondition(onColumnS.toString());
                        }
                    }
                    dataMatchBatchTaskDtos.add(dataMatchBatchTaskDto);
                }
                LOGGER.info("MasterDataUnifiedServiceImpl.dataMatchBatch dataMatchBatchTaskDtos is {}", JSONObject.toJSONString(dataMatchBatchTaskDtos));
                masterDataUnifiedJobMapper.dataMathBatchTask(dataMatchBatchTaskDtos);
            }
            resultJson.put("status",KettleJobConstant.KettleException.SUCCESS);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg",AdminConstant.Character.PARAM_NULL);
            return resultJson.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("dataMatchBatch throw exception, {}", e.getMessage());
            resultJson.put("status",KettleJobConstant.KettleException.FAIL);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg",e.getMessage());
            return resultJson.toJSONString();
        }
    }
}

package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.common.KettleJobConstant;
import com.njwd.entity.admin.OneToManySql;
import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.TableObj;
import com.njwd.entity.admin.dto.EnterpriseDataTypeDto;
import com.njwd.entity.admin.vo.PrimaryPaddingVo;
import com.njwd.exception.ResultCode;
import com.njwd.kettlejob.mapper.PrimaryPaddingMapper;
import com.njwd.kettlejob.service.PrimaryPaddingService;
import com.njwd.utils.AdminUtil;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
    private PrimaryPaddingMapper primaryPaddingMapper;

    /**
     * @Author Chenfulian
     * @Description 查询填充规则，并且拼接sql
     * @Date  2019/11/25 19:06
     * @Param
     * @return
     * @param params
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public String dealPrimaryPaddingJob(String appId,String enteId,Map<String,String> params) {
        LOGGER.info(String.format("dealPrimaryPaddingJob params:"), JSON.toJSONString(params));
        JSONObject resultJson = new JSONObject();
        // 判断参数为空
        if (params == null) {
            resultJson.put("status",KettleJobConstant.KettleException.FAIL);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg", ResultCode.PARAMS_NOT.getMessage());
            return resultJson.toJSONString();
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = enteId;
        String dataType = params.get(AdminConstant.JOB_PARAM_KEY.DATATYPE);
        if (StringUtil.isEmpty(enterpriseId) || StringUtil.isEmpty(dataType)) {
            resultJson.put("status",KettleJobConstant.KettleException.FAIL);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg", ResultCode.PARAMS_NOT.getMessage());
            return resultJson.toJSONString();
        }

        EnterpriseDataTypeDto enterpriseDataTypeDto = new EnterpriseDataTypeDto();
        enterpriseDataTypeDto.setEnterpriseId(enterpriseId);
        enterpriseDataTypeDto.setDataType(dataType);

        //获取填充规则
        List<PrimaryPaddingVo> primaryPaddingVoList = primaryPaddingMapper.getPrimayPadding(enterpriseDataTypeDto.getEnterpriseId(),enterpriseDataTypeDto.getDataType());
        if (primaryPaddingVoList == null || primaryPaddingVoList.size() == 0) {
            resultJson.put("status",KettleJobConstant.KettleException.SUCCESS);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg", AdminConstant.Character.PARAM_NULL);
            return resultJson.toJSONString();
        }

        //一对一的base表，直接用update更新对应字段的数据
        Map<String,String> one2OneSqlMap = new HashMap<>();
        //一对多的base表，先清除多余数据，再用replace into更新对应字段的数据
        Map<String, OneToManySql> one2ManySqlMap = new HashMap<>();

        try {
            appendSqlGroupByTable(enterpriseDataTypeDto,primaryPaddingVoList, one2OneSqlMap, one2ManySqlMap);
            if (!one2OneSqlMap.isEmpty()) {
                primaryPaddingMapper.executeOneToOnePadding(one2OneSqlMap);
            }
            if (!one2ManySqlMap.isEmpty()) {
                primaryPaddingMapper.executeOneToManyDelete(one2ManySqlMap);
                primaryPaddingMapper.executeOneToManyInsert(one2ManySqlMap);
            }
            resultJson.put("status",KettleJobConstant.KettleException.SUCCESS);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg", AdminConstant.Character.PARAM_NULL);
            return resultJson.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("dealPrimaryPaddingJob error ,{}", e.getMessage());
            // 事务补偿回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            resultJson.put("status",KettleJobConstant.KettleException.FAIL);
            resultJson.put("param",AdminConstant.Character.PARAM_NULL);
            resultJson.put("msg", e.getMessage());
            return resultJson.toJSONString();
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


}

package com.njwd.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.admin.mapper.DataMapMapper;
import com.njwd.admin.service.DataMapService;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.DataMap;
import com.njwd.entity.admin.DataMapSql;
import com.njwd.entity.admin.dto.DataMapKeyDto;
import com.njwd.entity.admin.vo.MapDataVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.utils.StringUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @Description:数据映射服务实现类
 *
 * @Author: yuanman
 * @Date: 2019/11/25 11:40
 */
@Service
public class DataMapServiceImpl implements DataMapService {

    @Resource
    private DataMapMapper dataMapMapper;


    /**
     * @param dataMap
     * @param dataMapKeyDto
     * @Description:分页返回源数据
     * @Author: yuanman
     * @Date: 2019/11/26 15:06
     * @return:com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.njwd.entity.admin.vo.MapDataVo>
     */
    @Override
    public Page<MapDataVo> getSourceData(DataMap dataMap, DataMapKeyDto dataMapKeyDto) {
        //分页返回
        Page<MapDataVo> page = dataMapKeyDto.getPage();
        if(!StringUtil.isSqlValid(dataMapKeyDto.getName())){
            throw new ServiceException(ResultCode.PARAM_EXISIS_SQL_INJECT);
        }
        //生成sql的相关信息
        DataMapSql dataMapSql = createQuerySourceSql(dataMap, dataMapKeyDto);
        //执行sql,返回数据
        page = dataMapMapper.selectSourceData(page, dataMapSql);
        // key1_key2_key_3-name-code, key1_key2_key_3-name-code  -->List<MapDataVo>
        convertToMapVoList(page);
        return page;
    }


    /**
     * @param dataMap
     * @param dataMapKeyDto
     * @Description:查询目标表数据
     * @Author: yuanman
     * @Date: 2019/11/27 11:03
     * @return:java.util.List<com.njwd.entity.admin.vo.MapDataVo>
     */
    @Override
    public Page<MapDataVo> getTargetData(DataMap dataMap, DataMapKeyDto dataMapKeyDto) {
        //分页返回
        Page<MapDataVo> page = dataMapKeyDto.getPage();

        String dataType=dataMapKeyDto.getType();
        List<String> targetIds = Arrays.asList(StringUtil.split(dataMap.getTargetTableKey(),Constant.Character.COMMA));
        //定义sql
        DataMapSql dataMapSql = new DataMapSql();
        //设置enteId
        dataMapSql.setEnteId(dataMap.getEnteId());
        //设置关系表
        dataMapSql.setMapTable(dataMap.getDatamapTable());
        //设置目标表
        dataMapSql.setTargetTable(dataMap.getTargetTable());
        //拼接source column  "m.target_" + targetIds.get(0);
        StringBuilder sourceColumnSb = new StringBuilder();
        build(sourceColumnSb, AdminConstant.DataMap.MAP_TABLE_PRE, AdminConstant.DataMap.TARGET_PRE, targetIds.get(Constant.Number.ZERO));
        dataMapSql.setSourceColumns(sourceColumnSb.toString());
        //拼接target column
        dataMapSql.setTargetColumns(getQueryTargetColumnByTarget(dataMap));
        //拼接目标表与源表的关联条件
        dataMapSql.setTMCondition(getTargetMapCondition(dataMap));
        //设置映射条件：已匹配，未匹配和全部
        StringBuilder mapCondition=new StringBuilder();
        if (dataType.equals(AdminConstant.DataMap.MAPED)) {
            build(mapCondition,AdminConstant.DataMap.MAP_TABLE_PRE,AdminConstant.DataMap.TARGET_PRE,targetIds.get(0),AdminConstant.Character.IS_NOT_NULL);
        } else if (dataType.equals(AdminConstant.DataMap.UNMAPED)) {
            build(mapCondition,AdminConstant.DataMap.MAP_TABLE_PRE,AdminConstant.DataMap.TARGET_PRE,targetIds.get(0),AdminConstant.Character.IS_NULL);
        } else {
            mapCondition.append(AdminConstant.Character.ONE_EQUAL_ONE);
        }
        dataMapSql.setPrimrayCondition(mapCondition.toString());
        StringBuilder nameConditionSb = new StringBuilder();
        //设置名称模糊匹配
        if(!StringUtil.isSqlValid(dataMapKeyDto.getName())) {
            throw new ServiceException(ResultCode.PARAM_EXISIS_SQL_INJECT);
        }
        if (StringUtil.isNotEmpty(dataMapKeyDto.getName())) {
            // and t.name like '%name%'
            build(nameConditionSb,AdminConstant.Character.AND,AdminConstant.DataMap.TARGET_TABLE_PRE,dataMap.getTargetTableColumn(),
                    AdminConstant.Character.LIKE,AdminConstant.Character.SINGLE_QUOTE,Constant.Character.Percent,dataMapKeyDto.getName(),
                    Constant.Character.Percent,AdminConstant.Character.SINGLE_QUOTE
            );
        }
        String nameCondition=nameConditionSb.toString();
        if(StringUtil.isNotEmpty(nameCondition)){
            dataMapSql.setPrimrayCondition(mapCondition.toString()+nameConditionSb.toString());
        }
        //拼接groupBy, t.key1,t.key2
        StringBuilder groupBy = new StringBuilder();
        int i = Constant.Number.ONE;
        for (String targetId : targetIds) {
            if (i == Constant.Number.ONE) {
                build(groupBy,AdminConstant.DataMap.TARGET_TABLE_PRE,targetId);
            } else {
                build(groupBy,AdminConstant.Character.SPACE,Constant.Character.COMMA,AdminConstant.Character.SPACE ,AdminConstant.DataMap.TARGET_TABLE_PRE,targetId);
            }
            i++;
        }
        dataMapSql.setGroupBy(groupBy.toString());
        return dataMapMapper.selectTargetData(page,dataMapSql);
    }

    /**
     * @Description:根据目标表查询关联源表数据
     * @Author: yuanman
     * @Date: 2019/11/28 17:13
     * @param dataMap
     * @param primaryVo
     * @return:java.util.List<com.njwd.entity.admin.vo.MapDataVo>
     */
    @Override
    public List<MapDataVo> getSingleMapData(DataMap dataMap,MapDataVo primaryVo,String type){
        checkViewType(type);
        DataMapSql dataMapSql=new DataMapSql();
        try{
            dataMapSql.setEnteId(dataMap.getEnteId());
            dataMapSql.setMapTable(dataMap.getDatamapTable());
            //映射数据的主键
            List<String> mapIds;
            //主表的主键
            List<String> primaryIds=new ArrayList<>();
            String name;
            String code;
            //映射数据的视角
            String mapType;
            String pre="";
            if(type.equals(AdminConstant.DataMap.SOURCE)){
                //如果是源视角则查目标表的字段
                mapType=AdminConstant.DataMap.TARGET;
                mapIds=Arrays.asList(StringUtil.split(dataMap.getTargetTableKey(),Constant.Character.COMMA));
                primaryIds=Arrays.asList(StringUtil.split(dataMap.getSourceTableKey(),Constant.Character.COMMA));
                name=dataMap.getTargetTableColumn();
                code=dataMap.getTargetTableCode();
                dataMapSql.setTargetTable(dataMap.getTargetTable());
                dataMapSql.setTMCondition(getTargetMapCondition(dataMap));
                dataMapSql.setTargetColumns(getPrimaryColumn(mapIds,name,code,mapType));
                //pre="m.source_";
                pre = AdminConstant.DataMap.MAP_TABLE_PRE + AdminConstant.DataMap.SOURCE_PRE;
            }else if(type.equals(AdminConstant.DataMap.TARGET)){
                //如果是目标视角则查源表字段
                mapType=AdminConstant.DataMap.SOURCE;
                mapIds=Arrays.asList(StringUtil.split(dataMap.getSourceTableKey(),Constant.Character.COMMA));
                primaryIds=Arrays.asList(StringUtil.split(dataMap.getTargetTableKey(),Constant.Character.COMMA));
                name=dataMap.getSourceTableColumn();
                code=dataMap.getSourceTableCode();
                dataMapSql.setSourceTable(dataMap.getSourceTable());
                dataMapSql.setSMCondition(getSourceMapCondition(dataMap));
                dataMapSql.setSourceColumns(getPrimaryColumn(mapIds,name,code,mapType));
                //pre="m.target_";
                pre = AdminConstant.DataMap.MAP_TABLE_PRE + AdminConstant.DataMap.TARGET_PRE;
            }
            //拼接条件
            StringBuilder primaryCondition= new StringBuilder();
            int i=Constant.Number.ZERO;
            String key1=primaryVo.getKey1();
            String key2=primaryVo.getKey2();
            String key3=primaryVo.getKey3();
            String key4=primaryVo.getKey4();
            String key5=primaryVo.getKey5();
            if(StringUtil.isNotEmpty(key1)){
               // m.target_key1 = 'key1'
                build(primaryCondition,pre,primaryIds.get(0),Constant.Character.EQUALS,AdminConstant.Character.SINGLE_QUOTE,key1,AdminConstant.Character.SINGLE_QUOTE);
                i=Constant.Number.ONE;
            }
            if(StringUtil.isNotEmpty(key2)){
                // m.target_key2 = 'key2'
                build(primaryCondition,AdminConstant.Character.AND,pre,primaryIds.get(1),Constant.Character.EQUALS,AdminConstant.Character.SINGLE_QUOTE,key2,AdminConstant.Character.SINGLE_QUOTE);
                i=Constant.Number.TWO;
            }
            if(StringUtil.isNotEmpty(key3)){
                build(primaryCondition,AdminConstant.Character.AND,pre,primaryIds.get(2),Constant.Character.EQUALS,AdminConstant.Character.SINGLE_QUOTE,key3,AdminConstant.Character.SINGLE_QUOTE);
                i=Constant.Number.THREE;
            }
            if(StringUtil.isNotEmpty(key4)){
                build(primaryCondition,AdminConstant.Character.AND,pre,primaryIds.get(3),Constant.Character.EQUALS,AdminConstant.Character.SINGLE_QUOTE,key4,AdminConstant.Character.SINGLE_QUOTE);
                i=Constant.Number.FOUR;
            }
            if(StringUtil.isNotEmpty(key5)){
                build(primaryCondition,AdminConstant.Character.AND,pre,primaryIds.get(4),Constant.Character.EQUALS,AdminConstant.Character.SINGLE_QUOTE,key5,AdminConstant.Character.SINGLE_QUOTE);
                i=Constant.Number.FIVE;
            }
            if(i!=primaryIds.size()){
                throw new ServiceException(ResultCode.DATA_MAP_KEYCOUNT_WRONG);
            }
            dataMapSql.setPrimrayCondition(primaryCondition.toString());
        }catch(ArrayIndexOutOfBoundsException e){
            throw new ServiceException(ResultCode.DATA_MAP_KEYCOUNT_WRONG);
        }
        return dataMapMapper.selectSourceDataByTarget(dataMapSql);
    }
    @Override
    @Transactional(rollbackFor =Exception.class )
    public int updateMapData(DataMap dataMap,String viewType,List<MapDataVo> addList,List<MapDataVo> deleteList){
        int count=0;
        //新增
        if(null!=addList&&addList.size()>0){
            count=saveMapData(dataMap,addList,viewType);
        }

        //删除
        if(null!=deleteList&&deleteList.size()>0){
            for(MapDataVo vo:deleteList){
                int i=0;
                if(null!=vo.getMapData()&&vo.getMapData().size()>0){
                    i= deleteMapData(dataMap,vo,viewType);
                }
                count=count+i;
            }
        }
        return count;
    }

    /**
     * @Description:保存映射数据
     * @Author: yuanman
     * @Date: 2019/11/28 11:21
     * @param dataMap
     * @param data
     * @param viewType
     * @return:int
     */
    @Override
    public int saveMapData(DataMap dataMap, List<MapDataVo> data, String viewType) {
        checkViewType(viewType);
        int insertCount = 0;
        try{
            List<String> sourceIds = Arrays.asList(StringUtil.split(dataMap.getSourceTableKey(), Constant.Character.COMMA));
            List<String> targetIds = Arrays.asList(StringUtil.split(dataMap.getTargetTableKey(), Constant.Character.COMMA));
            DataMapSql savesql = new DataMapSql();
            savesql.setMapTable(dataMap.getDatamapTable());
            //拼接columns
            StringBuilder columns=new StringBuilder();
            //"ente_id,"
            build(columns,AdminConstant.JOB_PARAM_KEY.ENTE_ID_DB,Constant.Character.COMMA);
            if (viewType.equals(AdminConstant.DataMap.SOURCE)) {
                for (String sourceId : sourceIds) {
                    //"source_" + sourceId + ",";
                    build(columns,AdminConstant.DataMap.SOURCE_PRE,sourceId,Constant.Character.COMMA);
                }
                for (String targetId : targetIds) {
                    //"target_" + targetId + ",";
                    build(columns,AdminConstant.DataMap.TARGET_PRE,targetId,Constant.Character.COMMA);
                }
            } else if (viewType.equals(AdminConstant.DataMap.TARGET)) {
                for (String targetId : targetIds) {
                    //"target_" + targetId + ",";
                    build(columns,AdminConstant.DataMap.TARGET_PRE,targetId,Constant.Character.COMMA);
                }
                for (String sourceId : sourceIds) {
                    //"source_" + sourceId + ",";
                    build(columns,AdminConstant.DataMap.SOURCE_PRE,sourceId,Constant.Character.COMMA);
                }
            }
            //去掉逗号
            String columnsStr = columns.substring(0, columns.length() - 1);
            savesql.setColumns(columnsStr);
            //分批执行拼接values，每次最多一百条
            int batch = 1;
            while (true) {
                int start = (batch - 1) * AdminConstant.DataMap.SAVESIZE;
                int end = batch * AdminConstant.DataMap.SAVESIZE;
                if (end >= data.size()) {
                    end = data.size();
                }
                StringBuilder values=new StringBuilder();
                for (int i = start; i < end; i++) {
                    MapDataVo vo = data.get(i);
                    //拼接values
                    build(values,getValues(viewType,sourceIds,targetIds,vo,dataMap.getEnteId()),Constant.Character.COMMA);
                }
                //去掉最后的逗号
                String valuesStr = values.substring(0, values.length() - 1);
                savesql.setValues(valuesStr);
                insertCount = insertCount + dataMapMapper.insertMapData(savesql);
                if (end >= data.size()) {
                    break;
                }
                batch++;
            }
        }catch(ArrayIndexOutOfBoundsException e){
            //角标越界说明key的数量不对
            throw new ServiceException(ResultCode.DATA_MAP_KEYCOUNT_WRONG);
        }catch(DuplicateKeyException e){
            //数据重复
            throw new ServiceException(ResultCode.DUPLICATE_KEY,e.getCause().getMessage());
        }
        return insertCount;
    }


    /**
     * @Description:删除映射数据
     * @Author: yuanman
     * @Date: 2019/11/29 11:31
     * @param dataMap
     * @param vo
     * @param viewType
     * @return:int
     */
    @Override
    public int deleteMapData(DataMap dataMap,MapDataVo vo,String viewType){
        //最终拼接成：delete from maptable m where m.prea_key1='key1' and m.prea_key2='key2' and ((m.preb_key1='aa' and m.preb_key2='bb') or (m.preb_key1='cc' and m.preb_key2='dd'))
        checkViewType(viewType);
        DataMapSql deletesql = new DataMapSql();
         try{
            List<String> sourceIds = Arrays.asList(StringUtil.split(dataMap.getSourceTableKey(), Constant.Character.COMMA));
            List<String> targetIds = Arrays.asList(StringUtil.split(dataMap.getTargetTableKey(), Constant.Character.COMMA));
            deletesql.setEnteId(dataMap.getEnteId());
            StringBuilder condition = new StringBuilder();
            String pre;
            //拼接主表条件
            if (viewType.equals(AdminConstant.DataMap.SOURCE)) {
                pre = AdminConstant.DataMap.SOURCE_PRE;
                condition.append(builtCondtitionByVoAndPre(sourceIds,vo,pre));
            } else if (viewType.equals(AdminConstant.DataMap.TARGET)) {
                pre = AdminConstant.DataMap.TARGET_PRE;
                condition.append(builtCondtitionByVoAndPre(targetIds,vo,pre));
            }
            //拼接映射条件
            List<MapDataVo> mapVos=vo.getMapData();
            StringBuilder mapCondition=new StringBuilder();
            for(MapDataVo mapVo: mapVos){
                if (viewType.equals(AdminConstant.DataMap.SOURCE)) {
                    //" ("+builtCondtitionByVoAndPre(targetIds,mapVo,"target_")+") or";
                    build(mapCondition,AdminConstant.Character.SPACE,AdminConstant.Character.LEFT_PARENTHESIS,
                            builtCondtitionByVoAndPre(targetIds,mapVo,AdminConstant.DataMap.TARGET_PRE),
                            AdminConstant.Character.RIGHT_PARENTHESIS,AdminConstant.Character.OR);
                } else if (viewType.equals(AdminConstant.DataMap.TARGET)) {
                    //" ("+builtCondtitionByVoAndPre(sourceIds,mapVo,"source_")+") or";
                    build(mapCondition,AdminConstant.Character.SPACE,AdminConstant.Character.LEFT_PARENTHESIS,
                            builtCondtitionByVoAndPre(sourceIds,mapVo,AdminConstant.DataMap.SOURCE_PRE),
                            AdminConstant.Character.RIGHT_PARENTHESIS,AdminConstant.Character.OR);
                }
            }
            //去掉最后的or
            String mapConditionStr=mapCondition.substring(0,mapCondition.length()-3);
            //" and  ("+mapConditionStr+")";
            build(condition,AdminConstant.Character.AND,AdminConstant.Character.LEFT_PARENTHESIS,mapConditionStr,AdminConstant.Character.RIGHT_PARENTHESIS);
            deletesql.setMapTable(dataMap.getDatamapTable());
            deletesql.setPrimrayCondition(condition.toString());
         }catch(ArrayIndexOutOfBoundsException e){
            throw new ServiceException(ResultCode.DATA_MAP_KEYCOUNT_WRONG);
         }
        return dataMapMapper.deleteMapData(deletesql);
    }

    /**
     * @Description:构建删除映射数据的条件
     * @Author: yuanman
     * @Date: 2019/11/29 11:31
     * @param ids
     * @param vo
     * @param pre
     * @return:java.lang.String
     */
    private String builtCondtitionByVoAndPre(List<String> ids,MapDataVo vo,String pre){
        //最终拼接成：pre_key1='key1' and pre_key2='key2' and pre_key3='key3'
        StringBuilder condition=new StringBuilder();
        //拼接主表条件
        if(!checkSql(vo)){
            throw new ServiceException(ResultCode.PARAM_EXISIS_SQL_INJECT);
        }
        String key1 = vo.getKey1();
        String key2 = vo.getKey2();
        String key3 = vo.getKey3();
        String key4 = vo.getKey4();
        String key5 = vo.getKey5();
        int i = 0;
        if (StringUtil.isNotEmpty(key1)) {
            i = 1;
            //condition = condition + "   " + pre + ids.get(0) + " = '" + vo.getKey1() + "'";
            build(condition,AdminConstant.Character.SPACE,pre+ids.get(0),AdminConstant.Character.EQUALS_SPACE,
            AdminConstant.Character.SINGLE_QUOTE,vo.getKey1(),AdminConstant.Character.SINGLE_QUOTE);
        }
        if (StringUtil.isNotEmpty(key2)) {
            i = 2;
            build(condition,AdminConstant.Character.AND,pre+ids.get(1),AdminConstant.Character.EQUALS_SPACE,
                    AdminConstant.Character.SINGLE_QUOTE,vo.getKey2(),AdminConstant.Character.SINGLE_QUOTE);
        }
        if (StringUtil.isNotEmpty(key3)) {
            i = 3;
            build(condition,AdminConstant.Character.AND,pre+ids.get(2),AdminConstant.Character.EQUALS_SPACE,
                    AdminConstant.Character.SINGLE_QUOTE,vo.getKey3(),AdminConstant.Character.SINGLE_QUOTE);
        }
        if (StringUtil.isNotEmpty(key4)) {
            i = 4;
            build(condition,AdminConstant.Character.AND,pre+ids.get(3),AdminConstant.Character.EQUALS_SPACE,
                    AdminConstant.Character.SINGLE_QUOTE,vo.getKey4(),AdminConstant.Character.SINGLE_QUOTE);
        }
        if (StringUtil.isNotEmpty(key5)) {
            i = 5;
            build(condition,AdminConstant.Character.AND,pre+ids.get(4),AdminConstant.Character.EQUALS_SPACE,
                    AdminConstant.Character.SINGLE_QUOTE,vo.getKey5(),AdminConstant.Character.SINGLE_QUOTE);
        }
        if (i != ids.size()) {
            throw new ServiceException(ResultCode.DATA_MAP_KEYCOUNT_WRONG);
        }
        return condition.toString();
    }

    /**
     * @Description:插入数据时，拼接 values
     * @Author: yuanman
     * @Date: 2019/11/28 11:23
     * @param type
     * @param sourceIds
     * @param targetIds
     * @param vo
     * @return:java.lang.String
     */
    private String getValues(String type,List<String> sourceIds,List<String> targetIds,MapDataVo vo,String enteId) {
        //最终拼接成： （'enteId','key1','key2','key3','keya','keyb'）,（'enteId','key1','key2','key3','keya','keyb'）,（'enteId','key1','key2','key3','keya','keyb'）
        if(!checkSql(vo)) {
            throw new ServiceException(ResultCode.PARAM_EXISIS_SQL_INJECT);
        }
        List<MapDataVo> mapVos = vo.getMapData();
        StringBuilder values=new StringBuilder();
        String primaryValues="";
        if(type.equals(AdminConstant.DataMap.SOURCE)){
            primaryValues= bulidValuesByVo(sourceIds,vo);
        }else if(type.equals(AdminConstant.DataMap.TARGET)){
            primaryValues= bulidValuesByVo(targetIds,vo);
        }

        for (MapDataVo mapVo : mapVos) {
            if(!checkSql(mapVo)) {
                throw new ServiceException(ResultCode.PARAM_EXISIS_SQL_INJECT);
            }
            StringBuilder mapValues=new StringBuilder();
            if(type.equals(AdminConstant.DataMap.SOURCE)){
                mapValues.append(bulidValuesByVo(targetIds,mapVo));
            }else if(type.equals(AdminConstant.DataMap.TARGET)){
                mapValues.append(bulidValuesByVo(sourceIds,mapVo));
            }
            build(values,AdminConstant.Character.LEFT_PARENTHESIS,AdminConstant.Character.SINGLE_QUOTE,enteId,
                    AdminConstant.Character.SINGLE_QUOTE, Constant.Character.COMMA,primaryValues,Constant.Character.COMMA,mapValues.toString(),AdminConstant.Character.RIGHT_PARENTHESIS,
                    Constant.Character.COMMA);
        }
        //去掉最后的逗号
        String valuesStr = values.substring(0, values.length() - 1);
        return valuesStr;
    }

    /**
     * @Description:根据vo获取value
     * @Author: yuanman
     * @Date: 2019/11/28 11:32
     * @param ids
     * @param vo
     * @return:java.lang.String
     */
    private String bulidValuesByVo(List<String> ids,MapDataVo vo) {
        StringBuilder values = new StringBuilder();
        //拼接ids
        String key1 = vo.getKey1();
        String key2 = vo.getKey2();
        String key3 = vo.getKey3();
        String key4 = vo.getKey4();
        String key5 = vo.getKey5();
        int i=0;
        if (StringUtil.isNotEmpty(key1)){
            build(values,AdminConstant.Character.SINGLE_QUOTE,key1,AdminConstant.Character.SINGLE_QUOTE,Constant.Character.COMMA);
            i=1;
        }
        if (StringUtil.isNotEmpty(key2)){
            build(values,AdminConstant.Character.SINGLE_QUOTE,key2,AdminConstant.Character.SINGLE_QUOTE,Constant.Character.COMMA);
            i=2;
        }
        if (StringUtil.isNotEmpty(key3)){
            build(values,AdminConstant.Character.SINGLE_QUOTE,key3,AdminConstant.Character.SINGLE_QUOTE,Constant.Character.COMMA);
            i=3;
        }
        if (StringUtil.isNotEmpty(key4)){
            build(values,AdminConstant.Character.SINGLE_QUOTE,key4,AdminConstant.Character.SINGLE_QUOTE,Constant.Character.COMMA);
            i=4;
        }
        if (StringUtil.isNotEmpty(key5)){
            build(values,AdminConstant.Character.SINGLE_QUOTE,key5,AdminConstant.Character.SINGLE_QUOTE,Constant.Character.COMMA);
            i=5;
        }
        if(i!=ids.size()) {
            throw new ServiceException(ResultCode.DATA_MAP_KEYCOUNT_WRONG);
        }
        //去掉最后的逗号
        String valuesStr = values.substring(0, values.length() - 1);
        return valuesStr;
    }

    /**
     * @Description:查询目标表数据时，拼接目标表的查询字段
     * @Author: yuanman
     * @Date: 2019/11/28 11:37
     * @param dataMap
     * @return:java.lang.String
     */
    private String getQueryTargetColumnByTarget(DataMap dataMap) {
        // xx as key1,xx as key2,xx as key3, xx as name,
        StringBuilder columns = new StringBuilder();
        //将datamap里逗号拼接的id转换成list
        List<String> targetIds = Arrays.asList(StringUtil.split(dataMap.getTargetTableKey(), Constant.Character.COMMA));
        //拼接source表的id
        int i = 1;
        for (String targetId : targetIds) {
            //columns.append("t." + targetId + " as key" + i + ",");
            build(columns,AdminConstant.DataMap.TARGET_TABLE_PRE,targetId,AdminConstant.Character.SPACE,AdminConstant.DataMap.KEY+i,Constant.Character.COMMA);
            i++;
        }
        //拼接source表的name,columns.append("t." + dataMap.getTargetTableColumn() + " as name");
        build(columns,AdminConstant.DataMap.TARGET_TABLE_PRE,dataMap.getTargetTableColumn(),AdminConstant.Character.SPACE,AdminConstant.DataMap.NAME);
        if(StringUtil.isNotEmpty(dataMap.getTargetTableCode())){
            build(columns,Constant.Character.COMMA,AdminConstant.DataMap.TARGET_TABLE_PRE,dataMap.getTargetTableCode(),AdminConstant.Character.SPACE,AdminConstant.DataMap.CODE);
        }
        return columns.toString();
    }


    /**
     * @Description:把字符串拼接的映射数据转为List<MapDataVo>
     * @Author: yuanman
     * @Date: 2019/11/28 11:39
     * @param page
     * @return:com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.njwd.entity.admin.vo.MapDataVo>
     */
    private Page<MapDataVo> convertToMapVoList(Page<MapDataVo> page) {
        List<MapDataVo> records = page.getRecords();
        for (MapDataVo vo : records) {
            try{
                List<MapDataVo> targetList = new ArrayList<>();
                String targets = vo.getMaps();
                String[] targetsArr = StringUtil.split(targets, Constant.Character.COMMA);
                for (String target : targetsArr) {
                    String[] targetArr = StringUtil.split(target, AdminConstant.DataMap.BIG_SPILT);
                    String keys = targetArr[0];
                    String name = targetArr[1];
                    String code="";
                    if(target.length()>=3){
                        code=targetArr[2];
                    }
                    MapDataVo targetVo = convertToMapVo(keys,name,code);
                    targetList.add(targetVo);
                }
                vo.setMaps("");
                vo.setMapData(targetList);
            }catch (Exception e){
                continue;
            }

        }
        return page;
    }

    /**
     * @Description:将字符串转换为DataMapVo
     * @Author: yuanman
     * @Date: 2019/11/28 11:40
     * @param keys
     * @param name
     * @return:com.njwd.entity.admin.vo.MapDataVo
     */
    private MapDataVo convertToMapVo(String keys, String name,String code) {
        MapDataVo vo = new MapDataVo();
        String[] keysArr = StringUtil.split(keys, AdminConstant.DataMap.SMALL_SPILT);
        int i = 1;
        for (String key : keysArr) {
            if (i == 1){
                vo.setKey1(key);
            }
            if (i == 2){
                vo.setKey2(key);
            }
            if (i == 3) {
                vo.setKey3(key);
            }
            if (i == 4){
                vo.setKey4(key);
            }
            if (i == 5){
                vo.setKey5(key);
            }
            i++;
        }
        vo.setName(name);
        vo.setCode(code);
        return vo;
    }

    /**
     * @Description:拼接查询原表数据时用到的sql
     * @Author: yuanman
     * @Date: 2019/11/28 11:42
     * @param dataMap
     * @param dataMapKeyDto
     * @return:com.njwd.entity.admin.DataMapSql
     */
    private DataMapSql createQuerySourceSql(DataMap dataMap, DataMapKeyDto dataMapKeyDto) {
        //定义类型
        String dataType = dataMapKeyDto.getType();

        List<String> sourceIds = Arrays.asList(StringUtil.split(dataMap.getSourceTableKey(), Constant.Character.COMMA));
        //定义查询的sql
        DataMapSql dataMapSql = new DataMapSql();
        //设置企业id
        dataMapSql.setEnteId(dataMap.getEnteId());
        //设置源表
        dataMapSql.setSourceTable(dataMap.getSourceTable());
        //设置关系表
        dataMapSql.setMapTable(dataMap.getDatamapTable());
        //设置目标表
        dataMapSql.setTargetTable(dataMap.getTargetTable());
        //设置需查询的源表字段
        dataMapSql.setSourceColumns(getPrimaryColumn(sourceIds,dataMap.getSourceTableColumn(),dataMap.getSourceTableCode(),AdminConstant.DataMap.SOURCE));
        //设置需查询的目标表字段
        dataMapSql.setTargetColumns(getQueryTargetColumnBysource(dataMap));
        //设置源表与关系表的关联关系
        dataMapSql.setSMCondition(getSourceMapCondition(dataMap));
        //设置目标表与关系表的关联关系
        dataMapSql.setTMCondition(getTargetMapCondition(dataMap));
        //设置源表的过滤条件，已匹配的，未匹配的，全部
        StringBuilder mapCondition=new StringBuilder();
        if (dataType.equals(AdminConstant.DataMap.MAPED)) {
            build(mapCondition,AdminConstant.DataMap.MAP_TABLE_PRE,AdminConstant.DataMap.SOURCE_PRE,sourceIds.get(0),AdminConstant.Character.IS_NOT_NULL);
        } else if (dataType.equals(AdminConstant.DataMap.UNMAPED)) {
            build(mapCondition,AdminConstant.DataMap.MAP_TABLE_PRE,AdminConstant.DataMap.SOURCE_PRE,sourceIds.get(0),AdminConstant.Character.IS_NULL);
        } else {
            mapCondition.append(AdminConstant.Character.ONE_EQUAL_ONE);
        }
        //设置名称模糊匹配
        StringBuilder nameCondition = new StringBuilder();
        if (StringUtil.isNotEmpty(dataMapKeyDto.getName())) {
            build(nameCondition,AdminConstant.Character.AND,AdminConstant.DataMap.SOURCE_TABLE_PRE,dataMap.getSourceTableColumn(),
                    AdminConstant.Character.LIKE,AdminConstant.Character.SINGLE_QUOTE,Constant.Character.Percent,dataMapKeyDto.getName(),
                    Constant.Character.Percent,AdminConstant.Character.SINGLE_QUOTE);
        }
        mapCondition.append(nameCondition);
        dataMapSql.setPrimrayCondition(mapCondition.toString());
        //设置groupBy s.`source_key_b`,s.`source_key_b`,s.`source_key_c`
        StringBuilder groupBy= new StringBuilder();
        int i = 1;
        for (String sourceId : sourceIds) {
            if (i == 1) {
                build(groupBy,AdminConstant.DataMap.SOURCE_TABLE_PRE,sourceId);
            } else {
                build(groupBy,Constant.Character.COMMA,AdminConstant.DataMap.SOURCE_TABLE_PRE,sourceId);
            }
            i++;
        }
        dataMapSql.setGroupBy(groupBy.toString());
        return dataMapSql;
    }


    /**
     * @Description:拼接colums
     * @Author: yuanman
     * @Date: 2019/11/29 16:12
     * @param ids
     * @param name
     * @param type
     * @return:java.lang.String
     */
    private String getPrimaryColumn(List<String> ids,String name,String code,String type) {
        // xx as key1,xx as key2,xx as key3, xx as name,
        String pre="";
        if(type.equals(AdminConstant.DataMap.SOURCE)){
            pre=AdminConstant.DataMap.SOURCE_TABLE_PRE;
        }else if(type.equals(AdminConstant.DataMap.TARGET)){
            pre=AdminConstant.DataMap.TARGET_TABLE_PRE;
        }
        StringBuilder columns=new StringBuilder();
        //拼接source表的id
        int i = 1;
        for (String id : ids) {
            // xx as keyi, columns.append(pre + id + " as key" + i + ",");
            build(columns,pre,id,AdminConstant.DataMap.KEY,i+"",Constant.Character.COMMA);
            i++;
        }
        //拼接source表的name,columns.append(pre + name + " as name");
        build(columns,pre,name,AdminConstant.DataMap.NAME);
        if(StringUtil.isNotEmpty(code)){
            build(columns,Constant.Character.COMMA,code,AdminConstant.DataMap.CODE);
        }
        return columns.toString();
    }

    /**
     * @Description:查询源表数据时，拼接目标表的字段
     * @Author: yuanman
     * @Date: 2019/11/28 11:44
     * @param dataMap
     * @return:java.lang.String
     */
    private String getQueryTargetColumnBysource(DataMap dataMap) {
        //t.target_key_a,'_',t.target_key_b,'-',t.target_name,'-',t.target_code
        StringBuilder columns=new StringBuilder();
        //将datamap里逗号拼接的id转换成list
        List<String> targetIds = Arrays.asList(StringUtil.split(dataMap.getTargetTableKey(), Constant.Character.COMMA));
        //拼接source表的id
        int i=1;
        for (String targretId : targetIds) {
            if(i==1){
                build(columns,AdminConstant.DataMap.TARGET_TABLE_PRE,targretId);
            }else{
                build(columns,Constant.Character.COMMA,AdminConstant.Character.SINGLE_QUOTE, AdminConstant.DataMap.SMALL_SPILT,
                        AdminConstant.Character.SINGLE_QUOTE,Constant.Character.COMMA,AdminConstant.DataMap.TARGET_TABLE_PRE,targretId);
            }
            i++;
        }
        //拼接name
        build(columns,Constant.Character.COMMA,AdminConstant.Character.SINGLE_QUOTE, AdminConstant.DataMap.BIG_SPILT,
                AdminConstant.Character.SINGLE_QUOTE,Constant.Character.COMMA,AdminConstant.DataMap.TARGET_TABLE_PRE,dataMap.getTargetTableColumn());
        //code
        build(columns,Constant.Character.COMMA,AdminConstant.Character.SINGLE_QUOTE, AdminConstant.DataMap.BIG_SPILT,
                AdminConstant.Character.SINGLE_QUOTE,Constant.Character.COMMA,AdminConstant.DataMap.TARGET_TABLE_PRE,dataMap.getTargetTableCode());
        return columns.toString();
    }

    /**
     * @Description:拼接源表与映射表的关联条件
     * @Author: yuanman
     * @Date: 2019/11/28 11:45
     * @param dataMap
     * @return:java.lang.String
     */
    private String getSourceMapCondition(DataMap dataMap) {
        //s.`source_key_a`=m.`source_source_key_a` AND s.`source_key_b`=m.`source_source_key_b` AND s.`source_key_c`=m.`source_source_key_c`
        StringBuilder conditions=new StringBuilder();
        //将datamap里逗号拼接的id转换成list
        List<String> sourceIds = Arrays.asList(StringUtil.split(dataMap.getSourceTableKey(), ","));
        int i = 1;
        for (String sourceId : sourceIds) {
            if (i == 1) {
                build(conditions,AdminConstant.DataMap.SOURCE_TABLE_PRE,sourceId,AdminConstant.Character.EQUALS_SPACE,AdminConstant.DataMap.MAP_TABLE_PRE,
                        AdminConstant.DataMap.SOURCE_PRE,sourceId);
            } else {
                build(conditions,AdminConstant.Character.AND,AdminConstant.DataMap.SOURCE_TABLE_PRE,sourceId,AdminConstant.Character.EQUALS_SPACE,AdminConstant.DataMap.MAP_TABLE_PRE,
                        AdminConstant.DataMap.SOURCE_PRE,sourceId);
            }
            i++;
        }
        return conditions.toString();
    }

    /**
     * @Description:拼接目标表与映射表的关联条件
     * @Author: yuanman
     * @Date: 2019/11/28 11:46
     * @param dataMap
     * @return:java.lang.String
     */
    private String getTargetMapCondition(DataMap dataMap) {
        //m.`target_target_key_a`=t.`target_key_a` AND m.`target_target_key_b`=t.`target_key_b`
        StringBuilder conditions=new StringBuilder();
        //将datamap里逗号拼接的id转换成list
        List<String> ids = Arrays.asList(StringUtil.split(dataMap.getTargetTableKey(), Constant.Character.COMMA));
        int i = 1;
        for (String id : ids) {
            if (i == 1) {
                build(conditions,AdminConstant.DataMap.TARGET_TABLE_PRE,id,AdminConstant.Character.EQUALS_SPACE,AdminConstant.DataMap.MAP_TABLE_PRE,
                        AdminConstant.DataMap.TARGET_PRE,id);
            } else {
                build(conditions,AdminConstant.Character.AND,AdminConstant.DataMap.TARGET_TABLE_PRE,id,AdminConstant.Character.EQUALS_SPACE,AdminConstant.DataMap.MAP_TABLE_PRE,
                        AdminConstant.DataMap.TARGET_PRE,id);
            }
            i++;
        }
        return conditions.toString();
    }

    /**
     * @Description:检查参数对象是否存在sql注入
     * @Author: yuanman
     * @Date: 2019/11/28 11:48
     * @param vo
     * @return:boolean
     */
    private boolean checkSql(MapDataVo vo){
         String sqlStr=vo.getKey1()+vo.getKey2()+vo.getKey3()+vo.getKey4()+vo.getKey5();
         return (StringUtil.isSqlValid(sqlStr));
    };

    /**
     * @Description:检查viewType
     * @Author: yuanman
     * @Date: 2019/12/4 13:39
     * @param type
     * @return:void
     */
    private void checkViewType(String type){
        if(!type.equals(AdminConstant.DataMap.SOURCE)&&!type.equals(AdminConstant.DataMap.TARGET)){
            throw new ServiceException(ResultCode.PARAMS_NOT_RIGHT);
        }
    }

    /**
     * @Description:拼接字符串
     * @Author: yuanman
     * @Date: 2019/12/4 13:41
     * @param sb
     * @param strs
     * @return:java.lang.StringBuilder
     */
    private static StringBuilder build(StringBuilder sb,String... strs){
        for(String str:strs){
            sb.append(str);
        }
        return sb;
    }
}


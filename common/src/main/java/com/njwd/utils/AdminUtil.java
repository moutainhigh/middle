package com.njwd.utils;

import com.njwd.common.AdminConstant;

/**
 * @program: middle-data
 * @description: 后台管理工具类
 * @author: Chenfulian
 * @create: 2019-11-20 11:17
 **/
public class AdminUtil {
    /**
     * 根据数据类型返回rela表名
     * @param dataType
     * @return
     */
    public static String getRelaTableByDataType(String dataType){
        return AdminConstant.Db.BASE_PREFIX + dataType + AdminConstant.Db.RELA_SUFFIX;
    }

    /**
     * 根据base表名返回rela表名
     * @param baseTable
     * @return
     */
    public static String getRelaTableByBaseTable(String baseTable){
        return baseTable + AdminConstant.Db.RELA_SUFFIX;
    }

    /**
     * 根据数据类型返回base表名
     * @param dataType
     * @return
     */
    public static String getBaseTableByDataType(String dataType){
        return AdminConstant.Db.BASE_PREFIX + dataType;
    }

    /**
     * 根据数据类型返回中台id列名
     * @param dataType
     * @return
     */
    public static String getBaseIdByDataType(String dataType){
        return dataType + AdminConstant.Db.ID_SUFFIX;
    }

    /**
     * 根据数据类型返回第三方id列名
     * @param dataType
     * @return
     */
    public static String getThirdIdByDataType(String dataType){
        return AdminConstant.Db.THIRD_PREFIX + dataType + AdminConstant.Db.ID_SUFFIX;
    }

    /**
     * 根据中台id返回第三方id列名
     * @param baseId
     * @return
     */
    public static String getThirdIdByBaseId(String baseId){
        return AdminConstant.Db.THIRD_PREFIX + baseId;
    }
}

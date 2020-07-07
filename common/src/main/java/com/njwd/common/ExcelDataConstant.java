package com.njwd.common;

/**
 * @description: excel数据转换,
 * @author: xdy
 * @create: 2019/7/2 9:27
 */
public interface ExcelDataConstant {

    //=========================业务数据转换=======================
    /**
     * 门店营业状态 0:关停 1：营业
     */
    String SHOP_STATUS = "system_data_shop_status";

    /**
     * 百分号标识
     */
    String PERCENT_FLAG = "%";

    /**
     * 以双横线显示
     */
    String NULL_AND_ZERO_LINE = "--";

    /**
     * 转换类型
     */
    interface ConvertType{

        String PERCENT_SIGN = "percent_sign";

        String INTEGER_STRING = "integer";
    }


}

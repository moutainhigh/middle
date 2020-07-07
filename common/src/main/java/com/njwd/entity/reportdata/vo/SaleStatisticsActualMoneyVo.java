package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;
/** 
* @Description: 销售净收入 
* @Param:  
* @return:  
* @Author: LuoY
* @Date: 2019/12/12 14:12
*/ 
@Data
public class SaleStatisticsActualMoneyVo {
    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 收入金额
     */
    private BigDecimal incomeAmount;

    /**
     * 数值类型
     */
    private int dataType;

    /**
     * 原金额
     */
    private BigDecimal amount;
}

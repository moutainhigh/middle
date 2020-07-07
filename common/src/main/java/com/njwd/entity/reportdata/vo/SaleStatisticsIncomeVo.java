package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 收入分析
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
public class SaleStatisticsIncomeVo {
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

    /**
     * 销售净收入
     */
    private List<SaleStatisticsActualMoneyVo> saleStatisticsActualMoneyVos;
}

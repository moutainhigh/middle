package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 赠送分析
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
public class SaleStatisticsGiveVo {
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
}

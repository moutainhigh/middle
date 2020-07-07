package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 消费分析
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
public class SaleStatisticsConsumptionVo {
    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 消费金额
     */
    private BigDecimal ConsumptionAmount;

    /**
     * 数值类型
     */
    private int dataType;

    /**
     * 上期客流量
     */
    private BigDecimal upClient;

    /**
     * 本期客流量
     */
    private BigDecimal currentClient;

    /**
     * 上期总桌数
     */
    private BigDecimal upDeskCount;

    /**
     * 上期收入总额
     */
    private BigDecimal upIncomeMoney;
}

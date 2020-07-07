package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 会员卡充值消费统计
 * @Author: ZhuHC
 * @Date: 2020/2/14 16:35
 */
@Data
public class CardPrepaidConsumeStatisticalVo {

    /**
     * 办卡门店id
     */
    private String shopId;
    /**
     * 充值总金额(充值实收+充值返现)
     */
    private BigDecimal totalPrepaidMoney;
    /**
     * 充值实收金额
     */
    private BigDecimal prepaidMoney;
    /**
     * 使用的储值金额
     */
    private BigDecimal consumeMoney;
    /**
     * 使用的存值实收金额
     */
    private BigDecimal consumePrepaidMoney;

}

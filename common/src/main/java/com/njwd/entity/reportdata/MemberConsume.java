package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 会员消费
 * @Author ljc
 * @Date 2019/11/27
 */
@Data
public class MemberConsume {
    /**
     * 门店id
     */
    private String shopId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 区域id
     */
    private String regionId;
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 品牌id
     */
    private String brandId;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 使用储值金额-会员储值适用
     */
    private BigDecimal consumeMoney;
    /**
     * 使用实收预存额(元)
     */
    private BigDecimal consumePrepaidMoney;
    /**
     * 使用奖励预存额(元)
     */
    private BigDecimal consumeLargessMoney;
    /**
     * 会员消费金额
     */
    private BigDecimal memberConsumeMoney;
    /**
     * 营业收入
     */
    private BigDecimal actualMoney;
    /**
     * 本月储值实收余额
     */
    private BigDecimal currentPrepaidMoney;
    /**
     * 上月储值实收余额
     */
    private BigDecimal previousPrepaidMoney;
    /**
     * 本月充值金额
     */
    private BigDecimal currTotalPrepaidMoney;
    /**
     * 储值消费占比
     */
    private BigDecimal perPrepaidConsume;
    /**
     * 会员消费占比
     */
    private BigDecimal perMemberConsume;
    /**
     * 储值沉淀率
     */
    private BigDecimal ratePrepaid;
}

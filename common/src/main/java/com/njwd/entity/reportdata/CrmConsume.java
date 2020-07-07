package com.njwd.entity.reportdata;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class CrmConsume {
    /**
    * 消费ID
    */
    private String consumeId;

    /**
    * 消费门店id
    */
    private String shopId;

    /**
    * 集团id
    */
    private String enteId;

    /**
    * 应用id
    */
    private String appId;

    /**
    * 会员ID
    */
    private String memberId;

    /**
    * 会员卡ID
    */
    private String cardId;

    /**
    * 会员卡卡号
    */
    private String cardNo;

    /**
    * 消费总金额
    */
    private BigDecimal totalMoney;

    /**
    * 实收金额
    */
    private BigDecimal actualMoney;

    /**
    * 储值支付金额
    */
    private BigDecimal consumeMoney;

    /**
    * 消费金额 实收
    */
    private BigDecimal consumePrepaidMoney;

    /**
    * 返现金额
    */
    private BigDecimal consumeLargessMoney;

    /**
    * 当前消费的活动返现金额
    */
    private BigDecimal consumeMarketLargessMoney;

    /**
    * 使用代金券抵扣金额
    */
    private BigDecimal couponMoney;

    /**
    * 使用礼品券抵扣金额
    */
    private BigDecimal couponGiftMoney;

    /**
    * 使用积分数量
    */
    private Integer useIntegral;

    /**
    * 使用积分抵扣金额
    */
    private BigDecimal integralMoney;

    /**
    * 奖励积分数量
    */
    private Integer integralGain;

    /**
    * 消费时间
    */
    private Date consumeTime;

    /**
    * 消费门店名称
    */
    private String shopName;

    /**
    * 订单编号 
    */
    private String orderId;

    /**
    * 账单编号
    */
    private String cashId;

    /**
    * 消费后总余额
    */
    private BigDecimal moneyAfter;

    /**
    * 消费后充值余额
    */
    private BigDecimal prepaidMoneyAfter;

    /**
    * 消费后返现余额
    */
    private BigDecimal largessMoneyAfter;

    /**
    * 消费后活动返现金额
    */
    private BigDecimal marketLargessMoneyAfter;

    /**
    * 消费赠送返现金额
    */
    private BigDecimal largessMoney;

    /**
    * 消费赠送活动返现金额
    */
    private BigDecimal marketLargessMoney;

    /**
    * 支付方式id
    */
    private String payTypeId;

    /**
    * 交易类型id
    */
    private String transactionTypeId;

    /**
    * 交易备注
    */
    private String remark;
}
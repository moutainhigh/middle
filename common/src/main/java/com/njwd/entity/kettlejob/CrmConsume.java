package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @Description 会员消费记录
 * @create 2019/11/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmConsume extends BaseModel {
    /**
     * 消费ID
     */
    private String consumeId;

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
    private Double totalMoney;

    /**
     * 实收金额
     */
    private Double actualMoney;

    /**
     * 消费的储值金额
     */
    private Double consumeMoney;

    /**
     * 消费的充值金额（实收）
     */
    private Double consumePrepaidMoney;

    /**
     * 消费的返现金额
     */
    private Double consumeLargessMoney;

    /**
     * 消费的活动返现金额
     */
    private Double consumeMarketLargessMoney;

    /**
     * 使用代金券抵扣金额
     */
    private Double couponMoney;

    /**
     * 使用礼品券抵扣金额
     */
    private Double couponGiftMoney;

    /**
     * 使用积分数量
     */
    private Integer useIntegral;

    /**
     * 使用积分抵扣金额
     */
    private Double integralMoney;

    /**
     * 奖励积分数量
     */
    private Integer integralGain;

    /**
     * 消费时间
     */
    private String consumeTime;

    /**
     * 消费门店id
     */
    private String shopId;

    /**
     * 第三方消费门店id
     */
    private String thirdShopId;
    /**
     * 应用id
     */
    private String appId;

    /**
     * 集团id
     */
    private String enteId;

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
    private Double moneyAfter;

    /**
     * 消费后充值余额
     */
    private Double prepaidMoneyAfter;

    /**
     * 消费后返现余额
     */
    private Double largessMoneyAfter;

    /**
     * 消费活动返现余额
     */
    private Double marketLargessMoneyAfter;

    /**
     * 消费赠送返现金额
     */
    private Double largessMoney;

    /**
     * 消费赠送活动返现金额
     */
    private Double marketLargessMoney;

    /**
     * 支付类型id
     */
    private String payTypeId;
    /**
     * 支付类型
     */
    private String thirdPayTypeId;
    /**
     * 交易类型id
     */
    private String transactionTypeId;
    /**
     * 交易类型
     */
    private String thirdTransactionTypeId;
    /**
     * 备注
     */
    private String remark;


}

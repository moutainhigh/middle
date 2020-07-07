package com.njwd.entity.reportdata;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CrmCardType {
    /**
    * 会员卡等级ID
    */
    private String cardGradeId;

    /**
    * 企业ID
    */
    private String enteId;

    /**
    * 应用id
    */
    private String appId;

    /**
    * 会员卡等级名称
    */
    private String cardGradeName;

    /**
    * 每次充值或者开卡的金额
    */
    private BigDecimal onceMoney;

    /**
    * 卡费
    */
    private BigDecimal cardFee;

    private BigDecimal largessMoney;

    /**
    * 是否打折
    */
    private Integer isDiscount;

    /**
    * 折扣 1-100(百分比,90表示打9折)
    */
    private Integer discount;

    private String shopId;

    /**
    * 实体卡验证方式0:无验证1:密码2：动态码
    */
    private String swipeConfirm;

    /**
    * 虚拟卡验证方式0：无验证1：密码2:动态码
    */
    private String enterConfirm;

    /**
    * 提成方式：-1：无提成;0：固定提成;1：按销售金额提成
    */
    private Integer commissionType;

    /**
    * 固定提成金额设置
    */
    private BigDecimal commissionMoney;

    /**
    * 按销售金额比例提成
    */
    private BigDecimal commissionPercent;

    /**
    * 是否计算提成 0:否;1:是
    */
    private Integer isCommission;

    /**
    * 返现金额每日最多消费次数
    */
    private Integer consumeLargessLimitNum;

    /**
    * 单次最高消费返现金额
    */
    private BigDecimal consumeLargessMaxMoney;

    /**
    * 单次最高消费使用返现比例
    */
    private BigDecimal consumeLargessMoneyPercent;

    /**
    * 充值返现到账天数
    */
    private Integer consumeLargessEffectiveDate;

    /**
    * 充值返现是否立即到账
    */
    private Integer consumeLargessIsImmediately;

    /**
    * 每日最多消费活动返现次数
    */
    private Integer marketLargessLimitNum;

    /**
    * 单次最高消费使用活动返现金额
    */
    private BigDecimal marketLargessMaxMoney;

    /**
    * 单次最高消费使用活动返现比例
    */
    private BigDecimal marketLargessMoneyPercent;

    /**
    * 活动返现到账天数
    */
    private Integer marketLargessEffectiveDate;

    /**
    * 活动返现是否立即到账
    */
    private Integer marketLargessIsImmediately;

    /**
    * 该类别是否可以转账 0不可以1可以
    */
    private Integer isExchangeMoney;

    /**
    * 消费满x元可使用返现金额
    */
    private BigDecimal consumeReachMoney;

    /**
    * 消费满x元金额可使用活动返现
    */
    private BigDecimal marketReachMoney;
}
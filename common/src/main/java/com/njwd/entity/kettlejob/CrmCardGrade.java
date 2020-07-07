package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @Description 会员等级
 * @create 2019/11/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmCardGrade extends BaseModel {
    /**
     * 会员等级id
     */
    private String cardGradeId;
    /**
     * 会员等级名称
     */
    private String cardGradeName;
    /**
     * 集团id
     */
    private String enteId;
    /**
     * 应用id
     */
    private String appId;

    /**
     * 办卡充值金额
     */
    private Double onceMoney;

    /**
     * 卡费
     */
    private Double cardFee;

    /**
     * 返现金额
     */
    private Double largessMoney;

    /**
     * 是否打折
     */
    private Integer isDiscount;

    /**
     * 折扣 1-100(百分比 90表示打9折)
     */
    private Integer discount;

    /**
     * 实体卡验证方式0:无验证1:密码2：动态码
     */
    private Integer swipeConfirm;

    /**
     * 虚拟卡验证方式0：无验证1：密码2:动态码
     */
    private Integer enterConfirm;

    /**
     * 提成方式：-1：无提成;0：固定提成;1：按销售金额提成
     */
    private Integer commissionType;

    /**
     * 提成金额
     */
    private Double commissionMoney;

    /**
     * 提成比例
     */
    private Integer commissionPercent;

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
    private Double consumeLargessMaxMoney;

    /**
     * 单次最高消费使用返现比例
     */
    private Integer consumeLargessMoneyPercent;

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
    private Double marketLargessMaxMoney;

    /**
     * 单次最高消费使用活动返现比例
     */
    private Integer marketLargessMoneyPercent;

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
    private Double consumeReachMoney;

    /**
     * 消费满x元金额可使用活动返现
     */
    private Double marketReachMoney;

}

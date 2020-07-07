package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @Description 会员储值记录
 * @create 2019/11/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmPrepaid extends BaseModel {
    /**
     * 充值记录id
     */
    private  String prepaidId;
    /**
     * 会员id
     */
    private String memberId;
    /**
     * 会员卡id
     */
    private String cardId;
    /**
     * 会员卡号
     */
    private String cardNo;
    /**
     * 充值金额
     */
    private Double money;
    /**
     * 充值实际金额
     */
    private Double prepaidMoney;
    /**
     * 充值返现金额
     */
    private Double largessMoney;

    /**
     * 活动返现金额
     */
    private Double marketLargessMoney;
    /**
     * 充值后余额
     */
    private Double moneyAfter;
    /**
     *  充值后余额
     */
    private Double prepaidMoneyAfter;
    /**
     * 充值后返现余额
     */
    private Double largessMoneyAfter;
    /**
     * 充值后活动返现余额
     */
    private Double marketLargessMoneyAfter;
    /**
     * 组合支付的各金额保存
     */
    private String payInfo;
    /**
     * 第三方支付类型
     */
    private String thirdPayTypeId;
    /**
     * 交易类型id
     */
    private String transactionTypeId;
    /**
     * 第三方交易类型
     */
    private String thirdTransactionTypeId;
    /**
     *  门店id
     */
    private String shopId;
    /**
     * 第三方门店id
     */
    private String thirdShopId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 充值时间
     */
    private String prepaidTime;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 集团id
     */
    private String enteId;
    /**
     * 是否开票 0：未开票 1：已开票
     */
    private Integer isInvoice;
    /**
     * 备注
     */
    private String remark;
    /**
     * 第三方操作人id
     */
    private String thirdCreatorId;
}
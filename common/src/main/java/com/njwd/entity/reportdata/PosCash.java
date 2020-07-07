package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Desciption: PosCash类
 * @author: LuoY
 * @Date:2020/2/15 11:48
 */
@Data
public class PosCash {
    /**
     * 账单id
     */
    private String cashId;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 入账日期(yyyymmdd)
     */
    private Date accountDate;

    /**
     * 结账时间
     */
    private Date cashTime;

    /**
     * 结账人
     */
    private String cashUserId;

    /**
     * 会员号
     */
    private String memberNo;

    /**
     * 会员手机号
     */
    private String memberPhone;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员等级
     */
    private String memberType;

    /**
     * 消费金额
     */
    private BigDecimal moneyConsume;

    /**
     * 应收
     */
    private BigDecimal moneyReceivable;

    /**
     * 服务费
     */
    private BigDecimal moneyService;

    /**
     * 茶位费
     */
    private BigDecimal moneyTea;

    /**
     * 配送费
     */
    private BigDecimal moneyDelivery;

    /**
     * 打包费
     */
    private BigDecimal moneyBox;

    /**
     * 实收金额
     */
    private BigDecimal moneyActual;

    /**
     * 平台服务费，例如外卖
     */
    private BigDecimal commissionTakeout;

    /**
     * 抹零金额
     */
    private BigDecimal moneyChange;

    /**
     * 账单状态 0未结账     1结账
     */
    private Integer status;

    /**
     * 折扣金额
     */
    private BigDecimal moneyDiscount;

    /**
     * 赠送金额
     */
    private BigDecimal moneyFreeamount;

    /**
     * 优惠金额
     */
    private BigDecimal moneyFavorable;

    /**
     * 单品折扣金额
     */
    private BigDecimal moneyDanDiscount;

    /**
     * 会员优惠
     */
    private BigDecimal moneyMemberFavorable;
}


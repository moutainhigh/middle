package com.njwd.entity.reportdata.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author jds
 * @Description 收入折扣分析表
 * @create 2019/12/7 14:27
 */
@Data
@ToString(callSuper = true)
public class PosDiscountDetailPayVo {
    /**
     * 抹零
     */
    private BigDecimal moneyChange;
    /**
     * 折扣
     */
    private BigDecimal moneyDiscount;
    /**
     * 赠送
     */
    private BigDecimal moneyFreeAmount;
    /**
     * 单品折扣
     */
    private BigDecimal moneyDanDiscount;
    /**
     * 会员优惠
     */
    private BigDecimal moneyMemberFavorable;
    /**
     * 其他
     */
    private BigDecimal moneyFavorable;

    /**
     * 序号 导出用
     */
    private String num;

    /**
     * 合计赠送金额
     */
    private BigDecimal totalMoney;
    /**
     * 支付方式名称
     */
    private String payTypeName;
    /**
     * 支付方式编码
     */
    private String payTypeCode;
    /**
     * 本期
     */
    private BigDecimal currentMoney;

    /**
     * 上期
     */
    private BigDecimal prior;

    /**
     * 去年同期
     */
    private BigDecimal lastYear;

    /**
     * 占比
     */
    private BigDecimal proportion;


    /**
     * 环比
     */
    private BigDecimal linkRatio;

    /**
     * 同比
     */
    private BigDecimal overYear;

    /**
     * 门店Id
     */
    private String shopId;
}

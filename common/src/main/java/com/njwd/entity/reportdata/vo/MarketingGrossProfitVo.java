package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author lj
 * @Description 营销活动毛利统计表
 * @Date:16:42 2020/3/26
 **/
@Data
public class MarketingGrossProfitVo {
    /**
     * 类型
     */
    private String type;
    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 支付类型id
     */
    private String payTypeId;

    /**
     * 支付方式名称
     */
    private String payTypeName;

    /**
     * 订单量
     */
    private BigDecimal orderCount;

    /**
     * 本期收入
     */
    private BigDecimal moneyActual;

    /**
     * 菜品编号
     */
    private String foodNo;

    /**
     * 菜品名称
     */
    private String foodName;

    /**
     * 菜品销量
     */
    private BigDecimal foodNum;

    /**
     * 本期成本
     */
    private BigDecimal moneyCost;

    /**
     * 本期毛利
     */
    private BigDecimal grossProfit;

    /**
     * 上一期收入
     */
    private BigDecimal monthMoneyActual;

    /**
     * 上一期成本
     */
    private BigDecimal monthMoneyCost;

    /**
     * 上一期毛利
     */
    private BigDecimal monthGrossProfit;

    /**
     * 去年同期收入
     */
    private BigDecimal yearMoneyActual;

    /**
     * 去年同期成本
     */
    private BigDecimal yearMoneyCost;

    /**
     * 去年同期毛利
     */
    private BigDecimal yearGrossProfit;

    /**
     * 收入同比
     */
    private BigDecimal yearMoneyActualRatio;

    /**
     * 收入环比
     */
    private BigDecimal monthMoneyActualRatio;

    /**
     * 成本同比
     */
    private BigDecimal yearMoneyCostRatio;

    /**
     * 成本环比
     */
    private BigDecimal monthMoneyCostRatio;

    /**
     * 毛利同比
     */
    private BigDecimal yearGrossProfitRatio;

    /**
     * 毛利环比
     */
    private BigDecimal monthGrossProfitRatio;
}

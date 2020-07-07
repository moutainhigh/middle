package com.njwd.entity.reportdata.vo;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.math.BigDecimal;

/**
 * @Description: 毛利分析vo
 * @Author: 周鹏
 * @Date: 2020/03/02
 */
@Data
public class GrossProfitVo {
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
     * 客流量-午市
     */
    private Integer afternoonMarket;

    /**
     * 客流量-晚市
     */
    private Integer eveningMarket;

    /**
     * 客流量-夜宵
     */
    private Integer midnightSnack;

    /**
     * 客流量-合计
     */
    private Integer clientCountTotal;

    /**
     * 开台数
     */
    private Integer deskCountTotal;

    /**
     * 综合-收入
     */
    private BigDecimal incomeTotal;

    /**
     * 综合-成本
     */
    private BigDecimal costTotal;

    /**
     * 综合-毛利
     */
    private BigDecimal grossProfitTotal;

    /**
     * 综合-毛利率
     */
    private BigDecimal grossProfitPercentTotal;

    /**
     * 综合-单客毛利率
     */
    private BigDecimal personGrossProfitPercentTotal;

    /**
     * 综合-单台毛利率
     */
    private BigDecimal deskGrossProfitPercentTotal;

    /**
     * 菜肴-收入
     */
    private BigDecimal incomeDish;

    /**
     * 菜肴-实收合计
     */
    private BigDecimal receipts;

    /**
     * 菜肴-酒水饮料类、附加、其他收入
     */
    private BigDecimal incomeDishExcept;

    /**
     * 菜肴-成本
     */
    private BigDecimal costDish;

    /**
     * 菜肴-毛利
     */
    private BigDecimal grossProfitDish;

    /**
     * 菜肴-毛利率
     */
    private BigDecimal grossProfitPercentDish;

    /**
     * 菜肴-减赠送水果、小菜毛利
     */
    private BigDecimal grossProfitExceptDish;

    /**
     * 酒水-收入
     */
    private BigDecimal incomeWine;

    /**
     * 酒水-成本
     */
    private BigDecimal costWine;

    /**
     * 酒水-毛利
     */
    private BigDecimal grossProfitWine;

    /**
     * 酒水-毛利率
     */
    private BigDecimal grossProfitPercentWine;

    /**
     * 自选调料-销量
     */
    private BigDecimal saleCondiment;

    /**
     * 自选调料-收入
     */
    private BigDecimal incomeCondiment;

    /**
     * 自选调料-成本
     */
    private BigDecimal costCondiment;

    /**
     * 自选调料-毛利
     */
    private BigDecimal grossProfitCondiment;

    /**
     * 自选调料-毛利率
     */
    private BigDecimal grossProfitPercentCondiment;

    /**
     * 赠送菜品
     */
    private BigDecimal dishGive;

    /**
     * 赠送水果
     */
    private BigDecimal fruitGive;

    /**
     * 菜品库存金额
     */
    private BigDecimal dishStockAmount;

    /**
     * 平均库存
     */
    private BigDecimal stockAverage;

    /**
     * 菜品库存周转天数
     */
    private BigDecimal dishStockTurnoverDays;

    /**
     * 类型(shop 门店 brand 品牌 region区域)
     */
    private String type;
}

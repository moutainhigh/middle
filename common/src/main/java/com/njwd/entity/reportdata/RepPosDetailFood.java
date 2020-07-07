package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RepPosDetailFood {
    /**
     * 企业id
     */
    private String enteId;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 门店编码
     */
    private String shopNo;

    /**
     * 门店类型
     */
    private String shopTypeId;

    /**
     * 门店名称
     */
    private String shopName;

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
     * 菜品id
     */
    private String foodId;

    /**
     * 菜品编码
     */
    private String foodNo;

    /**
     * 菜品名称
     */
    private String foodName;

    /**
     * 分类id
     */
    private String foodStyleId;

    /**
     * 分类名称
     */
    private String foodStyleName;

    /**
     * 单位id
     */
    private String unitId;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 数量
     */
    private BigDecimal foodNum;

    /**
     * 单价
     */
    private BigDecimal originalPrice;

    /**
     * 退单数量
     */
    private BigDecimal retreatCount;

    /**
     * 赠送数量
     */
    private BigDecimal griveCount;

    /**
     * 账单日期
     */
    private Date accountDate;
}

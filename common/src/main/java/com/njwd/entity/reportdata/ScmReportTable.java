package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 供应链报表表
 * @author: 周鹏
 * @create: 2020-03-31
 */
@Data
public class ScmReportTable {
    /**
     * 企业id
     */
    private String enteId;

    /**
     * 交易日期
     */
    private String transDay;

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 菜肴成本
     */
    private BigDecimal costDish;

    /**
     * 自选调料成本
     */
    private BigDecimal costCondiment;

    /**
     * 赠送菜品
     */
    private BigDecimal dishGive;

    /**
     * 赠送水果
     */
    private BigDecimal fruitGive;

    /**
     * 酒水成本
     */
    private BigDecimal costWine;

    /**
     * 菜品库存金额
     */
    private BigDecimal dishStockAmount;
}

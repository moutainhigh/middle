package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Author: LIBAO
 * @Date: 2020/3/24 18:00
 */
@Data
public class CompanyVo implements Serializable {

    /**
     * 门店id
     */
    private String shopId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 区域ID
     */
    private String regionId;
    /**
     * 品牌ID
     */
    private String brandId;

    /**
     * 门店税率
     */
    private BigDecimal mdTax;

    /**
     * 优免合计
     */
    private BigDecimal exemptionTotal = new BigDecimal(0.00);

    /**
     * 收入合计
     */
    private BigDecimal saleTotal  = new BigDecimal(0.00);

    /**
     * 营业管理费
     */
    private BigDecimal BusinessManageTotal = new BigDecimal(0.00);

    /**
     * 预算数
     */
    private BigDecimal budgetMoney = new BigDecimal(0.00);

    /**
     * 预算比
     */
    private BigDecimal budgetCompare = new BigDecimal(0.00);

    /**
     * 啤酒进场费
     */
    private BigDecimal beerIntoFactoryFee = new BigDecimal(0.00);

    /**
     * 摊销费用
     */
    private BigDecimal amortMoney = new BigDecimal(0.00);

    /**
     * 摊销名称
     */
    private String amortName;


    /**
     * 物料发出成本
     */
    private BigDecimal materialCost = new BigDecimal(0.00);

    /**
     * 物料类别描述
     */
    private String description;

    /**
     * 奖金
     */
    private BigDecimal bonusMoney = new BigDecimal(0.00);

    /**
     * 金额
     */
    private BigDecimal amount = new BigDecimal(0.00);
}

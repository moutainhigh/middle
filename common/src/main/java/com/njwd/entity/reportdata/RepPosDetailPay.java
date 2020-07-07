package com.njwd.entity.reportdata;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 支付方式明细表
 * @Author LuoY
 * @Date 2019/12/9
 */
@Data
public class RepPosDetailPay implements Serializable {
    private static final long serialVersionUID = -5406147489531747108L;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 大区id
     */
    private String regionId;

    /**
     * 大区名称
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
     * 门店名称
     */
    private String shopTypeId;

    /**
     * 支付类型id
     */
    private String payCategoryId;

    /**
     * 支付类型名称
     */
    private String payCategoryName;

    /**
     * 支付方式id
     */
    private String payTypeId;

    /**
     * 支付方式编码
     */
    private String payTypeCode;

    /**
     * 支付方式名称
     */
    private String payTypeName;

    /**
     * 实收金额
     */
    private BigDecimal moneyActual;

    /**
     * 原金额
     */
    private BigDecimal money;

    /**
     * 账单日期
     */
    private Date accountDate;
}

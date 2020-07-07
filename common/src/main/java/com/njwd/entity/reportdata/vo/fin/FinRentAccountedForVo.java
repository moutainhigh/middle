package com.njwd.entity.reportdata.vo.fin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName FinRentAccountedForVo
 * @Description 租金占销Vo
 * @Author liBao
 * @Date 2020/2/7 12:52
 */
@Getter
@Setter
public class FinRentAccountedForVo implements Serializable {

    //类型 shop 为门店 brand 品牌 region区域
    private String type ;
    //品牌id
    private String brandId;
    //品牌
    private String brandName;
    //区域id
    private String regionId;
    //区域
    private String regionName;
    //门店id
    private String shopId;
    //门店
    private String shopName;
    //门店面积
    private BigDecimal shopArea;
    //租金
    private BigDecimal rent;

    //物业
    private BigDecimal property;
    //合计 = 租金+物业
    private BigDecimal total;
    //销售额
    private BigDecimal salesVolume;

    //营业附加费
    private BigDecimal businessSurcharges;

    //实收
    private BigDecimal realSales;

    //租金占销比
    private BigDecimal rentSalesRatio;

    //物业占销比
    private BigDecimal propertySalesRatio;

    //合计占销比
    private BigDecimal totalSalesRatio;

    /**
     * 预算数
     */
    private BigDecimal BudgetMoney;

    /**
     * 预算比
     */
    private BigDecimal BudgetCompare;

    private BigDecimal moneyActual = new BigDecimal(0.00);

}

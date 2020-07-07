package com.njwd.entity.reportdata.vo.fin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *@description: 筹建投资回报Vo
 *@author: fancl
 *@create: 2020-01-14 
 */
@Getter
@Setter
public class FinPrepaInvestVo implements Serializable {
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
    //营业期
    private BigDecimal businessPeriod;
    //累计利润
    private BigDecimal accumulatedProfit;
    //年均利润
    private BigDecimal avgYearProfit;
    //筹建投入
    private BigDecimal investPreparation;
    //投资回报率
    private BigDecimal retRate;


}

package com.njwd.entity.reportdata.vo.fin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *@description: 筹建成本Vo
 *@author: fancl
 *@create: 2020-01-14 
 */
@Getter
@Setter
public class FinCostCompareVo implements Serializable {
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
    //筹建成本
    private BigDecimal cost;

    //单位筹建成本 = 筹建成本/店面积 即:unitCost = cost/shopArea
    private BigDecimal unitCost;
    //区域平均成本
    private BigDecimal avgRegionCost;
    //差异率
    private BigDecimal diffRate;
}

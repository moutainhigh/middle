package com.njwd.entity.reportdata.vo.scm;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *@description: 菜品毛利返回Vo
 *@author: fancl
 *@create: 2020-03-27 
 */
@Getter
@Setter
public class DishGrossProfitVo extends GrossWeightVo{
    //门店信息
    String shopId;
    String shopName;
    //菜品大类名称
    String foodStyleName;
    //单位名
    String unitName;
    //销售单价
    BigDecimal salePrice;
    //菜品销售数量
    BigDecimal saleNum;
    //乘以销售数量得到的金额
    BigDecimal costAmount;
    //成本单价
    BigDecimal costPrice;
    //门店成本
    BigDecimal shopCost;
    //门店毛利
    BigDecimal shopProfit;
    //门店毛利率
    BigDecimal shopPercent;
    //央厨成本
    BigDecimal centerCost;
    //央厨毛利
    BigDecimal centerProfit;
    //央厨毛利率
    BigDecimal centerPercent;
    //差异成本
    BigDecimal differentCost;
    //差异毛利
    BigDecimal differentProfit;
    //差异毛利率
    BigDecimal differentPercent;


}

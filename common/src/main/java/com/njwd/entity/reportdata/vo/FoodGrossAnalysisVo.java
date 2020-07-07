package com.njwd.entity.reportdata.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *@description: 菜品毛利分析Vo
 *@author: fancl
 *@create: 2020-03-01 
 */
@Getter
@Setter
public class FoodGrossAnalysisVo implements Serializable {
    //菜品大类
    private String foodStyleName;
    //菜品名称
    private String foodName;
    //单位名
    private String unitName;
    //销售金额
    private BigDecimal foodAmount;
    //菜品数量
    private BigDecimal foodNum;


}

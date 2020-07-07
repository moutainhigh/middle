package com.njwd.entity.reportdata.vo.scm;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *@description: 毛重Vo
 *@author: fancl
 *@create: 2020-03-24 
 */
@Getter
@Setter
public class GrossWeightVo implements Serializable {

    //菜品no
    String foodNo;
    String foodName;
    //门店id
    String shopId;
    String shopName;
    //父物料id
    String materialId;
    //子物料id
    String subMaterialId;
    //毛重
    BigDecimal grossWeight;
    //菜品成本=菜品单价*毛重
    BigDecimal cost;
    //物料编码
    String materialNumber;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("foodNo:").append(foodNo)
                .append(",foodName:").append(foodName)
                .append(",shopId:").append(shopId)
                .append(",materialId:").append(materialId)
                .append(",subMaterialId:").append(subMaterialId)
                .append(",materialNumber:").append(materialNumber)
                .append(",grossWeight:").append(grossWeight)
                .append(",cost:").append(cost).append("\r\n").toString();
    }


}

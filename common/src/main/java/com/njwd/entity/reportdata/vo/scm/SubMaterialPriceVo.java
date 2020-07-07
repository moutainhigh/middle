package com.njwd.entity.reportdata.vo.scm;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *@description: 子物料菜品单价Vo
 *@author: fancl
 *@create: 2020-03-24 
 */
@Setter
@Getter
public class SubMaterialPriceVo implements Serializable {
    String shopId;
    //父物料id
    String materialId;
    //子物料id
    String subMaterialId;
    //菜品单价
    BigDecimal price;
    //物料编码
    String materialNumber;
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("shopId:").append(shopId)
                .append(",materialId:").append(materialId)
                .append(",subMaterialId:").append(subMaterialId)
                .append(",materialNumber").append(materialNumber)
                .append(",price:").append(price)
                .append("\r\n").toString();
    }

}

package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.RepPosDetailFood;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: LuoY
 * @Date: 2020/1/2 17:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RepPosDetailFoodVo extends RepPosDetailFood {
    /**
     * 销售菜品汇总金额
     */
    private BigDecimal foodAllPrice;
    /**
     * 菜品平均售价
     */
    private BigDecimal foodAvgPrice;

    /**
     * 菜品类别编码
     */
    private String foodStyleNo;

    /**
     * 菜品赠送金额
     */
    private BigDecimal giveFoodPrice;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("shopId:").append(getShopId())
                .append(",foodNo:").append(getFoodNo())
                .append(",foodName:").append(getFoodName())
                .append(",foodNum:").append(getFoodNum())
                .append(",foodAvgPrice:").append(foodAvgPrice).append("\r\n").toString();
    }
}

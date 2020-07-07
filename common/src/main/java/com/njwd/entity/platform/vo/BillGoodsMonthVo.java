package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品月份消费出参
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BillGoodsMonthVo implements Serializable {

    @ApiModelProperty(value = "商品名称")
    private  String name;//商品名称

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;//商品ID

    @ApiModelProperty(value = "月份")
    private String monthDate;//月份

    @ApiModelProperty(value = "账单总额")
    private BigDecimal billSum;//账单总额
}

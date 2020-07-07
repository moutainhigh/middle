package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExpireOrderGoodsVo implements Serializable {

    @ApiModelProperty(value = "下单系统")
    private String order_sys;// 下单系统

    @ApiModelProperty(value = "订单明细ID")
    private Long detail_id;// 订单明细ID

    @ApiModelProperty(value = "支付方式")
    private Integer pay_type;// 支付方式

    @ApiModelProperty(value = "产品ID")
    private Long goods_id;// 产品ID

    @ApiModelProperty(value = "产品名称")
    private String goods_name;// 产品名称

    @ApiModelProperty(value = "产品编码")
    private String goods_code;// 产品编码

    @ApiModelProperty(value = "订单ID")
    private Long order_id;// 订单ID

    @ApiModelProperty(value = "开始计费日期")
    private String start_date;// 开始计费日期

    @ApiModelProperty(value = "客户iD")
    private String customer_id;// 客户iD

    @ApiModelProperty(value = "剩余几天到期")
    private Integer remaining_days;// 剩余几天到期

    @ApiModelProperty(value = "商品单价")
    private BigDecimal price;// 商品单价

    @ApiModelProperty(value = "计价单位")
    private String price_unit;// 计价单位

    @ApiModelProperty(value = "订单编码")
    private String order_code;// 订单编码
}

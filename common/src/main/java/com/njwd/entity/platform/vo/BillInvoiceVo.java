package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class BillInvoiceVo implements Serializable {

    @ApiModelProperty(value = "渠道名称")
    private String channel_name;// 渠道名称

    @ApiModelProperty(value = "账单支付方式,wechat:微信支付、alipay：支付宝支付、balance：余额支付")
    private String bill_pay_type;// 账单支付方式

    @ApiModelProperty(value = "计价参数")
    private String price_parameter;// 计价参数

    @ApiModelProperty(value = "分类名称")
    private String type_name;// 分类名称

    @ApiModelProperty(value = "商品名称")
    private String goods_name;// 商品名称

    @ApiModelProperty(value = "计价单位")
    private String price_unit;// 计价单位

    @ApiModelProperty(value = "商品编码")
    private String goods_code;// 商品编码

    @ApiModelProperty(value = "账单编码")
    private String bill_code;// 账单编码

    @ApiModelProperty(value = "账单金额")
    private BigDecimal bill_sum;// 账单金额

    @ApiModelProperty(value = "账单ID")
    private Long bill_id;// 账单ID

    @ApiModelProperty(value = "账单日期")
    private String bill_date;// 账单日期

    @ApiModelProperty(value = "订单编码")
    private String order_code;// 订单编码
}

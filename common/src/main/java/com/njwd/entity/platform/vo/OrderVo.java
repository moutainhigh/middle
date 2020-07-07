package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderVo implements Serializable {

     @ApiModelProperty(value = "下单系统")
     private String orderSys;//下单系统

     @ApiModelProperty(value = "渠道名称")
     private String channelName;//渠道名称

     @ApiModelProperty(value = "订单明细ID")
     private String detailId;//订单明细ID

     @ApiModelProperty(value = "状态：0试用1开通2停用3欠费")
     private Integer status;//状态：0试用1开通2停用3欠费

     @ApiModelProperty(value = "备注")
     private String  remark;//备注

     @ApiModelProperty(value = "计价参数")
     private String  priceParameter;//计价参数

     @ApiModelProperty(value = "开通日期")
     private String  openDate;//开通日期

     @ApiModelProperty(value = "客户名称")
     private String  customerName;//客户名称

     @ApiModelProperty(value = "商品名称")
     private String  goodsName;//商品明恒

     @ApiModelProperty(value = "订单ID")
     private Long  orderId;//订单ID

     @ApiModelProperty(value = "欠费天数")
     private Integer oweBillNum;//欠费天数

     @ApiModelProperty(value = "渠道编码")
     private String  channelCode;//渠道编码

     @ApiModelProperty(value = "订单编码")
     private String  orderCode;//订单编码

     @ApiModelProperty(value = "渠道ID")
     private Long channelId;//渠道ID

     @ApiModelProperty(value = "分类ID")
     private Long typeId;//分类ID

     @ApiModelProperty(value = "停用日期")
     private String endDate;//停用日期

     @ApiModelProperty(value = "单价")
     private BigDecimal price;//单价

     @ApiModelProperty(value = "支付类型0后付费1预付费")
     private Integer payType;//支付类型0后付费1预付费

     @ApiModelProperty(value = "商品ID")
     private Long goodsId;//商品ID

     @ApiModelProperty(value = "分类名称")
     private String typeName;//分类名称

     @ApiModelProperty(value = "计价单位")
     private String priceUnit;//计价单位

     @ApiModelProperty(value = "商品编码")
     private String goodsCode;//商品编码

     @ApiModelProperty(value = "开始计费日期")
     private String startDate;//开始计费日期

     @ApiModelProperty(value = "客户ID")
     private String customerId;//客户ID
}

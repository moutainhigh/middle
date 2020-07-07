package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class BillVO implements Serializable {

    @ApiModelProperty(value = "账单支付方式")
    private String billPayType;//账单支付方式

    @ApiModelProperty(value = "扣款时间")
    private String deductionTime;//扣款时间

    @ApiModelProperty(value = "商品名称")
    private String  goodsName;//商品明恒

    @ApiModelProperty(value = "订单ID")
    private Long  orderId;//订单ID

    @ApiModelProperty(value = "渠道编码")
    private String  channelCode;//渠道编码

    @ApiModelProperty(value = "订单编码")
    private String  orderCode;//订单编码

    @ApiModelProperty(value = "计价参数值")
    private String parameterValue;//计价参数值

    @ApiModelProperty(value = "账单id")
    private Long id;//账单id

    @ApiModelProperty(value = "扣款金额")
    private BigDecimal  deductionSum;//备注

    @ApiModelProperty(value = "固定值1")
    private Long rootOrgId;//固定值1

    @ApiModelProperty(value = "计价单位")
    private String priceUnit;//计价单位

    @ApiModelProperty(value = "商品编码")
    private String goodsCode;//商品编码

    @ApiModelProperty(value = "渠道名称")
    private String  channelName;//渠道名称

    @ApiModelProperty(value = "欠费金额")
    private BigDecimal  oweSum;//欠费金额

    @ApiModelProperty(value = "计价参数")
    private String  priceParameter;//计价参数

    @ApiModelProperty(value = "客户名称")
    private String  customerName;//客户名称

    @ApiModelProperty(value = "账单明细id")
    private Long orderDetailId;//账单明细id

    @ApiModelProperty(value = "账单金额")
    private BigDecimal  billSum;//账单金额

    @ApiModelProperty(value = "渠道ID")
    private Long channelId;//渠道ID

    @ApiModelProperty(value = "分类ID")
    private Long typeId;//分类ID

    @ApiModelProperty(value = "单价")
    private BigDecimal price;//单价

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;//商品ID

    @ApiModelProperty(value = "分类名称")
    private String typeName;//分类名称

    @ApiModelProperty(value = "客户ID")
    private String customerId;//客户ID

    @ApiModelProperty(value = "创建时间")
    private String  createTime;//开通日期

    @ApiModelProperty(value = "账单日期")
    private String billDate;//账单日期

    @ApiModelProperty(value = "账单编码")
    private String billCode;//账单编码

    @ApiModelProperty(value = "支付类型0后付费1预付费")
    private Integer payType;//支付类型0后付费1预付费

}

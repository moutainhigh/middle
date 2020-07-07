package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsDetailVO implements Serializable {

    @ApiModelProperty(value = "商品图片地址")
    private String  imgUrl;//商品图片地址

    @ApiModelProperty(value = "渠道名称")
    private String  channelName;//渠道名称

    @ApiModelProperty(value = "")
    private Long  imgId;

    @ApiModelProperty(value = "商品状态0上架1下架")
    private Integer  status;//商品状态0上架1下架

    @ApiModelProperty(value = "备注")
    private String  remark;//备注

    @ApiModelProperty(value = "计价参数")
    private String priceParameter;//计价参数

    @ApiModelProperty(value = "编码")
    private String code;//编码

    @ApiModelProperty(value = "试用天数")
    private Integer probationDay;//试用天数

    @ApiModelProperty(value = "欠费天数")
    private Integer oweBillNum;//欠费天数

    @ApiModelProperty(value = "渠道编码")
    private String  channelCode;//渠道编码

    @ApiModelProperty(value = "订单状态：-1无订单0试用1开通2停用3欠费")
    private Integer orderStatus;//订单状态：-1无订单0试用1开通2停用3欠费

    @ApiModelProperty(value = "渠道ID")
    private Long channelId;//渠道ID

    @ApiModelProperty(value = "商品分类ID")
    private Long typeId;//商品分类ID

    @ApiModelProperty(value = "商品ID")
    private Long id;//商品ID

    @ApiModelProperty(value = "")
    private Long rootOrgId;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;//价格

    @ApiModelProperty(value = "订单价格")
    private BigDecimal orderPrice;// 订单价格

    @ApiModelProperty(value = "支付方式：0后付费1预付费")
    private Integer payType;//支付方式：0后付费1预付费

    @ApiModelProperty(value = "商品名称")
    private String  name;//商品名称

    @ApiModelProperty(value = "分类名称")
    private String  typeName;//分类名称

    @ApiModelProperty(value = "计价单位")
    private String  priceUnit;//计价单位

    @ApiModelProperty(value = "评价统计")
    private TotalEvaluateVo totalEvaluateVo;

    @ApiModelProperty(value = "评价列表")
    private List<EvaluateVo> evaluateList;


}

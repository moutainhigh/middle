package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsProbationVo implements Serializable {

    @ApiModelProperty(value = "下单系统名称")
    private String orderSys;//下单系统名称

    private Long detailId;

    @ApiModelProperty(value = "备注")
    private String remark;//备注

    @ApiModelProperty(value = "状态：0使用1开通")
    private Integer status;// 状态：0使用1开通

    @ApiModelProperty(value = "数量")
    private String  priceParameter;//数量

    @ApiModelProperty(value = "2020-05-01,开通日期")
    private String  openDate;//"2020-05-01",开通日期

    @ApiModelProperty(value = "商品名称")
    private String  goodsName;// 商品名称

    private Integer probationDay;

    @ApiModelProperty(value = "订单id")
    private Long orderId;//订单id

    @ApiModelProperty(value = "类型id")
    private Long typeId;//类型id

    private Long channelId;

    @ApiModelProperty(value = "之前开通的订单编号")
    private String orderCode;// 之前开通的订单编号

    private String endDate;

    private BigDecimal  price;

    private Integer  needAutoDeduction;

    private Integer payType;

    @ApiModelProperty(value = "商品ID")
    private Long goodsId;// 商品ID

            //"create_time":Object{...},

    private String priceUnit;

    private String goodsCode;

    @ApiModelProperty(value = "2020-05-02 开始计费日期")
    private String startDate;//"2020-05-02"开始计费日期
}

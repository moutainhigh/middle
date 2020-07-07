package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderListDto implements Serializable {

    @ApiModelProperty(value = " 固定值，必传1")
    private Long root_org_id;// 固定值，必传1

    @ApiModelProperty(value = "2020-02-01,开始日期")
    private String open_start_date; //2020-02-01,开始日期

    @ApiModelProperty(value = "商品ID")
    private Long goods_id;//商品ID

    @ApiModelProperty(value = "2020-02-21,结束日期")
    private String open_end_date;//2020-02-21,结束日期

    @ApiModelProperty(value = "订单编码")
    private String order_code; //订单编码

    @ApiModelProperty(value = "分类ID")
    private Long type_id;//分类ID

    @ApiModelProperty(value = "客户ID，必传")
    private String customer_id;//客户ID，必传

    @ApiModelProperty(value = "页码")
    private Integer page;//客户ID，必传

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;//客户ID，必传
}

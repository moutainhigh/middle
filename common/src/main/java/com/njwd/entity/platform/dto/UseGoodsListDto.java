package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 使用商品相关入参
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UseGoodsListDto implements Serializable {

    @ApiModelProperty(value = "下单系统名称")
    private String order_sys;// 下单系统名称

    @ApiModelProperty(value = "固定值1")
    private Long root_org_id;// 固定值1

    @ApiModelProperty(value = "将要使用的商品集合")
    private List<UseGoodsDto> goods_list;

    @ApiModelProperty(value = "会员名称")
    private String customer_name;// 会员名称

    @ApiModelProperty(value = "会员ID")
    private String customer_id;// 会员ID
}

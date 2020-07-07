package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsDetailDto implements Serializable {

    @ApiModelProperty(value = " 固定值为1")
    private Long root_org_id;// 固定值1

    @ApiModelProperty(value = " 客户ID")
    private String customer_id;//客户ID

    @ApiModelProperty(value = " 商品ID")
    private Long goods_id;//商品ID

}

package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsConditionVo implements Serializable {

    @ApiModelProperty(value = "商品ID")
    private Long id;//商品ID

    @ApiModelProperty(value = "商品名称")
    private String name;//商品名称

    @ApiModelProperty(value = "类别ID")
    private String type_id; //类别ID
}

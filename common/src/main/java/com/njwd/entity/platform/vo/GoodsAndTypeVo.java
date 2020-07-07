package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsAndTypeVo implements Serializable {

    @ApiModelProperty(value = "类别结果集")
    private List<TypeConditonVo> list_type;//类别结果集

    @ApiModelProperty(value = "执行结果状态")
    private String status;//执行结果状态

    @ApiModelProperty(value = "商品结果集")
    private List<GoodsConditionVo> list_goods;//商品结果集

}

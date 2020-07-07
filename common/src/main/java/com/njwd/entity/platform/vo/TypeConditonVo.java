package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class TypeConditonVo implements Serializable {

    @ApiModelProperty(value = "类别ID")
    private Long id;//类别ID

    @ApiModelProperty(value = "类别名称")
    private String name;//类别名称
}

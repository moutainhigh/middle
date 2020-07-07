package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodAndTypeDto implements Serializable {

    @ApiModelProperty(value = "固定值，必传")
    private Long root_org_id;// 固定值，必传

    @ApiModelProperty(value = "渠道编码")
    private String channel_code;//渠道编码

    @ApiModelProperty(value = "分类ID")
    private Long type_id;//分类ID
}

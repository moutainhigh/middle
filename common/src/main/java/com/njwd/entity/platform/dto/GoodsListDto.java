package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsListDto implements Serializable {

    /**
     * 因外部提供接口为下划线型，故此处的入参也需设置为下划线型
     */
    @ApiModelProperty(value = " 固定值为1")
    private Long root_org_id;	// 固定值1

    @ApiModelProperty(value = "页码")
    private Integer page=1;	// 页码

    @ApiModelProperty(value = "每页显示条数")
    private Integer pageSize=10;	// 每页显示条数

    @ApiModelProperty(value = "商品渠道")
    private String channel_code;	//商品渠道
}

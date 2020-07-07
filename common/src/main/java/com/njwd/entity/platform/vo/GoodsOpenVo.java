package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsOpenVo implements Serializable {

    @ApiModelProperty(value = "停用日期")
    private String endDate;//停用日期

    @ApiModelProperty(value = "是否需要自动续费")
    private Integer needAutoDeduction;//是否需要自动续费

    @ApiModelProperty(value = "状态0试用1开通2停用")
    private Integer status;//状态0试用1开通2停用

    @ApiModelProperty(value = "订单ID")
    private Long orderId;//订单ID
}

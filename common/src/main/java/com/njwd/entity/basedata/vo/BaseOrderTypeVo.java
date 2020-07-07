package com.njwd.entity.basedata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @Description: 订单类型vo
* @Author: shenhf
* @Date: 2020/2/13 17:46
*/
@ApiModel(value = "订单类型")
@Data
public class BaseOrderTypeVo{
    /**
     * 订单类型id
     */
    @ApiModelProperty(value = "订单类型id")
    private String OrderTypeId;

    /**
     * 订单类型名称
     */
    @ApiModelProperty(value = "订单类型名称")
    private String OrderTypeName;

}

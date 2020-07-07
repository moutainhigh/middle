package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExpireOrderGoodsDto implements Serializable {

    @ApiModelProperty(value = "预警天数")
    private Integer day_num;//预警天数

    @ApiModelProperty(value = "crm客户id")
    private String customer_id;//crm客户id
}

package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class FindPrepaidDto implements Serializable {

    /**
     * 业务交易号
     */
    @ApiModelProperty(value = "业务交易号")
    private String payCode;
}

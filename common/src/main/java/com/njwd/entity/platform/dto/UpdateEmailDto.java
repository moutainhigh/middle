package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateEmailDto implements Serializable {

    /**
     * 新邮箱地址
     */
    @ApiModelProperty(value = "新邮箱地址")
    private String email;
}

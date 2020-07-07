package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class MobileDto implements Serializable {

    /**
     * 用户账号(一般为手机号) 账号（一般为手机号）
     */
    @ApiModelProperty(value = "用户账号(一般为手机号)")
    private String mobile;

}

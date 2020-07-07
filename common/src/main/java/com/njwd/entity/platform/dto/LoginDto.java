package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoginDto extends UserDto{

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    private String verificationCode;

    /**
     * 登录方式 1、账号密码登录 2、验证码登录
     */
    @ApiModelProperty(value = "登录方式 1、账号密码登录 2、验证码登录")
    private Integer loginType;
}

package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdatePasswordDto implements Serializable {

    /**
     * 新登录密码 账号密码（加密）
     */
    @ApiModelProperty(value = "新密码")
    private String newPassword;
}

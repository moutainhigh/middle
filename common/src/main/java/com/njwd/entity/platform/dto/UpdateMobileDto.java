package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateMobileDto implements Serializable {

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    private String verificationCode;

    /**
     * 旧手机号码
     */
    @ApiModelProperty(value = "旧手机号码")
    private String oldMobile;

    /**
     * 新手机号码
     */
    @ApiModelProperty(value = "新手机号码")
    private String newMobile;
}

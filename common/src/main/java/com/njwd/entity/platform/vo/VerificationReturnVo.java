package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 调验证码接口返回入参
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class VerificationReturnVo implements Serializable {

    @ApiModelProperty(value = "返回状态")
    private String status;

    @ApiModelProperty(value = "验证码")
    private String code;
}

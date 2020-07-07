package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 调用微信支付接口公共请求参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PayOnlinePublicDto implements Serializable {

    @ApiModelProperty(value = "充值金额")
    private  BigDecimal money;

    @ApiModelProperty(value = "充值支付业务类型")
    private String identification;

    @ApiModelProperty(value = "支付类型")
    private String meansType;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "类型")
    private PayOnlineDto content;

}

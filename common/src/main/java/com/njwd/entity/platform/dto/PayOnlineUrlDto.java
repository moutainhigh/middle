package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class PayOnlineUrlDto implements Serializable {

    @ApiModelProperty(value = "充值金额")
    private BigDecimal money;

    @ApiModelProperty(value = "充值支付业务类型")
    private String identification;

    @ApiModelProperty(value = "支付类型")
    private String meansType;

    @ApiModelProperty(value = "类型")
    private String type;
}

package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class PayOnlineDto implements Serializable {

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "收款门店id")
    private Long enterpriseIdPayAccount;

    @ApiModelProperty(value = "支付成功回调")
    private String notifyUrl;

    @ApiModelProperty(value = "门店名称")
    private String enterprise_name;

    @ApiModelProperty(value = "商户支付编号，唯一标识，前端传")
    private String payCode;

    @ApiModelProperty(value = "注册公司名")
    private String company_name;

    @ApiModelProperty(value = "注册公司会员id")
    private String cardId;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal money;

}

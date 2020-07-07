package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 支付入口出参
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PayEntranceVo implements Serializable {

    @ApiModelProperty(value = "支付二维码图片地址")
    private String wechatpayImageUrl;

    @ApiModelProperty(value = "商户支付编号")
    private String payCode;

    @ApiModelProperty(value = "调用返回状态")
    private String status;

}

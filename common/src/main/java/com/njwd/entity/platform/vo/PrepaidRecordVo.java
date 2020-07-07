package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class PrepaidRecordVo implements Serializable {

    /**
     * 注册公司会员id
     */
    @ApiModelProperty(value = "注册公司会员id")
    private String cardId;

    @ApiModelProperty(value = "充值金额")
    private String onceMoney;

    @ApiModelProperty(value = "充值日期")
    private String createTime;

    @ApiModelProperty(value = "数据格式逗号分隔(payCode,公司名称)")
    private String remark;

    @ApiModelProperty(value = "3-微信支付")
    private Integer payType;

    @ApiModelProperty(value = "业务交易号")
    private String payCode;

    @ApiModelProperty(value = "交易渠道流水号")
    private String thirdCode;





}

package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * wd_recharge
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RechargeDto implements Serializable {
    /**
     * 主键 默认自动递增
     */
    private Long rechargeId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 业务交易号
     */
    private String code;

    /**
     * 支付方式 0微信1支付宝2银联
     */
    @ApiModelProperty(value = "支付方式 0微信1支付宝2银联")
    private Boolean payType;

    /**
     * 充值金额
     */
    @ApiModelProperty(value = "充值金额")
    private BigDecimal money;

    /**
     * 充值状态 0：未回调到结果 1：充值成功
     */
    private Boolean status;

    /**
     * 业务创建时间
     */
    private Date createTime;

    /**
     * 条目修改时间
     */
    private Date updateTime;

    /**
     * 第三方交易号
     */
    private String thirdCode;


}
package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现用于调用MSS系统的入参
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ApplyCashMssDto implements Serializable {

    /**
     * 企业id 本系统固定为1
     */
    @ApiModelProperty(value = "企业id 本系统固定为1")
    private Long root_org_id;

    /**
     * 提现类型
     */
    @ApiModelProperty(value = "提现类型")
    private Integer apply_type;

    /**
     * 提现人id
     */
    @ApiModelProperty(value = "提现人id")
    private String user_id;

    /**
     * 提现人
     */
    @ApiModelProperty(value = "提现人")
    private String user_name;

    /**
     * 提现人手机号
     */
    @ApiModelProperty(value = "提现人手机号")
    private String mobile;

    /**
     * 提现单号 提现单号（系统生成）
     */
    @ApiModelProperty(value = "提现单号")
    private String cash_code;

    /**
     * 提现单号 提现单号（系统生成）
     */
    @ApiModelProperty(value = "提现单号")
    private String apply_code;

    /**
     * 线下打款时间
     */
    @ApiModelProperty(value = "线下打款时间")
    private String apply_time;

    /**
     * 提现金额
     */
    @ApiModelProperty(value = "提现金额")
    private BigDecimal cash_sum;

    /**
     * 账户名称
     */
    @ApiModelProperty(value = "账户名称")
    private String bank_account;

    /**
     * 银行卡账号
     */
    @ApiModelProperty(value = "银行卡账号")
    private String bank_number;

    /**
     * 账号所在银行
     */
    @ApiModelProperty(value = "账号所在银行")
    private String bank_name;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}

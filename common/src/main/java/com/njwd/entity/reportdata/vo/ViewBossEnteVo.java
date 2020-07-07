package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 企业概况
 *
 * @author shenhf
 * @date 2020-03-25 18:02
 */
@Data
@ApiModel
public class ViewBossEnteVo implements Serializable {
    private static final long serialVersionUID = 6143086550908034570L;
    /**
     * 员工数
     */
    @ApiModelProperty(value = "员工数")
    private BigDecimal userNum;

    /**
     * 会员数
     */
    @ApiModelProperty(value = "会员数")
    private BigDecimal memberNum;
    /**
     * 会员数
     */
    @ApiModelProperty(value = "供应商数量")
    private BigDecimal supplierNum;

    /**
     * 收入（万）
     */
    @ApiModelProperty(value = "收入")
    private BigDecimal incomeAmount;

    /**
     * 成本费用（万）
     */
    @ApiModelProperty(value = "成本费用")
    private BigDecimal costAmount;

    /**
     * 净利润（万）
     */
    @ApiModelProperty(value = "净利润")
    private BigDecimal netProfitAmount;

    /**
     * 会员卡充值（万）
     */
    @ApiModelProperty(value = "会员卡充值")
    private BigDecimal memberRechargeAmount;

    /**
     * 会员卡消费（万）
     */
    @ApiModelProperty(value = "会员卡消费")
    private BigDecimal memberConsumeAmount;

    /**
     * 会员卡余额（万）
     */
    @ApiModelProperty(value = "会员卡余额")
    private BigDecimal memberCardAmount;

}


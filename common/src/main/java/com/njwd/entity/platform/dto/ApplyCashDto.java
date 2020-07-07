package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * wd_apply_cash
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApplyCashDto implements Serializable {
    /**
     * 主键 默认自动递增
     */
    @ApiModelProperty(value = "主键")
    private Long cashId;

    /**
     * 主键 默认自动递增
     */
    @ApiModelProperty(value = "主键")
    private Long crmUserId;

    /**
     * 提现人id
     */
    @ApiModelProperty(value = "提现人id")
    private String userId;

    /**
     * 提现人
     */
    @ApiModelProperty(value = "提现人")
    private String userName;

    /**
     * 注册的客户公司名
     */
    @ApiModelProperty(value = "注册的客户公司名")
    private String companyName;

    /**
     * 注册手机号
     */
    @ApiModelProperty(value = "注册手机号")
    private String mobile;

    /**
     * 提现单号 提现单号（系统生成）
     */
    @ApiModelProperty(value = "提现单号")
    private String cashCode;

    /**
     * 提现金额
     */
    @ApiModelProperty(value = "提现金额")
    private BigDecimal cashSum;

    /**
     * 账户名称
     */
    @ApiModelProperty(value = "账户名称")
    private String bankAccount;

    /**
     * 银行卡账号
     */
    @ApiModelProperty(value = "银行卡账号")
    private String bankNumber;

    /**
     * 银行账号
     */
    @ApiModelProperty(value = "银行账号")
    private String accountNumber;

    /**
     * 账号所在银行
     */
    @ApiModelProperty(value = "账号所在银行")
    private String bankName;

    /**
     * CRM结果状态 0未扣款1已扣款
     */
    @ApiModelProperty(value = "CRM结果状态 0未扣款1已扣款")
    private Integer crmStatus;

    /**
     * MSS结果状态 0未打款1已打款 -1未打款
     */
    @ApiModelProperty(value = " MSS结果状态 0未打款1已打款 -1未打款")
    private Integer mssStatus;

    /**
     * 线下打款时间
     */
    @ApiModelProperty(value = "线下打款时间")
    private Date payTime;

    /**
     * 提现申请时间
     */
    @ApiModelProperty(value = "提现申请时间")
    private Date createTime;

    /**
     * 条目修改时间
     */
    @ApiModelProperty(value = "条目修改时间")
    private Date updateTime;


    /**
     * 查询开始日期
     */
    @ApiModelProperty(value = "查询开始日期")
    private Date createStartTime;

    /**
     * 查询结束日期
     */
    @ApiModelProperty(value = "查询结束日期")
    private Date createEndTime;

}
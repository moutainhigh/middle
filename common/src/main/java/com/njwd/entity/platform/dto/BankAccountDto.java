package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * wd_bank_account
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BankAccountDto implements Serializable {
    /**
     * 主键 默认自动递增
     */
    @ApiModelProperty(value = "主键 默认自动递增")
    private Long bankAccountId;

    /**
     * 用户id 外键关联用户表
     */
    @ApiModelProperty(value = "用户id 外键关联用户表")
    private String userId;

    /**
     * 账户类型 0对公1个人
     */
    @ApiModelProperty(value = "账户类型 0对公1个人")
    private Integer accountType;

    /**
     * 开户名
     */
    @ApiModelProperty(value = "开户名")
    private String accountName;

    /**
     * 银行账号
     */
    @ApiModelProperty(value = "银行账号")
    private String accountNumber;

    /**
     * 开户行
     */
    @ApiModelProperty(value = "开户行")
    private String bankName;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String linkman;

    /**
     * 联系手机
     */
    @ApiModelProperty(value = "联系手机")
    private String mobile;

    /**
     * 条目创建时间
     */
    @ApiModelProperty(value = "条目创建时间")
    private Date createTime;

    /**
     * 条目修改时间
     */
    @ApiModelProperty(value = "条目修改时间")
    private Date updateTime;

    /**
     * 逻辑删除标志位
     */
    @ApiModelProperty(value = "逻辑删除标志位")
    private Integer isDel;

    /*以上为数据库表直接对应字段，以下为业务增加字段*/

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    private String verificationCode;
}
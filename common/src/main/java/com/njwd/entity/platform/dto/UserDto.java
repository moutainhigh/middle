package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hxh
 * @Description 用户实体类
 * @create 2020/3/23 15:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDto implements Serializable {
    /**
     * 主键 默认自动递增
     */
    @ApiModelProperty(value = "主键")
    private String userId;

    /**
     * crm返回的识别用户的id crm返回的识别用户的id
     */
    private String crmUserId;

    /**
     * crm返回的识别用户的id crm返回的识别用户的id
     */
    private String crmUserName;

    /**
     * 用户账号(一般为手机号) 账号（一般为手机号）
     */
    @ApiModelProperty(value = "用户账号(一般为手机号)")
    private String mobile;

    /**
     * 用户名 用户的名字、昵称等
     */
    private String userName;

    /**
     * 用户公司名称 用户公司名称
     */
    @ApiModelProperty(value = "用户公司名称")
    private String companyName;

    /**
     * 登录密码 账号密码（加密）
     */
    @ApiModelProperty(value = "登录密码 账号密码")
    private String password;

    /**
     * 绑定邮箱 账号所绑定的邮箱
     */
    @ApiModelProperty(value = "绑定邮箱 账号所绑定的邮箱")
    private String email;

    /**
     * 账号头像的图片CODE
     */
    @ApiModelProperty(value = "账号头像图片code")
    private String fileCode;

    /**
     * 账号头像图片URL
     */
    @ApiModelProperty(value = "账号头像图片URL")
    private String fileUrl;

    /**
     * 省 用户注册时的省
     */
    @ApiModelProperty(value = "省 用户注册时的省")
    private String province;

    /**
     * 市 用户注册时的市
     */
    @ApiModelProperty(value = "市 用户注册时的市")
    private String city;

    /**
     * (区)县 用户注册时的区县
     */
    @ApiModelProperty(value = "区)县 用户注册时的区县")
    private String county;

    /**
     * 条目创建时间 条目创建时间
     */
    private Date createTime;

    /**
     * 条目修改时间 条目修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除标志 逻辑删除标志位（0：未删除 1、已删除）
     */
    private Integer isDel;

}
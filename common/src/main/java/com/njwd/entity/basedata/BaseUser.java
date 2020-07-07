package com.njwd.entity.basedata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.utils.DateUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/6 16:06
 */
@Data
public class BaseUser implements Serializable {
    private static final long serialVersionUID = 3012543342956422577L;

    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 工号
     */
    private String jobCode;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 性别
     */
    private String sex;
    /**
     * 身份证号
     */
    private String identityCard;
    /**
     * 生日
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date birthday;
    /**
     * email邮箱
     */
    private String email;
    /**
     * 学历
     */
    private String eduLevel;
    /**
     * 婚姻状态
     */
    private String marriage;
    /**
     * 住址
     */
    private String address;
    /**
     * 入职时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date hireDate;
    /**
     * 转正时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date positiveTime;
    /**
     * 工作类型
     */
    private String workType;
    /**
     * 工作状态
     */
    private String workStatus;
    /**
     * 在职状态  0在职1离职
     */
    private Integer status;
    /**
     * 离职日期
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date leaveDate;
    /**
     * 企业ID
     */
    private String enteId;
}

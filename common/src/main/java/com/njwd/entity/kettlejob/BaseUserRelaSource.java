package com.njwd.entity.kettlejob;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.utils.DateUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工信息来源
 *
 * @author zhuzs
 * @date 2019-12-31 09:38
 */
@Data
public class BaseUserRelaSource implements Serializable {
    private static final long serialVersionUID = -8980504603443914534L;

    /**
     * 员工ID
     */

    private Long EmpID ;

    /**
     * 工号
     */
    private String Code ;

    /**
     * 姓名
     */
    private String Name ;

    /**
     * 国际区号(中国: +86)
     */
    private String MobileAreaCode ;

    /**
     * 手机号
     */
    private String Mobile ;

    /**
     * 性别(男，女)
     */
    private String Gender ;

    /**
     * 邮箱
     */
    private String Email ;

    /**
     * 生日（可为空，返回空时表示没有生日信息）(格式1980-01-01）
     */
    private String BirthDay ;

    /**
     * 部门ID
     */
    private Long DepartID ;

    /**
     * 部门名称
     */
    private String DepartName ;

    /**
     * 部门编码
     */
    private String DepartCode ;

    /**
     * 部门负责人员工编号
     */
    private String DepartManagerCode ;

    /**
     * 公司层级名称
     */
    private String LayerbranchName ;

    /**
     * 部门层级名称
     */
    private String LayerdepartName ;

    /**
     * 职位ID
     */
    private Long PostID ;

    /**
     * 职位编码
     */
    private String PostCode ;

    /**
     * 职位名称
     */
    private String PostName ;

    /**
     * 职位类型名称
     */
    private String PostTypeName ;

    /**
     * 在职状态(0: 离职；1:在职)
     */
    private Integer IsOnDuty ;

    /**
     * 是否转正（1:已转正，0:未转正）
     */
    private Integer IsRegular ;

    /**
     * 入职日期(格式1980-01-01）
     */
    private String EnterDate ;

    /**
     * 转正日期可为空(格式1980-01-01）
     */
    private String RegularDate ;

    /**
     * 离职日期可为空(格式1980-01-01）
     */
    private String DimissionDate ;

    /**
     * 用户微信UserId
     */
    private String WeiXinUserID ;

    /**
     * 用户钉钉UserId
     */
    private String DingTalkUserID ;

    /**
     * 删除状态(1: 已删除； 0:未删除)
     */
    private Byte IsDel ;

    /**
     * 是否是员工(1: 员工；0：试岗中)
     */
    private Byte IsNoteEmployee ;
    /**
     * 参数工作日期可为空(格式1980-01-01）
     */
    private String StartWorkDate ;

    /**
     * 员工工作状态
     */
    private String WorkStatus ;
    /**
     * 户藉
     */
    private String Residence;

    /**
     * 婚姻状况
     */
    private String marriage;

    /**
     * 民族
     */
    private String nation;

    /**
     * 学历
     */
    private String edulevel;

    /**
     * 紧急联系人
     */
    private String hurryupman;

    /**
     * 与紧急联系人的关系
     */
    private String hurryrelative;

    /**
     * 紧急联系人的电话
     */
    private String hurryupphone;

    /**
     * 证件类型（身份证，护照，港澳通行证，海外证件）
     */
    private String IDType;

    /**
     * 证件号码
     */
    private String IDNumber;

    /**
     * 银行名称
     */
    private String BankName;

    /**
     * 银行账号
     */
    private String BankAccount;

    /**
     * 银行名称2
     */
    private String BankName2;

    /**
     * 银行账号2
     */
    private String BankAccount2;
    /**
     * 日期类型:Date(1556868032680)
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_SECOND)
    @JsonFormat(pattern = DateUtils.PATTERN_SECOND)
    private Date updateDate ;
    @DateTimeFormat(pattern = DateUtils.PATTERN_SECOND)
    @JsonFormat(pattern = DateUtils.PATTERN_SECOND)
    private Date updateDate2 ;


}



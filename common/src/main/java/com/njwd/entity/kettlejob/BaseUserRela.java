package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import com.njwd.utils.DateUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工信息
 *
 * @author zhuzs
 * @date 2019-12-30 19:00
 */
@Data
public class BaseUserRela implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 应用id
	 */
	private String appId;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 第三方用户id
	 */
	@JSONField(name = "EmpID")
	private String thirdUserId;

	/**
	 * 用户名称
	 */
	@JSONField(name = "Name")
	private String userName;

	/**
	 * 工号
	 */
	@JSONField(name = "Code")
	private String jobCode;

	/**
	 * 手机号
	 */
	@JSONField(name = "Mobile")
	private String mobile;

	/**
	 * 性别
	 */
	private String sex;

	/**
	 * 身份证号
	 */
	@JSONField(name = "IDNumber")
	private String identityCard;

	/**
	 * 生日
	 */
	@JSONField(name = "BirthDay")
	@DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
	private Date birthday;

	/**
	 * email邮箱
	 */
	@JSONField(name = "Email")
	private String email;

	/**
	 * 学历
	 */
	private String eduLevel = "-1";

	/**
	 * 婚姻状态
	 */
	@JSONField(name = "marriageStr")
	private String marriage = "-1";

	/**
	 * 住址
	 */
	private String address;

	/**
	 * 入职时间
	 */
	@JSONField(name = "EnterDate")
	@DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
	private Date hiredate;

	/**
	 * 转正时间
	 */
	@JSONField(name = "RegularDate")
	@DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
	private Date positiveTime;

	/**
	 * 工作类型
	 */
	@JSONField(name = "PostTypeName")
	private String workType;

	/**
	 * 工作状态
	 */
	private String workStatus = "-1";

	/**
	 * 在职状态  0在职1离职
	 */
	@JSONField(name = "IsOnduty")
	private Integer status;

	/**
	 * 离职日期
	 */
	@JSONField(name = "DimissionDate")
	@DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
	private Date leavedate;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * 第三方企业ID
	 */
	private String thirdEnteId;

	/**
	 * 第三方更新时间
	 */
	@JSONField(name = "UpdateDate")
	@DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
	private Date thirdUpdateTime;

	/**
	 * 最后创建时间
	 */
	private Date createTime;

	/**
	 * 最后更新时间
	 */
	private Date updateTime;

	/**
	 * 部门ID
	 */
	private String orgId;

	/**
	 * 第三方组织id
	 */
	@JSONField(name = "departId")
	private String thirdOrgId;

	/**
	 * 职位ID
	 */
	private String postId;

	/**
	 * 第三方职位id
	 */
	@JSONField(name = "postId")
	private String thirdPostId;

}

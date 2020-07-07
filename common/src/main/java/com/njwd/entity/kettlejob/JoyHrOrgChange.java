package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 乐才 HR 调动 实体类
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrOrgChange {
	/**
	 *	用户ID
	 **/
	private String userId;

	/**
	 *	第三方用户ID
	 **/
	@JSONField(name = "EmpId")
	private String thirdUserId;

	/**
	 * 调动类型
	 **/
	@JSONField(name = "ChangeType")
	private String changeType;

	/**
	 *	调动日期
	 **/
	@JSONField(name = "ChangeDate")
	private Date changeDate;

	/**
	 *	原组织ID
	 **/
	private String oldOrgId;

	/**
	 *	第三方原组织ID
	 **/
	@JSONField(name = "OldDepartId")
	private String thirdOldOrgId;

	/**
	 *	原工作状态
	 **/
	@JSONField(name = "OldWorkStatus")
	private String oldWorkStatus;

	/**
	 *	 原工作类型
	 **/
	@JSONField(name = "OldWorkType")
	private String oldWorkType;

	/**
	 *	调入组织ID
	 **/
	private String newOrgId;

	/**
	 *	调入组织编号
	 **/
	@JSONField(name = "NewDepartId")
	private String newWorkStatus;

	/**
	 *	第三方调入组织ID
	 **/
	@JSONField(name = "NewDepartId")
	private String thirdNewOrgId;

	/**
	 *	调入工作类型
	 **/
	@JSONField(name = "NewWorkType")
	private String newWorkType;

	/**
	 *	调动状态（审批状态：2已审批、9终审驳回、10已驳回、11无需审批、12已撤销、13已撤回）
	 **/
	@JSONField(name = "Auditstatus")
	private Integer status;

	/**
	 *	企业ID
	 **/
	private String enteId;

	/**
	 *	APPID
	 **/
	private String appId;

	/**
	 * 最后获取时间
	 **/
	private Date lastUpdateTime;
}

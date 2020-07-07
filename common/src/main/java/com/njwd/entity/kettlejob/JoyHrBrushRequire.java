package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 乐才 HR 补卡数据 实体类
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrBrushRequire {

	/**
	 * 请假单ID
	 */
	@JSONField(name = "Id")
	private String brushReuqireId;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 第三方用户ID
	 */
	@JSONField(name = "EmpId")
	private String thirdUserId;

	/**
	 * 补卡日期
	 */
	@JSONField(name = "Attenddate")
	private String attendDate;


	/**
	 * 时段1上班补卡（有值表示有补卡)
	 */
	@JSONField(name = "Starttime1")
	private String StartTime1;


	/**
	 * 时段1下班补卡（有值表示有补卡)
	 */
	@JSONField(name = "Endtime1")
	private String EndTime1;


	/**
	 * 时段2上班补卡（有值表示有补卡)
	 */
	@JSONField(name = "Starttime2")
	private String StartTime2;


	/**
	 * 时段2下班补卡（有值表示有补卡)
	 */
	@JSONField(name = "Endtime2")
	private String EndTime2;


	/**
	 * 时段3上班补卡（有值表示有补卡)
	 */
	@JSONField(name = "Starttime3")
	private String StartTime3;

	/**
	 * 时段3下班补卡（有值表示有补卡)
	 */
	@JSONField(name = "Endtime3")
	private String EndTime3;

	/**
	 * 审核状态（2和11表示有效，其它状态表示无效单据）
	 */
	@JSONField(name = "Auditstatus")
	private String status;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * APP ID
	 */
	private String appId;

	/**
	 * 新增时间
	 */
	private Date createTime;

	/**
	 * 最后修改时间
	 */
	private Date updateTime;

	/**
	 * 第三方修改时间
	 */
	private Date lastUpdateTime;

}

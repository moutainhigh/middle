package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 乐才 HR 出勤数据 实体类
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrAttend {
	/**
	 * 出勤ID
	 */
	private String attendId;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 第三方用户ID
	 */
	@JSONField(name = "EmpId")
	private String thirdUserId;

	/**
	 * 日期
	 */
	@JSONField(name = "AttendDate")
	private String attendDate;

	/**
	 * 班次ID
	 */
	@JSONField(name = "RegularId")
	private String shiftId;

	/**
	 * 实际出勤工时
	 */
	@JSONField(name = "WorkHours")
	private String attendHour;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * APPID
	 */
	private String appId;

	/**
	 * 最后获取时间
	 */
	private Date lastUpdateTime;

}

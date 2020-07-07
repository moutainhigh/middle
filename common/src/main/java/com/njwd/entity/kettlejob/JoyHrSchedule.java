package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 乐才 HR 排版数据 实体类
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrSchedule {

	/**
	 *	排班ID
	 */
	private String scheduleId;

	/**
	 *	用户ID
	 */
	private String userId;

	/**
	 *	第三方用户ID
	 */
	@JSONField(name = "EmpId")
	private String thirdUserId;

	/**
	 *	班次
	 */
	@JSONField(name = "RegularId")
	private String shiftId;

	/**
	 *	排班日期
	 */
	@JSONField(name = "AttendDate")
	private String scheduleDate;

	/**
	 *	是否划线班班次 -1:划线班次 0:固定班次
	 */
	@JSONField(name = "Isline")
	private Integer isLine;

	/**
	 *	企业ID
	 */
	private String enteId;

	/**
	 * APP ID
	 */
	private String appId;

	/**
	 * 最后更新时间
	 */
	private Date lastUpdateTime;
}

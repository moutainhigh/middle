package com.njwd.entity.kettlejob;

import lombok.Data;

/**
 * @Description 乐才 HR 班次 实体类
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrShiftTime {

	/**
	 * 时段ID
	 */
	private String shiftTimeId;

	/**
	 * 名称
	 */
	private String shiftTimeName;

	/**
	 * 班次ID
	 */
	private String shiftId;

	/**
	 * 计薪时长
	 */
	private Double paidTime;

	/**
	 * 允许最早打卡时间
	 */
	private String startTime;

	/**
	 * 允许最晚打卡时间
	 */
	private String endTime;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * APPID
	 */
	private String appId;

}

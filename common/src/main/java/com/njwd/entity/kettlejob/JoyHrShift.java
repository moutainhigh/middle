package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 乐才 HR 班次 实体类
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrShift {

	/**
	 * 班次ID
	 */
	@JSONField(name = "Id")
	private String shiftId;

	/**
	 * 名称
	 */
	@JSONField(name = "Name")
	private String shiftName;

	/**
	 * 简称
	 */
	@JSONField(name = "Shortname")
	private String shortName;

	/**
	 * 适用范围  全职工 小时工
	 */
	@JSONField(name = "Userange")
	private Integer scope;

	/**
	 * 班次性质
	 */
	private Integer nature;

	/**
	 * 班次类型
	 */
	private String shiftType;

	/**
	 * 班次类型 字符串
	 */
	@JSONField(name = "RegularyTypeName")
	private String shiftTypeStr;

	/**
	 * 补贴
	 */
	@JSONField(name = "RegularySubsidy")
	private Double subsidy;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * APPID
	 */
	private String appId;

	/**
	 * 小时数
	 */
	@JSONField(name = "Hours")
	private Double hours;
	/**
	 * 工时1
	 */
	@JSONField(name = "Hours1")
	private Double hours1;
	/**
	 * 工时2
	 */
	@JSONField(name = "Hours2")
	private Double hours2;
	/**
	 * 工时3
	 */
	@JSONField(name = "Hours3")
	private Double hours3;
	/**
	 * 开始时间1
	 */
	@JSONField(name = "Starttime1")
	private String startTime1;
	/**
	 * 结束时间1
	 */
	@JSONField(name = "Endtime1")
	private String endTime1;
	/**
	 * 开始时间2
	 */
	@JSONField(name = "Starttime2")
	private String startTime2;
	/**
	 * 结束时间2
	 */
	@JSONField(name = "Endtime2")
	private String endTime2;
	/**
	 * 开始时间3
	 */
	@JSONField(name = "Starttime3")
	private String startTime3;
	/**
	 * 结束时间3
	 */
	@JSONField(name = "Endtime3")
	private String endTime3;

	/**
	 * 最后获取时间
	 */
	private Date lastUpdateTime;

	public void setShiftTypeStr(String shiftTypeStr) {
		if (shiftTypeStr == null) {
			return;
		}
		this.shiftTypeStr = shiftTypeStr;
		if (shiftTypeStr.equals("正常班")) {
			this.shiftType = "0";
		} else if (shiftTypeStr.equals("晚班")) {
			this.shiftType = "1";
		}

	}
}

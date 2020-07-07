package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 乐才 HR 出差数据 实体类
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrTravel {

	/**
	 * 请假单ID
	 */
	@JSONField(name = "Id")
	private String travelId;

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
	 * 开始城市
	 */
	@JSONField(name = "Address_start")
	private String startCity;

	/**
	 * 结束城市
	 */
	@JSONField(name = "Address")
	private String endCity;

	/**
	 * 开始时间
	 */
	@JSONField(name = "Startdate")
	private String startTime;

	/**
	 * 结束时间
	 */
	@JSONField(name = "Enddate")
	private String endTime;

	/**
	 * 出差天数
	 */
	@JSONField(name = "Days")
	private String travelDays;

	/**
	 * 审批状态（2和11表示有效，其它状态表示无效单据）
	 */
	@JSONField(name = "Auditstatus")
	private Integer status;

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

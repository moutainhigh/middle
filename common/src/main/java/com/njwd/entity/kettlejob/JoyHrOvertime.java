package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 乐才 HR 加班数据 实体类
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrOvertime {

	/**
	 * 加班单ID
	 */
	@JSONField(name = "Id")
	private String overtimeId;

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
	 * 加班开始时间
	 */
	@JSONField(name = "StartDate")
	private String startTime;

	/**
	 * 加班结束时间
	 */
	@JSONField(name = "EndDate")
	private String endTime;

	/**
	 * 加班时长
	 */
	@JSONField(name = "Hours")
	private String hour;

	/**
	 * 加班类型
	 */
	@JSONField(name = "Type")
	private Long overtimeTypeId;

	/**
	 * 算薪倍率
	 */
	@JSONField(name = "SalaryRate")
	private Double SalaryRate;

	/**
	 * 存休倍率
	 */
	@JSONField(name = "HedingRate")
	private Double hedingRate;

	/**
	 * 审核状态（2和11表示有效，其它状态表示无效单据）
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
	 * 最后更新时间
	 */
	private Date lastUpdateTime;

}

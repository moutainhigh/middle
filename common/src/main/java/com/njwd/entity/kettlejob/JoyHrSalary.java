package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 乐才 HR 薪资
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrSalary {
	/**
	 *  组织ID
	 */
	private String orgId;

	/**
	 * 第三方组织ID
	 */
	@JSONField(name = "DepartId")
	private String thirdOrgId;

	/**
	 * 项目ID
	 */
	private String itemId;

	/**
	 * 项目名称
	 */
	private String itemName;

	/**
	 * 薪水
	 */
	@JSONField(name = "SalarySum")
	private Double money;

	/**
	 * 期间月份
	 */
	private Integer periodYearNum;

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

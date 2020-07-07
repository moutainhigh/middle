package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 设置 经营日报 实体类
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class SettingDaily {

	/**
	 * ID 唯一 生成
	 */
	private String dailyIndicId;

	/**
	 * 品牌ID
	 */
	private String brandId;

	/**
	 * 区域ID
	 */
	private String regionId;

	/**
	 * 门店ID
	 */
	private String shopId;

	/**
	 * 项目编码
	 */
	private String projectId;

	/**
	 * 项目名称
	 */
	private String projectName;

	/**
	 * 项目指标
	 */
	private BigDecimal indicator;

	/**
	 *	有效期
	 */
	private Integer periodYearNum;

	/**
	 * 状态 0 禁用 1 启用
	 */
	private Integer status;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * 更新时间
	 */
	private Date updateTime;


}

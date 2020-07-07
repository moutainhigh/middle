package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 设置 SettingDaily 实体类
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class SettingProfit {

	/**
	 * ID 唯一 生成
	 */
	private String id;

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
	 * 预算数
	 */
	private BigDecimal budget;

	/**
	 * 占收入比
	 */
	private BigDecimal percentage;

	/**
	 * 开始有效时间
	 */
	private Date beginDate;

	/**
	 * 结束有效时间
	 */
	private Date endDate;

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
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;


}

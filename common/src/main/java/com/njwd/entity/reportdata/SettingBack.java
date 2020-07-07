package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 设置模块 退赠优免安全阀值 实体类 wd_setting_back
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class SettingBack {

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
	 * 食物ID
	 */
	private String foodId;
	/**
	 * 单位ID
	 */
	private String unitId;

	/**
	 * 退菜数量阀值
	 */
	private BigDecimal threshold;

	/**
	 * 状态 0 禁用 1 启用
	 */
	private Integer status;

	/**
	 * 企业ID
	 */
	private Long enteId;

	/**
	 * 更新时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;


}

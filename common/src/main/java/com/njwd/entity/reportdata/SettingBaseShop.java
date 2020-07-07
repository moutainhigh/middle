package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 设置模块 实体类 wd_setting_base_shop
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class SettingBaseShop {

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
	 * 门店面积
	 */
	private BigDecimal shopArea;

	/**
	 * 累计利润
	 */
	private BigDecimal addProfit;

	/**
	 * 开业时间
	 */
	private Date openingDate;

	/**
	 * 闭店时间
	 */
	private Date shutdownDate;

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

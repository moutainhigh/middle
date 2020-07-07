package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 设置 异动工资 实体类
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class SettingChange {

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
	 *	异动工资
	 */
	private BigDecimal money;

	/**
	 *	有效期
	 */
	private Integer periodYearNum;

	/**
	 *  状态
	 */
	private Integer status;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}

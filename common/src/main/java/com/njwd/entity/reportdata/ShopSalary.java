package com.njwd.entity.reportdata;

import com.njwd.common.Constant;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 薪酬分析 实体类
 * @Date 2020/3/30 15:00
 * @Author 郑勇浩
 */
@Data
public class ShopSalary {

	/**
	 * @Description 品牌ID
	 */
	private String brandId;

	/**
	 * @Description 品牌名称
	 */
	private String brandName;

	/**
	 * @Description 区域ID
	 */
	private String regionId;

	/**
	 * @Description 区域名称
	 */
	private String regionName;

	/**
	 * @Description 门店ID
	 */
	private String shopId;

	/**
	 * @Description 门店名称
	 */
	private String shopName;

	/**
	 * @Description 实发工资
	 */
	private BigDecimal actualSalary = new BigDecimal(Constant.Number.ZERO);

	/**
	 * @Description 应发工资
	 */
	private BigDecimal grossSalary = new BigDecimal(Constant.Number.ZERO);

	/**
	 * @Description 社保
	 */
	private BigDecimal socialSalary = new BigDecimal(Constant.Number.ZERO);

	/**
	 * @Description 异动工资
	 */
	private BigDecimal abnormalSalary = new BigDecimal(Constant.Number.ZERO);

	/**
	 * @Description 合计
	 */
	private BigDecimal totalSalary = new BigDecimal(Constant.Number.ZERO);

	/**
	 * @Description 人数
	 */
	private Integer employeeNum = Constant.Number.ZERO;

	/**
	 * @Description 人均
	 */
	private BigDecimal perPerson = new BigDecimal(Constant.Number.ZERO);

	/**
	 * @Description 收入
	 */
	private BigDecimal income = new BigDecimal(Constant.Number.ZERO);

	/**
	 * @Description 人工成本占收入比
	 */
	private BigDecimal mix = new BigDecimal(Constant.Number.ZERO);

	/**
	 * 企业ID
	 */
	private String enteId;
}

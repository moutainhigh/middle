package com.njwd.entity.reportdata;

import com.njwd.common.Constant;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 人均创利 实体类
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class EmployeeProfit {

	/**
	 * 企业ID
	 */
	private String enteId;
	/**
	 * 品牌ID
	 */
	private String brandId;

	/**
	 * 品牌
	 */
	private String brandName;


	/**
	 * 区域ID
	 */
	private String regionId;

	/**
	 * 区域
	 */
	private String regionName;

	/**
	 * 门店ID
	 */
	private String shopId;

	/**
	 * 门店
	 */
	private String shopName;

	/**
	 * 利润额
	 */
	private BigDecimal amountBalance = new BigDecimal(Constant.Number.ZERO);

	/**
	 * 员工人数
	 */
	private Integer employeeNum = Constant.Number.ZERO;

	/**
	 * 服务人员
	 */
	private Integer waiterNum = Constant.Number.ZERO;

	/**
	 * 厨务线人员
	 */
	private Integer kitchenNum = Constant.Number.ZERO;

	/**
	 * 门店人均创利（元/人）
	 */
	private BigDecimal employeePerAmount = new BigDecimal(Constant.Number.ZERO);

	/**
	 * 服务线人员人均创利（元/人）
	 */
	private BigDecimal waiterPerAmount = new BigDecimal(Constant.Number.ZERO);

	/**
	 *
	 * 厨务线人员人均创利（元/人）
	 */
	private BigDecimal kitchenPerAmount = new BigDecimal(Constant.Number.ZERO);
}

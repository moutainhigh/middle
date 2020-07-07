package com.njwd.entity.reportdata.vo;

import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.ShopSalary;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 薪酬分析 Vo
 * @Date 2020/3/30 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShopSalaryVo extends ShopSalary {

	/**
	 * 列类型
	 */
	private String type = ReportDataConstant.Finance.TYPE_SHOP;
}

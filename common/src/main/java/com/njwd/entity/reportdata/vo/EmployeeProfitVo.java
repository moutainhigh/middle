package com.njwd.entity.reportdata.vo;

import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.EmployeeProfit;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description 人均创利 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EmployeeProfitVo extends EmployeeProfit {

	/**
	 * 列类型
	 */
	private String type = ReportDataConstant.Finance.TYPE_SHOP;

}

package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Description 薪酬分析 Vo
 * @Date 2020/3/30 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShopSalaryDto extends BaseQueryDto {

	/**
	 * 开始查询年月
	 */
	private Integer startYearNum;

	/**
	 * 结束查询年月
	 */
	private Integer endYearNum;

	/**
	 * 以下为 Excel导出用
	 */
	private String modelType;

	private String menuName;

	private String shopTypeName;

	private String orgTree;

	private String type;

	@Override
	public void setBeginDate(Date beginDate) {
		if (beginDate == null) {
			return;
		}
		this.setStartYearNum(DateUtils.getPeriodYearNum(DateUtils.format(beginDate, DateUtils.PATTERN_DAY)));
		super.setBeginDate(beginDate);
	}

	@Override
	public void setEndDate(Date endDate) {
		if (endDate == null) {
			return;
		}
		this.setEndYearNum(DateUtils.getPeriodYearNum(DateUtils.format(endDate, DateUtils.PATTERN_DAY)));
		super.setEndDate(endDate);
	}

}

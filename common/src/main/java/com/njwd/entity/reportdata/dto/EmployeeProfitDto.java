package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 人均创利 Dto
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EmployeeProfitDto extends BaseQueryDto {

	/**
	 * @Description reportId
	 */
	private Integer reportId;

	/**
	 * 查询年月
	 */
	private Integer periodYearNum;

	/**
	 * 员工类型
	 */
	private String employeeType;

	/**
	 * 以下为 Excel导出用
	 */
	private String modelType;

	private String menuName;

	private String shopTypeName;

	private String orgTree;

	private String type;
}

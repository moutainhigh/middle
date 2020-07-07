package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 会员数量统计
 * @Author: shenhf
 * @Date: 2020/2/14 11:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberNumAnalysisDto extends BaseQueryDto implements Serializable {

	/**
	 * 去年
	 */
	private Date lastYearDate;

	/**
	 * 上期
	 */
	private Date priorDate;

	/**
	 * 以下为 Excel导出用
	 */
	private String modelType;

	private String menuName;

	private String shopTypeName;

	private String orgTree;

	private String type;
}

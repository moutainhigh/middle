package com.njwd.entity.reportdata.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 会员数量统计报表
 * @Author: shenhf
 * @Date: 2020/2/14 10:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberNumAnalysisVo extends BaseScopeOfQueryType implements Serializable {
	private static final long serialVersionUID = 3425356103255207965L;
	/**
	 * 会员数量
	 */
	private Integer memberNum = 0;
	/**
	 * 去年同期
	 */
	private Integer lastYearNum = 0;
	/**
	 * 上期
	 */
	private Integer priorNum = 0;
	/**
	 * 同比
	 */
	private BigDecimal overYear = new BigDecimal(0.00);
	/**
	 * 环比
	 */
	private BigDecimal linkRatio = new BigDecimal(0.00);
	/**
	 * 本期新增
	 */
	private Integer memberAddNum = 0;
	/**
	 * 本期减少
	 */
	private Integer memberLowerNum = 0;


}

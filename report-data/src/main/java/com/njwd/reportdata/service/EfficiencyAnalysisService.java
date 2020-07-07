package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.EmployeeProfitDto;
import com.njwd.entity.reportdata.vo.EmployeeProfitVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 人均人效分析 service
 * @Author LuoY 郑勇浩
 * @Date 2019/11/20
 */
public interface EfficiencyAnalysisService {

	/**
	 * @Description 人均创利分析列表查询
	 * @Author 郑勇浩
	 * @Data 2020/3/23 17:00
	 * @Param
	 */
	List<EmployeeProfitVo> findEmployeeProfitReport(EmployeeProfitDto employeeProfitDto);

	/**
	 * @Description 导出人均创利分析列表
	 * @Author 郑勇浩
	 * @Data 2020/3/23 18:22
	 * @Param [response, param]
	 */
	void exportEmployeeProfitReport(HttpServletResponse response, EmployeeProfitDto param);

}

package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.SalaryAnalysisDto;
import com.njwd.entity.reportdata.dto.ShopSalaryDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.CompanyVo;
import com.njwd.entity.reportdata.vo.ShopSalaryVo;
import com.njwd.entity.reportdata.vo.WageShareAnalysisVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 薪酬分析
 * @Author LuoY
 * @Date 2019/11/20
 */
public interface SalaryAnalysisService {

	List<WageShareAnalysisVo> wageShareAnalysis(SalaryAnalysisDto queryDto);

	/**
	 * @Description 薪酬分析(带社保)
	 * @Author 郑勇浩
	 * @Data 2020/4/14 15:01
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.ShopSalaryVo>
	 */
	List<ShopSalaryVo> findSampleShopSalaryList(ShopSalaryDto param);

	/**
	 * @Description 薪酬分析
	 * @Author 郑勇浩
	 * @Data 2020/3/30 14:52
	 * @Param [queryDto]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.ShopSalaryVo>
	 */
	List<ShopSalaryVo> findShopSalaryVoList(ShopSalaryDto param);

	/**
	 * @Description 导出薪酬分析
	 * @Author 郑勇浩
	 * @Data 2020/3/31 16:15
	 * @Param [response, param]
	 */
	void exportShopSalaryVoList(HttpServletResponse response, ShopSalaryDto param);

	void exportWageShareAnalysis(HttpServletResponse response, ExcelExportDto queryDto);

}

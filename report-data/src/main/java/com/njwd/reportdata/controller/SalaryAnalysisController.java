package com.njwd.reportdata.controller;

import com.njwd.entity.reportdata.dto.SalaryAnalysisDto;
import com.njwd.entity.reportdata.dto.ShopSalaryDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.ShopSalaryVo;
import com.njwd.entity.reportdata.vo.WageShareAnalysisVo;
import com.njwd.reportdata.service.SalaryAnalysisService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 薪酬分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "salaryAnalysisController", tags = "薪酬分析")
@RestController
@RequestMapping("salaryAnalysis")
public class SalaryAnalysisController extends BaseController {

	@Resource
	private SalaryAnalysisService salaryAnalysisService;

	@ApiOperation(value = "工资占销分析表", notes = "工资占销分析表")
	@RequestMapping("wageShareAnalysis")
	public Result<List<WageShareAnalysisVo>> wageShareAnalysis(@RequestBody SalaryAnalysisDto queryDto) {
		FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
				queryDto.getEndDate(), queryDto.getShopIdList(),queryDto.getDateType());
		return ok(salaryAnalysisService.wageShareAnalysis(queryDto));
	}

	/**
	 * @Description 薪酬分析
	 * @Author 郑勇浩
	 * @Data 2020/3/30 14:56
	 * @Param [param]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.ShopSalaryVo>>
	 */
	@ApiOperation(value = "薪酬分析", notes = "薪酬分析")
	@RequestMapping("findShopSalaryVoList")
	public Result<List<ShopSalaryVo>> findShopSalaryVoList(@RequestBody ShopSalaryDto param) {
		FastUtils.checkParams(param.getShopIdList(), param.getShopTypeIdList(), param.getBeginDate(), param.getEndDate());
		param.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		return ok(salaryAnalysisService.findShopSalaryVoList(param));
	}

	/**
	 * @Description 导出薪酬分析
	 * @Author 郑勇浩
	 * @Data 2020/3/30 14:56
	 * @Param [param]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.ShopSalaryVo>>
	 */
	@ApiOperation(value = "导出薪酬分析", notes = "导出薪酬分析")
	@RequestMapping("exportShopSalaryVoList")
	public void exportShopSalaryVoList(HttpServletResponse response, @RequestBody ShopSalaryDto param) {
		FastUtils.checkParams(param.getShopIdList(), param.getShopTypeIdList(), param.getBeginDate(), param.getEndDate());
		param.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		salaryAnalysisService.exportShopSalaryVoList(response, param);
	}

	@ApiOperation(value = "导出工资占销分析表", notes = "导出工资占销分析表")
	@RequestMapping("exportWageShareAnalysis")
	public void exportWageShareAnalysis(HttpServletResponse response, @RequestBody ExcelExportDto queryDto) {
		FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
				queryDto.getEndDate(), queryDto.getShopIdList());
		salaryAnalysisService.exportWageShareAnalysis(response, queryDto);
	}

}

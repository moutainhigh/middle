package com.njwd.reportdata.controller;

import com.njwd.entity.reportdata.dto.EmployeeProfitDto;
import com.njwd.entity.reportdata.vo.EmployeeProfitVo;
import com.njwd.reportdata.service.EfficiencyAnalysisService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 人均人效分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "efficiencyAnalysisController", tags = "人均人效分析")
@RestController
@RequestMapping("efficiencyAnalysis")
public class EfficiencyAnalysisController extends BaseController {

	@Resource
	private EfficiencyAnalysisService efficiencyAnalysisService;

	/**
	 * @Description 人均创利分析列表查询
	 * @Author 郑勇浩
	 * @Data 2020/3/23 15:37
	 * @Param [excelExportDto, response]
	 */
	@ApiOperation(value = "人均创利分析", notes = "人均创利分析")
	@RequestMapping("findEmployeeProfitReport")
	public Result<List<EmployeeProfitVo>> findEmployeeProfitReport(@RequestBody EmployeeProfitDto param) {
		FastUtils.checkParams(param.getShopIdList(), param.getShopTypeIdList(), param.getBeginDate());
		param.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("2002595340009472");
		return ok(efficiencyAnalysisService.findEmployeeProfitReport(param));
	}

	/**
	 * @Description 导出人均创利分析列表
	 * @Author 郑勇浩
	 * @Data 2020/3/11 17:50
	 * @Param [response, posCashPayDto]
	 */
	@PostMapping("exportEmployeeProfitReport")
	public void exportEmployeeProfitReport(HttpServletResponse response, @RequestBody EmployeeProfitDto param) {
		FastUtils.checkParams(param.getBeginDate(), param.getEndDate());
		param.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		efficiencyAnalysisService.exportEmployeeProfitReport(response, param);
	}

}

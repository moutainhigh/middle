package com.njwd.reportdata.controller;

import com.njwd.entity.reportdata.CostAggregation;
import com.njwd.entity.reportdata.dto.BalanceDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.CostAggregationVo;
import com.njwd.entity.reportdata.vo.FinCashFlowReportTableVo;
import com.njwd.entity.reportdata.vo.FinProfitReportTableVo;
import com.njwd.entity.reportdata.vo.FinReportTableVo;
import com.njwd.reportdata.service.FinancialReportService;
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
 * @Description: 财务报表
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "financialReportController",tags = "财务报表")
@RestController
@RequestMapping("financialReport")
public class FinancialReportController extends BaseController {

    @Resource
    private FinancialReportService financialReportService;

    /**
     * 资产负债表
     * @Author lj
     * @Date:9:13 2019/12/26
     * @param balanceDto
     * @return com.njwd.support.Result<java.util.List<BalanceVo>>
     **/
    @ApiOperation(value = "资产负债表", notes = "资产负债表")
    @RequestMapping("findBalanceReport")
    public Result<FinReportTableVo> findBalanceReport(@RequestBody BalanceDto balanceDto){
        return ok(financialReportService.findBalanceReport(balanceDto));
    }


    /**
    * Description: 利润表
    * @author: LuoY
    * @date: 2020/2/17 0017 10:48
    * @param:[balanceDto]
    * @return:com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.FinProfitReportTableVo>>
    */
    @ApiOperation(value = "利润表", notes = "利润表")
    @RequestMapping("findProfitReport")
    public Result<List<FinProfitReportTableVo>> findProfitReport(@RequestBody BalanceDto balanceDto){
        return ok(financialReportService.findProfitReport(balanceDto));
    }

    /**
     * 现金流量表
     * @Author lj
     * @Date:9:13 2019/12/26
     * @param balanceDto
     * @return com.njwd.support.Result<java.util.List<BalanceVo>>
     **/
    @ApiOperation(value = "现金流量表", notes = "现金流量表")
    @RequestMapping("findCashFlowReport")
    public Result<List<FinCashFlowReportTableVo>> findCashFlowReport(@RequestBody BalanceDto balanceDto){
        return ok(financialReportService.findCashFlowReport(balanceDto));
    }

    /**
    * Description: 费用汇总表
    * @author: LuoY
    * @date: 2020/2/24 0024 14:24
    * @param:[balanceDto]
    * @return:com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.FinProfitReportTableVo>>
    */
    @ApiOperation(value = "费用汇总表", notes = "费用汇总表")
    @RequestMapping("findCostAggregation")
    public Result<List<CostAggregationVo>> findCostAggregation(@RequestBody BalanceDto balanceDto){
        return ok(financialReportService.findCostAggregationReport(balanceDto));
    }

    /**
     * 导出现金流量表
     * @Author lj
     * @Date:9:09 2020/2/12
     * @param excelExportDto, response]
     * @return void
     **/
    @RequestMapping("exportCashExcel")
    public void exportCashExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        financialReportService.exportCashExcel(excelExportDto,response);
    }

    /**
     * 导出资产负债表
     * @Author lj
     * @Date:9:09 2020/2/12
     * @param excelExportDto, response]
     * @return void
     **/
    @RequestMapping("exportBalanceExcel")
    public void exportBalanceExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        financialReportService.exportBalanceExcel(excelExportDto,response);
    }

    /**
     * 导出利润表
     * @author: LuoY
     * @param excelExportDto
     * @param response
     * @Date 2020/2/17 10:30
     */
    @RequestMapping("exportProfitExcel")
    public void exportProfitExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkNullOrEmpty(excelExportDto.getBeginDate(),excelExportDto.getMenuName(),excelExportDto.getModelType());
        financialReportService.exportProfitExcel(excelExportDto,response);
    }

    /** 
    * @Description: 导出费用汇总表
    * @Param: [excelExportDto, response]
    * @return: void 
    * @Author: LuoY
    * @Date: 2020/3/2 11:50
    */ 
    @RequestMapping("exportCostReport")
    public void exportCostReport(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response){
        FastUtils.checkNullOrEmpty(excelExportDto.getBeginDate(),excelExportDto.getMenuName(),excelExportDto.getModelType());
        financialReportService.exportCostExcel(excelExportDto,response);
    }
}

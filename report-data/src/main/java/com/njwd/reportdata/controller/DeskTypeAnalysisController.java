package com.njwd.reportdata.controller;

import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.DeskTypeAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo;
import com.njwd.reportdata.service.DeskTypeAnalysisService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/18 14:40
 * @Description 台型分析  台型分析（同比） 台型分析（环比）
 */
@Api(value = "deskTypeAnalysisController",tags = "台型分析(包含同比、环比)")
@RestController
@RequestMapping("deskTypeAnalysis")
public class DeskTypeAnalysisController extends BaseController {

    @Autowired
    private DeskTypeAnalysisService deskTypeAnalysisService;

    /**
     * @Author ZhuHC
     * @Date  2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析报表
     */
    @ApiOperation(value = "查询台型分析报表", notes="根据机构、时间获取台型分析")
    @PostMapping("analysisReport")
    public Result<Map<String, List<DeskTypeAnalysisVo>>> analysisReport (@RequestBody DeskTypeAnalysisDto deskTypeAnalysisDto)
    {
        checkParams(deskTypeAnalysisDto);
        return ok(deskTypeAnalysisService.findDeskTypeAnalysisReport(deskTypeAnalysisDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析报表 同比
     */
    @ApiOperation(value = "查询台型分析报表-同比", notes="根据机构、时间获取台型分析")
    @PostMapping("analysisReportCompareLastYear")
    public Result<Map<String, List<DeskTypeAnalysisVo>>> analysisReportCompareLastYear (@RequestBody DeskTypeAnalysisDto deskTypeAnalysisDto)
    {
        checkParams(deskTypeAnalysisDto);
        return ok(deskTypeAnalysisService.findDeskTypeAnalysisReportCompareLastYear(deskTypeAnalysisDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 手机-台型分析报表 同比
     */
    @ApiOperation(value = "手机-查询台型分析报表-同比", notes="根据机构、时间获取台型分析")
    @PostMapping("analysisReportCompareLastYearApp")
    public Result<Map<String, List<DeskTypeAnalysisVo>>> analysisReportCompareLastYearApp (@RequestBody DeskTypeAnalysisDto deskTypeAnalysisDto)
    {
        checkParams(deskTypeAnalysisDto);
        return ok(deskTypeAnalysisService.findDeskTypeAnalysisReportCompareLastYearApp(deskTypeAnalysisDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析报表 环比
     */
    @ApiOperation(value = "查询台型分析报表-环比", notes="根据机构、时间获取台型分析")
    @PostMapping("analysisReportCompareLastPeriod")
    public Result<Map<String, List<DeskTypeAnalysisVo>>> analysisReportCompareLastPeriod (@RequestBody DeskTypeAnalysisDto deskTypeAnalysisDto)
    {
        checkParams(deskTypeAnalysisDto);
        FastUtils.checkParams(deskTypeAnalysisDto.getDateType());
        return ok(deskTypeAnalysisService.findDeskTypeAnalysisReportCompareLastPeriod(deskTypeAnalysisDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 手机-台型分析报表 环比
     */
    @ApiOperation(value = "手机-查询台型分析报表-环比", notes="根据机构、时间获取台型分析")
    @PostMapping("analysisReportCompareLastPeriodApp")
    public Result<Map<String, List<DeskTypeAnalysisVo>>> analysisReportCompareLastPeriodApp (@RequestBody DeskTypeAnalysisDto deskTypeAnalysisDto)
    {
        checkParams(deskTypeAnalysisDto);
        FastUtils.checkParams(deskTypeAnalysisDto.getDateType());
        return ok(deskTypeAnalysisService.findDeskTypeAnalysisReportCompareLastPeriodApp(deskTypeAnalysisDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/5 9:22
     * @Param [deskTypeAnalysisDto, response]
     * @return void
     * @Description 台型分析报表导出
     */
    @ApiOperation(value = "台型分析报表导出", notes = "台型分析报表导出")
    @RequestMapping("exportDeskAnalysisExcel")
    public void exportDeskAnalysisExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkParams(excelExportDto.getEnteId(),excelExportDto.getBeginDate(),excelExportDto.getEndDate(),excelExportDto.getShopIdList());
        deskTypeAnalysisService.exportDeskAnalysisExcel(excelExportDto,response);
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/5 9:23
     * @Param [deskTypeAnalysisDto, response]
     * @return void
     * @Description 台型分析报表（同比/ 环比）导出
     */
    @ApiOperation(value = "台型分析报表（同比）导出", notes = "台型分析报表（同比）导出")
    @RequestMapping("exportDeskAnalysisLastYearExcel")
    public void exportDeskAnalysisLastYearExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkParams(excelExportDto.getEnteId(),excelExportDto.getBeginDate(),excelExportDto.getEndDate(),excelExportDto.getShopIdList());
        deskTypeAnalysisService.exportDeskAnalysisLastExcel(excelExportDto,response, ReportDataConstant.ExcelExportInfo.DESKANALYSISLASTYEARXLS);
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/5 9:23
     * @Param [deskTypeAnalysisDto, response]
     * @return void
     * @Description 台型分析报表（环比）导出
     */
    @ApiOperation(value = "台型分析报表（环比）导出", notes = "台型分析报表（环比）导出")
    @RequestMapping("exportDeskAnalysisLastPeriodExcel")
    public void exportDeskAnalysisLastPeriodExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkParams(excelExportDto.getEnteId(),excelExportDto.getBeginDate(),excelExportDto.getEndDate(),excelExportDto.getShopIdList());
        deskTypeAnalysisService.exportDeskAnalysisLastExcel(excelExportDto,response,ReportDataConstant.ExcelExportInfo.DESKANALYSISLASTPERIODXLS);
    }


    /**
     * @Author ZhuHC
     * @Date  2019/11/21 16:55
     * @Param [deskTypeAnalysisDto]
     * @return void
     * @Description 校验必填参数
     */
    private void checkParams(@RequestBody DeskTypeAnalysisDto deskTypeAnalysisDto) {
        FastUtils.checkParams(deskTypeAnalysisDto.getEnteId());
        FastUtils.checkParams(deskTypeAnalysisDto.getBeginDate());
        FastUtils.checkParams(deskTypeAnalysisDto.getEndDate());
        deskTypeAnalysisDto.setStartDate(deskTypeAnalysisDto.getBeginDate());
    }
}

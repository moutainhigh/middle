package com.njwd.reportdata.controller;

import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.BalanceDto;
import com.njwd.entity.reportdata.dto.BusinessReportDayDto;
import com.njwd.entity.reportdata.dto.DeskTypeAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.dto.querydto.PreparationAnalysisDto;
import com.njwd.entity.reportdata.vo.BusinessReportDayItemVo;
import com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo;
import com.njwd.entity.reportdata.vo.PreparationAnalysisVo;
import com.njwd.entity.reportdata.vo.fin.FinCostCompareVo;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;
import com.njwd.reportdata.service.BusinessAnalysisService;
import com.njwd.reportdata.service.DeskTypeAnalysisService;
import com.njwd.reportdata.service.PreparationManagementService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description: 筹建经营
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "preparationManagementController", tags = "筹建经营")
@RestController
@RequestMapping("preparationManagement")
public class PreparationManagementController extends BaseController {
    @Autowired
    private PreparationManagementService preparationManagementService;

    @Autowired
    private BusinessAnalysisService businessAnalysisService;

    @Autowired
    private DeskTypeAnalysisService deskTypeAnalysisService;

    @ApiOperation(value = "租金占销分析", notes = "租金占销分析")
    @PostMapping("rentAccountedFor")
    public Result rentAccountedFor(@RequestBody FinQueryDto queryDto) {
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginTime(),
                queryDto.getEndTime(), queryDto.getShopIdList());
        List<FinRentAccountedForVo> list = preparationManagementService.findRentAccountedFor(queryDto);


        return ok(list);
    }


    /**
     * @description: 筹建经营分析表
     * @param: [queryDto]
     * @return: BusinessReportDayItemVo
     * @author: 周鹏
     * @date: 2020/2/11 11:37
     */
    @ApiOperation(value = "筹建经营分析表", notes = "筹建经营分析表")
    @PostMapping("findPreparationAnalysis")
    public Result<List<BusinessReportDayItemVo>> findPreparationAnalysis(@RequestBody BusinessReportDayDto queryDto) {
        FastUtils.checkNull(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType(), queryDto.getShopIdList());
        List<BusinessReportDayItemVo> list = businessAnalysisService.findBusinessReportDay(queryDto, ReportDataConstant.ReportItemReportId.PREPARATION_MANAGE_ANALYSIS);
        return ok(list);
    }

    /**
     * @Description: 导出筹建经营分析表
     * @Param: [queryDto]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/2/11 11:37
     */
    @ApiOperation(value = "筹建经营分析表导出", notes = "筹建经营分析表导出")
    @PostMapping("exportPreparationAnalysis")
    public void exportPreparationAnalysis(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkNull(excelExportDto.getBeginDate(), excelExportDto.getEndDate(), excelExportDto.getDateType(),
                excelExportDto.getShopIdList(), excelExportDto.getModelType(), excelExportDto.getMenuName());
        BusinessReportDayDto queryDto = new BusinessReportDayDto();
        FastUtils.copyProperties(excelExportDto, queryDto);
        List<BusinessReportDayItemVo> list = businessAnalysisService.findBusinessReportDay(queryDto, ReportDataConstant.ReportItemReportId.PREPARATION_MANAGE_ANALYSIS);
        preparationManagementService.exportPreparationAnalysis(list, excelExportDto, response);
    }

    /**
     * @return void
     * @Author liBao
     * @Date 2020/2/11 15:21
     * @Param [baseShopDto, response]
     * @Description 租金占销导出
     */
    @RequestMapping("exportExcel")
    public void exportExcel(@RequestBody ExcelExportDto finQueryDto, HttpServletResponse response) {
        FastUtils.checkParams(finQueryDto.getEnteId());
        preparationManagementService.exportExcel(finQueryDto, response);
    }

    /**
     * @Description: 查询筹建台型分析报表
     * @Param: [deskTypeAnalysisDto]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/2/21 11:37
     */
    @ApiOperation(value = "查询筹建台型分析报表", notes = "根据机构、时间获取台型分析")
    @PostMapping("findPreparationDeskTypeAnalysis")
    public Result<Map<String, List<DeskTypeAnalysisVo>>> findPreparationDeskTypeAnalysis(@RequestBody DeskTypeAnalysisDto deskTypeAnalysisDto) {
        FastUtils.checkParams(deskTypeAnalysisDto.getBeginDate());
        FastUtils.checkParams(deskTypeAnalysisDto.getEndDate());
        FastUtils.checkParams(deskTypeAnalysisDto.getDateType());
        FastUtils.checkParams(deskTypeAnalysisDto.getShopIdList());
        deskTypeAnalysisDto.setStartDate(deskTypeAnalysisDto.getBeginDate());
        return ok(deskTypeAnalysisService.findDeskTypeAnalysisReportCompareLastYear(deskTypeAnalysisDto));
    }

    /**
     * @Description: 导出筹建台型分析报表
     * @Param: [excelExportDto]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/3/11 11:37
     */
    @ApiOperation(value = "筹建台型分析报表导出", notes = "筹建台型分析报表导出")
    @PostMapping("exportPreparationDeskTypeAnalysis")
    public void exportDeskAnalysisLastYearExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkParams(excelExportDto.getEnteId(), excelExportDto.getBeginDate(), excelExportDto.getEndDate(),
                excelExportDto.getDateType(), excelExportDto.getShopIdList(), excelExportDto.getModelType(), excelExportDto.getMenuName());
        deskTypeAnalysisService.exportDeskAnalysisLastExcel(excelExportDto, response, ReportDataConstant.ExcelExportInfo.DESKANALYSISLASTYEARXLS);
    }
}

package com.njwd.reportdata.controller;

import com.njwd.annotation.NoLogin;
import com.njwd.entity.reportdata.dto.StaffAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.reportdata.service.StaffAnalysisService;
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
 * @Description: 人资分析-人员分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "staffAnalysisController",tags = "人资分析-人员分析")
@RestController
@RequestMapping("staffAnalysis")
public class StaffAnalysisController extends BaseController {

    @Resource
    private StaffAnalysisService staffAnalysisService;

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 15:09
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.StaffAnalysisVo>>
     * @Description 在职员工分析表
     */
    @ApiOperation(value = "在职员工分析表", notes = "在职员工分析表")
    @RequestMapping("staffOnJobAnalysis")
    public Result<List<StaffAnalysisVo>> staffOnJobAnalysis(@RequestBody StaffAnalysisDto queryDto){
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
                queryDto.getEndDate(), queryDto.getShopIdList());
        return ok(staffAnalysisService.staffOnJobAnalysis(queryDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 15:09
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.StaffAnalysisVo>>
     * @Description 离职员工分析表
     */
    @ApiOperation(value = "离职员工分析表", notes = "离职员工分析表")
    @RequestMapping("separatedStaffAnalysis")
    public Result<List<StaffAnalysisVo>> separatedStaffAnalysis(@RequestBody StaffAnalysisDto queryDto){
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
                queryDto.getEndDate(), queryDto.getShopIdList());
        return ok(staffAnalysisService.separatedStaffAnalysis(queryDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/23 17:20
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.StaffWorkHoursVo>>
     * @Description 人员工作时长分析表
     */
    @ApiOperation(value = "人员工作时长分析表", notes = "人员工作时长分析表")
    @RequestMapping("staffWorkHoursAnalysis")
    public Result<List<StaffWorkHoursVo>> staffWorkHoursAnalysis(@RequestBody StaffAnalysisDto queryDto){
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
                queryDto.getEndDate(), queryDto.getShopIdList());
        return ok(staffAnalysisService.staffWorkHoursAnalysis(queryDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/24 10:43
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.EfficiencyPerCapitaVo>>
     * @Description 人均效率分析表
     */
    @ApiOperation(value = "人均效率分析表", notes = "人均效率分析表")
    @RequestMapping("efficiencyPerCapitaAnalysis")
    public Result<List<EfficiencyPerCapitaVo>> efficiencyPerCapitaAnalysis(@RequestBody StaffAnalysisDto queryDto){
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
                queryDto.getEndDate(), queryDto.getShopIdList());
        return ok(staffAnalysisService.efficiencyPerCapitaAnalysis(queryDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/27 10:15
     * @Param [queryDto, response]
     * @return void
     * @Description 人均效率分析表导出
     */
    @ApiOperation(value = "人均效率分析表导出", notes = "人均效率分析表导出")
    @RequestMapping("exportEfficiencyPerAnalysis")
    public void exportEfficiencyPerAnalysis(@RequestBody ExcelExportDto queryDto, HttpServletResponse response) {
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
                queryDto.getEndDate(), queryDto.getShopIdList());
        staffAnalysisService.exportEfficiencyPerAnalysis(queryDto,response);
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 15:09
     * @Param [excelExportDto, response]
     * @return void
     * @Description 人员工作时长分析表导出
     */
    @ApiOperation(value = "人员工作时长分析表导出", notes = "人员工作时长分析表导出")
    @RequestMapping("exportStaffWorkHoursAnalysis")
    public void exportStaffWorkHoursAnalysis(@RequestBody ExcelExportDto queryDto, HttpServletResponse response) {
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
                queryDto.getEndDate(), queryDto.getShopIdList());
        staffAnalysisService.exportStaffWorkHoursAnalysis(queryDto,response);
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 15:09
     * @Param [excelExportDto, response]
     * @return void
     * @Description 在职员工分析表导出
     */
    @ApiOperation(value = "在职员工分析表导出", notes = "在职员工分析表导出")
    @RequestMapping("exportStaffOnJobAnalysisExcel")
    public void exportStaffOnJobAnalysisExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(), excelExportDto.getBeginDate(),
                excelExportDto.getEndDate(), excelExportDto.getShopIdList());
        staffAnalysisService.exportStaffOnJobAnalysisExcel(excelExportDto,response);
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 15:09
     * @Param [excelExportDto, response]
     * @return void
     * @Description 离职员工分析表导出
     */
    @ApiOperation(value = "离职员工分析表导出", notes = "离职员工分析表导出")
    @RequestMapping("exportSeparatedStaffAnalysisExcel")
    public void exportSeparatedStaffAnalysisExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(), excelExportDto.getBeginDate(),
                excelExportDto.getEndDate(), excelExportDto.getShopIdList());
        staffAnalysisService.exportSeparatedStaffAnalysisExcel(excelExportDto,response);
    }

    /** 人效分析
     * @Author ljc
     * @Date  2020/3/25
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.EffectAnalysisVo>>
     * @Description 人效分析表
     */
    @ApiOperation(value = "人效分析表", notes = "人效分析表")
    @RequestMapping("findEffectAnalysis")
    @NoLogin
    public Result<List<EffectAnalysisVo>> findEffectAnalysis(@RequestBody StaffAnalysisDto queryDto){
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
                queryDto.getEndDate(), queryDto.getShopIdList());
        return ok(staffAnalysisService.findEffectAnalysis(queryDto));
    }

    /** 人效分析
     * @Author ljc
     * @Date  2020/3/26
     * @Param [queryDto]
     * @Description 人效分析表
     */
    @ApiOperation(value = "人效分析导出", notes = "人效分析表导出")
    @RequestMapping("exportEffectAnalysis")
    public void exportEffectAnalysis(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response){
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(), excelExportDto.getBeginDate(),
                excelExportDto.getEndDate(), excelExportDto.getShopIdList());
        staffAnalysisService.exportEffectAnalysis(excelExportDto,response);
    }

    /** 品牌奖金
     * @Author ljc
     * @Date  2020/4/7
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.BrandBonusVo>>
     * @Description 人效分析表
     */
    @ApiOperation(value = "品牌奖金", notes = "品牌奖金")
    @RequestMapping("findBrandBonusAnalysis")
    public Result<List<BrandBonusVo>> findBrandBonusAnalysis(@RequestBody StaffAnalysisDto queryDto){
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
                queryDto.getEndDate(), queryDto.getShopIdList());
        return ok(staffAnalysisService.findBrandBonusAnalysis(queryDto));
    }

    /** 品牌奖金导出
     * @Author ljc
     * @Date  2020/4/7
     * @Param [queryDto]
     * @Description 品牌奖金导出
     */
    @ApiOperation(value = "品牌奖金导出", notes = "品牌奖金导出")
    @RequestMapping("exportBrandBonusAnalysis")
    public void exportBrandBonusAnalysis(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response){
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(), excelExportDto.getBeginDate(),
                excelExportDto.getEndDate(), excelExportDto.getShopIdList());
        staffAnalysisService.exportBrandBonusAnalysis(excelExportDto,response);
    }
}

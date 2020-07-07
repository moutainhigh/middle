package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.StaffAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/11/20
 */
public interface StaffAnalysisService {

    /**
     * @Author ZhuHC
     * @Date  2020/2/7 17:52
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.StaffOnJobAnalysisVo>
     * @Description 在职员工分析
     */
    List<StaffAnalysisVo> staffOnJobAnalysis(StaffAnalysisDto queryDto);

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 14:28
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.StaffAnalysisVo>
     * @Description 离职人员分析
     */
    List<StaffAnalysisVo> separatedStaffAnalysis(StaffAnalysisDto queryDto);

    /**
     * @Author ZhuHC
     * @Date  2020/3/23 17:20
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.StaffWorkHoursVo>>
     * @Description 人员工作时长分析表
     */
    List<StaffWorkHoursVo> staffWorkHoursAnalysis(StaffAnalysisDto queryDto);

    /**
     * @Author ZhuHC
     * @Date  2020/3/24 10:43
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.EfficiencyPerCapitaVo>>
     * @Description 人均效率分析表
     */
    List<EfficiencyPerCapitaVo> efficiencyPerCapitaAnalysis(StaffAnalysisDto queryDto);


    /**
     * @Author ZhuHC
     * @Date  2020/3/27 10:14
     * @Param [excelExportDto, response]
     * @return void
     * @Description 人均效率分析导出
     */
    void exportEfficiencyPerAnalysis(ExcelExportDto excelExportDto, HttpServletResponse response);
    /**
     * @Author ZhuHC
     * @Date  2020/3/12 15:09
     * @Param [excelExportDto, response]
     * @return void
     * @Description 人员工作时长分析表导出
     */
    void exportStaffWorkHoursAnalysis(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 10:17
     * @Param [excelExportDto, response]
     * @return void
     * @Description 在职人员分析导出
     */
    void exportStaffOnJobAnalysisExcel(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 15:08
     * @Param [excelExportDto, response]
     * @return void
     * @Description 离职人员导出
     */
    void exportSeparatedStaffAnalysisExcel(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * @Author ljc
     * @Date  2020/3/25
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.EffectAnalysisVo>>
     * @Description 人效分析
     */
    List<EffectAnalysisVo> findEffectAnalysis(StaffAnalysisDto queryDto);

    /**
     * @Author ljc
     * @Date  2020/3/26
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.EffectAnalysisVo>>
     * @Description 人效分析导出
     */
    void exportEffectAnalysis(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * @Author ljc
     * @Date  2020/4/7
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.BrandBonusVo>>
     * @Description 品牌奖金
     */
    List<BrandBonusVo> findBrandBonusAnalysis(StaffAnalysisDto queryDto);

    /**
     * @Author ljc
     * @Date  2020/4/7
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.BrandBonusVo>>
     * @Description 品牌奖金导出
     */
    void exportBrandBonusAnalysis(ExcelExportDto excelExportDto, HttpServletResponse response);
}

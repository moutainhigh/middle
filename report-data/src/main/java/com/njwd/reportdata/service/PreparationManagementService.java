package com.njwd.reportdata.service;

import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.reportdata.dto.BusinessReportDayDto;
import com.njwd.entity.reportdata.dto.DeskTypeAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.dto.querydto.PreparationAnalysisDto;
import com.njwd.entity.reportdata.vo.BusinessReportDayItemVo;
import com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo;
import com.njwd.entity.reportdata.vo.PreparationAnalysisVo;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description: 租金占销
 * @Author liBao
 * @Date 2020/2/7
 */
public interface PreparationManagementService {
    /**
     * @param queryDto
     * @return list
     * @description 租金占销
     * @author liBao
     * @date 2020/2/7
     */
    List<FinRentAccountedForVo> findRentAccountedFor(FinQueryDto queryDto);

    /**
     * @Description: 筹建经营分析表
     * @Param: [queryDto]
     * @return: PreparationAnalysisVo
     * @Author: 周鹏
     * @Date: 2020/2/11 11:37
     */
//    List<PreparationAnalysisVo> findPreparationAnalysis(PreparationAnalysisDto queryDto);

    /**
     * @Description: 导出筹建经营分析表
     * @Param: [list]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/2/11 11:37
     */
    void exportPreparationAnalysis(List<BusinessReportDayItemVo> list, ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * @return void
     * @Author liBao
     * @Date 2020/2/11 15:21
     * @Param [baseShopDto, response]
     * @Description 导出
     */
    void exportExcel(ExcelExportDto finQueryDto, HttpServletResponse response);

    /**
     * @Description: 查询筹建台型分析报表
     * @Param: [deskTypeAnalysisDto]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/2/21 11:37
     */
//    Map<String, List<DeskTypeAnalysisVo>> findPreparationDeskTypeAnalysis(DeskTypeAnalysisDto deskTypeAnalysisDto);
}

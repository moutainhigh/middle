package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.DeskTypeAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo;
import com.njwd.poiexcel.TitleEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author ZhuHC
 * @Date  2019/11/18 16:21
 * @Description
 */
public interface DeskTypeAnalysisService {

    /**
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析报表
     */
    Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReport(DeskTypeAnalysisDto deskTypeAnalysisDto);

    /**
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析报表 同比
     */
    Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReportCompareLastYear( DeskTypeAnalysisDto deskTypeAnalysisDto);

    /**
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析报表 同比
     */
    Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReportCompareLastYearApp( DeskTypeAnalysisDto deskTypeAnalysisDto);

    /**
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析报表 环比
     */
    Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReportCompareLastPeriod( DeskTypeAnalysisDto deskTypeAnalysisDto);

    /**
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析报表 环比
     */
    Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReportCompareLastPeriodApp( DeskTypeAnalysisDto deskTypeAnalysisDto);

    /**
     * @Author ZhuHC
     * @Date  2020/3/5 9:22
     * @Param [deskTypeAnalysisDto, response]
     * @return void
     * @Description 台型分析报表导出
     */
    void exportDeskAnalysisExcel(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * @Author ZhuHC
     * @Date  2020/3/5 9:23
     * @Param [deskTypeAnalysisDto, response]
     * @return void
     * @Description 台型分析报表（同比/ 环比）导出
     */
    void exportDeskAnalysisLastExcel(ExcelExportDto excelExportDto, HttpServletResponse response,String exportType);

    void exportMethod(HttpServletResponse response, List<TitleEntity> titleList, List<Map<String, Object>> rowList);
}

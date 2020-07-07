package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.BusinessReportDayDto;
import com.njwd.entity.reportdata.dto.GrossProfitDto;
import com.njwd.entity.reportdata.dto.RealTimeProfitDto;
import com.njwd.entity.reportdata.dto.ShopScoreDto;
import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 经营分析service
 * @Author LuoY
 * @Date 2019/11/20
 */
public interface BusinessAnalysisService {

    /**
     * 查询巡店项目平均評分
     *
     * @param shopScoreDto
     * @return
     */
    List<ShopScoreVo> findPsItemScoreAvg(ShopScoreDto shopScoreDto);

    /**
     * 查询巡店評分對比
     *
     * @param shopScoreDto
     * @return
     */
    ShopScoreVo findScoreContrast(ShopScoreDto shopScoreDto);

    /**
     * 查询巡店評分汇总
     *
     * @param shopScoreDto
     * @return
     */
    ShopScoreVo findScoreSummary(ShopScoreDto shopScoreDto);
    /**
     * @Description: 查询经营日报表
     * @Param: [businessReportDayDto]
     * @return: com.njwd.entity.reportdata.vo.BusinessReportDayVo
     * @Author: LuoY
     * @Date: 2019/12/29 11:40
     */
    List<BusinessReportDayItemVo> findBusinessReportDay(BusinessReportDayDto businessReportDayDto, Integer reportItem);

    /**
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.StatisticsTurnoverRateVo>>
     * @Author ZhuHC
     * @Date 2019/12/30 16:47
     * @Param [baseQueryDto]
     * @Description 翻台率统计表
     */
    List<StatisticsTurnoverRateVo> findStatisticsTurnoverRate(BaseQueryDto baseQueryDto);




    /**
     * @Author ZhuHC
     * @Date  2020/3/2 16:33
     * @Param [baseQueryDto, response]
     * @return void
     * @Description
     */
    void exportStatisticsExcel(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * 门店评分汇总表导出
     * @param excelExportDto
     * @return
     */
    void exportScoreSummary( HttpServletResponse response, ExcelExportDto excelExportDto);

    /**
     * 门店评分对比表导出
     * @param excelExportDto
     * @return
     */
    void exportScoreContrast(HttpServletResponse response, ExcelExportDto excelExportDto);

    /** 
    * @Description: 经营日报表导出
    * @Param: [response, excelExportDto] 
    * @return: void 
    * @Author: LuoY
    * @Date: 2020/3/19 11:22
    */ 
    void exportBusinessReportDay(HttpServletResponse response, ExcelExportDto excelExportDto);

}

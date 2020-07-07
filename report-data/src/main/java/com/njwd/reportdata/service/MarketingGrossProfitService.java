package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.MarketingGrossProfitDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.MarketingGrossProfitVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 * @Author lj
 * @Description 营销活动毛利统计表service
 * @Date:9:48 2020/3/30
 **/
public interface MarketingGrossProfitService {

    /**
     * 营销活动毛利统计表
     *
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.MarketingGrossProfitVo>
     * @Author lj
     * @Date:16:48 2020/3/26
     **/
    List<MarketingGrossProfitVo> findMarketingGrossProfit(MarketingGrossProfitDto queryDto);

    /**
     * 营销活动毛利统计表
     *
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.MarketingGrossProfitVo>
     * @Author lj
     * @Date:16:48 2020/3/26
     **/
    List<MarketingGrossProfitVo> findMarketingGrossProfitNew(MarketingGrossProfitDto queryDto);

    /**
     * 营销活动毛利统计表导出
     * @Author lj
     * @Date:11:03 2020/3/31
     * @param excelExportDto, response]
     * @return void
     **/
    void exportMarketingGrossProfit(ExcelExportDto excelExportDto, HttpServletResponse response);
}

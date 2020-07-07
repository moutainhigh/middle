package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 毛利分析service
 * @Author 周鹏
 * @Date 2020/03/30
 */
public interface GrossProfitService {
    /**
     * 查询毛利分析表
     *
     * @param queryDto
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date 2020/03/30
     */
    List<GrossProfitVo> findGrossProfit(GrossProfitDto queryDto);

    /**
     * 导出毛利分析表
     *
     * @param excelExportDto
     * @param response
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date 2020/03/30
     */
    void exportGrossProfit(ExcelExportDto excelExportDto, HttpServletResponse response);

}

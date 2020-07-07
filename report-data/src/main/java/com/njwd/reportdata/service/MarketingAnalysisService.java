package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.fin.FinSubjectVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/11/20
 */
public interface MarketingAnalysisService {

    /**
     * 营销费用统计
     * @Author lj
     * @Date:14:10 2020/1/14
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinSubjectVo>
     **/
    List<FinSubjectVo> marketingCost(FinQueryDto queryDto);

    /**
     * 导出营销费用统计
     * @Author lj
     * @Date:14:43 2020/3/6
     * @param excelExportDto, response]
     * @return void
     **/
    void exportMarketingCost(ExcelExportDto excelExportDto, HttpServletResponse response);
}

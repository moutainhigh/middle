package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.CostAggregation;
import com.njwd.entity.reportdata.dto.BalanceDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.CostAggregationVo;
import com.njwd.entity.reportdata.vo.FinCashFlowReportTableVo;
import com.njwd.entity.reportdata.vo.FinProfitReportTableVo;
import com.njwd.entity.reportdata.vo.FinReportTableVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 财务报告
 * @Author LuoY
 * @Date 2019/11/20
 */
public interface FinancialReportService {

    /**
     * 资产负债表
     * @Author lj
     * @Date:17:40 2020/1/13
     * @param balanceDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinReportTableVo>
     **/
    FinReportTableVo findBalanceReport(BalanceDto balanceDto);

    /** 
    * @Description: 利润表
    * @Param: [balanceDto] 
    * @return: com.njwd.entity.reportdata.FinProfitReportTable 
    * @Author: LuoY
    * @Date: 2020/2/10 10:49
    */
    List<FinProfitReportTableVo> findProfitReport(BalanceDto balanceDto);

    /**
     * 现金流量表
     * @Author lj
     * @Date:17:40 2020/1/13
     * @param balanceDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinReportTableVo>
     **/
    List<FinCashFlowReportTableVo> findCashFlowReport(BalanceDto balanceDto);

    /**
    * Description: 费用汇总表
    * @author: LuoY
    * @date: 2020/2/26 0026 10:40
    * @param:[balanceDto]
    * @return:java.util.List<com.njwd.entity.reportdata.CostAggregation>
    */
    List<CostAggregationVo> findCostAggregationReport(BalanceDto balanceDto);


    /**
     * 导出现金流量表
     * @Author lj
     * @Date:9:09 2020/2/12
     * @param excelExportDto, response]
     * @return void
     **/
    void exportCashExcel(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * 导出资产负债表
     * @Author lj
     * @Date:11:38 2020/2/12
     * @param excelExportDto, response]
     * @return void
     **/
    void exportBalanceExcel(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
    * Description: 导出利润表
    * @author: LuoY
    * @date: 2020/2/17 0017 16:01
    * @param:[balanceDto, response]
    * @return:void
    */
    void exportProfitExcel(ExcelExportDto excelExportDto, HttpServletResponse response);

    /** 
    * @Description: 导出费用汇总表
    * @Param: [balanceDto, response] 
    * @return: void 
    * @Author: LuoY
    * @Date: 2020/3/2 11:55
    */ 
    void exportCostExcel(ExcelExportDto excelExportDto, HttpServletResponse response);
}

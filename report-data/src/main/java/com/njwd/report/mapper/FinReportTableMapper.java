package com.njwd.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.FinProfitReportTable;
import com.njwd.entity.reportdata.FinReportTable;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FinReportTableMapper extends BaseMapper<FinReportTable> {

    /**
     * 插入更新报表表
     * @Author lj
     * @Date:11:16 2020/1/13
     * @param financialReportItemSetVos
     * @return int
     **/
    int refreshFinReportTable(@Param("reportItemSetVos") List<FinancialReportItemSetVo> financialReportItemSetVos);

    /**
     * 查询报表数据
     * @Author lj
     * @Date:18:04 2020/1/13
     * @param finReportTableDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinReportTableVo>
     **/
    List<FinReportTableVo> findFinReportTableList(FinReportTableDto finReportTableDto);

    /** 
    * @Description: 查询利润表
    * @Param: [finProfitReportTableDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.FinProfitReportTableVo> 
    * @Author: LuoY
    * @Date: 2020/2/10 11:25
    */ 
    List<FinProfitReportTableVo> findProfitReportTableVo(FinProfitReportTableDto finProfitReportTableDto);

    /**
     * 现金流量
     * @Author lj
     * @Date:17:16 2020/2/11
     * @param finCashFlowReportTableDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinCashFlowReportTableVo>
     **/
    List<FinCashFlowReportTableVo> findCashFlowReportTableVo(FinCashFlowReportTableDto finCashFlowReportTableDto);

    /**
     * 获取净利润
     * @Author lj
     * @Date:10:47 2020/3/17
     * @param shareholderDividendDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.NetProfitVo>
     **/
    List<NetProfitVo> findNetProfit(@Param("queryDto") ShareholderDividendDto shareholderDividendDto);

    /**
     * @Description: 查询利润表
     * @Param: [finProfitReportTableDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.FinProfitReportTableVo>
     * @Author: LuoY
     * @Date: 2020/2/10 11:25
     */
    int deleteReportTableDataByCondition(BalanceDto balanceDto);
}
package com.njwd.report.service;

import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.vo.FinancialReportItemSetVo;
import com.njwd.support.Result;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author lj
 * @Description 财务报表_触发Job
 * @Date:17:10 2020/1/9
 **/
public interface TransferFinancialReportService {

    /**
     * 资产负债表
     * @Author lj
     * @Date:17:03 2020/1/9
     * @param transferReportDto
     * @return com.njwd.support.Result<java.lang.String>
     **/
    String transferBalanceReport(TransferReportSimpleDto transferReportDto);

    /**
    * @Description: 财务报告公式计算
    * @Param: [financialReportItemSetVos, balanceSubject]
    * @return: java.util.Map<java.lang.String,java.math.BigDecimal>
    * @Author: LuoY
    * @Date: 2020/2/11 11:10
    */
    Map<String, BigDecimal> financialReportCalculationResult(@NotNull List<FinancialReportItemSetVo> financialReportItemSetVos,
                                                                    @NotNull Map<String, BigDecimal> balanceSubject);

    /**
     * 现金流量表
     * @Author lj
     * @Date:11:53 2020/2/10
     * @param transferReportDto
     * @return com.njwd.support.Result<java.lang.String>
     **/
    String transferCashFlowReport(TransferReportSimpleDto transferReportDto);
}

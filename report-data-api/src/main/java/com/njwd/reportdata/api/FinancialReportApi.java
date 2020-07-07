package com.njwd.reportdata.api;

import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("reportdata/transferFinancialReport")
public interface FinancialReportApi {

    /**
     * @param
     * @return
     * @description 资产负债表
     * @author fancl
     * @date 2020/1/4
     */
    @PostMapping("balanceReport")
    Result balanceReport(TransferReportSimpleDto simpleDto);

    /**
     * 现金流量表
     * @Author lj
     * @Date:11:52 2020/2/10
     * @param simpleDto
     * @return com.njwd.support.Result
     **/
    @PostMapping("cashFlowReport")
    Result cashFlowReport(TransferReportSimpleDto simpleDto);

}

package com.njwd.report.controller;

import com.njwd.annotation.NoLogin;
import com.njwd.common.Constant;
import com.njwd.entity.basedata.dto.BaseAccountBookDto;
import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.report.service.TransferFinancialReportService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import com.njwd.utils.RedisUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author lj
 * @Description 财务报表
 * @Date:16:40 2020/1/9
 **/
@RestController
@RequestMapping("transferFinancialReport")
public class TransferFinancialReportController extends BaseController {

    @Resource
    TransferFinancialReportService transferFinancialReportService;

    /**
     * @description 资产负债表增量接口
     * @author fancl
     * @date 2020/1/4
     * @param
     * @return
     */
    @PostMapping("balanceReport")
    @NoLogin
    public Result<String> balanceReport(@RequestBody TransferReportSimpleDto simpleDto){
        //校验
        FastUtils.checkParams(simpleDto,simpleDto.getEnteId());
        //加锁
        RedisUtils.lock(Constant.TransferFinancialReport.BALANCE_REPORT, Constant.TransferFinancialReport.TIME_OUT,()->transferFinancialReportService.transferBalanceReport(simpleDto));
        return ok();
    }

   /**
    * 现金流量表
    * @Author lj
    * @Date:11:52 2020/2/10
    * @param simpleDto
    * @return com.njwd.support.Result
    **/
    @PostMapping("cashFlowReport")
    @NoLogin
    public Result<String> cashFlowReport(@RequestBody TransferReportSimpleDto simpleDto){
        //校验
        FastUtils.checkParams(simpleDto,simpleDto.getEnteId());
        //加锁
        RedisUtils.lock(Constant.TransferFinancialReport.CASH_FLOW_REPORT,Constant.TransferFinancialReport.TIME_OUT,()->transferFinancialReportService.transferCashFlowReport(simpleDto));
        return ok();
    }
}

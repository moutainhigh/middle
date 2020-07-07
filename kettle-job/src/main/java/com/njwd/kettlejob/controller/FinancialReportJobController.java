package com.njwd.kettlejob.controller;

import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.kettlejob.service.FinancialReportJobService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author lj
 * @Description 科目余额数据Controller
 * @Date:9:32 2020/1/16
 **/
@RestController
@RequestMapping("financialReportJob")
public class FinancialReportJobController extends BaseController {

    @Resource
    FinancialReportJobService financialReportJobService;

    /**
     * 资产负债处理
     * @Author lj
     * @Date:9:34 2020/1/16
     * @param simpleDtoDto
     * @return com.njwd.support.Result<java.lang.String>
     **/
    public Result<String> balanceReport(@RequestBody TransferReportSimpleDto simpleDtoDto){
        FastUtils.checkParams(simpleDtoDto,simpleDtoDto.getEnteId(),simpleDtoDto.getTransDay());
//        financialReportJobService.doGetBalanceReportData(simpleDtoDto);
        return ok();
    }

    /**
     * 现金流量表处理
     * @Author lj
     * @Date:9:34 2020/1/16
     * @param simpleDtoDto
     * @return com.njwd.support.Result<java.lang.String>
     **/
    public Result<String> cashFlowReport(@RequestBody TransferReportSimpleDto simpleDtoDto){
        FastUtils.checkParams(simpleDtoDto,simpleDtoDto.getEnteId(),simpleDtoDto.getTransDay());
//        financialReportJobService.doGetCashFlowReportData(simpleDtoDto);
        return ok();
    }
}

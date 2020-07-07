package com.njwd.kettlejob.service;

import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.support.Result;

import java.util.Map;

public interface FinancialReportJobService {

    /**
     * 获取报表基准表数据
     * @Author lj
     * @Date:9:39 2020/1/16
     * @param appId
     * @return com.njwd.support.Result<com.njwd.entity.kettlejob.dto.TransferReportDto>
     **/
    String doGetBalanceReportData(String appId, String enteId, Map<String,String> params);

    String doGetCashFlowReportData(String appId, String enteId, Map<String,String> params);
}

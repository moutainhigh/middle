package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.njwd.common.Constant;
import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.kettlejob.cloudclient.FinancialReportFeignClient;
import com.njwd.kettlejob.service.FinancialReportJobService;
import com.njwd.support.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FinancialReportJobServiceImpl implements FinancialReportJobService {

    @Resource
    FinancialReportFeignClient financialReportFeignClient;

    /**
     * 获取报表基准表数据
     *
     * @param appId
     * @return com.njwd.support.Result<com.njwd.entity.kettlejob.dto.TransferReportDto>
     * @Author lj
     * @Date:9:39 2020/1/16
     **/
    @Override
    public String doGetBalanceReportData(String appId, String enteId, Map<String,String> params) {
        TransferReportSimpleDto simpleDto = new TransferReportDto();
        simpleDto.setEnteId(enteId);
        //异步更新报表基准表数据
        CompletableFuture.runAsync(() -> {
            financialReportFeignClient.balanceReport(simpleDto);
        });
        JSONObject taskJson = new JSONObject();
        taskJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        return taskJson.toJSONString();
    }

    @Override
    public String doGetCashFlowReportData(String appId, String enteId, Map<String,String> params) {
        TransferReportSimpleDto simpleDto = new TransferReportDto();
        simpleDto.setEnteId(enteId);
        //异步更新报表基准表数据
        CompletableFuture.runAsync(() -> {
            financialReportFeignClient.cashFlowReport(simpleDto);
        });
        JSONObject taskJson = new JSONObject();
        taskJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        return taskJson.toJSONString();
    }


}

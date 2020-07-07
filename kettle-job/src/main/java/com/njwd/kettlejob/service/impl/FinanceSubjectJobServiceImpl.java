package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.njwd.common.Constant;
import com.njwd.common.LogConstant;
import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.kettlejob.cloudclient.FinanceSubjectFeignClient;
import com.njwd.kettlejob.service.FinanceSubjectJobService;
import com.njwd.support.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 *@description:调用科目数据 Api
 *@author: fancl
 *@create: 2020-01-07 
 */
@Service
public class FinanceSubjectJobServiceImpl implements FinanceSubjectJobService {


    @Resource
    FinanceSubjectFeignClient financeSubjectFeignClient;

    private final static Logger logger = LoggerFactory.getLogger(FinanceSubjectJobServiceImpl.class);
    /**
     * @description 全量更新科目发生额
     * @author fancl
     * @date 2020/1/7
     * @param
     * @return
     */
    @Override
    public Result<TransferReportDto> doRefreshSubjectData(TransferReportSimpleDto simpleDto) {
        //更新科目发生额
        Result result1 = financeSubjectFeignClient.refreshSubjectData(simpleDto);
        logger.info(LogConstant.Finance.CALC_AND_UPDATE_FIN_SUBJECT);
        return result1.ok(result1.getData());
    }

    /**
     * @description 部分更新科目发生额
     * @author fancl
     * @date 2020/1/7
     * @param
     * @return
     */
    @Override
    public String doRefreshPartSubjectData(String appId, String enteId, Map<String,String> params) {
        TransferReportSimpleDto simpleDto = new TransferReportDto();
        simpleDto.setEnteId(enteId);
        //异步更新科目发生额
        CompletableFuture.runAsync(() -> {
            logger.info(LogConstant.Finance.UPDATE_SUBJECT);
            financeSubjectFeignClient.refreshPartSubjectData(simpleDto);
        });
        JSONObject taskJson = new JSONObject();
        taskJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        logger.info(LogConstant.Finance.CALC_AND_UPDATE_FIN_SUBJECT);
        return taskJson.toString();
    }


    /**
     * @description 手工执行刷新科目数据
     * @author fancl
     * @date 2020/3/18
     * @param 
     * @return 
     */
    @Override
    public Result<TransferReportDto> doRefreshPartSubjectDataByMan(String appId, String enteId, Map<String,String> params) {
        TransferReportSimpleDto simpleDto = new TransferReportDto();
        simpleDto.setEnteId(enteId);
        //更新科目发生额
        Result result1 = financeSubjectFeignClient.refreshPartSubjectData(simpleDto);
        logger.info(LogConstant.Finance.CALC_AND_UPDATE_FIN_SUBJECT);
        return result1.ok(result1.getData());
    }
}

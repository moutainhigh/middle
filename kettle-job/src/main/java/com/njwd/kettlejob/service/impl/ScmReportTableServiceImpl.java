package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.njwd.common.Constant;
import com.njwd.common.LogConstant;
import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.kettlejob.cloudclient.FinanceSubjectFeignClient;
import com.njwd.kettlejob.cloudclient.ScmReportTableFeignClient;
import com.njwd.kettlejob.service.FinanceSubjectJobService;
import com.njwd.kettlejob.service.ScmReportTableService;
import com.njwd.support.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @description: 生成供应链报表表
 * @author: 周鹏
 * @create: 2020-04-01
 */
@Service
public class ScmReportTableServiceImpl implements ScmReportTableService {
    @Resource
    ScmReportTableFeignClient scmReportTableFeignClient;

    private final static Logger logger = LoggerFactory.getLogger(ScmReportTableServiceImpl.class);

    /**
     * 生成全量报表表信息
     *
     * @param appId, enteId, params
     * @return Result
     * @author: 周鹏
     * @create: 2020-04-01
     */
    @Override
    public Result refreshFullScmData(String appId, String enteId, Map<String, String> params) {
        TransferReportSimpleDto simpleDto = new TransferReportSimpleDto();
        simpleDto.setEnteId(enteId);
        //生成毛利分析表信息
        Result result = scmReportTableFeignClient.refreshFullScmData(simpleDto);
        logger.info(LogConstant.Scm.GROSS_PROFIT);
        return result.ok(result.getData());
    }

    /**
     * 批量更新报表表信息
     *
     * @param appId, enteId, params
     * @return Result
     * @author: 周鹏
     * @create: 2020-04-01
     */
    @Override
    public String refreshPartScmData(String appId, String enteId, Map<String, String> params) {
        TransferReportSimpleDto simpleDto = new TransferReportSimpleDto();
        simpleDto.setEnteId(enteId);
        //异步更新毛利分析表信息
        CompletableFuture.runAsync(() -> {
            logger.info(LogConstant.Scm.GROSS_PROFIT);
            scmReportTableFeignClient.refreshPartScmData(simpleDto);
        });
        JSONObject taskJson = new JSONObject();
        taskJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        logger.info(LogConstant.Scm.GROSS_PROFIT);
        return taskJson.toString();
    }

}

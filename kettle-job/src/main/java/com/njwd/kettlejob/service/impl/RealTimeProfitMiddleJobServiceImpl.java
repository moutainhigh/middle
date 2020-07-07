package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.njwd.common.Constant;
import com.njwd.common.LogConstant;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.dto.RealTimeProfitDto;
import com.njwd.kettlejob.cloudclient.FinancialReportFeignClient;
import com.njwd.kettlejob.cloudclient.RealTimeProfitMiddleFeignClient;
import com.njwd.kettlejob.service.RealTimeProfitMiddleJobService;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName RealTimeProfitMiddleJobServiceImpl
 * @Description RealTimeProfitMiddleJobServiceImpl
 * @Author admin
 * @Date 2020/4/28 15:40
 */
@Service
public class RealTimeProfitMiddleJobServiceImpl implements RealTimeProfitMiddleJobService {
    private final static Logger logger = LoggerFactory.getLogger(RealTimeProfitMiddleJobServiceImpl.class);
    @Resource
    RealTimeProfitMiddleFeignClient financialReportFeignClient;


    /**
     * 批量更新报表表信息
     *
     * @param appId
     * @param enteId
     * @param params
     * @return Result
     * @author: 李宝
     * @create: 2020-04-28
     */
    @Override
    public String refreshPartRealTimeProfitData(String appId, String enteId, Map<String, String> params)  throws ParseException{
        RealTimeProfitDto realTimProfitDto = new RealTimeProfitDto();
        realTimProfitDto.setEnteId(enteId);
        if (params!= null && StringUtil.isNotEmpty(params.get("beginDate"))){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            realTimProfitDto.setBeginDate(sdf.parse(params.get("beginDate")));
        }
        //异步更新实时利润分析表信息
        CompletableFuture.runAsync(() -> {
            logger.info(LogConstant.Scm.REALTIME_PROFIT);
            try {
                financialReportFeignClient.refreshRealTimeProfitMiddle(realTimProfitDto);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        JSONObject taskJson = new JSONObject();
        taskJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        logger.info(LogConstant.Scm.REALTIME_PROFIT);
        return taskJson.toString();
    }
}

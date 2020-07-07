package com.njwd.kettlejob.cloudclient;

import com.njwd.reportdata.api.RealTimeMiddleApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName RealTimeProfitMiddleFeignClient
 * @Description RealTimeProfitMiddleFeignClient
 * @Author admin
 * @Date 2020/4/28 16:03
 */
@FeignClient(value = "report-data" ,contextId = "RealTimeProfitMiddleFeignClient")
public interface RealTimeProfitMiddleFeignClient extends RealTimeMiddleApi {
}

package com.njwd.kettlejob.cloudclient;

import com.njwd.reportdata.api.FinancialReportApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author lj
 * @Description 调用report-data-api
 * @Date:9:30 2020/1/16
 **/
@FeignClient(value = "report-data" ,contextId = "FinancialReportFeignClient")
public interface FinancialReportFeignClient extends FinancialReportApi {
}

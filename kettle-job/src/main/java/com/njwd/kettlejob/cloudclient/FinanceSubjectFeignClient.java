package com.njwd.kettlejob.cloudclient;

import com.njwd.reportdata.api.FinanceSubjectApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description 调用report-data-api
 * @author fancl
 * @date 2020/1/8
 * @param
 * @return
 */
@FeignClient(value = "report-data" ,contextId = "FinanceSubjectFeignClient")
public interface FinanceSubjectFeignClient extends FinanceSubjectApi {

}

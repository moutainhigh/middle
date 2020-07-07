package com.njwd.kettlejob.cloudclient;

import com.njwd.reportdata.api.ScmReportTableApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description: 生成供应链报表表
 * @author: 周鹏
 * @create: 2020-04-01
 */
@FeignClient(value = "report-data" ,contextId = "ScmReportTableFeignClient")
public interface ScmReportTableFeignClient extends ScmReportTableApi {

}

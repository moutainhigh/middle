package com.njwd.admin.cloudclient;

import com.njwd.schedule.api.TaskExcuteApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 修改任务状态API Client
 * @author XiaFq
 * @date 2019/12/30 8:56 上午
 */
@FeignClient(value = "schedule" ,contextId = "TaskExecuteFeignClient")
public interface TaskExecuteFeignClient extends TaskExcuteApi {
}

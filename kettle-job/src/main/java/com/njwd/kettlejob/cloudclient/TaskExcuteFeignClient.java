package com.njwd.kettlejob.cloudclient;

import com.njwd.schedule.api.TaskExcuteApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description: 任务执行API
 * @Author LuoY
 * @Date 2019/11/25
 */
@FeignClient(value = "schedule" ,contextId = "TaskExcuteFeignClient")
public interface TaskExcuteFeignClient extends TaskExcuteApi {
}

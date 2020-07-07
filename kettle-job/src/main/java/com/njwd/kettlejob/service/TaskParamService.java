package com.njwd.kettlejob.service;

import com.njwd.entity.schedule.vo.TaskParamVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface TaskParamService {
    /**
     * 批量修改任务参数
     * @param appId
     * @param enteId
     * @param paramsMap
     */
    void updateTaskParamBatch(String appId, String enteId, Map<String, Object> paramsMap);

    /**
     * 查询任务参数
     * @param enteId
     * @param paramsMap
     * @return
     */
    TaskParamVo findTaskParam(String enteId, Map<String, Object> paramsMap) ;

    /**
     * 查询任务
     * @param enteId
     * @param taskKeys
     * @return
     */
    List<TaskParamVo> findTaskParamList(String enteId, List<String> taskKeys);
}

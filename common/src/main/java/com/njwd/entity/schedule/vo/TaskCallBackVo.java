package com.njwd.entity.schedule.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:任务回调返回的参数
 * @Author: yuanman
 * @Date: 2019/11/22 10:04
 */
@Data
public class TaskCallBackVo {
    /**
     * 日志是否保存成功状态
     **/
    private String logSave;
    /**
     * 任务是否更新成功状态
     **/
    private String taskUpdate;
    /**
     * 触发的任务
     */
    private List<String> triggerTask;
}

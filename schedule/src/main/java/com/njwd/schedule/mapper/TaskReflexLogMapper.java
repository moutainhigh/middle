package com.njwd.schedule.mapper;

import com.njwd.entity.schedule.TaskReflexLog;

/**
 * @Description:wd_task_reflex_log
 * @Author: yuanman
 * @Date: 2019/12/11 14:35
 */
public interface TaskReflexLogMapper {
    /**
     * 新增日志记录
     * @param record
     * @return
     */
    int insertSelective(TaskReflexLog record);

}
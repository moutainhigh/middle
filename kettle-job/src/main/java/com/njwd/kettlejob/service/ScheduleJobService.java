package com.njwd.kettlejob.service;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/11/26
 */
public interface ScheduleJobService {
    /**
     * @Author LuoY
     * @Description 启动任务调度工作
     * @Date 2019/11/26 19:09
     * @Param []
     * @return java.lang.Boolean
     **/
    void executeScheduleJob();

    /**
     * @Author LuoY
     * @Description 取消任务调度工作
     * @Date 2019/11/26 19:30
     * @Param []
     * @return void
     **/
    void cancelScheduleJob();
}

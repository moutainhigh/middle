package com.njwd.schedule.service;

import com.njwd.common.ScheduleConstant;
import com.njwd.entity.schedule.Task;
import com.njwd.entity.schedule.TaskReflexLog;
import com.njwd.schedule.mapper.TaskMapper;
import com.njwd.schedule.mapper.TaskReflexLogMapper;
import com.njwd.utils.CronUtil;
import com.njwd.utils.RedisUtils;
import com.njwd.utils.StringUtil;
import com.njwd.utils.idworker.IdWorker;
import org.quartz.CronExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: 定时处理的service
 * @Author: yuanman
 * @Date: 2019/12/11 9:37
 */
@Service
@Configuration
@EnableScheduling
public class AutoService {
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private TaskReflexLogMapper taskReflexLogMapper;
    @Resource
    private IdWorker idWorker;


    /**
     * @Description:任务超时自动恢复
     * @Author: yuanman
     * @Date: 2019/12/12 13:39
     * @param
     * @return:void
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void reflexTask(){
        System.out.println("--------------------任务自动恢复开始");
        //使用锁机制
         RedisUtils.tasklock(ScheduleConstant.TaskLock.LOCK_OF_AUTO_REFLEX, () -> {
            //查询已经超时的任务
            List<Task> list=taskMapper.selectOverTimeTask();
            //遍历任务列表
            list.forEach(task -> {
                String cron=task.getCron();
                if (StringUtil.isNotEmpty(cron) && CronExpression.isValidExpression(cron)) {
                    //如果该任务被其他任务触发了则不修改下次执行时间
                    if(task.getTriggerCount()==0){
                        task.setNextExcuteTime(CronUtil.getNextTriggerTime(cron));
                    }
                    //更新下次执行时间和任务状态为WAITING
                    RedisUtils.tasklock(ScheduleConstant.TaskLock.TASK_LOCK_UPDATE_PRE + task.getEnteId()+task.getTaskKey(), () ->  taskMapper.updateForReflexTask(task));
                    //记录恢复日志
                    TaskReflexLog log=new TaskReflexLog();
                    log.setId(idWorker.nextId());
                    log.setCreateTime(new Date());
                    log.setEnteId(task.getEnteId());
                    log.setTaskKey(task.getTaskKey());
                    taskReflexLogMapper.insertSelective(log);
                }
            });
             return null;
         });
    }
}

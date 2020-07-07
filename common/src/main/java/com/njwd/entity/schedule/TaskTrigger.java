package com.njwd.entity.schedule;

import lombok.Data;

import java.util.Date;

/**
 * @Description:表wd_task_trigger
 * @Author: yuanman
 * @Date: 2019/11/6 13:39
 */
@Data
public class TaskTrigger {
    /**
     *id
     */
    private String triggerId;
    /**
     *企业id
     */
    private String enteId;
    /**
     *源任务key
     */
    private String sourceTaskKey;
    /**
     *触发任务key
     */
    private String targetTaskKey;
    /**
     *触发状态：ON/OFF
     */
    private String triggerStatus;
    /**
     *描述
     */
    private String description;
    /**
     *创建时间
     */
    private Date createTime;

}
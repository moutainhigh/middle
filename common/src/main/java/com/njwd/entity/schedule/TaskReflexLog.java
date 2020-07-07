package com.njwd.entity.schedule;

import lombok.Data;

import java.util.Date;

/**
 * @Description:任务自动恢复日志
 * @Author: yuanman
 * @Date: 2019/12/11 15:33
 */
@Data
public class TaskReflexLog {
    /**
     * id
     */
    private String id;
    /**
     *企业id
     */
    private String enteId;
    /**
     *任务key
     */
    private String taskKey;
    /**
     *创建时间
     */
    private Date createTime;

}
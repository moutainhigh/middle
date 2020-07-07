package com.njwd.entity.schedule;

import lombok.Data;

/**
 * @Description:任务表主键
 * @Author: yuanman
 * @Date: 2019/11/22 11:09
 */
@Data
public class TaskKey {
    /**
     * 企业id
     */
    private String enteId;
    /**
     *任务key
     */
    private String taskKey;
}

package com.njwd.entity.schedule.vo;

import com.njwd.entity.schedule.TaskRelyBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: yuanman
 * @Date: 2019/11/18 11:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskRelyBaseVo extends TaskRelyBase {
    /**
     * 源任务名称
     */
    private String sourceTaskName;
    /**
     * 目标任务名称
     */
    private String targetTaskName;
}
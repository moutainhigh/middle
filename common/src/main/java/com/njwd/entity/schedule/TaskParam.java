package com.njwd.entity.schedule;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:任务参数表wd_task_param
 * @Author: ljc
 * @Date: 2020/1/7 14:09
 */
@Setter
@Getter
public class TaskParam {
    /**
     * 集团id
     */
    public String enteId;
    /**
     * 任务key
     */
    public String taskKey;
    /**
     * 任务类型 拉取PULL，清洗CLEAN，迁移TRANSFER，生成报表表REPORT
     */
    public String businessType;
    /**
     * 参数类型  0:时间 1：ID
     */
    public Integer paramType;
    /**
     * 切片参数
     */
    public String param;
    /**
     * 状态
     */
    public Integer status;
    /**
     * 上次切片参数
     */
    public String lastParam;

}

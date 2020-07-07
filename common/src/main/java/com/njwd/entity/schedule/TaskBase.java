package com.njwd.entity.schedule;

import lombok.Data;

/**
 * @Description: 任务基础信息配置表
 * @Author: yuanman
 * @Date: 2019/11/18 11:31
 */
@Data
public class TaskBase {
    /**
     * key
     */
    private String taskKey;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     *应用id
     */
    private String appId;
    /**
     *任务类型
     */
    private String taskType;
    /**
     *执行目标
     */
    private String targetExcute;
    /**
     *定时表达式
     */
    private String cron;
    /**
     *运行时参数
     */
    private String runtimeParam;
    /**
     *任务角色
     */
    private String jobRole;

    /**
     *最大执行时间
     */
    private int maxExcuteTime;

    /**
     * 业务类型：拉取PULL，清洗CLEAN，迁移TRANSFER，生成报表表REPORT
     */
    private String businessType;

    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 任务开启类型，AUTO手动，HANDLE手动
     */
    private String openType;

}
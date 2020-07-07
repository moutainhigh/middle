package com.njwd.entity.schedule;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Description:表wd_task
 * @Author: yuanman
 * @Date: 2019/11/6 13:39
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Task extends TaskKey{
    /**
     *任务名称
     */
    private String taskName;
    /**
     *appId
     */
    private String appId;
    /**
     *任务类型，KTR/BEAN
     */
    private String taskType;
    /**
     *执行目标，ktr文件路劲或者 类.方法
     */
    private String targetExcute;
    /**
     *任务状态：WAITING,EXCUTING,ERROR
     */
    private String taskStatus;
    /**
     *错误原因
     */
    private String errorReason;
    /**
     *定时表达式
     */
    private String cron;
    /**
     *最后执行时间
     */
    private Date lastExcuteTime;
    /**
     *下次执行时间
     */
    private Date nextExcuteTime;
    /**
     *开关状态：ON/OFF
     */
    private String switchStatus;
    /**
     *运行时参数
     */
    private String runtimeParam;
    /**
     *任务角色
     */
    private String jobRole;
    /**
     *创建时间
     */
    private Date createTime;
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
    /**
     * 删除标志：0未删除，1已删除
     */
    private String deleteStatus;
    /**
     * 任务执行前被触发的次数
     */
    private Integer triggerCount;

}
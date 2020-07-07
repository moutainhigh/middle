package com.njwd.entity.schedule;

import lombok.Data;
/**
 * @Description:任务关系基础配置
 * @Author: yuanman
 * @Date: 2019/11/18 11:31
 */
@Data
public class TaskRelyBase {
    /**
     * id
     */
    private String relyBaseId;
    /**
     *源任务key
     */
    private String sourceTaskKey;
    /**
     *触发的任务key
     */
    private String targetTaskKey;
    /**
     *顺向执行表达式
     */
    private String frontExpression;
    /**
     *顺向执行表达式
     */
    private String backExpression;
    /**
     *最大拒绝次数
     */
    private int maxRefuseCount;
    /**
     *当连续拒绝次数大于该值时预警
     */
    private int warnRefuseCount;
}
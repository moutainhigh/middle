package com.njwd.entity.schedule;

import lombok.Data;

import java.util.Date;
/**
 * @Description:任务关系
 * @Author: yuanman
 * @Date: 2019/11/22 11:09
 */
@Data
public class TaskRely {
    /**
     *关系id
     */
    private String relyId;
    /**
     *企业id
     */
    private String enteId;
    /**
     *源任务key
     */
    private String sourceTaskKey;
    /**
     *会触发的任务key
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
    private Integer maxRefuseCount;
    /**
     *当前拒绝次数
     */
    private Integer currentRefuseCount;
    /**
     *总拒绝次数
     */
    private Integer sumRefuseCount;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *当连续拒绝次数大于该值时预警
     */
    private int warnRefuseCount;

}
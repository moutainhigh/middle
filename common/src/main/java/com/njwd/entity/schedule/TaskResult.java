package com.njwd.entity.schedule;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @Description: 任务执行结果
 * @Author: yuanman
 * @Date: 2019/11/18 11:31
 */
@Data
public class TaskResult {
    /**
     * id
     */
    private String resultId;
    /**
     *企业id
     */
    private String enteId;
    /**
     *任务key
     */
    private String taskKey;
    /**
     *任务名称
     */
    private String taskName;
    /**
     *任务结果：SUCCESS,FAIL,PART_FAIL
     */
    private String result;
    /**
     *错误代码
     */
    private String errCode;
    /**
     *日志详细
     */
    private String backLog;
    /**
     *执行消耗时间
     */
    private Integer excuteTime;
    /**
     *更新数量
     */
    private Integer effectCount;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *业务系统填充后的参数
     */
    private Map<String,String> param;
    /**
     *运行时参数
     */
    private String runtimeParam;


}
package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @program: middle-data
 * @description: 任务Dto
 * @author: Chenfulian
 * @create: 2019-11-18 18:53
 **/
@Data
public class TaskDto {
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务KEY
     */
    private String taskKey;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 任务开关状态
     */
    private String switchStatus;
    /**
     * 任务执行状态
     */
    private String taskStatus;
}

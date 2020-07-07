package com.njwd.entity.admin.vo;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description AppDataObjectVo 应用配置的数据来源对象
 * @Date 2019/12/24 2:10 下午
 * @Version 1.0
 */
@Data
public class AppDataObjectVo {

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 数据对应id
     */
    private String objectId;

    /**
     * 数据对象类型
     */
    private String objectType;

    /**
     * 是否选中
     */
    private String selected;

    /**
     * 任务类型 PULL_PRI 主数据任务 PULL_BUS 业务数据任务
     */
    private String taskType;
}

package com.njwd.entity.schedule;

import lombok.Data;

/**
 * @Description:wd_task_config
 * @Author: yuanman
 * @Date: 2019/12/12 10:37
 */
@Data
public class TaskConfig {
    /**
     * 企业id
     */
    private String enteId;

    /**
     * 超时自动恢复开关 ON/OFF
     */
    private String reflexStatus;

    /**
     * 超时自动修复时间系数
     */
    private Double reflexRatio;

    /**
     * 超时预警开关 ON/OFF
     */
    private String warnStatus;

    /**
     * 超时预警系数
     */
    private Double warnRatio;


    /**
     * 企业是否有效,比如是否续费,ON/OFF
     */
    private String validStatus;

}
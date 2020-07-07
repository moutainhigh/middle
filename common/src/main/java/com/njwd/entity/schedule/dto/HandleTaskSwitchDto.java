package com.njwd.entity.schedule.dto;

import com.njwd.entity.schedule.Task;
import lombok.Data;

import java.util.List;

/**
 * @Description:开启任务时的参数
 * @Author: yuanman
 * @Date: 2019/12/27 16:36
 */
@Data
public class HandleTaskSwitchDto {
    /**
     * 企业Id
     */
    private String enteId;
    /**
     * 通过keys开启任务，需要开启的任务key集合
     */
    private List<String> taskKeys;
    /**
     * 通过task列表，需要开启的任务
     */
    private List<Task> tasks;
    /**
     * 开关状态:ON / OFF
     */
    private String switchStatus;

    /**
     * 删除标志
     */
    private String deleteStatus;
}

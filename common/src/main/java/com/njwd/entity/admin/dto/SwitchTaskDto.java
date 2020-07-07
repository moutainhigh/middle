package com.njwd.entity.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: middle-data
 * @description: 修改任务状态Dto
 * @author: Chenfulian
 * @create: 2019-11-19 14:44
 **/
@Data
public class SwitchTaskDto {
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 任务列表
     */
    private List<TaskDto> taskList;

}

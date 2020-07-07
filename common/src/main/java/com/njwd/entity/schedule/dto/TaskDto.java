package com.njwd.entity.schedule.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.schedule.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: yuanman
 * @Date: 2019/11/6 14:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskDto extends Task {
    /**
     *分页参数
     */
    private Page<Task> page = new Page<>();

    /**
     * 企业id
     **/
    private String enteId;

    /**
     * 角色
     **/
    private String role;
}

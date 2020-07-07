package com.njwd.entity.schedule.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.schedule.TaskResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: yuanman
 * @Date: 2019/11/19 14:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskResultDto extends TaskResult {
    /**
     *分页参数
     */
    private Page<TaskResult> page=new Page<>();
}

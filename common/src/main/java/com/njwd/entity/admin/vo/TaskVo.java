package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.dto.TaskDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author Chenfulian
 * @Description TaskVo
 * @Date 2019/12/13 10:31
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskVo extends TaskDto {
    /**
     * 任务标记
     */
    private String taskTag;
}

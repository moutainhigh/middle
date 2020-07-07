package com.njwd.schedule.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.schedule.TaskResult;
import com.njwd.entity.schedule.dto.TaskResultDto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:wd_task_result的mapper
 * @Author: yuanman
 * @Date: 2019/11/5 14:35
 */
@Repository
public interface TaskResultMapper {
    /**
     * @Description:新增
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int insertSelective(TaskResult record);
    /**
     * @Description:根据主键修改
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */

    Page<TaskResult> getListByParam(Page page, TaskResultDto resultDto);

}
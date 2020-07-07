package com.njwd.kettlejob.mapper;

import com.njwd.entity.schedule.dto.TaskParamDto;
import com.njwd.entity.schedule.vo.TaskParamVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description 任务参数
 * @Author ljc
 * @Date 2020/1/7
 **/
@Repository
public interface TaskParamMapper {
    /**
     * 修改任务
     * @param taskParamDto
     * @return
     */
    Integer updateTaskParam(@Param("taskParamDto")TaskParamDto taskParamDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateTaskParamBatch(List<TaskParamDto> list);

    /**
     * 根据任务名称查询任务参数
     * @param taskParamDto
     * @return
     */
    TaskParamVo findTaskParamByKey(@Param("taskParamDto")TaskParamDto taskParamDto);

    /**
     * 批量查询任务
     * @param map
     * @return
     */
    List<TaskParamVo> findTaskParamListByKeys( Map<String,Object> map);
}


package com.njwd.schedule.mapper;

import com.njwd.entity.schedule.TaskConfig;
import org.springframework.stereotype.Repository;

/**
 * @Description:wd_task_config
 * @Author: yuanman
 * @Date: 2019/12/12 10:37
 */
@Repository
public interface TaskConfigMapper {
    /**
     * 新增
     * @param record
     * @return
     */
    int insertSelective(TaskConfig record);

    /**
     *根据企业id获取配置信息
     * @param enteId
     * @return
     */
    TaskConfig selectByPrimaryKey(String enteId);

    /**
     * 修改
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(TaskConfig record);

    /**
     * 根据主键删除
     * @param enteId
     * @return
     */
    int deleteByPrimaryKey(String enteId);

}
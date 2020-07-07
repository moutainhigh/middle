package com.njwd.schedule.mapper;

import com.njwd.entity.schedule.TaskTrigger;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description:wd_task_trigger的mapper
 * @Author: yuanman
 * @Date: 2019/11/5 14:35
 */
@Repository
public interface TaskTriggerMapper {
    /**
     * @Description:根据主键删除
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int deleteByPrimaryKey(String triggerId);
    /**
     * @Description:新增
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int insertSelective(TaskTrigger record);
    /**
     * @Description:根据主键获取
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    TaskTrigger selectByPrimaryKey(String triggerId);
    /**
     * @Description:根据主键更新，为null的不更新
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int updateByPrimaryKeySelective(TaskTrigger record);

    /**
     * @Description:根据source和status获取targetIds
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    List<String> selectTargetBySourceAndStatus(TaskTrigger record);
    /**
     * @Description:根据ids将status修改为off
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int updateStatusToOffByIds(Map<String, Object> param);

    /**
     * @Description:根据ente_id,target_task_key查询出
     * @Author: yuanman
     * @Date: 2019/11/20 16:18
     */
    List<String> selectSourceByTargetAndExcuting(TaskTrigger record);
}
package com.njwd.schedule.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.njwd.entity.schedule.TaskRely;
import com.njwd.entity.schedule.vo.TaskRelyVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:wd_task_rely的mapper
 * @Author: yuanman
 * @Date: 2019/11/5 14:35
 */
@Repository
@SqlParser(filter=true)
public interface TaskRelyMapper {

    /**
     * @Description:新增
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int insertSelective(TaskRely record);

    /**
     * @Description:根据主键更新，为null的不更新
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    int updateByPrimaryKeySelective(TaskRely record);

    /**
     * @Description:根据源任务获取未停止的关系列表
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    List<TaskRely> selectBySourceWithoutStoped(TaskRely record);

    /**
     * @Description:根据目标任务获取未停止的关系列表
     * @Author: yuanman
     * @Date: 2019/11/20 11:01
     */
    List<TaskRely> selectByTargetWithoutStoped(TaskRely record);

    /**
     * @Description:验证是否满足表达式
     * @Author: yuanman
     * @Date: 2019/11/22 14:01
     */
    boolean excuteExpression(String expression);

}
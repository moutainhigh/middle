package com.njwd.schedule.mapper;

import com.njwd.entity.schedule.EnteApp;
import com.njwd.entity.schedule.vo.TaskVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:wd_ente_app的mapper
 * @Author: yuanman
 * @Date: 2019/11/5 14:35
 */
@Repository
public interface EnteAppMapper {
    /**
    * @Description:根据ente_id获取相关信息
    * @Author: yuanman
    * @Date: 2019/11/20 11:01
    */
    List<EnteApp> selectByEnteIdAndAppId(TaskVo taskVo);

    /**
     * @Author ZhuHC
     * @Date  2020/4/15 10:42
     * @Param
     * @return
     * @Description 获取需要重新拉取数据的任务
     */
    List<EnteApp> findNeedTask();

}
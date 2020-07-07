package com.njwd.schedule.api;

import com.njwd.entity.schedule.Task;
import com.njwd.entity.schedule.TaskResult;
import com.njwd.entity.schedule.dto.HandleTaskSwitchDto;
import com.njwd.entity.schedule.dto.TaskDto;
import com.njwd.entity.schedule.vo.TaskCallBackVo;
import com.njwd.entity.schedule.vo.TaskVo;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @Description: schedule模块任务执行API
 * @Author LuoY
 * @Date 2019/11/25
 */
@RequestMapping("schedule/taskExcute")
public interface TaskExcuteApi {

    /**
     * @Description:初始化企业任务数据
     * @Author: yuanman
     * @Date: 2019/11/21 17:21
     * @param taskDto
     * @return:com.njwd.support.Result
     */
    @PostMapping("initEnteTasks")
    Result initEnteTasks(@RequestBody TaskDto taskDto);

    /**
     * @Description:获取任务去执行
     * @Author: yuanman
     * @Date: 2019/11/20 10:35
     * @param taskDto
     * @return:com.njwd.support.Result
     */
    @PostMapping("getTaskToExcute")
    Result<TaskVo> getTaskToExcute(@RequestBody TaskDto taskDto);
    /**
     * @Description:任务反馈
     * @Author: yuanman
     * @Date: 2019/11/20 10:35
     * @param result
     * @return:com.njwd.support.Result
     */
    @PostMapping("taskCallback")
    Result<TaskCallBackVo> taskCallback(@RequestBody TaskResult result);

    /**
     * @param handleTaskSwitchDto
     * @Description:根据keys开启或者关闭任务
     * @Author: yuanman
     * @Date: 2019/11/20 10:35
     * @return:com.njwd.support.Result
     */
    @PostMapping("HandleSwicthStatusByKeys")
    Result<Map<String,Object>> HandleSwicthStatusByKeys(@RequestBody HandleTaskSwitchDto handleTaskSwitchDto);

    /**
     * @param handleTaskSwitchDto
     * @Description:根据Types开启或者关闭任务
     * @Author: yuanman
     * @Date: 2019/11/20 10:35
     * @return:com.njwd.support.Result
     */
    @PostMapping("HandleSwicthStatusByTypes")
    Result<Map<String,Object>> HandleSwicthStatusByTypes(@RequestBody HandleTaskSwitchDto handleTaskSwitchDto);

    /**
     * @Description:根据keys查询相关任务
     * @Author: yuanman
     * @Date: 2019/12/29 14:00
     * @param handleTaskSwitchDto
     * @return:com.njwd.support.Result<java.util.List<com.njwd.entity.schedule.Task>>
     */
    @PostMapping("getListByKeys")
    Result<List<Task>> getListByKeys(@RequestBody HandleTaskSwitchDto handleTaskSwitchDto);

    /**
     * @Description:删除或创建企业任务
     * @Author: yuanman
     * @Date: 2019/12/30 13:58
     * @param handleTaskSwitchDto
     * @return:com.njwd.support.Result<java.lang.String>
     */
    @PostMapping("handleTaskDeleteStatus")
    Result<String> handleTaskDeleteStatus(@RequestBody HandleTaskSwitchDto handleTaskSwitchDto);

    /**
     * @Author ZhuHC
     * @Date  2020/4/15 10:42
     * @Param
     * @return
     * @Description 获取需要重新拉取数据的任务
     */
    @PostMapping("getNeedUpdateDataTask")
    Result<TaskVo> getNeedUpdateDataTask();
}

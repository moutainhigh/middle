package com.njwd.schedule.controller;

import com.njwd.annotation.NoLogin;
import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.basedata.KettleInfo;
import com.njwd.entity.schedule.Task;
import com.njwd.entity.schedule.TaskResult;
import com.njwd.entity.schedule.dto.HandleTaskSwitchDto;
import com.njwd.entity.schedule.dto.TaskDto;
import com.njwd.entity.schedule.vo.TaskCallBackVo;
import com.njwd.entity.schedule.vo.TaskVo;
import com.njwd.schedule.service.TaskCallBackService;
import com.njwd.schedule.service.TaskConfigService;
import com.njwd.schedule.service.TaskGetService;
import com.njwd.service.KettleService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description:任务执行
 * @Author: yuanman
 * @Date: 2019/11/5 14:35
 */
@RestController
@RequestMapping("taskExcute")
@Api(value = "dataAppConfigController",tags = "任务调度")
public class TaskExcuteController extends BaseController {

    @Resource
    private TaskGetService taskGetService;

    @Resource
    private TaskCallBackService taskCallBackService;

    @Resource
    private TaskConfigService taskConfigService;

    @Resource
    private KettleService kettleService;

    @PostMapping("testJob")
    public void testJob(@RequestBody KettleInfo kettleInfo) {
        String flag =kettleService.runKettle(kettleInfo,Constant.KettleType.kjb);
        System.out.println(flag);
    }


    /**
     * @param taskDto
     * @Description:获取任务去执行
     * @Author: yuanman
     * @Date: 2019/11/20 10:35
     * @return:com.njwd.support.Result
     */
    @PostMapping("getTaskToExcute")
    @NoLogin
    @ApiOperation(value = "获取任务去执行", notes="获取任务去执行")
    public Result<TaskVo> getTaskToExcute(@RequestBody TaskDto taskDto) {
        FastUtils.checkParams(taskDto.getRole());
        return ok(taskGetService.getTaskByRole(taskDto.getRole()));
    }

    /**
     * @param result
     * @Description:任务反馈
     * @Author: yuanman
     * @Date: 2019/11/20 10:35
     * @return:com.njwd.support.Result
     */
    @PostMapping("taskCallback")
    @NoLogin
    @ApiOperation(value = "任务结果反馈", notes="任务结果反馈")
    public Result<TaskCallBackVo> taskCallback(@RequestBody TaskResult result) {
        FastUtils.checkParams(result.getTaskKey(), result.getEnteId(), result.getResult(), result.getEffectCount(),
                result.getParam(), result.getExcuteTime(), result.getTaskName());
        HttpServletRequest request=getRequest();
        //获取ip，
        String ip="";
        String host="";
        int port;
        if (request.getHeader("x-forwarded-for") == null) {
            ip=request.getRemoteAddr();//ip
            port =request.getRemotePort();//端口号。
            String url=ip+ Constant.Character.COMMA+port;
            result.setBackLog(url+Constant.Character.COLON+result.getBackLog());
        }
        //获取端口号
        return ok(taskCallBackService.taskCallBack(result));
    }

    /**
     * @param handleTaskSwitchDto
     * @Description:根据keys开启或者关闭任务
     * @Author: yuanman
     * @Date: 2019/11/20 10:35
     * @return:com.njwd.support.Result
     */
    @PostMapping("HandleSwicthStatusByKeys")
    @NoLogin
    @ApiOperation(value = "根据keys开启或者关闭任务", notes="根据keys开启或关闭任务")
    public Result<Map<String,Object>> HandleSwicthStatusByKeys(@RequestBody HandleTaskSwitchDto handleTaskSwitchDto) {
        FastUtils.checkParams(handleTaskSwitchDto.getEnteId(),handleTaskSwitchDto.getTasks());
        return ok(taskConfigService.HandleSwicthStatus(handleTaskSwitchDto,ScheduleConstant.handleType.BY_KEYS));
    }

    /**
     * @param handleTaskSwitchDto
     * @Description:根据Types开启或者关闭任务
     * @Author: yuanman
     * @Date: 2019/11/20 10:35
     * @return:com.njwd.support.Result
     */
    @PostMapping("HandleSwicthStatusByTypes")
    @NoLogin
    @ApiOperation(value = "根据types开启或者关闭任务", notes="根据types开启或者关闭任务")
    public Result<Map<String,Object>> HandleSwicthStatusByTypes(@RequestBody HandleTaskSwitchDto handleTaskSwitchDto) {
        FastUtils.checkParams(handleTaskSwitchDto.getEnteId(),handleTaskSwitchDto.getTasks());
        return ok(taskConfigService.HandleSwicthStatus(handleTaskSwitchDto,ScheduleConstant.handleType.BY_TYPES));
    }

    /**
     * @Description:根据keys查询相关任务
     * @Author: yuanman
     * @Date: 2019/12/29 14:00
     * @param handleTaskSwitchDto
     * @return:com.njwd.support.Result<java.util.List<com.njwd.entity.schedule.Task>>
     */
    @PostMapping("getListByKeys")
    @NoLogin
    @ApiOperation(value = "根据keys查询相关任务", notes="根据keys查询相关任务")
    public Result<List<Task>> getListByKeys(@RequestBody HandleTaskSwitchDto handleTaskSwitchDto) {
        FastUtils.checkParams(handleTaskSwitchDto.getEnteId(),handleTaskSwitchDto.getTaskKeys());
        return ok(taskConfigService.getListByKeys(handleTaskSwitchDto));
    }

    /**
     * @Description:删除或创建企业任务
     * @Author: yuanman
     * @Date: 2019/12/30 13:58
     * @param handleTaskSwitchDto
     * @return:com.njwd.support.Result<java.lang.String>
     */
    @PostMapping("handleTaskDeleteStatus")
    @NoLogin
    @ApiOperation(value = "删除或创建企业任务", notes="删除或创建企业任务")
    public Result<String> handleTaskDeleteStatus(@RequestBody HandleTaskSwitchDto handleTaskSwitchDto) {
        FastUtils.checkParams(handleTaskSwitchDto.getEnteId(),handleTaskSwitchDto.getTaskKeys());
        return ok(taskConfigService.handleDelStatus(handleTaskSwitchDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2020/4/15 10:42
     * @Param
     * @return
     * @Description 获取需要重新拉取数据的任务
     */
    @PostMapping("getNeedUpdateDataTask")
    @NoLogin
    @ApiOperation(value = "查询需要重新拉取数据的任务", notes="查询需要重新拉取数据的任务")
    public Result<TaskVo> getNeedUpdateDataTask() {
        return ok(taskGetService.getNeedUpdateDataTask());
    }
}

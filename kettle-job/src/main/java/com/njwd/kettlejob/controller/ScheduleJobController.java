package com.njwd.kettlejob.controller;

import com.njwd.support.BaseController;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/11/26
 */
@RestController
@RequestMapping("ScheduleJob")
public class ScheduleJobController extends BaseController {

    /**
     * @Author LuoY
     * @Description 启动任务调度worker接口
     * @Date 2019/11/26 19:10
     * @Param [cronExpresion] 执行间隔
     * @return com.njwd.support.Result<java.lang.Boolean>
     **/
    @RequestMapping("taskExecute")
    public Result taskExecute(){
        return ok();
    }

    /**
     * @Author LuoY
     * @Description 终止线程池任务执行
     * @Date 2019/11/26 19:29
     * @Param []
     * @return com.njwd.support.Result
     **/
    @RequestMapping("cancelTask")
    public Result cancelTask(){
        return ok();
    }
}

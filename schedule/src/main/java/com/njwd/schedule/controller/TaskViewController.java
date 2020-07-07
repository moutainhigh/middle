package com.njwd.schedule.controller;

import com.njwd.annotation.NoLogin;
import com.njwd.schedule.service.TaskViewService;
import com.njwd.support.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description:任务关系图查询
 * @Author: yuanman
 * @Date: 2019/11/5 14:35
 */
@Controller
@RequestMapping("taskView")
public class TaskViewController extends BaseController {
    @Resource
    TaskViewService taskViewService;
    @NoLogin
    @RequestMapping (value = "/getRelyPic", method = RequestMethod.GET)
    public ModelAndView getRelyPic() {
        ModelAndView mv=new ModelAndView();
        mv.setViewName("index");
       return mv;
    }
    @NoLogin
    @RequestMapping (value = "/getRelyPicData", method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getRelyPicData(@RequestParam String enteId,@RequestParam String taskKey,@RequestParam String type){
       return taskViewService.getData(enteId,taskKey,type);
    }
}

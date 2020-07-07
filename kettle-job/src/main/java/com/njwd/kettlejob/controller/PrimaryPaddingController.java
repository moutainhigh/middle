package com.njwd.kettlejob.controller;

import com.njwd.kettlejob.service.PrimaryPaddingService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @program: middle-data
 * @description: 数据填充Controller
 * @author: Chenfulian
 * @create: 2019-11-20 10:32
 **/
@RestController
@RequestMapping("primaryPadding")
public class PrimaryPaddingController extends BaseController {
    @Resource
    private PrimaryPaddingService primaryPaddingService;


    /**
     * 执行主系统填充任务
     * @author Chenfulian
     * @date  2019/11/24 14:02
     * @param params 企业id，数据类型
     * @return com.njwd.support.Result
     */
//    @PostMapping("doDealPrimaryPaddingJob")
//    public Result doDealPrimaryPaddingJob(@RequestBody Map<String, String> params) {
//        primaryPaddingService.dealPrimaryPaddingJob(String appId,String enteId,params);
//        return ok();
//    }

}

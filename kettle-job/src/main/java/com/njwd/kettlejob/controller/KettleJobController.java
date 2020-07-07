package com.njwd.kettlejob.controller;

import com.njwd.kettlejob.service.KettleJobService;
import com.njwd.entity.basedata.KettleInfo;
import com.njwd.entity.basedata.KettleResult;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/10/30 14:03
 */
@RestController
@RequestMapping("kettle")
public class KettleJobController {

    @Autowired
    private KettleJobService kettleService;


    @PostMapping("patientTrans")
    public Result<KettleResult> patientTrans (@RequestBody KettleInfo kettleInfo)
    {
        FastUtils.checkParams(kettleInfo.getFileName());
        return kettleService.runKtr(kettleInfo);
    }

    @PostMapping(value = "patientIn")
    public Result<KettleResult> patientIn(@RequestBody KettleInfo kettleInfo)
    {
        FastUtils.checkParams(kettleInfo.getFileName());
        return kettleService.runKjb(kettleInfo);
    }

}

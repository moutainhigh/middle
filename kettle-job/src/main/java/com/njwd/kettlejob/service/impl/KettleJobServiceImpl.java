package com.njwd.kettlejob.service.impl;

import com.njwd.common.Constant;
import com.njwd.kettlejob.service.KettleJobService;
import com.njwd.entity.basedata.KettleInfo;
import com.njwd.entity.basedata.KettleResult;
import com.njwd.service.KettleService;
import com.njwd.support.Result;

import org.springframework.stereotype.Service;


import javax.annotation.Resource;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/10/30 14:20
 */
@Service
public class KettleJobServiceImpl implements KettleJobService {
    @Resource
    private KettleService kettleService;

    /**
     * 执行ktr文件
     * @param kettleInfo
     * @return
     */
    @Override
    public Result<KettleResult> runKtr(KettleInfo kettleInfo) {
        String flag =kettleService.runKettle(kettleInfo, Constant.KettleType.ktr);
        Result<KettleResult> response = new Result<>();
        response.setStatus(flag);
        return response;
    }

    /**
     * 执行kjb文件
     * @param kettleInfo
     * @return
     */
    @Override
    public Result<KettleResult> runKjb(KettleInfo kettleInfo) {
        String flag =kettleService.runKettle(kettleInfo, Constant.KettleType.kjb);
        Result<KettleResult> response = new Result<>();
        response.setStatus(flag);
        return response;
    }

}

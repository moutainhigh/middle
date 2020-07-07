package com.njwd.service;

import com.njwd.entity.basedata.KettleInfo;

/**
 * @Author ZhuHC
 * @Date  2019/10/30 14:26
 * @Param 
 * @return 
 * @Description 
 */
public interface KettleService {
    /**
     * @Author ZhuHC
     * @Date  2019/10/30 14:26
     * @Param kettleInfo kettle执行参数，kettleType 执行kettle文件类型
     * @return Result<KettleResult>
     * @Description 执行kettle任务
     */
    String runKettle(KettleInfo kettleInfo,String kettleType);
}

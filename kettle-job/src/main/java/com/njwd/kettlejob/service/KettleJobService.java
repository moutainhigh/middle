package com.njwd.kettlejob.service;

import com.njwd.entity.basedata.KettleInfo;
import com.njwd.entity.basedata.KettleResult;
import com.njwd.support.Result;

/**
 * @Author ZhuHC
 * @Date  2019/10/30 14:26
 * @Param 
 * @return 
 * @Description 
 */
public interface KettleJobService {

    /**
     * @Author ZhuHC
     * @Date  2019/10/30 14:26
     * @Param
     * @return
     * @Description
     */
    Result<KettleResult> runKtr(KettleInfo kettleInfo);

    /**
     * @Author ZhuHC
     * @Date  2019/10/30 14:26
     * @Param
     * @return
     * @Description
     */
    Result<KettleResult> runKjb(KettleInfo kettleInfo);
}

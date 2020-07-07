package com.njwd.kettlejob.service;

import java.util.Map;

/**
* @Author Chenfulian
* @Description 数据填充Service类
* @Date  2019/11/20 10:36
*/
public interface PrimaryPaddingService {

    /**
     * 查询填充规则，并且拼接sql
     * @author Chenfulian
     * @date 2019/12/2 16:14
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    String dealPrimaryPaddingJob(String appId,String enteId,Map<String,String> params);
}

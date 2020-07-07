package com.njwd.kettlejob.service;

import java.text.ParseException;
import java.util.Map;

/**
 * @ClassName RealTimeProfitMiddleJobService
 * @Description RealTimeProfitMiddleJobService
 * @Author admin
 * @Date 2020/4/28 15:39
 */
public interface RealTimeProfitMiddleJobService {

    /**
     * 批量更新报表表信息
     *
     * @param params
     * @return Result
     * @author: 李宝
     * @create: 2020-04-28
     */
    String refreshPartRealTimeProfitData(String appId, String enteId, Map<String, String> params) throws ParseException;

}

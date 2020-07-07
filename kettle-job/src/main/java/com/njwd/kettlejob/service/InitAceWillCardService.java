package com.njwd.kettlejob.service;

import java.util.Map;

public interface InitAceWillCardService {


    /**
     * 同步微生活的会员消费记录
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    void addOrUpdateConsume(String appId, String enteId, Map<String,Object> paramsMap) ;

    /**
     * 同步微生活的会员储值记录
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    void addOrUpdatePrepaid(String appId, String enteId, Map<String,Object> paramsMap) ;

}

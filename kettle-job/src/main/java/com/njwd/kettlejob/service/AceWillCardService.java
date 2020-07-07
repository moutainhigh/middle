package com.njwd.kettlejob.service;

import java.util.Map;

public interface AceWillCardService {

    /**
     * 同步微生活的门店列表
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    String addOrUpdateShopRela(String appId, String enteId, Map<String,Object> paramsMap);

    /**
     * 同步微生活的会员消费记录
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    String addOrUpdateConsume(String appId, String enteId, Map<String,Object> paramsMap) ;

    /**
     * 清洗会员消费记录
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    String cleanConsume(String appId,String enteId,Map<String,Object> paramsMap);

    /**
     * 同步微生活的会员充值记录
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    String addOrUpdatePrepaid(String appId, String enteId, Map<String,Object> paramsMap);

    /**
     * 清洗会员充值记录
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    String cleanPrepaid(String appId,String enteId,Map<String,Object> paramsMap);
    /**
     * 同步会员等级并保存
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    String addOrUpdateGrade(String appId, String enteId, Map<String,Object> paramsMap) throws Exception;

    /**
     * 同步支付方式数据
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    String addOrUpdatePayType(String appId, String enteId, Map<String,Object> paramsMap)  throws Exception;

}
package com.njwd.kettlejob.service;

import java.util.Map;

/**
 * @Description 巡店
 * @Author jds
 * @Date 2019/11/6 14:25
 **/

public interface CruiseShopService {

    /**
     * @Description 同步巡店门店
     * @Author jds
     * @Date 2019/11/6 14:30
     * @Param [mapParam] enteId:企业ID  appId：appID  id:  password:  code:
     * @return int
     **/
    String addOrUpdateShop(String appId,String enteId,Map<String, Object> mapParam);


    /**
     * @Description 同步巡店项目
     * @Author jds
     * @Date 2019/11/13 11:30
     * @Param [mapParam] enteId:企业ID  appId：appID  id:  password:  code:
     * @return int
     **/
    String addOrUpdateItem(String appId,String enteId,Map<String, Object> mapParam);


    /**
     * @Description 同步巡店项目得分
     * @Author jds
     * @Date 2019/11/13 11:30
     * @Param [mapParam] enteId:企业ID  appId：appID  id:  password:  code:
     * @return int
     **/
    String addOrUpdateItemScore(String appId,String enteId,Map<String, Object> mapParam);

    /**
     * @Description 清洗巡店项目得分
     * @Author jds
     * @Date 2019/11/13 11:30
     * @Param [mapParam] enteId:企业ID  appId：appID  id:  password:  code:
     * @return int
     **/
    String cleanItemScore(String appId,String enteId,Map<String, Object> mapParam);

}

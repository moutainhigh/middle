package com.njwd.kettlejob.controller;



import com.njwd.common.Constant;
import com.njwd.kettlejob.service.CruiseShopService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author jds
 * @Description 巡店
 * @create 2019/11/6 14:08
 */
@RestController
@RequestMapping("cruiseShop")
public class CruiseShopController extends BaseController {

    @Resource
    private CruiseShopService cruiseShopService;

    /**
     * @Description  同步巡店门店数据
     * @Author jds
     * @Date 2019/11/12 17:08
     * @Param [map]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("addOrUpdateShop")
    public void addOrUpdateShop(@RequestBody  Map<String,Object> map) {
        cruiseShopService.addOrUpdateShop(map.get(Constant.CruiseShop.APPID).toString(),map.get(Constant.CruiseShop.ENTEID).toString(),map);
    }

    /**
     * @Description  同步巡店项目数据
     * @Author jds
     * @Date 2019/11/13 11:58
     * @Param [map]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("addOrUpdateItem")
    public void addOrUpdateItem(@RequestBody Map<String,Object> map) {
        cruiseShopService.addOrUpdateItem(map.get(Constant.CruiseShop.APPID).toString(),map.get(Constant.CruiseShop.ENTEID).toString(),map);
    }


    /**
     * @Description  同步巡店项目得分数据
     * @Author jds
     * @Date 2019/11/13 11:58
     * @Param [map]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("addOrUpdateItemScore")
    public void addOrUpdateItemScore( @RequestBody Map<String,Object> map) {
        cruiseShopService.addOrUpdateItemScore(map.get(Constant.CruiseShop.APPID).toString(),map.get(Constant.CruiseShop.ENTEID).toString(),map);
    }

}

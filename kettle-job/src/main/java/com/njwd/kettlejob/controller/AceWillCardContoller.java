package com.njwd.kettlejob.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.ScheduleConstant;
import com.njwd.kettlejob.service.AceWillCardService;
import com.njwd.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author: ljc
 * @Date: 2019/11/12
 */
@RestController
@RequestMapping("aceWillCard")
public class AceWillCardContoller {

    @Resource
    AceWillCardService aceWillCardService ;
    @Value("${aceWill.server}")
    private String aceWillServer;


    @RequestMapping("doAddShop")
    public void doAdd(String json)
    {
        JSONObject jsonObj = JSONObject.parseObject(json);
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("url", "https://api.acewill.net/");
//        params.put("appid","dp0C08m9OWAOAB5umOvCnAOU");
//        params.put("appkey","d709315fe09a9099ed33355f14a28831");
//        params.put("v","2.0");
//        params.put("fmt","JSON");
        params.put("url", jsonObj.get("url"));
        params.put("appid",jsonObj.get("appid"));
        params.put("appkey",jsonObj.get("appkey"));
        params.put("v",jsonObj.get("v"));
        params.put("fmt",jsonObj.get("fmt"));
        try {
            aceWillCardService.addOrUpdateShopRela(jsonObj.getString("app"),jsonObj.getString("enteId"),params);
            //aceWillCardService.addOrUpdateShopRela("crm","999",params);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("doAddConsume")
    public void doAddConsume(String json)
    {
        JSONObject jsonObj = JSONObject.parseObject(json);
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("url", "https://api.acewill.net/");
//        params.put(ScheduleConstant.AppInterface.STRATTIME, "2019-12-01 00:00:00");
//        params.put(ScheduleConstant.AppInterface.ENDTIME, "2019-12-15 23:59:59");
//        params.put("page","1");
//        //是否全部流水 true是 false否[默认，只查接口来源的流水]
//        params.put("is_all","true");
//        //是否包括分页信息
//        params.put("is_have_page","true");
//        params.put("appid","dp1RujMGQIVlW6FqrZ6Ey5d5");
//        params.put("appkey","9532a5a96c6988279078a049f5e85ff4");
//        params.put("v","2.0");
//        params.put("fmt","JSON");
        params.put("url", jsonObj.get("url"));
        params.put(ScheduleConstant.AppInterface.STRATTIME, jsonObj.get("startTime"));
        params.put(ScheduleConstant.AppInterface.ENDTIME, jsonObj.get("endTime"));
        params.put("page", "1");
        //是否全部流水 true是 false否[默认，只查接口来源的流水]
        params.put("is_all","true");
        //是否包括分页信息
        params.put("is_have_page","true");
        params.put("appid",jsonObj.get("appid"));
        params.put("appkey",jsonObj.get("appkey"));
        params.put("v",jsonObj.get("v"));
        params.put("fmt",jsonObj.get("fmt"));
        try{
            System.out.println("-----------------------start="+ DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND));
           aceWillCardService.addOrUpdateConsume(jsonObj.getString("app"),jsonObj.getString("enteId"),params);
            System.out.println("-----------------------end="+ DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("doAddPrepaid")
    public void doAddPrepaid(String json)
    {
        JSONObject jsonObj = JSONObject.parseObject(json);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("url", "https://api.acewill.net/");
//        params.put(ScheduleConstant.AppInterface.STRATTIME, "2020-01-01 00:00:00");
//        params.put(ScheduleConstant.AppInterface.ENDTIME, "2020-01-01 01:00:00");
        params.put("page","1");
        //是否全部流水 true是 false否[默认，只查接口来源的流水]
        params.put("is_all","true");
        //相遇
//        params.put("appid","dp0C08m9OWAOAB5umOvCnAOU");
//        params.put("appkey","d709315fe09a9099ed33355f14a28831");
        //九鼎轩火锅
        params.put("appid","dp1RujMGQIVlW6FqrZ6Ey5d5");
        params.put("appkey","9532a5a96c6988279078a049f5e85ff4");
        params.put("v","2.0");
        params.put("fmt","JSON");
//        params.put("url", jsonObj.get("url"));
//        params.put(ScheduleConstant.AppInterface.STRATTIME, jsonObj.get("startTime"));
//        params.put(ScheduleConstant.AppInterface.ENDTIME, jsonObj.get("endTime"));
//        params.put("page", "1");
//        //是否全部流水 true是 false否[默认，只查接口来源的流水]
//        params.put("is_all","true");
//        //是否包括分页信息
//        params.put("is_have_page","true");
//        params.put("appid",jsonObj.get("appid"));
//        params.put("appkey",jsonObj.get("appkey"));
//        params.put("v",jsonObj.get("v"));
//        params.put("fmt",jsonObj.get("fmt"));
        try{
            aceWillCardService.addOrUpdatePrepaid(jsonObj.getString("app"),jsonObj.getString("enteId"),params);
            System.out.println("-----------------------end="+ DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @RequestMapping("doDealCleanPrepaid")
    public void doDealCleanPrepaid(String json)
    {
        JSONObject jsonObj = JSONObject.parseObject(json);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("task_key","task_acewill_clean_prepaid");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("migrate_crm_prepaid");
        jsonArray.add("migrate_crm_prepaid_coupon");
        jsonArray.add("migrate_crm_prepaid_pay_type");
        params.put("target_task_key",jsonArray);
        try{
            aceWillCardService.cleanPrepaid(jsonObj.getString("app"),jsonObj.getString("enteId"),params);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("doAddGrade")
    public void doAddGrade(String json)
    {
        JSONObject jsonObj = JSONObject.parseObject(json);
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("url", "https://api.acewill.net/");
//        params.put("appid","dp1RujMGQIVlW6FqrZ6Ey5d5");
//        params.put("appkey","9532a5a96c6988279078a049f5e85ff4");
//        params.put("v","2.0");
//        params.put("fmt","JSON");
        params.put("url", jsonObj.get("url"));
        params.put("appid",jsonObj.get("appid"));
        params.put("appkey",jsonObj.get("appkey"));
        params.put("v",jsonObj.get("v"));
        params.put("fmt",jsonObj.get("fmt"));
        try{
            aceWillCardService.addOrUpdateGrade(jsonObj.getString("app"),jsonObj.getString("enteId"),params);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("doAddPayType")
    public void doAddPayType(String json)
    {
        JSONObject jsonObj = JSONObject.parseObject(json);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("url", "https://api.acewill.net/");
        params.put("appid","dp1RujMGQIVlW6FqrZ6Ey5d5");
        params.put("appkey","9532a5a96c6988279078a049f5e85ff4");
        params.put("v","2.0");
        params.put("fmt","JSON");
//        params.put("url", jsonObj.get("url"));
//        params.put("appid",jsonObj.get("appid"));
//        params.put("appkey",jsonObj.get("appkey"));
//        params.put("v",jsonObj.get("v"));
//        params.put("fmt",jsonObj.get("fmt"));
        try{
            aceWillCardService.addOrUpdatePayType(jsonObj.getString("app"),jsonObj.getString("enteId"),params);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}


package com.njwd.kettlejob.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njwd.annotation.NoLogin;
import com.njwd.kettlejob.service.InitAceWillCardService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("initCrm")
public class InitAceWillCardController {

    @Resource
    private InitAceWillCardService initAceWillCardService;

    @NoLogin
    @RequestMapping("doAddConsume")
    public void executeCrmConsume(int type,String year,String month) {
        String enteId = "2002595340009472";
        String appId="crm_acewill_01";
        Map paramsMap = new HashMap();
        paramsMap.put("url", "https://api.acewill.net/");
        paramsMap.put("page","1");
        //是否全部流水 true是 false否[默认，只查接口来源的流水]
        paramsMap.put("is_all","true");
        //是否包括分页信息
        paramsMap.put("is_have_page","true");
        paramsMap.put("v","2.0");
        paramsMap.put("fmt","JSON");
        //相遇
//        paramsMap.put("appid","dp0C08m9OWAOAB5umOvCnAOU");
//        paramsMap.put("appkey","d709315fe09a9099ed33355f14a28831");
        //九鼎轩火锅
        paramsMap.put("appid","dp1RujMGQIVlW6FqrZ6Ey5d5");
        paramsMap.put("appkey","9532a5a96c6988279078a049f5e85ff4");

        List<JSONObject> list = new ArrayList<>();
        if(type==0){
            JSONObject json01 = new JSONObject();
            json01.put("startTime",year+"-"+month+"-01 00:00:00");
            json01.put("endTime",year+"-"+month+"-01 24:00:00");
            list.add(json01);
        }
        if(type==1){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-02 00:00:00");
            json02.put("endTime",year+"-"+month+"-02 24:00:00");
            list.add(json02);
        }
        if(type==2){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-03 00:00:00");
            json02.put("endTime",year+"-"+month+"-03 24:00:00");
            list.add(json02);
        }
        if(type==3){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-04 00:00:00");
            json02.put("endTime",year+"-"+month+"-04 24:00:00");
            list.add(json02);
        }
        if(type==4){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-05 00:00:00");
            json02.put("endTime",year+"-"+month+"-05 24:00:00");
            list.add(json02);
        }
        if(type==5){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-06 00:00:00");
            json02.put("endTime",year+"-"+month+"-06 24:00:00");
            list.add(json02);
        }
        if(type==6){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-07 00:00:00");
            json02.put("endTime",year+"-"+month+"-07 24:00:00");
            list.add(json02);
        }
        if(type==7){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-08 00:00:00");
            json02.put("endTime",year+"-"+month+"-08 24:00:00");
            list.add(json02);
        }
        if(type==8){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-09 00:00:00");
            json02.put("endTime",year+"-"+month+"-09 24:00:00");
            list.add(json02);
        }
        if(type==9){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-10 00:00:00");
            json02.put("endTime",year+"-"+month+"-10 24:00:00");
            list.add(json02);
        }
        if(type==10){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-11 00:00:00");
            json02.put("endTime",year+"-"+month+"-11 24:00:00");
            list.add(json02);
        }
        if(type==11){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-12 00:00:00");
            json02.put("endTime",year+"-"+month+"-12 24:00:00");
            list.add(json02);
        }
        if(type==12){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-13 00:00:00");
            json02.put("endTime",year+"-"+month+"-13 24:00:00");
            list.add(json02);
        }
        if(type==13){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-14 00:00:00");
            json02.put("endTime",year+"-"+month+"-14 24:00:00");
            list.add(json02);
        }
        if(type==14){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-15 00:00:00");
            json02.put("endTime",year+"-"+month+"-15 24:00:00");
            list.add(json02);
        }
        if(type==15){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-16 00:00:00");
            json02.put("endTime",year+"-"+month+"-16 24:00:00");
            list.add(json02);
        }
        if(type==16){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-17 00:00:00");
            json02.put("endTime",year+"-"+month+"-17 24:00:00");
            list.add(json02);
        }
        if(type==17){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-18 00:00:00");
            json02.put("endTime",year+"-"+month+"-18 24:00:00");
            list.add(json02);
        }
        if(type==18){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-19 00:00:00");
            json02.put("endTime",year+"-"+month+"-19 24:00:00");
            list.add(json02);
        }
        if(type==19){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-20 00:00:00");
            json02.put("endTime",year+"-"+month+"-20 24:00:00");
            list.add(json02);
        }
        if(type==20){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-21 00:00:00");
            json02.put("endTime",year+"-"+month+"-21 24:00:00");
            list.add(json02);
        }
        if(type==21){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-22 00:00:00");
            json02.put("endTime",year+"-"+month+"-22 24:00:00");
            list.add(json02);
        }
        if(type==22){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-23 00:00:00");
            json02.put("endTime",year+"-"+month+"-23 24:00:00");
            list.add(json02);
        }
        if(type==23){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-24 00:00:00");
            json02.put("endTime",year+"-"+month+"-24 24:00:00");
            list.add(json02);
        }
        if(type==24){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-25 00:00:00");
            json02.put("endTime",year+"-"+month+"-25 24:00:00");
            list.add(json02);
        }
        if(type==25){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-26 00:00:00");
            json02.put("endTime",year+"-"+month+"-26 24:00:00");
            list.add(json02);
        }
        if(type==26){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-27 00:00:00");
            json02.put("endTime",year+"-"+month+"-27 24:00:00");
            list.add(json02);
        }
        if(type==27){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-28 00:00:00");
            json02.put("endTime",year+"-"+month+"-28 24:00:00");
            list.add(json02);
        }
        if(type==28){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-29 00:00:00");
            json02.put("endTime",year+"-"+month+"-29 24:00:00");
            list.add(json02);
        }
        if(type==29){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-30 00:00:00");
            json02.put("endTime",year+"-"+month+"-30 24:00:00");
            list.add(json02);
        }
        if(type==30){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-31 00:00:00");
            json02.put("endTime",year+"-"+month+"-31 24:00:00");
            list.add(json02);
        }
        paramsMap.put("date", JSONArray.toJSONString(list));
        initAceWillCardService.addOrUpdateConsume(appId,enteId,paramsMap);
    }


    @NoLogin
    @RequestMapping("doAddPrepaid")
    public void doAddPrepaid(int type,String year,String month,String appId) {
        String enteId = "2002595340009472";
        Map paramsMap = new HashMap();
        paramsMap.put("url", "https://api.acewill.net/");
        paramsMap.put("page","1");
        //是否全部流水 true是 false否[默认，只查接口来源的流水]
        paramsMap.put("is_all","true");
        //是否包括分页信息
        paramsMap.put("is_have_page","true");
        paramsMap.put("v","2.0");
        paramsMap.put("fmt","JSON");
        //相遇
        paramsMap.put("appid","dp0C08m9OWAOAB5umOvCnAOU");
        paramsMap.put("appkey","d709315fe09a9099ed33355f14a28831");
        //九鼎轩火锅
//        paramsMap.put("appid","dp1RujMGQIVlW6FqrZ6Ey5d5");
//        paramsMap.put("appkey","9532a5a96c6988279078a049f5e85ff4");


        List<JSONObject> list = new ArrayList<>();
        if(type==0){
            JSONObject json01 = new JSONObject();
            json01.put("startTime",year+"-"+month+"-01 00:00:00");
            json01.put("endTime",year+"-"+month+"-02 24:00:00");
            list.add(json01);
        }
        if(type==1){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-04 00:00:00");
            json02.put("endTime",year+"-"+month+"-05 24:00:00");
            list.add(json02);
        }
        if(type==2){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-07 00:00:00");
            json02.put("endTime",year+"-"+month+"-08 24:00:00");
            list.add(json02);
        }
        if(type==3){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-10 00:00:00");
            json02.put("endTime",year+"-"+month+"-11 24:00:00");
            list.add(json02);
        }
        if(type==4){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-13 00:00:00");
            json02.put("endTime",year+"-"+month+"-14 24:00:00");
            list.add(json02);
        }
        if(type==5){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-16 00:00:00");
            json02.put("endTime",year+"-"+month+"-17 24:00:00");
            list.add(json02);
        }
        if(type==6){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-19 00:00:00");
            json02.put("endTime",year+"-"+month+"-20 24:00:00");
            list.add(json02);
        }
        if(type==7){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-22 00:00:00");
            json02.put("endTime",year+"-"+month+"-23 24:00:00");
            list.add(json02);
        }
        if(type==8){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-25 00:00:00");
            json02.put("endTime",year+"-"+month+"-26 24:00:00");
            list.add(json02);
        }
        if(type==9){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-28 00:00:00");
            json02.put("endTime",year+"-"+month+"-30 24:00:00");
            list.add(json02);
        }


        if(type==112){
            JSONObject json02 = new JSONObject();
            json02.put("startTime",year+"-"+month+"-12 00:00:00");
            json02.put("endTime",year+"-"+month+"-12 24:00:00");
            list.add(json02);
        }
        paramsMap.put("date", JSONArray.toJSONString(list));
        initAceWillCardService.addOrUpdatePrepaid(appId,enteId,paramsMap);
    }

}

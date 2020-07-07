package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.kettlejob.dto.*;
import com.njwd.entity.kettlejob.vo.*;
import com.njwd.kettlejob.service.InitAceWillCardService;
import com.njwd.mapper.CrmConsumeCouponTestMapper;
import com.njwd.mapper.CrmConsumeTestMapper;
import com.njwd.mapper.CrmPrepaidCouponInitMapper;
import com.njwd.mapper.CrmPrepaidInitMapper;
import com.njwd.utils.idworker.IdWorker;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class InitAceWillCardServiceImpl implements InitAceWillCardService {

    @Resource
    private CrmConsumeTestMapper crmConsumeTestMapper;
    @Resource
    private CrmConsumeCouponTestMapper crmConsumeCouponTestMapper;
    @Resource
    private CrmPrepaidInitMapper crmPrepaidInitMapper;
    @Resource
    private CrmPrepaidCouponInitMapper crmPrepaidCouponInitMapper;
    @Resource
    private AceWillCardServiceImpl aceWillCardService;
    @Value("${aceWill.url.consume_list}")
    private String consume_list_url;
    @Value("${aceWill.url.prepaid_list}")
    private String prepaid_list_url;
    @Resource
    private IdWorker idWorker;

    @Override
    public void addOrUpdateConsume(String appId, String enteId, Map<String,Object> paramsMap){
        try{
            CrmConsumeDto crmCardConsumeDto = new CrmConsumeDto();
            crmCardConsumeDto.setEnteId(enteId);
            crmCardConsumeDto.setAppId(appId);
            JSONArray jsonArr = JSONArray.parseArray(paramsMap.get("date").toString());

            for (int j=0;j<jsonArr.size();j++){
                JSONObject jsonObject = jsonArr.getJSONObject(j);
                String startTime = jsonObject.getString("startTime");
                String endTime = jsonObject.getString("endTime");
                //数据变更数量
                if(appId!=null&&enteId!=null){
                    String url = paramsMap.get(ScheduleConstant.AppInterface.URL).toString()+consume_list_url;
                    //查询业务表中最大时间
                    //设置已有数据的查询条件
                    //请求接口参数
                    JSONObject reqJson = new JSONObject();
                    reqJson.put(Constant.AceWillCrm.BEGIN_DATE,startTime);
                    reqJson.put(Constant.AceWillCrm.END_DATE,endTime);
                    reqJson.put(Constant.AceWillCrm.PAGE,paramsMap.get(ScheduleConstant.AppInterface.PAGE));
                    //是否全部流水 true是 false否[默认，只查接口来源的流水]
                    reqJson.put(Constant.AceWillCrm.IS_ALL,paramsMap.get(ScheduleConstant.AppInterface.IS_ALL));
                    //是否包括分页信息
                    reqJson.put(Constant.AceWillCrm.IS_HAVE_PAGE,paramsMap.get(ScheduleConstant.AppInterface.IS_HAVE_PAGE));
                    //查询会员消费记录
                    String result = aceWillCardService.findAceWillData(url,reqJson.toString(),paramsMap);
                    System.out.println("--------------------result="+result);
                    if (StringUtils.isNotBlank(result)) {
                        JSONObject resultJson = JSONObject.parseObject(result);
                        //errcode=0，表示成功
                        if (resultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(resultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                            //修改会员消费集合
                            List<CrmConsumeDto> updateList = new ArrayList<>();
                            //新增会员消费集合
                            List<CrmConsumeDto> addList = new ArrayList<>();
                            //修改会员消费券集合
                            List<CrmConsumeCouponDto> updateCouponRecordList = new ArrayList<>();
                            //新增会员消费券集合
                            List<CrmConsumeCouponDto> addCouponRecordList = new ArrayList<>();
                            //会员消费券集合
                            List<CrmConsumeCouponDto> couponRecordList = new ArrayList<>();
                            CrmConsumeDto entityDto;
                            //接口返回的消费流水
                            List<Map<String, Object>> cardConsumeList = null;
                            //包含分页
                            if(reqJson.containsKey(Constant.AceWillCrm.IS_HAVE_PAGE) && "true".equals(reqJson.getString(Constant.AceWillCrm.IS_HAVE_PAGE))){
                                if(resultJson.get("res")!=null && !"[]".equals(resultJson.getString("res"))
                                        &&(resultJson.getJSONObject("res")).get("data")!=null&&!"[]".equals((resultJson.getJSONObject("res")).getString("data"))){
                                    JSONObject resJson =  resultJson.getJSONObject("res");
                                    cardConsumeList = (List<Map<String, Object>>) resJson.get("data");
                                    JSONObject pageOptionJson = resJson.getJSONObject("page_option");
                                    Integer  max_page = pageOptionJson.getInteger("max_page");
                                    for (int i=Constant.Number.TWO;i<=max_page;i++){
                                        reqJson.put(Constant.AceWillCrm.PAGE,String.valueOf(i));
                                        String pageResult = aceWillCardService.findAceWillData(url,reqJson.toString(),paramsMap);
                                        if (StringUtils.isNotBlank(pageResult)) {
                                            JSONObject pageResultJson = JSONObject.parseObject(pageResult);
                                            //errcode=0，表示成功
                                            if (pageResultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(pageResultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                                                if(resultJson.get("res")!=null && !"[]".equals(resultJson.getString("res"))
                                                        &&(resultJson.getJSONObject("res")).get("data")!=null&&!"[]".equals((resultJson.getJSONObject("res")).getString("data"))) {
                                                    List<Map<String, Object>> list = (List<Map<String, Object>>) (pageResultJson.getJSONObject("res")).get("data");
                                                    cardConsumeList.addAll(list);
                                                }
                                            }else{
                                                throw new RuntimeException("请求微生活crm失败...");
                                            }
                                        }
                                    }
                                }

                            }else{
                                cardConsumeList = (List<Map<String, Object>>) resultJson.get("res");
                            }
                            //当前时间
                            Date currentDate = new Date();
                            //组装会员消费id用于查询消费券记录
                            List<String> cardConsumeIds = new ArrayList<>();
                            //循环获取
                            if(cardConsumeList!=null && cardConsumeList.size()>Constant.Number.ZERO){
                                for (Map<String, Object> map : cardConsumeList) {
                                    String card_consume_id = map.get("deal_id").toString();
                                    cardConsumeIds.add(card_consume_id);
                                    //卡号
                                    String card_no = map.get("cno").toString();
                                    //消费门店id
                                    String third_shop_id = map.get("sid").toString();
                                    //消费总金额(单位:分)
                                    Double total_money = map.get("total_fee")==null?Constant.Number.ZEROD: (Double.valueOf(map.get("total_fee").toString())/Constant.Number.ONEHUNDRED);
                                    //实收金额(单位:分)--暂不知道对应哪个字段
                                    Double fee = map.get("fee")==null?Constant.Number.ZEROD: (Double.valueOf(map.get("fee").toString())/Constant.Number.ONEHUNDRED);
                                    //使用储值支付金额(单位:分)
                                    Double consume_money = map.get("stored_pay")==null?Constant.Number.ZEROD: (Double.valueOf(map.get("stored_pay").toString())/Constant.Number.ONEHUNDRED);
                                    //使用实际储值金额
                                    Double consume_prepaid_money = map.get("stored_sale_pay")==null?Constant.Number.ZEROD: (Double.valueOf(map.get("stored_sale_pay").toString())/Constant.Number.ONEHUNDRED);
                                    //使用代金券抵扣金额
                                    Double coupon_money = map.get("cash_coupon_pay")==null?Constant.Number.ZEROD: (Double.valueOf(map.get("cash_coupon_pay").toString())/Constant.Number.ONEHUNDRED);
                                    //使用礼品券抵扣金额
                                    Double coupon_gift_money = map.get("gift_coupon_pay")==null?Constant.Number.ZEROD: (Double.valueOf(map.get("gift_coupon_pay").toString())/Constant.Number.ONEHUNDRED);
                                    //使用金额数量
                                    Integer use_integral = map.get("credit_num")==null?Constant.Number.ZERO: (Integer.valueOf(map.get("credit_num").toString()));
                                    //使用积分抵扣金额(单位:分)
                                    Double integral_money = map.get("credit_pay")==null?Constant.Number.ZEROD: (Integer.valueOf(map.get("credit_pay").toString())/Constant.Number.ONEHUNDRED);
                                    //奖励积分
                                    Integer integral_gain = map.get("credit_award")==null?Constant.Number.ZERO: (Integer.valueOf(map.get("credit_award").toString()));
                                    //支付类型；
                                    String third_pay_type = map.get("pay_type")==null?Constant.Character.NULL_VALUE: map.get("pay_type").toString();
                                    //交易时间
                                    String pay_time = map.get("pay_time")==null?Constant.Character.NULL_VALUE: (map.get("pay_time").toString());
                                    //交易类型 1:充值 2:消费 3:撤销消费 4:被撤销消费 5:系统回收 6:撤销充值 7:被撤销充值 8:手工调账减少充值 9:手工调账减少积分 10:积分换礼
                                    String third_transcation_type = map.get("type")==null?Constant.Character.NULL_VALUE: (map.get("type").toString());
                                    //备注
                                    String remark = map.get("remark") == null ? Constant.Character.NULL_VALUE : map.get("remark").toString();
                                    //第三方交易id
                                    String biz_id = map.get("biz_id") == null ? Constant.Character.NULL_VALUE : map.get("biz_id").toString();
                                    //消费使用的券
                                    String user_coupons = map.get("user_coupon_list")==null?Constant.Character.NULL_VALUE:map.get("user_coupon_list").toString();
                                    entityDto = new CrmConsumeDto();
                                    entityDto.setAppId(appId);
                                    entityDto.setConsumeId(card_consume_id);
                                    entityDto.setThirdShopId(third_shop_id);
                                    entityDto.setMemberId(card_no);
                                    entityDto.setCardId(card_no);
                                    entityDto.setCardNo(card_no);
                                    entityDto.setTotalMoney(total_money);
                                    entityDto.setActualMoney(fee);
                                    entityDto.setConsumeMoney(consume_money);
                                    entityDto.setConsumePrepaidMoney(consume_prepaid_money);
                                    BigDecimal consumeMoneyB = new BigDecimal(consume_money);
                                    BigDecimal consumePrepaidMoneyB = new BigDecimal(consume_prepaid_money);
                                    Double consumeLargessMoneyD = (consumeMoneyB.subtract(consumePrepaidMoneyB)).doubleValue();
                                    entityDto.setConsumeLargessMoney(consumeLargessMoneyD);
                                    entityDto.setCouponMoney(coupon_money);
                                    entityDto.setCouponGiftMoney(coupon_gift_money);
                                    entityDto.setUseIntegral(use_integral);
                                    entityDto.setIntegralMoney(integral_money);
                                    entityDto.setIntegralGain(integral_gain);
                                    entityDto.setConsumeTime(pay_time);
                                    entityDto.setEnteId(enteId);
                                    entityDto.setPayTypeId(Constant.Character.NULL_VALUE);
                                    entityDto.setThirdPayTypeId(third_pay_type);
                                    entityDto.setTransactionTypeId(Constant.Character.NULL_VALUE);
                                    entityDto.setThirdTransactionTypeId(third_transcation_type);
                                    entityDto.setRemark(remark);
                                    entityDto.setCreateTime(currentDate);
                                    entityDto.setUpdateTime(currentDate);
                                    addList.add(entityDto);
                                    JSONArray useCouponsJsonArr = new JSONArray();
                                    if(!Constant.Character.NULL_VALUE.equals(user_coupons)&&!"[]".equals(user_coupons)){ //消费使用的券
                                        useCouponsJsonArr = JSONArray.parseArray(user_coupons);
                                        for (int i=Constant.Number.ZERO;i<useCouponsJsonArr.size();i++) {
                                            JSONObject coupon = useCouponsJsonArr.getJSONObject(i);
                                            Integer num = coupon.get("num")==null?Constant.Number.ZERO:Integer.valueOf(coupon.get("num").toString());
                                            String coupon_id = coupon.get("couponId")==null?Constant.Character.NULL_VALUE:coupon.get("couponId").toString();
                                            String coupon_name = coupon.get("name")==null?Constant.Character.NULL_VALUE:coupon.get("name").toString();
                                            Double amount = coupon.get("amount")==null?Constant.Number.ZEROD:Double.valueOf(coupon.get("amount").toString());
                                            CrmConsumeCouponDto couponRecordDto = new CrmConsumeCouponDto();
                                            couponRecordDto.setConsumeCouponId(idWorker.nextId());
                                            couponRecordDto.setConsumeId(card_consume_id);
                                            couponRecordDto.setAppId(appId);
                                            couponRecordDto.setEnteId(enteId);
                                            couponRecordDto.setThirdShopId(third_shop_id);
                                            couponRecordDto.setCardNo(card_no);
                                            couponRecordDto.setNum(num);
                                            couponRecordDto.setUseTime(pay_time);
                                            couponRecordDto.setCouponId(coupon_id);
                                            couponRecordDto.setCouponName(coupon_name);
                                            couponRecordDto.setDiscountMoney(amount);
                                            couponRecordDto.setTotalDiscountMoney(amount);
                                            couponRecordDto.setCreateTime(currentDate);
                                            couponRecordDto.setUpdateTime(currentDate);
                                            addCouponRecordList.add(couponRecordDto);
                                        }
                                    }
                                }
                                crmCardConsumeDto.setConsumeIds(cardConsumeIds);
                                //根据enteId/appid查询消费流水
                                List<CrmConsumeVo> existList = crmConsumeTestMapper.findCrmCardConsumeBatchTest(crmCardConsumeDto);
                                //判断消费流水是否已经存在  已存在数据是否变更
                                if(existList!=null && existList.size()>Constant.Number.ZERO){
                                    Map<String,Object> existConsumeMap = new HashMap<>();
                                    for (CrmConsumeVo crmCardConsumeVo : existList) {
                                        String consume_id = crmCardConsumeVo.getConsumeId();
                                        String third_shop_id = crmCardConsumeVo.getThirdShopId();
                                        //consume_id+"_"+third_shop_id为key，标识唯一
                                        existConsumeMap.put(consume_id+"_"+third_shop_id,crmCardConsumeVo);
                                    }
                                    //会员消费集合
                                    List<CrmConsumeDto> consumeList = new ArrayList<>();
                                    consumeList.addAll(addList);
                                    for (CrmConsumeDto consumeDto : consumeList) {
                                        //判断消费流水是否存在
                                        if (existConsumeMap.containsKey(consumeDto.getConsumeId()+"_"+consumeDto.getThirdShopId())
                                                &&existConsumeMap.get(consumeDto.getConsumeId()+"_"+consumeDto.getThirdShopId())!=null) {
                                            //去除相同的数据
                                            addList.remove(consumeDto);
                                            updateList.add(consumeDto);
                                        }
                                    }
                                }
                                couponRecordList.addAll(addCouponRecordList);
                                //根据消费记录查询consume_coupon使用券记录
                                if(cardConsumeIds.size()>Constant.Number.ZERO){
                                    //设置已有数据的查询条件
                                    CrmConsumeCouponDto crmConsumeCouponDto = new CrmConsumeCouponDto();
                                    crmConsumeCouponDto.setEnteId(enteId);
                                    crmConsumeCouponDto.setAppId(appId);
                                    crmConsumeCouponDto.setConsumeIds(cardConsumeIds);
                                    List<CrmConsumeCouponVo> existCouponRecordList=crmConsumeCouponTestMapper.findCrmConsumeCouponBatch(crmConsumeCouponDto);
                                    if(existCouponRecordList!=null && existCouponRecordList.size()>Constant.Number.ZERO){
                                        Map<String,CrmConsumeCouponVo> existConsumeCouponMap = new HashMap<>();
                                        for (CrmConsumeCouponVo crmConsumeCouponVo : existCouponRecordList) {
                                            String consume_id = crmConsumeCouponVo.getConsumeId();
                                            String third_shop_id = crmConsumeCouponVo.getThirdShopId();
                                            String coupon_id = crmConsumeCouponVo.getCouponId();
                                            //consume_id+"_"+third_shop_id+"_"+coupon_id为key，此处标识不唯一，需重新考虑
                                            existConsumeCouponMap.put(consume_id+"_"+third_shop_id+"_"+coupon_id,crmConsumeCouponVo);
                                        }
                                        for (CrmConsumeCouponDto consumeCouponDto : couponRecordList) {
                                            //判断消费使用券记录是否存在
                                            if (existConsumeCouponMap.containsKey(consumeCouponDto.getConsumeId()+"_"+consumeCouponDto.getThirdShopId()+"_"+consumeCouponDto.getCouponId())
                                                    &&existConsumeCouponMap.get(consumeCouponDto.getConsumeId()+"_"+consumeCouponDto.getThirdShopId()+"_"+consumeCouponDto.getCouponId())!=null) {
                                                CrmConsumeCouponVo crmConsumeCouponVo =  existConsumeCouponMap.get(consumeCouponDto.getConsumeId());
                                                consumeCouponDto.setConsumeCouponId(crmConsumeCouponVo.getConsumeCouponId());
                                                //去除相同的数据
                                                addCouponRecordList.remove(consumeCouponDto);
                                                CrmConsumeCouponVo consumeCouponVo = existConsumeCouponMap.get(consumeCouponDto.getConsumeId()+"_"+consumeCouponDto.getThirdShopId()+"_"+consumeCouponDto.getCouponId());
                                                String consume_coupon_id = consumeCouponVo.getConsumeCouponId();
                                                consumeCouponDto.setConsumeCouponId(consume_coupon_id);
                                                updateCouponRecordList.add(consumeCouponDto);
                                            }
                                        }
                                    }
                                }
                                //处理消费流水
                                if (updateList.size() > Constant.Number.ZERO) {
                                    //批量修改
                                    crmConsumeTestMapper.updateCrmCardConsumeTest(updateList);
                                }
                                if (addList.size() > Constant.Number.ZERO) {
                                    //批量新增
                                    crmConsumeTestMapper.addCrmCardConsumeTest(addList);
                                }
                                //处理消费使用券记录
                                if (updateCouponRecordList.size() > Constant.Number.ZERO) {
                                    //批量修改
                                    crmConsumeCouponTestMapper.updateCrmConsumeCoupon(updateCouponRecordList);
                                }
                                if (addCouponRecordList.size() > Constant.Number.ZERO) {
                                    //批量新增
                                    crmConsumeCouponTestMapper.addCrmConsumeCoupon(addCouponRecordList);
                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 同步微生活会员充值数据
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws UnsupportedEncodingException
     */
    @Override
    public void addOrUpdatePrepaid(String appId,String enteId,Map<String,Object> paramsMap) {
        try{
            CrmPrepaidDto crmPrepaidDto = new CrmPrepaidDto();
            crmPrepaidDto.setEnteId(enteId);
            crmPrepaidDto.setAppId(appId);
            JSONArray jsonArr = JSONArray.parseArray(paramsMap.get("date").toString());
            for (int j=0;j<jsonArr.size();j++) {
                JSONObject jsonObject = jsonArr.getJSONObject(j);
                String startTime = jsonObject.getString("startTime");
                String endTime = jsonObject.getString("endTime");
                if(appId!=null&&enteId!=null) {

                    //查询会员充值记录
                    String url = paramsMap.get(ScheduleConstant.AppInterface.URL).toString()+prepaid_list_url;
                    //请求接口参数
                    JSONObject reqJson = new JSONObject();
                    reqJson.put(Constant.AceWillCrm.BEGIN_DATE,startTime);
                    reqJson.put(Constant.AceWillCrm.END_DATE,endTime);
                    reqJson.put(Constant.AceWillCrm.PAGE,paramsMap.get(ScheduleConstant.AppInterface.PAGE));
                    //是否全部流水 true是 false否[默认，只查接口来源的流水]
                    reqJson.put(Constant.AceWillCrm.IS_ALL,paramsMap.get(ScheduleConstant.AppInterface.IS_ALL));
                    String result = aceWillCardService.findAceWillData(url, reqJson.toString(),paramsMap);
                    if (StringUtils.isNotBlank(result)) {
                        JSONObject resultJson = JSONObject.parseObject(result);
                        //errcode=0，表示成功
                        if (resultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(resultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                            //修改充值记录集合
                            List<CrmPrepaidDto> updateList = new ArrayList<>();
                            //新增充值记录支付明细集合
                            List<CrmPrepaidPayTypeDto> addPrepaidPayTypeList = new ArrayList<>();
                            //修改充值记录支付明细集合
                            List<CrmPrepaidPayTypeDto> updatePrepaidPayTypeList = new ArrayList<>();
                            //新增充值记录集合
                            List<CrmPrepaidDto> addList = new ArrayList<>();
                            //修改会员储值赠送券集合
                            List<CrmPrepaidCouponDto> updatePrepaidCouponList = new ArrayList<>();
                            //新增会员储值赠送券集合
                            List<CrmPrepaidCouponDto> addPrepaidCouponList = new ArrayList<>();
                            //接口返回的充值记录
                            List<Map<String, Object>> cardPrepaidList = null;
                            JSONObject resJson = resultJson.getJSONObject("res");
                            cardPrepaidList = (List<Map<String, Object>>) resJson.get("data");
                            //JSONObject pageOptionJson = resJson.getJSONObject("pageOptions");
                            //储值流水接口未返回最大页面，默认查询100页，如任意页面返回的数据<50条或未返回数据则跳出循环
                            Integer max_page = Constant.Number.ONETHOUSAND;
                            for (int i = Constant.Number.TWO; i <= max_page; i++) {
                                reqJson.put(Constant.AceWillCrm.PAGE, String.valueOf(i));
                                String pageResult = aceWillCardService.findAceWillData(url, reqJson.toString(),paramsMap);
                                if (StringUtils.isNotBlank(pageResult)) {
                                    JSONObject pageResultJson = JSONObject.parseObject(pageResult);
                                    //errcode=0，表示成功
                                    if (pageResultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(pageResultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                                        if (!"[]".equals((pageResultJson.getJSONObject("res")).get("data").toString())) {
                                            List<Map<String, Object>> list = (List<Map<String, Object>>) (pageResultJson.getJSONObject("res")).get("data");
                                            cardPrepaidList.addAll(list);
                                        } else {
                                            break;
                                        }
                                    } else {
                                        throw new RuntimeException("请求微生活crm失败...");
                                    }
                                } else {
                                    break;
                                }
                            }
                            //当前时间
                            Date currentDate = new Date();
                            //组装会员储值id用于查询储值赠送券记录
                            List<String> cardPrepaidIds = new ArrayList<>();
                            //循环获取
                            if(cardPrepaidList!=null && cardPrepaidList.size()>Constant.Number.ZERO){
                                for (Map<String, Object> map : cardPrepaidList) {
                                    //{"charge_id":"1628062427580882","cno":"1115410920792894","sid":"999999999","total_fee":"50000","fee":"50000","award_fee":"0","type":"1","pay_time":"2019-03-15 17:15:22","pay_type":"6","has_receipt":"0","cashier_id":"1209398233","remark":"1","grade":"3014758","grade_name":"黄金贵宾卡","award_coupons":[],"biz_id":"","mixed_pay_info":null,"award_credit":0}
                                    String card_prepaid_id = map.get("charge_id").toString();
                                    cardPrepaidIds.add(card_prepaid_id);
                                    //卡号
                                    String card_no = map.get("cno").toString();
                                    //消费门店id
                                    String third_shop_id = map.get("sid").toString();
                                    //充值总金额(单位:分)
                                    Double money = map.get("total_fee") == null ? Constant.Number.ZEROD : (Double.valueOf(map.get("total_fee").toString()) / Constant.Number.ONEHUNDRED);
                                    //实收金额(单位:分)
                                    Double prepaid_money = map.get("fee") == null ? Constant.Number.ZEROD: (Double.valueOf(map.get("fee").toString()) / Constant.Number.ONEHUNDRED);
                                    //奖励金额
                                    Double largess_money = map.get("award_fee") == null ? Constant.Number.ZEROD : (Double.valueOf(map.get("award_fee").toString()) / Constant.Number.ONEHUNDRED);
                                    //奖励券
                                    String award_coupons = map.get("award_coupons") == null ? Constant.Character.NULL_VALUE : map.get("award_coupons").toString();
                                    //交易类型
                                    String third_transaction_type = map.get("type") == null ? Constant.Character.NULL_VALUE  : map.get("type").toString();
                                    //支付类型
                                    String third_pay_type = appId+(map.get("pay_type") == null ? Constant.Character.NULL_VALUE  : map.get("pay_type").toString());
                                    //交易时间
                                    String pay_time = map.get("pay_time") == null ? Constant.Character.NULL_VALUE  : (map.get("pay_time").toString());
                                    //是否开票
                                    Integer is_invoice = map.get("has_receipt") == null ? Constant.Number.ZERO : (Integer.valueOf(map.get("has_receipt").toString()));
                                    //操作人id
                                    String third_creator_id = map.get("cashier_id") == null ? Constant.Character.NULL_VALUE  : map.get("cashier_id").toString();
                                    //{“支付类型值”:支付金额(分)} 【注：只有支付类型为pay_type=99的，才会有此值，否则为null】
                                    String mixed_pay_info = map.get("mixed_pay_info") == null ? Constant.Character.NULL_VALUE  : map.get("mixed_pay_info").toString();
                                    //备注
                                    String remark = map.get("remark") == null ? Constant.Character.NULL_VALUE  : map.get("remark").toString();
                                    //用户等级id
                                    String grade = map.get("grade") == null ? Constant.Character.NULL_VALUE  : map.get("grade").toString();
                                    //用户等级名称
                                    String grade_name = map.get("grade_name") == null ? Constant.Character.NULL_VALUE  : map.get("grade_name").toString();
                                    //第三方交易id
                                    String biz_id = map.get("biz_id") == null ? Constant.Character.NULL_VALUE  : map.get("biz_id").toString();
                                    CrmPrepaidDto entityDto = new CrmPrepaidDto();
                                    entityDto.setAppId(appId);
                                    entityDto.setPrepaidId(card_prepaid_id);
                                    entityDto.setThirdShopId(third_shop_id);
                                    entityDto.setCardId(card_no);
                                    entityDto.setCardNo(card_no);
                                    entityDto.setMoney(money);
                                    entityDto.setPrepaidMoney(prepaid_money);
                                    entityDto.setLargessMoney(largess_money);
                                    entityDto.setMarketLargessMoney(Constant.Number.ZEROD);
                                    entityDto.setPayInfo(mixed_pay_info);
                                    entityDto.setThirdPayTypeId(third_pay_type);
                                    entityDto.setThirdTransactionTypeId(third_transaction_type);
                                    entityDto.setPrepaidTime(pay_time);
                                    entityDto.setRemark(remark);
                                    entityDto.setEnteId(enteId);
                                    entityDto.setThirdCreatorId(third_creator_id);
                                    entityDto.setIsInvoice(is_invoice);
                                    entityDto.setCreateTime(currentDate);
                                    entityDto.setUpdateTime(currentDate);
                                    addList.add(entityDto);
                                    //99为混合支付
                                    if ((appId+"99").equals(third_pay_type)) {
                                        JSONObject payInfoJo = JSONObject.parseObject(mixed_pay_info);
                                        for (String thirdPayTypeId : payInfoJo.keySet()) {
                                            Double pay_money = payInfoJo.get(thirdPayTypeId) == null ? Constant.Number.ZEROD: (Double.valueOf(payInfoJo.get(thirdPayTypeId).toString())/ Constant.Number.ONEHUNDRED);
                                            //充值支付明细
                                            CrmPrepaidPayTypeDto payTypeDto = new CrmPrepaidPayTypeDto();
                                            payTypeDto.setPrepaidPayTypeId(idWorker.nextId());
                                            payTypeDto.setAppId(appId);
                                            payTypeDto.setPrepaidId(card_prepaid_id);
                                            payTypeDto.setThirdShopId(third_shop_id);
                                            payTypeDto.setMoney(pay_money);
                                            payTypeDto.setThirdPayTypeId(appId+thirdPayTypeId);
                                            payTypeDto.setEnteId(enteId);
                                            payTypeDto.setPrepaidTime(pay_time);
                                            payTypeDto.setCreateTime(currentDate);
                                            payTypeDto.setUpdateTime(currentDate);
                                            addPrepaidPayTypeList.add(payTypeDto);
                                        }
                                    } else {
                                        //充值支付明细
                                        CrmPrepaidPayTypeDto payTypeDto = new CrmPrepaidPayTypeDto();
                                        payTypeDto.setPrepaidPayTypeId(idWorker.nextId());
                                        payTypeDto.setAppId(appId);
                                        payTypeDto.setPrepaidId(card_prepaid_id);
                                        payTypeDto.setThirdShopId(third_shop_id);
                                        payTypeDto.setMoney(prepaid_money);
                                        payTypeDto.setThirdPayTypeId(third_pay_type);
                                        payTypeDto.setEnteId(enteId);
                                        payTypeDto.setPrepaidTime(pay_time);
                                        payTypeDto.setCreateTime(currentDate);
                                        payTypeDto.setUpdateTime(currentDate);
                                        addPrepaidPayTypeList.add(payTypeDto);
                                    }
                                    JSONArray awardCouponsJsonArr = new JSONArray();
                                    if (!Constant.Character.NULL_VALUE.equals(award_coupons) && !"[]".equals(award_coupons)) { //储值赠送的券
                                        awardCouponsJsonArr = JSONArray.parseArray(award_coupons);
                                        for (int i = Constant.Number.ZERO; i < awardCouponsJsonArr.size(); i++) {
                                            JSONObject coupon = awardCouponsJsonArr.getJSONObject(i);
                                            Integer num = coupon.get("num") == null ? Constant.Number.ZERO : Integer.valueOf(coupon.get("num").toString());
                                            String coupon_id = coupon.get("couponId") == null ? Constant.Character.NULL_VALUE : coupon.get("couponId").toString();
                                            String coupon_name = coupon.get("name") == null ? Constant.Character.NULL_VALUE  : coupon.get("name").toString();
                                            Double amount = coupon.get("amount") == null ? Constant.Number.ZEROD : Double.valueOf(coupon.get("amount").toString());
                                            CrmPrepaidCouponDto couponRecordDto = new CrmPrepaidCouponDto();
                                            couponRecordDto.setPrepaidCouponId(idWorker.nextId());
                                            couponRecordDto.setPrepaidId(card_prepaid_id);
                                            couponRecordDto.setCardNo(card_no);
                                            couponRecordDto.setNum(num);
                                            couponRecordDto.setGiveTime(pay_time);
                                            couponRecordDto.setCouponId(coupon_id);
                                            couponRecordDto.setCouponName(coupon_name);
                                            couponRecordDto.setMoney(amount);
                                            couponRecordDto.setTotalMoney(amount * num);
                                            couponRecordDto.setCreateTime(currentDate);
                                            couponRecordDto.setUpdateTime(currentDate);
                                            addPrepaidCouponList.add(couponRecordDto);
                                        }
                                    }
                                }

                                crmPrepaidDto.setPrepaidIds(cardPrepaidIds);
                                //根据appId,enteId,cardPrepaidIds查询充值记录
                                List<CrmPrepaidVo> existList = crmPrepaidInitMapper.findCrmCardPrepaidBatchInit(crmPrepaidDto);
                                //根据appId,enteId,cardPrepaidIds查询充值记录支付明细
                                List<CrmPrepaidPayTypeVo> existPayTypeList = crmPrepaidInitMapper.findPrepaidPayTypeBatchInit(crmPrepaidDto);
                                //根据appId,enteId,cardPrepaidIds查询充值赠送券记录
                                //设置已有数据的查询条件
                                CrmPrepaidCouponDto crmPrepaidCouponDto = new CrmPrepaidCouponDto();
                                crmPrepaidCouponDto.setEnteId(enteId);
                                crmPrepaidCouponDto.setAppId(appId);
                                crmPrepaidCouponDto.setPrepaidIds(cardPrepaidIds);
                                List<CrmPrepaidCouponVo> existPrepaidCouponList = crmPrepaidCouponInitMapper.findCrmPrepaidCouponBatchInit(crmPrepaidCouponDto);
                                //判断充值流水是否已经存在  已存在数据是否变更
                                if (existList != null && existList.size() > Constant.Number.ZERO) {
                                    Map<String, Object> existPrepaidMap = new HashMap<>();
                                    for (CrmPrepaidVo crmCardPrepaidVo : existList) {
                                        String prepaid_id = crmCardPrepaidVo.getPrepaidId();
                                        String third_shop_id = crmCardPrepaidVo.getThirdShopId();
                                        existPrepaidMap.put(prepaid_id+"_"+third_shop_id, crmCardPrepaidVo);
                                    }
                                    List<CrmPrepaidDto> prepaidList = new ArrayList<>();
                                    prepaidList.addAll(addList);
                                    for (CrmPrepaidDto prepaidDto : prepaidList) {
                                        //判断充值流水是否存在
                                        if (existPrepaidMap.containsKey(prepaidDto.getPrepaidId()+"_"+prepaidDto.getThirdShopId())
                                                && existPrepaidMap.get(prepaidDto.getPrepaidId()+"_"+prepaidDto.getThirdShopId()) != null) {
                                            //去除相同的数据
                                            addList.remove(prepaidDto);
                                            updateList.add(prepaidDto);
                                        }
                                    }
                                }
                                //充值支付详情
                                if (existPayTypeList != null && existPayTypeList.size() > Constant.Number.ZERO) {
                                    Map<String, CrmPrepaidPayTypeVo> existPrepaidPayTypeMap = new HashMap<>();
                                    for (CrmPrepaidPayTypeVo crmCardPrepaidVo : existPayTypeList) {
                                        String prepaid_id = crmCardPrepaidVo.getPrepaidId();
                                        String third_shop_id = crmCardPrepaidVo.getPrepaidId();
                                        String third_pay_type_id = crmCardPrepaidVo.getThirdPayTypeId();
                                        //充值id_第三方充值门店id_第三方支付方式id作为key
                                        existPrepaidPayTypeMap.put(prepaid_id+"_"+third_shop_id+"_"+third_pay_type_id, crmCardPrepaidVo);
                                    }
                                    List<CrmPrepaidPayTypeDto> prepaidPayTypeList = new ArrayList<>();
                                    prepaidPayTypeList.addAll(addPrepaidPayTypeList);
                                    for (CrmPrepaidPayTypeDto prepaidPayTypeDto : prepaidPayTypeList) {
                                        //判断充值支付明细是否存在
                                        if (existPrepaidPayTypeMap.containsKey(prepaidPayTypeDto.getPrepaidId()+"_"+prepaidPayTypeDto.getThirdShopId()+"_"+prepaidPayTypeDto.getThirdPayTypeId())
                                                && existPrepaidPayTypeMap.get(prepaidPayTypeDto.getPrepaidId()+"_"+prepaidPayTypeDto.getThirdShopId()+"_"+prepaidPayTypeDto.getThirdPayTypeId()) != null) {
                                            //去除相同的数据
                                            addPrepaidPayTypeList.remove(prepaidPayTypeDto);
                                            CrmPrepaidPayTypeVo prepaidPayTypeVo = existPrepaidPayTypeMap.get(prepaidPayTypeDto.getPrepaidId()+"_"+prepaidPayTypeDto.getThirdShopId()+"_"+prepaidPayTypeDto.getThirdPayTypeId());
                                            String prepaid_pay_type_id = prepaidPayTypeVo.getPrepaidPayTypeId();
                                            prepaidPayTypeDto.setPrepaidPayTypeId(prepaid_pay_type_id);
                                            updatePrepaidPayTypeList.add(prepaidPayTypeDto);
                                        }
                                    }
                                }
                                //充值赠送券记录
                                if (existPrepaidCouponList != null && existPrepaidCouponList.size() > Constant.Number.ZERO) {
                                    Map<String, CrmPrepaidCouponVo> existPrepaidCouponMap = new HashMap<>();
                                    for (CrmPrepaidCouponVo crmPrepaidCouponVo : existPrepaidCouponList) {
                                        String prepaid_id = crmPrepaidCouponVo.getPrepaidId();
                                        String third_shop_id = crmPrepaidCouponVo.getThirdShopId();
                                        String coupon_id = crmPrepaidCouponVo.getCouponId();
                                        //consume_id+"_"+third_shop_id+"_"+coupon_id为key，此处标识不唯一，需重新考虑
                                        existPrepaidCouponMap.put(prepaid_id+"_"+third_shop_id+"_"+coupon_id, crmPrepaidCouponVo);
                                    }
                                    List<CrmPrepaidCouponDto> prepaidCouponList = new ArrayList<>();
                                    prepaidCouponList.addAll(addPrepaidCouponList);
                                    for (CrmPrepaidCouponDto prepaidCopuponDto : prepaidCouponList) {
                                        //判断充值赠送全是否存在
                                        if (existPrepaidCouponMap.containsKey(prepaidCopuponDto.getPrepaidId()+"_"+ crmPrepaidCouponDto.getThirdShopId()+"_"+crmPrepaidCouponDto.getCouponId())
                                                && existPrepaidCouponMap.get(prepaidCopuponDto.getPrepaidId()+"_"+ crmPrepaidCouponDto.getThirdShopId()+"_"+crmPrepaidCouponDto.getCouponId()) != null) {
                                            CrmPrepaidCouponVo prepaidCouponVo = existPrepaidCouponMap.get(prepaidCopuponDto.getPrepaidId());
                                            prepaidCopuponDto.setPrepaidCouponId(prepaidCouponVo.getPrepaidCouponId());
                                            //去除相同的数据
                                            addPrepaidCouponList.remove(prepaidCopuponDto);
                                            updatePrepaidCouponList.add(prepaidCopuponDto);
                                        }
                                    }
                                }
                                //充值流水
                                if (updateList.size() > Constant.Number.ZERO) {
                                    //批量修改
                                    crmPrepaidInitMapper.updateCrmCardPrepaidInit(updateList);
                                }
                                if (addList.size() > Constant.Number.ZERO) {
                                    //批量新增
                                    crmPrepaidInitMapper.addCrmCardPrepaidInit(addList);
                                }
                                //充值流水支付明细
                                if (updatePrepaidPayTypeList.size() > Constant.Number.ZERO) {
                                    //批量修改
                                    crmPrepaidInitMapper.updatePrepaidPayTypeInit(updatePrepaidPayTypeList);
                                }
                                if (addPrepaidPayTypeList.size() > Constant.Number.ZERO) {
                                    //批量新增
                                    crmPrepaidInitMapper.addPrepaidPayTypeInit(addPrepaidPayTypeList);
                                }
                                //充值赠送券记录
                                if (updatePrepaidCouponList.size() > Constant.Number.ZERO) {
                                    //批量修改
                                    crmPrepaidCouponInitMapper.updateCrmPrepaidCouponInit(updatePrepaidCouponList);
                                }
                                if (addPrepaidCouponList.size() > Constant.Number.ZERO) {
                                    //批量新增
                                    crmPrepaidCouponInitMapper.addCrmPrepaidCouponInit(addPrepaidCouponList);
                                }
                            }
                        }
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

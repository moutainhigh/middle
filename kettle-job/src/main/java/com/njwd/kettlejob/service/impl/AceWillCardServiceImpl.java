package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.kettlejob.dto.*;
import com.njwd.entity.kettlejob.vo.*;
import com.njwd.entity.schedule.vo.TaskParamVo;
import com.njwd.kettlejob.service.AceWillCardService;
import com.njwd.kettlejob.service.TaskParamService;
import com.njwd.mapper.*;
import com.njwd.utils.DateUtils;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.SignUtil;
import com.njwd.utils.StringUtil;
import com.njwd.utils.idworker.IdWorker;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Description
 * @Author: ljc
 * @Date: 2019/11/12
 */
@Service
public class AceWillCardServiceImpl implements AceWillCardService {
    @Value("${aceWill.url.shop_list}")
    private String shop_list_url;
    @Value("${aceWill.url.consume_list}")
    private String consume_list_url;
    @Value("${aceWill.url.prepaid_list}")
    private String prepaid_list_url;
    @Value("${aceWill.url.grade_list}")
    private String grade_list_url;
    @Value("${aceWill.url.pay_type_list}")
    private String pay_type_rul;
    @Resource
    private BaseShopRelaMapper baseShopRelaMapper;
    @Resource
    private CrmConsumeMapper crmConsumeMapper;
    @Resource
    private CrmPrepaidMapper crmPrepaidMapper;
    @Resource
    private CrmCardGradeMapper crmCardGradeMapper;
    @Resource
    private CrmConsumeCouponMapper crmConsumeCouponMapper;
    @Resource
    private BasePayTypeRelaMapper basePayTypeRelaMapper;
    @Resource
    private CrmPrepaidCouponMapper crmPrepaidCouponMapper;
    @Resource
    private TaskParamService taskParamService;
    @Resource
    private IdWorker idWorker;

    /**
     * 获取微生活门店数据并写入
     * @param paramsMap
     */
    @Override
    @Transactional
    public String addOrUpdateShopRela(String appId,String enteId,Map<String,Object> paramsMap) {
        JSONObject taskResJson = new JSONObject();
        try{
            if(appId!=null&&enteId!=null){
                String url = paramsMap.get(ScheduleConstant.AppInterface.URL).toString()+shop_list_url;
                //调用微生活接口的传参;(门店无参数，传空json字符串)
                JSONObject reqJson = new JSONObject();
                String result = findAceWillData(url,reqJson.toString(),paramsMap);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject resultJson = JSONObject.parseObject(result);
                    //errcode=0，表示成功
                    if(resultJson.containsKey(Constant.AceWillCrm.ERRCODE)&&Constant.Character.String_ZERO.equals(resultJson.getString(Constant.AceWillCrm.ERRCODE))){
                        //设置已有门店查询条件
                        BaseShopRelaDto baseShopRelaDto=new BaseShopRelaDto();
                        baseShopRelaDto.setAppId(appId);
                        baseShopRelaDto.setEnteId(enteId);
                        //查询的所有已有门店
                        List<BaseShopRelaVo> existShopList=baseShopRelaMapper.findBaseShopRelaBatch(baseShopRelaDto);
                        //修改门店集合
                        List<BaseShopRelaDto> updateList=new ArrayList<>();
                        //新增门店集合
                        List<BaseShopRelaDto> addList=new ArrayList<>();
                        BaseShopRelaDto baseShopRela;
                        //获取的所有门店
                        List<Map<String, Object>> shopList = (List<Map<String, Object>>) resultJson.get("res");
                        //当前时间
                        Date currentDate = new Date();
                        //循环处理门店数据
                        for(Map<String, Object> map : shopList){
                            String third_shop_id = map.get("shop_id").toString();
                            String shop_name = map.get("shop_name")==null?Constant.Character.NULL_VALUE:map.get("shop_name").toString();
                            String shop_address = map.get("shop_address")==null?Constant.Character.NULL_VALUE:map.get("shop_address").toString();
                            String shop_latitude = map.get("shop_latitude")==null?Constant.Character.NULL_VALUE:map.get("shop_latitude").toString();
                            String shop_longitude = map.get("shop_longitude")==null?Constant.Character.NULL_VALUE:map.get("shop_longitude").toString();
                            baseShopRela = new BaseShopRelaDto();
                            baseShopRela.setThirdShopId(third_shop_id);
                            baseShopRela.setShopName(shop_name);
                            baseShopRela.setAddress(shop_address);
                            baseShopRela.setShopLat(shop_latitude);
                            baseShopRela.setShopLon(shop_longitude);
                            baseShopRela.setThirdEnteId(enteId);
                            baseShopRela.setEnteId(enteId);
                            baseShopRela.setAppId(appId);
                            baseShopRela.setUpdateTime(currentDate);
                            addList.add(baseShopRela);
                        }
                        if(existShopList!=null && existShopList.size()> Constant.Number.ZERO){
                            Map<String,BaseShopRelaVo> existShopMap = new HashMap<>();
                            for(BaseShopRelaVo baseShopRelaVo:existShopList){
                                String third_shop_id = baseShopRelaVo.getThirdShopId();
                                existShopMap.put(third_shop_id,baseShopRelaVo);
                            }
                            List<BaseShopRelaDto> shopRelaList=new ArrayList<>();
                            shopRelaList.addAll(addList);
                            //判断门店数据是否已经存在  已存在数据是否变更
                            for(BaseShopRelaDto shopRelaDto : shopRelaList){
                                String third_shop_id = shopRelaDto.getThirdShopId();
                                //判断第三方id是否存在
                                if(existShopMap.containsKey(third_shop_id)&&existShopMap.get(third_shop_id)!=null){
                                    //去除新增门店中ID相同的数据
                                    addList.remove(shopRelaDto);
                                    //id相同编码和名称不同添加修改门店
                                    updateList.add(shopRelaDto);
                                }
                            }
                        }
                        if(updateList.size()> Constant.Number.ZERO){
                            //批量修改
                            baseShopRelaMapper.updateBaseShopRela(updateList);
                        }
                        if(addList.size()> Constant.Number.ZERO){
                            //批量新增
                            baseShopRelaMapper.addBaseShopRela(addList);
                        }
                    }
                }
            }
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        taskResJson.put(Constant.TaskResult.PARAM,Constant.Character.NULL_VALUE);
        taskResJson.put(Constant.TaskResult.MSG,Constant.Character.NULL_VALUE);
        return taskResJson.toString();
    }

    /**
     * 同步微生活会员消费数据
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws UnsupportedEncodingException
     */
    @Override
    @Transactional
    public String addOrUpdateConsume(String appId,String enteId,Map<String,Object> paramsMap) {
        JSONObject taskResJson = new JSONObject();
        try{
            CrmConsumeDto crmCardConsumeDto = new CrmConsumeDto();
            crmCardConsumeDto.setEnteId(enteId);
            crmCardConsumeDto.setAppId(appId);
            String startTime = DateUtils.getCurrentDate(DateUtils.PATTERN_DAY);
            String endTime = (paramsMap.containsKey(ScheduleConstant.AppInterface.ENDTIME)&&StringUtil.isNotBlank(paramsMap.get(ScheduleConstant.AppInterface.ENDTIME).toString()))?paramsMap.get(ScheduleConstant.AppInterface.ENDTIME).toString():DateUtils.getCurrentDate(DateUtils.PATTERN_MINUTE);
            if(paramsMap.containsKey(ScheduleConstant.AppInterface.STRATTIME)&&StringUtil.isNotBlank(paramsMap.get(ScheduleConstant.AppInterface.STRATTIME).toString())){
                startTime = paramsMap.get(ScheduleConstant.AppInterface.STRATTIME).toString();
            }else{
                String maxTime=crmConsumeMapper.findMaxConsumeTime(crmCardConsumeDto);
                if(StringUtils.isNotBlank(maxTime)){
                    Date maxTimeDate = DateUtils.parseDate(maxTime,DateUtils.PATTERN_SECOND);
                    //五分钟前的时间
                    Date subMinutes = DateUtils.subMinutes(maxTimeDate,5);
                    maxTime = DateUtils.dateConvertString(subMinutes,DateUtils.PATTERN_SECOND);
                    startTime=maxTime;
                }
            }
            System.out.println("prepiad----------------------------startTime="+startTime+"------------------endTime="+endTime);
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
                String result = findAceWillData(url,reqJson.toString(),paramsMap);
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
                            if (resultJson.get("res")!=null && !"[]".equals(resultJson.getString("res"))
                                    &&(resultJson.getJSONObject("res")).get("data")!=null&&!"[]".equals((resultJson.getJSONObject("res")).getString("data"))){
                                JSONObject resJson =  resultJson.getJSONObject("res");
                                cardConsumeList = (List<Map<String, Object>>) resJson.get("data");
                                JSONObject pageOptionJson = resJson.getJSONObject("page_option");
                                Integer  max_page = pageOptionJson.getInteger("max_page");
                                for (int i=Constant.Number.TWO;i<=max_page;i++){
                                    reqJson.put(Constant.AceWillCrm.PAGE,String.valueOf(i));
                                    String pageResult = findAceWillData(url,reqJson.toString(),paramsMap);
                                    if (StringUtils.isNotBlank(pageResult)) {
                                        JSONObject pageResultJson = JSONObject.parseObject(pageResult);
                                        //errcode=0，表示成功
                                        if (pageResultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(pageResultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                                            List<Map<String, Object>> list = (List<Map<String, Object>>) (pageResultJson.getJSONObject("res")).get("data");
                                            cardConsumeList.addAll(list);
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
                                        couponRecordDto.setCardId(card_no);
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
                            List<CrmConsumeVo> existList = crmConsumeMapper.findCrmCardConsumeBatch(crmCardConsumeDto);
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
                                List<CrmConsumeCouponVo> existCouponRecordList=crmConsumeCouponMapper.findCrmConsumeCouponBatch(crmConsumeCouponDto);
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
                                crmConsumeMapper.updateCrmCardConsume(updateList);
                            }
                            if (addList.size() > Constant.Number.ZERO) {
                                //批量新增
                                crmConsumeMapper.addCrmCardConsume(addList);
                            }
                            //处理消费使用券记录
                            if (updateCouponRecordList.size() > Constant.Number.ZERO) {
                                //批量修改
                                crmConsumeCouponMapper.updateCrmConsumeCoupon(updateCouponRecordList);
                            }
                            if (addCouponRecordList.size() > Constant.Number.ZERO) {
                                //批量新增
                                crmConsumeCouponMapper.addCrmConsumeCoupon(addCouponRecordList);
                            }
                            //当前任务类型
                            paramsMap.put(ScheduleConstant.AppInterface.BUSINESSTYPE,ScheduleConstant.BusinessType.PULL);
                            //变更清洗消费流水任务的时间
                            paramsMap.put(ScheduleConstant.AppInterface.STRATTIME,startTime);
                            taskParamService.updateTaskParamBatch(appId,enteId,paramsMap);
                        }
                    }
                }
             }
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        taskResJson.put(Constant.TaskResult.PARAM,Constant.Character.NULL_VALUE);
        taskResJson.put(Constant.TaskResult.MSG,Constant.Character.NULL_VALUE);
        return taskResJson.toString();
    }

    /**
     * 清洗消费流水数据
     * @param appId
     * @param enteId
     */
    @Override
    @Transactional
    public String cleanConsume(String appId,String enteId,Map<String,Object> paramsMap){
        JSONObject resultJson = new JSONObject();
        try{
            //查询清洗数据任务的执行时间
            TaskParamVo taskParamVo = taskParamService.findTaskParam(enteId,paramsMap);
            //设置条件
            CrmConsumeDto consumeDto = new CrmConsumeDto();
            consumeDto.setEnteId(enteId);
            consumeDto.setAppId(appId);
            consumeDto.setConsumeTime(taskParamVo.getParam());
            //洗数据
            crmConsumeMapper.updateCleanConsume(consumeDto);

            //设置条件
            CrmConsumeCouponDto consumeCouponDto = new CrmConsumeCouponDto();
            consumeCouponDto.setEnteId(enteId);
            consumeCouponDto.setAppId(appId);
            consumeCouponDto.setUseTime(taskParamVo.getParam());
            //洗数据
            crmConsumeCouponMapper.updateCleanConsumeCoupon(consumeCouponDto);
            List<Map<String,Object>> unCleanCodeList = crmConsumeMapper.findUnCleanCode(consumeDto);
            String unCleanCode = getUnCleanCode(unCleanCodeList);
            //存在未清洗干净的数据则返回未清洗的基础数据标识
    //                resultJson.put(Constant.TaskResult.PARAM,unCleanCode);
    //                resultJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
            //当前任务类型
            paramsMap.put(ScheduleConstant.AppInterface.BUSINESSTYPE,ScheduleConstant.BusinessType.CLEAN);
            //全部清洗，则变更wd_task_param该条清洗记录的status为1，并变更迁移任务的时间
            paramsMap.put(ScheduleConstant.AppInterface.UNCLEANCODE,unCleanCode);
            paramsMap.put(ScheduleConstant.AppInterface.STRATTIME,taskParamVo.getParam());
            taskParamService.updateTaskParamBatch(appId,enteId,paramsMap);
            resultJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            resultJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        resultJson.put(Constant.TaskResult.MSG,Constant.Character.NULL_VALUE);
        return resultJson.toString();
    }

    /**
     * 同步微生活会员充值数据
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws UnsupportedEncodingException
     */
    @Override
    @Transactional
    public String addOrUpdatePrepaid(String appId,String enteId,Map<String,Object> paramsMap) {
        JSONObject taskResJson = new JSONObject();
        try{
            CrmPrepaidDto crmPrepaidDto = new CrmPrepaidDto();
            crmPrepaidDto.setEnteId(enteId);
            crmPrepaidDto.setAppId(appId);
            String startTime = DateUtils.getCurrentDate(DateUtils.PATTERN_DAY);
            String endTime = (paramsMap.containsKey(ScheduleConstant.AppInterface.ENDTIME)&&StringUtil.isNotBlank(paramsMap.get(ScheduleConstant.AppInterface.ENDTIME).toString()))?paramsMap.get(ScheduleConstant.AppInterface.ENDTIME).toString():DateUtils.getCurrentDate(DateUtils.PATTERN_MINUTE);
            if(paramsMap.containsKey(ScheduleConstant.AppInterface.STRATTIME)&&StringUtil.isNotBlank(paramsMap.get(ScheduleConstant.AppInterface.STRATTIME).toString())){
                startTime = paramsMap.get(ScheduleConstant.AppInterface.STRATTIME).toString();
            }else{
                String maxTime=crmPrepaidMapper.findMaxPrepaidTime(crmPrepaidDto);
                if(StringUtils.isNotBlank(maxTime)){
                    startTime=maxTime;
                }
            }
            System.out.println("prepiad----------------------------startTime="+startTime+"------------------endTime="+endTime);
            if(appId!=null&&enteId!=null) {
                //设置已有数据的查询条件

                //查询会员充值记录
                String url = paramsMap.get(ScheduleConstant.AppInterface.URL).toString()+prepaid_list_url;
                //请求接口参数
                JSONObject reqJson = new JSONObject();
                reqJson.put(Constant.AceWillCrm.BEGIN_DATE,startTime);
                reqJson.put(Constant.AceWillCrm.END_DATE,endTime);
                reqJson.put(Constant.AceWillCrm.PAGE,paramsMap.get(ScheduleConstant.AppInterface.PAGE));
                //是否全部流水 true是 false否[默认，只查接口来源的流水]
                reqJson.put(Constant.AceWillCrm.IS_ALL,paramsMap.get(ScheduleConstant.AppInterface.IS_ALL));
                String result = findAceWillData(url, reqJson.toString(),paramsMap);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject resultJson = JSONObject.parseObject(result);
                    //errcode=0，表示成功
                    if (resultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(resultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                        if (resultJson.get("res")!=null && !"[]".equals(resultJson.getString("res"))
                                &&(resultJson.getJSONObject("res")).get("data")!=null&&!"[]".equals((resultJson.getJSONObject("res")).getString("data"))){
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
                            Integer max_page = Constant.Number.ONEHUNDRED;
                            for (int i = Constant.Number.TWO; i <= max_page; i++) {
                                reqJson.put(Constant.AceWillCrm.PAGE, String.valueOf(i));
                                String pageResult = findAceWillData(url, reqJson.toString(),paramsMap);
                                if (StringUtils.isNotBlank(pageResult)) {
                                    JSONObject pageResultJson = JSONObject.parseObject(pageResult);
                                    //errcode=0，表示成功
                                    if (pageResultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(pageResultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                                        if (pageResultJson.get("res")!=null && !"[]".equals(pageResultJson.getString("res"))
                                                &&(pageResultJson.getJSONObject("res")).get("data")!=null&&!"[]".equals((pageResultJson.getJSONObject("res")).getString("data"))) {
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
                                            couponRecordDto.setCardId(card_no);
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
                                List<CrmPrepaidVo> existList = crmPrepaidMapper.findCrmCardPrepaidBatch(crmPrepaidDto);
                                //根据appId,enteId,cardPrepaidIds查询充值记录支付明细
                                List<CrmPrepaidPayTypeVo> existPayTypeList = crmPrepaidMapper.findPrepaidPayTypeBatch(crmPrepaidDto);
                                //根据appId,enteId,cardPrepaidIds查询充值赠送券记录
                                //设置已有数据的查询条件
                                CrmPrepaidCouponDto crmPrepaidCouponDto = new CrmPrepaidCouponDto();
                                crmPrepaidCouponDto.setEnteId(enteId);
                                crmPrepaidCouponDto.setAppId(appId);
                                crmPrepaidCouponDto.setPrepaidIds(cardPrepaidIds);
                                List<CrmPrepaidCouponVo> existPrepaidCouponList = crmPrepaidCouponMapper.findCrmPrepaidCouponBatch(crmPrepaidCouponDto);
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
                                        String third_shop_id = crmCardPrepaidVo.getThirdShopId();
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
                                    crmPrepaidMapper.updateCrmCardPrepaid(updateList);
                                }
                                if (addList.size() > Constant.Number.ZERO) {
                                    //批量新增
                                    crmPrepaidMapper.addCrmCardPrepaid(addList);
                                }
                                //充值流水支付明细
                                if (updatePrepaidPayTypeList.size() > Constant.Number.ZERO) {
                                    //批量修改
                                    crmPrepaidMapper.updatePrepaidPayType(updatePrepaidPayTypeList);
                                }
                                if (addPrepaidPayTypeList.size() > Constant.Number.ZERO) {
                                    //批量新增
                                    crmPrepaidMapper.addPrepaidPayType(addPrepaidPayTypeList);
                                }
                                //充值赠送券记录
                                if (updatePrepaidCouponList.size() > Constant.Number.ZERO) {
                                    //批量修改
                                    crmPrepaidCouponMapper.updateCrmPrepaidCoupon(updatePrepaidCouponList);
                                }
                                if (addPrepaidCouponList.size() > Constant.Number.ZERO) {
                                    //批量新增
                                    crmPrepaidCouponMapper.addCrmPrepaidCoupon(addPrepaidCouponList);
                                }
                                //变更清洗储值流水任务的时间
                                //当前任务类型
                                paramsMap.put(ScheduleConstant.AppInterface.BUSINESSTYPE,ScheduleConstant.BusinessType.PULL);
                                paramsMap.put(ScheduleConstant.AppInterface.STRATTIME,startTime);
                                taskParamService.updateTaskParamBatch(appId,enteId,paramsMap);
                            }
                        }
                    }
                }
            }
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        taskResJson.put(Constant.TaskResult.PARAM,Constant.Character.NULL_VALUE);
        taskResJson.put(Constant.TaskResult.PARAM,Constant.Character.NULL_VALUE);
        return taskResJson.toString();
    }

    /**
     * 清洗储值流水数据
     * @param appId
     * @param enteId
     */
    @Override
    @Transactional
    public String cleanPrepaid(String appId,String enteId,Map<String,Object> paramsMap){
        JSONObject resultJson = new JSONObject();
        try{
            //查询清洗数据任务的执行时间
            TaskParamVo taskParamVo = taskParamService.findTaskParam(enteId,paramsMap);
            System.out.println("----------------taskParam="+taskParamVo.getParam()+"-------------taskLastParam="+taskParamVo.getParam());
            //设置条件
            CrmPrepaidDto prepaidDto = new CrmPrepaidDto();
            prepaidDto.setEnteId(enteId);
            prepaidDto.setAppId(appId);
            prepaidDto.setPrepaidTime(taskParamVo.getParam());
            //洗数据
            crmPrepaidMapper.updateCleanPrepaid(prepaidDto);
            //洗数据
            crmPrepaidMapper.updateCleanPrepaidPayType(prepaidDto);
            //洗数据
            crmPrepaidCouponMapper.updateCleanPrepaidCoupon(prepaidDto);
            List<Map<String,Object>> unCleanCodeList = crmPrepaidMapper.findUnCleanCode(prepaidDto);
            List<Map<String,Object>> unCleanCodePayTypeList = crmPrepaidMapper.findUnCleanPayTypeCode(prepaidDto);
            unCleanCodeList.addAll(unCleanCodePayTypeList);
            String unCleanCode = getUnCleanCode(unCleanCodeList);
            //存在未清洗干净的数据则返回未清洗的基础数据标识
//            resultJson.put(Constant.TaskResult.PARAM,unCleanCode);
//            resultJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
            //当前任务类型
            paramsMap.put(ScheduleConstant.AppInterface.BUSINESSTYPE,ScheduleConstant.BusinessType.CLEAN);
            //全部清洗，则变更wd_task_param该条清洗记录的status为1，并变更迁移任务的时间
            paramsMap.put(ScheduleConstant.AppInterface.UNCLEANCODE,unCleanCode);
            //全部清洗，则变更wd_task_param该条清洗记录的status为1，并变更迁移任务的时间
            paramsMap.put(ScheduleConstant.AppInterface.STRATTIME,taskParamVo.getParam());
            taskParamService.updateTaskParamBatch(appId,enteId,paramsMap);
            resultJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            resultJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        return resultJson.toString();
    }

    /**
     * 获取微生活会员等级数据
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    @Override
    @Transactional
    public String addOrUpdateGrade(String appId,String enteId,Map<String,Object> paramsMap) {
        JSONObject taskJson = new JSONObject();
        try{
            if(appId!=null&&enteId!=null) {
                //查询会员等级
                String url = paramsMap.get(ScheduleConstant.AppInterface.URL).toString()+grade_list_url;
                //请求接口参数(无参数传空json字符串)
                JSONObject reqJson = new JSONObject();
                String result = findAceWillData(url, reqJson.toString(),paramsMap);
                System.out.println("---------------"+result);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject resultJson = JSONObject.parseObject(result);
                    //errcode=0，表示成功
                    if (resultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(resultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                        //设置已有数据的查询条件
                        CrmCardGradeDto crmCardGradeDto = new CrmCardGradeDto();
                        crmCardGradeDto.setEnteId(enteId);
                        crmCardGradeDto.setAppId(appId);
                        //根据enteId及充值时间查询充值记录
                        List<CrmCardGradeVo> existList = crmCardGradeMapper.findCrmCardGradeBatch(crmCardGradeDto);
                        //修改等级集合
                        List<CrmCardGradeDto> updateList = new ArrayList<>();
                        //新增等级集合
                        List<CrmCardGradeDto> addList = new ArrayList<>();
                        CrmCardGradeDto entityDto;
                        JSONObject resJson = resultJson.getJSONObject("res");
                        //接口返回的等级
                        List<Map<String, Object>> cardGradeList = (List<Map<String, Object>>) resJson.get("grades");
                        //当前时间
                        Date currentDate = new Date();
                        //循环获取
                        for (Map<String, Object> map : cardGradeList) {
                            String card_grade_id = map.get("id").toString();
                            //等级名称
                            String card_grade_name = map.get("name").toString();
                            //特权标题
                            String title = map.get("title").toString();
                            //特权列表
                            String privileges = map.get("privileges") == null ? Constant.Character.NULL_VALUE  : map.get("privileges").toString();
                            //等级是否开启储值
                            Integer charge_flag = map.get("charge_flag") == null ? Constant.Number.ZERO : Integer.valueOf(map.get("charge_flag").toString());
                            //等级是否开启积分
                            Integer credit_flag = map.get("credit_flag") == null ? Constant.Number.ZERO : Integer.valueOf(map.get("credit_flag").toString());
                            //会员价比例(百分比)
                            Integer member_discount = map.get("member_discount") == null ? Constant.Number.ZERO : Integer.valueOf(map.get("member_discount").toString());
                            //升级规则设置，包括升级方式和升级达标量
                            String regulars = map.get("regulars") == null ? Constant.Character.NULL_VALUE  : (map.get("regulars").toString());
                            //升级规则排序
                            Integer order = map.get("order") == null ? Constant.Number.ZERO  : (Integer.valueOf(map.get("order").toString()));
                            entityDto = new CrmCardGradeDto();
                            entityDto.setCardGradeId(card_grade_id);
                            entityDto.setCardGradeName(card_grade_name);
                            entityDto.setEnteId(enteId);
                            entityDto.setAppId(appId);
                            entityDto.setCreateTime(currentDate);
                            entityDto.setUpdateTime(currentDate);
                            addList.add(entityDto);
                        }

                        //判断会员等级是否已经存在  已存在数据是否变更
                        if (existList != null && existList.size() > Constant.Number.ZERO ) {
                            Map<String,CrmCardGradeVo> existGradeMap = new HashMap<>();
                            for (CrmCardGradeVo crmCardGradeVo : existList) {
                                String card_grade_id = crmCardGradeVo.getCardGradeId();
                                existGradeMap.put(card_grade_id,crmCardGradeVo);
                            }
                            List<CrmCardGradeDto> addCardGradeList = new ArrayList<>();
                            addCardGradeList.addAll(addList);
                            for (CrmCardGradeDto cardGradeDto : addCardGradeList) {
                                String card_grade_id = cardGradeDto.getCardGradeId();
                                //判断会员等级是否存在
                                if (existGradeMap.containsKey(card_grade_id)&&existGradeMap.get(card_grade_id)!=null) {
                                    //去除相同的数据
                                    addList.remove(cardGradeDto);
                                    updateList.add(cardGradeDto);
                                }
                            }
                        }
                        if (updateList.size() > Constant.Number.ZERO) {
                            //批量修改
                            crmCardGradeMapper.updateCrmCardGrade(updateList);
                        }
                        if (addList.size() > Constant.Number.ZERO) {
                            //批量新增
                            crmCardGradeMapper.addCrmCardGrade(addList);
                        }
                    }
                }
            }
            taskJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            taskJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        return taskJson.toString();

    }

    /**
     * 同步支付方式
     * @param appId
     * @param enteId
     * @param paramsMap
     * @throws Exception
     */
    @Override
    @Transactional
    public String addOrUpdatePayType(String appId,String enteId,Map<String,Object> paramsMap){
        JSONObject taskJson = new JSONObject();
        try{
            if(appId!=null&&enteId!=null) {
                //查询支付方式
                String url = paramsMap.get(ScheduleConstant.AppInterface.URL).toString()+pay_type_rul;
                //请求接口参数(无参数传空json字符串)
                JSONObject reqJson = new JSONObject();
                //消费支付方式
                String consumResult = findAceWillData(url, reqJson.toString(),paramsMap);
                reqJson.put(Constant.AceWillCrm.OPTYPE, paramsMap.get(ScheduleConstant.AppInterface.OPTYPE));
                //充值支付方式
                String prepaidResult = findAceWillData(url, reqJson.toString(),paramsMap);

                if (StringUtils.isNotBlank(consumResult) || StringUtils.isNotBlank(prepaidResult)) {
                    JSONObject consumeResultJson = JSONObject.parseObject(consumResult);
                    JSONObject prepaidResultJson = JSONObject.parseObject(prepaidResult);
                    //errcode=0，表示成功
                    if (consumeResultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(consumeResultJson.getString(Constant.AceWillCrm.ERRCODE))
                            || prepaidResultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(prepaidResultJson.getString(Constant.AceWillCrm.ERRCODE))) {

                        //设置已有数据的查询条件
                        BasePayTypeRelaDto payTypeRelaDto = new BasePayTypeRelaDto();
                        payTypeRelaDto.setEnteId(enteId);
                        payTypeRelaDto.setAppId(appId);
                        //根据enteId及充值时间查询充值记录
                        List<BasePayTypeRelaVo> existList = basePayTypeRelaMapper.findBasePayTypeRelaBatch(payTypeRelaDto);
                        //修改支付方式集合
                        List<BasePayTypeRelaDto> updateList = new ArrayList<>();
                        //新增支付方式集合
                        List<BasePayTypeRelaDto> addList = new ArrayList<>();
                        BasePayTypeRelaDto entityDto;
                        //接口返回的支付方式
                        List<Map<String, Object>> payTypeList = new ArrayList<>();
                        //消费支付有值
                        if (consumeResultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(consumeResultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                            List<Map<String, Object>> consumePayTypeList = (List<Map<String, Object>>) consumeResultJson.get("res");
                            payTypeList.addAll(consumePayTypeList);
                        }
                        Map<String, Map<String, Object>> prepaidPayTypeMap = new HashMap<>();
                        //储值支付方式有值
                        if (prepaidResultJson.containsKey(Constant.AceWillCrm.ERRCODE) && Constant.Character.String_ZERO.equals(prepaidResultJson.getString(Constant.AceWillCrm.ERRCODE))) {
                            List<Map<String, Object>> prepaidPayTypeList = (List<Map<String, Object>>) prepaidResultJson.get("res");
                            for (Map<String, Object> map : prepaidPayTypeList) {
                                String third_pay_type_id = appId+map.get("wsh_id").toString();
                                map.put("wsh_id",third_pay_type_id);
                                prepaidPayTypeMap.put(third_pay_type_id, map);
                            }
                        }
                        for (Map<String, Object> map : payTypeList) {
                            String third_pay_type_id = map.get("wsh_id").toString();
                            if (prepaidPayTypeMap.containsKey(third_pay_type_id)) {
                                prepaidPayTypeMap.remove(third_pay_type_id);
                            }
                        }
                        for (String key : prepaidPayTypeMap.keySet()) {
                            payTypeList.add(prepaidPayTypeMap.get(key));
                        }
                        Date currentTime = new Date();  //当前时间
                        //循环获取
                        for (Map<String, Object> map : payTypeList) {
                            //支付方式id
                            String third_pay_type_id = map.get("wsh_id").toString();
                            //支付方式名称
                            String pay_type_name = map.get("name").toString();
                            // 收银的支付id
                            String pay_code = map.get("shou_yin_id").toString();
                            //0 表示微生活默认的支付方式 1 自定义的支付方式
                            Integer is_custom = map.get("is_custom") == null ? Constant.Number.ZERO  : (Integer.valueOf(map.get("is_custom").toString()));
                            entityDto = new BasePayTypeRelaDto();
                            entityDto.setAppId(appId);
                            entityDto.setPayCategoryId(Constant.Character.NULL_VALUE );
                            entityDto.setThirdPayCategoryId(Constant.Character.NULL_VALUE );
                            entityDto.setThirdPayTypeId(third_pay_type_id);
                            entityDto.setPayTypeName(pay_type_name);
                            entityDto.setPayTypeCode(pay_code);
                            entityDto.setEnteId(enteId);
                            entityDto.setCreateTime(currentTime);
                            entityDto.setUpdateTime(currentTime);
                            addList.add(entityDto);
                            //判断支付方式是否已经存在  已存在数据是否变更
                            if (existList != null && existList.size() > Constant.Number.ZERO) {
                                for (BasePayTypeRelaVo basePayTypeRelaVo : existList) {
                                    //判断支付方式是否存在
                                    if (basePayTypeRelaVo.getThirdPayTypeId().equals(third_pay_type_id) ) {
                                        //去除相同的数据
                                        addList.remove(entityDto);
                                        updateList.add(entityDto);
                                    }
                                }
                            }
                        }
                        if (updateList.size() > Constant.Number.ZERO) {
                            //批量修改
                            basePayTypeRelaMapper.updateBasePayTypeRela(updateList);
                        }
                        if (addList.size() > Constant.Number.ZERO) {
                            //批量新增
                            basePayTypeRelaMapper.addBasePayTypeRela(addList);
                        }
                    }
                }
            }
            taskJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            taskJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        return taskJson.toString();
    }

    /**
     * 调用微生活接口的公共方法
     * @param url 请求地址
     * @param reqJsonStr 接口请求参数
     * @return
     * @throws UnsupportedEncodingException
     */
    public String findAceWillData(String url,String reqJsonStr,Map<String,Object> paramsMap){
        //参数：req={}&appid={}&v=2.0&ts=时间戳&sig=xx&fmt=JSON
            String enCodeStr = Constant.Character.NULL_VALUE;
        if(StringUtil.isNotBlank(reqJsonStr)){
            JSONObject reqObj = JSONObject.parseObject(reqJsonStr);
            //若请求参数不为空，则需要将请求参数排序后进行编码
            if(reqObj!=null&&reqObj.size()> Constant.Number.ZERO){
                LinkedHashMap resultMap = SignUtil.sortJsonForMap(reqJsonStr);
                Iterator<Map.Entry<Integer, String>> it = resultMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, String> entry = it.next();
                    enCodeStr += entry.getKey()+Constant.Character.EQUALS+ URLEncoder.encode(entry.getValue())+Constant.Character.AND;
                }
            }
        }
        String appid = paramsMap.get(ScheduleConstant.AppInterface.APPID).toString();
        String appkey = paramsMap.get(ScheduleConstant.AppInterface.APPKEY).toString();
        String version = paramsMap.get(ScheduleConstant.AppInterface.VERSION).toString();
        String fmt = paramsMap.get(ScheduleConstant.AppInterface.FMT).toString();
        String ts = String.valueOf(System.currentTimeMillis() / Constant.Number.ONETHOUSANDL);
        enCodeStr+=Constant.AceWillCrm.APPID+Constant.Character.EQUALS+ appid + Constant.Character.AND
                +Constant.AceWillCrm.APPKEY+Constant.Character.EQUALS + appkey + Constant.Character.AND
                +Constant.AceWillCrm.VERSION +Constant.Character.EQUALS + version + Constant.Character.AND
                +Constant.AceWillCrm.TS +Constant.Character.EQUALS +ts ;
        //sig生成规则；1).参数ASCII码排序 2).urlEncode编码(空值不参与) 3).连接appid,appKey，版本号，时间戳(秒级) 4).md5小写
        String sig = DigestUtils.md5Hex(enCodeStr).toLowerCase();
        //调用微生活接口的传参
        Map params = new HashMap();
        params.put(Constant.AceWillCrm.REQ,reqJsonStr);
        params.put(Constant.AceWillCrm.APPID,appid);
        params.put(Constant.AceWillCrm.VERSION,version);
        params.put(Constant.AceWillCrm.TS ,ts);
        params.put(Constant.AceWillCrm.SIG,sig);
        params.put(Constant.AceWillCrm.FMT,fmt);
        System.out.println("-------------"+params);
        String result = HttpUtils.restPost(url,params);
        return result;
    }

    /**
     * 未清洗标识
     * @param unCleanCodeList
     * @return
     */
    private String getUnCleanCode(List<Map<String,Object>> unCleanCodeList){
        String resultStr = "";
        if(unCleanCodeList!=null && unCleanCodeList.size()>0){
            JSONObject resParam = new JSONObject();
            for (Map<String,Object> map :unCleanCodeList){
                String shopStr = map.get("shop")!=null?map.get("shop").toString():Constant.Character.NULL_VALUE;
                String payTypeStr = map.get("payType")!=null?map.get("payType").toString():Constant.Character.NULL_VALUE;
                String transactionTypeStr = map.get("transactionType")!=null?map.get("transactionType").toString():Constant.Character.NULL_VALUE;
                if(!"".equals(shopStr)){
                    resParam.put(Constant.TaskResult.SHOPCODE,Constant.Number.ONE);
                }
                if(!"".equals(payTypeStr)){
                    resParam.put(Constant.TaskResult.PAYTYPECODE,Constant.Number.ONE);
                }
                if(!"".equals(transactionTypeStr)){
                    resParam.put(Constant.TaskResult.TRANSACTIONTYPECODE,Constant.Number.ONE);
                }
            }
            resultStr = resParam.toString();
        }
        return resultStr;
    }



}


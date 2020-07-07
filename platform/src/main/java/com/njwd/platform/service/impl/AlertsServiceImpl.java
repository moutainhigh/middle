package com.njwd.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.*;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.mapper.AlertsConfigMapper;
import com.njwd.platform.mapper.AlertsRecordMapper;
import com.njwd.platform.service.AlertsService;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AlertsServiceImpl implements AlertsService {

    @Value("${njwdmss.server}")
    private String server;
    @Value("${njwdmss.crm_server}")
    private String crmServer;
    @Value("${njwdmss.url.find_expire_order_goods}")
    private String findExpireOrderGoods;
    @Value("${njwdmss.url.find_prepaid_balance}")
    private String findPrepaidBalance;

    @Resource
    AlertsConfigMapper alertsConfigMapper;
    @Resource
    AlertsRecordMapper alertsRecordMapper;

    /**
     * @Description: 修改预警配置
     * @Param: AlertsConfigDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:29
     */
    @Override
    @Transactional
    public void doUpdateAlertsConfig(AlertsConfigDto alertsConfigDto) {
        //先查询是否有对应的预警配置
        List<AlertsConfigVo> alertsConfigVos = alertsConfigMapper.selectAlertsConfigByUser(alertsConfigDto);
        if(alertsConfigVos!=null&&alertsConfigVos.size()>0){
            //如果有对应配置则修改对应配置
            alertsConfigDto.setAlertsConfigId(alertsConfigVos.get(0).getAlertsConfigId());
            alertsConfigDto.setUpdateTime(new Date());
            int updateAlertsConfig = alertsConfigMapper.updateAlertsConfig(alertsConfigDto);
        }else {
            //如果没有对应配置则新增配置
            alertsConfigDto.setCreateTime(new Date());
            alertsConfigDto.setUpdateTime(new Date());
            int insertAlertsConfig = alertsConfigMapper.insertAlertsConfig(alertsConfigDto);
        }
    }

    /**
     * @Description: 查询预警配置
     * @Param: AlertsConfigDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 19:31
     */
    @Override
    public List<AlertsConfigVo> findAlertsConfig(AlertsConfigDto alertsConfigDto) {
        List<AlertsConfigVo> alertsConfigVos = alertsConfigMapper.selectAlertsConfig(alertsConfigDto);
        return alertsConfigVos;
    }

    /**
     * @Description: 查询预警记录
     * @Param: AlertsRecordDto
     * @return: AlertsRecordVo
     * @Author: huxianghong
     * @Date: 2020/4/2 20:34
     */
    @Override
    public List<AlertsRecordVo> findAlertsRecord(AlertsRecordDto alertsRecordDto) {
        List<AlertsRecordVo> alertsRecordVos = alertsRecordMapper.selectAlertsRecord(alertsRecordDto);
        return alertsRecordVos;
    }

    /**
     * @Description: 查询几天后到期的预付费账单
     * @Param: expireOrderGoodsDto
     * @return: ExpireOrderGoodsReturnVo
     * @Author: huxianghong
     * @Date: 2020/4/8 16:34
     */
    @Override
    public ExpireOrderGoodsReturnVo findExpireOrderGoods(ExpireOrderGoodsDto expireOrderGoodsDto) {
        Map<String, String> map =  JSONObject.parseObject(JSONObject.toJSONString(expireOrderGoodsDto), Map.class);
        String params = "";
        for(Map.Entry entry:map.entrySet()){
            params = params+"&"+entry.getKey()+"="+entry.getValue();
        }
        String url = server+findExpireOrderGoods+params;
        String returnString  = HttpUtils.restPostJson(url,"");
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.PARAMS_NOT);
        }
        ExpireOrderGoodsReturnVo expireOrderGoodsReturnVo = JSON.parseObject(returnString,ExpireOrderGoodsReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(expireOrderGoodsReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.PARAMS_NOT);
        }
        return expireOrderGoodsReturnVo;
    }

    /**
     * @Description: 查询和生成预警的业务
     * @Param: void
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/4/13 16:34
     */
    @Override
    @Transactional
    public void findAndAddAlerts() {
        //1、查询所有用户的余额预警配置
        AlertsConfigDto alertsConfigDto = new AlertsConfigDto();
        alertsConfigDto.setAlertsType(1);
        List<AlertsConfigVo> alertsConfigVos = alertsConfigMapper.selectAlertsConfigAndUser(alertsConfigDto);
        List<AlertsRecordDto> alertsRecordDtos = new ArrayList<AlertsRecordDto>();
        if(alertsConfigVos!=null&&alertsConfigVos.size()>0){
            //2、遍历这些用户，查出他们的余额
            FindPrepaidRecordDto findPrepaidRecordDto = new FindPrepaidRecordDto();
            for(AlertsConfigVo alertsConfigVo:alertsConfigVos){
                findPrepaidRecordDto.setCardId(alertsConfigVo.getCrmUserId());
                //调用CRM查询其余额
                String jsonString = JSON.toJSONString(findPrepaidRecordDto);
                //查询会员余额
                String returnString = HttpUtils.doRequest(crmServer,findPrepaidBalance,PlatformContant.ReturnString.CRM_PAY_PAT+jsonString);
                if(StringUtil.isEmpty(returnString)){
                    System.out.println("调用CRM查询余额失败");
                }else {
                    PrepaidBalanceReturnVo prepaidBalanceReturnVo = JSON.parseObject(returnString,PrepaidBalanceReturnVo.class);
                    if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(prepaidBalanceReturnVo.getStatus()))){
                        System.out.println("调用CRM查询余额失败");
                    }else {
                        //查出的余额和预警阀值比较大小
                        if(prepaidBalanceReturnVo.getData().getMoney()!=null){
                            if(prepaidBalanceReturnVo.getData().getMoney().compareTo(alertsConfigVo.getWarningThreshold())==-1){
                                //余额小于预警阀值，查询有无先前该类型的预警记录
                                AlertsRecordDto alertsRecordDto = new AlertsRecordDto();
                                alertsRecordDto.setUserId(alertsConfigVo.getUserId());
                                alertsRecordDto.setAlertsType(1);
                                List<AlertsRecordVo> alertsRecordVos = alertsRecordMapper.selectAlertsRecordUsed(alertsRecordDto);
                                if(alertsConfigVos==null||alertsConfigVos.size()<=4){
                                    //当查出的预警记录小于或等于4的时候添加预警记录
                                    alertsRecordDto.setContent("你的余额已少于"+alertsConfigVo.getWarningThreshold()+"元，请及时充值");
                                    alertsRecordDto.setCreateTime(new Date());
                                    alertsRecordDtos.add(alertsRecordDto);
                                }
                            }
                        }
                    }
                }
            }
        }
        //3、查询所有用户的续费预警设置
        alertsConfigDto.setAlertsType(2);
        List<AlertsConfigVo> alertsConfigVoList = alertsConfigMapper.selectAlertsConfigAndUser(alertsConfigDto);
        if(alertsConfigVoList!=null&&alertsConfigVoList.size()>0){
            for(AlertsConfigVo alertsConfigVo:alertsConfigVoList){
                //调用接口查询5天内到期的订单
                ExpireOrderGoodsDto expireOrderGoodsDto = new ExpireOrderGoodsDto();
                Double alertDaysDouble = Double.parseDouble(alertsConfigVo.getWarningThreshold().toString());
                Integer alertDays = alertDaysDouble.intValue();
                expireOrderGoodsDto.setDay_num(alertDays);
                expireOrderGoodsDto.setCustomer_id(alertsConfigVo.getCrmUserId());
                Map<String, String> map =  JSONObject.parseObject(JSONObject.toJSONString(expireOrderGoodsDto), Map.class);
                String params = "";
                for(Map.Entry entry:map.entrySet()){
                    params = params+"&"+entry.getKey()+"="+entry.getValue();
                }
                String url = server+findExpireOrderGoods+params;
                String returnString  = HttpUtils.restPostJson(url,"");
                if(StringUtil.isEmpty(returnString)){
                    System.out.println("调用外部接口查询订单失败");
                }else {
                    ExpireOrderGoodsReturnVo expireOrderGoodsReturnVo = JSON.parseObject(returnString,ExpireOrderGoodsReturnVo.class);
                    if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(expireOrderGoodsReturnVo.getStatus()))){
                        System.out.println("调用外部接口查询订单失败");
                    }else {
                        for (ExpireOrderGoodsVo expireOrderGoodsVo:expireOrderGoodsReturnVo.getList_data()){
                            if((alertDays==(expireOrderGoodsVo.getRemaining_days()))||(1==(expireOrderGoodsVo.getRemaining_days()))){
                                AlertsRecordDto alertsRecordDto = new AlertsRecordDto();
                                alertsRecordDto.setUserId(alertsConfigVo.getUserId());
                                alertsRecordDto.setAlertsType(2);
                                alertsRecordDto.setGoodsId(expireOrderGoodsVo.getGoods_id());
                                alertsRecordDto.setGoodsName(expireOrderGoodsVo.getGoods_name());
                                alertsRecordDto.setContent("您开通的产品"+expireOrderGoodsVo.getGoods_name()+
                                        "将在"+expireOrderGoodsVo.getRemaining_days()+"日后到期，请及时续费或办理延长。");
                                alertsRecordDto.setCreateTime(new Date());
                                alertsRecordDtos.add(alertsRecordDto);
                            }
                        }
                    }
                }

            }
        }
        //批量插入预警记录表数据
        if(alertsRecordDtos!=null&&alertsRecordDtos.size()>0){
            int insertList = alertsRecordMapper.insertList(alertsRecordDtos);
        }
    }
}

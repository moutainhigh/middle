package com.njwd.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.*;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.mapper.RechargeMapper;
import com.njwd.platform.service.GoodsService;
import com.njwd.platform.service.PayOnlineService;
import com.njwd.platform.service.UserService;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 在线支付，充值相关业务
 */

@Service
public class PayOnlineServiceImpl implements PayOnlineService {

    @Value("${njwdmss.crm_server}")
    private String crmServer;
    @Value("${njwdmss.pay_online_server}")
    private String payOnlineServer;
    @Value("${njwdmss.url.pay_entrance}")
    private String payEntrance;
    @Value("${njwdmss.url.member_prepaid}")
    private String memberPrepaid;
    @Value("${njwdmss.url.find_prepaid_record}")
    private String findPrepaidRecord;
    @Value("${njwdmss.url.find_prepaid_balance}")
    private String findPrepaidBalance;


    @Resource
    UserService userService;
    @Resource
    RechargeMapper rechargeMapper;
    /**
     * @Description: 发起充值的支付请求
     * @Param: RechargeDto
     * @return: RechargeVo
     * @Author: huxianghong
     * @Date: 2020/4/2 14:33
     */
    @Override
    public RechargeVo addRecharge(RechargeDto rechargeDto) {
        //生成支付唯一标识
        String code = userService.getCodeKey("ZT");
        rechargeDto.setCode(code);
        rechargeDto.setCreateTime(new Date());
        rechargeDto.setUpdateTime(new Date());
        //插入一条新充值数据
        Integer insertRecharge = rechargeMapper.insertRecharge(rechargeDto);
        RechargeVo rechargeVo = new RechargeVo();
        if(insertRecharge>0){
            rechargeVo.setCode(code);
            rechargeVo.setPayCode(code);
            return rechargeVo;
        }else {
            throw new ServiceException(ResultCode.ADD_RECHARGE);
        }

    }

    /**
     * @Description: 获得支付入口接口
     * @Param: PayOnlinePublicDto
     * @return: PayEntranceVo
     * @Author: huxianghong
     * @Date: 2020/4/2 14:33
     */
    @Override
    public PayEntranceVo findPayEntrance(PayOnlinePublicDto payOnlinePublicDto) {
        //插入固定参数
        payOnlinePublicDto.setIdentification(PlatformContant.PayOnline.IDENTIFICATION);
        payOnlinePublicDto.setMeansType(PlatformContant.PayOnline.MEANS_TYPE);
        payOnlinePublicDto.setType(PlatformContant.PayOnline.TYPE);
        //拼接URL
//        PayOnlineUrlDto payOnlineUrlDto = new PayOnlineUrlDto();
//        BeanUtils.copyProperties(payOnlinePublicDto,payOnlineUrlDto);
//        Map<String, String> map =  JSONObject.parseObject(JSONObject.toJSONString(payOnlineUrlDto), Map.class);
//        String params = "";
//        for(Map.Entry entry:map.entrySet()){
//            params = params+"&"+entry.getKey()+"="+entry.getValue();
//        }
//        String url = payEntrance+params;
        //插入固定参数
        payOnlinePublicDto.getContent().setType(PlatformContant.PayOnline.TYPE);
        payOnlinePublicDto.getContent().setEnterpriseIdPayAccount(PlatformContant.FixedParameter.ROOT_ORG_ID);
        payOnlinePublicDto.getContent().setEnterprise_name(PlatformContant.ReturnString.ENTERPRISE_NAME);
        payOnlinePublicDto.getContent().setNotifyUrl(PlatformContant.PayOnline.NOTIFY_URL);
        payOnlinePublicDto.getContent().setMoney(payOnlinePublicDto.getMoney());
        String jsonString = JSON.toJSONString(payOnlinePublicDto);
        String returnString = HttpUtils.doRequest(payOnlineServer,payEntrance, PlatformContant.ReturnString.CRM_PAY_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_PAY_ENTRANCE_FAIL);
        }
        PayEntranceVo payEntranceVo = JSON.parseObject(returnString,PayEntranceVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(payEntranceVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_PAY_ENTRANCE_FAIL);
        }
        return payEntranceVo;
    }

    /**
     * @Description: 轮训查询支付状态
     * @Param: PayOnlinePublicDto
     * @return: FindPrepaidDto
     * @Author: huxianghong
     * @Date: 2020/4/2 14:39
     */
    @Override
    public FindPrepaidPayOnlineVo findPrepaidPayOnline(FindPrepaidDto findPrepaidDto) {
        String jsonString = JSON.toJSONString(findPrepaidDto);
        //调用外部接口轮训查询支付状态
        String returnString = HttpUtils.doRequest(crmServer,memberPrepaid,PlatformContant.ReturnString.CRM_PAY_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_PREPAID_PAY);
        }
        FindPrepaidPayOnlineVo findPrepaidPayOnlineVo = JSON.parseObject(returnString,FindPrepaidPayOnlineVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(findPrepaidPayOnlineVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_PREPAID_PAY);
        }
        if((PlatformContant.ReturnString.RETURN_SUCCESS).equals(findPrepaidPayOnlineVo.getStatus())){
            if(findPrepaidPayOnlineVo.getIs_pay()){
                //支付成功修改本地充值记录表状态
                RechargeDto rechargeDto = new RechargeDto();
                rechargeDto.setCode(findPrepaidDto.getPayCode());
                rechargeDto.setStatus(true);
                Integer updateRecharge = rechargeMapper.updateRecharge(rechargeDto);
            }
        }
        return findPrepaidPayOnlineVo;
    }

    /**
     * @Description: 查询会员卡记录列表
     * @Param: PrepaidRecordVo
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 14:59
     */
    @Override
    public PrepaidRecordReturnVo findPrepaidRecord(FindPrepaidRecordDto findPrepaidDto) {
        String jsonString = JSON.toJSONString(findPrepaidDto);
        //查询会员卡记录列表
        String returnString = HttpUtils.doRequest(crmServer,findPrepaidRecord,PlatformContant.ReturnString.CRM_PAY_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_PREPAID_RECORD_FAIL);
        }
        PrepaidRecordReturnVo prepaidRecordReturnVo = JSON.parseObject(returnString,PrepaidRecordReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(prepaidRecordReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_PREPAID_RECORD_FAIL);
        }
        for (PrepaidRecordVo prepaidRecordVo:prepaidRecordReturnVo.getData()){
           if(!(StringUtil.isEmpty(prepaidRecordVo.getRemark()))){
               List<String> list = Arrays.asList(prepaidRecordVo.getRemark().split(","));
               if(list!=null&&list.size()>0){
                   if(!(StringUtil.isEmpty(list.get(0)))){
                       prepaidRecordVo.setPayCode(list.get(0));
                   }
                   if(!(StringUtil.isEmpty(list.get(2)))){
                       prepaidRecordVo.setThirdCode(list.get(2));
                   }
               }
           }
        }
        return prepaidRecordReturnVo;
    }

    /**
     * @Description: 查询会员余额
     * @Param: PrepaidRecordVo
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 14:59
     */
    @Override
    public PrepaidBalanceReturnVo findPrepaidBalance(FindPrepaidRecordDto findPrepaidDto) {
        String jsonString = JSON.toJSONString(findPrepaidDto);
        //查询会员余额
        String returnString = HttpUtils.doRequest(crmServer,findPrepaidBalance,PlatformContant.ReturnString.CRM_PAY_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_PREPAID_BALANCE);
        }
        PrepaidBalanceReturnVo prepaidBalanceReturnVo = JSON.parseObject(returnString,PrepaidBalanceReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(prepaidBalanceReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_PREPAID_BALANCE);
        }
        return prepaidBalanceReturnVo;
    }
}

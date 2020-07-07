package com.njwd.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.ApplyCashDto;
import com.njwd.entity.platform.dto.ApplyCashMssDto;
import com.njwd.entity.platform.dto.CrmCarhDto;
import com.njwd.entity.platform.dto.PageApplyCashDto;
import com.njwd.entity.platform.vo.ApplyCashVo;
import com.njwd.entity.platform.vo.SimpleReturnVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.mapper.ApplyCashMapper;
import com.njwd.platform.service.ApplyCashService;
import com.njwd.platform.service.UserService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description 提现有关业务
 * @Date 2020/4/7 10:43
 * @Author 胡翔鸿
 */
@Service
public class ApplyCashServiceImpl implements ApplyCashService {

    private final static Logger logger = LoggerFactory.getLogger(ApplyCashServiceImpl.class);

    @Resource
    UserService userService;
    @Resource
    ApplyCashMapper applyCashMapper;

    @Value("${njwdmss.server}")
    private String server;
    @Value("${njwdmss.crm_server}")
    private String crmServer;
    @Value("${njwdmss.url.do_crm_cash}")
    private String doCrmCash;
    @Value("${njwdmss.url.mss_apply_cash}")
    private String mssApplyCash;

    /**
     * @Description: 新增提现的业务
     * @Param:  ApplyCashDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/4/7 14:07
     */
    @Override
    @Transactional
    public void doAddAppCash(ApplyCashDto applyCashDto) {
        //加入相关默认参数
        //字段对应说明：bank_account表account_name对应apply_cash表bank_account(开户名/账号名称)
        //bank_account表account_number对应apply_cash表bank_number(银行卡账号)
        applyCashDto.setCrmStatus(0);
        applyCashDto.setMssStatus(-1);
        applyCashDto.setCreateTime(new Date());
        applyCashDto.setUpdateTime(new Date());
        String cashCode = userService.getCodeKey("");
        applyCashDto.setCashCode(cashCode);
        //本系统新增一条提现记录
        applyCashMapper.insertApplyCash(applyCashDto);
        //调用CRM提现接口
        CrmCarhDto crmCarhDto = new CrmCarhDto();
        crmCarhDto.setCardId(applyCashDto.getCrmUserId());
        crmCarhDto.setEnterpriseId(PlatformContant.ReturnString.ENTERPRISE_ID);
        crmCarhDto.setEnterpriseName(PlatformContant.ReturnString.ENTERPRISE_NAME);
        crmCarhDto.setRefundMoney(applyCashDto.getCashSum());
        String jsonString = JSON.toJSONString(crmCarhDto);
        String addCashString = HttpUtils.doRequest(crmServer,doCrmCash,PlatformContant.ReturnString.CRM_URL_PAT+jsonString);
        if(StringUtil.isEmpty(addCashString)){
//        if(false){
            logger.info("提现单号为{}的提现在会员系统中提现失败。",applyCashDto.getCashCode());
        }else{
            SimpleReturnVo simpleReturnVo = JSON.parseObject(addCashString,SimpleReturnVo.class);
            if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
//            if(false){
                logger.info("提现单号为{}的提现在会员系统中提现失败。",applyCashDto.getCashCode());
            }else {
                //修改表中相关字段，标识调用了CRM调用成功
                applyCashDto.setCrmStatus(1);
                applyCashMapper.updateCrmStatus(applyCashDto);
                //会员系统成功返回，调MSS系统
                ApplyCashMssDto applyCashMssDto = new ApplyCashMssDto();
                applyCashMssDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
                applyCashMssDto.setApply_type(PlatformContant.ReturnString.APPLY_TYPE_BALANCE);//提现类型固定为余额提现
                applyCashMssDto.setUser_id(applyCashDto.getUserId());
                applyCashMssDto.setUser_name(applyCashDto.getCompanyName());//客户注册公司名作为调用接口的用户名
                applyCashMssDto.setMobile(applyCashDto.getMobile());
                applyCashMssDto.setCash_code(applyCashDto.getCashCode());
                applyCashMssDto.setApply_code(applyCashDto.getCashCode());
                applyCashMssDto.setApply_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                applyCashMssDto.setCash_sum(applyCashDto.getCashSum());
                applyCashMssDto.setBank_account(applyCashDto.getBankAccount());
                applyCashMssDto.setBank_number(applyCashDto.getAccountNumber());
                applyCashMssDto.setBank_name(applyCashDto.getBankName());
                applyCashMssDto.setRemark("提现"+applyCashDto.getCashSum()+"元");
                String applyCashMssJson = JSON.toJSONString(applyCashMssDto);
                //调用外部接口实现商品试用
                String returnString = HttpUtils.doRequest(server,mssApplyCash,PlatformContant.ReturnString.URL_PAT+applyCashMssJson);
//                String returnString = "";
                if(StringUtil.isEmpty(returnString)){
                    logger.info("提现单号为{}的提现在中台核心系统调用提现失败",applyCashMssDto.getCash_code());
                    //修改MSS提现状态为0
                    applyCashDto.setMssStatus(0);
                    applyCashMapper.updateMssStatus(applyCashDto);
                }else{
                    SimpleReturnVo simpleMssReturn = JSON.parseObject(returnString,SimpleReturnVo.class);
                    if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleMssReturn.getStatus()))){
                        logger.info("提现单号为{}的提现在中台核心系统调用提现失败",applyCashMssDto.getCash_code());
                        applyCashDto.setMssStatus(0);
                        applyCashMapper.updateMssStatus(applyCashDto);
                    }else {
                        //修改MSS提现状态为1
                        applyCashDto.setMssStatus(1);
                        applyCashMapper.updateMssStatus(applyCashDto);
                    }
                }
            }
        }



    }

    /**
     * @Description: 分页查询提现
     * @Param:  ApplyCashDto
     * @return: Page<ApplyCashVo>
     * @Author: huxianghong
     * @Date: 2020/4/7 14:15
     */
    @Override
    public Page<ApplyCashVo> findAppCashList(PageApplyCashDto param) {

        Page<ApplyCashVo> applyCashVoPage = applyCashMapper.selectApplyCashByPage(param.getPage(),param);

        return applyCashVoPage;
    }
}

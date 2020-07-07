package com.njwd.platform.controller;

import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.*;
import com.njwd.platform.service.PayOnlineService;
import com.njwd.platform.service.UserService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(value = "在线支付和提现相关接口",tags = "在线支付和提现相关接口")
@RestController
@RequestMapping("payOnline")
public class PayOnlineController extends BaseController {

    @Resource
    PayOnlineService payOnlineService;
    @Resource
    UserService userService;

    /**
     * @Description: 发起充值的支付请求
     * @Param: RechargeDto
     * @return: RechargeVo
     * @Author: huxianghong
     * @Date: 2020/4/2 14:33
     */
    @ApiOperation(value = "发起充值的支付请求",notes = "发起充值的支付请求")
    @PostMapping("/addRecharge")
    public Result<RechargeVo> addRecharge(HttpServletRequest request, @RequestBody RechargeDto rechargeDto){

        FastUtils.checkParams(rechargeDto,rechargeDto.getMoney(),rechargeDto.getPayType());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        rechargeDto.setUserId(userVO.getUserId());
        RechargeVo returnVo = payOnlineService.addRecharge(rechargeDto);
        return ok(returnVo);
    }

    /**
     * @Description: 获得支付入口接口
     * @Param: PayOnlinePublicDto
     * @return: PayEntranceVo
     * @Author: huxianghong
     * @Date: 2020/4/2 14:33
     */
    @ApiOperation(value = "获得支付入口接口",notes = "获得支付入口接口")
    @PostMapping("/findPayEntrance")
    public Result<PayEntranceVo> findPayEntrance(HttpServletRequest request, @RequestBody PayOnlinePublicDto payOnlinePublicDto){

        FastUtils.checkParams(payOnlinePublicDto,payOnlinePublicDto.getMoney(),payOnlinePublicDto.getContent().getPayCode());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        payOnlinePublicDto.getContent().setCompany_name(userVO.getCompanyName());
        payOnlinePublicDto.getContent().setCardId(userVO.getCrmUserId());
        PayEntranceVo returnVo = payOnlineService.findPayEntrance(payOnlinePublicDto);
        return ok(returnVo);
    }

    /**
     * @Description: 轮训查询支付状态
     * @Param: PayOnlinePublicDto
     * @return: FindPrepaidDto
     * @Author: huxianghong
     * @Date: 2020/4/2 14:39
     */
    @ApiOperation(value = "轮训查询支付状态",notes = "轮训查询支付状态")
    @PostMapping("/findPrepaidPayOnline")
    public Result<FindPrepaidPayOnlineVo> findPrepaidPayOnline(HttpServletRequest request, @RequestBody FindPrepaidDto findPrepaidDto){

        FastUtils.checkParams(findPrepaidDto,findPrepaidDto.getPayCode());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        FindPrepaidPayOnlineVo returnVo = payOnlineService.findPrepaidPayOnline(findPrepaidDto);
        return ok(returnVo);
    }


    /**
     * @Description: 查询会员卡记录列表
     * @Param: PrepaidRecordVo
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 14:59
     */
    @ApiOperation(value = "查询会员卡记录列表",notes = "查询会员卡记录列表")
    @PostMapping("/findPrepaidRecord")
    public Result<PrepaidRecordReturnVo> findPrepaidRecord(HttpServletRequest request){
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        FindPrepaidRecordDto findPrepaidDto = new FindPrepaidRecordDto();
        findPrepaidDto.setCardId(userVO.getCrmUserId());
        PrepaidRecordReturnVo returnVo = payOnlineService.findPrepaidRecord(findPrepaidDto);
        return ok(returnVo);
    }

    /**
     * @Description: 查询会员余额
     * @Param: PrepaidRecordVo
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 14:59
     */
    @ApiOperation(value = "查询会员余额",notes = "查询会员余额")
    @PostMapping("/findPrepaidBalance")
    public Result<PrepaidBalanceReturnVo> findPrepaidBalance(HttpServletRequest request){
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        FindPrepaidRecordDto findPrepaidDto = new FindPrepaidRecordDto();
        findPrepaidDto.setCardId(userVO.getCrmUserId());
        PrepaidBalanceReturnVo returnVo = payOnlineService.findPrepaidBalance(findPrepaidDto);
        return ok(returnVo);
    }


}

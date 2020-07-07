package com.njwd.platform.controller;

/**
 * @Description 预警相关接口
 * @Date 2020/4/2 17:43
 * @Author 胡翔鸿
 */

import com.njwd.entity.platform.dto.AddressDto;
import com.njwd.entity.platform.dto.AlertsConfigDto;
import com.njwd.entity.platform.dto.AlertsRecordDto;
import com.njwd.entity.platform.dto.ExpireOrderGoodsDto;
import com.njwd.entity.platform.vo.*;
import com.njwd.platform.service.AlertsService;
import com.njwd.platform.service.UserService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "AlertsController",tags = "预警相关接口")
@RestController
@RequestMapping("alerts")
public class AlertsController extends BaseController {

    @Resource
    AlertsService alertsService;
    @Resource
    UserService userService;

    /**
     * @Description: 修改预警配置
     * @Param: AlertsConfigDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 19:11
     */
    @ApiOperation(value = "修改预警配置",notes = "修改预警配置")
    @PostMapping("/doUpdateAlertsConfig")
    public Result doUpdateAlertsConfig(HttpServletRequest request, @RequestBody AlertsConfigDto alertsConfigDto){
        //校验入参
        FastUtils.checkParams(alertsConfigDto,alertsConfigDto.getAlertsType(),
                alertsConfigDto.getStatus(),alertsConfigDto.getWarningThreshold());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        alertsConfigDto.setUserId(userVO.getUserId());
        alertsService.doUpdateAlertsConfig(alertsConfigDto);
        return ok();
    }

    /**
     * @Description: 查询预警配置
     * @Param: AlertsConfigDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 19:31
     */
    @ApiOperation(value = "查询预警配置",notes = "查询预警配置")
    @PostMapping("/findAlertsConfig")
    public Result<List<AlertsConfigVo>> findAlertsConfig(HttpServletRequest request){
        AlertsConfigDto alertsConfigDto = new AlertsConfigDto();
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        alertsConfigDto.setUserId(userVO.getUserId());
        List<AlertsConfigVo> alertsConfigVos = alertsService.findAlertsConfig(alertsConfigDto);
        return ok(alertsConfigVos);
    }

    /**
     * @Description: 查询预警记录
     * @Param: AlertsRecordDto
     * @return: AlertsRecordVo
     * @Author: huxianghong
     * @Date: 2020/4/2 20:34
     */
    @ApiOperation(value = "查询预警记录",notes = "查询预警记录")
    @PostMapping("/findAlertsRecord")
    public Result<List<AlertsRecordVo>> findAlertsRecord(HttpServletRequest request){
        AlertsRecordDto alertsRecordDto = new AlertsRecordDto();
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        alertsRecordDto.setUserId(userVO.getUserId());
        List<AlertsRecordVo> alertsRecordVos = alertsService.findAlertsRecord(alertsRecordDto);
        return ok(alertsRecordVos);
    }

    /**
     * @Description: 查询几天后到期的预付费账单
     * @Param: expireOrderGoodsDto
     * @return: ExpireOrderGoodsReturnVo
     * @Author: huxianghong
     * @Date: 2020/4/8 16:34
     */
    @ApiOperation(value = "查询几天后到期的预付费账单",notes = "查询几天后到期的预付费账单")
    @PostMapping("/findExpireOrderGoods")
    public Result<ExpireOrderGoodsReturnVo> findExpireOrderGoods(HttpServletRequest request, @RequestBody ExpireOrderGoodsDto expireOrderGoodsDto){
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        expireOrderGoodsDto.setCustomer_id(userVO.getCrmUserId());
        ExpireOrderGoodsReturnVo expireOrderGoodsReturnVo = alertsService.findExpireOrderGoods(expireOrderGoodsDto);
        return ok(expireOrderGoodsReturnVo);
    }

    /**
     * @Description: 定时任务
     * @Param: expireOrderGoodsDto
     * @return: ExpireOrderGoodsReturnVo
     * @Author: huxianghong
     * @Date: 2020/4/8 16:34
     */
    @Scheduled(cron = "0 32 17 * * ?")
    public void findTime(){
        //进入查询和生成预警的业务
       alertsService.findAndAddAlerts();
    }


}

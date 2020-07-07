package com.njwd.platform.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.platform.dto.AlertsConfigDto;
import com.njwd.entity.platform.dto.ApplyCashDto;
import com.njwd.entity.platform.dto.PageApplyCashDto;
import com.njwd.entity.platform.vo.ApplyCashVo;
import com.njwd.entity.platform.vo.UserVO;
import com.njwd.platform.service.ApplyCashService;
import com.njwd.platform.service.UserService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description 提现有关接口
 * @Date 2020/4/7 10:43
 * @Author 胡翔鸿
 */
@Api(value = "ApplyCashController",tags = "提现有关接口")
@RestController
@RequestMapping("applyCash")
public class ApplyCashController extends BaseController {

    @Resource
    ApplyCashService applyCashService;
    @Resource
    UserService userService;

    /**
     * @Description: 新增提现
     * @Param: AlertsConfigDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 19:11
     */
    @ApiOperation(value = "新增提现",notes = "新增提现")
    @PostMapping("/doAdd")
    public Result doAddAppCash(HttpServletRequest request, @RequestBody ApplyCashDto applyCashDto){
        //校验入参
        FastUtils.checkParams(applyCashDto,applyCashDto.getCashSum(),
                applyCashDto.getBankName(),applyCashDto.getBankNumber(),applyCashDto.getBankAccount());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        applyCashDto.setUserId(userVO.getUserId());
        applyCashDto.setCrmUserId(Long.parseLong(userVO.getCrmUserId()));
        applyCashDto.setUserName(userVO.getMobile());
        applyCashDto.setMobile(userVO.getMobile());
        applyCashDto.setCompanyName(userVO.getCompanyName());
        applyCashService.doAddAppCash(applyCashDto);
        return ok();
    }

    /**
     * @Description: 分页查询提现列表
     * @Param: AlertsConfigDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 19:11
     */
    @ApiOperation(value = "分页查询提现列表",notes = "分页查询提现列表")
    @PostMapping("/findPage")
    public Result<Page<ApplyCashVo>> findAppCashList(HttpServletRequest request, @RequestBody PageApplyCashDto pageApplyCashDto){
        //校验入参
        FastUtils.checkParams(pageApplyCashDto);
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        pageApplyCashDto.setUserId(userVO.getUserId());
        //格式化传入的时间信息
        if(pageApplyCashDto.getCreateStartTime()!=null){
            pageApplyCashDto.setCreateStartTime(DateUtils.beginOfDate(pageApplyCashDto.getCreateStartTime()));
        }
        if(pageApplyCashDto.getCreateEndTime()!=null){
            pageApplyCashDto.setCreateEndTime(DateUtils.endOfDate(pageApplyCashDto.getCreateEndTime()));
        }
        Page<ApplyCashVo> applyCashVoPage = applyCashService.findAppCashList(pageApplyCashDto);
        return ok(applyCashVoPage);
    }
}

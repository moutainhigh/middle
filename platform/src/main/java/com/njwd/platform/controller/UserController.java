package com.njwd.platform.controller;

import com.alibaba.fastjson.JSON;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.UserVO;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.service.UserService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import com.njwd.utils.RedisUtils;
import com.njwd.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description 用户-注册登录维护等
 * @Date 2020/3/23 13:59
 * @Author 胡翔鸿
 */

@Api(value = "UserController",tags = "用户-注册登录维护等")
@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;
    @Resource
    private RedisUtils redisUtils;
    /**
     * @Description 用户模块（登录注册和用户数据维护）
     * @Author 胡翔鸿
     * @Data 2020/3/23 14:03
     * @Param AddUserDto
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "注册接口",notes = "注册有关的接口")
    @PostMapping("doAddUser")
    public Result doAddUser (@RequestBody AddUserDto addUserDto){
        FastUtils.checkParams(addUserDto,addUserDto.getMobile(),addUserDto.getVerificationCode(),addUserDto.getPassword(),addUserDto.getCompanyName(),addUserDto.getUserRoleDtoList());
        userService.addUser(addUserDto);
        return ok();
    }

    /**
     * @Description 用户模块（登录注册和用户数据维护）
     * @Author 胡翔鸿
     * @Data 2020/3/23 14:03
     * @Param AddUserDto
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "登录接口",notes = "登录有关的接口")
    @PostMapping("login")
    public Result<UserVO> login (@RequestBody LoginDto loginDto ){
        FastUtils.checkParams(loginDto,loginDto.getLoginType());
        UserVO userVO = new UserVO();
        if(1==loginDto.getLoginType()){
            //此时为账号密码登录
            FastUtils.checkParams(loginDto,loginDto.getMobile(),loginDto.getPassword());
            userVO = userService.doLoginUser(loginDto);
        }else if(2==loginDto.getLoginType()){
            //此时为验证码登录
            FastUtils.checkParams(loginDto,loginDto.getMobile(),loginDto.getVerificationCode());
            userVO = userService.doLoginUser(loginDto);
        }else {
            //手动抛异常登录方式选择错误
            throw new ServiceException(ResultCode.LOGIN_TIME_OUT);
        }
        return ok(userVO);
    }

    /**
     * @Description 获取当前登录人信息
     * @Author 胡翔鸿
     * @Data 2020/3/24 14:03
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "获取当前登录人信息",notes = "获取当前登录人信息")
    @PostMapping("getCurrLoginUser")
    public Result<UserVO> getCurrLoginUser (HttpServletRequest request){
        UserVO userVO = new UserVO();
        userVO = userService.getCurrLoginUser(request);
        return ok(userVO);
    }

    /**
     * @Description 安全设置页面信息查询（即为查询当前登录人信息）
     * @Author 胡翔鸿
     * @Data 2020/3/24 15:01
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "安全设置页面信息查询（即为查询当前登录人信息)",notes = "安全设置页面信息查询（即为查询当前登录人信息)")
    @PostMapping("findSecuritySetting")
    public Result<UserVO> findSecuritySetting (HttpServletRequest request){
        UserVO userVO = new UserVO();
        userVO = userService.findSecuritySetting(request);
        return ok(userVO);
    }

    /**
     * @Description 修改密码
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "修改密码接口",notes = "修改密码接口")
    @PostMapping("doUpdatePassword")
    public Result doUpdatePassword (HttpServletRequest request, @RequestBody UpdatePasswordDto updatePasswordDto){
        FastUtils.checkParams(updatePasswordDto,updatePasswordDto.getNewPassword());
        UserVO userVO = new UserVO();
        userVO = userService.getCurrLoginUser(request);
        UserDto userDto = new UserDto();
        userDto.setPassword(updatePasswordDto.getNewPassword());
        userDto.setUserId(userVO.getUserId());
        //进入修改密码的业务
        userService.doUpdatePassword(userDto);
        return ok();
    }

    /**
     * @Description 修改和设置邮箱接口
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "修改和设置邮箱接口",notes = "修改和设置邮箱接口")
    @PostMapping("doUpdateEmail")
    public Result doUpdateEmail (HttpServletRequest request, @RequestBody UpdateEmailDto updateEmailDto){
        FastUtils.checkParams(updateEmailDto,updateEmailDto.getEmail());
        UserVO userVO = new UserVO();
        userVO = userService.getCurrLoginUser(request);
        UserDto userDto = new UserDto();
        userDto.setCrmUserId(userVO.getCrmUserId());
        userDto.setUserId(userVO.getUserId());
        userDto.setMobile(userVO.getMobile());
        userDto.setEmail(updateEmailDto.getEmail());
        userDto.setCompanyName(userVO.getCompanyName());
        //进入修改邮箱的业务
        userService.doUpdateEmail(userDto);
        return ok();
    }

    /**
     * @Description 修改和手机号接口
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "修改和手机号接口",notes = "修改和手机号接口")
    @PostMapping("doUpdateMobile")
    public Result doUpdateMobile (HttpServletRequest request, @RequestBody UpdateMobileDto updateMobileDto ){
        FastUtils.checkParams(updateMobileDto,updateMobileDto.getOldMobile(),updateMobileDto.getNewMobile(),updateMobileDto.getVerificationCode());
        UserVO userVO = new UserVO();
        userVO = userService.getCurrLoginUser(request);
        //如果输入的原手机号和登录实际手机号不同，则抛异常
        if(!(userVO.getMobile().equals(updateMobileDto.getOldMobile()))){
            throw new ServiceException(ResultCode.MOBILE_DIFFERENT);
        }
        UserDto userDto = new UserDto();
        userDto.setCrmUserId(userVO.getCrmUserId());
        userDto.setUserId(userVO.getUserId());
        userDto.setMobile(updateMobileDto.getNewMobile());
        userDto.setEmail(userVO.getEmail());
        userDto.setCompanyName(userVO.getCompanyName());
        //进入修改邮箱的业务
        userService.doUpdateMobile(userDto,updateMobileDto);
        return ok();
    }

    /**
     * @Description 修改注册公司名字的接口
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "修改注册公司名字的接口",notes = "修改注册公司名字的接口")
    @PostMapping("doUpdateCompany")
    public Result doUpdateCompany (HttpServletRequest request, @RequestBody UpdateCompanyDto updateCompanyDto ){
        FastUtils.checkParams(updateCompanyDto,updateCompanyDto.getCompanyName());
        UserVO userVO = new UserVO();
        userVO = userService.getCurrLoginUser(request);
        UserDto userDto = new UserDto();
        userDto.setCrmUserId(userVO.getCrmUserId());
        userDto.setUserId(userVO.getUserId());
        userDto.setMobile(userVO.getMobile());
        userDto.setEmail(userVO.getEmail());
        userDto.setCompanyName(updateCompanyDto.getCompanyName());
        //进入修改邮箱的业务
        userService.doUpdateCompany(userDto);
        return ok();
    }

    /**
     * @Description 获取手机验证码
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "获取手机验证码",notes = "获取手机验证码")
    @PostMapping("findVerificationCode")
    public Result findVerificationCode (@RequestBody MobileDto mobileDto ){
        FastUtils.checkParams(mobileDto,mobileDto.getMobile());
        userService.findVerificationCode(mobileDto);
        return ok();
    }
}

package com.njwd.platform.service;

import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description 用户业务 Service
 * @Date 2020/3/23 15:12
 * @Author 胡翔鸿
 */
public interface UserService {

    /**
     * @Description: 
     * @Param: AddUserDto
     * @return: 
     * @Author: huxianghong
     * @Date: 2020/3/23 17:02
     */
    void addUser(AddUserDto addUserDto);

    /**
     * @Description:登录业务
     * @Param: LoginDto
     * @return: UserVO
     * @Author: huxianghong
     * @Date: 2020/3/24 9:22
     */
    UserVO doLoginUser(LoginDto loginDto);

    /**
     * @Description: 将登录人的信息处理放入redis
     * @Param: UserVO
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/24 10:34
     */
    void dealRedis(UserVO userVO);

    /**
     * 获取登录者信息
     * @param request
     * @return
     */
    UserVO getCurrLoginUser(HttpServletRequest request);

    /**
     * 字符串生成规则
     * @param key
     * @return
     */
    public String getCodeKey(String key);

    /**
     * @Description 安全设置页面信息查询（即为查询当前登录人信息）
     * @Author 胡翔鸿
     * @Data 2020/3/24 15:09
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    UserVO findSecuritySetting(HttpServletRequest request);

    /**
     * @Description 抽取方法，计算得到安全级别
     * @Author
     * @Data 2020/3/24 15:56
     * @Param String
     * @return Integer
     */
    Integer findSecurityLevel(String password);

    /**
     * @Description 修改密码
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return
     */
    void doUpdatePassword(UserDto userDto);

    /**
     * @Description 修改和设置邮箱接口
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return
     */
    void doUpdateEmail(UserDto userDto);

    /**
     * @Description 修改和手机号接口
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return
     */
    void doUpdateMobile(UserDto userDto, UpdateMobileDto updateMobileDto);

    /**
     * @Description 修改注册公司名字的接口
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return
     */
    void doUpdateCompany(UserDto userDto);

    /**
     * @Description 获取手机验证码
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return
     */
    void findVerificationCode(MobileDto mobileDto);

    /**
     * 抽取方法检验验证码是否正确
     * @param mobile
     * @return
     */
    Boolean checkVerificationCode(String mobile,String verificationCode);
}

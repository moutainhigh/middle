package com.njwd.aspect;

import com.alibaba.fastjson.JSONObject;
import com.njwd.annotation.NoLogin;
import com.njwd.annotation.Permissions;
import com.njwd.common.Constant;
import com.njwd.entity.admin.vo.AppVo;
import com.njwd.entity.admin.vo.UserVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.support.BaseController;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author Chenfulian
 * @Description AuthorizationInterceptor 登录验证过滤器
 * @Date 2019/12/16 14:39
 * @Version 1.0
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    @Resource
    private BaseController baseController;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //设置跨域--开始
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            setHeader(httpRequest, httpResponse);
            return true;
        }
        //设置跨域--结束

        //获取注解，标记了NoLogin注解的方法不需要进行登录验证
        NoLogin noLogin;
        if (handler instanceof HandlerMethod) {
            noLogin = ((HandlerMethod) handler).getMethodAnnotation(NoLogin.class);
            if (noLogin != null) {
                return true;
            }
        }

        //测试环境或者线上环境环境，校验登录信息
//        if (Constant.ProfileActive.TEST.equals(springProfilesActive) || Constant.ProfileActive.PROD.equals(springProfilesActive)) {
        if (Constant.ProfileActive.PROD.equals(springProfilesActive) || Constant.ProfileActive.LIANTIAO.equals(springProfilesActive)) {
//        if (Constant.ProfileActive.PROD.equals(springProfilesActive)
//                || Constant.ProfileActive.TEST.equals(springProfilesActive)
//                || Constant.ProfileActive.LIANTIAO.equals(springProfilesActive)
//        ) {
            //查询token信息
            UserVo user = baseController.getCurrLoginUserInfo();
            if (user == null) {
                throw new ServiceException(ResultCode.RECORD_NOT_EXIST);
            }

            // 验证权限
            Permissions permissions;
            if (handler instanceof HandlerMethod) {
                permissions = ((HandlerMethod) handler).getMethodAnnotation(Permissions.class);
                if (permissions != null) {
                    // 应用标识

                    String appSign = permissions.appSign(); // 菜单权限编码
                    String menuCode = permissions.menuCode();
                    // 按钮权限编码
                    String buttonCode = permissions.buttonCode();
                    LOGGER.info("preHandle appSign is {},menuCode is {},buttonCode is {}", appSign, menuCode, buttonCode);
                    if (user != null) {
                        // 判断应用
                        List<AppVo> roleInfoList = user.getRoleInfo();
                        if (roleInfoList != null && roleInfoList.size() > 0) {
                            roleInfoList.stream().forEach(role -> {
                                if (role.getAppSign() != null && role.getAppSign().equals(appSign)) {
                                    String codeStr = role.getRoleStr();
                                    LOGGER.info("preHandle codeStr is {}", codeStr);
                                    JSONObject codeJson = JSONObject.parseObject(codeStr);
                                    if (codeJson == null) {
                                        throw new ServiceException(ResultCode.PERMISSION_NOT);
                                    }
                                    LOGGER.info("preHandle codeJson is {}", codeJson);
                                    // 菜单权限
                                    if (StringUtil.isNotEmpty(menuCode)) {
                                        String[] menuCodes = menuCode.split(Constant.Character.COMMA);
                                        for (String menu : menuCodes) {
                                            if (StringUtil.isEmpty(codeJson.getString(menu))) {
                                                throw new ServiceException(ResultCode.PERMISSION_NOT);
                                            }
                                        }
                                    }

                                    // 按钮权限
                                    if (StringUtil.isNotEmpty(buttonCode)) {
                                        String[] buttonCodes = buttonCode.split(Constant.Character.COMMA);
                                        for (String button : buttonCodes) {
                                            if (StringUtil.isEmpty(codeJson.getString(button))) {
                                                throw new ServiceException(ResultCode.PERMISSION_NOT);
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            throw new ServiceException(ResultCode.PERMISSION_NOT);
                        }
                    } else {
                        throw new ServiceException(ResultCode.PERMISSION_NOT);
                    }
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * 为response设置header，实现跨域
     */
    private void setHeader(HttpServletRequest request, HttpServletResponse response) {
        //跨域的header设置
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", request.getMethod());
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        //防止乱码，适用于传输JSON数据
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
    }

}



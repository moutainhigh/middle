package com.njwd.admin.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.njwd.common.Constant;
import com.njwd.entity.admin.AuthParam;
import com.njwd.entity.admin.dto.EnteDto;
import com.njwd.entity.admin.vo.EnteVo;
import com.njwd.entity.admin.vo.UserVo;
import com.njwd.service.EnteService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import com.njwd.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author caihui
 * @description: 对接业务控制层
 * @date 2019/10/28 11:52
 */
@RestController
public class UserController extends BaseController {
    @Value("${zhongtai.ssoUrl}")
    private String ssoUrl;
    @Value("${zhongtai.redirectAdminUrl}")
    private String redirectAdminUrl;
    @Autowired
    private EnteService enteService;
    @Resource
    private RedisUtils redisUtil;
    /**
     * 自动登录
     * @return
     * @throws Exception
     */
    @RequestMapping("autoLogin")
    public void autoLogin(HttpServletRequest request) throws Exception {
        String jsonParam = request.getParameter("jsonParam");
        AuthParam authParam = JSONUtil.toBean(jsonParam, AuthParam.class);
        String signParam = authParam.getSign();
        String sign = SecureUtil.md5(String.format(Constant.InterfaceMethod.SIGN_FORMAT
                ,Constant.InterfaceMethod.KEY,Constant.InterfaceMethod.PLATFORM, authParam.getTimestamp()));
        String systemSignParam = authParam.getSystemSign();
        Instant fromDate = Instant.now();
        Instant toDate = Instant.ofEpochSecond(authParam.getTimestamp());
        Long howHours = ChronoUnit.HOURS.between(toDate,fromDate);
        if(signParam.equalsIgnoreCase(sign)
                && systemSignParam.equalsIgnoreCase(Constant.InterfaceMethod.PLATFORM)
                && howHours<=1){
            String result = null;
            String param = authParam.getSessionId();result = HttpRequest.post(ssoUrl)
                    .header("authorization", param)
                    .setConnectionTimeout(5000)
                    .execute().body();
            if (JSONUtil.isJson(result)) {
                UserVo user = JSONUtil.toBean(result,UserVo.class);
                //设置session信息
                String sessionId = setSession(request, user);
                Map<String, Object> map = new HashMap<>();
                map.put("authorization", sessionId);
                FastUtils.redirect(redirectAdminUrl, map, "get");
            }
        }
    }

    /**
     *设置session信息
     * @param request
     */
    private String setSession(HttpServletRequest request, UserVo user){
        HttpSession session = request.getSession();
        if ( session != null ) {
            session.invalidate(); // 废弃旧的 session
        }
        HttpSession sessionNew = request.getSession(true); // 开始一个新的 session
        user.setSessionId(sessionNew.getId());
        //根据企业ID获取企业名称
        EnteDto enteDto = new EnteDto();
        enteDto.setEnteId(user.getRootEnterpriseId());
        EnteVo enteVo = enteService.getEnteVoById(enteDto);
        if(null !=enteVo){
            user.setRootEnterpriseName(enteVo.getEnteName());
        }
        sessionNew.setMaxInactiveInterval(Constant.GetSessionTime.SESSION_TIME_2H);//两小时
        sessionNew.setAttribute("user",user);
        redisUtil.set(sessionNew.getId(),user);//把user绑定到sessionId存到redis
        int tokenTime = Constant.GetSessionTime.SESSION_TIME_2H;
        redisUtil.expire(sessionNew.getId(),tokenTime, TimeUnit.SECONDS);//设置有效时间
        return sessionNew.getId();
    }

    /**
     * 前端调用-获取当前登录人信息
     * @return
     */
    @RequestMapping("getCurrLoginUser")
    public Result getCurrLoginUser(){
        UserVo user = getCurrLoginUserInfo();
        return ok(user);
    }
}

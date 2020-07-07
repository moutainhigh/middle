package com.njwd.wrapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.vo.UserVo;
import com.njwd.utils.HttpRequestUtil;
import com.njwd.utils.RedisUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @Author Chenfulian
 * @Description 过滤器
 * @Date 2019/12/16 19:03
 * @Version 1.0
 */
public class WebHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebHttpServletRequestWrapper.class);

//    private final byte[] body;
    private byte[] body;

    public WebHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = HttpRequestUtil.getBodyContent(request).getBytes(Charset.forName("UTF-8"));
        try {
            String bodyString = new String(body);
            //空请求体处理
            if (StringUtil.isEmpty(bodyString) || bodyString.matches(Constant.RegExp.EMPTY_REQUEST_BODY)) {
                bodyString = Constant.Character.BRACKET_LEFT_B + Constant.Character.BRACKET_RIGHT_B;
                body = bodyString.getBytes(Charset.forName("UTF-8"));
            }
            //查询当前登录人信息，将企业信息放到body里
            HashMap bodyMap = JSON.parseObject(bodyString, HashMap.class);
            //从session中获取用户信息，进行登录校验
            UserVo user = (UserVo) request.getSession().getAttribute("user");
            if (user == null || user.getUserCode() == null) {
                //或者从redis中获取用户信息，进行登录校验
//                user = JSONUtil.toBean(RedisUtils.get(request.getHeader("authorization")).toString(), UserVo.class);
                user = (UserVo)RedisUtils.getObj(request.getHeader("authorization"));
            }
            if (user != null) {
                String enterpriseId = String.valueOf(user.getRootEnterpriseId());
                //企业id为空时从redis取
                LOGGER.info("WebHttpServletRequestWrapper bodyMap is {}",bodyMap);
                if (StringUtil.isEmpty((String.valueOf(bodyMap.get(AdminConstant.Character.ENTERPRISE_ID)))) && StringUtil.isEmpty(String.valueOf(bodyMap.get(AdminConstant.JOB_PARAM_KEY.ENTE_ID)))) {
                    bodyMap.put(AdminConstant.Character.ENTERPRISE_ID, enterpriseId);
                    bodyMap.put(AdminConstant.JOB_PARAM_KEY.ENTE_ID, enterpriseId);
                }
                LOGGER.info("WebHttpServletRequestWrapper new body is {}",bodyMap);
                body = JSON.toJSONString(bodyMap).getBytes(Charset.forName("UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("----- 修改body中的企业信息失败 ------", e.getMessage()));
        }

    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

}

package com.njwd.service.impl;


import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.njwd.entity.admin.Log;
import com.njwd.entity.admin.vo.UserVo;
import com.njwd.mapper.LogMapper;
import com.njwd.service.LogService;
import com.njwd.utils.IpUtils;
import com.njwd.utils.idworker.IdWorker;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 日志记录 ServiceImpl 类
 * @author XiaFq
 * @date 2020/1/7 11:16 上午
 */
@Service
public class LogServiceImpl implements LogService {

    private static final String LOG_CONTENT = "[类名]:%s,[方法]:%s,[参数]:%s,[IP]:%s";

    @Resource
    private LogMapper logMapper;

    @Resource
    IdWorker idWorker;

    /**
     * 日志记录
     *
     * @param joinPoint
     * @param methodName
     * @param module
     * @param description
     * @param user
     * @return
     * @author XiaFq
     * @date 2020/1/7 11:17 上午
     */
    @Override
    public void put(JoinPoint joinPoint, String methodName, String module, String description, UserVo user) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Log log = new Log();
            String userName = null;
            if (user != null) {
                userName = user.getUserName();
            } else {
                userName = "未知用户";
            }
            String ip = IpUtils.getIpAddr(request);
            String logId = idWorker.nextId();
            log.setLogId(logId);
            log.setUserId(user.getUserCode());
            log.setUserName(userName);
            log.setEnteId(user.getRootEnterpriseId().toString());
            log.setModule(module);
            log.setLogDesc(description);
            log.setIp(ip);
            log.setContent(operateContent(joinPoint, methodName, ip, request));
            log.setAble(1);
            logMapper.addLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造日志内容
     * @author XiaFq
     * @date 2020/1/8 7:17 下午
     * @param joinPoint
     * @param methodName
     * @param ip
     * @param request
     * @return java.lang.String
     */
    public String operateContent(JoinPoint joinPoint, String methodName, String ip, HttpServletRequest request) throws ClassNotFoundException, NotFoundException {
        String className = joinPoint.getTarget().getClass().getName();
        Object[] params = joinPoint.getArgs();
        String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = Class.forName(classType);
        String clazzName = clazz.getName();
        Map<String,Object > nameAndArgs = getFieldsName(this.getClass(), clazzName, methodName,params);
        StringBuffer bf = new StringBuffer();
        if (!CollectionUtils.isEmpty(nameAndArgs)){
            Iterator it = nameAndArgs.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                String value = JSONObject.toJSONString(entry.getValue());
                bf.append(key).append("=");
                bf.append(value).append("&");
            }
        }
        if (StringUtils.isEmpty(bf.toString())){
            bf.append(request.getQueryString());
        }
        return String.format(LOG_CONTENT, className, methodName, bf.toString(), ip);
    }

    /**
     * 获取字段名称
     * @author XiaFq
     * @date 2020/1/8 7:16 下午
     * @param cls
     * @param clazzName
     * @param methodName
     * @param args
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    private Map<String,Object> getFieldsName(Class cls, String clazzName, String methodName, Object[] args) throws NotFoundException {
        Map<String,Object > map=new HashMap<>();

        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);
        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            // exception
            return map;
        }
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < cm.getParameterTypes().length; i++){
            //paramNames即参数名
            map.put( attr.variableName(i + pos),args[i]);
        }
        return map;
    }
}


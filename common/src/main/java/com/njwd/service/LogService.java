package com.njwd.service;

import com.njwd.entity.admin.vo.UserVo;
import org.aspectj.lang.JoinPoint;

/**
 * 日志 service 类
 * @author XiaFq
 * @date 2020/1/7 11:15 上午
 */
public interface LogService {

    /**
     * 日志记录
     * @author XiaFq
     * @date 2020/1/7 11:17 上午
     * @param joinPoint
     * @param methodName
     * @param module
     * @param description
     * @param user
     * @return
     */
    void put(JoinPoint joinPoint, String methodName, String module, String description, UserVo user);
}

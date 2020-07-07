package com.njwd.aspect;

import com.njwd.annotation.Log;
import com.njwd.common.Constant;
import com.njwd.entity.admin.User;
import com.njwd.entity.admin.vo.UserVo;
import com.njwd.service.LogService;
import com.njwd.support.BaseController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;


/**
 *  日志 切面
 * @author XiaFq
 * @date 2020/1/16 4:29 下午
 */
@Component
@Aspect
public class LogAspect {

    @Resource
    private BaseController baseController;

    @Resource
    private LogService logService;

    /**
     * 日志切入点
     */
    @Pointcut("@annotation(com.njwd.annotation.Log)")
    public void logPointCut() {

    }

    @AfterReturning(pointcut = "logPointCut()")
    public void doAfter(JoinPoint joinPoint) {
        /**
         * 解析Log注解
         */
        String methodName = joinPoint.getSignature().getName();
        Method method = currentMethod(joinPoint, methodName);
        Log log = method.getAnnotation(Log.class);
        UserVo user = baseController.getCurrLoginUserInfo();
        logService.put(joinPoint,methodName,log.module(),log.description(), user);
    }

    /**
     * 获取当前执行的方法
     * @author XiaFq
     * @date 2020/1/7 9:41 上午
     * @param joinPoint
     * @param methodName
     * @return Method
     */
    private Method currentMethod(JoinPoint joinPoint, String methodName) {
        /**
         * 获取目标类的所有方法，找到当前要执行的方法
         */
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        Method resultMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                resultMethod = method;
                break;
            }
        }
        return resultMethod;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        if (target instanceof BaseController) {
            BaseController controller = (BaseController) target;
            String className = controller.getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            controller.logger.info(methodName + " 开始------");
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            controller.logger.info(methodName + " 结束------");
            long time = (end - start);
            if (time > Constant.SysConfig.LONG_TIME_THRESHOLD) {
                // 记录耗时大于1秒的接口
                controller.logger.error(Constant.SysConfig.LONG_OUT_TIME_LOG, className+"."+methodName, time);
            }else{
                // 输出接口调用耗时
                controller.logger.info(Constant.SysConfig.LONG_TIME_LOG, className+"."+methodName, time);
            }
            return result;
        }
        return joinPoint.proceed();
    }

}

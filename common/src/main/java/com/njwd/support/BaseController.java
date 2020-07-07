
package com.njwd.support;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.njwd.common.Constant;
import com.njwd.entity.admin.User;
import com.njwd.entity.admin.vo.UserVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.utils.FastUtils;
import com.njwd.utils.RedisUtils;
import feign.FeignException;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 公共 Controller。
 *
 * @author LuoY
 */
@RestControllerAdvice
public class BaseController {
    public final Logger logger;

    {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    protected <T> Result<T> ok(T data) {
        return new Result<T>().ok(data);
    }

    protected Result ok() {
        Result result = new Result().ok();
        return result;
    }

    protected Result ok(ResultCode resultCode, Object data) {
        Result result = new Result(resultCode);
        result.setData(data);
        return result;
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    protected Result error(String msg) {
        return error(msg, ResultCode.BAD_REQUEST);
    }

    protected Result error(ResultCode resultCode) {
        throw new ServiceException(resultCode);
    }

    protected Result error(String msg, ResultCode resultCode) {
        throw new ServiceException(msg, resultCode);
    }

    protected Result result(ResultCode resultCode) {
        Result result = new Result(resultCode);
        return result;
    }

    protected Result<Boolean> confirm(int result) {
        return result > 0 ? ok(true) : error(ResultCode.OPERATION_FAILURE);
    }

    public <T> Result<T> error(ResultCode resultCode, T data) {
        throw new ServiceException(resultCode, data);
    }

    /**
     * 进行异常处理,该方法与出错的方法必须在同一个Controller里面,使用该方法时,所有的Controller都需要继承该类
     */
    @ExceptionHandler(Exception.class)
    @SuppressWarnings("all")
    public Result handleException(Exception e, HttpServletRequest request) {
        Result result;
        if (e instanceof LoginException) {
            result = new Result(Constant.ReqResult.FAIL, ResultCode.ACCOUNT_NOT);
        } else if (e instanceof HttpMessageNotReadableException || e instanceof HttpMediaTypeNotSupportedException) {
            result = new Result(Constant.ReqResult.FAIL, ResultCode.PARAMS_NOT);
        } else if (e instanceof ServiceException) {
            ServiceException se = (ServiceException) e;
            result = new Result(Constant.ReqResult.FAIL, se.code, se.message, se.data);
        } else if (e instanceof FeignException) {
            result = new Result(Constant.ReqResult.ERROR, ResultCode.FEIGN_CONNECT_ERROR.code, String.format(ResultCode.FEIGN_CONNECT_ERROR.message, e.getMessage()), null);
        } else if (e instanceof MyBatisSystemException) {
            String message = e.getCause().getMessage();
            if (message.indexOf(ResultCode.SYS_USER_INVALID.message) > -1) {
                result = new Result(Constant.ReqResult.FAIL, ResultCode.SYS_USER_INVALID);
            }
            if (message.indexOf(Constant.Exception.ERRORSQL) > -1) {
                result = new Result(Constant.ReqResult.ERROR, ResultCode.SQL_ERROR_EXCEPTION);
            } else {
                result = new Result(Constant.ReqResult.ERROR, ResultCode.INTERNAL_SERVER_ERROR.code, message, null);
            }
        } else {
            result = new Result(Constant.ReqResult.ERROR, ResultCode.INTERNAL_SERVER_ERROR);
        }
        // 记录业务异常日志
        FastUtils.loggerRequestBody(logger, request);
        logger.error("接口: {} 异常，异常状态码 {}，异常信息：{}", request.getRequestURI(), result.getCode(), result.getMessage(), e);
        return result;
    }

    /**
     * 获取当前登录用户信息
     * @return
     */
    public UserVo getCurrLoginUserInfo() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        UserVo user = (UserVo) ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getSession().getAttribute("user");
        try {
            logger.info("getCurrLoginUserInfo request.getHeader('authorization') is {}", request.getHeader("authorization"));
            if (user == null || user.getUserCode() == null) {
                //从redis中获取用户信息
                Object obj = RedisUtils.getObj(request.getHeader("authorization"));
                user = (UserVo) obj;
                if (user == null || user.getUserCode() == null) {
                    throw new ServiceException(ResultCode.TOKEN_INVALID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.TOKEN_INVALID);
        }
        return user;
    }

}

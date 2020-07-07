package com.njwd.common;

/**
 * 中台大平台常量定义
 */
public interface PlatformContant {

    /**
     * 登录有关常量
     */
    interface Login {
        String REDIS_TOKEN = "token";
        String LOGIN_USER = "loginUser";
        String AUTH_TOKEN_KEY = "authorization-token";
        Integer LOGIN_TIME = 3600;
        Double MAX_SCORE = 5.0;
        Integer MAX_REMARK = 200;
        String VERIFICATION_CODE = "verificationCode";
        Integer VERIFICATION_TIME = 68;
        byte[] PWD = {0,3,1,1,0,1,3,0,2,1,4,0,0,2,0,2};
    }

    /**
     * 相关正则表达式
     */
    interface RegularString{
        String CONTAIN_NUMBER = ".*[0-9].*";
        String CONTAIN_CAPITAL = ".*[A-Z].*";
        String CONTAIN_LOWERCASE = ".*[a-z].*";
        String LETTER_DIGIT_REGEX = "^[a-z0-9A-Z]+$";
        String UNDERLINE_SIGN = "_(\\w)";
    }

    /**
     * 调用外部接口入参和返回常量
     */
    interface ReturnString{
        String RETURN_SUCCESS = "success";
        String URL_PAT = "&json_str=";
        String CRM_URL_PAT = "&jsonData=";
        String CRM_PAY_PAT = "&json=";
        String URL_CUSTOMER_ID = "&customer_id=";
        Long ENTERPRISE_ID = 1L;
        Integer APPLY_TYPE_BALANCE = 1;
        String ENTERPRISE_NAME = "南京网兜信息科技有限公司";
    }

    /**
     * 一些固定值参数
     */
    interface FixedParameter{
        String MIDDLE_CHANNEL = "S001";
        Long ROOT_ORG_ID = 1L;
        Integer ROUND_START = 0;//循环初始值
        Integer ROUND_END = 4;//循环结束值
        Integer STATUS_START = 0;//状态默认值
     }

    /**
     * 支付相关固定参数
     */
    interface PayOnline{
        String IDENTIFICATION = "payCode";
        String MEANS_TYPE = "wechatpayQr";
        String TYPE = "wecahtpayQrMiddleRecharge";
        String NOTIFY_URL = "wechatpayNotifyUrl";
    }

}

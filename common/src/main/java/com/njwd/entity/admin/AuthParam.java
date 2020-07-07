package com.njwd.entity.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthParam {
    /**
     * 请求类型
     * 1：开通账号 2：停用账号
     * 3：登录账号 4：修改密码
     */
    private String request;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 员工编码
     */
    private String workcode;
    /**
     * 密码
     */
    private String password;
    /**
     * 系统标识
     */
    private String systemSign;

    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 验证签名
     */
    private String sign;

    /**
     * sessionID
     */
     private String sessionId;

    /**
     * 系统唯一标识
     */
    private String appSign;
}

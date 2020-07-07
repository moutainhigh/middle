package com.njwd.entity.basedata;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/4 10:04
 */
@Getter
@Setter
public class DatabaseInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 连接名
     */
    private String connectionName;

    /**
     * 连接地址
     */
    private String hostName;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 端口
     */
    private String dbPort;

    /**
     * 用户
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据库连接类型
     */
    private String dbConnectType;

}

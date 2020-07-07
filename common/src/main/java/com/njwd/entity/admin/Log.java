package com.njwd.entity.admin;

import lombok.Data;

import java.util.Date;

/**
 * @Author XiaFq
 * @Description Log TODO
 * @Date 2020/1/7 11:25 上午
 * @Version 1.0
 */
@Data
public class Log {

    /**
     * 编号
     */
    private String logId;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 模块
     */
    private String module;

    /**
     * 日志描述
     */
    private String logDesc;

    /**
     * 内容
     */
    private String content;

    /**
     * ip
     */
    private String ip;

    /**
     * 是否可见
     */
    private Integer able;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}

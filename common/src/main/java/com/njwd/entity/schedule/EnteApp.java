package com.njwd.entity.schedule;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:表wd_ente_app
 * @Author: yuanman
 * @Date: 2019/11/6 13:39
 */
@Data
public class EnteApp {
    /**
     *主键
     */
    private String enteAppId;
    /**
     *企业id
     */
    private String enteId;
    /**
     *应用id
     */
    private String appId;
    /**
     *是否输入
     */
    private Integer isInput;
    /**
     *是否输出
     */
    private Integer isOutput;
    /**
     *网络类型 public：公网，private：私网，vpn
     */
    private String networkType;
    /**
     *连接信息 对于api和ant两种类型的通用的配置信息，例如爬的网页地址，api的官网接口地址
     */
    private String srcConfig;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *对接方式 DB：数据库；API：接口；ANT：爬虫
     */
    private String jointMode;
    /**
     *接口子集
     */
    private List<AppInterface> appInterfaces=new ArrayList<>();

}
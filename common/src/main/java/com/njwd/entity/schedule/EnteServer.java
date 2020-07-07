package com.njwd.entity.schedule;

import lombok.Data;

import java.util.Date;

/**
 * @Description:表wd_ente_server
 * @Author: yuanman
 * @Date: 2019/11/6 13:39
 */
@Data
public class EnteServer {
    /**
     *配置详情id
     */
    private String configId;
    /**
     *企业id
     */
    private String enteId;
    /**
     *配置类型 数仓DATA_DB，业仓BUS_DB，服务器SERVER
     */
    private String type;
    /**
     *连接网络类型 public-公网，private-私网。
     */
    private String networkType;
    /**
     *连接配置信息 保存连接信息。数据库时存放数据库类型和驱动，服务器时存放核心线程数信息
     */
    private String connectConfig;
    /**
     *备注
     */
    private String remark;
    /**
     *创建时间
     */
    private Date createTime;


}
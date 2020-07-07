package com.njwd.entity.admin;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author XiaFq
 * @Description EntepriseServer 企业服务器配置详情表
 * @Date 2019/11/11 10:55 上午
 * @Version 1.0
 */
@Data
public class EnterpriseServer implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * 配置详情id
     */
    private String configId;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 配置类型 数仓DATA_DB，业仓BUS_DB，服务器SERVER
     */
    private String type;

    /**
     * 连接网络类型 public-公网，private-私网。
     */
    private String networkType;

    /**
     * 连接配置信息 保存连接信息。数据库时存放数据库类型和驱动，服务器时存放核心线程数信息
     */
    private String connectConfig;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间 默认为当前时间
     */
    private Date createTime;
}

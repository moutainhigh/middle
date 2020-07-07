package com.njwd.entity.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author XiaFq
 * @Description EnterpriseApp 企业-应用关联表
 * @Date 2019/11/12 10:43 上午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class EnterpriseApp implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * id
     */
    private String enterpriseAppId;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 是否作为数据输入
     */
    private int isInput;

    /**
     * 是否作为输出
     */
    private int isOutput;

    /**
     * 网络类型 public：公网，private：私网，vpn
     */
    private String networkType;

    /**
     * 连接信息 对于api和ant两种类型的通用的配置信息，例如爬的网页地址，api的官网接口地址
     */
    private String srcConfig;

    /**
     * 创建时间 默认为当前时间
     */
    private Date createTime;
}

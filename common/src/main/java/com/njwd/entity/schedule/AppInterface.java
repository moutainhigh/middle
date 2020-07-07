package com.njwd.entity.schedule;

import lombok.Data;

import java.util.Date;

@Data
public class AppInterface {
    /**
     *接口id
     */
    private String interfaceId;
    /**
     *企业id
     */
    private String enteId;
    /**
     *应用id
     */
    private String appId;
    /**
     *接口名称
     */
    private String interfaceName;
    /**
     *接口配置
     */
    private String interfaceConfig;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *更新时间
     */
    private Date updateTime;

}
package com.njwd.entity.admin.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author XiaFq
 * @Description DataAppConfigDto TODO
 * @Date 2019/12/11 10:27 上午
 * @Version 1.0
 */
@Data
public class DataAppConfigDto {

    /**
     * id
     */
    private String id;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 类型编码
     */
    private String typeCode;

    /**
     * 类型 0 主数据 1 业务数据
     */
    private String type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}

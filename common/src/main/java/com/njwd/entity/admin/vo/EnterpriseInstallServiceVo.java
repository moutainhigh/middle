package com.njwd.entity.admin.vo;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description EnterpriseInstallServiceVo 企业安装的中台服务
 * @Date 2019/11/11 2:02 下午
 * @Version 1.0
 */
@Data
public class EnterpriseInstallServiceVo {

    /**
     * 服务id
     */
    private String serviceId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 数据来源
     */
    private String dbSource;

    /**
     * 图标
     */
    private String icon;
}

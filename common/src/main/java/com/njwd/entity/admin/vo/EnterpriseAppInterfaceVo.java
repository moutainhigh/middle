package com.njwd.entity.admin.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author XiaFq
 * @Description EnterpriseAppInterfaceVo 企业应用-接口关联信息Vo
 * @Date 2019/11/30 2:27 下午
 * @Version 1.0
 */
@Data
public class EnterpriseAppInterfaceVo {

    /**
     * 编号
     */
    private String interfaceId;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 接口配置信息
     */
    private String interfaceConfig;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 接口类型
     */
    private String interfaceType;
}

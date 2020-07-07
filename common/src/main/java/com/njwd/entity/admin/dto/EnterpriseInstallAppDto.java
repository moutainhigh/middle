package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description EnterpriseInstallAppDTO TODO
 * @Date 2019/11/11 2:46 下午
 * @Version 1.0
 */
@Data
public class EnterpriseInstallAppDto {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 企业应用Id
     */
    private String enterpriseAppId;
    /**
     * 应用名称-用于模糊查询
     */
    private String appName;
}

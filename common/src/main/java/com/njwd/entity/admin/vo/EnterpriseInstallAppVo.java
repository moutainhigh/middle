package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.EnterpriseApp;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author XiaFq
 * @Description EnterpriseInstallAppVo 企业安装的第三方应用
 * @Date 2019/11/11 2:17 下午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class EnterpriseInstallAppVo extends EnterpriseApp {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用图标
     */
    private String icon;
}

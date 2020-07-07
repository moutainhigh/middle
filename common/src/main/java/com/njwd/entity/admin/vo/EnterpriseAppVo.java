package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.EnterpriseApp;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppVo TODO
 * @Date 2019/11/11 2:30 下午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class EnterpriseAppVo extends EnterpriseApp {

    /**
     * 安装的中台服务列表
     */
    private List<EnterpriseInstallServiceVo> serviceList;

    /**
     * 安装的第三方应用列表
     */
    private List<EnterpriseInstallAppVo> appList;
}

package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.EnterpriseApp;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppInfoVo 企业应用信息
 * @Date 2019/11/11 6:00 下午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class EnterpriseAppInfoVo extends EnterpriseApp {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 对接方式
     */
    private String jointMode;

    /**
     * 对接方式名称
     */
    private String jointModeName;

    /**
     * 应用类型
     */
    private String appType;

    /**
     * 应用类型名称
     */
    private String appTypeName;

    /**
     * 参数格式
     */
    private String jointParam;

    /**
     * 标签
     */
    private String tagNames;
}

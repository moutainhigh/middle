package com.njwd.entity.admin.dto;

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
public class EnterpriseAppInfoDto extends EnterpriseApp {

    /**
     * 企业-应用id
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
     * 对接方式
     */
    private String jointMode;

    /**
     * 应用类型
     */
    private String appType;

    /**
     * 参数格式
     */
    private String jointParam;

    /**
     * 标签
     */
    private List<String> tagIds;
}

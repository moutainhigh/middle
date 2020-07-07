package com.njwd.entity.admin.vo;

import lombok.Data;
import lombok.NonNull;

/**
 * @Author XiaFq
 * @Description MasterDataAppVo 主数据应用Vo类
 * @Date 2019/11/18 5:02 下午
 * @Version 1.0
 */
@Data
public class MasterDataAppVo {

    /**
     * 企业id
     */
    private String enterpriseId;

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
     * 主数据来源id 用来区分是否是主数据
     */
    private String sourceId;

    /**
     * key
     */
    private String key;
}

package com.njwd.entity.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author XiaFq
 * @Description DataAppConfigVo 数据应用配置Vo类
 * @Date 2019/12/10 6:57 下午
 * @Version 1.0
 */
@Data
public class DataAppConfigVo implements Serializable {

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
     * 主数据来源列表
     */
    List<DataCategoryVo> masterDataList;

    /**
     * 业务数据来源列表
     */
    List<DataCategoryVo> businessDataList;
}

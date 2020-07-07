package com.njwd.entity.admin.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author Chenfulian
 * @Description 基准配置项Vo
 * @Date 2019/12/10 17:20
 * @Version 1.0
 */
@Data
public class BenchmarkConfigVo {
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 配置id
     */
    private String configCode;
    /**
     * 配置名称
     */
    private String configName;
    /**
     * 指标编码
     */
    private String metricsCode;
    /**
     * 指标名称
     */
    private String metricsName;
    /**
     * 种类编码
     */
    private String categoryCode;
    /**
     * 种类名称
     */
    private String categoryName;
    /**
     * sql
     */
    private String configSql;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}

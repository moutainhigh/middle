package com.njwd.entity.reportdata;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author liuxiang
 * @Description 财务报告项目明细设置
 * @Date:16:59 2019/8/1
 **/
@Getter
@Setter
public class FinancialReportItemSet implements Serializable {
    private static final long serialVersionUID = 1861689627800566455L;
    /**
     * 主键 默认自动递增
     */
    private Integer reportItemSetId;

    /**
     * 报表id
     */
    private Integer reportId;

    /**
     * 报表名称
     */
    private String reportName;

    /**
     * 项目序号
     */
    private String itemNumber;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 项目类型
     */
    private Integer itemType;

    /**
     * 项目类型名称
     */
    private String itemTypeName;

    /**
     * 项目级次
     */
    private Integer itemLevel;

    /**
     * 显示排序
     */
    private Integer sortNum;
}
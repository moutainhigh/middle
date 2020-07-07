package com.njwd.entity.basedata;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Description: 报表项目配置
* @Author: LuoY
* @Date: 2019/12/29 13:19
*/
@Data
public class BaseReportItemSet implements Serializable {
    private static final long serialVersionUID = -1631538417519837570L;
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
     * 数据类型 1.金额，2.百分比，3.数字，4.无
     */
    private Integer dataType;

    /**
    * 序号
    */
    private Integer sortNum;
}


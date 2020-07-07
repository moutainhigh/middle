package com.njwd.entity.reportdata;

import lombok.Getter;
import lombok.Setter;

/**
* @Description: 现金流量表
* @Author: lj
* @Date: 2020/2/10 9:44
*/
@Getter
@Setter
public class FinCashFlowReportTable {
    /**
     * 企业id
     */
    private String enteId;
    /**
     * 报表id
     */
    private Integer reportId;

    /**
     * 记账期间年号
     */
    private Integer periodYearNum;

    /**
    * 项目名称
    */
    private String itemName;

    /**
     * 项目行次
     */
    private String itemLine;

    /**
     * 显示序号
     */
    private String itemNumber;

    /**
     * 项目级次 0：标题、1：一级、2：二级、3：三级、4：小计、5：合计、6：总计
     */
    private String itemLevel;

    /**
     * 显示排序
     */
    private Integer sortNum;

    /**
     * 本期金额
     */
    private String amountBalance;

    /**
     * 上期金额
     */
    private String lastAmountBalance;
}

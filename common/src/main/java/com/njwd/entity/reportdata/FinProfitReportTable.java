package com.njwd.entity.reportdata;

import lombok.Data;

/**
* @Description: 利润表
* @Author: LuoY
* @Date: 2020/2/10 9:44
*/
@Data
public class FinProfitReportTable {
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
     * 显示级次
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
     * 本年累计金额
     */
    private String totalAmountBalance;
}

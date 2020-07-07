package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
* @Description: 经营日报项目
* @Author: LuoY
* @Date: 2019/12/28 16:13
*/
@Data
public class BusinessReportDayItemVo {
    /**
     * 项目排序
     */
    private Integer id;

    /**
     * 项目序号
     */
    private String itemNumber;
    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目级次
     */
    private Integer itemLevel;

    /**
     * 项目类型
     */
    private Integer itemType;

    /**
     * 项目类型名称
     */
    private String itemTypeName;

    /**
     * 本期金额
     */
    private BigDecimal currentMoney;

    /**
     * 数据类型
     */
    private Integer dataType;

    /**
     * 项目指标
     */
    private BigDecimal index;

    /**
     * 项目指标完成率
     */
    private BigDecimal completionIndex;

    /**
     * 指标完成排名
     */
    private Integer indexRanking;

    /**
     * 同比
     */
    private BigDecimal yearCompare;

    /**
     * 环比
     */
    private BigDecimal monthCompare;

    /**
     * 上期发生
     */
    private BigDecimal lastPeriod;

    /**
     * 去年同期发生
     */
    private BigDecimal lastYear;

    /**
     * 期间内月均
     */
    private Integer monthAverage;
}

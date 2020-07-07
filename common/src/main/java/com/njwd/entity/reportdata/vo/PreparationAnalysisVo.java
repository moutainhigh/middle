package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
* @Description: 筹建经营分析项目Vo
* @Author: 周鹏
* @Date: 2020/2/11 16:13
*/
@Data
public class PreparationAnalysisVo {
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

    /**
     * 同比
     */
    private BigDecimal yearCompare;

    /**
     * 环比
     */
    private BigDecimal monthCompare;
}

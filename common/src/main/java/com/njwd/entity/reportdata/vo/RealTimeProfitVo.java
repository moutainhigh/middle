package com.njwd.entity.reportdata.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
* @Description: 实时利润分析Vo
* @Author: liBao
* @Date: 2020/2/18 16:13
*/
@Getter
@Setter
public class RealTimeProfitVo {
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
     * 本期金额
     */
    private BigDecimal currentMoney;

    /**
     * 本年金额
     */
    private BigDecimal yearMoney;

    /**
     * 去年本期金额
     */
    private BigDecimal lastCurrentMoney;

    /**
     * 上期金额
     */
    private BigDecimal latelyCurrentMoney;

    /**
     * 预算数
     */
    private BigDecimal BudgetMoney;

    /**
     * 预算比
     */
    private BigDecimal BudgetCompare;


    /**
     * 本年累计占比
     */
    private BigDecimal yearProportion;

    /**
     * 本期占比
     */
    private BigDecimal currentProportion;

    /**
     * 同比
     */
    private BigDecimal sameCompareProportion;

    /**
     * 环比
     */
    private BigDecimal chainCompareProportion;

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
     * 数据类型
     */
    private Integer dataType;

    /**
     * 报表Id
     */
    private Integer reportId;

    /**
     * 展开类型
     */
    private String type;

    /**
     * 毛利率
     */
    private BigDecimal grossMargin;

    /**
     * 菜品成本
     */
    private BigDecimal dishesCost;
    /**
     * 销售成本
     */
    private BigDecimal saleCost;

    /**
     * 待计算金额
     */
    private BigDecimal calculationMoney;

}

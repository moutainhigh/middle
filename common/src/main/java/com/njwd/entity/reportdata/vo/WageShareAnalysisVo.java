package com.njwd.entity.reportdata.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 工资占销分析
 * @Author: ZhuHC
 * @Date: 2020/3/27 13:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WageShareAnalysisVo extends BaseScopeOfQueryType implements Serializable {

    /**
     * 营业额
     */
    private BigDecimal turnover = BigDecimal.ZERO;

    /**
     * 应发实际工资
     */
    private BigDecimal wage = BigDecimal.ZERO;

    /**
     * 占销比
     */
    private BigDecimal salesRatio;

    /**
     * 净利润
     */
    private BigDecimal profit = BigDecimal.ZERO;

    /**
     * 占净利润比
     */
    private BigDecimal profitRatio;

    /**
     *  去年同期-营业额
     */
    private BigDecimal lastYearTurnover = BigDecimal.ZERO;

    /**
     * 去年同期-应发实际工资
     */
    private BigDecimal lastYearWage = BigDecimal.ZERO;

    /**
     * 去年同期-占销比
     */
    private BigDecimal lastYearSalesRatio;

    /**
     * 去年同期-净利润
     */
    private BigDecimal lastYearProfit = BigDecimal.ZERO;

    /**
     * 去年同期-占净利润比
     */
    private BigDecimal lastYearProfitRatio;

    /**
     *  上期-营业额
     */
    private BigDecimal lastPeriodTurnover = BigDecimal.ZERO;

    /**
     * 上期-应发实际工资
     */
    private BigDecimal lastPeriodWage = BigDecimal.ZERO;

    /**
     * 上期-占销比
     */
    private BigDecimal lastPeriodSalesRatio;

    /**
     * 上期-净利润
     */
    private BigDecimal lastPeriodProfit = BigDecimal.ZERO;

    /**
     * 上期-占净利润比
     */
    private BigDecimal lastPeriodProfitRatio;

}

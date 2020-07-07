package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 看板（店长视角）查询数据（利润,净利率）
 *
 * @author shenhf
 * @date 2020-03-25 18:02
 */
@Data
public class ViewNetProfitVo {
    private static final long serialVersionUID = 8293579388252687383L;
    /**
     * 本期净利润 合计
     */
    private BigDecimal netProfit = new BigDecimal(0);
    /**
     * 上期净利润 合计
     */
    private BigDecimal shangNetProfit = new BigDecimal(0);
    /**
     * 同期净利润 合计
     */
    private BigDecimal tongNetProfit = new BigDecimal(0);
    /**
     * 本期收入合计
     */
    private BigDecimal currentMoney = new BigDecimal(0);
    /**
     * 上期收入合计
     */
    private BigDecimal shangCurrentMoney = new BigDecimal(0);
    /**
     * 同期收入合计
     */
    private BigDecimal tongCurrentMoney = new BigDecimal(0);
    /**
     * 本期成本合计
     */
    private BigDecimal costMoney = new BigDecimal(0);
    /**
     * 上期成本合计
     */
    private BigDecimal shangCostMoney = new BigDecimal(0);
    /**
     * 同期成本合计
     */
    private BigDecimal tongCostMoney = new BigDecimal(0);

}


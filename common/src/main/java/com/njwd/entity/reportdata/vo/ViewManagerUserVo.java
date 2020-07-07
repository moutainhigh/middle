package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 看板（店长视角）查询数据（在职离职员工数）
 *
 * @author shenhf
 * @date 2020-03-25 18:02
 */
@Data
public class ViewManagerUserVo {
    private static final long serialVersionUID = 8293579388252687383L;
    /**
     * 本期在职员工数 合计
     */
    private BigDecimal onTheJobSum = new BigDecimal(0);
    /**
     * 上期在职员工数 合计
     */
    private BigDecimal shangOnTheJobSum = new BigDecimal(0);
    /**
     * 同期在职员工数 合计
     */
    private BigDecimal tongOnTheJobSum = new BigDecimal(0);
    /**
     * 本期离职员工数 合计
     */
    private BigDecimal leaveSum = new BigDecimal(0);
    /**
     * 上期离职员工数 合计
     */
    private BigDecimal shangLeaveSum = new BigDecimal(0);
    /**
     * 同期离职员工数 合计
     */
    private BigDecimal tongLeaveSum = new BigDecimal(0);

}


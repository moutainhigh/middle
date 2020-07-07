package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 看板（店长视角）查询数据（增送金额）
 *
 * @author shenhf
 * @date 2020-03-25 18:02
 */
@Data
public class ViewManagerGiveVo {
    private static final long serialVersionUID = 8293579388252687383L;
    /**
     * 本期增送金额 合计
     */
    private BigDecimal giveAmount = new BigDecimal(0);
    /**
     * 上期增送金额 合计
     */
    private BigDecimal shangGiveAmount = new BigDecimal(0);
    /**
     * 同期增送金额 合计
     */
    private BigDecimal tongGiveAmount = new BigDecimal(0);


}


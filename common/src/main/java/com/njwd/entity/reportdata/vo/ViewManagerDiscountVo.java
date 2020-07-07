package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 看板（店长视角）查询数据（折扣率）
 *
 * @author shenhf
 * @date 2020-03-25 18:02
 */
@Data
public class ViewManagerDiscountVo {
    private static final long serialVersionUID = 8293579388252687383L;
    /**
     * 本期折扣率
     */
    private BigDecimal discountRate = new BigDecimal(0);
    /**
     * 上期折扣率
     */
    private BigDecimal shangDiscountRate = new BigDecimal(0);
    /**
     * 同期折扣率
     */
    private BigDecimal tongDiscountRate = new BigDecimal(0);


}


package com.njwd.entity.reportdata.vo.fin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *@description: 实时利润分析Vo
 *@author: liBao
 *@create: 2020-03-2
 */
@Getter
@Setter
@ToString
public class RealProfitVo implements Serializable {


    //门店Id
    private String shopId;

    //本期应提折旧额
    private BigDecimal shouldDepr ;

    //价税合计
    private BigDecimal allAmount;

    //成本
    private BigDecimal amount;

    /**
     * 预算数
     */
    private BigDecimal budgetMoney;

    /**
     * 预算比
     */
    private BigDecimal budgetCompare;






}

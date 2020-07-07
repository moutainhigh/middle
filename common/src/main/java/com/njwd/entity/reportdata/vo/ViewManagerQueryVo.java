package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 看板（店长视角）查询数据
 *
 * @author zhuzs
 * @date 2019-12-25 18:02
 */
@Data
public class ViewManagerQueryVo {
    private static final long serialVersionUID = 8293579388252687383L;
    /**
     * 本期订单数 合计
     */
    private BigDecimal orderNum = new BigDecimal(0);
    /**
     * 上期订单数 合计
     */
    private BigDecimal shangOrderNum = new BigDecimal(0);
    /**
     * 同期订单数 合计
     */
    private BigDecimal tongOrderNum = new BigDecimal(0);
    /**
     * 本期开台数 合计
     */
    private BigDecimal stationsNum = new BigDecimal(0);
    /**
     * 上期开台数 合计
     */
    private BigDecimal shangStationsNum = new BigDecimal(0);
    /**
     * 同期开台数 合计
     */
    private BigDecimal tongStationsNum = new BigDecimal(0);
    /**
     * 本期堂食客流数 合计
     */
    private BigDecimal customNum = new BigDecimal(0);
    /**
     * 上期堂食客流数 合计
     */
    private BigDecimal shangCustomNum = new BigDecimal(0);
    /**
     * 同期堂食客流数 合计
     */
    private BigDecimal tongCustomNum = new BigDecimal(0);
    /**
     * 本期销售额 合计
     */
    private BigDecimal consumeSum = new BigDecimal(0);

    /**
     * 上期销售额 合计
     */
    private BigDecimal shangConsumeSum = new BigDecimal(0);
    /**
     * 同期销售额 合计
     */
    private BigDecimal tongConsumeSum = new BigDecimal(0);
    /**
     * 本期堂食销售额 合计
     */
    private BigDecimal consumeTangSum = new BigDecimal(0);

    /**
     * 上期堂食销售额 合计
     */
    private BigDecimal shangConsumeTangSum = new BigDecimal(0);
    /**
     * 同期堂食销售额 合计
     */
    private BigDecimal tongConsumeTangSum = new BigDecimal(0);
    /**
     * 本期收款项 合计
     */
    private BigDecimal receivableSum = new BigDecimal(0);
    /**
     * 上期收款项 合计
     */
    private BigDecimal shangReceivableSum = new BigDecimal(0);
    /**
     * 同期收款项 合计
     */
    private BigDecimal tongReceivableSum = new BigDecimal(0);
    /*
    * 是否计入开台数标识
    * */
    private Integer noOpenTable;
    /*
    * 实收是否为0
    * */
    private Integer def1;
    /*
    * 开桌渠道
    * */
    private Integer channelId;

}


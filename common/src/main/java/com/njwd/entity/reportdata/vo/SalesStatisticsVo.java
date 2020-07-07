package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 销售情况统计表
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
public class SalesStatisticsVo {
    /**
     * 企业名称
     */
    private String enteName;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 收入分析
     */
    private SaleStatisticsIncomeVo saleStatisticsIncomeVo;

    /**
     * 桌台分析
     */
    private List<SaleStatisticsDeskVo> saleStatisticsDeskVos;

    /**
     * 消费分析
     */
    private List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos;

    /**
     * 赠送分析
     */
    private List<SaleStatisticsGiveVo> saleStatisticsGiveVos;

    /**
     * 品牌list
     */
    private List<SaleStatisticsBrandVo> statisticsBrandVos;
    /**
     * 全部品牌汇总
     */
    private BigDecimal allBrandAmountCount;
}

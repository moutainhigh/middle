package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 销售情况统计表门店
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
public class SaleStatisticsShopVo {
    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 门店code
     */
    private String shopCode;

    /**
     * 收入分析
     */
    SaleStatisticsIncomeVo saleStatisticsIncomeVo;

    /**
     * 桌台分析
     */
    List<SaleStatisticsDeskVo> saleStatisticsDeskVos;

    /**
     * 消费分析
     */
    List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos;

    /**
     * 赠送分析
     */
    List<SaleStatisticsGiveVo> saleStatisticsGiveVos;
}

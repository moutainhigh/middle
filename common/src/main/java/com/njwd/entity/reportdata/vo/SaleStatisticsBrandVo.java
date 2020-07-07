package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 销售情况统计表品牌
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
public class SaleStatisticsBrandVo {

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 品牌code
     */
    private String brandCode;

    /**
     * 区域list
     */
    private List<SaleStatisticsRegionVo> saleStatisticsRegionVos;

    /**
     * 区域list
     */
    private BigDecimal allRegionAmountCount;
}

package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 销售情况统计表大区
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
public class SaleStatisticsRegionVo {
    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 区域code
     */
    private String regionCode;

    /**
     * 门店list
     */
    private List<SaleStatisticsShopVo> saleStatisticsShopVos;

    /**
     * 门店list
     */
    private BigDecimal allShopAmountCount;
}

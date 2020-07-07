package com.njwd.entity.reportdata;

import com.njwd.entity.reportdata.vo.SaleStatisticsConsumptionVo;
import com.njwd.entity.reportdata.vo.SaleStatisticsDeskVo;
import com.njwd.entity.reportdata.vo.SaleStatisticsGiveVo;
import com.njwd.entity.reportdata.vo.SaleStatisticsIncomeVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
* @Description: 销售情况统计表手机端
* @Author: LuoY
* @Date: 2020/3/4 11:04
*/
@Getter
@Setter
public class SaleStatisticsPhone {
    /**
    * 品牌id
    */
    private String brandId;
    /**
     * 品牌id
     */
    private String brandName;
    /**
     * 区域id
     */
    private String regionId;
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 区域名称
     */
    private String shopId;
    /**
     * 区域名称
     */
    private String shopName;

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
}

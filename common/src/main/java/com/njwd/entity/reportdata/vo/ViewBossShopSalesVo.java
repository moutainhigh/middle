package com.njwd.entity.reportdata.vo;

import com.njwd.common.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 店铺
 *
 * @author zhuzs
 * @date 2019-12-25 18:02
 */
@Data
@ApiModel
public class ViewBossShopSalesVo  implements Serializable {
    private static final long serialVersionUID = 6143086550908034570L;
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    /**
     * 门店地址：经度
     */
    @ApiModelProperty(value = "店铺地址：经度")
    private String shopLat;

    /**
     * 门店地址：纬度
     */
    @ApiModelProperty(value = "店铺地址：纬度")
    private String shopLon;

    /**
     * 门店地址
     */
    @ApiModelProperty(value = "门店地址")
    private String address;

    /**
     * 月份
     */
    @ApiModelProperty(value = "月份")
    private String showMonth;

    // ================ 营业概述 ================

    /**
     * 客流量
     */
    @ApiModelProperty(name="passengerFlow",value = "客流量")
    private BigDecimal passengerFlow = Constant.Number.ZEROB;

    /**
     * 销售额
     */
    @ApiModelProperty(name="consume",value = "销售额")
    private BigDecimal consume = Constant.Number.ZEROBXS;

    /**
     *  堂食人均消费
     */
    @ApiModelProperty(name="perCapita",value = " 堂食人均消费")
    private BigDecimal perCapita = Constant.Number.ZEROBXS;

    /**
     * 门店数量
     */
    @ApiModelProperty(name="shopSum",value = "门店数量")
    private Integer shopSum = Constant.Number.ZERO;
    /**
     * 堂食客流量
     */
    @ApiModelProperty(name="passengerFlowDine",value = "堂食客流量")
    private BigDecimal passengerFlowDine = Constant.Number.ZEROB;

    /**
     * 堂食销售额
     */
    @ApiModelProperty(name="consumeDine",value = "堂食销售额")
    private BigDecimal consumeDine = Constant.Number.ZEROBXS;
}


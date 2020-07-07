package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 看板（店长视角） 指标完成情况
 *
 * @author zhuzs
 * @date 2020-01-11 19:17
 */
@Data
@ApiModel
public class ViewManagerIndicatorRateVo implements Serializable {
    private static final long serialVersionUID = 222118213111357255L;
    /**
     * 品牌id
     */
    private String brandId;
    /**
     * 门店id
     */
    private String shopId;
    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 收入
     */
    private BigDecimal consume;

    /**
     * 客流量
     */
    private Integer passengerFlow;
    /**
     * 总客流量
     */
    private Integer passengerFlowSum;

    /**
     * 人均
     */
    private BigDecimal perCapita;

    /**
     * 开台数
     */
    private Integer deskCount;

    /**
     * 桌均
     */
    private BigDecimal perTableCapita;

    /**
     * 折扣额
     */
    private BigDecimal discountAmount;

    /**
     * 折扣率
     */
    private BigDecimal discountRate;

}


package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.ViewManager;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 看板（店长视角） 营业概述
 *
 * @author zhuzs
 * @date 2019-12-25 18:02
 */
@Data
@ApiModel
public class ViewManagerBusinessVo {
    private static final long serialVersionUID = 8293579388252687383L;

    /**
     * 销售额
     */
    @ApiModelProperty(name = "consume",value = "销售额")
    private ViewManager consume;

    /**
     * 收款额
     */
    @ApiModelProperty(name = "receivable",value = "收款额")
    private ViewManager receivable;

    /**
     * 赠送金额
     */
    @ApiModelProperty(name = "freeamount",value = "赠送金额")
    private ViewManager freeamount;

    /**
     * 订单量
     */
    @ApiModelProperty(name = "orderVolume",value = "订单量")
    private ViewManager orderVolume;

    /**
     * 客流量
     */
    @ApiModelProperty(name = "passengerFlow",value = "客流量")
    private ViewManager passengerFlow;

    /**
     * 人均消费
     */
    @ApiModelProperty(name = "perCapita",value = "人均消费")
    private ViewManager perCapita;

    /**
     * 开台数
     */
    @ApiModelProperty(name = "deskCount",value = "开台数")
    private ViewManager deskCount;

}


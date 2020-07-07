package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;

/**
 * 看板（店长视角） 线形图分析
 *
 * @author zhuzs
 * @date 2019-12-25 18:02
 */
@Data
@ApiModel()
public class ViewManagerLineGraphVo {
    private static final long serialVersionUID = 8293579388252687383L;
    /**
     * 销售额收款
     */
    private BigDecimal amount;
    /**
     * 收款
     */
    @ApiModelProperty(name = "consume",value="收款")
    private BigDecimal consume;

    /**
     * 订单量
     */
    @ApiModelProperty(name = "orderVolume",value="订单量")
    private Integer orderVolume;

    /**
     * 人均消费
     */
    @ApiModelProperty(name = "perCapita",value="人均消费")
    private BigDecimal perCapita;

    /**
     * 时间
     */
    @ApiModelProperty(name = "moment",value="时间")
    private String moment;

    /**
     * 用餐人数 合计
     */
    private Integer peopleSum;
    /*
    * 时间区间
    * */
    private String dateInterval;
}


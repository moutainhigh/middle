package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class IndexDataVo implements Serializable {

    @ApiModelProperty(value = "欠费金额")
    private BigDecimal owe_sum;//欠费金额

    @ApiModelProperty(value = "执行结果状态")
    private String status;//执行结果状态

    @ApiModelProperty(value = "订单个数")
    private Integer order_num;//订单个数

    @ApiModelProperty(value = "当前月份")
    private String month_date;//当前月份

    @ApiModelProperty(value = "可开票账单金额")
    private BigDecimal bill_sum;//可开票账单金额

    @ApiModelProperty(value = "客户ID")
    private String customer_id;//客户ID

    @ApiModelProperty(value = "当月账单个数")
    private Integer bill_num;//当月账单个数
}

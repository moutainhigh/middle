package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class BillSumVo implements Serializable {

    @ApiModelProperty(value = "返回状态")
    private String status;

    @ApiModelProperty(value = "可开票总金额")
    private BigDecimal bill_sum;
}

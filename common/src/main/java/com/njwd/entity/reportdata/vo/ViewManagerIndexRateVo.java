package com.njwd.entity.reportdata.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 看板（店长视角） 指标完成情况
 *
 * @author shenhf
 * @date 2020-04-11 19:17
 */
@Data
@ApiModel
public class ViewManagerIndexRateVo implements Serializable {
    private static final long serialVersionUID = 222118213111357255L;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 收入达标率
     */
    @ApiModelProperty(name="indexRate",value = "达标率")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal indexRate;
}


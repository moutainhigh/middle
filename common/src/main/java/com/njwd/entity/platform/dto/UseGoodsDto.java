package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UseGoodsDto implements Serializable {

    @ApiModelProperty(value = "下单系统名称,开通时调用")
    private String remark;// 下单备注

    @ApiModelProperty(value = "开通日期，开通时调用")
    private String open_date;// 开通日期

    @ApiModelProperty(value = "2020-05-01 停用日期 停用接口调用")
    private String end_date;//"2020-05-01" 停用日期

    @ApiModelProperty(value = "商品ID")
    private Long goods_id;// 商品ID
}

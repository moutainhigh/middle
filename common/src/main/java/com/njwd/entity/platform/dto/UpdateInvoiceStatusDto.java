package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateInvoiceStatusDto implements Serializable {

    @ApiModelProperty(value = "开票申请ID")
    private Long  id;//开票申请ID

    @ApiModelProperty(value = "开票状态0:开票中 1已开票")
    private Integer status;//开票状态0:开票中 1已开票
}

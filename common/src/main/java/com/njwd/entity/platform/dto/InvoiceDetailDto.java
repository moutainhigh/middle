package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceDetailDto implements Serializable {

    @ApiModelProperty(value = "开票申请id")
    private Long invoice_id;

}

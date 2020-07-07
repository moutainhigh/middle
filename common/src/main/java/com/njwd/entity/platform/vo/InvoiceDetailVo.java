package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceDetailVo implements Serializable {

    @ApiModelProperty(value = "返回状态")
    private String status;

    @ApiModelProperty(value = "发票所绑账单集合")
    List<InvoiceBillVo> lits_bill;

    private InvoiceVo invoice;
}

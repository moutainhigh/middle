package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceListDto implements Serializable {

    @ApiModelProperty(value = " 账单结束日期")
    private String end_date;// 账单结束日期

    @ApiModelProperty(value = " 发票抬头，注意格式 'aaa','bbbb'")
    private String invoice_title;// 发票抬头，注意格式 'aaa','bbbb'

    @ApiModelProperty(value = " 账单编码")
    private String code;// 账单编码

    @ApiModelProperty(value = " 账单开始日期")
    private String start_date;// 账单开始日期

    @ApiModelProperty(value = " 客户ID")
    private String customer_id;// 客户ID

    @ApiModelProperty(value = "页码")
    private Integer page;//页码

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;//每页条数

}

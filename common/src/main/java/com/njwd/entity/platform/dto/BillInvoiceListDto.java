package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class BillInvoiceListDto implements Serializable {

    @ApiModelProperty(value = "2020-03-31 账单结束日期")
    private String end_date;//2020-03-31 账单结束日期

    @ApiModelProperty(value = "2,3,4 多个商品ID")
    private String goods_id;//2,3,4 多个商品ID

    @ApiModelProperty(value = "账单编码")
    private String bill_code;//账单编码

    @ApiModelProperty(value = "账单开始日期")
    private String start_date;// 账单开始日期

    @ApiModelProperty(value = "产品分类ID")
    private String type_id;// 产品分类ID

    @ApiModelProperty(value = "客户ID")
    private String customer_id;// 客户ID
}

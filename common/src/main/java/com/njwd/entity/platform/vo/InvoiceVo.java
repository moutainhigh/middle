package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceVo implements Serializable {

    @ApiModelProperty(value = "开户行")
    private String bank_name;//开户行

    @ApiModelProperty(value = "状态：0开票中1已开票")
    private Integer status;//状态：0开票中1已开票

    @ApiModelProperty(value = "开票备注")
    private String remark;//开票备注

    @ApiModelProperty(value = "发票抬头")
    private String invoice_title;//发票抬头

    @ApiModelProperty(value = "客户名称")
    private String customer_name;//客户名称

    @ApiModelProperty(value = "发票总金额")
    private BigDecimal total_sum;//发票总金额

    @ApiModelProperty(value = "接收人")
    private String receive_name;//接收人

    @ApiModelProperty(value = "开票时间")
    private String invoice_time;//开票时间

    @ApiModelProperty(value = "发票编码")
    private String code;//发票编码

    @ApiModelProperty(value = "发票类型：0纸张1电子")
    private Integer invoice_style;//发票类型：0纸张1电子

    @ApiModelProperty(value = "发票类型法律类型 0增值税普通发票1增值税专用发票")
    private String invoice_type;//发票类型法律类型 0增值税普通发票1增值税专用发票

    @ApiModelProperty(value = "开票申请ID")
    private Integer id;//开票申请ID

    @ApiModelProperty(value = "税务登记证号")
    private String tax_regist_number;//税务登记证号

    @ApiModelProperty(value = "接收地址")
    private String address;//接收地址

    @ApiModelProperty(value = "邮编")
    private String postal_code;//邮编

    @ApiModelProperty(value = "申请时间")
    private String create_time;//申请时间

    @ApiModelProperty(value = "接收人电话")
    private String receive_mobile;//接收人电话

    @ApiModelProperty(value = "开户银行")
    private String bank_account;//开户银行

    @ApiModelProperty(value = "客户ID")
    private String customer_id;//客户ID
}

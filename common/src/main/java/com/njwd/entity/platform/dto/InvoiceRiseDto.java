package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceRiseDto implements Serializable {

     @ApiModelProperty(value = "发票抬头ID，新增时不传")
     private Long id;//发票抬头ID，新增时不传

     @ApiModelProperty(value = "开户行")
     private String bank_name;//开户行

     @ApiModelProperty(value = "税务登记证号")
     private String tax_regist_number;//税务登记证号

     @ApiModelProperty(value = "发票抬头")
     private String invoice_title;//发票抬头

     @ApiModelProperty(value = "客户名称")
     private String customer_name;//客户名称

     @ApiModelProperty(value = "开户行账号")
     private String bank_account;//开户行账号

     @ApiModelProperty(value = "客户ID")
     private String customer_id;//客户ID

     @ApiModelProperty(value = "发票类型 0增值税普通发票1增值税专用发票")
     private String invoice_type;//发票类型 0增值税普通发票1增值税专用发票
}

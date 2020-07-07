package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class FindListBillDto implements Serializable {

    @ApiModelProperty(value = "固定值，必传")
    private Long root_org_id;// 固定值，必传

    @ApiModelProperty(value = "2020-02 账单月份格式yyyy-MM")
    private String month_date;//"2020-02" 账单月份格式yyyy-MM

    @ApiModelProperty(value = "客户ID，必传")
    private String customer_id;//客户ID，必传

    @ApiModelProperty(value = "页码")
    private Integer page;//客户ID，必传

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;//客户ID，必传

    @ApiModelProperty(value = "每页条数")
    private Integer month_num;//客户ID，必传

}

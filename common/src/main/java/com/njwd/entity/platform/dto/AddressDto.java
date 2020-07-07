package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class AddressDto implements Serializable {

    @ApiModelProperty(value = "地址ID")
    private Long id;// 地址ID

    @ApiModelProperty(value = "地址")
    private String address;// 地址

    @ApiModelProperty(value = "邮编")
    private String postal_code;// 邮编

    @ApiModelProperty(value = "接收人")
    private String receive_name;// 接收人

    @ApiModelProperty(value = "客户名称")
    private String customer_name;// 客户名称

    @ApiModelProperty(value = "逻辑删除标志")
    private Integer is_del;

    @ApiModelProperty(value = "是否默认1.默认0非默认")
    private Integer is_default;// 是否默认

    @ApiModelProperty(value = "接收人电话")
    private String receive_mobile;// 接收人电话

    @ApiModelProperty(value = "客户ID")
    private String customer_id;// 客户ID
}

package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class FindPrepaidPayOnlineVo implements Serializable {

    @ApiModelProperty(value = "返回状态")
    private String status;

    @ApiModelProperty(value = "付款是否成功状态")
    private Boolean is_pay;

}

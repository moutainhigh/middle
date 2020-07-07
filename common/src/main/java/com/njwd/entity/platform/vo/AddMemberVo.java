package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AddMemberVo implements Serializable {

    @ApiModelProperty(value = "返回状态")
    private String status;

    @ApiModelProperty(value = "返回会员id")
    private String cardId;
}

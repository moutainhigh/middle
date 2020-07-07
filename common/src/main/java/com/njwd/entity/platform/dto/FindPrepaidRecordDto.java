package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class FindPrepaidRecordDto implements Serializable {

    /**
     * 注册公司会员id
     */
    @ApiModelProperty(value = "注册公司会员id")
    private String cardId;
}

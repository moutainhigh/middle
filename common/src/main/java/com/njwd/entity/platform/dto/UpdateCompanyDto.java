package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateCompanyDto implements Serializable {

    /**
     * 用户公司名称 用户公司名称
     */
    @ApiModelProperty(value = "用户公司名称")
    private String companyName;
}

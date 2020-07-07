package com.njwd.entity.platform.vo;

import com.njwd.entity.platform.dto.AlertsConfigDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AlertsConfigVo extends AlertsConfigDto {

    /**
     * 主键 默认自动递增
     */
    @ApiModelProperty(value = "主键")
    private String userId;

    /**
     * crm返回的识别用户的id crm返回的识别用户的id
     */
    private String crmUserId;

    /**
     * 用户账号(一般为手机号) 账号（一般为手机号）
     */
    @ApiModelProperty(value = "用户账号(一般为手机号)")
    private String mobile;

    /**
     * 用户公司名称 用户公司名称
     */
    @ApiModelProperty(value = "用户公司名称")
    private String companyName;
}

package com.njwd.entity.platform.vo;

import com.njwd.entity.platform.dto.RechargeDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RechargeVo extends RechargeDto {

    /**
     * 业务交易号
     */
    @ApiModelProperty(value = "业务交易号")
    private String payCode;
}

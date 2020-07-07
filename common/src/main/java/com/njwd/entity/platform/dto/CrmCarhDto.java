package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 提现调用CRM入参DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CrmCarhDto implements Serializable {

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long cardId;

    /**
     * 销售方公司id(这里固定为1)
     */
    @ApiModelProperty(value = "销售方公司id(这里固定为1)")
    private Long enterpriseId;

    /**
     * 销售方公司名字(这里固定为南京网兜信息科技有限公司)
     */
    @ApiModelProperty(value = "销售方公司id(这里固定为南京网兜信息科技有限公司)")
    private String enterpriseName;

    /**
     * 提现金额
     */
    @ApiModelProperty(value = "提现金额")
    private BigDecimal refundMoney;
}

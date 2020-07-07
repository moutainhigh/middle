package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExpireOrderGoodsReturnVo implements Serializable {

    @ApiModelProperty(value = "执行结果状态")
    private String status;// 执行结果状态

    @ApiModelProperty(value = "返回结果集")
    List<ExpireOrderGoodsVo> list_data;

    @ApiModelProperty(value = "预警天数")
    private Integer day_num;//预警天数

    @ApiModelProperty(value = "crm客户id")
    private String customer_id;//crm客户id
}

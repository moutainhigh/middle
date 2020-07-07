package com.njwd.entity.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @Description: 门店类型
* @Author: LuoY
* @Date: 2020/1/7 17:35
*/
@ApiModel(value = "门店类型")
@Data
public class BaseShopType {
    /**
     * 门店类别id
     */
    @ApiModelProperty(value = "门店类别id")
    private String shopTypeId;

    /**
     * 门店类别编码
     */
    @ApiModelProperty(value = "门店类别编码")
    private String shopTypeNo;

    /**
     * 门店类别名称
     */
    @ApiModelProperty(value = "门店类别名称")
    private String shopTypeName;

    /**
     * 企业id
     */
    @ApiModelProperty(value = "企业id")
    private String enteId;
}

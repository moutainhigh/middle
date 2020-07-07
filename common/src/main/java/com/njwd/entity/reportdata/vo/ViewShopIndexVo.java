package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 看板（店长视角）指标返回结果集
 *
 * @author shenhf
 * @date 2020-04-13 18:02
 */
@Data
public class ViewShopIndexVo implements Serializable {
    private static final long serialVersionUID = 8293579388252687383L;

    /**
     * 项目编码
     */
    @ApiModelProperty(name="name",value = "项目编码")
    private String name;

    /**
     * 结果集
     */
    @ApiModelProperty(name="shopIndexVo",value = "前五后五结果集")
    private ViewShopIndexFiveVo shopIndexVo;
}


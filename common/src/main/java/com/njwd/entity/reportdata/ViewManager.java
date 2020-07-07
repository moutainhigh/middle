package com.njwd.entity.reportdata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 看板（店长视角）
 *
 * @author zhuzs
 * @date 2019-12-25 18:02
 */
@Data
@ApiModel
public class ViewManager implements Serializable {
    private static final long serialVersionUID = 8731756562091494854L;

    /**
     * 字段名
     */
    @ApiModelProperty(name="columnName",value = "字段名")
    private String columnName;

    /**
     * 总量
     */
    @ApiModelProperty(name="total",value = "总量")
    private String total;

    /**
     * 同比
     */
    @ApiModelProperty(name="percent",value = "同比")
    private String percent;

    /**
     * 环比
     */
    @ApiModelProperty(name="ratio",value = "环比")
    private String ratio;
    /*
    * 排序用
    * */
    private String code;
}


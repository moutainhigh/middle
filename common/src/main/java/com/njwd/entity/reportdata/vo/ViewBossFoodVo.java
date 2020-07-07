package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 菜品
 *
 * @author zhuzs
 * @date 2020-01-10 14:16
 */
@Data
@ApiModel
public class ViewBossFoodVo implements Serializable {
    private static final long serialVersionUID = 448605648745780478L;

    // ================ 月度菜品销量分类分析 ================

    /**
     * 菜品分类名称
     */
    @ApiModelProperty(name="foodStyleName",value = "菜品分类名称")
    private String foodStyleName;

    /**
     * 菜品分类 实售合计
     */
    @ApiModelProperty(name="foodStyleConsume",value = "菜品分类 实售合计")
    private BigDecimal foodStyleConsume;
    /*
    * 品牌id
    * */
    private String brandName;
}


package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * wd_degree
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DegreeDto implements Serializable {
    /**
     * 自增主键
     */
    @ApiModelProperty(value = "自增主键")
    private Long degreeId;

    /**
     * 商品id
     */
    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    /**
     * 商品名
     */
    @ApiModelProperty(value = "商品名")
    private String goodsName;

    /**
     * 点击量
     */
    @ApiModelProperty(value = "点击量")
    private Long clicks;


}
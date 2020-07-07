package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * wd_evaluate
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EvaluateDto implements Serializable {
    /**
     * 自增主键
     */
    @ApiModelProperty(value = "自增主键")
    private Long evaluateId;

    /**
     * 评价人id
     */
    @ApiModelProperty(value = "评价人id")
    private String userId;

    /**
     * 被评价的商品ID
     */
    @ApiModelProperty(value = "被评价的商品ID")
    private Long goodsId;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * 评价分数
     */
    @ApiModelProperty(value = "评价分数")
    private Double score;

    /**
     * 评价内容
     */
    @ApiModelProperty(value = "评价内容")
    private String remark;

    /**
     * 条目创建时间
     */
    @ApiModelProperty(value = "条目创建时间")
    private Date createTime;

    /**
     * 条目修改时间
     */
    @ApiModelProperty(value = "条目修改时间")
    private Date updateTime;

    /**
     * 该人对该产品的关注度
     */
    @ApiModelProperty(value = "该人对该产品的关注度")
    private Long degree;

    /**
     * 逻辑删除标志0：未删除 1、已删除
     */
    @ApiModelProperty(value = "逻辑删除标志0：未删除 1、已删除")
    private Integer isDel;

}
package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsVO implements Serializable {

    @ApiModelProperty(value = "商品图片地址")
    private String imgUrl;// 商品图片地址

    @ApiModelProperty(value = "")
    private Long updatorId;

    @ApiModelProperty(value = "")
    private Long imgId;

    @ApiModelProperty(value = "备注")
    private String remark;// 备注

    @ApiModelProperty(value = "试用天数")
    private Integer probationDay;	// 试用天数

    @ApiModelProperty(value = "渠道编码")
    private String  channelCode;// 渠道编码

    @ApiModelProperty(value = "商品ID")
    private String  id;// 商品ID

    @ApiModelProperty(value = "")
    private Integer rootOrgId;

    @ApiModelProperty(value = "支付方式0后付费1预付费")
    private Integer payType;// 支付方式0后付费1预付费

    @ApiModelProperty(value = "商品名称")
    private String  name;// 名称

    @ApiModelProperty(value = "单位")
    private String  priceUnit;	// 单位

    @ApiModelProperty(value = "渠道名称")
    private String  channelName;//渠道名称

    @ApiModelProperty(value = "状态")
    private Integer status;// 状态

    @ApiModelProperty(value = "计价参数")
    private String priceParameter;// 计价参数

    @ApiModelProperty(value = "编码")
    private String code;// 编码

    @ApiModelProperty(value = "商品分类ID")
    private Long typeId;// 商品分类ID

    @ApiModelProperty(value = "渠道ID")
    private Long channelId;// 渠道ID

    @ApiModelProperty(value = "")
    private Long creatorId;

    @ApiModelProperty(value = "")
    private String updatorName;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;// 价格

    @ApiModelProperty(value = "")
    private GoodsCreateTimeVo updateTime;

    @ApiModelProperty(value = "商品分类名称")
    private String typeName;// 商品分类名称

    @ApiModelProperty(value = "创建时间")
    private GoodsCreateTimeVo createTime;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;// 是否删除

    @ApiModelProperty(value = "系统管理员")
    private String  creatorName;//系统管理员
}

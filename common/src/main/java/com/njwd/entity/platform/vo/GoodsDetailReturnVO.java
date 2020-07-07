package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsDetailReturnVO implements Serializable{

    @ApiModelProperty(value = "执行结果状态")
    private String status;// 执行结果状态

    @ApiModelProperty(value = "结果集")
    private GoodsDetailVO goods;//结果集



}

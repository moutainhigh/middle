package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderReturnVo implements Serializable {

    @ApiModelProperty(value = "开始条数")
    private Integer fromNum;//开始条数

    @ApiModelProperty(value = "页码")
    private Integer page;//页码

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;//每页条数

    @ApiModelProperty(value = "结束条数")
    private Integer endNum;//结束条数

    @ApiModelProperty(value = "总条数")
    private Integer infoCount;//总条数

    @ApiModelProperty(value = "订单集合")
    private List<OrderVo> listData;

    @ApiModelProperty(value = "总页数")
    private Integer pageNum;//总页数
}

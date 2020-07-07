package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 商品开通出参
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsOpenListVo implements Serializable {

    @ApiModelProperty(value = "执行结果状态success/error/fail，非success另有返回值msg")
    private String status;// 执行结果状态success/error/fail，非success另有返回值msg

    @ApiModelProperty(value = "已开通的结果集")
    private List<GoodsOpenVo> listExist;

    @ApiModelProperty(value = "本次订单编号")
    private String orderCode;// 本次订单编号
}

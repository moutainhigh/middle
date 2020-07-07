package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 商品试用出参
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsProbationListVo implements Serializable {

    @ApiModelProperty(value = "执行结果状态success/error/fail，非success另有返回值msg")
    private String status;// 执行结果状态success/error/fail，非success另有返回值msg

    @ApiModelProperty(value = "已开通的商品集合")
    private  List<GoodsProbationVo> listExist;

    @ApiModelProperty(value = "本次订单编号")
    private String orderCode;// 本次订单编号
}

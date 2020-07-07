package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 最简单的返回出参
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SimpleReturnVo<T> implements Serializable {

    @ApiModelProperty(value = "返回状态")
    private String status;

    private List<T> listData;
}

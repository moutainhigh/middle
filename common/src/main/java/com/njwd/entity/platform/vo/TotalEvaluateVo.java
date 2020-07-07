package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 评价总计
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TotalEvaluateVo implements Serializable {

    @ApiModelProperty(value = "关注度，总点击量")
    private Long sumClicks;

    @ApiModelProperty(value = "平均分，保留两位小数")
    private Double avgScore;
}

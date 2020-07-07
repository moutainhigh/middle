package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsCreateTimeVo implements Serializable {

    private Integer nanos;
    @ApiModelProperty(value = "创建时间的时间戳")
    private Date time;

    private Integer minutes;

    private Integer seconds;

    private Integer hours;

    private Integer month;

    private Integer timezoneOffset;

    private Integer year;

    private Integer day;

    private Integer date;

}

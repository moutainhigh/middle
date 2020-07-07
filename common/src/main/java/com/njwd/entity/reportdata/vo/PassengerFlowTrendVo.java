package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Time;
import java.util.List;

/**
 * （老板视角）客流量趋势
 *
 * @author zhuzs
 * @date 2020-01-08 11:29
 */
@Data
@ApiModel()
public class PassengerFlowTrendVo {

    /**
     * 客流量
     */
    @ApiModelProperty(name = "peopleSum", value = "总客流量")
    private Integer peopleSum;

    /**
     * 堂食
     */
    @ApiModelProperty(name = "dinePeople", value = "堂食客流数")
    private Integer dinePeople;

    /**
     * 外卖
     */
    @ApiModelProperty(name = "takeAwayPeople", value = "外卖客流数")
    private Integer takeAwayPeople;

    /**
     * 自取
     */
    @ApiModelProperty(name = "tirePeople", value = "自取客流数")
    private Integer tirePeople;
    /**
     * 时间
     */
    @ApiModelProperty(name = "moment", value = "时间")
    protected String moment;

}


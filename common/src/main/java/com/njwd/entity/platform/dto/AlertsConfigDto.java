package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * wd_alerts_config
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AlertsConfigDto implements Serializable {
    /**
     * 主键 默认自动递增
     */
    @ApiModelProperty(value = "主键 默认自动递增")
    private Long alertsConfigId;

    /**
     * 被预警的用户id
     */
    @ApiModelProperty(value = "被预警的用户id")
    private String userId;

    /**
     * 预警类型 1：可用额度预警 2：提前续费预警
     */
    @ApiModelProperty(value = "预警类型 1：可用额度预警 2：提前续费预警")
    private Integer alertsType;

    /**
     * 预警阀值
     */
    @ApiModelProperty(value = "预警阀值")
    private BigDecimal warningThreshold;

    /**
     * 预警开启状态 0：未开启 1：已开启
     */
    @ApiModelProperty(value = "预警开启状态 0：未开启 1：已开启")
    private Integer status;

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
     * 逻辑删除标志位 0：未删除 1：已删除
     */
    @ApiModelProperty(value = "逻辑删除标志位 0：未删除 1：已删除")
    private Boolean isDel;

}
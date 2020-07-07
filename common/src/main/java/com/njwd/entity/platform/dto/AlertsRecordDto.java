package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * wd_alerts_record
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AlertsRecordDto implements Serializable {
    /**
     * 主键 默认自动递增
     */
    @ApiModelProperty(value = "主键")
    private Long alertsRecordId;

    /**
     * 被预警用户id
     */
    @ApiModelProperty(value = "被预警用户id")
    private String userId;

    /**
     * 被预警的产品id
     */
    @ApiModelProperty(value = "被预警的产品id")
    private Long goodsId;

    /**
     * 被预警的产品名
     */
    @ApiModelProperty(value = "被预警的产品名")
    private String goodsName;

    /**
     * 预警内容
     */
    @ApiModelProperty(value = "预警内容")
    private String content;

    /**
     * 条目创建时间
     */
    @ApiModelProperty(value = "条目修改时间")
    private Date createTime;

    /**
     * 预警类型 1：可用额度预警 2：提前续费预警
     */
    @ApiModelProperty(value = "预警类型 1：可用额度预警 2：提前续费预警")
    private Integer alertsType;

    /**
     * 逻辑删除标志
     */
    @ApiModelProperty(value = "逻辑删除标志")
    private Date isDel;

}
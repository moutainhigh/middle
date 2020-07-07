package com.njwd.entity.reportdata;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 同比环比阀值
 * @Author zhc
 * @Date 2020/1/11 14:10
 */
@Data
public class BaseYearMomThreshold implements Serializable {

    private static final long serialVersionUID = -2697188085355238860L;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 表含义
     */
    private String tableName;

    /**
     * 同比预警阀值
     */
    private BigDecimal yearThreshold;

    /**
     * 环比预警阀值
     */
    private BigDecimal momThreshold;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 表名
     */
    private String tableCode;

}

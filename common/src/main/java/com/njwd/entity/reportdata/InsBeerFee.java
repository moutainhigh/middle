package com.njwd.entity.reportdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.utils.DateUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 啤酒进场费
 * @Author jds
 * @Date 2019/12/3 14:10
 */
@Data
public class InsBeerFee implements Serializable {

    private static final long serialVersionUID = 42L;
    /**
     * 主键id
     */
    private String beerFeeId;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 供应商id
     */
    private String supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     *进场费返还
     */
    private BigDecimal fee;

    /**
     * 开始有效期
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date beginDate;

    /**
     * 结束有效期
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date endDate;

    /**
     * 状态 0不可用，1可用
     */
    private Byte status;

    /**
     * 最后修改时间
     */
    private Date updateTime;

}

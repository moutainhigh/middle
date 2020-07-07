package com.njwd.entity.reportdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.utils.DateUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 退赠优免安全阀值
 * @Author jds
 * @Date 2019/12/3 14:10
 */
@Data
public class DiscountsSafe implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * 主键id
     */
    private String discountsSafeId;

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
     * 菜品id
     */
    private String foodId;

    /**
     * 菜品编码
     */
    private String foodNo;

    /**
     * 菜品名称
     */
    private String foodName;

    /**
     * 单位id
     */
    private String unitId;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     *退菜数量阀值
     */
    private BigDecimal num;

    /**
     * 状态 0不可用，1可用
     */
    private Byte status;

    /**
     * 最后修改时间
     */
    private Date updateTime;
}

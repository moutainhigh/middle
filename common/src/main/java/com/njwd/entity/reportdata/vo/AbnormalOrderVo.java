package com.njwd.entity.reportdata.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author lj
 * @Description 异常账单统计表
 * @Date:16:42 2020/3/26
 **/
@Getter
@Setter
public class AbnormalOrderVo {
    /**
     * 类型
     */
    private String type;
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
     * 门店ids
     */
    private List<String> shopIdList;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 总账单数
     */
    private BigDecimal orderCount;

    /**
     * 异常账单数
     */
    private BigDecimal abnormalCount;

    /**
     * 异常账单占比
     */
    private BigDecimal abnormalRatio;
}

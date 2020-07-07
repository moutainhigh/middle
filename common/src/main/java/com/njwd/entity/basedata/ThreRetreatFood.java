package com.njwd.entity.basedata;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class ThreRetreatFood {
    /**
    * 主键ID
    */
    private String discountsSafeId;

    /**
    * 企业ID
    */
    private String enteId;

    /**
    * 品牌ID
    */
    private String brandId;

    /**
    * 品牌名称
    */
    private String brandName;

    /**
    * 区域ID
    */
    private String regionId;

    /**
    * 区域名称
    */
    private String regionName;

    /**
    * 门店ID
    */
    private String shopId;

    /**
    * 门店名称
    */
    private String shopName;

    /**
    * 菜品ID
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
    * 单位ID
    */
    private String unitId;

    /**
    * 单位名称
    */
    private String unitName;

    /**
    * 退菜数量阀值
    */
    private BigDecimal num;

    /**
    * 状态 0不可用，1可用
    */
    private BigDecimal status;

    /**
    * 开始时间
    */
    private Date beginDate;

    /**
    * 结束时间
    */
    private Date endDate;

    /**
    * 最后修改时间
    */
    private Date updateTime;
}
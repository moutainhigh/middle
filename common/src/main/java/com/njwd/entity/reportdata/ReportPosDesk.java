package com.njwd.entity.reportdata;

import jnr.ffi.annotations.In;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/12/5
 */
@Data
public class ReportPosDesk implements Serializable {
    private static final long serialVersionUID = -6697341948036623929L;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 门店类型
     */
    private String shopTypeId;

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
     * 台位id
     */
    private String deskId;

    /**
     * 台位编码
     */
    private String deskNo;

    /**
     * 桌台区域id
     */
    private String deskAreaId;

    /**
     * 桌台区域名称
     */
    private String deskAreaName;

    /**
     * 台位类型id
     */
    private String deskTypeId;

    /**
     * 台位类型名称
     */
    private String deskTypeName;

    /**
     * 台位区域类型编码
     */
    private String deskAreaTypeNo;

    /**
     * 台位区域类型名称
     */
    private String deskAreaTypeName;

    /**
     * 餐别id
     */
    private String mealId;

    /**
     * 餐别名称
     */
    private String mealName;

    /**
     * 订单合计金额
     */
    private BigDecimal amount;

    /**
     * 客流数
     */
    private Integer clientCount;

    /**
     * 开台数
     */
    private BigDecimal deskCount;

    /**
     * 开桌渠道id
     */
    private String channelId;

    /**
     * 账单日期
     */
    private Date accountDate;

    /**
     * 集团id
     */
    private String enteId;
}

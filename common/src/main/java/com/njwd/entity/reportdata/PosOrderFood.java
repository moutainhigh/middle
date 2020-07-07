package com.njwd.entity.reportdata;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 订单菜品分析类
 * @Author LuoY
 * @Date 2019/11/18
 */
@Data
public class PosOrderFood implements Serializable {

    private static final long serialVersionUID = 3196537579308531050L;
    /**
     * 应用id
     */
    private String appId;

    /**
     * 订单菜品id
     */
    private String orderFoodId;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 集团id
     */
    private String enteId;

    /**
     * 菜品id
     */
    private String foodId;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 账单id
     */
    private String cashId;

    /**
     * 菜品数量
     */
    private BigDecimal foodNum;

    /**
     * 菜品原价
     */
    private BigDecimal originalPrice;

    /**
     * 优惠前价格
     */
    private BigDecimal moneyReceivableprice;

    /**
     * 实收
     */
    private BigDecimal moneyRealprice;

    /**
     * 单位id
     */
    private String unitId;

    /**
     * 是否是套餐标志
     */
    private String isPackage;

    /**
     * 订单日期
     */
    private Object accountDate;

    /**
     * 点单时间
     */
    private Date orderTime;

    /**
     * 点单人id
     */
    private String orderUserId;

    /**
     * 单品备注
     */
    private String remark;

    /**
     * 是否退菜 0不是  1是
     */
    private int isRetreat;

    /**
     * 退菜备注
     */
    private String retreatRemark;

    /**
     * 退菜数量
     */
    private double retreatCount;

    /**
     * 退菜人id
     */
    private String retreatUserId;

    /**
     * 退菜时间
     */
    private Date retreatTime;

    /**
     * 是否赠菜 0不是  1是
     */
    private int isGrive;

    /**
     * 赠菜备注
     */
    private String griveRemark;

    /**
     * 赠送数量
     */
    private double griveCount;

    /**
     * 赠菜人id
     */
    private String griveUserId;

    /**
     * 赠菜时间
     */
    private Date griveTime;
}

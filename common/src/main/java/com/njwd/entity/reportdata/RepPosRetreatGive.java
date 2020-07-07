package com.njwd.entity.reportdata;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
/**
* @Description: 退菜赠送
* @Author: LuoY
* @Date: 2020/3/18 11:21
*/
@Setter
@Getter
public class RepPosRetreatGive {
    /**
     * 订单菜品id
     */
    private String orderFoodId;

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
     * 单位id
     */
    private String unitId;

    /**
     * 订单日期
     */
    private Date accountDate;

    /**
     * 点单时间
     */
    private Date orderTime;

    /**
     * 操作人id
     */
    private String orderUserId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 门店类型
     */
    private String shopTypeId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 退菜总价
     */
    private BigDecimal sumPrice;

    /**
     * 退菜备注
     */
    private String retreatRemark;

    /**
     * 退菜时间
     */
    private Date retreatTime;

    /**
     * 菜品编码
     */
    private String foodNo;

    /**
     * 菜品或套餐名
     */
    private String foodName;

    /**
     * 类别id
     */
    private String foodStyleId;

    /**
     * 类别名称
     */
    private String foodStyleName;

    /**
     * 桌位编号
     */
    private String deskNo;

    /**
     * 单位名称
     */
    private String unitName;
}


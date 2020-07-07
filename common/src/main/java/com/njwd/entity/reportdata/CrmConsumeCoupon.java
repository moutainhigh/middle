package com.njwd.entity.reportdata;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CrmConsumeCoupon {
    /**
    * 编号
    */
    private String consumeCouponId;

    /**
    * 消费ID
    */
    private String consumeId;

    /**
    * 券明细ID
    */
    private String couponId;

    /**
    * 券明细名称
    */
    private String couponName;

    /**
    * 会员ID
    */
    private String memberId;

    /**
    * 会员卡ID
    */
    private String cardId;

    /**
    * 会员号
    */
    private String cardNo;

    /**
    * 使用时间
    */
    private String useTime;

    /**
    * 单券金额
    */
    private BigDecimal discountMoney;

    /**
    * 数量
    */
    private Integer num;

    /**
    * 券总金额
    */
    private BigDecimal totalDiscountMoney;

    private String enteId;

    private String appId;

    /**
    * 门店id
    */
    private String shopId;
}
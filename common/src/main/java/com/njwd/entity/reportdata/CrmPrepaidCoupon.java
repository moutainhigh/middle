package com.njwd.entity.reportdata;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class CrmPrepaidCoupon {
    /**
    * 充值赠送券记录
    */
    private String prepaidCouponId;

    /**
    * 消费ID
    */
    private String prepaidId;

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
    private Date giveTime;

    /**
    * 单券金额
    */
    private BigDecimal money;

    /**
    * 数量
    */
    private Integer num;

    /**
    * 券总金额
    */
    private BigDecimal totalMoney;

    /**
    * 赠券门店id
    */
    private String shopId;

    /**
    * 应用id
    */
    private String appId;

    /**
    * 集团id
    */
    private String enteId;
}
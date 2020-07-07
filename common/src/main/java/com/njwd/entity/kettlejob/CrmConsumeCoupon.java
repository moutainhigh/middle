package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @Description 会员消费使用的优惠券
 * @create 2019/11/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmConsumeCoupon extends BaseModel {

    /**
     * 消费券记录id
     */
    private String consumeCouponId;
    /**
     * 对应的消费记录id
     */
    private String consumeId;
    /**
     * 消费使用的券id
     */
    private String couponId;
    /**
     * 消费使用的券名称
     */
    private String couponName;
    /**
     * 会员id
     */
    private String memberId;
    /**
     * 会员卡id
     */
    private String cardId;
    /**
     * 会员卡号
     */
    private String cardNo;
    /**
     * 使用时间
     */
    private String useTime;
    /**
     * 每张优惠券优惠的金额
     */
    private Double discountMoney;
    /**
     * 使用张数
     */
    private Integer num;
    /**
     * 总优惠金额
     */
    private Double totalDiscountMoney;
    /**
     * 集团id
     */
    private String enteId;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 第三方门店id
     */
    private String thirdShopId;
    /**
     * 门店id
     */
    private String shopId;

}

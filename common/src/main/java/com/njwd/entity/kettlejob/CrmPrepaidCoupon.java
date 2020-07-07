package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author ljc
* @Description 会员赠送券记录
* @create 2019/11/30
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmPrepaidCoupon extends BaseModel {

    /**
     * 会员赠送券记录id
     */
    private String prepaidCouponId;
    /**
     * 对应的消费记录id
     */
    private String prepaidId;
    /**
     * 储值赠送的券id
     */
    private String couponId;
    /**
     * 储值赠送的券名称
     */
    private String couponName;
    /**
     * 会员id
     */
    private String memberId;
    /**
     * 会员卡号id
     */
    private String cardId;
    /**
     * 会员卡号
     */
    private String cardNo;
    /**
     * 赠送时间
     */
    private String giveTime;
    /**
     * 每张优惠券的金额
     */
    private Double money;
    /**
     * 赠送张数
     */
    private Integer num;
    /**
     * 总金额
     */
    private Double totalMoney;
    /**
     * 门店id
     */
    private String shopId;
    /**
     * 第三方门店id
     */
    private String thirdShopId;
    /**
     *  应用id
     */
    private String appId;
    /**
     * 集团id
     */
    private String enteId;

}

package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @Description 会员储值支付明细
 * @create 2019/11/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmPrepaidPayType extends BaseModel {
    /**
     * 支付方式
     */
    private  String prepaidPayTypeId;
    /**
     * 充值记录id
     */
    private  String prepaidId;
    /**
     * 支付金额
     */
    private Double money;
    /**
     * 支付类型id
     */
    private String payTypeId;
    /**
     * 第三方支付类型
     */
    private String thirdPayTypeId;
    /**
     *  门店id
     */
    private String shopId;
    /**
     * 第三方门店id
     */
    private String thirdShopId;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 集团id
     */
    private String enteId;

    /**
     * 消费时间
     */
    private String prepaidTime;
}

package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 账单支付信息
 * @Author LuoY
 * @Date 2019/11/22
 */
@Data
public class PosCashPay {
    /**
     * 账单支付Id
     **/
    private String cashPayId;

    /**
     * 应用Id
     **/
    private String appId;

    /**
     * 结账单Id
     **/
    private String cashId;

    /**
     * 订单Id
     **/
    private String orderId;

    /**
     * 门店Id
     **/
    private String shopId;

    /**
     * 支付类型Id
     **/
    private String payTypeId;

    /**
     * 企业Id
     **/
    private String enteId;

    /**
     * 支付类型流水号
     **/
    private String cashPaySerialno;

    /**
     * 账单日期
     **/
    private String accountDate;

    /**
     * 团购券Id
     **/
    private String couponId;

    /**
     * 团购券名称
     **/
    private String couponName;

    /**
     * 支付总金额
     **/
    private BigDecimal money;

    /**
     * 实际收款金额
     **/
    private String moneyActual;
}

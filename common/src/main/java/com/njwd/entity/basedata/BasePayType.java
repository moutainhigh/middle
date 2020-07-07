package com.njwd.entity.basedata;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 支付方式
 * @Author LuoY
 * @Date 2019/12/4
 */
@Data
public class BasePayType implements Serializable {
    private static final long serialVersionUID = -3926491147652201147L;
    /**
     * 支付id
     */
    private String payTypeId;

    /**
     * 支付类型id
     */
    private String payCategoryId;

    /**
     * 支付编码
     */
    private String payTypeCode;

    /**
     * 支付名称
     */
    private String payTypeName;

    /**
     * 实收金额
     */
    private String moneyActual;

    /**
     * 应付金额
     */
    private String money;

    /**
     * 企业id
     */
    private String enteId;
}

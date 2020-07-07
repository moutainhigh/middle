package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ljc
 * @Description 会员消费充值统计报表
 * @create 2019/11/26
 */
@Data
public class MemberPrepaidConsume{

    /**
     * 支付方式名称
     */
    private String payTypeName;
    /**
     * 支付笔数
     */
    private Integer payCount;
    /**
     * 支付金额
     */
    private BigDecimal payMoney;
}

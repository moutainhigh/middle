package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 会员充值消费报表
 * @Author ljc
 * @Date 2019/11/27
 */
@Data
public class RepCrmTurnover {
    /**
     *  企业Id
     */
    private String enteId;
    /**
     *  门店id
     */
    private String shopId;
    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 门店类型id
     */
    private String shopTypeId;
    /**
     * 门店类型名称
     */
    private String shopTypeName;
    /**
     * 区域id
     */
    private String regionId;
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 品牌id
     */
    private String brandId;
    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 总充值金额（充值实收+返现）
     */
    private BigDecimal totalPrepaidMoney;
    /**
     * 充值实收
     */
    private BigDecimal prepaidMoney;
    /**
     * 充值返现
     */
    private BigDecimal largessMoney;
    /**
     * 总消费金额
     */
    private BigDecimal  totalConsumeMoney;
    /**
     * 使用储值金额
     */
    private BigDecimal consumeMoney;
    /**
     * 使用实收预存额(元)
     */
    private BigDecimal consumePrepaidMoney;
    /**
     * 使用奖励预存额(元)
     */
    private BigDecimal consumeLargessMoney;
    /**
     * 会员消费金额
     */
    private BigDecimal memberConsumeMoney;
    /**
     * 营业收入
     */
    private BigDecimal actualMoney;

    /**
     * 撤销充值总金额
     */
    private BigDecimal  revokeTotalPrepaidMoney;
    /**
     * 撤销充值实收金额
     */
    private BigDecimal revokePrepaidMoney;
    /**
     * 撤销充值返现金额
     */
    private BigDecimal revokeLargessMoney;
    /**
     * 撤销消费总金额
     */
    private BigDecimal revokeTotalConsumeMoney;
    /**
     * 撤销消费储值金额
     */
    private BigDecimal revokeConsumeMoney;
    /**
     * 撤销消费储值实收金额
     */
    private BigDecimal revokeConsumePrepaidMoney;
    /**
     * 撤销消费储值返现金额
     */
    private BigDecimal revokeConsumeLargessMoney;
    /**
     * 日期(yyyy-MM-dd)
     */
    private String accountDate;

    /**
     * 支付方式id
     */
    private String payTypeId;
    /**
     * 支付方式名称
     */
    private String payTypeName;
    /**
     * 消费笔数
     */
    private Integer consumeNum;
    /**
     * 储值笔数
     */
    private Integer prepaidNum;
    /**
     * 撤销消费笔数
     */
    private Integer revokeConsumeNum;
    /**
     * 撤销储值笔数
     */
    private Integer revokePrepaidNum;
    /**
     * 消费实收金额
     */
    private BigDecimal consumeActualMoney;

    /**
     * 撤销消费实收金额
     */
    private BigDecimal revokeConsumeActualMoney;
}

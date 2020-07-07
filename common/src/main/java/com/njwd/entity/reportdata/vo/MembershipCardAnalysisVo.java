package com.njwd.entity.reportdata.vo;

import com.njwd.common.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/11 10:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MembershipCardAnalysisVo extends BaseScopeOfQueryType implements Serializable {

    private static final long serialVersionUID = 3425356103255207965L;
    /**
     * 会员卡 期初数量
     */
    private BigDecimal initialNumber = Constant.Number.ZEROB;
    /**
     * 会员卡 本期新增
     */
    private BigDecimal currentAddNumber = Constant.Number.ZEROB;
    /**
     * 会员卡 期末数量
     */
    private BigDecimal finalNumber = Constant.Number.ZEROB;
    /**
     * 会员卡金额 期初
     */
    private BigDecimal initialAmount = Constant.Number.ZEROB;
    /**
     * 会员卡金额 期初实收
     */
    private BigDecimal initialReceiptsAmount = Constant.Number.ZEROB;
    /**
     * 会员卡金额 本期充值
     */
    private BigDecimal currentRechargeAmount = Constant.Number.ZEROB;
    /**
     * 会员卡金额 本期充值实收
     */
    private BigDecimal currentRechargeReceipts = Constant.Number.ZEROB;
    /**
     * 会员卡金额 本期消费
     */
    private BigDecimal currentConsumptionAmount = Constant.Number.ZEROB;
    /**
     * 会员卡金额 本期消费实收
     */
    private BigDecimal currentConsumptionReceipts = Constant.Number.ZEROB;
    /**
     * 会员卡金额 余额
     */
    private BigDecimal cardBalance = Constant.Number.ZEROB;
    /**
     * 会员卡金额 实收余额
     */
    private BigDecimal cardBalanceReceipts = Constant.Number.ZEROB;
    /**
     * 平均金额 余额
     */
    private BigDecimal averageBalance = Constant.Number.ZEROB;
    /**
     * 平均金额 实收余额
     */
    private BigDecimal averageBalanceReceipts = Constant.Number.ZEROB;
}

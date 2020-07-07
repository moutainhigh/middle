package com.njwd.entity.reportdata;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author lj
 * @Description 科目余额表
 * @Date:15:29 2020/1/10
 **/
@Getter
@Setter
@TableName(value = "FIN_BALANCE_SUBJECT")
public class FinBalanceSubject implements Serializable {
    /**
    * 应用id
    */
    @TableField(value = "APP_ID")
    private String appId;

    /**
     * 企业id
     */
    @TableField(value = "ENTE_ID")
    private String enteId;

    /**
     * 核算账簿id
     */
    @TableField(value = "ACCOUNT_BOOK_ID")
    private String accountBookId;

    /**
     * 核算主体id
     */
    @TableField(value = "ACCOUNT_ENTITY_ID")
    private String accountEntityId;

    /**
     * 科目id
     */
    @TableField(value = "ACCOUNT_SUBJECT_ID")
    private String accountSubjectId;

    /**
     * 币种id
     */
    @TableField(value = "CURRENCY_ID")
    private String currencyId;

    /**
     * 期间年
     */
    @TableField(value = "PERIOD_YEAR")
    private Integer periodYear;

    /**
     * 期间号
     */
    @TableField(value = "PERIOD_NUM")
    private Integer periodNum;

    /**
     * 期间年号
     */
    @TableField(value = "PERIOD_YEAR_NUM")
    private Integer periodYearNum;

    /**
     * 期初
     */
    @TableField(value = "OPEN_BALANCE")
    private BigDecimal openBalance;

    /**
     * 期初原币
     */
    @TableField(value = "OPEN_BALANCE_FOR")
    private BigDecimal openBalanceFor;

    /**
     * 期末
     */
    @TableField(value = "CLOSE_BALANCE")
    private BigDecimal closeBalance;

    /**
     * 期末原币
     */
    @TableField(value = "CLOSE_BALANCE_FOR")
    private BigDecimal closeBalanceFor;

    /**
     * 借方金额
     */
    @TableField(value = "DEBIT_AMOUNT")
    private BigDecimal debitAmount;

    /**
     * 借方金额原币
     */
    @TableField(value = "DEBIT_AMOUNT_FOR")
    private BigDecimal debitAmountFor;

    /**
     * 贷方金额
     */
    @TableField(value = "CREDIT_AMOUNT")
    private BigDecimal creditAmount;

    /**
     * 贷方金额原币
     */
    @TableField(value = "CREDIT_AMOUNT_FOR")
    private BigDecimal creditAmountFor;

    /**
     * 借方本年累计
     */
    @TableField(value = "TOTAL_DEBIT_AMOUNT")
    private BigDecimal totalDebitAmount;

    /**
     * 借方本年累计原币
     */
    @TableField(value = "TOTAL_DEBIT_AMOUNT_FOR")
    private BigDecimal totalDebitAmountFor;

    /**
     * 贷方本年累计
     */
    @TableField(value = "TOTAL_CREDIT_AMOUNT")
    private BigDecimal totalCreditAmount;

    /**
     * 贷方本年累计原币
     */
    @TableField(value = "TOTAL_CREDIT_AMOUNT_FOR")
    private BigDecimal totalCreditAmountFor;

    /**
     * 辅助核算id
     */
    @TableField(value = "AUXILIARY_ID")
    private String auxiliaryId;

    private static final long serialVersionUID = -5260334056259727683L;
}
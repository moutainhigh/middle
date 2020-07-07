package com.njwd.entity.reportdata;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author lj
 * @Description 现金流量余额
 * @Date:16:02 2020/2/10
 **/
@Getter
@Setter
public class FinBalanceCashFlow implements Serializable {

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
     * 现金流量项目id
     */
    @TableField(value = "ITEM_ID")
    private String itemId;

    /**
     * 期间年号
     */
    @TableField(value = "PERIOD_YEAR_NUM")
    private Integer periodYearNum;

    /**
     * 本期金额
     */
    private BigDecimal occurAmount;

    private static final long serialVersionUID = -5260334056259728683L;
}

package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.RepPosDetailPay;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author jds
 * @Description 收入折扣分析表
 * @create 2019/12/7 14:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RepPosDetailPayVo extends RepPosDetailPay {

    /**
     * 本期
     */
    private BigDecimal currentMoney;

    /**
     * 上期
     */
    private BigDecimal prior;

    /**
     * 去年同期
     */
    private BigDecimal lastYear;

    /**
     * 占比
     */
    private BigDecimal proportion;


    /**
     * 环比
     */
    private BigDecimal linkRatio;

    /**
     * 同比
     */
    private BigDecimal overYear;

    /**
     * 实收合计金额
     */
    private BigDecimal moneyActualSum;

    /**
     * 合计原金额
     */
    private BigDecimal moneySum;
}

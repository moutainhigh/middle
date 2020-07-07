package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.ReportPosDesk;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description: ReportPosDeskVo
 * @Author LuoY
 * @Date 2019/12/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportPosDeskVo extends ReportPosDesk {
    /**
     * 总桌数
     */
    private Integer deskAllCount;
    /**
     * 订单合计金额
     */
    private BigDecimal amountSum;
    /**
     * 总客流数
     */
    private Integer personSum;
    /**
     * 总营业外收入
     */
    private BigDecimal moneyOverChargeSum;
}

package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 收款汇总分析
 * @Author LuoY
 * @Date 2019/11/22
 */
@Data
public class PosCashPayAnalysisVo implements Serializable {
    private static final long serialVersionUID = -1876580745138051843L;

    private String num;


    /**
     * 支付类型id
     */
    private String payTypeId;

    /**
     * 支付方式名称
     */
    private String payTypeName;

    /**
     * 笔数
     */
    private Integer count;

    /**
     * 金额
     */
    private BigDecimal moneyActualSum;

    /**
     * 笔数占比
     */
    private BigDecimal countPercent;

    /**
     * 金额占比
     */
    private BigDecimal moneyPercent;

    /**
     * 笔数占比 app
     */
    List<PosCashPayAnalysisVo> countPercentList;

    /**
     * 金额占比 app
     */
    List<PosCashPayAnalysisVo> moneyPercentList;
}

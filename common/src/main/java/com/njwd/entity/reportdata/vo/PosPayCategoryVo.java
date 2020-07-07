package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:收款方式分析
 * @Author shenhf
 * @Date 2019/11/25
 */
@Data
public class PosPayCategoryVo implements Serializable {
    private static final long serialVersionUID = 3196537579308531050L;

    /**
     * 序号
     */
    private String num;

    /**
     * 支付类型名称
     */
    private String payCategoryName;

    /**
     * 笔数
     */
    private Integer sumCount;

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
    List<PosPayCategoryVo> countPercentList;

    /**
     * 金额占比 app
     */
    List<PosPayCategoryVo> moneyPercentList;
}

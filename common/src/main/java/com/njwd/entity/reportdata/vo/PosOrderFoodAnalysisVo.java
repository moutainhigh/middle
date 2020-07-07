package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 菜品订单分析Vo
 * @Author LuoY
 * @Date 2019/11/18
 */
@Data
public class PosOrderFoodAnalysisVo implements Serializable {

    private static final long serialVersionUID = 3839170521961489030L;
    /**
     * 菜品类型名称
     */
    private String foodStyleId;

    /**
     * 菜品类型名称
     */
    private String foodStyleName;

    /**
     * 笔数
     */
    private Integer count;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 笔数占比
     */
    private BigDecimal countPercent;

    /**
     * 金额占比
     */
    private BigDecimal amountPercent;

    /**
     * 退菜笔数占比
     */
    List<PosOrderFoodAnalysisVo> retreatCountPercentList;

    /**
     * 退菜金额占比
     */
    List<PosOrderFoodAnalysisVo> retreatAmountPercentList;

    /**
     * 赠菜笔数占比
     */
    List<PosOrderFoodAnalysisVo> giveCountPercentList;

    /**
     * 赠菜金额占比
     */
    List<PosOrderFoodAnalysisVo> giveAmountPercentList;

    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 退增类型 1赠送；0退单
     */
    private int retreatGiveType;
}

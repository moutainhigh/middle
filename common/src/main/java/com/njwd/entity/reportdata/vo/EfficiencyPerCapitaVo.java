package com.njwd.entity.reportdata.vo;

import com.njwd.common.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 人均效率分析
 * @Author: ZhuHC
 * @Date: 2020/3/24 10:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EfficiencyPerCapitaVo extends BaseScopeOfQueryType implements Serializable {

    /**
     * 本期-前厅-客流人数
     */
    private Integer customNum = Constant.Number.ZERO;

    /**
     * 本期-前厅-员工人数
     */
    private Integer staffNum = Constant.Number.ZERO;

    /**
     * 本期-前厅-人均工作量
     */
    private BigDecimal avgWork = Constant.Number.ZEROB;

    /**
     * 本期-后厨-菜品份数
     */
    private BigDecimal dishesNum = Constant.Number.ZEROB;

    /**
     * 本期-后厨-员工人数
     */
    private Integer backStaffNum = Constant.Number.ZERO;

    /**
     * 本期-后厨-人均工作量
     */
    private BigDecimal backAvgWork = Constant.Number.ZEROB;

    /**
     * 上年同期-前厅-客流人数
     */
    private Integer lastCustomNum = Constant.Number.ZERO;

    /**
     * 上年同期-前厅-员工人数
     */
    private Integer lastStaffNum = Constant.Number.ZERO;

    /**
     * 上年同期-前厅-人均工作量
     */
    private BigDecimal lastAvgWork = Constant.Number.ZEROB;

    /**
     * 上年同期-后厨-菜品份数
     */
    private BigDecimal lastDishesNum = Constant.Number.ZEROB;

    /**
     * 上年同期-后厨-员工人数
     */
    private Integer lastBackStaffNum = Constant.Number.ZERO;

    /**
     * 上年同期-后厨-人均工作量
     */
    private BigDecimal lastBackAvgWork = Constant.Number.ZEROB;

    /**
     * 较上年人数-前厅
     */
    private BigDecimal staffNumPercentage;

    /**
     * 较上年人数-后厨
     */
    private BigDecimal backStaffNumPercentage;

    /**
     * 较上年工作量-前厅
     */
    private BigDecimal avgWorkPercentage;

    /**
     * 较上年工作量-后厨
     */
    private BigDecimal backAvgWorkPercentage;

}

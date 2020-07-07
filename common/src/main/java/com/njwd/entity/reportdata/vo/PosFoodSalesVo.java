package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description:菜品销量分析表
 * @Author shenhf
 * @Date 2019/11/26
 */
@Data
@ApiModel
public class PosFoodSalesVo implements Serializable {
    private static final long serialVersionUID = 3196537579308531050L;

    /**
     * Excel导出用序号
     */
    private String num;

    /**
     *  菜品ID
     */
    @ApiModelProperty(name = "foodId",value="菜品ID")
    private String foodId;

    /**
     * 菜品名称
     */
    @ApiModelProperty(name = "foodName",value="菜品名称")
    private String foodName;

    /**
     * 销量
     */
    @ApiModelProperty(name = "salesCount",value="本期销量")
    private BigDecimal salesCount;

    /**
     * 占比
     */
    @ApiModelProperty(name = "countPercent",value="占比")
    private BigDecimal countPercent;


    /**
     * 上期销量
     */
    @ApiModelProperty(name = "upSalesCount",value="上期销量")
    private BigDecimal upSalesCount;
    /**
     * 环比
     */
    @ApiModelProperty(name = "ringRatioPercent",value="环比")
    private BigDecimal ringRatioPercent;

    /**
     * 去年同期销量
     */
    @ApiModelProperty(name = "lastYearSalesCount",value="去年同期销量")
    private BigDecimal lastYearSalesCount;
    /**
     * 同比
     */
    @ApiModelProperty(name = "withPercent",value="同比")
    private BigDecimal withPercent;

}

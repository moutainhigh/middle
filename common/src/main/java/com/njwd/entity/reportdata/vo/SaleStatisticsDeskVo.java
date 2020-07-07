package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 台位分析
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
public class SaleStatisticsDeskVo {
    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 桌台数
     */
    private BigDecimal deskCount;

    /**
     * 数值类型
     */
    private int dataType;

    /**
     * 是否节假日，工作日
     */
    private Byte isWorkDay;

    /**
     * 开始天数
     */
    private Integer startDay;

    /**
     * 结束天数
     */
    private Integer endDay;

    /**
     * 天数
     */
    private Integer days;

    /**
    * 门店数
    */
    private Integer shopCount;
}

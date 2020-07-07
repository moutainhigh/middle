package com.njwd.entity.basedata.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.njwd.entity.basedata.BaseShop;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/27 13:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseShopVo extends BaseShop {

    private static final long serialVersionUID = 2850263751866682538L;

    /**
     * 品牌
     */
    @ExcelProperty(index = 0,value = "品牌")
    private String brandName;
    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 区域
     */
    @ExcelProperty(index = 1,value = "区域")
    private String regionName;

    //累计利润
    private BigDecimal addProfit;

    //实时利润分析金额
    private BigDecimal amount = new BigDecimal(0.00);

    //实时利润分析项目编码
    private String itemCode;

    //实时利润分析日期
    private Date itemDate;

    /**
     * 项目序号
     */
    private String itemNumber;


    /**
     * 项目级次
     */
    private Integer itemLevel;

}

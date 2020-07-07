package com.njwd.entity.reportdata.vo.fin;

import com.njwd.entity.reportdata.FinReport;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *@description: 财务报表基准数据表Vo
 *@author: fancl
 *@create: 2020-01-10 
 */
@Getter
@Setter
public class FinReportVo extends FinReport {
    //品牌
    private String brandName;
    //区域
    private String regionName;
    //门店
    private String shopName;
    //门店面积
    private BigDecimal shopArea;
    //物业费用
    private BigDecimal property;

    //合计 = 租金+物业
    private BigDecimal total;

    /**
     * 预算数
     */
    private BigDecimal BudgetMoney;

    /**
     * 预算比
     */
    private BigDecimal BudgetCompare;

}

package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.FinancialReportItemSet;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author liuxiang
 * @Description 财务报告项目明细设置
 * @Date:14:19 2019/7/18
 **/
@Getter
@Setter
public class FinancialReportItemSetVo extends FinancialReportItemSet {
    /**
     * @Description 财务报告项目明细公式列表
     **/
    private List<FinancialReportItemFormulaVo> financialReportItemFormulaVoList;

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 门店机构类型
     */
    private String shopTypeId;

    /**
     * 记账期间年号
     */
    private Integer periodYearNum;

    /**
     * 期末余额
     */
    private BigDecimal closeBalance;

    /**
     * 期末余额
     */
    private BigDecimal yearBalance;

    /**
     * 本期发生金额
     */
    private BigDecimal amountBalance;

    /**
     * 上期发生金额
     */
    private BigDecimal lastAmountBalance;

    /**
     * 本年累计金额
     */
    private BigDecimal totalAmountBalance;
}
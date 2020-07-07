package com.njwd.entity.reportdata.vo;


import com.njwd.entity.reportdata.FinReportTable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author lj
 * @Description 财务报告报表表
 * @Date:16:46 2020/1/13
 **/
@Getter
@Setter
public class FinReportTableVo extends FinReportTable {

    /**
     * 资产期末余额
     */
    private BigDecimal assetsCloseBalance;

    /**
     * 资产年初余额
     */
    private BigDecimal assetsYearBalance;

    /**
     * 资产期末余额(格式化数据)
     */
    private String assetsCloseBalanceS;

    /**
     * 资产年初余额
     */
    private String assetsYearBalanceS;

    /**
     * 资产项目名称
     */
    private String assetsItemName;

    /**
     * 资产项目行次
     */
    private Integer assetsLineNumber;

    /**
     * 资产项目序号
     */
    private String assetsItemNumber;

    /**
     * 资产项目类型
     */
    private Integer assetsItemType;

    /**
     * 资产项目级次
     */
    private Integer assetsItemLevel;

    /**
     * 负债期末余额
     */
    private BigDecimal debtCloseBalance;

    /**
     * 负债年初余额
     */
    private BigDecimal debtYearBalance;

    /**
     * 负债期末余额
     */
    private String debtCloseBalanceS;

    /**
     * 负债年初余额
     */
    private String debtYearBalanceS;

    /**
     * 负债项目名称
     */
    private String debtItemName;

    /**
     * 负债项目序号
     */
    private String debtItemNumber;

    /**
     * 负债项目行次
     */
    private Integer debtLineNumber;

    /**
     * 负债项目类型
     */
    private Integer debtItemType;

    /**
     * 负债项目级次
     */
    private Integer debtItemLevel;

    /**
     * 资产list
     */
    private List<FinReportTableVo> assetsList;

    /**
     * 负债list
     */
    private List<FinReportTableVo> debtList;

}

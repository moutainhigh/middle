package com.njwd.entity.reportdata;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author lj
 * @Description 财务报告报表表
 * @Date:16:46 2020/1/13
 **/
@Getter
@Setter
@TableName(value = "FIN_REPORT_TABLE")
public class FinReportTable implements Serializable {
    /**
     * 企业id
     */
    @TableField(value = "ENTE_ID")
    private String enteId;

    /**
     * 品牌id
     */
    @TableField(value = "BRAND_ID")
    private String brandId;

    /**
     * 区域id
     */
    @TableField(value = "REGION_ID")
    private String regionId;

    /**
     * 门店id
     */
    @TableField(value = "SHOP_ID")
    private String shopId;

    /**
     * 记账年度
     */
    @TableField(value = "PERIOD_YEAR_NUM")
    private Integer periodYearNum;

    /**
     * 期末余额
     */
    @TableField(value = "CLOSE_BALANCE")
    private BigDecimal closeBalance;

    /**
     * 年初余额
     */
    @TableField(value = "YEAR_BALANCE")
    private BigDecimal yearBalance;

    /**
     * 项目名称
     */
    @TableField(value = "ITEM_NAME")
    private String itemName;

    /**
     * 项目序号
     */
    @TableField(value = "ITEM_NUMBER")
    private String itemNumber;

    /**
     * 项目类型
     */
    @TableField(value = "ITEM_TYPE")
    private Integer itemType;

    /**
     * 项目级次
     */
    @TableField(value = "ITEM_LEVEL")
    private Integer itemLevel;

    /**
     * 报表id
     */
    @TableField(value = "REPORT_ID")
    private Integer reportId;

    /**
     * 报表名称
     */
    @TableField(value = "REPORT_NAME")
    private String reportName;

    private static final long serialVersionUID = -6448660094322182653L;
}
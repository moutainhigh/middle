package com.njwd.entity.reportdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.annotation.ExcelCell;
import com.njwd.utils.DateUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 经营日报指标
 * @Author jds
 * @Date 2019/12/3 14:10
 */
@Data
public class BusinessDailyIndic implements Serializable {

    private static final long serialVersionUID = 42L;
    /**
     * 主键id
     */
    private String dailyIndicId;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 门店id
     */
    public String shopId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 报表id
     */
    private Integer reportId;

    /**
     * 报表名称
     */
    private String reportName;

    /**
     * 项目id
     */
    public String projectId;

    /**
     * 项目名称
     */
    @ExcelCell(index = 1)
    private String projectName;

    /**
     *指标
     */
    @ExcelCell(index = 2)
    public BigDecimal indicator;

    /**
     * 开始有效期
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date beginDate;

    /**
     * 结束有效期
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    @ExcelCell(index = 3)
    private Date endDate;

    /**
     * 状态 0不可用，1可用
     */
    private Byte status;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 0 小数 1 百分号
     */
    private Integer type;

    /**
     * 有效时间日期
     */
    private String periodYearNum;

}

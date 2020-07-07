package com.njwd.entity.reportdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.utils.DateUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 实时利润预算
 * @Author jds
 * @Date 2019/12/3 14:10
 */
@Data
public class ProfitBudget implements Serializable {

    private static final long serialVersionUID = 42L;
    /**
     * 主键id
     */
    private String profitBudgetId;

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
    private String shopId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     *预算数
     */
    private BigDecimal num;

    /**
     *占收入比
     */
    private BigDecimal proportion;

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
    private Date endDate;

    /**
     * 状态 0不可用，1可用
     */
    private Byte status;

    /**
     * 最后修改时间
     */
    private Date updateTime;

}

package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/7 14:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SalaryAnalysisDto extends BaseQueryDto implements Serializable {
    /**
     * 上期开始时间
     */
    private Date prevBeginDate;
    /**
     * 上期结束时间
     */
    private Date prevEndDate;

    /**
     * 去年同期开始时间
     */
    private Date lastYearBeginDate;
    /**
     * 去年同期结束时间
     */
    private Date lastYearEndDate;

    /**
     * 查询开始时间
     */
    private Integer beginNum;

    /**
     * 查询结束时间
     */
    private Integer endNum;
    /**
     * 上期开始时间
     */
    private Integer prevBeginNum;
    /**
     * 上期结束时间
     */
    private Integer prevEndNum;

    /**
     * 去年同期开始时间
     */
    private Integer lastYearBeginNum;
    /**
     * 去年同期结束时间
     */
    private Integer lastYearEndNum;
}

package com.njwd.entity.reportdata.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/7 14:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffAnalysisDto extends BaseQueryDto implements Serializable {
    private static final long serialVersionUID = 242588294230304860L;
    /**
     * 上期开始时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date prevBeginDate;
    /**
     * 上期结束时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date prevEndDate;

    /**
     * 去年同期开始时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date lastYearBeginDate;
    /**
     * 去年同期结束时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date lastYearEndDate;

    /**
     * 员工id集合
     */
    @ApiModelProperty(name="userIdList",value = "员工ID")
    private List<String> userIdList;

    /**
     * 职位名称
     */
    @ApiModelProperty(name="postName",value = "职位名称")
    private String postName;

    /**
     * 查询开始时间
     */
    private Integer beginNum;

    /**
     * 查询结束时间
     */
    private Integer endNum;
}

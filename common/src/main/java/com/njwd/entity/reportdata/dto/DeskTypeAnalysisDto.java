package com.njwd.entity.reportdata.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/18 14:47
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DeskTypeAnalysisDto extends BaseQueryDto implements Serializable {
    private static final long serialVersionUID = 633931633327500530L;

    /**
     * 门店ID
     */
    private List<String> shopIdList;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY,timezone = "GMT+8")
    private Date startDate;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY,timezone = "GMT+8")
    private Date endDate;

    /**
     * 门店类别编码
     */
    private String shopTypeNo;
}

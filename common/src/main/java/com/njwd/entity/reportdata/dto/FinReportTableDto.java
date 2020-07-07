package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.vo.FinReportTableVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FinReportTableDto extends BaseQueryDto {
    /**
     * 报表id
     */
    private Integer reportId;

    /**
     * 记账期间年号
     */
    private Integer periodYearNum;
}

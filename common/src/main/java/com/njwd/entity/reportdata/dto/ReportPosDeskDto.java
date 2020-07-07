package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: ReportPosDeskDto
 * @Author LuoY
 * @Date 2019/12/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportPosDeskDto extends BaseQueryDto {
    /**
    * 门店id
    */
    private String shopId;
}

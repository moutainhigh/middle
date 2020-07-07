package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: SalesStatisticsDto
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SalesStatisticsDto extends BaseQueryDto {
    /**
     * 菜单 code
     */
    private String menuCode;
}

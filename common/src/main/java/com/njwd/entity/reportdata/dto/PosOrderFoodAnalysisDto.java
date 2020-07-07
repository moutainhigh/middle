package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 菜品订单分析Dto
 * @Author LuoY
 * @Date 2019/11/18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PosOrderFoodAnalysisDto extends BaseQueryDto {
    /**
     * 菜单 code
     */
    private String menuCode;

}

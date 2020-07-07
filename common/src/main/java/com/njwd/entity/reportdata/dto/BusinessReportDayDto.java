package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @Description: 经营日报表dto
* @Author: LuoY
* @Date: 2019/12/29 11:30
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessReportDayDto extends BaseQueryDto {
    /**
     * 查询类型
     */
    @ApiModelProperty(name="dataType",value = "查询类型 2:同比 3:环比")
    private Integer dataType;
}

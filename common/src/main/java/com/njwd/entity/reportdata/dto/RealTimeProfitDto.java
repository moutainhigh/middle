package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
* @Description: 实时利润分析dto
* @Author: liBao
* @Date: 2020/2/18 11:30
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class RealTimeProfitDto extends BaseQueryDto {

    /**
     * 科目code list
     */
    private List<String> subjectCodeList;

    /**
     * 查询开始时间
     */
    @ApiModelProperty(name="beginTime",value = "查询开始时间 yyyy-mm-dd格式")
    private String beginTime;

    /**
     * 查询结束时间
     */
    @ApiModelProperty(name="endTime",value = "查询结束时间 yyyy-mm-dd格式")
    private String endTime;

    /**
     * 查询类型
     */
    @ApiModelProperty(name="dataType",value = "查询类型 2:同比 3:环比")
    private Integer dataType;

    /**
     * 导出类型
     */
    @ApiModelProperty(name="type",value = "导出类型 all:全部 main:一级")
    private String type;

    /**
     * 查询毛利率  是：1  否：0
     */
    private Integer isQueryGrossMargin;

    /**
     * 待计算金额开始日期
     */
    private Date calculationBeginDate;

    /**
     * 待计算金额结束日期
     */
    private Date calculationEndDate;

}

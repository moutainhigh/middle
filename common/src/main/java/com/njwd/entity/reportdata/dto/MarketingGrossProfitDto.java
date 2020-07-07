package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author lj
 * @Description 营销活动毛利统计表
 * @Date:11:58 2020/3/27
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingGrossProfitDto extends BaseQueryDto {
    private String type;
    /**
     *1、本期，2、环比，3、同比
     **/
    private Integer queryType;
}

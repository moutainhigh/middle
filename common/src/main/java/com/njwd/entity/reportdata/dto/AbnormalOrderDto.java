package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author lj
 * @Description 异常账单统计表
 * @Date:11:58 2020/3/27
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class AbnormalOrderDto extends BaseQueryDto {
    private List<String> orderTypeIdList;
    private String type;
    private String city;
    private BigDecimal value;
    private BigDecimal valueTwo;
}

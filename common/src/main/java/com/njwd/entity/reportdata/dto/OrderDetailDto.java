package com.njwd.entity.reportdata.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.vo.OrderDetailVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author lj
 * @Description 账单明细表
 * @Date:11:58 2020/3/27
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetailDto extends BaseQueryDto {
    private Page<OrderDetailVo> page = new Page();
    private List<String> orderTypeIdList;
    private String city;
    private BigDecimal value;
    private BigDecimal valueTwo;
    private String foodName;
    private String abnormalFlag;
}

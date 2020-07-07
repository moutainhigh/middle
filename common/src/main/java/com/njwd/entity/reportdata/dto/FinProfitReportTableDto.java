package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.FinProfitReportTableVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
* @Description: 利润表查询dto1
* @Author: LuoY
* @Date: 2020/2/10 11:24
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class FinProfitReportTableDto extends FinProfitReportTableVo {
    /**
     * 选中的门店id集合
     */
    private List<String> shopIdList;

    /**
     * 门店类型ID
     */
    private List<String> shopTypeIdList;
}

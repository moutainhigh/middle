package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.FinCashFlowReportTableVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FinCashFlowReportTableDto extends FinCashFlowReportTableVo {
    /**
     * 选中的门店id集合
     */
    private List<String> shopIdList;

    /**
     * 门店类型ID
     */
    private List<String> shopTypeIdList;
}

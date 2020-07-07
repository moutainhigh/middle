package com.njwd.entity.basedata.dto;

import com.njwd.entity.basedata.BaseReportItemFormula;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @program: middle-data
 * @description: BaseReportItemFormulaDto
 * @author: shenhf
 * @create: 2020-03-23 19:43
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseReportItemFormulaDto extends BaseReportItemFormula {
    /**
     * 企业id
     */
    private String enteId;
    /**
     * 报表id
     */
    private Integer reportId;
}


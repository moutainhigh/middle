package com.njwd.entity.basedata;

import lombok.Data;

import java.io.Serializable;

/**
* @Description: 报表明细公式
* @Author: shf
* @Date: 2020/03/23 13:19
*/
@Data
public class BaseReportItemFormula implements Serializable {
    private static final long serialVersionUID = -1631538417519837570L;
    /**
     * 明细项目ID 【报告明细表】表ID
     */
    private Integer itemSetId;

    /**
     * 公式类型 0：科目或项目、1：项目行、2：现金流量特用
     */
    private Integer formulaType;

    /**
     * 公式项目编码 科目、现金流量项目、报告项目
     */
    private String formulaItemCode;

    /**
     * 运算标识 0：加、1：减
     */
    private Integer operator;

}


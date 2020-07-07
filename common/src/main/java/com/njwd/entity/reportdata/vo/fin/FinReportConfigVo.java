package com.njwd.entity.reportdata.vo.fin;

import com.njwd.entity.reportdata.FinReportConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *@description: 财务配置表Vo
 *@author: fancl
 *@create: 2020-01-10 
 */
@Getter
@Setter
public class FinReportConfigVo extends FinReportConfig {
    //配置的科目
    List<String> subjectCodeList ;
    //公式表达式对象
    List<FormulaVo> formulaExpList;
}

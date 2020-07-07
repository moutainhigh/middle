package com.njwd.report.service.impl;

import com.njwd.entity.reportdata.FinReport;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.reportdata.service.DemoFormulaService;

import java.util.List;

/**
 *@description:
 *@author: fancl
 *@create: 2020-02-22 
 */
public class DemoFormulaServiceImpl implements DemoFormulaService {
    @Override
    public List<FinReport> getReportList(FinQueryDto finQueryDto) {
        //根据配置查询公式
        //例如公式配置为科目 6401:debit:+,6402:debit

        return null;
    }
}

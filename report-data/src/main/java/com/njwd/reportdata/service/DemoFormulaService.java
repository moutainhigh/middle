package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.FinReport;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;

import java.util.List;
/**
 * @description Demo 展示公式配置的使用
 * @author fancl
 * @date 2020/2/22
 * @param 
 * @return 
 */
public interface DemoFormulaService {
    /**
     * @description 根据公式取得所需要的报表数据
     * @author fancl
     * @date 2020/2/22
     * @param 
     * @return 
     */
    List<FinReport> getReportList(FinQueryDto finQueryDto);
}

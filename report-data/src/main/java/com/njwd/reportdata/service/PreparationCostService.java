package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.fin.FinCostCompareVo;
import com.njwd.entity.reportdata.vo.fin.FinPrepaInvestVo;
import com.njwd.entity.reportdata.vo.fin.FinSubjectVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 筹建成本
 * @Author fancl
 * @Date 2019/11/20
 */
public interface PreparationCostService {
    /**
     * @description 工程明细查询
     * @author fancl
     * @date 2020/1/11
     * @param
     * @return
     */
    List<FinSubjectVo> getProjectDetail(FinQueryDto queryDto);

    /**
     * @description 筹建成本对比
     * @author fancl
     * @date 2020/1/13
     * @param
     * @return
     */
    List<FinCostCompareVo> compareCost(FinQueryDto queryDto);


    /**
     * @description 筹建成本明细
     * @author fancl
     * @date 2020/1/13
     * @param
     * @return
     */
    List<FinSubjectVo> costDetail(FinQueryDto queryDto);

    /**
     * @description 筹建投资回报Service
     * @author fancl
     * @date 2020/2/7
     * @param
     * @return
     */
    List<FinPrepaInvestVo> prepaInvest(FinQueryDto queryDto);

    /**
     * @description 工程明细 导出
     * @author fancl
     * @date 2020/2/22
     * @param
     * @return
     */
    void exportProjectDetail(ExcelExportDto excelExportDto, HttpServletResponse response);
    
    /**
     * @description 筹建成本对比 导出
     * @author fancl
     * @date 2020/2/23
     * @param
     * @return
     */
    void exportCompareCost(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * @description 筹建成本明细 导出
     * @author fancl
     * @date 2020/2/23
     * @param
     * @return
     */
    void exportCostDetail(ExcelExportDto excelExportDto, HttpServletResponse response);

    

    /**
     * @description 筹建投资回报 导出
     * @author fancl
     * @date 2020/2/11
     * @param
     * @return
     */
    void exportInvest(ExcelExportDto excelExportDto, HttpServletResponse response);


}

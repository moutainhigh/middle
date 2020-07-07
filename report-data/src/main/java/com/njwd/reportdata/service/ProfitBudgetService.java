package com.njwd.reportdata.service;


import com.njwd.entity.reportdata.dto.ProfitBudgetDto;
import com.njwd.entity.reportdata.vo.ProfitBudgetVo;
import com.njwd.support.BatchResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description 实时利润预算
 * @Author jds
 * @Date  2019/12/3 14:31
 */
public interface ProfitBudgetService {

    /**
     * @Description //批量新增实时利润预算
     * @Author jds
     * @Date 2019/12/5 19:12
     * @Param [profitBudgetDto]
     * @return java.lang.Integer
     **/
    Integer addBudgetBatch(ProfitBudgetDto profitBudgetDto);

    /**
     * @Description //批量修改实时利润预算
     * @Author jds
     * @Date 2019/12/5 19:12
     * @Param [profitBudgetDto]
     * @return com.njwd.support.BatchResult
     **/
    BatchResult updateBudgetBatch(ProfitBudgetDto profitBudgetDto);

    /**
     * @Description //修改实时利润预算
     * @Author jds
     * @Date 2019/12/5 19:12
     * @Param [profitBudgetDto]
     * @return java.lang.Integer
     **/
    Integer updateBudgetById(ProfitBudgetDto profitBudgetDto);

    /**
     * @Description //查询实时利润预算列表
     * @Author jds
     * @Date 2019/12/5 19:13
     * @Param [profitBudgetDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.ProfitBudgetVo>
     **/
    List<ProfitBudgetVo>findBudgetList(ProfitBudgetDto profitBudgetDto);

    /**
     * @Description //根据id查询实时利润预算
     * @Author jds
     * @Date 2019/12/5 19:13
     * @Param [profitBudgetDto]
     * @return com.njwd.entity.reportdata.vo.ProfitBudgetVo
     **/
    ProfitBudgetVo findBudgetById(ProfitBudgetDto profitBudgetDto);

    /**
     * @Description //导出
     * @Author jds
     * @Date 2019/12/5 19:13
     * @Param [profitBudgetDto, response]
     * @return void
     **/
    void exportExcel(ProfitBudgetDto profitBudgetDto, HttpServletResponse response);

}

package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.dto.ProfitBudgetDto;
import com.njwd.entity.reportdata.vo.ProfitBudgetVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 实时利润预算
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Repository
public interface ProfitBudgetMapper extends BaseMapper<ProfitBudgetDto> {


    /**
     * @Description //批量新增实时利润预算
     * @Author jds
     * @Date 2019/12/5 17:47
     * @Param [list]
     * @return java.lang.Integer
     **/
    Integer addBudgetBatch(List<ProfitBudgetDto> list);


    /**
     * @Description //批量禁用反禁用
     * @Author jds
     * @Date 2019/12/5 17:48
     * @Param [profitBudgetDto]
     * @return java.lang.Integer
     **/
    Integer updateBudgetBatch(@Param("profitBudgetDto") ProfitBudgetDto profitBudgetDto);

    /**
     * @Description //修改实时利润预算
     * @Author jds
     * @Date 2019/12/5 17:48
     * @Param [profitBudgetDto]
     * @return java.lang.Integer
     **/
    Integer updateBudgetById(@Param("profitBudgetDto") ProfitBudgetDto profitBudgetDto);


    /**
     * @Description //查询实时利润预算列表
     * @Author jds
     * @Date 2019/12/5 17:49
     * @Param [profitBudgetDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.ProfitBudgetVo>
     **/
    List<ProfitBudgetVo>findBudgetList(@Param("profitBudgetDto") ProfitBudgetDto profitBudgetDto);

    /**
     * @Description //根据查询实时利润预算
     * @Author jds
     * @Date 2019/12/5 17:49
     * @Param [profitBudgetDto]
     * @return com.njwd.entity.reportdata.vo.ProfitBudgetVo
     **/
    ProfitBudgetVo findBudgetById(@Param("profitBudgetDto") ProfitBudgetDto profitBudgetDto);
}

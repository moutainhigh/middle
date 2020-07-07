package com.njwd.reportdata.mapper;

import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.dto.GrossProfitDto;
import com.njwd.entity.reportdata.dto.scm.ScmReportTableDto;
import com.njwd.entity.reportdata.vo.scm.ScmReportTableVo;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @description: 生成供应链报表表mapper
 * @author: 周鹏
 * @create: 2020-03-31
 **/
@Repository
public interface ScmReportTableMapper {
    /**
     * 查询菜金、自选调料和赠送成本
     *
     * @param queryDto
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date 2020/03/31
     */
    List<ScmReportTableVo> findStkCostList(ScmReportTableDto queryDto);

    /**
     * 查询酒水成本
     *
     * @param queryDto
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date 2020/03/31
     */
    List<ScmReportTableVo> findWineCostList(ScmReportTableDto queryDto);

    /**
     * 删除报表表信息
     *
     * @param simpleDto
     * @return int
     * @Author 周鹏
     * @Date 2020/03/31
     */
    int deleteByParam(TransferReportSimpleDto simpleDto);

    /**
     * 批量生成报表表信息
     *
     * @param list
     * @return int
     * @Author 周鹏
     * @Date 2020/03/31
     */
    int addBatch(List<ScmReportTableVo> list);

    /**
     * 查询毛利分析表信息
     *
     * @param queryDto
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date 2020/03/31
     */
    List<ScmReportTableVo> findGrossProfitCostList(GrossProfitDto queryDto);

    /**
     * 查询菜金、自选调料和赠送成本
     *
     * @param queryDto
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date 2020/04/02
     */
    List<ScmReportTableVo> findDishStockAmountList(ScmReportTableDto queryDto);

    /**
     * 查询上期库存
     *
     * @param queryDto
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date 2020/04/02
     */
    List<ScmReportTableVo> findLastPeriodStockList(GrossProfitDto queryDto);
}

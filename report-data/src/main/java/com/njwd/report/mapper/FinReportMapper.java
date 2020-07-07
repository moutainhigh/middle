package com.njwd.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.FinReport;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.fin.FinReportVo;
import com.njwd.entity.reportdata.vo.fin.FinVoucherVo;
import com.njwd.entity.reportdata.vo.fin.RealProfitBudgetVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 财务报表基准表Mapper
 * @author fancl
 * @date 2020/1/8
 * @param
 * @return
 */
public interface FinReportMapper extends BaseMapper<FinReport> {

    /**
     * @description 更新科目发生额数据
     * @author fancl
     * @date 2020/1/8
     * @param
     * @return
     */
    @Deprecated
    int refreshSubjectData(@Param("simpleDto") TransferReportSimpleDto simpleDto, @Param("subList") List<String> subList);


    /**
     * @description 从凭证表汇总发生额
     * @author fancl
     * @date 2020/2/13
     * @param dayType 查询类型 ,一共分为4种 ,详见Service对应方法
     * @return
     */
    List<FinReportVo> getVouchers(@Param("dayType") String dayType, @Param("simpleDto") TransferReportSimpleDto simpleDto, @Param("subjectList") List<String> subjectList);

    /**
     * @description 查询废弃凭证
     * @author fancl
     * @date 2020/2/28
     * @param
     * @return
     */
    List<FinVoucherVo> getDiscardVouchers(@Param("simpleDto") TransferReportSimpleDto simpleDto);
    /**
     * @description 从财务科目基准表中取数
     * @author fancl
     * @date 2020/2/13
     * @param
     * @return
     */
    List<FinReportVo> getReports(@Param("dayType") String dayType, @Param("simpleDto") TransferReportSimpleDto simpleDto);

    /**
        获取个性化本地数据
     */
    List<FinReportVo> getPersonalReports(@Param("dayType") String dayType, @Param("simpleDto") TransferReportSimpleDto simpleDto, @Param("flag") String flag);


    /**
     * @description report表新增数据
     * @author fancl
     * @date 2020/2/14
     * @param
     * @return
     */
    int addBatch(List<? extends FinReport> reports);

    //flag:个性化标识
    int addPersonalBatch(@Param("list") List<? extends FinReport> reports ,@Param("flag") String flag);

    //清空report主表
    int truncateReport();

    //清空report主表
    int truncatePersonalReport();

    /**
     * @description 批量更新fin_report
     * @author fancl
     * @date 2020/2/14
     * @param
     * @return
     */
    int updateBatch(List<? extends FinReport> reports);

    //批量更新fin_report_personal
    int updatePersonalBatch(List<? extends FinReport> reports);

    /**
     * @description 查询科目发生额
     * @author fancl
     * @date 2020/1/11
     * @param
     * @return
     */
    List<FinReportVo> getSubjectDataByCondition(@Param("queryDto") FinQueryDto queryDto);

    /**
     * @description 查询实时利润预算
     * @author liBao
     * @date 2020/2/29
     * @param
     * @return
     */
    List<RealProfitBudgetVo> getRealProfitBudgetList(@Param("queryDto") FinQueryDto queryDto);
}

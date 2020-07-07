package com.njwd.report.service;

import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.FinReportConfig;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.FinBalanceSubjectVo;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;
import com.njwd.entity.reportdata.vo.fin.FinReportVo;
import com.njwd.entity.reportdata.vo.fin.FormulaVo;
import com.njwd.entity.reportdata.vo.fin.RealProfitBudgetVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description 汇总科目发生额
 * @author fancl
 * @date 2020/1/4
 * @param
 * @return
 */
public interface FinanceSubjectService {


    /**
     * @description 查询报表配置
     * @author fancl
     * @date 2020/1/14
     * @param
     * @return
     */
    @Deprecated
    FinReportConfig getFinReportConfig(@Param("enteId") String enteId,@Param("finType") String finType);


    /**
     * @description 根据group 和 type获取单个配置对象
     * @author fancl
     * @date 2020/2/20
     * @param
     * @return
     */
    FinReportConfigVo getConfigByGroupAndType(String enteId, String finGroup, String finType);
    
    
    /**
     * @description 根据企业id 和 group查询多个对象
     * @author fancl
     * @date 2020/2/21
     * @param 
     * @return 
     */
    List<FinReportConfigVo> getConfigByGroup(String enteId, String finGroup);
    /**
     * @description 取得公式配置对象
     * @author fancl
     * @date 2020/2/19
     * @param
     * @return
     */
    FinReportConfigVo getFinConfigAndFormula(String enteId, String finGroup, String finType);

    /**
     * @description 取得废弃凭证id list
     * @author fancl
     * @date 2020/2/28
     * @param
     * @return
     */
    @Deprecated
    List<String> discardVoucherIdList(TransferReportSimpleDto simpleDto);

    /**
     * @description 计算并且更新科目发生额
     * @author fancl
     * @date 2020/1/11
            * @param
     * @return
             */
    int calcAndRefreshSubjectData(TransferReportSimpleDto simpleDto);

/**
 * @description 更新个性化数据 例如根据特殊条件(摘要等)汇总的
 * @author fancl
 * @date 2020/2/29
 * @param
 * @return
 */
    int calcAndRefreshPersonal(TransferReportSimpleDto simpleDto);

    /**
     * @description 获取科目发生额
     * @author fancl
     * @date 2020/1/11
     * @param
     * @return
     */
    List<FinReportVo> getSubjectData(FinQueryDto queryDto);

    /**
     * @description 获取科目余额表中的数据
     * @author fancl
     * @date 2020/2/19
     * @param finReportListMap: key为门店id, value为门店下的凭证数据列表
     * @param formulaVos: 公式表达式列表
     * @return key为shopId ,value是该门店下根据公式计算后的加个
     */
    Map<String, BigDecimal> getShopAmountByFormula(Map<String, List<FinReportVo>> finReportListMap, List<FormulaVo> formulaVos);

    //根据条件查询凭证数据
    List<FinReportVo> getVouchersByCondition(String dayType, TransferReportSimpleDto simpleDtoDto, List<String> subjectList) ;

    /**
     * @description 查询实时利润分析预算
     * @author liBao
     * @return
     */
    List<RealProfitBudgetVo> getRealProfitBudgetList(FinQueryDto queryDto);

}

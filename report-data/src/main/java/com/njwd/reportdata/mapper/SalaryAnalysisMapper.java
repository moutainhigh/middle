package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.dto.SalaryAnalysisDto;
import com.njwd.entity.reportdata.vo.BrandBonusVo;
import com.njwd.entity.reportdata.vo.WageShareAnalysisVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ZhuHC
 * @Date  2020/3/30 16:25
 * @Description 工资占销比
 */
@Repository
public interface SalaryAnalysisMapper {

    List<WageShareAnalysisVo> findTurnoverList(@Param("queryDto") SalaryAnalysisDto queryDto);

    List<WageShareAnalysisVo> findProfitList(@Param("queryDto") SalaryAnalysisDto queryDto);

}

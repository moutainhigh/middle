package com.njwd.report.mapper;

import com.njwd.entity.reportdata.dto.BalanceDto;
import com.njwd.entity.reportdata.vo.FinancialReportItemSetVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author lj
 * @Description 财务报表
 * @Date:9:20 2020/1/2
 **/
public interface FinancialReportMapper {

    /**
     * 查询报告项目公式信息
     * @Author lj
     * @Date:9:24 2020/1/2
     * @param balanceDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinancialReportItemSetVo>
     **/
    List<FinancialReportItemSetVo> findFinancialReportItemList(@Param("balanceDto") BalanceDto balanceDto);

}

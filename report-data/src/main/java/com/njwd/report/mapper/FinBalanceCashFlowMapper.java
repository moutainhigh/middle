package com.njwd.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.FinBalanceCashFlow;
import com.njwd.entity.reportdata.dto.FinBalanceCashFlowDto;
import com.njwd.entity.reportdata.dto.FinBalanceSubjectDto;
import com.njwd.entity.reportdata.vo.FinBalanceCashFlowVo;

import java.util.List;

public interface FinBalanceCashFlowMapper extends BaseMapper<FinBalanceCashFlow> {

    /**
     * 查询科目余额列表数据
     * @Author lj
     * @Date:15:32 2020/1/10
     * @param finBalanceCashFlowDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinBalanceSubjectVo>
     **/
    List<FinBalanceCashFlowVo> findFinBalanceCashFlowList(FinBalanceCashFlowDto finBalanceCashFlowDto);
}
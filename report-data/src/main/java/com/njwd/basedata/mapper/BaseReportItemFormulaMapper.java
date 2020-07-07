package com.njwd.basedata.mapper;

import com.njwd.entity.basedata.dto.BaseReportItemFormulaDto;
import com.njwd.entity.basedata.vo.BaseReportItemFormulaVo;

import java.util.List;

/**
 * @Description: 报表明细公式
 * @Author: shf
 * @Date: 2020/03/23 13:19
 */
public interface BaseReportItemFormulaMapper {
    /**
     * @Description: 根据报表id查询报表项目明细公式
     * @Param: [baseReportItemFormulaDto]
     * @return: java.util.List<com.njwd.entity.basedata.vo.BaseReportItemFormulaVo>
     * @Author: shf
     * @Date: 2020-03-23 19:43
     */
    List<BaseReportItemFormulaVo> findBaseReportItemFormulaVoByReportId(BaseReportItemFormulaDto baseReportItemFormulaDto);
}

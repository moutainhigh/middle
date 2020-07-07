package com.njwd.basedata.service.impl;

import com.njwd.basedata.mapper.BaseReportItemFormulaMapper;
import com.njwd.basedata.service.BaseReportItemFormulaService;
import com.njwd.entity.basedata.dto.BaseReportItemFormulaDto;
import com.njwd.entity.basedata.vo.BaseReportItemFormulaVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: middle-data
 * @description: 报表项目明细公式
 * @author: shenhf
 * @create: 2020-03-23 19:50
 **/
@Service
public class BaseReportItemFormulaServiceImpl implements BaseReportItemFormulaService {

    @Resource
    private BaseReportItemFormulaMapper baseReportItemFormulaMapper;
    @Override
    public List<BaseReportItemFormulaVo> findBaseReportItemFormulaVoByReportId(BaseReportItemFormulaDto baseReportItemFormulaDto) {
        return baseReportItemFormulaMapper.findBaseReportItemFormulaVoByReportId(baseReportItemFormulaDto);
    }
}


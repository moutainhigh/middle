package com.njwd.reportdata.service;

import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.dto.ScmReportDto;
import com.njwd.entity.reportdata.vo.ScmReportVo;

import java.util.List;

/**
 * @description: 生成供应链报表表service
 * @author: 周鹏
 * @create: 2020-03-31
 */
public interface ScmReportTableService {
    /**
     * 生成报表表信息
     *
     * @param simpleDto
     * @return Result
     * @author: 周鹏
     * @create: 2020-03-31
     */
    int refreshScmData(TransferReportSimpleDto simpleDto);
}

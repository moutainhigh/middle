package com.njwd.reportdata.api;

import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: 生成供应链报表表
 * @author: 周鹏
 * @create: 2020-04-01
 */
@RequestMapping("reportdata/scmReportTable")
public interface ScmReportTableApi {
    /**
     * 生成全量报表表信息
     *
     * @param simpleDto
     * @return Result
     * @author: 周鹏
     * @create: 2020-04-01
     */
    @PostMapping("refreshFullScmData")
    Result refreshFullScmData(@RequestBody TransferReportSimpleDto simpleDto);

    /**
     * 批量更新报表表信息
     *
     * @param simpleDto
     * @return Result
     * @author: 周鹏
     * @create: 2020-04-01
     */
    @PostMapping("refreshPartScmData")
    Result refreshPartScmData(@RequestBody TransferReportSimpleDto simpleDto);

}

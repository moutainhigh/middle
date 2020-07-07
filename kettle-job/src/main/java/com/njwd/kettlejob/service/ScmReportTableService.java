package com.njwd.kettlejob.service;

import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @description: 生成供应链报表表
 * @author: 周鹏
 * @create: 2020-04-01
 */
public interface ScmReportTableService {
    /**
     * 生成全量报表表信息
     *
     * @param simpleDto
     * @return Result
     * @author: 周鹏
     * @create: 2020-04-01
     */
    Result refreshFullScmData(String appId, String enteId, Map<String, String> params);

    /**
     * 批量更新报表表信息
     *
     * @param simpleDto
     * @return Result
     * @author: 周鹏
     * @create: 2020-04-01
     */
    String refreshPartScmData(String appId, String enteId, Map<String, String> params);
}

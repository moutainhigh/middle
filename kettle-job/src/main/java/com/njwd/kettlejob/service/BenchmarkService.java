package com.njwd.kettlejob.service;

import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;

/**
 * @description 基准设置Service
 * @author fancl
 * @date 2020/1/7
 * @param
 * @return
 */
public interface BenchmarkService {
    /**
     * @description 根据企业id 和benchcode得到报表传输对象
     * @author fancl
     * @date 2020/1/7
     * @param
     * @return
     */
    TransferReportDto getBenchmark(TransferReportSimpleDto transferReportSimple);


}

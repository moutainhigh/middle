package com.njwd.kettlejob.service;

import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.support.Result;

import java.util.Map;

/**
 * @description 取科目发生数据Service
 * @author fancl
 * @date 2020/1/7
 * @param
 * @return
 */
public interface FinanceSubjectJobService {
    /**
     * @description 调用reportData模块
     * @author fancl
     * @date 2020/1/7
     * @param
     * @return
     */
    Result<TransferReportDto> doRefreshSubjectData(TransferReportSimpleDto simpleDto);


    /**
     * @description 自动刷新科目数据
     * @author fancl
     * @date 2020/3/18
     * @param
     * @return
     */
    String doRefreshPartSubjectData(String appId,String enteId,Map<String,String> params);

    /**
     * @description 手工执行刷新科目数据
     * @author fancl
     * @date 2020/3/18
     * @param
     * @return
     */
    Result<TransferReportDto> doRefreshPartSubjectDataByMan(String appId,String enteId,Map<String,String> params);
}

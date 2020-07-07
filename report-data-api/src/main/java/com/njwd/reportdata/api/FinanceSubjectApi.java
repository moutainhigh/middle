package com.njwd.reportdata.api;

import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * @description 筹建成本Api
 * @author fancl
 * @date 2020/1/4
 * @param
 * @return
 */
@RequestMapping("reportdata/financeSubject")
public interface FinanceSubjectApi {

    /**
     * @description 全量更新科目发生额
     * @author fancl
     * @date 2020/1/8
     * @param
     * @return
     */
    @PostMapping("refreshSubjectData")
    Result refreshSubjectData(@RequestBody TransferReportSimpleDto simpleDto);


    /**
     * @description 增量更新科目发生额
     * @author fancl
     * @date 2020/1/8
     * @param
     * @return
     */
    @PostMapping("refreshPartSubjectData")
    Result refreshPartSubjectData(@RequestBody TransferReportSimpleDto simpleDto);




}

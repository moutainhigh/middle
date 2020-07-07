package com.njwd.kettlejob.controller;

import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.kettlejob.service.FinanceSubjectJobService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *@description: 科目发生数据Controller
 *@author: fancl
 *@create: 2020-01-07 
 */
@RestController
@RequestMapping("financeSubjectJob")
public class FinanceSubjectJobController extends BaseController {


    @Resource
    FinanceSubjectJobService financeSubjectJobService;
    /**
     * @description 调用report-data模块更新科目发生额
     * @author fancl
     * @date 2020/1/7
     * @param
     * @return
     */
    Result<String> doRefreshSubjectData(@RequestBody TransferReportSimpleDto simpleDtoDto){
        FastUtils.checkParams(simpleDtoDto,simpleDtoDto.getEnteId(),simpleDtoDto.getTransDay());
        financeSubjectJobService.doRefreshSubjectData(simpleDtoDto);
        return ok();
    }

}

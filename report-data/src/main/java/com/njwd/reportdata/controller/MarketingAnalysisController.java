package com.njwd.reportdata.controller;

import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;

import com.njwd.entity.reportdata.vo.fin.FinSubjectVo;
import com.njwd.reportdata.service.MarketingAnalysisService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 营销分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "marketingAnalysisController",tags = "营销分析")
@RestController
@RequestMapping("marketingAnalysis")
public class MarketingAnalysisController extends BaseController {

    @Resource
    private MarketingAnalysisService marketingAnalysisService;
    /**
     * 营销费用统计
     * @Author lj
     * @Date:14:07 2020/1/14
     * @param queryDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.FinSubjectVo>>
     **/
    @ApiOperation(value = "营销费用统计", notes = "营销费用统计")
    @RequestMapping("marketingCost")
    public Result<List<FinSubjectVo>> marketingCost(@RequestBody FinQueryDto queryDto){
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginTime(),
                queryDto.getEndTime(), queryDto.getShopIdList());
        return ok(marketingAnalysisService.marketingCost(queryDto));
    }

    /**
     * 导出营销费用统计
     * @Author lj
     * @Date:14:41 2020/3/6
     * @param excelExportDto, response]
     * @return void
     **/
    @RequestMapping("exportMarketingCost")
    public void exportMarketingCost(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginTime(), excelExportDto.getEndTime(),
                excelExportDto.getType(), excelExportDto.getModelType(), excelExportDto.getMenuName());
        marketingAnalysisService.exportMarketingCost(excelExportDto, response);
    }
}

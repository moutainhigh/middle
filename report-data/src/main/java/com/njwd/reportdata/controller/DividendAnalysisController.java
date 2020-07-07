package com.njwd.reportdata.controller;

import com.njwd.entity.reportdata.dto.BalanceDto;
import com.njwd.entity.reportdata.dto.ShareholderDividendDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.FinReportTableVo;
import com.njwd.entity.reportdata.vo.ShareholderDividendVo;
import com.njwd.reportdata.service.DividendAnalysisService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author lj
 * @Description 分红分析
 * @Date:11:23 2020/2/28
 **/
@Api(value = "dividendAnalysisController",tags = "分红分析")
@RestController
@RequestMapping("dividendAnalysis")
public class DividendAnalysisController extends BaseController {

    @Autowired
    private DividendAnalysisService dividendAnalysisService;

    /**
     * 股东分红表
     * @Author lj
     * @Date:15:10 2020/2/29
     * @param shareholderDividendDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.ShareholderDividendVo>>
     **/
    @ApiOperation(value = "股东分红表", notes = "股东分红表")
    @RequestMapping("findShareholderDividend")
    public Result<List<ShareholderDividendVo>> findShareholderDividend(@RequestBody ShareholderDividendDto shareholderDividendDto){
        return ok(dividendAnalysisService.findShareholderDividend(shareholderDividendDto));
    }

    /**
     * 导出股东分红表
     * @Author lj
     * @Date:16:43 2020/3/12
     * @param excelExportDto, response]
     * @return void
     **/
    @RequestMapping("exportShareholderDividend")
    public void exportShareholderDividend(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginDate(), excelExportDto.getType(), excelExportDto.getModelType());
        dividendAnalysisService.exportShareholderDividend(excelExportDto, response);
    }

    /**
     * 股东分红单
     * @Author lj
     * @Date:15:10 2020/2/29
     * @param shareholderDividendDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.ShareholderDividendVo>>
     **/
    @ApiOperation(value = "股东分红单", notes = "股东分红单")
    @RequestMapping("findShareholderSheet")
    public Result<List<ShareholderDividendVo>> findShareholderSheet(@RequestBody ShareholderDividendDto shareholderDividendDto){
        return ok(dividendAnalysisService.findShareholderSheet(shareholderDividendDto));
    }

    /**
     * 导出股东分红单
     * @Author lj
     * @Date:16:43 2020/3/12
     * @param excelExportDto, response]
     * @return void
     **/
    @RequestMapping("exportShareholderSheet")
    public void exportShareholderSheet(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginDate(), excelExportDto.getType(), excelExportDto.getModelType());
        dividendAnalysisService.exportShareholderSheet(excelExportDto, response);
    }

}

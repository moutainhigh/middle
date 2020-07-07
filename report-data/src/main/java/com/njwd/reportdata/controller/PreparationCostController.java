package com.njwd.reportdata.controller;

import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.fin.FinCostCompareVo;
import com.njwd.entity.reportdata.vo.fin.FinPrepaInvestVo;
import com.njwd.entity.reportdata.vo.fin.FinSubjectVo;
import com.njwd.reportdata.service.PreparationCostService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 筹建成本:报表 + 导出
 *
 * @Author fancl
 * @Date 2019/11/20
 */
@Api(value = "preparationCostController", tags = "筹建成本")
@RestController
@RequestMapping("preparationCost")
public class PreparationCostController extends BaseController {

    @Autowired
    PreparationCostService preparationCostService;

    @ApiOperation(value = "工程明细统计", notes = "工程明细统计")
    @PostMapping("projectDetail")
    public Result<List<FinSubjectVo>> projectDetail(@RequestBody FinQueryDto queryDto) {
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginTime(),
                queryDto.getEndTime(), queryDto.getShopIdList()
                //, queryDto.getShopTypeIdList()
        );
        List<FinSubjectVo> subjectList = preparationCostService.getProjectDetail(queryDto);
        return ok(subjectList);
    }

    /**
     * @description 工程明细导出Excel
     * @author fancl
     * @date 2020/2/11
     * @param
     * @return
     */
    @RequestMapping("exportProjectDetail")
    public void exportProjectDetail(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginTime(), excelExportDto.getEndTime(),
                excelExportDto.getType(), excelExportDto.getModelType(), excelExportDto.getMenuName());
        preparationCostService.exportProjectDetail(excelExportDto, response);
    }

    @ApiOperation(value = "筹建成本对比", notes = "筹建成本对比")
    @PostMapping("comparePreparationCost")
    public Result comparePreparationCost(@RequestBody FinQueryDto queryDto) {
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginTime(),
                queryDto.getEndTime(), queryDto.getShopIdList()
        );
        List<FinCostCompareVo> list = preparationCostService.compareCost(queryDto);
        return ok(list);
    }

    /**
     * @description 筹建成本对比 导出Excel
     * @author fancl
     * @date 2020/2/11
     * @param
     * @return
     */
    @RequestMapping("exportCompareCost")
    public void exportCompareCost(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginTime(), excelExportDto.getEndTime(),
                excelExportDto.getType(), excelExportDto.getModelType(), excelExportDto.getMenuName());
        preparationCostService.exportCompareCost(excelExportDto, response);
    }

    @ApiOperation(value = "筹建成本明细", notes = "筹建成本明细")
    @PostMapping("preparationCostDetails")
    public Result preparationCostDetails(@RequestBody FinQueryDto queryDto) {
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginTime(),
                queryDto.getEndTime(), queryDto.getShopIdList()
                //,queryDto.getShopTypeIdList()
        );

        List<FinSubjectVo> list = preparationCostService.costDetail(queryDto);
        return ok(list);
    }

    /**
     * @description 筹建成本明细 导出Excel
     * @author fancl
     * @date 2020/2/11
     * @param
     * @return
     */
    @RequestMapping("exportCostDetail")
    public void exportCostDetail(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginTime(), excelExportDto.getEndTime(),
                excelExportDto.getType(), excelExportDto.getModelType(), excelExportDto.getMenuName());

        preparationCostService.exportCostDetail(excelExportDto, response);
    }


    @ApiOperation(value = "筹建投资回报", notes = "筹建投资回报")
    @PostMapping("preparationInvest")
    public Result preparationInvest(@RequestBody FinQueryDto queryDto) {
        FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginTime(),
                queryDto.getEndTime(), queryDto.getShopIdList());

        List<FinPrepaInvestVo> list = preparationCostService.prepaInvest(queryDto);
        return ok(list);
    }

    /**
     * @description 筹建投资回报导出Excel
     * @author fancl
     * @date 2020/2/11
     * @param
     * @return
     */
    @RequestMapping("exportExcelInvest")
    public void exportExcelInvest(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginTime(), excelExportDto.getEndTime(),
                excelExportDto.getType(), excelExportDto.getModelType(), excelExportDto.getMenuName());

        preparationCostService.exportInvest(excelExportDto, response);
    }


}

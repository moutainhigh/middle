package com.njwd.reportdata.controller;

import com.njwd.common.ScmConstant;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.ScmQueryDto;
import com.njwd.entity.reportdata.vo.scm.DishGrossProfitVo;
import com.njwd.reportdata.service.FootStyleAnalysisService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 菜品分析
 * @Author fancl
 * @Date 2019/11/20
 */
@Api(value = "footStyleAnalysisController", tags = "菜品分析")
@RestController
@RequestMapping("footStyleAnalysis")
public class FootStyleAnalysisController extends BaseController {
    @Autowired
    FootStyleAnalysisService footStyleAnalysisService;

    /**
     * @description 菜品毛利分析
     * @author fancl
     * @date 2020/3/2
     * @param
     * @return
     */
    @PostMapping("foodGrossProfit")
    public Result<List<DishGrossProfitVo>> foodGrossProfit(@RequestBody ScmQueryDto queryDto) {
        //校验
        FastUtils.checkParams(queryDto, queryDto.getEnteId(),
                queryDto.getBeginDate(), queryDto.getEndDate(),
                queryDto.getShopIdList(), queryDto.getChainType());
        //本期
        if (ScmConstant.ChainType.CURRENT.equals(queryDto.getChainType())) {
            List<DishGrossProfitVo> foodGrossAnalysisVos = footStyleAnalysisService.getFoodGrossProfit(queryDto);
            return ok(foodGrossAnalysisVos);
        }
        //同比
        else if (ScmConstant.ChainType.THE_SAME.equals(queryDto.getChainType())) {
            List<DishGrossProfitVo> theSame = footStyleAnalysisService.getTheSame(queryDto);
            return ok(theSame);
        }
        //环比
        else if (ScmConstant.ChainType.CHAIN.equals(queryDto.getChainType())) {
            List<DishGrossProfitVo> chain = footStyleAnalysisService.getChain(queryDto);
            return ok(chain);
        }
        //测试公共接口
        //List<DishGrossProfitVo> foodGrossAnalysisVos = footStyleAnalysisService.getDishBaseList(queryDto,null);
        return null;
    }


    //导出菜品毛利
    @PostMapping("exportFoodGrossProfit")
    public void exportFoodGrossProfit(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginDate(), excelExportDto.getEndDate(),
                excelExportDto.getModelType(), excelExportDto.getMenuName());
        footStyleAnalysisService.exportFoodGrossProfit(excelExportDto, response);
    }

    //导出菜品毛利 同比
    @PostMapping("exportFoodGrossProfitTheSame")
    public void exportFoodGrossProfitTheSame(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginDate(), excelExportDto.getEndDate(),
                excelExportDto.getModelType(), excelExportDto.getMenuName());
        footStyleAnalysisService.exportFoodGrossProfitTheSame(excelExportDto, response);
    }


    //导出菜品毛利 环比
    @PostMapping("exportFoodGrossProfitChain")
    public void exportFoodGrossProfitChain(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto, excelExportDto.getEnteId(),
                excelExportDto.getBeginDate(), excelExportDto.getEndDate(),
                excelExportDto.getModelType(), excelExportDto.getMenuName());
        footStyleAnalysisService.exportFoodGrossProfitChain(excelExportDto, response);
    }


}

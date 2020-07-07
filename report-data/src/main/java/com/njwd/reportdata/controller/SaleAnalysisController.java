package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.reportdata.service.SaleAnalysisService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description: 销售分析
 * @Author LuoY
 * @Date 2019/11/18
 */
@Api(value = "saleAnalysisController", tags = "销售分析")
@RestController
@RequestMapping("saleAnalysis")
public class SaleAnalysisController extends BaseController {
    @Resource
    private SaleAnalysisService saleAnalysisService;

    /**
     * @return com.njwd.support.Result<com.njwd.entity.reportdata.vo.PosOrderFoodVo>
     * @Author LuoY
     * @Description 根据条件查询退增统计表
     * @Date 201/18 17:32
     * @Param [posOrderFoodDto]
     **/
    @PostMapping("findRegressionReportByCondition")
    public Result<List<PosOrderFoodAnalysisVo>> findRegressionReportByCondition(@RequestBody PosOrderFoodAnalysisDto posOrderFoodAnalysisDto) {
        FastUtils.checkParams(posOrderFoodAnalysisDto.getEnteId());
        FastUtils.checkParams(posOrderFoodAnalysisDto.getBeginDate());
        FastUtils.checkParams(posOrderFoodAnalysisDto.getEndDate());
        List<PosOrderFoodAnalysisVo> posOrderFoodAnalysisVoList = saleAnalysisService.findPosOrderFoodByCondition(posOrderFoodAnalysisDto);
        return ok(posOrderFoodAnalysisVoList);
    }

    /**
     * 退增统计表 app
     *
     * @param: [posOrderFoodAnalysisDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.PosOrderFoodAnalysisVo>
     * @author: zhuzs
     * @date: 2019-12-24
     */
    @PostMapping("findRegressionReportApp")
    public Result<PosOrderFoodAnalysisVo> findRegressionReportApp(@RequestBody PosOrderFoodAnalysisDto posOrderFoodAnalysisDto) {
        FastUtils.checkParams(posOrderFoodAnalysisDto.getEnteId());
        FastUtils.checkParams(posOrderFoodAnalysisDto.getBeginDate());
        FastUtils.checkParams(posOrderFoodAnalysisDto.getEndDate());
        PosOrderFoodAnalysisVo posOrderFoodAnalysisVo = saleAnalysisService.findRegressionReportApp(posOrderFoodAnalysisDto);
        return ok(posOrderFoodAnalysisVo);
    }

    /**
     * @return com.njwd.support.Result<Map>
     * @Author shenhf
     * @Description 根据条件查询退菜明细
     * @Date 2019/11/21 13:32
     * @Param [PosRetreatDetailDto]
     **/
    @PostMapping("findRetreatDetail")
    public Result<Page<PosRetreatDetailVo>> findRetreatDetail(@RequestBody PosRetreatDetailDto posOrderFoodDto) {
        FastUtils.checkParams(posOrderFoodDto.getEnteId());
        FastUtils.checkParams(posOrderFoodDto.getBeginDate());
        FastUtils.checkParams(posOrderFoodDto.getEndDate());
        // 设置分页参数
        posOrderFoodDto.getPage().setCurrent(posOrderFoodDto.getPageNum());
        posOrderFoodDto.getPage().setSize(posOrderFoodDto.getPageSize());
        Page<PosRetreatDetailVo> posRetreatDetailVoList = saleAnalysisService.findRetreatDetail(posOrderFoodDto);
        return ok(posRetreatDetailVoList);
    }
    /**
     * @Author shenhf
     * @Description 导出退菜明细
     * @Date 2019/11/21 13:32
     * @param posOrderFoodDto, response]
     * @return void
     **/
    @RequestMapping("exportRetreatDetailExcel")
    public void exportRetreatDetailExcel(@RequestBody PosRetreatDetailDto posOrderFoodDto, HttpServletResponse response) {
        FastUtils.checkParams(posOrderFoodDto.getBeginDate(),posOrderFoodDto.getEndDate());
        posOrderFoodDto.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
        saleAnalysisService.exportRetreatDetailExcel(posOrderFoodDto,response);
    }

    /**
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.PosCashPayAnalysisVo>>
     * @Author LuoY
     * @Description 根据条件查询收款汇总分析表
     * @Date 2019/11/22 14:05
     * @Param [posCashPayDto]
     **/
    @PostMapping("findPayTypeReportByCondition")
    public Result<List<PosCashPayAnalysisVo>> findPayTypeReportByCondition(@RequestBody PosCashPayDto posCashPayDto) {
        FastUtils.checkParams(posCashPayDto.getEnteId());
        FastUtils.checkParams(posCashPayDto.getBeginDate());
        FastUtils.checkParams(posCashPayDto.getEndDate());
        List<PosCashPayAnalysisVo> posCashPayAnalysisVos = saleAnalysisService.findPayTypeReportByCondition(posCashPayDto);
        return ok(posCashPayAnalysisVos);
    }

   /**
    * @Description 导出收款汇总分析表
    * @Author 郑勇浩
    * @Data 2020/3/11 17:50
    * @Param [response, posCashPayDto]
    */
    @PostMapping("exportPayTypeReportByCondition")
    public void exportPayTypeReportByCondition(HttpServletResponse response,@RequestBody PosCashPayDto posCashPayDto) {
        FastUtils.checkParams(posCashPayDto.getBeginDate());
        FastUtils.checkParams(posCashPayDto.getEndDate());
        posCashPayDto.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
        saleAnalysisService.exportPayTypeReportByCondition(response,posCashPayDto);
    }

    /**
     * 收款汇总分析表 app端
     *
     * @param: [posCashPayDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.PosCashPayAnalysisVo>
     * @author: zhuzs
     * @date: 2019-12-20
     */
    @PostMapping("findPayTypeReportApp")
    public Result<PosCashPayAnalysisVo> findPayTypeReportApp(@RequestBody PosCashPayDto posCashPayDto) {
        FastUtils.checkParams(posCashPayDto.getEnteId());
        FastUtils.checkParams(posCashPayDto.getBeginDate());
        FastUtils.checkParams(posCashPayDto.getEndDate());
        PosCashPayAnalysisVo posCashPayAnalysisVo = saleAnalysisService.findPayTypeReportApp(posCashPayDto);
        return ok(posCashPayAnalysisVo);
    }

    /**
     * @return com.njwd.support.Result<List < PosPayCategoryVo>>
     * @Author shenhf
     * @Description 根据条件查询收款方式分析
     * @Date 2019/11/25 10:32
     * @Param [PosPayCategoryDto]
     **/
    @PostMapping("findPayCategoryReport")
    public Result<List<PosPayCategoryVo>> findPayCategoryReport(@RequestBody PosPayCategoryDto posPayCategoryDto) {
        FastUtils.checkParams(posPayCategoryDto.getEnteId());
        FastUtils.checkParams(posPayCategoryDto.getBeginDate());
        FastUtils.checkParams(posPayCategoryDto.getEndDate());
        List<PosPayCategoryVo> payCategoryVosList = saleAnalysisService.findPayCategoryReport(posPayCategoryDto);
        return ok(payCategoryVosList);
    }

    /**
     * @Description 导出收款方式分析
     * @Author 郑勇浩
     * @Data 2020/3/11 18:09
     * @Param [posPayCategoryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.PosPayCategoryVo>>
     */
    @PostMapping("exportPayCategoryReport")
    public void exportPayCategoryReport(HttpServletResponse response,@RequestBody PosPayCategoryDto posPayCategoryDto) {
        FastUtils.checkParams(posPayCategoryDto.getBeginDate(),posPayCategoryDto.getEndDate());
        posPayCategoryDto.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
        saleAnalysisService.exportPayCategoryReport(response,posPayCategoryDto);
    }

    /**
     * 收款方式分析表 app端
     *
     * @param: [posPayCategoryDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.PosPayCategoryVo>
     * @author: zhuzs
     * @date: 2019-12-20
     */
    @PostMapping("findPayCategoryReportApp")
    public Result<PosPayCategoryVo> findPayCategoryReportApp(@RequestBody PosPayCategoryDto posPayCategoryDto) {
        FastUtils.checkParams(posPayCategoryDto.getEnteId());
        FastUtils.checkParams(posPayCategoryDto.getBeginDate());
        FastUtils.checkParams(posPayCategoryDto.getEndDate());
        PosPayCategoryVo posPayCategoryVo = saleAnalysisService.findPayCategoryReportApp(posPayCategoryDto);
        return ok(posPayCategoryVo);
    }

    /**
     * @Description: 根据条件查询菜品销量分析表
     * @Param: [PosFoodSalesDto]
     * @return: com.njwd.support.Result<List < PosFoodSalesVo>>
     * @Author: shenhf
     * @Date: 2019/11/26 10:52
     */
    @PostMapping("findFoodSalesReport")
    public Result<Map> findFoodSalesReport(@RequestBody PosFoodSalesDto posFoodSalesDto) {
        FastUtils.checkParams(posFoodSalesDto.getEnteId());
        FastUtils.checkParams(posFoodSalesDto.getBeginDate());
        FastUtils.checkParams(posFoodSalesDto.getEndDate());
        Map map = saleAnalysisService.findFoodSalesReport(posFoodSalesDto);
        return ok(map);
    }
    /**
     * 菜品销量分析表 导出
     *
     * @param: [posFoodSalesDto, response]
     * @return: void
     * @author: shenhf
     * @date: 2020-03-02
     */
    @PostMapping("exportFoodSalesReport")
    public void exportFoodSalesReport(@RequestBody PosFoodSalesDto posFoodSalesDto, HttpServletResponse response) {
        FastUtils.checkParams(posFoodSalesDto.getBeginDate(), posFoodSalesDto.getEndDate());
        posFoodSalesDto.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
        saleAnalysisService.exportFoodSalesReport(posFoodSalesDto,response);
    }
    /**
     * @return com.njwd.support.Result<com.njwd.entity.reportdata.vo.SalesStatisticsVo>
     * @Author LuoY
     * @Description 销售情况统计表
     * @Date 2019/12/3 15:42
     * @Param [salesStatisticsDto]
     **/
    @PostMapping("findSalesStatisticsReport")
    public Result<SalesStatisticsVo> findSalesStatisticsReport(@RequestBody SalesStatisticsDto salesStatisticsDto) {
        FastUtils.checkNullOrEmpty(salesStatisticsDto.getShopIdList(),salesStatisticsDto.getBeginDate(),salesStatisticsDto.getBeginDate());
        return ok(saleAnalysisService.findSalesStatisticsReport(salesStatisticsDto));
    }

    /**
    * @Description: 销售情况统计表手机端
    * @Param: [salesStatisticsDto]
    * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.SalesStatisticsVo>
    * @Author: LuoY
    * @Date: 2020/3/4 10:50
    */
    @PostMapping("findSalesStatisticsReportPhone")
    public Result<List<SaleStatisticsPhoneVo>> findSalesStatisticsReportPhone(@RequestBody SalesStatisticsDto salesStatisticsDto) {
        FastUtils.checkNullOrEmpty(salesStatisticsDto.getShopIdList(),salesStatisticsDto.getBeginDate(),salesStatisticsDto.getBeginDate());
        return ok(saleAnalysisService.findSalesStatisticsPhoneReport(salesStatisticsDto));
    }

    /** 
    * @Description: 查询啤酒进场费
    * @Param: [bearIntoFactoryDto] 
    * @return: com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.BearIntoFactoryVo>> 
    * @Author: LuoY
    * @Date: 2020/3/27 11:43
    */ 
    @PostMapping("findBearIntoFactoryTable")
    public Result<List<BearIntoFactoryVo>> findBearIntoFactoryTable(@RequestBody BearIntoFactoryDto bearIntoFactoryDto){
        FastUtils.checkNullOrEmpty(bearIntoFactoryDto.getShopIdList(),bearIntoFactoryDto.getBeginDate(),bearIntoFactoryDto.getEndDate());
        return ok(saleAnalysisService.findBearIntoFactoryInfo(bearIntoFactoryDto));
    }

    /**
     * @return com.njwd.support.Result<java.util.List <com.njwd.entity.reportdata.vo.PosDiscountDetailPayVo>>
     * @Description //收入折扣分析表
     * @Author jds
     * @Date 2019/12/7 16:59
     * @Param [repPosDetailPayDto]
     **/
    @PostMapping("findDetailPayReport")
    public Result<List<PosDiscountDetailPayVo>> findDetailPayReport(@RequestBody RepPosDetailPayDto repPosDetailPayDto) {
        List<PosDiscountDetailPayVo> result = saleAnalysisService.findDetailPayReport(repPosDetailPayDto);
        return ok(result);
    }

    /**
     * @Description 导出收入折扣分析
     * @Author 郑勇浩
     * @Data 2020/3/12 10:28
     * @Param [repPosDetailPayDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.PosDiscountDetailPayVo>>
     */
    @PostMapping("exportDetailPayReport")
    public void exportDetailPayReport(HttpServletResponse response,@RequestBody RepPosDetailPayDto repPosDetailPayDto) {
        FastUtils.checkParams(repPosDetailPayDto.getBeginDate(),repPosDetailPayDto.getEndDate());
        repPosDetailPayDto.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
        saleAnalysisService.exportDetailPayReport(response,repPosDetailPayDto);
    }

    /**
     * 退菜统计表 导出
     *
     * @param: [posOrderFoodAnalysisDto, response]
     * @return: void
     * @author: zhuzs
     * @date: 2020-01-07
     */
    @PostMapping("exportRegressionReport")
    public void exportRegressionReport(@RequestBody ExcelExportDto regressionDto, HttpServletResponse response) {
        FastUtils.checkParams(regressionDto.getBeginDate(),regressionDto.getEndDate());
        regressionDto.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
        saleAnalysisService.exportRegressionReport(regressionDto,response);
    }
    
    /** 
    * @Description: 销售情况统计表导出
    * @Param: [regressionDto, response] 
    * @return: void 
    * @Author: LuoY
    * @Date: 2020/3/24 17:39
    */
    @PostMapping("exportSalesStatisticsReport")
    public void exportSalesStatisticsReport(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response){
        FastUtils.checkParams(excelExportDto.getBeginDate(),excelExportDto.getEndDate());
        saleAnalysisService.exportSalesStatisticsReport(excelExportDto,response);
    }

    /**
     * @Description: 啤酒进场费导出
     * @Param: [bearIntoFactoryDto]
     * @return: com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.BearIntoFactoryVo>>
     * @Author: LuoY
     * @Date: 2020/3/27 11:43
     */
    @PostMapping("exportBearIntoFactory")
    public void exportBearIntoFactory(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response){
        saleAnalysisService.exportBearIntoFactory(excelExportDto,response);
    }
}

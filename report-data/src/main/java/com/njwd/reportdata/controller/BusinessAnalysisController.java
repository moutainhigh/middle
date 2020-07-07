package com.njwd.reportdata.controller;

import com.njwd.annotation.NoLogin;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.reportdata.service.BusinessAnalysisService;
import com.njwd.reportdata.service.GrossProfitService;
import com.njwd.reportdata.service.MarketingGrossProfitService;
import com.njwd.reportdata.service.RealTimeProfitService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * @Description: 经营分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "businessAnalysisController", tags = "经营分析")
@RestController
@RequestMapping("businessAnalysis")
public class BusinessAnalysisController extends BaseController {

    @Resource
    private BusinessAnalysisService businessAnalysisService;

    @Resource
    private MarketingGrossProfitService marketingGrossProfitService;

    @Resource
    private RealTimeProfitService realTimeProfitService;

    @Resource
    private GrossProfitService grossProfitService;

    /**
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.ShopScoreVo>>
     * @Description //门店评分对比表
     * @Author jds
     * @Date 2019/11/19 19:47
     * @Param [shopScoreDto]
     **/
    @RequestMapping("findScoreContrast")
    public Result<ShopScoreVo> findScoreContrast(@RequestBody ShopScoreDto shopScoreDto) {
        FastUtils.checkParams(shopScoreDto.getDateType(), shopScoreDto.getBeginDate(), shopScoreDto.getEndDate(), shopScoreDto.getEnteId());
        ShopScoreVo result = businessAnalysisService.findScoreContrast(shopScoreDto);
        return ok(result);
    }

    /**
     * @param excelExportDto
     * @return
     * @Description 门店评分对比表导出
     * @Author ljc
     * @Date 2019/3/3
     */
    @ApiOperation(value = "门店评分对比表导出", notes = "门店评分对比表导出")
    @RequestMapping("doDealExportScoreContrast")
    @ResponseBody
    public void doDealExportScoreContrast(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        try {
            FastUtils.checkParams(excelExportDto.getBeginDate(), excelExportDto.getEndDate(), excelExportDto.getEnteId());
            businessAnalysisService.exportScoreContrast(response, excelExportDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.ShopScoreVo>>
     * @Description //门店评分汇总表
     * @Author jds
     * @Date 2019/11/20 17:46
     * @Param [shopScoreDto]
     **/
    @ApiOperation(value = "门店评分汇总表", notes = "门店评分汇总表")
    @RequestMapping("findScoreSummary")
    public Result<ShopScoreVo> findScoreSummary(@RequestBody ShopScoreDto shopScoreDto) {
        FastUtils.checkParams(shopScoreDto.getBeginDate(), shopScoreDto.getEndDate(), shopScoreDto.getEnteId());
        ShopScoreVo result = businessAnalysisService.findScoreSummary(shopScoreDto);
        return ok(result);
    }

    /**
     * @param excelExportDto
     * @return
     * @Description 门店评分汇总表导出
     * @Author ljc
     * @Date 2019/3/3
     */
    @ApiOperation(value = "门店评分汇总表导出", notes = "门店评分汇总表导出")
    @RequestMapping("doDealExportScoreSummary")
    @ResponseBody
    public void doDealExportScoreSummary(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        try {
            FastUtils.checkParams(excelExportDto.getBeginDate(), excelExportDto.getEndDate(), excelExportDto.getEnteId());
            businessAnalysisService.exportScoreSummary(response, excelExportDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 经营日报表
     * @Param: [businessReportDayDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.BusinessReportDayVo>
     * @Author: LuoY
     * @Date: 2019/12/29 11:37
     */
    @ApiOperation(value = "经营日报表", notes = "经营日报表")
    @RequestMapping("findBusinessReportDay")
    public Result<List<BusinessReportDayItemVo>> findBusinessReportDay(@RequestBody BusinessReportDayDto businessReportDayDto) {
        FastUtils.checkNull(businessReportDayDto.getBeginDate(), businessReportDayDto.getEndDate(), businessReportDayDto.getShopIdList());
        List<BusinessReportDayItemVo> businessReportDayItemVos = businessAnalysisService.findBusinessReportDay(businessReportDayDto, ReportDataConstant.ReportItemReportId.BUSINESSREPORTDAY);
        return ok(businessReportDayItemVos);
    }


    /**
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.StatisticsTurnoverRateVo>>
     * @Author ZhuHC
     * @Date 2019/12/30 16:47
     * @Param [baseQueryDto]
     * @Description 翻台率统计表
     */
    @ApiOperation(value = "翻台率统计表", notes = "翻台率统计表")
    @RequestMapping("findStatisticsTurnoverRate")
    public Result<List<StatisticsTurnoverRateVo>> findStatisticsTurnoverRate(@RequestBody BaseQueryDto baseQueryDto) {
        FastUtils.checkParams(baseQueryDto.getBeginDate());
        FastUtils.checkParams(baseQueryDto.getEndDate());
        return ok(businessAnalysisService.findStatisticsTurnoverRate(baseQueryDto));
    }

    /**
     * @Description: 实时利润分析
     * @Param: [realTimProfitDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.RealTimeProfitVo>
     * @Author: liBao
     * @Date: 2020/2/19 11:37
     */
    @ApiOperation(value = "实时利润分析", notes = "实时利润分析")
    @RequestMapping("findRealTimeProfit")
    public Result<List<RealTimeProfitVo>> findRealTimeProfit(@RequestBody RealTimeProfitDto realTimProfitDto) {
        FastUtils.checkNull(realTimProfitDto.getBeginTime(), realTimProfitDto.getEndTime(), realTimProfitDto.getShopIdList());
        List<RealTimeProfitVo> realTimeProfitVos = realTimeProfitService.findRealTimeProfit(realTimProfitDto);
        return ok(realTimeProfitVos);
    }


    /**
     * @Description: 实时利润分析（通用）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.RealTimeProfitVo>
     * @Author: liBao
     * @Date: 2020/2/19 11:37
     */
    @ApiOperation(value = "实时利润分析（通用）", notes = "实时利润分析（通用）")
    @RequestMapping("findRealTimeProfitMain")
    public Result<List<RealTimeProfitVo>> findRealTimeProfitMain(@RequestBody RealTimeProfitDto realTimProfitDto) throws ParseException {
        FastUtils.checkNull(realTimProfitDto.getBeginTime(), realTimProfitDto.getEndTime(), realTimProfitDto.getShopIdList());
        List<RealTimeProfitVo> realTimeProfitVos = realTimeProfitService.findRealTimeProfitMain(realTimProfitDto);
        return ok(realTimeProfitVos);
    }


    /**
     * @Description: 实时利润分析（毛利率）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.RealTimeProfitVo>
     * @Author: liBao
     * @Date: 2020/2/19 11:37
     */
    @ApiOperation(value = "实时利润分析（毛利率）", notes = "实时利润分析（毛利率）")
    @RequestMapping("getGrossMargin")
    public Result<RealTimeProfitVo> getGrossMargin(@RequestBody RealTimeProfitDto realTimProfitDto){
        FastUtils.checkNull(realTimProfitDto.getBeginTime(), realTimProfitDto.getEndTime(), realTimProfitDto.getShopIdList());
        RealTimeProfitVo vo = realTimeProfitService.getGrossMargin(realTimProfitDto);
        return ok(vo);
    }

    /**
     * @Description: 实时利润分析(同比、环比)
     * @Param: [realTimProfitDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.RealTimeProfitVo>
     * @Author: liBao
     * @Date: 2020/2/19 11:37
     */
    @ApiOperation(value = "实时利润分析同比环比", notes = "实时利润分析同比环比")
    @RequestMapping("findRealTimeProfitSameOrChainCompare")
    public Result<List<RealTimeProfitVo>> findRealTimeProfitSameOrChainCompare(@RequestBody RealTimeProfitDto realTimProfitDto) {
        FastUtils.checkNull(realTimProfitDto.getBeginTime(), realTimProfitDto.getEndTime(), realTimProfitDto.getShopIdList());
        List<RealTimeProfitVo> realTimeProfitVos = realTimeProfitService.findRealTimeProfitSameOrChainCompare(realTimProfitDto, realTimProfitDto.getDataType());
        return ok(realTimeProfitVos);
    }

    /**
     * @Description: 实时利润分析(预算比)
     * @Param: [realTimProfitDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.RealTimeProfitVo>
     * @Author: liBao
     * @Date: 2020/2/19 11:37
     */
    @ApiOperation(value = "实时利润分析预算比", notes = "实时利润分析预算比")
    @RequestMapping("findRealTimeProfitBudgetCompare")
    public Result<List<RealTimeProfitVo>> findRealTimeProfitBudgetCompare(@RequestBody RealTimeProfitDto realTimProfitDto) {
        FastUtils.checkNull(realTimProfitDto.getBeginTime(), realTimProfitDto.getEndTime(), realTimProfitDto.getShopIdList());
        List<RealTimeProfitVo> realTimeProfitVos = realTimeProfitService.findRealTimeProfitBudgetCompare(realTimProfitDto, realTimProfitDto.getDataType());
        return ok(realTimeProfitVos);
    }

    /**
     * @Description: 实时利润分析导出
     * @Param: [realTimProfitDto]
     * @return: void
     * @Author: liBao
     * @Date: 2020/2/19 11:37
     */
    @RequestMapping("exportRealTimeProfit")
    public void exportRealTimeProfit(@RequestBody ExcelExportDto finQueryDto, HttpServletResponse response) {
        FastUtils.checkNull(finQueryDto.getBeginDate(), finQueryDto.getEndDate(), finQueryDto.getShopIdList());
        realTimeProfitService.exportRealTimeProfit(finQueryDto, response);
    }

    /**
     * @Description: 实时利润分析同比环比导出
     * @Param: [realTimProfitDto]
     * @return: void
     * @Author: liBao
     * @Date: 2020/2/19 11:37
     */
    @RequestMapping("exportRealTimeProfitSameCompare")
    public void exportRealTimeProfitSameCompare(@RequestBody ExcelExportDto finQueryDto, HttpServletResponse response) {
        FastUtils.checkParams(finQueryDto.getEnteId());
        realTimeProfitService.exportRealTimeProfitSameCompare(finQueryDto, response);
    }


    /**
     * @Description: 实时利润分析预算导出
     * @Param: [realTimProfitDto]
     * @return: void
     * @Author: liBao
     * @Date: 2020/2/19 11:37
     */
    @RequestMapping("exportRealTimeProfitBudget")
    public void exportRealTimeProfitBudget(@RequestBody ExcelExportDto finQueryDto, HttpServletResponse response) {
        FastUtils.checkParams(finQueryDto.getEnteId());
        realTimeProfitService.exportRealTimeProfitBudget(finQueryDto, response);
    }

    /**
     * @Description: 毛利分析表
     * @Param: [queryDto]
     * @return: GrossProfitAnalysisVo
     * @Author: 周鹏
     * @Date: 2020/2/11 11:37
     */
    @ApiOperation(value = "毛利分析表", notes = "毛利分析表")
    @PostMapping("findGrossProfit")
    public Result<List<GrossProfitVo>> findGrossProfit(@RequestBody GrossProfitDto queryDto) {
        FastUtils.checkParams(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getShopIdList(), queryDto.getShopTypeIdList());
        List<GrossProfitVo> list = grossProfitService.findGrossProfit(queryDto);
        return ok(list);
    }

    /**
     * 营销活动毛利统计表
     *
     * @param queryDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.GrossProfitVo>>
     * @Author lj
     * @Date:11:12 2020/3/26
     **/
    @ApiOperation(value = "营销活动毛利统计表", notes = "营销活动毛利统计表")
    @PostMapping("findMarketingGrossProfit")
    public Result<List<MarketingGrossProfitVo>> findMarketingGrossProfit(@RequestBody MarketingGrossProfitDto queryDto) {
        FastUtils.checkParams(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getShopIdList());
        List<MarketingGrossProfitVo> list = marketingGrossProfitService.findMarketingGrossProfit(queryDto);
        return ok(list);
    }

    /**
     * 营销活动毛利统计表
     *
     * @param queryDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.GrossProfitVo>>
     * @Author lj
     * @Date:11:12 2020/3/26
     **/
    @ApiOperation(value = "营销活动毛利统计表", notes = "营销活动毛利统计表")
    @PostMapping("findMarketingGrossProfitNew")
    public Result<List<MarketingGrossProfitVo>> findMarketingGrossProfitNew(@RequestBody MarketingGrossProfitDto queryDto) {
        FastUtils.checkParams(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getShopIdList());
        List<MarketingGrossProfitVo> list = marketingGrossProfitService.findMarketingGrossProfitNew(queryDto);
        return ok(list);
    }

    /**
     * 营销活动毛利统计表导出
     *
     * @param excelExportDto, response]
     * @return void
     * @Author lj
     * @Date:11:03 2020/3/31
     **/
    @ApiOperation(value = "营销活动毛利统计表导出", notes = "营销活动毛利统计表导出")
    @RequestMapping("exportMarketingGrossProfit")
    public void exportMarketingGrossProfit(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        marketingGrossProfitService.exportMarketingGrossProfit(excelExportDto, response);
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/3/2 16:33
     * @Param [baseQueryDto, response]
     * @Description
     */
    @ApiOperation(value = "翻台率统计表导出", notes = "翻台率统计表导出")
    @RequestMapping("exportStatisticsExcel")
    public void exportStatisticsExcel(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkParams(excelExportDto.getBeginDate());
        FastUtils.checkParams(excelExportDto.getEndDate());
        businessAnalysisService.exportStatisticsExcel(excelExportDto, response);
    }

    @ApiOperation(value = "经营日报表导出", notes = "经营日报表导出")
    @RequestMapping("exportBusinessReportDay")
    public void exportBusinessReportDay(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        FastUtils.checkParams(excelExportDto.getBeginDate());
        FastUtils.checkParams(excelExportDto.getEndDate());
        businessAnalysisService.exportBusinessReportDay(response, excelExportDto);
    }

    /**
     * @Description: 毛利分析表
     * @Param: [queryDto]
     * @return: GrossProfitAnalysisVo
     * @Author: 周鹏
     * @Date: 2020/04/03
     */
    @ApiOperation(value = "毛利分析表导出", notes = "毛利分析表导出")
    @PostMapping("exportGrossProfit")
    public void exportGrossProfit(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto.getEnteId(), excelExportDto.getBeginDate(), excelExportDto.getEndDate(),
                excelExportDto.getShopIdList(), excelExportDto.getShopTypeIdList(),
                excelExportDto.getType(), excelExportDto.getModelType(), excelExportDto.getMenuName());
        grossProfitService.exportGrossProfit(excelExportDto, response);
    }

    /**
     * @Description: 查询实时利润分析（本年、同比、环比、预算比）
     * @Param: realTimProfitDto
     * @return: RealTimeProfitVo
     * @Author: 周鹏
     * @Date: 2020/04/27
     */
    @ApiOperation(value = "查询实时利润分析（本年、同比、环比、预算比）", notes = "查询实时利润分析（本年、同比、环比、预算比）")
    @PostMapping("findRealTimeProfitAnalysis")
    public Result<List<RealTimeProfitVo>> findRealTimeProfitAnalysis(@RequestBody RealTimeProfitDto realTimProfitDto) {
        FastUtils.checkNull(realTimProfitDto.getBeginDate(), realTimProfitDto.getEndDate(), realTimProfitDto.getDateType(),
                realTimProfitDto.getDataType(), realTimProfitDto.getShopIdList());
        List<RealTimeProfitVo> realTimeProfitVos = realTimeProfitService.findRealTimeProfitAnalysis(realTimProfitDto);
        return ok(realTimeProfitVos);
    }
}

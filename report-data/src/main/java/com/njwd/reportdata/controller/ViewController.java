package com.njwd.reportdata.controller;

import com.njwd.entity.reportdata.ViewManager;
import com.njwd.entity.reportdata.dto.RealTimeProfitDto;
import com.njwd.entity.reportdata.dto.ViewBossQueryDto;
import com.njwd.entity.reportdata.dto.ViewManagerQueryDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.reportdata.service.ViewService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 看板
 *
 * @author zhuzs
 * @date 2019-12-25 18:00
 */
@Api(value = "ViewController", tags = "看板")
@RestController
@RequestMapping("ViewController")
public class ViewController extends BaseController {
    @Autowired
    private ViewService viewService;

    /**
     * （店长视角）营业概述
     *
     * @param: [viewManagerDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.ViewManager>
     * @author: zhuzs
     * @date: 2019-12-27
     */
    @ApiOperation(value = "（店长视角）营业概述", notes = "（店长视角）营业概述")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopIdList",value = "店铺ID 集合",required = false,dataType = "Query"),
            @ApiImplicitParam(name = "enteId",value = "企业ID",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "beginDate",value = "查询 开时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "endDate",value = "查询 结束时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "dateType",value = "时间类型",required = true,dataType = "Query")
    })
    @PostMapping("findViewManagerBusiness")
    public Result<List<ViewManager>> findViewManagerBusiness(@RequestBody ViewManagerQueryDto viewManagerDto) {
        FastUtils.checkParams(viewManagerDto.getEnteId());
        FastUtils.checkParams(viewManagerDto.getBeginDate());
        FastUtils.checkParams(viewManagerDto.getEndDate());
        viewManagerDto.setUserId(getCurrLoginUserInfo().getId().toString());
        viewManagerDto.setUserName(getCurrLoginUserInfo().getName());
        return ok(viewService.findViewManagerBusiness(viewManagerDto));
    }
    /**
     * （店长视角）线形图分析
     *
     * @param: [viewManagerDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.ViewManagerBusinessVo>
     * @author: zhuzs
     * @date: 2019-12-27
     */
    @ApiOperation(value = "（店长视角）线形图分析", notes = "（店长视角）线形图分析")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopIdList",value = "店铺ID 集合",required = false,dataType = "Query"),
            @ApiImplicitParam(name = "beginDate",value = "查询 开时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "endDate",value = "查询 结束时间",required = true,dataType = "Query"),
    })
    @PostMapping("findViewManagerLineGraph")
    public Result<List<ViewManagerLineGraphVo>> findViewManagerLineGraph(@RequestBody ViewManagerQueryDto viewManagerDto) {
        FastUtils.checkParams(viewManagerDto.getEnteId());
        FastUtils.checkParams(viewManagerDto.getBeginDate());
        FastUtils.checkParams(viewManagerDto.getEndDate());
        List<ViewManagerLineGraphVo> viewManagerLineGraphVos = null;
        try {
             viewManagerLineGraphVos=   viewService.findViewManagerLineGraph(viewManagerDto);
        }catch (Exception e){

        }
        return ok(viewManagerLineGraphVos);
    }

    /**
     * （店长视角）指标完成情况
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.ViewManagerIndicatorRate>>
     * @author: zhuzs
     * @date: 2020-01-11
     */
    @ApiOperation(value = "店长视角）指标完成情况", notes = "(店长视角）指标完成情况")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopIdList",value = "店铺ID 集合",required = false,dataType = "Query"),
            @ApiImplicitParam(name = "beginDate",value = "查询 开时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "endDate",value = "查询 结束时间",required = true,dataType = "Query"),
    })
    @PostMapping("findViewManagerTarget")
    public Result<List<ViewShopIndexVo> > findViewManagerTarget(@RequestBody ViewManagerQueryDto viewManagerDto) {
        FastUtils.checkParams(viewManagerDto.getEnteId());
        FastUtils.checkParams(viewManagerDto.getBeginDate());
        FastUtils.checkParams(viewManagerDto.getEndDate());
        viewManagerDto.setUserId(getCurrLoginUserInfo().getId().toString());
        viewManagerDto.setUserName(getCurrLoginUserInfo().getName());
        return ok(viewService.findViewManagerTarget(viewManagerDto));
    }

    /**
     * （老板视角）客流量趋势
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.PassengerFlowTrendVo>>
     * @author: shenhf
     * @date: 2020-04-02
     */
    @ApiOperation(value = "老板视角）企业概况", notes = "（老板视角）企业概况")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate",value = "查询 开时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "endDate",value = "查询 结束时间",required = true,dataType = "Query"),
    })
    @PostMapping("findViewBossEnte")
    public Result<ViewBossEnteVo>  findViewBossEnte(@RequestBody ViewBossQueryDto viewBossQueryDto) {
        FastUtils.checkParams(viewBossQueryDto.getEnteId());
        FastUtils.checkParams(viewBossQueryDto.getBeginDate());
        FastUtils.checkParams(viewBossQueryDto.getEndDate());
        return ok(viewService.findViewBossEnte(viewBossQueryDto));
    }
    /**
     * （老板视角）营业概述
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.ViewBossShopSalesVo>>
     * @author: zhuzs
     * @date: 2020-01-10
     */
    @ApiOperation(value = "（老板视角）营业概述", notes = "（老板视角）营业概述")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate",value = "查询 开时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "endDate",value = "查询 结束时间",required = true,dataType = "Query"),
    })
    @PostMapping("findViewBossBusiness")
    public Result<List<ViewBossShopSalesVo>> findViewBossBusiness(@RequestBody ViewBossQueryDto viewBossQueryDto) {
        FastUtils.checkParams(viewBossQueryDto.getEnteId());
        FastUtils.checkParams(viewBossQueryDto.getBeginDate());
        FastUtils.checkParams(viewBossQueryDto.getEndDate());
        return ok(viewService.findViewBossBusiness(viewBossQueryDto));
    }

    /**
     * （老板视角）月度菜品销量分类分析
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.ViewBossFoodVo>>
     * @author: zhuzs
     * @date: 2019-12-30
     */
    @ApiOperation(value = "（老板视角）月度菜品销量分类分析", notes = "（店长视角）月度菜品销量分类分析")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate",value = "查询 开时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "endDate",value = "查询 结束时间",required = true,dataType = "Query"),
    })
    @PostMapping("findViewBossDishesSalesMonth")
    public Result<List> findViewBossDishesSalesMonth(@RequestBody ViewBossQueryDto viewBossQueryDto) {
        FastUtils.checkParams(viewBossQueryDto.getEnteId());
        FastUtils.checkParams(viewBossQueryDto.getBeginDate());
        FastUtils.checkParams(viewBossQueryDto.getEndDate());
        return ok(viewService.findViewBossDishesSalesMonth(viewBossQueryDto));
    }

    /**
     * （老板视角）销售前五及后五
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.ViewManager>
     * @author: zhuzs
     * @date: 2019-12-27
     */
    @ApiOperation(value = "（老板视角）销售前五及后五", notes = "（老板视角）销售前五及后五")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate",value = "查询 开时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "endDate",value = "查询 结束时间",required = true,dataType = "Query"),
    })
    @PostMapping("findViewBossTopFiveAndLastFive")
    public Result<ViewBossBusinessVo> findViewBossTopFiveAndLastFive(@RequestBody ViewBossQueryDto viewBossQueryDto) {
        FastUtils.checkParams(viewBossQueryDto.getEnteId());
        FastUtils.checkParams(viewBossQueryDto.getBeginDate());
        FastUtils.checkParams(viewBossQueryDto.getEndDate());
        return ok(viewService.findViewBossTopFiveAndLastFive(viewBossQueryDto));
    }

    /**
     * （老板视角）月度销售额走势
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.ViewManager>
     * @author: zhuzs
     * @date: 2019-12-27
     */
    @ApiOperation(value = "（老板视角）月度销售额走势", notes = "（老板视角）月度销售额走势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate",value = "查询 开时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "endDate",value = "查询 结束时间",required = true,dataType = "Query"),
    })
    @PostMapping("findViewBossConsumeMonth")
    public Result<List<ViewBossShopSalesVo>> findViewBossConsumeMonth(@RequestBody ViewBossQueryDto viewBossQueryDto) {
        FastUtils.checkParams(viewBossQueryDto.getEnteId());
        FastUtils.checkParams(viewBossQueryDto.getBeginDate());
        FastUtils.checkParams(viewBossQueryDto.getEndDate());
        return ok(viewService.findViewBossConsumeMonth(viewBossQueryDto));
    }

    /**
     * （老板视角）客流量趋势
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.PassengerFlowTrendVo>>
     * @author: zhuzs
     * @date: 2020-01-08
     */
    @ApiOperation(value = "老板视角）客流量趋势", notes = "（老板视角）客流量趋势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate",value = "查询 开时间",required = true,dataType = "Query"),
            @ApiImplicitParam(name = "endDate",value = "查询 结束时间",required = true,dataType = "Query"),
    })
    @PostMapping("findViewBossPassengerFlowTrend")
    public Result<List<PassengerFlowTrendVo>> findViewBossPassengerFlowTrend(@RequestBody ViewBossQueryDto viewBossQueryDto) {
        FastUtils.checkParams(viewBossQueryDto.getEnteId());
        FastUtils.checkParams(viewBossQueryDto.getBeginDate());
        FastUtils.checkParams(viewBossQueryDto.getEndDate());
        return ok(viewService.findViewBossPassengerFlowTrend(viewBossQueryDto));
    }


	/**
	 * （老板视角）年度成本费用结构分析
	 *
	 * @param: [viewBossQueryDto]
	 * @return: com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.PassengerFlowTrendVo>>
	 * @author: zhuzs
	 * @date: 2020-01-08
	 */
	@ApiOperation(value = "老板视角）年度成本费用结构分析", notes = "（老板视角）年度成本费用结构分析")
	@PostMapping("findViewBossCostFlowTrend")
	public Result<Map<String, BigDecimal>> findViewBossCostChart(@RequestBody RealTimeProfitDto param) {
        FastUtils.checkNull(param.getBeginTime(), param.getEndTime(), param.getShopIdList());
        param.setBeginDate(DateUtils.parseDate(param.getBeginTime(),DateUtils.PATTERN_DAY));
        param.setEndDate(DateUtils.parseDate(param.getEndTime(),DateUtils.PATTERN_DAY));
		return ok(viewService.findViewBossCostChart(param));
	}

}


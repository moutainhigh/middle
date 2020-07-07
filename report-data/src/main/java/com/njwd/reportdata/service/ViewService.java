package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.ViewManager;
import com.njwd.entity.reportdata.dto.RealTimeProfitDto;
import com.njwd.entity.reportdata.dto.ViewBossQueryDto;
import com.njwd.entity.reportdata.dto.ViewManagerQueryDto;
import com.njwd.entity.reportdata.vo.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 看板
 *
 * @author zhuzs
 * @date 2019-12-25 19:04
 */
public interface ViewService {
// ================ 看板（店长视角）=====================

    /**
     * 获取看板（店长视角）营业概述
     *
     * @param: [viewManagerDto]
     * @return: com.njwd.entity.reportdata.ViewManager
     * @author: zhuzs
     * @date: 2019-12-26
     */
    List<ViewManager> findViewManagerBusiness(ViewManagerQueryDto viewManagerDto);

    /**
     * 看板（店长视角）线形图分析
     *
     * @param: [viewManagerDto]
     * @return: com.njwd.entity.reportdata.vo.ViewManagerLineGraphVo
     * @author: zhuzs
     * @date: 2019-12-27
     */
    List<ViewManagerLineGraphVo> findViewManagerLineGraph(ViewManagerQueryDto viewManagerDto) throws ParseException;

    /**
     * 看板（店长视角）TODO 指标完成情况 前五后五
     *
     * @param: [viewManagerDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewManagerIndicatorRate>
     * @author: zhuzs
     * @date: 2020-01-11
     */
    List<ViewShopIndexVo>  findViewManagerTarget(ViewManagerQueryDto viewManagerDto);



// ================ 看板（老板视角）=====================

    /**
     * 获取看板（老板视角）企业概况
     *
     * @param: [viewBossQueryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossEnteVo>
     * @author: shenhf
     * @date: 2020-01-10
     */
    ViewBossEnteVo findViewBossEnte(ViewBossQueryDto viewBossQueryDto);
    /**
     * 获取看板（老板视角）营业概述
     *
     * @param: [viewBossQueryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossShopSalesVo>
     * @author: zhuzs
     * @date: 2020-01-10
     */
    List<ViewBossShopSalesVo> findViewBossBusiness(ViewBossQueryDto viewBossQueryDto);

    /**
     * 获取看板（老板视角）月度菜品销量分类分析
     *
     * @param: [viewBossQueryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossFoodVo>
     * @author: zhuzs
     * @date: 2020-01-10
     */
    List findViewBossDishesSalesMonth(ViewBossQueryDto viewBossQueryDto);

    /**
     * 看板（老板视角）销售前五及后五
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.entity.reportdata.vo.ViewBossBusinessVo
     * @author: zhuzs
     * @date: 2019-12-29
     */
    ViewBossBusinessVo findViewBossTopFiveAndLastFive(ViewBossQueryDto viewBossQueryDto);

    /**
     * 看板（老板视角）月度销售额走势
     *
     * @param: [viewBossQueryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossBusinessVo>
     * @author: zhuzs
     * @date: 2019-12-29
     */
    List<ViewBossShopSalesVo> findViewBossConsumeMonth(ViewBossQueryDto viewBossQueryDto);

    /**
     * （老板视角）客流量趋势
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.entity.reportdata.vo.PassengerFlowTrendVo
     * @author: zhuzs
     * @date: 2020-01-08
     */
    List<PassengerFlowTrendVo> findViewBossPassengerFlowTrend(ViewBossQueryDto viewBossQueryDto);

    /**
     * @Description 老板视角）年度成本费用结构分析
     * @Author 郑勇浩
     * @Data 2020/4/17 16:17
     * @Param [viewBossQueryDto]
     * @return java.util.List
     */
    Map<String, BigDecimal> findViewBossCostChart(RealTimeProfitDto viewBossQueryDto);
    /*
    * 指标计算逻辑
    * */
    List<BusinessDailyIndicVo> dealWithIndic(ViewManagerQueryDto viewManagerDto,String flag);
}

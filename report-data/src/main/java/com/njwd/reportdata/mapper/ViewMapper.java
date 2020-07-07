package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.dto.ViewBossQueryDto;
import com.njwd.entity.reportdata.dto.ViewManagerQueryDto;
import com.njwd.entity.reportdata.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 看板
 *
 * @author zhuzs
 * @date 2019-12-26 10:55
 */
public interface ViewMapper {

// ================ 看板（店长视角）=====================
    /**
     * 获取 开台数,客流数,销售额,收款额 合计 （店长视角）
     *
     * @param: [viewManagerDto]
     * @return: com.njwd.entity.reportdata.vo.ViewManagerVo
     * @author: shenhf
     * @date: 2020-03-26
     */
    List<ViewManagerQueryVo> getViewManagerQuer(ViewManagerQueryDto viewManagerDto);

    /**
     * 看板（店长视角）线形图分析(按照日查询)
     *
     * @param: [viewManagerDto]
     * @return: com.njwd.entity.reportdata.vo.ViewManagerLineGraphVo
     * @author: zhuzs
     * @date: 2019-12-27
     */
    List<ViewManagerLineGraphVo> findViewManagerLineGraph(@Param("viewManagerDto")ViewManagerQueryDto viewManagerDto);

    /**
     * 获取 收入、客流量、开台数、折扣额
     *
     * @param: [viewManagerDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewManagerIndicatorRate>
     * @author: zhuzs
     * @date: 2020-01-11
     */
    List<ViewManagerIndicatorRateVo> selectConsumeAndPassengerFlowAndDeskCount(@Param("viewManagerDto")ViewManagerQueryDto viewManagerDto);
    /**
     * 获取 增送金额 合计 （店长视角 营业概况）
     *
     * @param: [viewManagerDto]
     * @return: com.njwd.entity.reportdata.vo.ViewManagerGiveVo
     * @author: shenhf
     * @date: 2020-03-26
     */
    ViewManagerGiveVo getViewManagerPosGive(ViewManagerQueryDto viewManagerDto);
    /**
     * 获取 折扣 合计 （店长视角）
     *
     * @param: [viewManagerDto]
     * @return: HashMap
     * @author: shenhf
     * @date: 2020-03-26
     */
    HashMap getViewManagerDiscount(ViewManagerQueryDto viewManagerDto);
    /**
     * 获取 主表折扣合计 （店长视角）
     *
     * @param: [viewManagerDto]
     * @return: HashMap
     * @author: shenhf
     * @date: 2020-03-26
     */
    HashMap getViewManagerZhuDiscount(ViewManagerQueryDto viewManagerDto);

    /**
     * 获取 折扣 合计 （店长视角指标）
     *
     * @param: [viewManagerDto]
     * @return: HashMap
     * @author: shenhf
     * @date: 2020-03-26
     */
    List<HashMap> getViewManagerDiscountShop(ViewManagerQueryDto viewManagerDto);
    /**
     * 获取 主表折扣合计 （店长视角指标）
     *
     * @param: [viewManagerDto]
     * @return: HashMap
     * @author: shenhf
     * @date: 2020-03-26
     */
    List<HashMap> getViewManagerZhuDiscountShop(ViewManagerQueryDto viewManagerDto);
    /**
     * 获取 在职，离职人数合计 （店长视角）
     *
     * @param: [viewManagerDto]
     * @return: com.njwd.entity.reportdata.vo.ViewManagerUserVo
     * @author: shenhf
     * @date: 2020-03-26
     */
    ViewManagerUserVo getViewManagerUser(ViewManagerQueryDto viewManagerDto);
// ================ 看板（老板视角）=====================

    /**
     * 查询 客流量、消费总额、人均消费 （老板视角）
     *
     * @param: [viewBossQueryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossShopSalesVo>
     * @author: zhuzs
     * @date: 2020-01-10
     */
    List<ViewBossShopSalesVo> selectViewBossBusiness(@Param("viewBossQueryDto")ViewBossQueryDto viewBossQueryDto);

    /**
     * 查询 当前企业 在营业门店数
     *
     * @param: [viewBossQueryDto]
     * @return: java.lang.Integer
     * @author: zhuzs
     * @date: 2020-01-09
     */
    Integer selectShopCount(@Param("viewBossQueryDto")ViewBossQueryDto viewBossQueryDto);

    /**
     * 获取看板（老板视角）月度菜品销量分类分析
     *
     * @param: [viewBossQueryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossFoodVo>
     * @author: zhuzs
     * @date: 2020-01-10
     */
    List<ViewBossFoodVo> selectViewBossDishesSalesMonth(ViewBossQueryDto viewBossQueryDto);

    /**
     * 看板（老板视角）销售前五及后五
     *
     * @param: [viewBossQueryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossBusinessVo>
     * @author: zhuzs
     * @date: 2019-12-29
     */
    List<ViewBossShopSalesVo> selectViewBossTopFiveAndLastFive(@Param("viewBossQueryDto")ViewBossQueryDto viewBossQueryDto);

    /**
     * 看板（老板视角）月度销售额走势
     *
     * @param: [viewBossQueryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossBusinessVo>
     * @author: zhuzs
     * @date: 2019-12-29
     */
    List<ViewBossShopSalesVo> selectViewBossConsumeMonth(@Param("viewBossQueryDto") ViewBossQueryDto viewBossQueryDtoo);

    /**
     * 看板 （老板视角）客流量趋势
     *
     * @param: [viewBossQueryDto]
     * @return: com.njwd.entity.reportdata.vo.PassengerFlowTrendVo
     * @author: zhuzs
     * @date: 2020-01-08
     */
    List<PassengerFlowTrendVo> findViewBossPassengerFlowTrend(@Param("viewBossQueryDto") ViewBossQueryDto viewBossQueryDto);
    /**
     * 看板 （老板视角）营业概况中在职人数
     *
     * @param: [viewBossQueryDto]
     * @return: Integer
     * @author: shenhf
     * @date: 2020-01-08
     */
    Integer findViewBossUserNum(ViewBossQueryDto viewBossQueryDto);
    /**
     * 看板 （老板视角）营业概况中在职人数
     *
     * @param: [viewBossQueryDto]
     * @return: Integer
     * @author: shenhf
     * @date: 2020-01-08
     */
    Integer findViewBossIncomeAmount(ViewBossQueryDto viewBossQueryDto);
    /**
     * 看板 （老板视角）获取会员数量
     *
     * @param: [viewBossQueryDto]
     * @return: Integer
     * @author: shenhf
     * @date: 2020-01-08
     */
    Integer findViewBossMemberNum(ViewBossQueryDto viewBossQueryDto);
    /**
     * 看板 （老板视角）获取会员充值消费金额
     *
     * @param: [viewBossQueryDto]
     * @return: ViewBossEnteVo
     * @author: shenhf
     * @date: 2020-01-08
     */
    ViewBossEnteVo findViewBossMemberAmount(ViewBossQueryDto viewBossQueryDto);
    /**
     * 看板 （老板视角）获取供应商数量
     *
     * @param: [viewBossQueryDto]
     * @return: Integer
     * @author: shenhf
     * @date: 2020-01-08
     */
    Integer findViewBossSupplierNum(ViewBossQueryDto viewBossQueryDto);
    /*
    * 获取查询条件中所属品牌
    * */
    List<String> getBrandList(ViewManagerQueryDto viewManagerDto);
}

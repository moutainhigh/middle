package com.njwd.reportdata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description: 订单菜品
 * @Author LuoY
 * @Date 2019/11/19
 */
public interface SaleAnalysisService {

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.PosOrderFoodVo>
     * @Author LuoY
     * @Description 根据条件查询订单菜品退增明细
     * @Date 2019/11/19 11:15
     * @Param [posOrderFoodDto]
     **/
    List<PosOrderFoodAnalysisVo> findPosOrderFoodByCondition(PosOrderFoodAnalysisDto posOrderFoodAnalysisDto);

    /**
     * 退菜统计表 app
     *
     * @param: [posOrderFoodAnalysisDto]
     * @return: com.njwd.entity.reportdata.vo.PosOrderFoodAnalysisVo
     * @author: zhuzs
     * @date: 2019-12-23
     */
    PosOrderFoodAnalysisVo findRegressionReportApp(PosOrderFoodAnalysisDto posOrderFoodAnalysisDto);

    /**
     * @Description: 根据条件查询退单菜品明细
     * @Param: [posRetreatDetailDto]
     * @return: List<PosRetreatDetailVo>
     * @Author: shenhf
     * @Date: 2019/11/21 14:16
     */
    Page<PosRetreatDetailVo> findRetreatDetail(PosRetreatDetailDto posRetreatDetailDto);
    /**
     * @Description: 根据条件导出退单菜品明细
     * @Param: [posRetreatDetailDto]
     * @return: List<PosRetreatDetailVo>
     * @Author: shenhf
     * @Date: 2019/11/21 14:16
     */
    void exportRetreatDetailExcel(PosRetreatDetailDto posRetreatDetailDto, HttpServletResponse response);

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.PosOrderFoodAnalysisVo>
     * @Author LuoY
     * @Description 根据条件查询收款汇总分析表
     * @Date 2019/11/22 14:08
     * @Param [posOrderFoodAnalysisDto]
     **/
    List<PosCashPayAnalysisVo> findPayTypeReportByCondition(PosCashPayDto posCashPayDto);

    /**
     * @Description 导出收款汇总分析表
     * @Author 郑勇浩
     * @Data 2020/3/11 17:51
     * @Param [response, posCashPayDto]
     */
    void exportPayTypeReportByCondition(HttpServletResponse response, PosCashPayDto posCashPayDto);

    /**
     * @Description:根据条件查询收款方式分析
     * @Param: PosPayCategoryDto
     * @return:List<PosPayCategoryVo>
     * @Author: shenhf
     * @Date: 2019/11/25 11:31
     */
    List<PosPayCategoryVo> findPayCategoryReport(PosPayCategoryDto posPayCategoryDto);

    /**
     * @Description 导出收款方式分析
     * @Author 郑勇浩
     * @Data 2020/3/11 18:10
     * @Param [responseas, posPayCategoryDto]
     * @return void
     */
    void exportPayCategoryReport(HttpServletResponse response,PosPayCategoryDto posPayCategoryDto);

    /**
     * @Description: 根据条件查询菜品销量分析表
     * @Param: [PosFoodSalesDto]
     * @return: com.njwd.support.Result<List < PosFoodSalesVo>>
     * @Author: shenhf
     * @Date: 2019/11/26 10:59
     */
    Map findFoodSalesReport(PosFoodSalesDto posFoodSalesDto);
    /**
     * @Description: 根据条件导出菜品销量分析表
     * @Param: [posFoodSalesDto，response]
     * @return: void
     * @Author: shenhf
     * @Date: 2019/11/26 10:59
     */
    void exportFoodSalesReport(PosFoodSalesDto posFoodSalesDto, HttpServletResponse response);

    /**
     * @Author LuoY
     * @Description 销售情况统计表
     * @Date 2019/12/3 15:44
     * @Param [salesStatisticsDto]
     * @return com.njwd.entity.reportdata.vo.SalesStatisticsVo
     **/
    SalesStatisticsVo findSalesStatisticsReport(SalesStatisticsDto salesStatisticsDto);

    /**
     * @Author LuoY
     * @Description 销售情况统计表
     * @Date 2019/12/3 15:44
     * @Param [salesStatisticsDto]
     * @return com.njwd.entity.reportdata.vo.SalesStatisticsVo
     **/
    List<SaleStatisticsPhoneVo> findSalesStatisticsPhoneReport(SalesStatisticsDto salesStatisticsDto);

    /**
     * 收入折扣分析表
     * @param detailPayDto
     * @return
     */
    List<PosDiscountDetailPayVo>findDetailPayReport(RepPosDetailPayDto detailPayDto);

    /**
     * @Description 收入折扣分析导出
     * @Author 郑勇浩
     * @Data 2020/3/12 10:30
     * @Param [response]
     * @return void
     */
    void exportDetailPayReport(HttpServletResponse response,RepPosDetailPayDto detailPayDto);

    /**
     * 收款汇总分析表 app端
     *
     * @param: [posCashPayDto]
     * @return: com.njwd.entity.reportdata.vo.PosCashPayAnalysisVo
     * @author: zhuzs
     * @date: 2019-12-20
     */
    PosCashPayAnalysisVo findPayTypeReportApp(PosCashPayDto posCashPayDto);

    /**
     * 收款方式分析表 app端
     *
     * @param: [posPayCategoryDto]
     * @return: com.njwd.entity.reportdata.vo.PosPayCategoryVo
     * @author: zhuzs
     * @date: 2019-12-20
     */
    PosPayCategoryVo findPayCategoryReportApp(PosPayCategoryDto posPayCategoryDto);
    
    /** 
    * @Description: 啤酒进场费报表 
    * @Param: [bearIntoFactoryDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.BearIntoFactoryVo> 
    * @Author: LuoY
    * @Date: 2020/3/27 13:42
    */ 
    List<BearIntoFactoryVo> findBearIntoFactoryInfo(BearIntoFactoryDto bearIntoFactoryDto);

    /**
     * 退菜统计表 导出
     *
     * @param: [posOrderFoodAnalysisDto, response]
     * @return: void
     * @author: zhuzs
     * @date: 2020-01-07
     */
    void exportRegressionReport(ExcelExportDto posOrderFoodAnalysisDto, HttpServletResponse response);

    /** 
    * @Description: 销售情况统计表导出 
    * @Param: [posOrderFoodAnalysisDto, responses] 
    * @return: void 
    * @Author: LuoY
    * @Date: 2020/3/24 17:33
    */ 
    void exportSalesStatisticsReport(ExcelExportDto posOrderFoodAnalysisDto, HttpServletResponse responses);
    
    /** 
    * @Description: 导出啤酒进场费
    * @Param: [posOrderFoodAnalysisDto, responses] 
    * @return: void 
    * @Author: LuoY
    * @Date: 2020/3/28 14:07
    */ 
    void exportBearIntoFactory(ExcelExportDto posOrderFoodAnalysisDto, HttpServletResponse responses);
}

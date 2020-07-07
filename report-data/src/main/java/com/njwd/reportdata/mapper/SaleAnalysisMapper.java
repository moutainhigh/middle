package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.PosOrderFood;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description: 销售分析
 * @Author LuoY
 * @Date 2019/11/18
 */
@Repository
public interface SaleAnalysisMapper extends BaseMapper<PosOrderFood> {
    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.PosOrderFoodVo>
     * @Author LuoY
     * @Description 根据条件查询退增统计表
     * @Date 2019/11/19 11:21
     * @Param [PosOrderFoodDto]
     **/
    List<PosOrderFoodAnalysisVo> findPosOrderFoodByCondition(PosOrderFoodAnalysisDto posOrderFoodAnalysisDto);

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.PosRetreatDetailVo>
     * @Author shenhf
     * @Description 根据条件查询退增统计表(分页)
     * @Date 2019/11/21 14:21
     * @Param [PosRetreatDetailDto]
     **/
    Page<PosRetreatDetailVo> findRetreatDetail(@Param("page")Page<PosRetreatDetailVo> page ,@Param("posRetreatDetailDto")PosRetreatDetailDto posRetreatDetailDto);
    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.PosRetreatDetailVo>
     * @Author shenhf
     * @Description 根据条件查询退增统计表
     * @Date 2019/11/21 14:21
     * @Param [PosRetreatDetailDto]
     **/
    List<PosRetreatDetailVo> findRetreatDetail(@Param("posRetreatDetailDto")PosRetreatDetailDto posRetreatDetailDto);

    /**
    * @Description: 查詢贈菜明細
    * @Param: [posRetreatDetailDto]
    * @return: com.njwd.entity.reportdata.vo.PosRetreatDetailVo
    * @Author: shenhuaf
    * @Date: 2020/1/20 17:12
    */
    PosRetreatDetailVo sumRetreatDetail(PosRetreatDetailDto posRetreatDetailDto);

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.PosOrderFoodAnalysisVo>
     * @Author LuoY
     * @Description 根据条件查询收款汇总方式表
     * @Date 2019/11/22 14:11
     * @Param [posOrderFoodAnalysisDto]
     **/
    List<PosCashPayAnalysisVo> findPayTypeReportByCondition(PosCashPayDto posCashPayDto);

    /**
     * @Description:
     * @Param: PosPayCategoryDto
     * @return: List<PosPayCategoryVo>
     * @Author: shenhf
     * @Date: 2019/11/25 18:45
     */
    List<PosPayCategoryVo> findPayCategoryReport(PosPayCategoryDto posPayCategoryDto);

    /**
     * @Description: 根据条件查询菜品销量分析表
     * @Param: [PosFoodSalesDto]
     * @return: com.njwd.support.Result<List < PosFoodSalesVo>>
     * @Author: shenhf
     * @Date: 2019/11/27 11:15
     */
    List<PosFoodSalesVo> findFoodSalesReport(PosFoodSalesDto posFoodSalesDto);
    /**
     * @Description: 根据条件查询菜品销量分析表桌数
     * @Param: [PosFoodSalesDto]
     * @return: Map
     * @Author: shenhf
     * @Date: 2019/11/27 11:15
     */
    Map findDeskCount(PosFoodSalesDto posFoodSalesDto);

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.ReportPosDeskVo>
     * @Author LuoY
     * @Description 根据条件查询台位汇总表
     * @Date 2019/12/5 19:44
     * @Param [reportPosDeskDto]
     **/
    List<ReportPosDeskVo> findReportDeskByCondition(ReportPosDeskDto reportPosDeskDto);

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.ReportPosDeskVo>
     * @Author LuoY
     * @Description 根据条件查询台位汇总表
     * @Date 2019/12/5 19:44
     * @Param [reportPosDeskDto]
     **/
    List<ReportPosDeskVo> findReportDeskCountByCondition(ReportPosDeskDto reportPosDeskDto);

    /**
     * @return com.njwd.entity.reportdata.ReportPosDesk
     * @Author LuoY
     * @Description 查询指定门店指定时间段开台数，订单合计金额，客流总数
     * @Date 2019/12/9 10:46
     * @Param [reportPosDeskDto]
     **/
    List<ReportPosDeskVo> findCountInfoByCondition(ReportPosDeskDto reportPosDeskDto);

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailPayVo>
     * @Author LuoY
     * @Description 查询支付方式信息
     * @Date 2019/12/9 14:36
     * @Param [repPosDetailPayDto]
     **/
    List<RepPosDetailPayVo> findRepPosDetailPayByCondition(RepPosDetailPayDto repPosDetailPayDto);
    /**
     * 查询 收入折扣
     * @param detailPayDto
     * @return
     */
    List<PosDiscountDetailPayVo>findDetailPayList(RepPosDetailPayDto detailPayDto);

    /**
     * 查询 收入折扣根据门店分组
     * @param detailPayDto
     * @return
     */
    List<PosDiscountDetailPayVo>findDetailPayListGpByShopId(RepPosDetailPayDto detailPayDto);
    /**
     * 查询 收入折扣(横表)
     * @param detailPayDto
     * @return
     */
    @SqlParser(filter=true)
    List<PosDiscountDetailPayVo>findDetailPayListTwo(RepPosDetailPayDto detailPayDto);

    /**
    * @Description: 查询入库信息
    * @Param: [scmInstockEntryDto]
    * @return: java.util.List<ScmInstockEntryVo>
    * @Author: LuoY
    * @Date: 2020/3/27 17:17
    */
    List<ScmInstockEntryVo> findScmInstockEntry(ScmInstockEntryDto scmInstockEntryDto);

    /**
     * 查询 收入折扣(横表)根据门店分组
     * @param detailPayDto
     * @return
     */
    @SqlParser(filter=true)
    List<PosDiscountDetailPayVo>findDetailPayListTwoGPByShopId(RepPosDetailPayDto detailPayDto);
}

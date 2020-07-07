package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.entity.reportdata.vo.fin.RealProfitVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @Description 经营分析
 * @Author jds
 * @Date 2019/11/13 10:44
 **/
@Repository
public interface BusinessAnalysisMapper {


    /**
     * 查询巡店项目均分评分明细
     *
     * @param shopScoreDto
     * @return
     */
    List<ShopScoreVo> findPsItemScoreAvg(ShopScoreDto shopScoreDto);

    /**
     * @Description: 根据餐别id分别查询对应的金额
     * @Param: [reportPosDeskVo]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ReportPosDeskVo>
     * @Author: LuoY
     * @Date: 2019/12/29 15:01
     */
    List<ReportPosDeskVo> findReportPostDeskMealAmountByCondition(ReportPosDeskDto reportPosDeskDto);

    /**
     * @Description: 根据开桌渠道分别计算对应金额
     * @Param: [reportPosDeskVo]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.ReportPosDeskVo>
     * @Author: LuoY
     * @Date: 2019/12/29 15:01
     */
    List<ReportPosDeskVo> findReportPostDeskChannelAmountByCondition(ReportPosDeskDto reportPosDeskDto);

    /**
     * 查询参数信息
     *
     * @param enteId
     * @return DictVo
     * @Author 周鹏
     * @Date: 2020/03/27
     */
    List<DictVo> findDictList(@Param("enteId") String enteId);

    /**
     * 查询门店基础信息
     *
     * @param queryDto
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date: 2020/03/02
     */
    List<GrossProfitVo> findShopList(GrossProfitDto queryDto);

    /**
     * 查询客流量和开台数信息
     *
     * @param queryDto
     * @return ReportPosDeskVo
     * @Author 周鹏
     * @Date: 2020/03/02
     */
    List<ReportPosDeskVo> findPosDeskList(GrossProfitDto queryDto);

    /**
     * 查询实收金额信息
     *
     * @param queryDto
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date: 2020/04/26
     */
    List<GrossProfitVo> findPosReceiptsList(GrossProfitDto queryDto);

    /**
     * 查询收入和销量信息
     *
     * @param queryDto
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date: 2020/03/03
     */
    List<GrossProfitVo> findPosDetailFoodList(GrossProfitDto queryDto);

    /**
     * 查询支付方式明细
     *
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.MarketingGrossProfitVo>
     * @Author lj
     * @Date:14:26 2020/3/27
     **/
    List<MarketingGrossProfitVo> findRepPosPayList(MarketingGrossProfitDto queryDto);

    /**
     * 查询支付方式菜品明细
     *
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.MarketingGrossProfitVo>
     * @Author lj
     * @Date:14:26 2020/3/27
     **/
    List<MarketingGrossProfitVo> findRepPosPayFoodList(MarketingGrossProfitDto queryDto);

    /**
     * @Description: 折旧调整单按照门店分组
     * @return: List<RealProfitVo>
     * @Author: 李宝
     * @Date: 2020/03/03
     */
    List<RealProfitVo> findShouldDeprListByShopId(FinQueryDto finQueryDto);

    /**
     * @Description: 物料名称为广告品取价税合计按照门店分组
     * @return: List<RealProfitVo>
     * @Author: 李宝
     * @Date: 2020/03/03
     */
    List<RealProfitVo> findAllAmountListByShopId(FinQueryDto finQueryDto);

    /**
     * @Description: 盘亏单【取物料编码40.06.008、40.06.009总成本】按照门店分组
     * @return: List<RealProfitVo>
     * @Author: 李宝
     * @Date: 2020/03/03
     */
    List<RealProfitVo> findCountLossListByShopId(FinQueryDto finQueryDto);

    /**
     * 查询门店税率
     *
     * @param finQueryDto
     * @return
     */
    List<CompanyVo> findShopInfoList(FinQueryDto finQueryDto);

    /**
     * 凭证摊销
     *
     * @param finQueryDto
     * @return
     */
    List<CompanyVo> findAmortSchemeList(FinQueryDto finQueryDto);

    /**
     * 酒水成本、员工餐(物料收发汇总)
     */
    List<CompanyVo> findMaterialCost(FinQueryDto finQueryDto);

    /**
     * 查询实时利润分析（本年、同比、环比）
     *
     * @param queryDto
     * @return RealTimeProfitVo
     */
    List<RealTimeProfitVo> findRealTimeProfitList(RealTimeProfitDto queryDto);
}

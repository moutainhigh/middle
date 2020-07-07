package com.njwd.reportdata.service;

import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.RealTimeProfitDto;
import com.njwd.entity.reportdata.dto.RepPosDetailPayDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.CompanyVo;
import com.njwd.entity.reportdata.vo.RealTimeProfitVo;
import com.njwd.entity.reportdata.vo.RepPosDetailFoodVo;
import com.njwd.entity.reportdata.vo.ShopSalaryVo;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;
import com.njwd.entity.reportdata.vo.fin.FinReportVo;
import com.njwd.entity.reportdata.vo.fin.RealProfitVo;
import com.njwd.entity.reportdata.vo.scm.DishGrossProfitVo;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @ClassName RealTimeProfitService
 * @Description RealTimeProfitService
 * @Author liBao
 * @Date 2020/3/30 10:20
 */
public interface RealTimeProfitService {
    /**
     * @Description: 实时利润分析
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    List<RealTimeProfitVo> findRealTimeProfit(RealTimeProfitDto realTimProfitDto);


    /**
     * @Description: 实时利润分析（通用）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    List<RealTimeProfitVo> findRealTimeProfitMain(RealTimeProfitDto realTimeProfitDto) throws ParseException;



    /**
     * @Description: 实时利润分析同比环比
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    List<RealTimeProfitVo> findRealTimeProfitSameOrChainCompare(RealTimeProfitDto realTimProfitDto,Integer dataType);


    /**
     * @Description: 实时利润分析预算比
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    List<RealTimeProfitVo> findRealTimeProfitBudgetCompare(RealTimeProfitDto realTimProfitDto,Integer dataType);

    /**
     *  凭证摊销
     * @param finQueryDto
     * @return
     */
    List<CompanyVo> findAmortSchemeList(FinQueryDto finQueryDto);

    /**
     * 获取7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
     * @param repPosDetailPayDto
     * @param queryDto
     * @return
     */
    List<CompanyVo> getBussnessManageFee(List<FinRentAccountedForVo> saleList,RepPosDetailPayDto repPosDetailPayDto, FinQueryDto queryDto);

    /**
     * 获取折旧调整单金额
     *
     * @param queryDto
     * @param sdf
     * @return
     */
    List<RealProfitVo> getRealProfitVos(FinQueryDto queryDto, SimpleDateFormat sdf);

    /**
     * @return void
     * @Author liBao
     * @Date 2020/2/11 15:21
     * @Param [baseShopDto, response]
     * @Description 导出
     */
    void exportRealTimeProfit(ExcelExportDto finQueryDto, HttpServletResponse response);

    /**
     * @return void
     * @Author liBao
     * @Date 2020/2/11 15:21
     * @Param [baseShopDto, response]
     * @Description 同比环比导出
     */
    void exportRealTimeProfitSameCompare(ExcelExportDto finQueryDto, HttpServletResponse response);


    /**
     * @return void
     * @Author liBao
     * @Date 2020/2/11 15:21
     * @Param [baseShopDto, response]
     * @Description 预算比导出
     */
    void exportRealTimeProfitBudget(ExcelExportDto finQueryDto, HttpServletResponse response);

    /**
     * @Description: 实时利润分析（毛利率）、成本
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    RealTimeProfitVo getGrossMargin(RealTimeProfitDto realTimeProfitDto);

    /**
     * @Description: 查询实时利润分析（本年、同比、环比、预算比）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: 周鹏
     * @Date: 2020/04/27
     */
    List<RealTimeProfitVo> findRealTimeProfitAnalysis(RealTimeProfitDto realTimeProfitDto);
}

package com.njwd.reportdata.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.njwd.basedata.service.BaseDeskService;
import com.njwd.basedata.service.BaseReportItemSetService;
import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.common.ScmConstant;
import com.njwd.entity.basedata.BaseShop;
import com.njwd.entity.basedata.dto.BaseReportItemSetDto;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseReportItemSetVo;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.ShopSalary;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.dto.querydto.ScmQueryDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.entity.reportdata.vo.fin.*;
import com.njwd.entity.reportdata.vo.scm.DishGrossProfitVo;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.reportdata.mapper.BusinessAnalysisMapper;
import com.njwd.reportdata.mapper.RealTimeProfitReportTableMapper;
import com.njwd.reportdata.mapper.SaleAnalysisMapper;
import com.njwd.reportdata.service.*;
import com.njwd.service.FileService;
import com.njwd.utils.*;
import io.prometheus.client.Collector;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName RealTimeProfitServiceImpl
 * @Description RealTimeProfitServiceImpl
 * @Author liBao
 * @Date 2020/3/30 10:22
 */
@Service
public class RealTimeProfitServiceImpl implements RealTimeProfitService {

    @Resource
    private BusinessAnalysisMapper businessAnalysisMapper;

    @Resource
    private BaseReportItemSetService baseReportItemSetService;

    @Resource
    private RepPosDetailFoodService repPosDetailFoodService;

    @Resource
    private BaseDeskService baseDeskService;

    @Resource
    private SaleAnalysisMapper saleAnalysisMapper;

    @Resource
    private FinanceSubjectService financeSubjectService;

    @Autowired
    private SaleAnalysisService saleAnalysisService;

    @Autowired
    private FootStyleAnalysisService footStyleAnalysisService;

    @Autowired
    private SalaryAnalysisService salaryAnalysisService;

    @Autowired
    private FileService fileService;

    @Autowired
    private StaffAnalysisService staffAnalysisService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private RealTimeProfitReportTableMapper reportTableMapper;

    private Logger logger = LoggerFactory.getLogger(ScmReportTableServiceImpl.class);

    /**
     * 实时利润分析
     *
     * @param realTimProfitDto
     * @return
     */
    @Override
    public List<RealTimeProfitVo> findRealTimeProfit(RealTimeProfitDto realTimProfitDto) {
        List<RealTimeProfitVo> realTimeProfitVos = new ArrayList<>();
        //查询预置项目项
        BaseReportItemSetDto baseReportItemSetDto = new BaseReportItemSetDto();
        baseReportItemSetDto.setEnteId(realTimProfitDto.getEnteId());
        baseReportItemSetDto.setReportId(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT);
        List<BaseReportItemSetVo> baseReportItemSetVos = baseReportItemSetService.findBaseReportItemSetVoByReportId(baseReportItemSetDto);
        //初始化报表左侧项目数
        initRealTimeProfit(realTimeProfitVos, baseReportItemSetVos);
        //计算本期
        calculationRealTimeProfitCurrentMoney(realTimeProfitVos, realTimProfitDto,
                realTimProfitDto.getDataType());
        //折叠或展开
        List<RealTimeProfitVo> allList = new ArrayList<>();
        List<RealTimeProfitVo> all = new ArrayList<>();
        realTimeProfitVos.forEach(
                data -> {
                    RealTimeProfitVo realTime = new RealTimeProfitVo();
                    realTime.setType(ReportDataConstant.ReportRealTimePutType.All);
                    realTime.setItemNumber(data.getItemNumber());
                    realTime.setItemName(data.getItemName());
                    realTime.setCurrentMoney(data.getCurrentMoney());
                    realTime.setCurrentProportion(data.getCurrentProportion());
                    realTime.setYearMoney(data.getYearMoney());
                    realTime.setYearProportion(data.getYearProportion());
                    realTime.setItemLevel(data.getItemLevel());
                    all.add(realTime);
                }
        );
        List<RealTimeProfitVo> mainList = realTimeProfitVos.stream().filter(data -> ReportDataConstant.ReportItemLevel.LEVEL_ONE.equals(data.getItemLevel())).collect(Collectors.toList());
        mainList.forEach(
                data -> data.setType(ReportDataConstant.ReportRealTimePutType.MAIN)
        );
        allList.addAll(all);
        allList.addAll(mainList);
        return allList;
    }

    /**
     * @param realTimeProfitDto
     * @Description: 实时利润分析（通用）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    @Override
    public List<RealTimeProfitVo> findRealTimeProfitMain(RealTimeProfitDto realTimeProfitDto) throws ParseException {
        List<RealTimeProfitVo> realTimeProfitVos = new ArrayList<>();
        //查询预置项目项
        BaseReportItemSetDto baseReportItemSetDto = new BaseReportItemSetDto();
        baseReportItemSetDto.setEnteId(realTimeProfitDto.getEnteId());
        baseReportItemSetDto.setReportId(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT);
        List<BaseReportItemSetVo> baseReportItemSetVos = baseReportItemSetService.findBaseReportItemSetVoByReportId(baseReportItemSetDto);
        //初始化报表左侧项目数
        initRealTimeProfit(realTimeProfitVos, baseReportItemSetVos);
        //计算本期、本年、同比、环比
        calculationRealTimeProfitCommonMoney(realTimeProfitVos, realTimeProfitDto,
                realTimeProfitDto.getDataType());
        //折叠或展开
        List<RealTimeProfitVo> allList = new ArrayList<>();
        List<RealTimeProfitVo> all = new ArrayList<>();
        realTimeProfitVos.forEach(
                data -> {
                    RealTimeProfitVo realTime = new RealTimeProfitVo();
                    realTime.setType(ReportDataConstant.ReportRealTimePutType.All);
                    realTime.setItemNumber(data.getItemNumber());
                    realTime.setItemName(data.getItemName());
                    realTime.setCurrentMoney(data.getCurrentMoney());
                    realTime.setCurrentProportion(data.getCurrentProportion());
                    realTime.setYearMoney(data.getYearMoney());
                    realTime.setYearProportion(data.getYearProportion());
                    realTime.setLastCurrentMoney(data.getLastCurrentMoney());
                    realTime.setLatelyCurrentMoney(data.getLatelyCurrentMoney());
                    realTime.setItemLevel(data.getItemLevel());
                    realTime.setItemCode(data.getItemCode());
                    all.add(realTime);
                }
        );
        List<RealTimeProfitVo> mainList = realTimeProfitVos.stream().filter(data -> ReportDataConstant.ReportItemLevel.LEVEL_ONE.equals(data.getItemLevel())).collect(Collectors.toList());
        mainList.forEach(
                data -> data.setType(ReportDataConstant.ReportRealTimePutType.MAIN)
        );
        allList.addAll(all);
        allList.addAll(mainList);
        return allList;
    }


    /**
     * 获取新的门店集合
     * @param shopVos
     * @return
     */
    private List<BaseShopVo> getNewShopVoList(List<BaseShopVo> shopVos) {
        List<BaseShopVo> commonVos = new ArrayList<>();
        shopVos.forEach(data->{
            BaseShopVo base = new BaseShopVo();
            base.setShopId(data.getShopId());
            base.setShopName(data.getShopName());
            commonVos.add(base);
        });
        return commonVos;
    }


    /**
     * @Description:计算金额
     * @Param: [businessReportDayItemVos, businessReportDayDto]
     * @return: void
     * @Author: liBao
     * @Date: 2019/12/29 14:47
     */
    private void calculationRealTimeProfitCommonMoney(@NotNull List<RealTimeProfitVo> realTimeProfitVos,
                                                      RealTimeProfitDto realTimProfitDto, Integer dataType) throws ParseException {
        //查询支付方式明细表
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        Date startDate = sf.parse(realTimProfitDto.getBeginTime());
        Date endDate = sf.parse(realTimProfitDto.getEndTime());
        realTimProfitDto.setBeginDate(startDate);
        realTimProfitDto.setEndDate(endDate);
        //Dto转换
        FinQueryDto queryDto = getFinQueryDto(realTimProfitDto);
        if (ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR.equals(dataType)) {
            //本年累计
            //查询时间重置
            String beginDate = queryDto.getBeginTime();
            queryDto.setBeginTime(beginDate.substring(0, 4) + "-01-01");
            //算出本年累计跨度
            queryDto.setDays(DateUtils.getBetweenDay(queryDto.getBeginTime(), queryDto.getEndTime(), "yyyy-MM-dd"));
            Date beginYearDate = sf.parse(queryDto.getBeginTime());
            queryDto.setBeginDate(beginYearDate);
        } else if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)) {
            if (Constant.DateType.MONTH == queryDto.getDateType()) {
                if (DateUtils.ifEndOfMouth(queryDto.getEndDate())) {
                    queryDto.setEndDate(DateUtils.getLastDayOfMonth(queryDto.getBeginDate()));
                } else {
                    queryDto.setEndDate(DateUtils.subYears(queryDto.getEndDate(), Constant.Number.ONE));
                }
            } else {
                queryDto.setEndDate(DateUtils.subYears(queryDto.getEndDate(), Constant.Number.ONE));
            }
            queryDto.setBeginDate(DateUtils.subYears(sf.parse(queryDto.getBeginTime()), Constant.Number.ONE));
            queryDto.setBeginTime(sf.format(queryDto.getBeginDate()));
            queryDto.setEndTime(sf.format(queryDto.getEndDate()));
            //算出去年同期累计跨度
            queryDto.setDays(DateUtils.getBetweenDay(queryDto.getBeginTime(), queryDto.getEndTime(), "yyyy-MM-dd"));
        } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType)) {
            //环比
            List<Date> dates = DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
            queryDto.setBeginDate(dates.get(Constant.Number.ZERO));
            queryDto.setEndDate(dates.get(Constant.Number.ONE));
            queryDto.setBeginTime(sf.format(queryDto.getBeginDate()));
            queryDto.setEndTime(sf.format(queryDto.getEndDate()));
            //算出上期累计跨度
            queryDto.setDays(DateUtils.getBetweenDay(queryDto.getBeginTime(), queryDto.getEndTime(), "yyyy-MM-dd"));
        }

        //获取7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
        RepPosDetailPayDto repPosDetailPayDto = new RepPosDetailPayDto();
        repPosDetailPayDto.setShopIdList(queryDto.getShopIdList());
        repPosDetailPayDto.setBeginDate(queryDto.getBeginDate());
        repPosDetailPayDto.setEndDate(queryDto.getEndDate());
        repPosDetailPayDto.setEnteId(queryDto.getEnteId());
        repPosDetailPayDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        //查询收入合计
        List<FinRentAccountedForVo> saleList = baseDeskService.findSaleByCondition(queryDto);
        List<CompanyVo> shopInfoList = getBussnessManageFee(saleList, repPosDetailPayDto, queryDto);

        //查询销售菜品明细表
        RepPosDetailFoodDto repPosDetailFoodDto = new RepPosDetailFoodDto();
        repPosDetailFoodDto.setShopIdList(queryDto.getShopIdList());
        repPosDetailFoodDto.setBeginDate(queryDto.getBeginDate());
        repPosDetailFoodDto.setEndDate(queryDto.getEndDate());
        repPosDetailFoodDto.setEnteId(queryDto.getEnteId());
        repPosDetailFoodDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        //查询菜品金额明细
        List<RepPosDetailFoodVo> repPosDetailFoodVos = repPosDetailFoodService.findRepPosDetailFoodByCondition(repPosDetailFoodDto);
        //菜品收入
        RepPosDetailFoodVo repPosDetailFoodVo = new RepPosDetailFoodVo();
        repPosDetailFoodVo.setFoodAllPrice(CollectionUtil.isEmpty(saleList) == true ? BigDecimal.ZERO : saleList.stream().map(FinRentAccountedForVo::getMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add));

        //查询菜品成本
        ScmQueryDto scmQueryDto = new ScmQueryDto();
        scmQueryDto.setShopIdList(queryDto.getShopIdList());
        scmQueryDto.setBeginDate(queryDto.getBeginDate());
        scmQueryDto.setEndDate(queryDto.getEndDate());
        scmQueryDto.setEnteId(queryDto.getEnteId());
        scmQueryDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        scmQueryDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);

        List<DishGrossProfitVo> dishBaseList = footStyleAnalysisService.getDishBaseList(scmQueryDto, null);

        //品牌奖金表总额
        List<CompanyVo> bonusList = getBrandBonusVos(queryDto);


        //啤酒进场费
        List<CompanyVo> beerList = getBearIntoFactory(queryDto);

        List<FinReportConfigVo> configVos = financeSubjectService.getConfigByGroup(queryDto.getEnteId(),
                ReportDataConstant.FinType.REALTIME_PROFIT);

        //7.16.01	营业费用_广告宣传费_广告费	金蝶-总账-凭证摊销明细里转入科目为广告费的项目合计除以期间乘以天数
        //7.22	营业费用_长期待摊费	金蝶-总账-凭证摊销明细里转入科目为长期待摊费的项目合计）除以期间乘以天数
        List<String> explanationList = new ArrayList<>();
        //广告费
        explanationList.add(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY);
        //长期待摊费
        explanationList.add(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY);

        List<String> allPeriod = DateUtils.getTwoDateAllMonth(queryDto.getBeginTime(), queryDto.getEndTime());
        queryDto.setPeriods(allPeriod);
        queryDto.setPeriodDays(allPeriod.size() * 30);
        queryDto.setExplanation(explanationList);

        List<CompanyVo> amortSchemeListByShop = findAmortSchemeList(queryDto);
        //计算酒水成本和员工餐
        List<CompanyVo> materialCostList = getCostOfWineAndMeal(sf, queryDto);


        //乐才薪酬分析
        ShopSalaryDto shopSalaryDto = new ShopSalaryDto();
        shopSalaryDto.setShopIdList(queryDto.getShopIdList());
        shopSalaryDto.setBeginDate(queryDto.getBeginDate());
        shopSalaryDto.setEndDate(queryDto.getEndDate());
        shopSalaryDto.setEnteId(queryDto.getEnteId());
        shopSalaryDto.setShopTypeIdList(queryDto.getShopTypeIdList());

        List<ShopSalaryVo> shopScoreVos = salaryAnalysisService.findSampleShopSalaryList(shopSalaryDto);

        //本期
        List<FinReportVo> shopSubjects = financeSubjectService.getSubjectData(queryDto);

        //修改时间，查询最近12月数据
        String yearTime = queryDto.getEndTime().substring(0, 4);
        String monthTime = queryDto.getEndTime().substring(5, 7);
        String lastYear = String.valueOf(Integer.valueOf(yearTime) - 1);
        String beginTimeForLately = lastYear + "-" + monthTime + "-01";
        //获取上个月的最后一天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String endTimeForLately = queryDto.getEndTime();
        Date date = sdf.parse(queryDto.getEndTime());
        Calendar c = Calendar.getInstance();
        //设置为指定日期
        c.setTime(date);
        //指定日期月份减去一
        c.add(Calendar.MONTH, -1);
        //指定日期月份减去一后的 最大天数
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        //获取最终的时间
        Date lastDateOfPrevMonth = c.getTime();
        endTimeForLately = sdf.format(lastDateOfPrevMonth);

        FinQueryDto dto = new FinQueryDto();
        dto.setEnteId(queryDto.getEnteId());
        dto.setBeginTime(beginTimeForLately);
        dto.setEndTime(endTimeForLately);
        dto.setShopIdList(queryDto.getShopIdList());
        dto.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<FinReportVo> voForLastYearSubject = financeSubjectService.getSubjectData(dto);

        //查询折旧调整单
        //设置折旧调整单的日期(上个月初到月末)
        List<RealProfitVo> shouldDeprList = getRealProfitVos(queryDto, sdf);

        //查询营业费用_广告宣传费_企划费用
        List<RealProfitVo> layOutList = businessAnalysisMapper.findAllAmountListByShopId(queryDto);

        //查询杂项成本  供应链-库存管理-盘亏单【取物料编码40.06.008、40.06.009总成本】
        RealTimeProfitVo realTimeProfitVo = realTimeProfitVos.stream().filter(data -> ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS_NAME
                .equals(data.getItemName().trim())).collect(Collectors.toList()).get(0);
        List<String> codeList = new ArrayList<>();
        String[] codes = realTimeProfitVo.getItemCode().split(",");
        for (String code : codes) {
            codeList.add(code);
        }
        queryDto.setCodes(codeList);
        List<RealProfitVo> countLossList = businessAnalysisMapper.findCountLossListByShopId(queryDto);
        //计算金额
        calculationCurrentMoney(realTimeProfitVos, repPosDetailFoodVo,
                dataType, repPosDetailFoodVos, shopSubjects, configVos, queryDto,
                voForLastYearSubject, shouldDeprList, layOutList, countLossList, shopInfoList, beerList, dishBaseList, shopScoreVos,
                amortSchemeListByShop, materialCostList, bonusList);

        //计算占比
        if (ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY.equals(dataType) || ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR.equals(dataType)) {
            calculationCurrentProportion(dataType, realTimeProfitVos, repPosDetailFoodVo);
        }
    }

    private List<CompanyVo> getBearIntoFactory(FinQueryDto queryDto) {
        BearIntoFactoryDto bearIntoFactoryDto = new BearIntoFactoryDto();
        bearIntoFactoryDto.setShopIdList(queryDto.getShopIdList());
        bearIntoFactoryDto.setBeginDate(queryDto.getBeginDate());
        bearIntoFactoryDto.setEndDate(queryDto.getEndDate());
        bearIntoFactoryDto.setEnteId(queryDto.getEnteId());
        bearIntoFactoryDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<BearIntoFactoryVo> bearIntoFactoryVos = saleAnalysisService.findBearIntoFactoryInfo(bearIntoFactoryDto);
        Map<String, List<BearIntoFactoryVo>> bearMap = CollectionUtil.isEmpty(bearIntoFactoryVos) == true ? null : bearIntoFactoryVos.stream().filter(data -> data.getShopNo() != null).collect(Collectors.groupingBy(BearIntoFactoryVo::getShopNo));
        List<CompanyVo> beerList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(bearMap)) {
            bearMap.forEach((key, data) -> {
                CompanyVo beer = new CompanyVo();
                BigDecimal bear1 = bearMap.get(key).stream().collect(Collectors.toList()).get(0).getCountMoney();
                beer.setShopId(key);
                beer.setBeerIntoFactoryFee(bear1);
                beerList.add(beer);
            });
        }
        return beerList;
    }


    /**
     * @Description: 初始化实时利润分析左侧项目树
     * @Param: [businessReportDayItemVos, baseReportItemSetVos]
     * @return: void
     * @Author: liBao
     * @Date: 2019/12/29 14:21
     */
    private void initRealTimeProfit(@NotNull List<RealTimeProfitVo> businessReportDayItemVos, @NotNull List<BaseReportItemSetVo> baseReportItemSetVos) {
        if (!FastUtils.checkNullOrEmpty(baseReportItemSetVos)) {
            //循环项目表
            //按itemNumber排序
            baseReportItemSetVos.sort(Comparator.comparing(BaseReportItemSetVo::getItemType).
                    thenComparing(BaseReportItemSetVo::getItemNumber));
            //初始化左侧项目树
            for (BaseReportItemSetVo data : baseReportItemSetVos) {
                RealTimeProfitVo realTimeProfitVo = setRealTimeProfitInfo(data.getItemNumber(), data.getItemCode(),
                        data.getItemName(), data.getItemLevel(), data.getItemType(), data.getItemTypeName(), data.getDataType(), data.getReportId());
                businessReportDayItemVos.add(realTimeProfitVo);

            }

        }
    }

    /**
     * @Description: 项目设置
     * @Param: [itemNumber, itemCode, itemName, itemLevel, itemType, itemTypeNa]
     * @return: com.njwd.entity.reportdata.vo.BusinessReportDayItemVo
     * @Author: liBao
     * @Date: 2019/12/31 13:36
     */
    private RealTimeProfitVo setRealTimeProfitInfo(String itemNumber, String itemCode, String itemName, Integer itemLevel, Integer itemType, String itemTypeName, Integer dataType, Integer reportId) {
        RealTimeProfitVo realTimeProfitVo = new RealTimeProfitVo();
        realTimeProfitVo.setItemNumber(itemNumber);
        realTimeProfitVo.setItemType(itemType);
        realTimeProfitVo.setItemTypeName(itemTypeName);
        realTimeProfitVo.setItemCode(itemCode);
        realTimeProfitVo.setItemName(itemName);
        realTimeProfitVo.setItemLevel(itemLevel);
        realTimeProfitVo.setDataType(dataType);
        realTimeProfitVo.setReportId(reportId);
        return realTimeProfitVo;
    }

    /**
     * @Description:计算金额
     * @Param: [businessReportDayItemVos, businessReportDayDto]
     * @return: void
     * @Author: liBao
     * @Date: 2019/12/29 14:47
     */
    private void calculationRealTimeProfitCurrentMoney(@NotNull List<RealTimeProfitVo> realTimeProfitVos,
                                                       RealTimeProfitDto realTimProfitDto, Integer dataType) {

        //查询支付方式明细表
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = sf.parse(realTimProfitDto.getBeginTime());
            endDate = sf.parse(realTimProfitDto.getEndTime());
            realTimProfitDto.setBeginDate(startDate);
            realTimProfitDto.setEndDate(endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Dto转换
        FinQueryDto queryDto = getFinQueryDto(realTimProfitDto);
        List<FinReportConfigVo> configVos = financeSubjectService.getConfigByGroup(queryDto.getEnteId(),
                ReportDataConstant.FinType.REALTIME_PROFIT);
        //查询收入合计
        List<FinRentAccountedForVo> saleList = baseDeskService.findSaleByCondition(queryDto);
        //获取7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
        RepPosDetailPayDto repPosDetailPayDto = new RepPosDetailPayDto();
        repPosDetailPayDto.setShopIdList(realTimProfitDto.getShopIdList());
        repPosDetailPayDto.setBeginDate(realTimProfitDto.getBeginDate());
        repPosDetailPayDto.setEndDate(realTimProfitDto.getEndDate());
        repPosDetailPayDto.setEnteId(realTimProfitDto.getEnteId());
        repPosDetailPayDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        List<CompanyVo> shopInfoList = getBussnessManageFee(saleList, repPosDetailPayDto, queryDto);

        //查询销售菜品明细表
        RepPosDetailFoodDto repPosDetailFoodDto = new RepPosDetailFoodDto();
        repPosDetailFoodDto.setShopIdList(realTimProfitDto.getShopIdList());
        repPosDetailFoodDto.setBeginDate(realTimProfitDto.getBeginDate());
        repPosDetailFoodDto.setEndDate(realTimProfitDto.getEndDate());
        repPosDetailFoodDto.setEnteId(realTimProfitDto.getEnteId());
        repPosDetailFoodDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        //查询菜品金额明细
        List<RepPosDetailFoodVo> repPosDetailFoodVos = repPosDetailFoodService.findRepPosDetailFoodByCondition(repPosDetailFoodDto);
        //菜品收入
        RepPosDetailFoodVo repPosDetailFoodVo = new RepPosDetailFoodVo();

        repPosDetailFoodVo.setFoodAllPrice(CollectionUtil.isEmpty(saleList) == true ? BigDecimal.ZERO : saleList.stream().map(FinRentAccountedForVo::getMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add));


        //查询菜品成本
        ScmQueryDto scmQueryDto = new ScmQueryDto();
        scmQueryDto.setShopIdList(realTimProfitDto.getShopIdList());
        scmQueryDto.setBeginDate(realTimProfitDto.getBeginDate());
        scmQueryDto.setEndDate(realTimProfitDto.getEndDate());
        scmQueryDto.setEnteId(realTimProfitDto.getEnteId());
        scmQueryDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        scmQueryDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);

        List<DishGrossProfitVo> dishBaseList = footStyleAnalysisService.getDishBaseList(scmQueryDto, null);

        //啤酒进场费
        List<CompanyVo> beerList = getBearIntoFactory(queryDto);


        //品牌奖金表总额
        List<CompanyVo> bonusList = getBrandBonusVos(queryDto);


        //7.16.01	营业费用_广告宣传费_广告费	金蝶-总账-凭证摊销明细里转入科目为广告费的项目合计除以期间乘以天数
        //7.22	营业费用_长期待摊费	金蝶-总账-凭证摊销明细里转入科目为长期待摊费的项目合计）除以期间乘以天数
        List<String> explanationList = new ArrayList<>();
        //广告费
        explanationList.add(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY);
        //长期待摊费
        explanationList.add(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY);

        try {
            List<String> allPeriod = DateUtils.getTwoDateAllMonth(queryDto.getBeginTime(), queryDto.getEndTime());
            queryDto.setPeriods(allPeriod);
            queryDto.setPeriodDays(allPeriod.size() * 30);
            queryDto.setExplanation(explanationList);

        } catch (Exception e) {

        }
        List<CompanyVo> amortSchemeListByShop = findAmortSchemeList(queryDto);
        //计算酒水成本和员工餐
        List<CompanyVo> materialCostList = getCostOfWineAndMeal(sf, queryDto);


        //乐才薪酬分析
        ShopSalaryDto shopSalaryDto = new ShopSalaryDto();
        shopSalaryDto.setShopIdList(realTimProfitDto.getShopIdList());
        shopSalaryDto.setBeginDate(realTimProfitDto.getBeginDate());
        shopSalaryDto.setEndDate(realTimProfitDto.getEndDate());
        shopSalaryDto.setEnteId(realTimProfitDto.getEnteId());
        shopSalaryDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());

        List<ShopSalaryVo> shopScoreVos = salaryAnalysisService.findSampleShopSalaryList(shopSalaryDto);

        //本期
        List<FinReportVo> shopSubjects = financeSubjectService.getSubjectData(queryDto);

        //修改时间，查询最近12月数据
        String yearTime = queryDto.getEndTime().substring(0, 4);
        String monthTime = queryDto.getEndTime().substring(5, 7);
        String lastYear = String.valueOf(Integer.valueOf(yearTime) - 1);
        String beginTimeForLately = lastYear + "-" + monthTime + "-01";
        //获取上个月的最后一天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String endTimeForLately = queryDto.getEndTime();
        try {
            Date date = sdf.parse(queryDto.getEndTime());
            Calendar c = Calendar.getInstance();
            //设置为指定日期
            c.setTime(date);
            //指定日期月份减去一
            c.add(Calendar.MONTH, -1);
            //指定日期月份减去一后的 最大天数
            c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
            //获取最终的时间
            Date lastDateOfPrevMonth = c.getTime();
            endTimeForLately = sdf.format(lastDateOfPrevMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        FinQueryDto dto = new FinQueryDto();
        dto.setEnteId(queryDto.getEnteId());
        dto.setBeginTime(beginTimeForLately);
        dto.setEndTime(endTimeForLately);
        dto.setShopIdList(queryDto.getShopIdList());
        dto.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<FinReportVo> voForLastYearSubject = financeSubjectService.getSubjectData(dto);

        //查询折旧调整单
        //设置折旧调整单的日期(上个月初到月末)
        List<RealProfitVo> shouldDeprList = getRealProfitVos(queryDto, sdf);

        //查询营业费用_广告宣传费_企划费用
        List<RealProfitVo> layOutList = businessAnalysisMapper.findAllAmountListByShopId(queryDto);

        //查询杂项成本  供应链-库存管理-盘亏单【取物料编码40.06.008、40.06.009总成本】
        RealTimeProfitVo realTimeProfitVo = realTimeProfitVos.stream().filter(data -> ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS_NAME
                .equals(data.getItemName().trim())).collect(Collectors.toList()).get(0);
        List<String> codeList = new ArrayList<>();
        String[] codes = realTimeProfitVo.getItemCode().split(",");
        for (String code : codes) {
            codeList.add(code);
        }
        queryDto.setCodes(codeList);
        List<RealProfitVo> countLossList = businessAnalysisMapper.findCountLossListByShopId(queryDto);
        //计算本期
        calculationCurrentMoney(realTimeProfitVos, repPosDetailFoodVo,
                ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY, repPosDetailFoodVos, shopSubjects, configVos, queryDto,
                voForLastYearSubject, shouldDeprList, layOutList, countLossList, shopInfoList, beerList, dishBaseList, shopScoreVos,
                amortSchemeListByShop, materialCostList, bonusList);
        //本期占比
        calculationCurrentProportion(dataType, realTimeProfitVos, repPosDetailFoodVo);

        //本年累计
        //查询时间重置
        String beginDate = queryDto.getBeginTime();
        queryDto.setBeginTime(beginDate.substring(0, 4) + "-01-01");
        //算出本年累计跨度
        queryDto.setDays(DateUtils.getBetweenDay(queryDto.getBeginTime(), queryDto.getEndTime(), "yyyy-MM-dd"));
        Date beginYearDate = new Date();
        try {
            beginYearDate = sf.parse(queryDto.getBeginTime());
            queryDto.setBeginDate(beginYearDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<FinReportVo> shopSubjectsYear = financeSubjectService.getSubjectData(queryDto);

        //查询  7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%

        //查询收入合计
        List<FinRentAccountedForVo> saleListYear = baseDeskService.findSaleByCondition(queryDto);
        RepPosDetailPayDto repPosDetailPayDto1 = new RepPosDetailPayDto();
        repPosDetailPayDto1.setShopIdList(queryDto.getShopIdList());
        repPosDetailPayDto1.setBeginDate(queryDto.getBeginDate());
        repPosDetailPayDto1.setEndDate(queryDto.getEndDate());
        repPosDetailPayDto1.setEnteId(queryDto.getEnteId());
        repPosDetailPayDto1.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        List<CompanyVo> shopInfoYearList = getBussnessManageFee(saleListYear, repPosDetailPayDto1, queryDto);

        //查询销售菜品明细表
        RepPosDetailFoodDto repPosDetailFoodYearDto = new RepPosDetailFoodDto();
        repPosDetailFoodYearDto.setShopIdList(queryDto.getShopIdList());
        repPosDetailFoodYearDto.setBeginDate(beginYearDate);
        repPosDetailFoodYearDto.setEndDate(endDate);
        repPosDetailFoodYearDto.setEnteId(queryDto.getEnteId());
        repPosDetailFoodYearDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        //查询菜品金额明细
        List<RepPosDetailFoodVo> repPosDetailFoodYearVos = repPosDetailFoodService.findRepPosDetailFoodByCondition(repPosDetailFoodYearDto);
        //菜品收入
        RepPosDetailFoodVo repPosDetailFoodYearVo = new RepPosDetailFoodVo();
        repPosDetailFoodYearVo.setFoodAllPrice(CollectionUtil.isEmpty(saleListYear) == true ? BigDecimal.ZERO : saleListYear.stream().map(FinRentAccountedForVo::getMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add));

        //查询菜品成本
        ScmQueryDto scmQueryYearDto = new ScmQueryDto();
        scmQueryYearDto.setShopIdList(realTimProfitDto.getShopIdList());
        scmQueryYearDto.setBeginDate(beginYearDate);
        scmQueryYearDto.setEndDate(endDate);
        scmQueryYearDto.setEnteId(realTimProfitDto.getEnteId());
        scmQueryYearDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        scmQueryYearDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);

        List<DishGrossProfitVo> dishBaseYearList = footStyleAnalysisService.getDishBaseList(scmQueryYearDto, null);

        //乐才薪酬分析
        ShopSalaryDto shopSalaryYearDto = new ShopSalaryDto();
        shopSalaryYearDto.setShopIdList(realTimProfitDto.getShopIdList());
        shopSalaryYearDto.setBeginDate(beginYearDate);
        shopSalaryYearDto.setEndDate(endDate);
        shopSalaryYearDto.setEnteId(realTimProfitDto.getEnteId());
        shopSalaryYearDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());

        List<ShopSalaryVo> shopScoreYearVos = salaryAnalysisService.findSampleShopSalaryList(shopSalaryYearDto);


        //啤酒进场费
        BearIntoFactoryDto bearIntoFactoryDto1 = new BearIntoFactoryDto();
        bearIntoFactoryDto1.setShopIdList(queryDto.getShopIdList());
        bearIntoFactoryDto1.setBeginDate(queryDto.getBeginDate());
        bearIntoFactoryDto1.setEndDate(queryDto.getEndDate());
        bearIntoFactoryDto1.setEnteId(queryDto.getEnteId());
        bearIntoFactoryDto1.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<BearIntoFactoryVo> bearIntoFactoryVos1 = saleAnalysisService.findBearIntoFactoryInfo(bearIntoFactoryDto1);
        Map<String, List<BearIntoFactoryVo>> bearMap1 = bearIntoFactoryVos1.isEmpty() == true ? null : bearIntoFactoryVos1.stream().filter(data -> data.getShopNo() != null).collect(Collectors.groupingBy(BearIntoFactoryVo::getShopNo));
        List<CompanyVo> beerList1 = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(bearMap1)) {
            bearMap1.forEach((key, data) -> {
                CompanyVo beer = new CompanyVo();
                BigDecimal bear1 = bearMap1.get(key).stream().collect(Collectors.toList()).get(0).
                        getCountMoney();
                beer.setShopId(key);
                beer.setBeerIntoFactoryFee(bear1);
                beerList1.add(beer);
            });
        }


        //品牌奖金表总额
        List<CompanyVo> bonusListYear = getBrandBonusVos(queryDto);

        //7.16.01	营业费用_广告宣传费_广告费	金蝶-总账-凭证摊销明细里转入科目为广告费的项目合计除以期间乘以天数
        //7.22	营业费用_长期待摊费	金蝶-总账-凭证摊销明细里转入科目为长期待摊费的项目合计）除以期间乘以天数

        try {
            List<String> allPeriod = DateUtils.getTwoDateAllMonth(queryDto.getBeginTime(), queryDto.getEndTime());
            queryDto.setPeriods(allPeriod);
            queryDto.setPeriodDays(allPeriod.size() * 30);
            queryDto.setExplanation(explanationList);

        } catch (Exception e) {

        }
        List<CompanyVo> amortSchemeListYearByShop = findAmortSchemeList(queryDto);

        List<CompanyVo> materialCostYearList = businessAnalysisMapper.findMaterialCost(queryDto);


        //查询本年累计折旧调整单
        //设置折旧调整单的日期(开始时间的上个月月初，结束日期的上个月月末)
        queryDto.setAdjustDays(DateUtils.getBetweenDay(sdf.format(beginYearDate), sdf.format(queryDto.getEndDate()), "yyyy-MM-dd"));
        //查询营业费用_广告宣传费_企划费用
        List<RealProfitVo> layOutYearList = businessAnalysisMapper.findAllAmountListByShopId(queryDto);

        //查询杂项成本  供应链-库存管理-盘亏单【取物料编码40.06.008、40.06.009总成本】
        queryDto.setCodes(codeList);
        List<RealProfitVo> countLossYearList = businessAnalysisMapper.findCountLossListByShopId(queryDto);
        //计算本年
        calculationCurrentMoney(realTimeProfitVos, repPosDetailFoodYearVo,
                ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR,
                repPosDetailFoodYearVos, shopSubjectsYear, configVos, queryDto, voForLastYearSubject, shouldDeprList,
                layOutYearList, countLossYearList, shopInfoYearList, beerList1, dishBaseYearList, shopScoreYearVos, amortSchemeListYearByShop, materialCostYearList, bonusListYear);

        //本年占比
        calculationCurrentProportion(ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR, realTimeProfitVos, repPosDetailFoodYearVo);
    }

    /**
     * 乐才奖金获取
     *
     * @param queryDto
     * @return
     */
    private List<CompanyVo> getBrandBonusVos(FinQueryDto queryDto) {
        StaffAnalysisDto analysisDto = new StaffAnalysisDto();
        analysisDto.setShopIdList(queryDto.getShopIdList());
        analysisDto.setBeginDate(queryDto.getBeginDate());
        analysisDto.setEndDate(queryDto.getEndDate());
        analysisDto.setEnteId(queryDto.getEnteId());
        analysisDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<BrandBonusVo> bonusVos = staffAnalysisService.findBrandBonusAnalysis(analysisDto);
        List<BrandBonusVo> bonusList = CollectionUtil.isEmpty(bonusVos) == true ? null : bonusVos.stream().filter(data -> data != null && data.getShopId() != null && data.getAmount() != null && ReportDataConstant.Finance.SHOP.equals(data.getType())).collect(Collectors.toList());
        List<CompanyVo> bonusVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(bonusList)) {
            bonusList.forEach(data -> {
                CompanyVo companyVo = new CompanyVo();
                companyVo.setShopId(data.getShopId());
                companyVo.setBonusMoney(data.getAmount());
                bonusVoList.add(companyVo);
            });
        }
        return bonusVoList;
    }

    /**
     * 获取折旧调整单金额
     *
     * @param queryDto
     * @param sdf
     * @return
     */
    @Override
    public List<RealProfitVo> getRealProfitVos(FinQueryDto queryDto, SimpleDateFormat sdf) {
        Date[] date = DateUtils.lastMonth(queryDto.getEndDate());
        FinQueryDto adjustQuery = new FinQueryDto();
        adjustQuery.setEnteId(queryDto.getEnteId());
        adjustQuery.setBeginDate(date[0]);
        adjustQuery.setEndDate(date[1]);
        adjustQuery.setShopIdList(queryDto.getShopIdList());
        adjustQuery.setShopTypeIdList(queryDto.getShopTypeIdList());
        //queryDto.setAdjustDays(DateUtils.getBetweenDay(sdf.format(date[0]), sdf.format(date[1]), "yyyy-MM-dd"));
        queryDto.setAdjustDays(DateUtils.getBetweenDay(sdf.format(queryDto.getBeginDate()), sdf.format(queryDto.getEndDate()), "yyyy-MM-dd"));
        return businessAnalysisMapper.findShouldDeprListByShopId(adjustQuery);
    }


    /**
     * 获取7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
     *
     * @param repPosDetailPayDto
     * @param queryDto
     * @return
     */
    @Override
    public List<CompanyVo> getBussnessManageFee(List<FinRentAccountedForVo> saleList, RepPosDetailPayDto repPosDetailPayDto, FinQueryDto queryDto) {
        //查询  7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
        //赠送金额
        List<PosDiscountDetailPayVo> giveMoneyList = saleAnalysisMapper.findDetailPayListGpByShopId(repPosDetailPayDto);
        //折扣额
        List<PosDiscountDetailPayVo> disCountMoneyList = saleAnalysisMapper.findDetailPayListTwoGPByShopId(repPosDetailPayDto);
        //查询门店信息
        List<CompanyVo> shopInfoList = businessAnalysisMapper.findShopInfoList(queryDto);

        //合并门店税额和赠送金额
        MergeUtil.merge(shopInfoList, giveMoneyList,
                CompanyVo::getShopId, PosDiscountDetailPayVo::getShopId,
                (shopInfo, payInfo) -> {
                    BigDecimal payMoney = payInfo.getCurrentMoney();
                    shopInfo.setExemptionTotal(payMoney == null ? new BigDecimal("0.00") : payMoney);
                });
        //合并门店税额和收入合计
        List<FinRentAccountedForVo> saleListTwo = saleList.stream().filter(data -> data.getSalesVolume() != null && data.getBusinessSurcharges() != null).collect(Collectors.toList());
        MergeUtil.merge(shopInfoList, saleListTwo,
                CompanyVo::getShopId, FinRentAccountedForVo::getShopId,
                (shopInfo, accountInfo) -> {
                    BigDecimal saleMoney = accountInfo.getSalesVolume() == null ? new BigDecimal("0.00") : accountInfo.getSalesVolume().add(accountInfo.getBusinessSurcharges());
                    shopInfo.setSaleTotal(saleMoney == null ? new BigDecimal("0.00") : saleMoney);
                });

        //合并赠送金额和折扣额为优免合计,并且计算出每个门店的营业管理费
        MergeUtil.merge(shopInfoList, disCountMoneyList,
                CompanyVo::getShopId, PosDiscountDetailPayVo::getShopId,
                (shopInfo, disCountInfo) -> {
                    BigDecimal disCountMoney = disCountInfo.getTotalMoney();
                    BigDecimal exemptionMoney = shopInfo.getExemptionTotal().add(disCountMoney == null ? new BigDecimal("0.00") : disCountMoney);
                    BigDecimal BusinessManageTotal = (shopInfo.getSaleTotal().subtract(exemptionMoney))
                            .multiply((new BigDecimal(1.00).add(shopInfo.getMdTax()))).multiply(new BigDecimal(0.038));
                    shopInfo.setBusinessManageTotal(BusinessManageTotal);
                });
        return shopInfoList;
    }


    /**
     * @Description: 计算本期
     * @Param: [businessReportDayItemVos, reportPosDeskVos, repPosDetailPayVos]
     * @return: void
     * @Author: liBao
     * @Date: 2020/1/2 16:13
     */
    public void calculationCurrentMoney(List<RealTimeProfitVo> RealTimeProfitVos, RepPosDetailFoodVo repPosDetailFoodVo,
                                        Integer dataType, List<RepPosDetailFoodVo> repPosDetailFoodVos, List<FinReportVo> shopSubject, List<FinReportConfigVo> configVos,
                                        FinQueryDto queryDto, List<FinReportVo> voForLastYearSubject, List<RealProfitVo> shouldDeprList, List<RealProfitVo> layOutList,
                                        List<RealProfitVo> countLossList, List<CompanyVo> shopInfoList, List<CompanyVo> beerList, List<DishGrossProfitVo> dishBaseList,
                                        List<ShopSalaryVo> shopScoreVos, List<CompanyVo> amortSchemeListByShop, List<CompanyVo> materialCostList, List<CompanyVo> bonusVoList) {
        //赋值
        if (!FastUtils.checkNullOrEmpty(shopSubject, configVos)) {
            RealTimeProfitVos.forEach(report -> {
                //本期，本年
                calculateRealTimeProfitCurrentMoney(repPosDetailFoodVo, report, repPosDetailFoodVos, shopSubject, configVos, queryDto,
                        dataType, voForLastYearSubject, shouldDeprList, layOutList, countLossList, shopInfoList, beerList, dishBaseList, shopScoreVos,
                        amortSchemeListByShop, materialCostList, bonusVoList);
            });
        }
    }


    /**
     * @Description: 计算预算比
     * @Param: [businessReportDayItemVos, reportPosDeskVos, repPosDetailPayVos]
     * @return: void
     * @Author: liBao
     * @Date: 2020/1/2 16:13
     */
    private void calculationBudgetCompare(List<RealTimeProfitVo> RealTimeProfitVos, List<RepPosDetailFoodVo> repPosDetailFoodVos, List<FinReportVo> shopSubject,
                                          List<FinReportConfigVo> configVos, FinQueryDto queryDto, List<RealProfitBudgetVo> budgetVos,
                                          List<RealProfitVo> shouldDeprList, List<RealProfitVo> layOutList, List<RealProfitVo> countLossList, List<CompanyVo> shopInfoList,
                                          List<CompanyVo> beerList, List<DishGrossProfitVo> dishBaseList, List<ShopSalaryVo> shopScoreVos, List<CompanyVo> amortSchemeListByShop,
                                          List<CompanyVo> materialCostList, List<FinReportVo> voForLastYearSubject, List<CompanyVo> bonusList) {
        //赋值
        if (!FastUtils.checkNullOrEmpty(shopSubject, configVos)) {
            RealTimeProfitVos.forEach(report -> {
                //本期，本年
                calculateRealTimeProfitBudgetCompare(report, repPosDetailFoodVos, shopSubject, configVos, queryDto, budgetVos, shouldDeprList,
                        layOutList, countLossList, shopInfoList, beerList, dishBaseList, shopScoreVos, amortSchemeListByShop, materialCostList, voForLastYearSubject, bonusList);

            });
        }
    }

//    /**
//     *  获取净利润方法
//     * @param realTimProfitDto
//     * @return
//     */
//    private RealTimeProfitVo getNetProfit(RealTimeProfitDto realTimProfitDto){
//
//        List<RealTimeProfitVo> realTimeProfitVos = new ArrayList<>();
//        //查询预置项目项
//        BaseReportItemSetDto baseReportItemSetDto = new BaseReportItemSetDto();
//        baseReportItemSetDto.setEnteId(baseReportItemSetDto.getEnteId());
//        baseReportItemSetDto.setReportId(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT);
//        List<BaseReportItemSetVo> baseReportItemSetVos = baseReportItemSetService.findBaseReportItemSetVoByReportId(baseReportItemSetDto);
//        //初始化报表左侧项目数
//        initRealTimeProfit(realTimeProfitVos, baseReportItemSetVos);
//        //Dto转换
//        FinQueryDto queryDto = getFinQueryDto(realTimProfitDto);
//
//        //本期报表表
//        List<FinReportVo> shopSubjects = financeSubjectService.getSubjectData(queryDto);
//
//        List<FinReportConfigVo> configVos = financeSubjectService.getConfigByGroup(queryDto.getEnteId(),
//                ReportDataConstant.FinType.REALTIME_PROFIT);
//
//        //1、实收合计(主营业务收入)
//        //菜品收入
//        //查询销售菜品明细表
//        RepPosDetailFoodDto repPosDetailFoodDto = new RepPosDetailFoodDto();
//        repPosDetailFoodDto.setShopIdList(realTimProfitDto.getShopIdList());
//        repPosDetailFoodDto.setBeginDate(realTimProfitDto.getBeginDate());
//        repPosDetailFoodDto.setEndDate(realTimProfitDto.getEndDate());
//        repPosDetailFoodDto.setEnteId(realTimProfitDto.getEnteId());
//        repPosDetailFoodDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
//        RepPosDetailFoodVo repPosDetailFoodVo = repPosDetailFoodService.findFoodAllPrice(repPosDetailFoodDto);
//        BigDecimal mainBusiness = repPosDetailFoodVo.getFoodAllPrice();
//
//
//        //4、主营业务成本 -供应链
//        BigDecimal mainBusinessCost = new BigDecimal("0.00");
//        //4.1 菜品成本
//        //查询菜品成本
//        ScmQueryDto scmQueryDto = new ScmQueryDto();
//        scmQueryDto.setShopIdList(realTimProfitDto.getShopIdList());
//        scmQueryDto.setBeginDate(realTimProfitDto.getBeginDate());
//        scmQueryDto.setEndDate(realTimProfitDto.getEndDate());
//        scmQueryDto.setEnteId(realTimProfitDto.getEnteId());
//        scmQueryDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
//        scmQueryDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);
//
//        List<DishGrossProfitVo> dishBaseList = footStyleAnalysisService.getDishBaseList(scmQueryDto, null);
//        BigDecimal costOfDishes = dishBaseList.stream().map(DishGrossProfitVo::getCostAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        //4.2 酒水成本
//        BigDecimal beverageCost = new BigDecimal("0.00");
//        //4.3 杂项成本
//        RealTimeProfitVo realTimeProfitVo = realTimeProfitVos.stream().filter(data -> ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS_NAME
//                .equals(data.getItemName().trim())).collect(Collectors.toList()).get(0);
//        List<String> codeList = new ArrayList<>();
//        String[] codes = realTimeProfitVo.getItemCode().split(",");
//        for (String code : codes) {
//            codeList.add(code);
//        }
//        queryDto.setCodes(codeList);
//        List<RealProfitVo> countLossList = businessAnalysisMapper.findCountLossListByShopId(queryDto);
//        BigDecimal miscellaneousCost = countLossList.stream().map(RealProfitVo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        mainBusinessCost = costOfDishes.add(beverageCost).add(miscellaneousCost);
//        //5、其他业务支出
//        BigDecimal otherBusinessPay = new BigDecimal("0.00");
//        //根据FinType获取配置项
//        otherBusinessPay = getDataByFormula(shopSubjects, configVos, otherBusinessPay, ReportDataConstant.FinType.REALTIME_PROFIT_OTHER_BUSINESS_PAY);
//
//        //6、营业外支出
//        BigDecimal outsideBusinessPay = new BigDecimal("0.00");
//        //根据FinType获取配置项
//        outsideBusinessPay = getDataByFormula(shopSubjects, configVos, outsideBusinessPay, ReportDataConstant.FinType.REALTIME_PROFIT_OUTSIDE_BUSINESS_PAY);
//
//        //7、营业税金及附加
//        BigDecimal taxAndAdd = new BigDecimal("0.00");
//        taxAndAdd = getDataByFormula(shopSubjects, configVos, taxAndAdd, ReportDataConstant.FinType.REALTIME_PROFIT_TAXES_AND_ADD_BUSINESS);
//
//        //8.营业费用
//        BigDecimal businessMoney = new BigDecimal("0.00");
//        //8.1  营业费用_职工薪酬
//        BigDecimal staffSalaries = new BigDecimal("0.00");
//        //8.01.01  营业费用_职工薪酬_在职工资   乐才
//        BigDecimal workWages = new BigDecimal("0.00");
//        //8.01.02  营业费用_职工薪酬_离职工资
//        //根据FinType获取配置项
//        FinReportConfigVo configVoQuit = configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_QUIT_MONEY.
//                equals(data.getFinType())).collect(Collectors.toList()).get(0);
//        BigDecimal quitMoney = shopSubjects.stream().filter(data -> configVoQuit.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
//                reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        //8.01.03 营业费用_职工薪酬_奖金    TODO 乐才表暂时没有
//        BigDecimal bonus = new BigDecimal("0.00");
//        staffSalaries = workWages.add(quitMoney).add(bonus);
//
//        //10	所得税
//        //修改时间，查询最近12月数据
//        String yearTime = queryDto.getEndTime().substring(0, 4);
//        String monthTime = queryDto.getEndTime().substring(5, 7);
//        String lastYear = String.valueOf(Integer.valueOf(yearTime) - 1);
//        String beginTimeForLately = lastYear + "-" + monthTime + "-01";
//        //获取上个月的最后一天
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String endTimeForLately = queryDto.getEndTime();
//        try {
//            Date date = sdf.parse(queryDto.getEndTime());
//            Calendar c = Calendar.getInstance();
//            //设置为指定日期
//            c.setTime(date);
//            //指定日期月份减去一
//            c.add(Calendar.MONTH, -1);
//            //指定日期月份减去一后的 最大天数
//            c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
//            //获取最终的时间
//            Date lastDateOfPrevMonth = c.getTime();
//            endTimeForLately = sdf.format(lastDateOfPrevMonth);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        //查询最近12月份数据
//        FinQueryDto dto = new FinQueryDto();
//        dto.setEnteId(queryDto.getEnteId());
//        dto.setBeginTime(beginTimeForLately);
//        dto.setEndTime(endTimeForLately);
//        dto.setShopIdList(queryDto.getShopIdList());
//        dto.setShopTypeIdList(queryDto.getShopTypeIdList());
//        List<FinReportVo> voForLastYearSubject = financeSubjectService.getSubjectData(dto);
//        FinReportConfigVo configVoIncomeTax = configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_INCOME_TAX.
//                equals(data.getFinType())).collect(Collectors.toList()).get(0);
//        BigDecimal incomeTax = voForLastYearSubject.stream().filter(data -> configVoIncomeTax.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
//                reduce(BigDecimal.ZERO, BigDecimal::add);
//        incomeTax = incomeTax.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
//                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
//
//        //11	 净利润  主营业务收入-（主营业务成本+其他业务支出+营业外支出+营业税金及附加+营业费用+所得税）
//        BigDecimal netProfit = mainBusiness.subtract(mainBusinessCost).subtract(otherBusinessPay).
//                subtract(outsideBusinessPay).subtract(taxAndAdd).subtract(staffSalaries).
//                subtract(incomeTax);
//        //赋值
//        RealTimeProfitVo profitVo = new RealTimeProfitVo();
//            realTimeProfitVos.forEach(report -> {
//                if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(report.getItemCode())) {
//                    profitVo.setCurrentMoney(netProfit);
//                }
//            });
//        return profitVo;
//    }


    /**
     * @Description: 计算本期
     * @Param: [reportPosDeskVos, repPosDetailPayVos, report, dataType]
     * @return: void
     * @Author: liBao
     * @Date: 2020/2/20 11:41
     */
    private void calculateRealTimeProfitCurrentMoney(RepPosDetailFoodVo repPosDetailFoodVo, RealTimeProfitVo report, List<RepPosDetailFoodVo> repPosDetailFoodVos,
                                                     List<FinReportVo> shopSubject, List<FinReportConfigVo> configVos, FinQueryDto queryDto, Integer dataType,
                                                     List<FinReportVo> voForLastYearSubject, List<RealProfitVo> shouldDeprList, List<RealProfitVo> layOutList,
                                                     List<RealProfitVo> countLossList, List<CompanyVo> shopInfoList, List<CompanyVo> beerList, List<DishGrossProfitVo> dishBaseList,
                                                     List<ShopSalaryVo> shopScoreVos, List<CompanyVo> amortSchemeListByShop, List<CompanyVo> materialCostList, List<CompanyVo> bonusVoList) {
        //1、实收合计(主营业务收入)
        BigDecimal mainBusiness = repPosDetailFoodVo == null ? BigDecimal.ZERO : repPosDetailFoodVo.getFoodAllPrice();
        //List<RealProfitBudgetVo> budgetVos = financeSubjectService.getRealProfitBudgetList(queryDto);
        //1.2、酒水饮料
        BigDecimal beverage = CollectionUtil.isEmpty(repPosDetailFoodVos) == true ? BigDecimal.ZERO : repPosDetailFoodVos.stream().filter(data -> Arrays.asList(ReportDataConstant.RealProfitItemCode.WINE_MONEY.split(",")).
                contains(data.getFoodStyleName())).map(RepPosDetailFoodVo::getFoodAllPrice).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //1.3、杂项收入
        BigDecimal other = CollectionUtil.isEmpty(repPosDetailFoodVos) == true ? BigDecimal.ZERO : repPosDetailFoodVos.stream().filter(data -> Arrays.asList(ReportDataConstant.RealProfitItemCode.MISCELLANEOUS_MONEY.split(",")).
                contains(data.getFoodStyleName())).map(RepPosDetailFoodVo::getFoodAllPrice).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //1.1、菜品收入= 实收合计-酒水-杂项
        BigDecimal food = mainBusiness.subtract(beverage).subtract(other);

        //1、主营业务收入
        if (ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, mainBusiness);
        }

        //1.1、菜品收入 = 实收合计-酒水-杂项
        if (ReportDataConstant.RealProfitItemCode.VARIETY_OF_DISHES_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, food);
        }

        //1.2、酒水饮料
        if (ReportDataConstant.RealProfitItemCode.WINE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, beverage);
        }

        //1.3、杂项收入
        if (ReportDataConstant.RealProfitItemCode.MISCELLANEOUS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, other);
        }
        //2、其他业务收入  -供应链
        BigDecimal otherBusinessMoney = CollectionUtil.isEmpty(beerList) == true ? BigDecimal.ZERO : beerList.stream().map(CompanyVo::getBeerIntoFactoryFee).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, otherBusinessMoney);
        }
        //3、营业外收入
        BigDecimal outsideBussinessFee = BigDecimal.ZERO;
        FinReportConfigVo configVo = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_OUTSIDE_BUSINESS_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        outsideBussinessFee = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> Arrays.asList(configVo.getCodes().split(",")).
                contains(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_MONEY.equals(report.getItemCode())) {
            if (ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY.equals(dataType)) {
                report.setCurrentMoney(outsideBussinessFee);
            } else if (ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR.equals(dataType)) {
                report.setYearMoney(outsideBussinessFee);
            } else if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)) {
                report.setLastCurrentMoney(outsideBussinessFee);
            } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType)) {
                report.setLatelyCurrentMoney(outsideBussinessFee);
            }
        }

        //4、主营业务成本 -供应链
        BigDecimal mainBusinessCost;
        //4.1 菜品成本
        BigDecimal costOfDishes = CollectionUtil.isEmpty(dishBaseList) == true ? BigDecimal.ZERO : dishBaseList.stream().filter(data -> data.getCostAmount() != null).map(DishGrossProfitVo::getCostAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        //4.2 酒水成本
        BigDecimal beverageCost = CollectionUtil.isEmpty(materialCostList) == true ? BigDecimal.ZERO : materialCostList.stream().filter(data -> data.getMaterialCost() != null && ReportDataConstant.RealProfitItemCode.COST_OF_WINE.equals(data.getDescription())).
                map(CompanyVo::getMaterialCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        //4.3 杂项成本
        BigDecimal miscellaneousCost = CollectionUtil.isEmpty(countLossList) == true ? BigDecimal.ZERO : countLossList.stream().map(RealProfitVo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        mainBusinessCost = costOfDishes.add(beverageCost).add(miscellaneousCost);
        if (ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, mainBusinessCost);
        }
        if (ReportDataConstant.RealProfitItemCode.COST_OF_DISHES.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, costOfDishes);
        }
        if (ReportDataConstant.RealProfitItemCode.COST_OF_WINE.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, beverageCost);
        }
        if (ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, miscellaneousCost);
        }

        //5、其他业务支出
        BigDecimal otherBusinessPay = new BigDecimal("0.00");
        //根据FinType获取配置项
        otherBusinessPay = getDataByFormula(shopSubject, configVos, otherBusinessPay, ReportDataConstant.FinType.REALTIME_PROFIT_OTHER_BUSINESS_PAY);
        if (ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, otherBusinessPay);
        }

        //6、营业外支出
        BigDecimal outsideBusinessPay = new BigDecimal("0.00");
        //根据FinType获取配置项
        outsideBusinessPay = getDataByFormula(shopSubject, configVos, outsideBusinessPay, ReportDataConstant.FinType.REALTIME_PROFIT_OUTSIDE_BUSINESS_PAY);

        if (ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, outsideBusinessPay);
        }

        //7、营业税金及附加
        BigDecimal taxAndAdd = new BigDecimal("0.00");
        taxAndAdd = getDataByFormula(shopSubject, configVos, taxAndAdd, ReportDataConstant.FinType.REALTIME_PROFIT_TAXES_AND_ADD_BUSINESS);
        if (ReportDataConstant.RealProfitItemCode.TAXES_AND_ADD_BUSINESS.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, taxAndAdd);
        }

        //8.营业费用
        BigDecimal businessMoney = new BigDecimal("0.00");
        //8.1  营业费用_职工薪酬
        BigDecimal staffSalaries;
        //8.01.01  营业费用_职工薪酬_在职工资  乐才表
        BigDecimal workWages = CollectionUtil.isEmpty(shopScoreVos) == true ? BigDecimal.ZERO : shopScoreVos.stream().map(ShopSalaryVo::getGrossSalary).reduce(BigDecimal.ZERO, BigDecimal::add);
        //8.01.02  营业费用_职工薪酬_离职工资
        //根据FinType获取配置项
        FinReportConfigVo configVoQuit = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_QUIT_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal quitMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoQuit.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);

        //8.01.03 营业费用_职工薪酬_奖金    乐才表
        BigDecimal bonus = CollectionUtil.isEmpty(bonusVoList) == true ? BigDecimal.ZERO : bonusVoList.stream().map(CompanyVo::getBonusMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        staffSalaries = workWages.add(quitMoney).add(bonus);

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, staffSalaries);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_ONLINE_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, workWages);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_QUIT_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, quitMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_BONUS.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, bonus);
        }

        //8.02 营业费用_水电气费
        BigDecimal waterElectricGasMoney = new BigDecimal("0.00");

        //8.02.01 营业费用_水电气费_水费 最近一年累计/12/30*天数
        FinReportConfigVo configVoWater = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_WATER_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal waterMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoWater.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        waterMoney = waterMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        //8.02.02 营业费用_电费
        FinReportConfigVo configVoElectric = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ELECTRIC_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal electricMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoElectric.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        electricMoney = electricMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        //8.02.03 营业费用_燃气费

        FinReportConfigVo configVoGas = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_GAS_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal gasMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoGas.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        gasMoney = gasMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        waterElectricGasMoney = waterMoney.add(electricMoney).add(gasMoney);

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_ELECTRIC_GAS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, waterElectricGasMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, waterMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ELECTRIC_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, electricMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GAS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, gasMoney);
        }

        //8.03 营业费用_租赁费
        BigDecimal rentMoney = new BigDecimal("0.00");
        //8.03.01 营业费用_租赁费_营业租金
        FinReportConfigVo configVoRentBusiness = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_RENT_BUSINESS_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal rentBusinessMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoRentBusiness.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        rentBusinessMoney = rentBusinessMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        //8.03.02 营业费用_租赁费_宿舍租金
        FinReportConfigVo configVoRentHotel = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_RENT_HOSTEL_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal rentHotelMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoRentHotel.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        rentHotelMoney = rentHotelMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        rentMoney = rentMoney.add(rentBusinessMoney).add(rentHotelMoney);

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, rentMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_BUSINESS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, rentBusinessMoney);
        }

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_HOSTEL_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, rentHotelMoney);
        }

        //8.04 营业费用_物业费
        FinReportConfigVo configVoProperty = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PROPERTY_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal propertyMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoProperty.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        propertyMoney = propertyMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PROPERTY_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, propertyMoney);
        }
        //8.05 营业费用_赠送费用

        FinReportConfigVo configVoGive = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_GIVE_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal giveMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoGive.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        giveMoney = giveMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GIVE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, giveMoney);
        }
        //8.06	营业费用_低值易耗品
        BigDecimal lowCostLabware = new BigDecimal("0.00");

        //8.06.01	营业费用_低值易耗品_工具类
        FinReportConfigVo configVoTool = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_TOOL.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal toolMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoTool.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        toolMoney = toolMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        //8.06.02	营业费用_低值易耗品_消耗类
        FinReportConfigVo configVoConsume = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_CONSUME.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal consumeMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoConsume.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        consumeMoney = consumeMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        lowCostLabware = toolMoney.add(consumeMoney);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, lowCostLabware);
        }

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_TOOL.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, toolMoney);
        }

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_CONSUME.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, consumeMoney);
        }


        //8.07	营业费用_修理费
        FinReportConfigVo configVoRepair = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PEPAIR_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal repairMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoRepair.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PEPAIR_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, repairMoney);
        }

        //8.08	营业费用_清洁费
        BigDecimal cleanMoney = new BigDecimal("0.00");
        cleanMoney = cleanMoney.add(new BigDecimal(queryDto.getShopIdList().size() * 1650 / 12 / 30 * queryDto.getDays()));

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CLEAN_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, cleanMoney);
        }

        //8.09	营业费用_运输费
        FinReportConfigVo configVoTransport = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_TRANSPORT_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal consumeTranSport = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoTransport.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        consumeTranSport = consumeTranSport.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRANSPORT_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, consumeTranSport);
        }

        //8.10  营业费用_折旧费
        BigDecimal oldMoney = CollectionUtil.isEmpty(shouldDeprList) == true ? BigDecimal.ZERO : shouldDeprList.stream().map(RealProfitVo::getShouldDepr).reduce(BigDecimal.ZERO, BigDecimal::add);
        oldMoney = oldMoney.divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getAdjustDays()));
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_DEPRECIATION_CHARGE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, oldMoney);
        }
        //8.11	营业费用_劳动保险费
        BigDecimal laborInsurance = CollectionUtil.isEmpty(shopScoreVos) == true ? BigDecimal.ZERO : shopScoreVos.stream().map(ShopSalary::getSocialSalary).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_INSURANCE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, laborInsurance);
        }

        //8.12	营业费用_福利费
        BigDecimal welfareMoney = new BigDecimal("0.00");

        //8.12.01	营业费用_福利费_员工餐 供应链
        BigDecimal staffMeal = CollectionUtil.isEmpty(materialCostList) == true ? BigDecimal.ZERO : materialCostList.stream().filter(data -> data.getMaterialCost() != null && ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL.equals(data.getDescription())).
                map(CompanyVo::getMaterialCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        //8.12.02	营业费用_福利费_其他福利
        FinReportConfigVo configVoWelfareOther = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PEPAIR_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal welfareOtherMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoWelfareOther.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);

        welfareMoney = welfareOtherMoney.add(staffMeal);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, welfareMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, staffMeal);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_OTHER.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, welfareOtherMoney);
        }

        //8.13	营业费用_办公费
        FinReportConfigVo configVoOffice = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_OFFICE_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal officeMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoOffice.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WORK_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, officeMoney);
        }

        //8.14	营业费用_差旅费
        FinReportConfigVo configVoTravel = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_TRAVEL_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal travelMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoTravel.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRAVEL_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, travelMoney);
        }

        //8.15	营业费用_业务招待费
        FinReportConfigVo configVoEntertainment = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ENTERTAINMENT_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal entertainmentMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoEntertainment.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_BUSINESS_ENTERTAINMENT_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, entertainmentMoney);
        }

        //8.16	营业费用_广告宣传费
        BigDecimal advertisingExpenses = new BigDecimal("0.00");
        //8.16.01	营业费用_广告宣传费_广告费
        BigDecimal advertising = CollectionUtil.isEmpty(amortSchemeListByShop) == true ? BigDecimal.ZERO : amortSchemeListByShop.stream().filter(data -> data.getAmortName().contains(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY) == true)
                .map(CompanyVo::getAmortMoney).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(queryDto.getPeriodDays()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        //8.16.02	营业费用_广告宣传费_企划费用
        BigDecimal planningFee = CollectionUtil.isEmpty(layOutList) == true ? BigDecimal.ZERO : layOutList.stream().map(RealProfitVo::getAllAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //8.16.03	营业费用_广告宣传费_活动宣传费
        FinReportConfigVo configVoAdvertisingActivity = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ADVERTISING_ACTIVITY_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal advertisingActivityMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoAdvertisingActivity.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        advertisingExpenses = advertising.add(planningFee).add(advertisingActivityMoney);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, advertisingExpenses);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, advertising);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PLANNING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, planningFee);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PROPAGANDA_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, advertisingActivityMoney);
        }

        //8.17	营业费用_劳动保护费
        FinReportConfigVo configVoLaborProtect = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LABOR_PROTECTION_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal laborProtectMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoLaborProtect.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOR_PROTECTION_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, laborProtectMoney);
        }

        //8.18	营业费用_装饰费
        FinReportConfigVo configVoDecorate = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_DECORATE_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal decorateMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoDecorate.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_decorate_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, decorateMoney);
        }

        //8.19	营业费用_工会经费
        FinReportConfigVo configVoLabourUnion = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LABOUR_UNION_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal labourUnionMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoLabourUnion.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_UNION_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, labourUnionMoney);
        }

        //8.20 	营业费用_职工教育经费
        FinReportConfigVo configVoStaffEdu = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_EDUCATION_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal staffEduMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoStaffEdu.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_EDUCATION_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, staffEduMoney);
        }

        //8.21	营业费用_住房公积金
        FinReportConfigVo configVoHousingProvidentFund = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_HOUSING_PROVIDENT_FUND.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal housingProvidentFundMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoHousingProvidentFund.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_HOUSING_PROVIDENT_FUND.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, housingProvidentFundMoney);
        }

        //8.22	营业费用_长期待摊费
        BigDecimal longTermPending = CollectionUtil.isEmpty(amortSchemeListByShop) == true ? BigDecimal.ZERO : amortSchemeListByShop.stream().filter(data -> data.getAmortName().contains(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY) == true)
                .map(CompanyVo::getAmortMoney).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(queryDto.getPeriodDays()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, longTermPending);
        }

        //8.23	营业费用_咨询服务培训费
        BigDecimal consultServiceTrainingFee = new BigDecimal("0.00");
        //8.23.01	营业费用_服务咨询费_管理服务费

        BigDecimal actualSum = CollectionUtil.isEmpty(shopInfoList) == true ? BigDecimal.ZERO : shopInfoList.stream().map(CompanyVo::getBusinessManageTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        //8.23.02	营业费用_服务咨询费_咨询服务费
        FinReportConfigVo configVoConsultingService = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_CONSULTING_SERVICE.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal consultingServiceMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoConsultingService.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //8.23.03	营业费用_服务咨询费_培训费
        FinReportConfigVo configVoTrain = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_TRAIN_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal trainMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoTrain.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        consultServiceTrainingFee = actualSum.add(consultingServiceMoney).add(trainMoney);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, consultServiceTrainingFee);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_MANAGE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, actualSum);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_CONSULTING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, consultingServiceMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_TRAINING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, trainMoney);
        }
        //8.24	营业费用_商业保险
        FinReportConfigVo configVoCommercialInsurance = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_COMMERCIAL_INSURANCE.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal commercialInsuranceMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoCommercialInsurance.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMERCIAL_INSURANCE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, commercialInsuranceMoney);
        }
        //8.25	营业费用_通讯费
        FinReportConfigVo configVoCommunication = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_COMMUNICATION.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal communicationMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoCommunication.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMUNICATION_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, communicationMoney);
        }

        //8.26	营业费用_其他
        FinReportConfigVo configVoOther = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_OTHER.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal otherMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoOther.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_OTHER.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, otherMoney);
        }
        businessMoney = staffSalaries.add(waterElectricGasMoney).add(rentMoney).add(propertyMoney).add(giveMoney).add(lowCostLabware).add(repairMoney).add(cleanMoney).
                add(consumeTranSport).add(oldMoney).add(laborInsurance).add(welfareMoney).add(officeMoney).add(travelMoney).add(entertainmentMoney).
                add(advertisingExpenses).add(laborProtectMoney).add(decorateMoney).add(labourUnionMoney).add(staffEduMoney).add(housingProvidentFundMoney).
                add(longTermPending).add(consultServiceTrainingFee).add(commercialInsuranceMoney).add(communicationMoney).add(otherMoney);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, businessMoney);
        }

        //9	财务费用
        BigDecimal financeFee = new BigDecimal("0.00");
        //9.01	财务费用_利息收入
        FinReportConfigVo configVoFinanceInterestMoney = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_INTEREST_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal financeInterestMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoFinanceInterestMoney.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //9.02	财务费用_利息支出
        FinReportConfigVo configVoFinanceInterestPay = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_INTEREST_PAY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal financeInterestPay = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoFinanceInterestPay.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //9.03	财务费用_手续费
        FinReportConfigVo configVoFinancePorceduresMoney = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_PROCEDURES_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal financePorceduresMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoFinancePorceduresMoney.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //9.04	财务费用_汇兑损益
        FinReportConfigVo configVoFinanceExcGainsAndLosses = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_EXCHANGE_GAINS_AND_LOSSES.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal financeExcGainsAndLosses = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoFinanceExcGainsAndLosses.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        financeFee = financeInterestMoney.add(financeInterestPay).add(financePorceduresMoney).add(financeExcGainsAndLosses);
        if (ReportDataConstant.RealProfitItemCode.FINANCE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financeFee);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_INCOME_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financeInterestMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_IEXPENSE.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financeInterestPay);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_SERVICE_CHARGE.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financePorceduresMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_EXCHANGE_GAINS_AND_LOSSES.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financeExcGainsAndLosses);
        }

        //10	所得税
        FinReportConfigVo configVoIncomeTax = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_INCOME_TAX.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal incomeTax = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoIncomeTax.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        incomeTax = incomeTax.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        if (ReportDataConstant.RealProfitItemCode.INCOME_TAX.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, incomeTax);
        }

        //11	 净利润  主营业务收入+其他业务收入+营业外收入-（主营业务成本+其他业务支出+营业外支出+营业税金及附加+营业费用+所得税）
        BigDecimal netProfit = mainBusiness.add(otherBusinessMoney).add(outsideBussinessFee).subtract(mainBusinessCost).subtract(otherBusinessPay).
                subtract(outsideBusinessPay).subtract(taxAndAdd).subtract(businessMoney).
                subtract(incomeTax);
        if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, netProfit);
        }

        //12	 折旧前利润
        BigDecimal profitBeforeOld = netProfit.add(oldMoney);
        if (ReportDataConstant.RealProfitItemCode.PROFIT_BEFORE_DEPRECIATION.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, profitBeforeOld);
        }
    }

    /**
     * 本期本年赋值
     *
     * @param report
     * @param dataType
     * @param mainBusiness
     */
    private void setCurrentOrYearMoney(RealTimeProfitVo report, Integer dataType, BigDecimal mainBusiness) {
        if (ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY.equals(dataType)) {
            report.setCurrentMoney(mainBusiness);
        } else if (ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR.equals(dataType)) {
            report.setYearMoney(mainBusiness);
        } else if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)) {
            report.setLastCurrentMoney(mainBusiness);
        } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType)) {
            report.setLatelyCurrentMoney(mainBusiness);
        }
    }

    /**
     * @Description: 计算本期占比
     * @Param: [businessReportDayItemVos, reportPosDeskVos, repPosDetailPayVos]
     * @return: void
     * @Author: liBao
     * @Date: 2020/1/2 16:13
     */
    private void calculationCurrentProportion(Integer dataType, List<RealTimeProfitVo> RealTimeProfitVos, RepPosDetailFoodVo repPosDetailFoodVo) {
        //营业收入
        //1、实收合计(主营业务收入)
        BigDecimal mainBusiness = repPosDetailFoodVo == null ? BigDecimal.ZERO : repPosDetailFoodVo.getFoodAllPrice();
        //赋值
        RealTimeProfitVos.forEach(report -> {
            //计算占比
            calculateRealTimeProfitProportion(dataType, report, mainBusiness);

        });
    }

    /**
     * @Description: 计算本期占比
     * @Param: [reportPosDeskVos, repPosDetailPayVos, report, dataType]
     * @return: void
     * @Author: liBao
     * @Date: 2020/2/20 11:41
     */
    private void calculateRealTimeProfitProportion(Integer dataType, RealTimeProfitVo report, BigDecimal
            businessMoney) {

        //1、主营业务收入
        if (ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY.equals(report.getItemCode())) {
            BigDecimal total = new BigDecimal(100);
            report.setCurrentProportion(total);
            if (ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY.equals(dataType)) {
                report.setCurrentProportion(total);
            } else if (ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR.equals(dataType)) {
                report.setYearProportion(total);
            } else if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)) {
                BigDecimal rentPercent = BigDecimalUtils.getPercent(report.getCurrentMoney() == null ? BigDecimal.ZERO : report.getCurrentMoney().
                        subtract(report.getLastCurrentMoney() == null ? BigDecimal.ZERO : report.getLastCurrentMoney()), report.getLastCurrentMoney() == null ? BigDecimal.ZERO : report.getLastCurrentMoney());
                report.setSameCompareProportion(rentPercent);
            } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType)) {
                BigDecimal rentPercent = BigDecimalUtils.getPercent(report.getCurrentMoney() == null ? BigDecimal.ZERO : report.getCurrentMoney().
                        subtract(report.getLatelyCurrentMoney() == null ? BigDecimal.ZERO : report.getLatelyCurrentMoney()), report.getLatelyCurrentMoney() == null ? BigDecimal.ZERO : report.getLatelyCurrentMoney());
                report.setChainCompareProportion(rentPercent);
            }
        }

        //1.1、菜品收入 = 实收合计-酒水-杂项
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.VARIETY_OF_DISHES_MONEY);


        //1.2、酒水饮料
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.WINE_MONEY);

        //1.3、杂项收入
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.MISCELLANEOUS_MONEY);
        //2、其他业务收入  -供应链
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_PAY);
        //3、营业外收入
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_MONEY);

        //4、主营业务成本 -供应链
        //4.1 菜品成本

        //4.2 酒水成本
        //4.3 杂项成本

        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.COST_OF_DISHES);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.COST_OF_WINE);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS);


        //5、其他业务支出
        //根据FinType获取配置项
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_PAY);

        //6、营业外支出
        //根据FinType获取配置项
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_PAY);

        //7、营业税金及附加
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.TAXES_AND_ADD_BUSINESS);

        //8.营业费用
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY);
        //8.1  营业费用_职工薪酬
        //8.01.01  营业费用_职工薪酬_在职工资   乐才表
        //8.01.02  营业费用_职工薪酬_离职工资
        //8.01.03 营业费用_职工薪酬_奖金    TODO 乐才表暂时没有
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_ONLINE_PAY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_QUIT_PAY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_BONUS);


        //8.02 营业费用_水电气费
        //8.02.01 营业费用_水电气费_水费 最近一年累计/12/30*天数
        //8.02.02 营业费用_电费
        //8.02.03 营业费用_燃气费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_ELECTRIC_GAS_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ELECTRIC_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GAS_MONEY);

        //8.03 营业费用_租赁费
        //8.03.01 营业费用_租赁费_营业租金
        //8.03.02 营业费用_租赁费_宿舍租金
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_BUSINESS_MONEY);

        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_HOSTEL_MONEY);

        //8.04 营业费用_物业费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PROPERTY_MONEY);
        //8.05 营业费用_赠送费用
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GIVE_MONEY);
        //8.06	营业费用_低值易耗品

        //8.06.01	营业费用_低值易耗品_工具类
        //8.06.02	营业费用_低值易耗品_消耗类
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE);

        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_TOOL);

        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_CONSUME);


        //8.07	营业费用_修理费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PEPAIR_MONEY);

        //8.08	营业费用_清洁费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CLEAN_MONEY);

        //8.09	营业费用_运输费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRANSPORT_MONEY);

        //8.10  营业费用_折旧费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_DEPRECIATION_CHARGE_MONEY);
        //8.11	营业费用_劳动保险费  乐才
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_INSURANCE_MONEY);

        //8.12	营业费用_福利费

        //8.12.01	营业费用_福利费_员工餐   供应链
        //8.12.02	营业费用_福利费_其他福利
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_OTHER);

        //8.13	营业费用_办公费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WORK_MONEY);

        //8.14	营业费用_差旅费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRAVEL_MONEY);

        //8.15	营业费用_业务招待费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_BUSINESS_ENTERTAINMENT_MONEY);

        //8.16	营业费用_广告宣传费
        //8.16.01	营业费用_广告宣传费_广告费
        //8.16.02	营业费用_广告宣传费_企划费用
        //8.16.03	营业费用_广告宣传费_活动宣传费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PLANNING_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PROPAGANDA_MONEY);

        //8.17	营业费用_劳动保护费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOR_PROTECTION_MONEY);

        //8.18	营业费用_装饰费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_decorate_MONEY);

        //8.19	营业费用_工会经费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_UNION_MONEY);

        //8.20 	营业费用_职工教育经费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_EDUCATION_MONEY);

        //8.21	营业费用_住房公积金
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_HOUSING_PROVIDENT_FUND);

        //8.22	营业费用_长期待摊费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY);

        //8.23	营业费用_咨询服务培训费
        //8.23.01	营业费用_服务咨询费_管理服务费

        //8.23.02	营业费用_服务咨询费_咨询服务费
        //8.23.03	营业费用_服务咨询费_培训费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_MANAGE_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_CONSULTING_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_TRAINING_MONEY);
        //8.24	营业费用_商业保险
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMERCIAL_INSURANCE_MONEY);
        //8.25	营业费用_通讯费
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMUNICATION_MONEY);

        //8.26	营业费用_其他
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_OTHER);


        //9	财务费用
        //9.01	财务费用_利息收入
        //9.01.01	财务费用_利息支出
        //9.01.02	财务费用_手续费
        //9.01.03	财务费用_汇兑损益
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.FINANCE_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_INCOME_MONEY);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_IEXPENSE);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.FINANCE_SERVICE_CHARGE);
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.FINANCE_EXCHANGE_GAINS_AND_LOSSES);

        //10	所得税
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.INCOME_TAX);

        //11	 净利润  主营业务收入-（主营业务成本+其他业务支出+营业外支出+营业税金及附加+营业费用+所得税）
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.NET_PROFIT);

        //12	 折旧前利润
        getCurrentProportionResult(dataType, report, businessMoney, ReportDataConstant.RealProfitItemCode.PROFIT_BEFORE_DEPRECIATION);
    }

    /**
     * 实时利润分析(同比)
     *
     * @param realTimProfitDto
     * @return
     */
    @Override
    public List<RealTimeProfitVo> findRealTimeProfitSameOrChainCompare(RealTimeProfitDto realTimProfitDto, Integer
            dataType) {
        List<RealTimeProfitVo> realTimeProfitVos = new ArrayList<>();
        //查询预置项目项
        BaseReportItemSetDto baseReportItemSetDto = new BaseReportItemSetDto();
        baseReportItemSetDto.setEnteId(realTimProfitDto.getEnteId());
        baseReportItemSetDto.setReportId(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT);
        List<BaseReportItemSetVo> baseReportItemSetVos = baseReportItemSetService.findBaseReportItemSetVoByReportId(baseReportItemSetDto);
        //初始化报表左侧项目数
        initRealTimeProfit(realTimeProfitVos, baseReportItemSetVos);
        //计算同比
        calculationRealTimeProfitSameCompare(realTimeProfitVos, realTimProfitDto,
                dataType);

        //折叠或展开
        List<RealTimeProfitVo> allList = new ArrayList<>();
        List<RealTimeProfitVo> all = new ArrayList<>();
        realTimeProfitVos.forEach(
                data -> {
                    RealTimeProfitVo realTime = new RealTimeProfitVo();
                    realTime.setType(ReportDataConstant.ReportRealTimePutType.All);
                    realTime.setItemNumber(data.getItemNumber());
                    realTime.setItemName(data.getItemName());
                    realTime.setCurrentMoney(data.getCurrentMoney());
                    realTime.setLastCurrentMoney(data.getLastCurrentMoney());
                    realTime.setLatelyCurrentMoney(data.getLatelyCurrentMoney());
                    realTime.setSameCompareProportion(data.getSameCompareProportion());
                    realTime.setChainCompareProportion(data.getChainCompareProportion());
                    realTime.setItemLevel(data.getItemLevel());
                    all.add(realTime);
                }
        );
        List<RealTimeProfitVo> mainList = realTimeProfitVos.stream().filter(data -> ReportDataConstant.ReportItemLevel.LEVEL_ONE.equals(data.getItemLevel())).collect(Collectors.toList());
        mainList.forEach(
                data -> data.setType(ReportDataConstant.ReportRealTimePutType.MAIN)
        );
        allList.addAll(all);
        allList.addAll(mainList);
        return allList;
    }


    /**
     * @Description:计算同比环比
     * @Param: [businessReportDayItemVos, businessReportDayDto]
     * @return: void
     * @Author: liBao
     * @Date: 2019/12/29 14:47
     */
    private void calculationRealTimeProfitSameCompare(@NotNull List<RealTimeProfitVo> realTimeProfitVos,
                                                      RealTimeProfitDto realTimProfitDto, Integer dataType) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = sf.parse(realTimProfitDto.getBeginTime());
            endDate = sf.parse(realTimProfitDto.getEndTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        realTimProfitDto.setBeginDate(startDate);
        realTimProfitDto.setEndDate(endDate);

        //Dto转换
        FinQueryDto queryDto = getFinQueryDto(realTimProfitDto);
        List<FinReportConfigVo> configVos = financeSubjectService.getConfigByGroup(queryDto.getEnteId(),
                ReportDataConstant.FinType.REALTIME_PROFIT);


        //获取7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
        RepPosDetailPayDto repPosDetailPayDto = new RepPosDetailPayDto();
        repPosDetailPayDto.setShopIdList(queryDto.getShopIdList());
        repPosDetailPayDto.setBeginDate(queryDto.getBeginDate());
        repPosDetailPayDto.setEndDate(queryDto.getEndDate());
        repPosDetailPayDto.setEnteId(queryDto.getEnteId());
        repPosDetailPayDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        //查询收入合计
        List<FinRentAccountedForVo> saleList = baseDeskService.findSaleByCondition(queryDto);
        List<CompanyVo> shopInfoList = getBussnessManageFee(saleList, repPosDetailPayDto, queryDto);

        //查询销售菜品明细表
        RepPosDetailFoodDto repPosDetailFoodDto = new RepPosDetailFoodDto();
        repPosDetailFoodDto.setShopIdList(realTimProfitDto.getShopIdList());
        repPosDetailFoodDto.setBeginDate(realTimProfitDto.getBeginDate());
        repPosDetailFoodDto.setEndDate(realTimProfitDto.getEndDate());
        repPosDetailFoodDto.setEnteId(realTimProfitDto.getEnteId());
        repPosDetailFoodDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        //查询菜品金额明细
        List<RepPosDetailFoodVo> repPosDetailFoodVos = repPosDetailFoodService.findRepPosDetailFoodByCondition(repPosDetailFoodDto);
        //菜品收入
        RepPosDetailFoodVo repPosDetailFoodVo = new RepPosDetailFoodVo();
        repPosDetailFoodVo.setFoodAllPrice(CollectionUtil.isEmpty(saleList) == true ? BigDecimal.ZERO : saleList.stream().map(FinRentAccountedForVo::getMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add));

        //查询菜品成本
        ScmQueryDto scmQueryDto = new ScmQueryDto();
        scmQueryDto.setShopIdList(realTimProfitDto.getShopIdList());
        scmQueryDto.setBeginDate(realTimProfitDto.getBeginDate());
        scmQueryDto.setEndDate(realTimProfitDto.getEndDate());
        scmQueryDto.setEnteId(realTimProfitDto.getEnteId());
        scmQueryDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        scmQueryDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);

        List<DishGrossProfitVo> dishBaseList = footStyleAnalysisService.getDishBaseList(scmQueryDto, null);

        //乐才薪酬分析
        ShopSalaryDto shopSalaryDto = new ShopSalaryDto();
        shopSalaryDto.setShopIdList(realTimProfitDto.getShopIdList());
        shopSalaryDto.setBeginDate(realTimProfitDto.getBeginDate());
        shopSalaryDto.setEndDate(realTimProfitDto.getEndDate());
        shopSalaryDto.setEnteId(realTimProfitDto.getEnteId());
        shopSalaryDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());

        List<ShopSalaryVo> shopScoreVos = salaryAnalysisService.findSampleShopSalaryList(shopSalaryDto);


        //啤酒进场费
        List<CompanyVo> beerList = getBearIntoFactory(queryDto);

        //品牌奖金表总额
        List<CompanyVo> bonusList = getBrandBonusVos(queryDto);

        //7.16.01	营业费用_广告宣传费_广告费	金蝶-总账-凭证摊销明细里转入科目为广告费的项目合计除以期间乘以天数
        //7.22	营业费用_长期待摊费	金蝶-总账-凭证摊销明细里转入科目为长期待摊费的项目合计）除以期间乘以天数
        List<String> explanationList = new ArrayList<>();
        //广告费
        explanationList.add(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY);
        //长期待摊费
        explanationList.add(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY);

        try {
            List<String> allPeriod = DateUtils.getTwoDateAllMonth(queryDto.getBeginTime(), queryDto.getEndTime());
            queryDto.setPeriods(allPeriod);
            queryDto.setPeriodDays(allPeriod.size() * 30);
            queryDto.setExplanation(explanationList);

        } catch (Exception e) {

        }
        List<CompanyVo> amortSchemeListByShop = findAmortSchemeList(queryDto);
        //计算酒水和员工餐
        List<CompanyVo> materialCostList = getCostOfWineAndMeal(sf, queryDto);


        //本期
        List<FinReportVo> shopSubjects = financeSubjectService.getSubjectData(queryDto);

        //修改时间，查询最近12月数据
        String yearTime = queryDto.getEndTime().substring(0, 4);
        String monthTime = queryDto.getEndTime().substring(5, 7);
        String lastYear = String.valueOf(Integer.valueOf(yearTime) - 1);
        String beginTimeForLately = lastYear + "-" + monthTime + "-01";
        //获取上个月的最后一天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String endTimeForLately = queryDto.getEndTime();
        try {
            Date date = sdf.parse(queryDto.getEndTime());
            Calendar c = Calendar.getInstance();
            //设置为指定日期
            c.setTime(date);
            //指定日期月份减去一
            c.add(Calendar.MONTH, -1);
            //指定日期月份减去一后的 最大天数
            c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
            //获取最终的时间
            Date lastDateOfPrevMonth = c.getTime();
            endTimeForLately = sdf.format(lastDateOfPrevMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //查询最近12月份数据
        FinQueryDto dto = new FinQueryDto();
        dto.setEnteId(queryDto.getEnteId());
        dto.setBeginTime(beginTimeForLately);
        dto.setEndTime(endTimeForLately);
        dto.setShopIdList(queryDto.getShopIdList());
        dto.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<FinReportVo> voForLastYearSubject = financeSubjectService.getSubjectData(dto);

        //查询折旧调整单
        //设置折旧调整单的日期(上个月初到月末)
        List<RealProfitVo> shouldDeprList = getRealProfitVos(queryDto, sdf);

        //查询营业费用_广告宣传费_企划费用
        List<RealProfitVo> layOutList = businessAnalysisMapper.findAllAmountListByShopId(queryDto);

        //查询杂项成本  供应链-库存管理-盘亏单【取物料编码40.06.008、40.06.009总成本】
        RealTimeProfitVo realTimeProfitVo = realTimeProfitVos.stream().filter(data -> ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS_NAME
                .equals(data.getItemName().trim())).collect(Collectors.toList()).get(0);
        List<String> codeList = new ArrayList<>();
        String[] codes = realTimeProfitVo.getItemCode().split(",");
        for (String code : codes) {
            codeList.add(code);
        }
        queryDto.setCodes(codeList);
        List<RealProfitVo> countLossList = businessAnalysisMapper.findCountLossListByShopId(queryDto);

        //计算本期
        calculationCurrentMoney(realTimeProfitVos, repPosDetailFoodVo,
                ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY,
                repPosDetailFoodVos, shopSubjects, configVos, queryDto, voForLastYearSubject,
                shouldDeprList, layOutList, countLossList, shopInfoList, beerList, dishBaseList, shopScoreVos, amortSchemeListByShop, materialCostList, bonusList);

        //去年同期
        //查询时间重置
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
        if (dataType.equals(ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE)) {
            if (Constant.DateType.MONTH == queryDto.getDateType()) {
                if (DateUtils.ifEndOfMouth(queryDto.getEndDate())) {
                    queryDto.setEndDate(DateUtils.getLastDayOfMonth(queryDto.getBeginDate()));
                } else {
                    queryDto.setEndDate(DateUtils.subYears(queryDto.getEndDate(), Constant.Number.ONE));
                }
            } else {
                queryDto.setEndDate(DateUtils.subYears(queryDto.getEndDate(), Constant.Number.ONE));
            }
            try {
                queryDto.setBeginDate(DateUtils.subYears(simpleDateFormat.parse(queryDto.getBeginTime()), Constant.Number.ONE));
                queryDto.setBeginTime(simpleDateFormat.format(queryDto.getBeginDate()));
                queryDto.setEndTime(simpleDateFormat.format(queryDto.getEndDate()));
                //算出去年同期累计跨度
                queryDto.setDays(DateUtils.getBetweenDay(queryDto.getBeginTime(), queryDto.getEndTime(), "yyyy-MM-dd"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //环比
            List<Date> dates = DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
            queryDto.setBeginDate(dates.get(Constant.Number.ZERO));
            queryDto.setEndDate(dates.get(Constant.Number.ONE));
            queryDto.setBeginTime(simpleDateFormat.format(queryDto.getBeginDate()));
            queryDto.setEndTime(simpleDateFormat.format(queryDto.getEndDate()));
            //算出上期累计跨度
            queryDto.setDays(DateUtils.getBetweenDay(queryDto.getBeginTime(), queryDto.getEndTime(), "yyyy-MM-dd"));
        }


        List<FinReportVo> shopSubjectsYear = financeSubjectService.getSubjectData(queryDto);

        //获取7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
        RepPosDetailPayDto repPosDetailPayYearDto = new RepPosDetailPayDto();
        repPosDetailPayYearDto.setShopIdList(queryDto.getShopIdList());
        repPosDetailPayYearDto.setBeginDate(queryDto.getBeginDate());
        repPosDetailPayYearDto.setEndDate(queryDto.getEndDate());
        repPosDetailPayYearDto.setEnteId(queryDto.getEnteId());
        repPosDetailPayYearDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        //查询收入合计
        List<FinRentAccountedForVo> saleListCompare = baseDeskService.findSaleByCondition(queryDto);
        List<CompanyVo> shopInfoCompareList = getBussnessManageFee(saleListCompare, repPosDetailPayYearDto, queryDto);


        //查询销售菜品明细表
        RepPosDetailFoodDto repPosDetailFoodYearDto = new RepPosDetailFoodDto();
        repPosDetailFoodYearDto.setShopIdList(queryDto.getShopIdList());
        repPosDetailFoodYearDto.setBeginDate(queryDto.getBeginDate());
        repPosDetailFoodYearDto.setEndDate(queryDto.getEndDate());
        repPosDetailFoodYearDto.setEnteId(queryDto.getEnteId());
        repPosDetailFoodYearDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        //查询菜品金额明细
        List<RepPosDetailFoodVo> repPosDetailFoodYearVos = repPosDetailFoodService.findRepPosDetailFoodByCondition(repPosDetailFoodYearDto);
        //菜品收入
        RepPosDetailFoodVo repPosDetailFoodYearVo = new RepPosDetailFoodVo();
        repPosDetailFoodYearVo.setFoodAllPrice(CollectionUtil.isEmpty(saleListCompare) == true ? BigDecimal.ZERO : saleListCompare.stream().map(FinRentAccountedForVo::getMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add));

        //乐才薪酬分析
        ShopSalaryDto shopSalaryCompareDto = new ShopSalaryDto();
        shopSalaryCompareDto.setShopIdList(queryDto.getShopIdList());
        shopSalaryCompareDto.setBeginDate(queryDto.getBeginDate());
        shopSalaryCompareDto.setEndDate(queryDto.getEndDate());
        shopSalaryCompareDto.setEnteId(queryDto.getEnteId());
        shopSalaryCompareDto.setShopTypeIdList(queryDto.getShopTypeIdList());

        List<ShopSalaryVo> shopScoreCompareVos = salaryAnalysisService.findSampleShopSalaryList(shopSalaryCompareDto);


        //查询菜品成本
        ScmQueryDto scmQueryCompareDto = new ScmQueryDto();
        scmQueryCompareDto.setShopIdList(realTimProfitDto.getShopIdList());
        scmQueryCompareDto.setBeginDate(queryDto.getBeginDate());
        scmQueryCompareDto.setEndDate(queryDto.getEndDate());
        scmQueryCompareDto.setEnteId(realTimProfitDto.getEnteId());
        scmQueryCompareDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        scmQueryCompareDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);

        List<DishGrossProfitVo> dishBaseCompareList = footStyleAnalysisService.getDishBaseList(scmQueryCompareDto, null);

        //品牌奖金表总额
        List<CompanyVo> bonusListCompare = getBrandBonusVos(queryDto);


        //修改时间，查询最近12月数据
        String yearCompareTime = queryDto.getEndTime().substring(0, 4);
        String monthCompareTime = queryDto.getEndTime().substring(5, 7);
        String lastCompareYear = String.valueOf(Integer.valueOf(yearCompareTime) - 1);
        String beginTimeFoCompare = lastCompareYear + "-" + monthCompareTime + "-01";
        //获取上个月的最后一天
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String endTimeForCompare = queryDto.getEndTime();
        try {
            Date date = sdf1.parse(queryDto.getEndTime());
            Calendar c = Calendar.getInstance();
            //设置为指定日期
            c.setTime(date);
            //指定日期月份减去一
            c.add(Calendar.MONTH, -1);
            //指定日期月份减去一后的 最大天数
            c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
            //获取最终的时间
            Date lastDateOfPrevMonth = c.getTime();
            endTimeForCompare = sdf.format(lastDateOfPrevMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //查询最近12月份数据(财务)
        FinQueryDto dto1 = new FinQueryDto();
        dto1.setEnteId(queryDto.getEnteId());
        dto1.setBeginTime(beginTimeFoCompare);
        dto1.setEndTime(endTimeForCompare);
        dto1.setShopIdList(queryDto.getShopIdList());
        dto1.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<FinReportVo> voForCompareSubject = financeSubjectService.getSubjectData(dto1);


        //啤酒进场费
        List<CompanyVo> beerList1 = getBearIntoFactory(queryDto);
        try {
            List<String> allPeriod = DateUtils.getTwoDateAllMonth(queryDto.getBeginTime(), queryDto.getEndTime());
            queryDto.setPeriods(allPeriod);
            queryDto.setPeriodDays(allPeriod.size() * 30);
            queryDto.setExplanation(explanationList);

        } catch (Exception e) {

        }
        List<CompanyVo> amortSchemeYearListByShop = findAmortSchemeList(queryDto);
        //计算酒水和员工餐
        List<CompanyVo> materialCostYearList = getCostOfWineAndMeal(sf, queryDto);

        //查询折旧调整单
        //设置折旧调整单的日期(上个月初到月末)
        List<RealProfitVo> shouldDeprCompareList = getRealProfitVos(queryDto, sdf);
        //查询营业费用_广告宣传费_企划费用
        List<RealProfitVo> layOutCompareList = businessAnalysisMapper.findAllAmountListByShopId(queryDto);

        //查询杂项成本  供应链-库存管理-盘亏单【取物料编码40.06.008、40.06.009总成本】
        List<RealProfitVo> countLossCompareList = businessAnalysisMapper.findCountLossListByShopId(queryDto);
        if (dataType.equals(ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE)) {
            //计算去年同期
            calculationCurrentMoney(realTimeProfitVos, repPosDetailFoodYearVo,
                    ReportDataConstant.BusinessAnalysisDataType.LASTCURRENTMONEY,
                    repPosDetailFoodYearVos, shopSubjectsYear, configVos, queryDto, voForCompareSubject,
                    shouldDeprCompareList, layOutCompareList, countLossCompareList, shopInfoCompareList,
                    beerList1, dishBaseCompareList, shopScoreCompareVos, amortSchemeYearListByShop, materialCostYearList, bonusListCompare);
        } else {
            //环比，时间减一个月
            //计算去年同期
            calculationCurrentMoney(realTimeProfitVos, repPosDetailFoodYearVo,
                    ReportDataConstant.BusinessAnalysisDataType.LATELYCURRENTMONEY,
                    repPosDetailFoodYearVos, shopSubjectsYear, configVos, queryDto, voForCompareSubject,
                    shouldDeprCompareList, layOutCompareList, countLossCompareList, shopInfoCompareList,
                    beerList1, dishBaseCompareList, shopScoreCompareVos, amortSchemeYearListByShop, materialCostYearList, bonusListCompare);
        }

        //同比
        calculationCurrentProportion(dataType, realTimeProfitVos, repPosDetailFoodYearVo);
    }

    /**
     * 计算酒水和员工餐
     *
     * @param sf
     * @param queryDto
     * @return
     */
    private List<CompanyVo> getCostOfWineAndMeal(SimpleDateFormat sf, FinQueryDto queryDto) {
        //计算酒水成本和员工餐
        List<String> costTypeNameList = new ArrayList<>();
        //酒水成本
        costTypeNameList.add(ReportDataConstant.RealProfitItemCode.COST_OF_WINE);
        //员工餐
        costTypeNameList.add(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL);
        //上个月开始时间和结束时间
        Date[] lastMonthDay = DateUtils.lastMonth(queryDto.getEndDate());

        //上个月期间
        Integer lastPeriod = Integer.parseInt(sf.format(lastMonthDay[1]).substring(0, 7).replaceAll("-", ""));
        queryDto.setCostTypeName(costTypeNameList);
        queryDto.setYearNum(lastPeriod);
        try {
            //上月最后一天
            String date = sf.format(lastMonthDay[1]);
            Date date1 = sf.parse(date);
            queryDto.setEndDayOfLastMonth(date1);
        } catch (Exception e) {

        }
        return businessAnalysisMapper.findMaterialCost(queryDto);
    }

    /**
     * @param realTimProfitDto
     * @param dataType
     * @Description: 实时利润分析预算比
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    @Override
    public List<RealTimeProfitVo> findRealTimeProfitBudgetCompare(RealTimeProfitDto realTimProfitDto, Integer
            dataType) {
        List<RealTimeProfitVo> realTimeProfitVos = new ArrayList<>();
        //查询预置项目项
        BaseReportItemSetDto baseReportItemSetDto = new BaseReportItemSetDto();
        baseReportItemSetDto.setEnteId(realTimProfitDto.getEnteId());
        baseReportItemSetDto.setReportId(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT);
        List<BaseReportItemSetVo> baseReportItemSetVos = baseReportItemSetService.findBaseReportItemSetVoByReportId(baseReportItemSetDto);
        //初始化报表左侧项目数
        initRealTimeProfit(realTimeProfitVos, baseReportItemSetVos);
        //计算预算比
        calculationRealTimeProfitBudgetCompare(realTimeProfitVos, realTimProfitDto,
                dataType);

        //折叠或展开
        List<RealTimeProfitVo> allList = new ArrayList<>();
        List<RealTimeProfitVo> all = new ArrayList<>();
        realTimeProfitVos.forEach(
                data -> {
                    RealTimeProfitVo realTime = new RealTimeProfitVo();
                    realTime.setType(ReportDataConstant.ReportRealTimePutType.All);
                    realTime.setItemNumber(data.getItemNumber());
                    realTime.setItemName(data.getItemName());
                    realTime.setCurrentMoney(data.getCurrentMoney());
                    realTime.setBudgetMoney(data.getBudgetMoney());
                    realTime.setBudgetCompare(data.getBudgetCompare());
                    realTime.setItemLevel(data.getItemLevel());
                    all.add(realTime);
                }
        );
        List<RealTimeProfitVo> mainList = realTimeProfitVos.stream().filter(data -> ReportDataConstant.ReportItemLevel.LEVEL_ONE.equals(data.getItemLevel())).collect(Collectors.toList());
        mainList.forEach(
                data -> data.setType(ReportDataConstant.ReportRealTimePutType.MAIN)
        );
        allList.addAll(all);
        allList.addAll(mainList);
        return allList;
    }

    /**
     * 凭证摊销
     *
     * @param finQueryDto
     * @return
     */
    @Override
    public List<CompanyVo> findAmortSchemeList(FinQueryDto finQueryDto) {
        return businessAnalysisMapper.findAmortSchemeList(finQueryDto);
    }

    /**
     * @Description:计算预算比
     * @Param: [businessReportDayItemVos, businessReportDayDto]
     * @return: void
     * @Author: liBao
     * @Date: 2019/12/29 14:47
     */
    private void calculationRealTimeProfitBudgetCompare(@NotNull List<RealTimeProfitVo> realTimeProfitVos,
                                                        RealTimeProfitDto realTimProfitDto, Integer dataType) {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = sf.parse(realTimProfitDto.getBeginTime());
            endDate = sf.parse(realTimProfitDto.getEndTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        realTimProfitDto.setBeginDate(startDate);
        realTimProfitDto.setEndDate(endDate);

        //Dto转换
        FinQueryDto queryDto = getFinQueryDto(realTimProfitDto);
        List<FinReportConfigVo> configVos = financeSubjectService.getConfigByGroup(queryDto.getEnteId(),
                ReportDataConstant.FinType.REALTIME_PROFIT);

        //获取7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
        RepPosDetailPayDto repPosDetailPayDto = new RepPosDetailPayDto();
        repPosDetailPayDto.setShopIdList(queryDto.getShopIdList());
        repPosDetailPayDto.setBeginDate(queryDto.getBeginDate());
        repPosDetailPayDto.setEndDate(queryDto.getEndDate());
        repPosDetailPayDto.setEnteId(queryDto.getEnteId());
        repPosDetailPayDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        //查询收入合计
        List<FinRentAccountedForVo> saleListBudget = baseDeskService.findSaleByCondition(queryDto);
        List<CompanyVo> shopInfoList = getBussnessManageFee(saleListBudget, repPosDetailPayDto, queryDto);

        //查询销售菜品明细表
        RepPosDetailFoodDto repPosDetailFoodDto = new RepPosDetailFoodDto();
        repPosDetailFoodDto.setShopIdList(realTimProfitDto.getShopIdList());
        repPosDetailFoodDto.setBeginDate(startDate);
        repPosDetailFoodDto.setEndDate(endDate);
        repPosDetailFoodDto.setEnteId(realTimProfitDto.getEnteId());
        repPosDetailFoodDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        //查询菜品金额明细
        List<RepPosDetailFoodVo> repPosDetailFoodVos = repPosDetailFoodService.findRepPosDetailFoodByCondition(repPosDetailFoodDto);
        //菜品收入
        RepPosDetailFoodVo repPosDetailFoodVo = new RepPosDetailFoodVo();
        repPosDetailFoodVo.setFoodAllPrice(CollectionUtil.isEmpty(saleListBudget) == true ? BigDecimal.ZERO : saleListBudget.stream().map(FinRentAccountedForVo::getMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add));

        //查询菜品成本
        ScmQueryDto scmQueryDto = new ScmQueryDto();
        scmQueryDto.setShopIdList(realTimProfitDto.getShopIdList());
        scmQueryDto.setBeginDate(realTimProfitDto.getBeginDate());
        scmQueryDto.setEndDate(realTimProfitDto.getEndDate());
        scmQueryDto.setEnteId(realTimProfitDto.getEnteId());
        scmQueryDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        scmQueryDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);

        List<DishGrossProfitVo> dishBaseList = footStyleAnalysisService.getDishBaseList(scmQueryDto, null);

        //乐才薪酬分析
        ShopSalaryDto shopSalaryDto = new ShopSalaryDto();
        shopSalaryDto.setShopIdList(realTimProfitDto.getShopIdList());
        shopSalaryDto.setBeginDate(realTimProfitDto.getBeginDate());
        shopSalaryDto.setEndDate(realTimProfitDto.getEndDate());
        shopSalaryDto.setEnteId(realTimProfitDto.getEnteId());
        shopSalaryDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());

        List<ShopSalaryVo> shopScoreVos = salaryAnalysisService.findSampleShopSalaryList(shopSalaryDto);

        //啤酒进场费
        List<CompanyVo> beerList = getBearIntoFactory(queryDto);


        //品牌奖金表总额
        List<CompanyVo> bonusList = getBrandBonusVos(queryDto);

        //7.16.01	营业费用_广告宣传费_广告费	金蝶-总账-凭证摊销明细里转入科目为广告费的项目合计除以期间乘以天数
        //7.22	营业费用_长期待摊费	金蝶-总账-凭证摊销明细里转入科目为长期待摊费的项目合计）除以期间乘以天数
        List<String> explanationList = new ArrayList<>();
        //广告费
        explanationList.add(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY);
        //长期待摊费
        explanationList.add(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY);

        try {
            List<String> allPeriod = DateUtils.getTwoDateAllMonth(queryDto.getBeginTime(), queryDto.getEndTime());
            queryDto.setPeriods(allPeriod);
            queryDto.setPeriodDays(allPeriod.size() * 30);
            queryDto.setExplanation(explanationList);

        } catch (Exception e) {

        }
        List<CompanyVo> amortSchemeListByShop = findAmortSchemeList(queryDto);
        //计算酒水和员工餐
        List<CompanyVo> materialCostList = getCostOfWineAndMeal(sf, queryDto);


        //本期
        List<FinReportVo> tempShopSubjects = financeSubjectService.getSubjectData(queryDto);
        Map<String, List<FinReportVo>> map = tempShopSubjects.stream().collect(Collectors.groupingBy(o -> o.getShopId() + "_" + o.getAccountSubjectCode()));
        List<FinReportVo> shopSubjects = new ArrayList<>();
        map.forEach((k, v) -> {
            FinReportVo finReportVo = new FinReportVo();
            finReportVo.setDebitAmount(v.stream().map(FinReportVo::getDebitAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            finReportVo.setCreditAmount(v.stream().map(FinReportVo::getCreditAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            finReportVo.setAmount(v.stream().map(FinReportVo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            finReportVo.setShopId(v.get(0).getShopId());
            finReportVo.setAccountSubjectCode(v.get(0).getAccountSubjectCode());
            shopSubjects.add(finReportVo);
        });
        //修改时间，查询最近12月数据
        String yearTime = queryDto.getEndTime().substring(0, 4);
        String monthTime = queryDto.getEndTime().substring(5, 7);
        String lastYear = String.valueOf(Integer.valueOf(yearTime) - 1);
        String beginTimeForLately = lastYear + "-" + monthTime + "-01";
        //获取上个月的最后一天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String endTimeForLately = queryDto.getEndTime();
        try {
            Date date = sdf.parse(queryDto.getEndTime());
            Calendar c = Calendar.getInstance();
            //设置为指定日期
            c.setTime(date);
            //指定日期月份减去一
            c.add(Calendar.MONTH, -1);
            //指定日期月份减去一后的 最大天数
            c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
            //获取最终的时间
            Date lastDateOfPrevMonth = c.getTime();
            endTimeForLately = sdf.format(lastDateOfPrevMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //查询最近12月份数据
        FinQueryDto dto = new FinQueryDto();
        dto.setEnteId(queryDto.getEnteId());
        dto.setBeginTime(beginTimeForLately);
        dto.setEndTime(endTimeForLately);
        dto.setShopIdList(queryDto.getShopIdList());
        dto.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<FinReportVo> tempVoForLastYearSubject = financeSubjectService.getSubjectData(dto);
        Map<String, List<FinReportVo>> mapVo = tempVoForLastYearSubject.stream().collect(Collectors.groupingBy(o -> o.getShopId() + "_" + o.getAccountSubjectCode()));
        List<FinReportVo> voForLastYearSubject = new ArrayList<>();
        mapVo.forEach((k, v) -> {
            FinReportVo finReportVo = new FinReportVo();
            finReportVo.setDebitAmount(v.stream().map(FinReportVo::getDebitAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            finReportVo.setCreditAmount(v.stream().map(FinReportVo::getCreditAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            finReportVo.setAmount(v.stream().map(FinReportVo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            finReportVo.setShopId(v.get(0).getShopId());
            finReportVo.setAccountSubjectCode(v.get(0).getAccountSubjectCode());
            voForLastYearSubject.add(finReportVo);
        });
        //查询折旧调整单
        //设置折旧调整单的日期(上个月初到月末)
        List<RealProfitVo> shouldDeprList = getRealProfitVos(queryDto, sdf);

        //查询营业费用_广告宣传费_企划费用
        List<RealProfitVo> layOutList = businessAnalysisMapper.findAllAmountListByShopId(queryDto);

        //查询杂项成本  供应链-库存管理-盘亏单【取物料编码40.06.008、40.06.009总成本】
        RealTimeProfitVo realTimeProfit = realTimeProfitVos.stream().filter(data -> ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS_NAME
                .equals(data.getItemName().trim())).collect(Collectors.toList()).get(0);
        List<String> codeList = new ArrayList<>();
        String[] codes = realTimeProfit.getItemCode().split(",");
        for (String code : codes) {
            codeList.add(code);
        }
        queryDto.setCodes(codeList);
        List<RealProfitVo> countLossList = businessAnalysisMapper.findCountLossListByShopId(queryDto);

        //计算本期
        calculationCurrentMoney(realTimeProfitVos, repPosDetailFoodVo,
                ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY,
                repPosDetailFoodVos, shopSubjects, configVos, queryDto, voForLastYearSubject,
                shouldDeprList, layOutList, countLossList, shopInfoList, beerList, dishBaseList, shopScoreVos, amortSchemeListByShop, materialCostList, bonusList);
        //查询菜品金额明细按照门店分组
        List<RepPosDetailFoodVo> repPosDetailFoodVosGyShopId = repPosDetailFoodService.findRepPosDetailFoodList(repPosDetailFoodDto);

        //查询实时利润预算设置项
        List<RealProfitBudgetVo> budgetVos = financeSubjectService.getRealProfitBudgetList(queryDto);
        //计算预算比
        calculationBudgetCompare(realTimeProfitVos, repPosDetailFoodVosGyShopId, shopSubjects, configVos, queryDto, budgetVos, shouldDeprList,
                layOutList, countLossList, shopInfoList, beerList, dishBaseList, shopScoreVos, amortSchemeListByShop, materialCostList, voForLastYearSubject, bonusList);

        //统计求和的项目预算
        //酒水
        RealTimeProfitVo wine = realTimeProfitVos.stream().filter(data -> data.getItemCode().equals(ReportDataConstant.RealProfitItemCode.WINE_MONEY)).collect(Collectors.toList()).get(0);
        //杂项
        RealTimeProfitVo miscellaneous = realTimeProfitVos.stream().filter(data -> data.getItemCode().equals(ReportDataConstant.RealProfitItemCode.MISCELLANEOUS_MONEY)).collect(Collectors.toList()).get(0);
        //主营业收入
        RealTimeProfitVo mainBusiness = realTimeProfitVos.stream().filter(data -> data.getItemCode().equals(ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY)).collect(Collectors.toList()).get(0);


        //其他业务支出
        RealTimeProfitVo otherBusinessPay = realTimeProfitVos.stream().filter(data -> data.getItemCode().equals(ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_PAY)).collect(Collectors.toList()).get(0);

        //营业外支出
        RealTimeProfitVo outsideBusinessPay = realTimeProfitVos.stream().filter(data -> data.getItemCode().equals(ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_PAY)).collect(Collectors.toList()).get(0);

        //营业税金及附加
        RealTimeProfitVo taxesAndAddBusiness = realTimeProfitVos.stream().filter(data -> data.getItemCode().equals(ReportDataConstant.RealProfitItemCode.TAXES_AND_ADD_BUSINESS)).collect(Collectors.toList()).get(0);

        //所得税
        RealTimeProfitVo incomeTax = realTimeProfitVos.stream().filter(data -> data.getItemCode().equals(ReportDataConstant.RealProfitItemCode.INCOME_TAX)).collect(Collectors.toList()).get(0);


        //主营业务成本
        BigDecimal mainBusinessCostBudget = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.TWO) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("4.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal mainBusinessCostCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.TWO) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("4.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);


        //营业费用-职工薪资 8.01
        BigDecimal businessStaffPay = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("8.01.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal businessStaffPayCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("8.01.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);

        //营业费用_水电气费 8.02
        BigDecimal waterElectricGas = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("8.02.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal waterElectricGasCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("8.02.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);

        //营业费用_租赁费 8.03
        BigDecimal rent = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("8.04.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal rentCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("8.04.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);

        //营业费用_低值易耗品 8.06
        BigDecimal lowcostlabware = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("8.06.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal lowcostlabwareCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("8.06.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);

        //营业费用_折旧费 8.10
        RealTimeProfitVo depreciationCharge = realTimeProfitVos.stream().filter(data -> data.getItemCode().equals(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_DEPRECIATION_CHARGE_MONEY)).collect(Collectors.toList()).get(0);


        //营业费用_福利费 8.12
        BigDecimal welfareFunds = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("8.12.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal welfareFundsCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("8.12.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);

        //营业费用_广告宣传费 8.16
        BigDecimal advertisingExpenses = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("8.16.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal advertisingExpensesCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("8.16.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);
        //8.23	营业费用_咨询服务培训费
        BigDecimal consultingServiceTraining = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("8.23.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal consultingServiceTrainingCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.THREE) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("8.23.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);
        //9	财务费用
        BigDecimal finance = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.TWO) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("9.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal financeCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.TWO) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("9.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);

        //给项目赋值
        for (RealTimeProfitVo realTimeProfitVo : realTimeProfitVos) {
            if (ReportDataConstant.RealProfitItemCode.VARIETY_OF_DISHES_MONEY.equals(realTimeProfitVo.getItemCode())) {
                //1.1 菜品收入
                BigDecimal dishes = mainBusiness.getBudgetMoney() == null ? BigDecimal.ZERO : mainBusiness.getBudgetMoney().subtract(wine.getBudgetMoney() == null ? BigDecimal.ZERO : wine.getBudgetMoney()).subtract(miscellaneous.getBudgetMoney() == null ? BigDecimal.ZERO : miscellaneous.getBudgetMoney());
                BigDecimal dishesCompare = mainBusiness.getBudgetCompare() == null ? BigDecimal.ZERO : mainBusiness.getBudgetCompare().subtract(wine.getBudgetCompare() == null ? BigDecimal.ZERO : wine.getBudgetCompare()).subtract(miscellaneous.getBudgetCompare() == null ? BigDecimal.ZERO : miscellaneous.getBudgetCompare());
                realTimeProfitVo.setBudgetMoney(dishes);
                realTimeProfitVo.setBudgetCompare(dishesCompare);
            } else if (ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(realTimeProfitVo.getItemCode())) {
                realTimeProfitVo.setBudgetMoney(mainBusinessCostBudget);
                realTimeProfitVo.setBudgetCompare(mainBusinessCostCompare);
            } else if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY.equals(realTimeProfitVo.getItemCode())) {
                //8.01 营业费用_职工薪酬
                realTimeProfitVo.setBudgetMoney(businessStaffPay);
                realTimeProfitVo.setBudgetCompare(businessStaffPayCompare);
            } else if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_ELECTRIC_GAS_MONEY.equals(realTimeProfitVo.getItemCode())) {
                //8.02	营业费用_水电气费
                realTimeProfitVo.setBudgetMoney(waterElectricGas);
                realTimeProfitVo.setBudgetCompare(waterElectricGasCompare);
            } else if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_MONEY.equals(realTimeProfitVo.getItemCode())) {
                //8.03	营业费用_租赁费
                realTimeProfitVo.setBudgetMoney(rent);
                realTimeProfitVo.setBudgetCompare(rentCompare);
            } else if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE.equals(realTimeProfitVo.getItemCode())) {
                //8.06 营业费用_低值易耗品
                realTimeProfitVo.setBudgetMoney(lowcostlabware);
                realTimeProfitVo.setBudgetCompare(lowcostlabwareCompare);
            } else if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS.equals(realTimeProfitVo.getItemCode())) {
                //8.12	营业费用_福利费
                realTimeProfitVo.setBudgetMoney(welfareFunds);
                realTimeProfitVo.setBudgetCompare(welfareFundsCompare);
            } else if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_MONEY.equals(realTimeProfitVo.getItemCode())) {
                //8.16 营业费用_广告宣传费
                realTimeProfitVo.setBudgetMoney(advertisingExpenses);
                realTimeProfitVo.setBudgetCompare(advertisingExpensesCompare);
            } else if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_MONEY.equals(realTimeProfitVo.getItemCode())) {
                //8.23	营业费用_咨询服务培训费
                realTimeProfitVo.setBudgetMoney(consultingServiceTraining);
                realTimeProfitVo.setBudgetCompare(consultingServiceTrainingCompare);
            } else if (ReportDataConstant.RealProfitItemCode.FINANCE_MONEY.equals(realTimeProfitVo.getItemCode())) {
                //9	财务费用
                realTimeProfitVo.setBudgetMoney(finance);
                realTimeProfitVo.setBudgetCompare(financeCompare);
            }
        }
        //8	营业费用
        BigDecimal business = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.TWO) && data.getBudgetMoney() != null && data.getItemNumber().startsWith("8.")).
                map(RealTimeProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal businessCompare = realTimeProfitVos.stream().filter(data -> data.getReportId().equals(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT) &&
                data.getItemLevel().equals(ReportDataConstant.RealProfitItemLevel.TWO) && data.getBudgetCompare() != null && data.getItemNumber().startsWith("8.")).
                map(RealTimeProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add);
        for (RealTimeProfitVo realTimeProfitVo : realTimeProfitVos) {
            if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_MONEY.equals(realTimeProfitVo.getItemCode())) {
                //8	营业费用
                realTimeProfitVo.setBudgetMoney(business == null ? BigDecimal.ZERO : business);
                realTimeProfitVo.setBudgetCompare(businessCompare == null ? BigDecimal.ZERO : businessCompare);
            }
        }
        // 净利润
        BigDecimal netProfit = mainBusiness.getBudgetMoney() == null ? BigDecimal.ZERO : mainBusiness.getBudgetMoney().subtract(mainBusinessCostBudget == null ? BigDecimal.ZERO : mainBusinessCostBudget).subtract(otherBusinessPay.getBudgetMoney() == null ? BigDecimal.ZERO : otherBusinessPay.getBudgetMoney()).
                subtract(outsideBusinessPay.getBudgetMoney() == null ? BigDecimal.ZERO : outsideBusinessPay.getBudgetMoney()).subtract(taxesAndAddBusiness.getBudgetMoney() == null ? BigDecimal.ZERO : taxesAndAddBusiness.getBudgetMoney()).subtract(business == null ? BigDecimal.ZERO : business).subtract(incomeTax.getBudgetMoney() == null ? BigDecimal.ZERO : incomeTax.getBudgetMoney());
        BigDecimal netProfitCompare = mainBusiness.getBudgetCompare() == null ? BigDecimal.ZERO : mainBusiness.getBudgetCompare().subtract(mainBusinessCostCompare == null ? BigDecimal.ZERO : mainBusinessCostCompare).subtract(otherBusinessPay.getBudgetCompare() == null ? BigDecimal.ZERO : otherBusinessPay.getBudgetCompare()).
                subtract(outsideBusinessPay.getBudgetCompare() == null ? BigDecimal.ZERO : outsideBusinessPay.getBudgetCompare()).subtract(taxesAndAddBusiness.getBudgetCompare() == null ? BigDecimal.ZERO : taxesAndAddBusiness.getBudgetCompare()).subtract(businessCompare == null ? BigDecimal.ZERO : businessCompare).subtract(incomeTax.getBudgetCompare() == null ? BigDecimal.ZERO : incomeTax.getBudgetCompare());

        //折旧前利润
        BigDecimal profitBeforeDepreciation = netProfit.add(depreciationCharge.getBudgetMoney());
        BigDecimal profitBeforeDepreciationCompare = netProfitCompare.add(depreciationCharge.getBudgetCompare());

        for (RealTimeProfitVo realTimeProfitVo : realTimeProfitVos) {
            if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(realTimeProfitVo.getItemCode())) {
                realTimeProfitVo.setBudgetMoney(netProfit);
                realTimeProfitVo.setBudgetCompare(businessCompare);
            } else if (ReportDataConstant.RealProfitItemCode.PROFIT_BEFORE_DEPRECIATION.equals(realTimeProfitVo.getItemCode())) {
                realTimeProfitVo.setBudgetMoney(profitBeforeDepreciation);
                realTimeProfitVo.setBudgetCompare(profitBeforeDepreciationCompare);
            }
        }
    }

    /**
     * @Description: 计算预算
     * @Param: [reportPosDeskVos, repPosDetailPayVos, report, dataType]
     * @return: void
     * @Author: liBao
     * @Date: 2020/2/20 11:41
     */
    private void calculateRealTimeProfitBudgetCompare(RealTimeProfitVo report, List<RepPosDetailFoodVo> repPosDetailFoodVos, List<FinReportVo> shopSubject,
                                                      List<FinReportConfigVo> configVos, FinQueryDto queryDto, List<RealProfitBudgetVo> budgetVos, List<RealProfitVo> shouldDeprList,
                                                      List<RealProfitVo> layOutList, List<RealProfitVo> countLossList, List<CompanyVo> shopInfoList, List<CompanyVo> beerList,
                                                      List<DishGrossProfitVo> dishBaseList, List<ShopSalaryVo> shopScoreVos, List<CompanyVo> amortSchemeListByShop, List<CompanyVo> materialCostList,
                                                      List<FinReportVo> voForLastYearSubject, List<CompanyVo> bonusList) {


        //1、实收合计(主营业务收入)

        //1.2、酒水饮料

        //1.3、杂项收入

        //1.1、菜品收入= 实收合计-酒水-杂项


        //1、主营业务收入
        if (ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY.equals(report.getItemCode())) {
            getFoodBudgetMethod(ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY, report, repPosDetailFoodVos, queryDto, budgetVos);
        }

        //1.1、菜品收入 = 实收合计-酒水-杂项
//        if (ReportDataConstant.RealProfitItemCode.VARIETY_OF_DISHES_MONEY.equals(report.getItemCode())) {
//            getFoodBudgetMethod(ReportDataConstant.RealProfitItemCode.VARIETY_OF_DISHES_MONEY, report, repPosDetailFoodVos, queryDto, budgetVos);
//        }

        //1.2、酒水饮料
        if (ReportDataConstant.RealProfitItemCode.WINE_MONEY.equals(report.getItemCode())) {
            getFoodBudgetMethod(ReportDataConstant.RealProfitItemCode.WINE_MONEY, report, repPosDetailFoodVos, queryDto, budgetVos);
        }

        //1.3、杂项收入
        if (ReportDataConstant.RealProfitItemCode.MISCELLANEOUS_MONEY.equals(report.getItemCode())) {
            getFoodBudgetMethod(ReportDataConstant.RealProfitItemCode.MISCELLANEOUS_MONEY, report, repPosDetailFoodVos, queryDto, budgetVos);
        }
        //2、其他业务收入  -供应链
        if (ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_MONEY.equals(report.getItemCode())) {
            Integer type = ReportDataConstant.BudgetType.BEER_INTO_FACTORY;
            getBudgetForShopList(report, queryDto, budgetVos, beerList, type);
        }
        //3、营业外收入
        if (ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_OUTSIDE_BUSINESS_MONEY);

        }

        //4、主营业务成本 -供应链
        //4.1 菜品成本
        //4.2 酒水成本
        //4.3 杂项成本
        if (ReportDataConstant.RealProfitItemCode.COST_OF_DISHES.equals(report.getItemCode())) {
//            report.setBudgetMoney(vo.getBudget());
//            report.setBudgetCompare( BigDecimalUtils.getPercent(report.getCurrentMoney().subtract(vo.getBudget()),vo.getBudget()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //设置表有效期
            Date startTime = new Date();
            Date endTime = new Date();
            Date start = new Date();
            Date end = new Date();

            Map<String, List<DishGrossProfitVo>> shopCollect = dishBaseList.stream().filter(fin -> fin.getShopId() != null && fin.getCostAmount() != null).collect(Collectors.groupingBy(DishGrossProfitVo::getShopId));
            List<FinReportVo> finList = new ArrayList<>();
            Integer day = new Integer(1);
            for (RealProfitBudgetVo vo : budgetVos) {
                for (Map.Entry<String, List<DishGrossProfitVo>> entry : shopCollect.entrySet()) {
                    if (report.getItemName().trim().equals(vo.getProject().trim()) && entry.getKey().equals(vo.getShopId())) {
                        try {
                            startTime = sdf.parse(vo.getBeginDate());
                            endTime = sdf.parse(vo.getEndDate());
                            start = sdf.parse(queryDto.getBeginTime());
                            end = sdf.parse(queryDto.getEndTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //将字符串形式的时间转化为Date类型的时间
                        if (startTime.getTime() <= start.getTime() && endTime.getTime() >= end.getTime()) {
                            FinReportVo fin = new FinReportVo();
                            fin.setBudgetMoney(vo.getBudget().divide(new BigDecimal(day), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays())));
                            fin.setBudgetCompare(BigDecimalUtils.getPercent(entry.getValue().stream().map(DishGrossProfitVo::getCostAmount).reduce(BigDecimal.ZERO, BigDecimal::add).subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                            finList.add(fin);
                        }
                    }
                }
            }
            report.setBudgetMoney(finList.stream().map(FinReportVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
            report.setBudgetCompare(finList.stream().map(FinReportVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        if (ReportDataConstant.RealProfitItemCode.COST_OF_WINE.equals(report.getItemCode())) {
            Integer type = ReportDataConstant.BudgetType.WINE_MEAL_MONEY;
            List<CompanyVo> wineList = CollectionUtil.isEmpty(materialCostList) == true ? null : materialCostList.stream().filter(data -> ReportDataConstant.RealProfitItemCode.COST_OF_WINE.equals(data.getDescription())).collect(Collectors.toList());
            getBudgetForShopList(report, queryDto, budgetVos, wineList, type);

        }
        if (ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS.equals(report.getItemCode())) {
            Integer type = ReportDataConstant.BudgetType.COUNTLOSS;
            getBudgetForNormal(report, queryDto, budgetVos, countLossList, type);
        }

        //5、其他业务支出
        if (ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_PAY.equals(report.getItemCode())) {
            getBudgetHasFormulExp(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_OTHER_BUSINESS_PAY);
        }

        //6、营业外支出
        if (ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_PAY.equals(report.getItemCode())) {
            getBudgetHasFormulExp(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_OUTSIDE_BUSINESS_PAY);
        }

        //7、营业税金及附加
        if (ReportDataConstant.RealProfitItemCode.TAXES_AND_ADD_BUSINESS.equals(report.getItemCode())) {
            getBudgetHasFormulExp(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_TAXES_AND_ADD_BUSINESS);
        }

        //8.营业费用
        //8.1  营业费用_职工薪酬
        //8.01.01  营业费用_职工薪酬_在职工资
        //8.01.02  营业费用_职工薪酬_离职工资
        //8.01.03 营业费用_职工薪酬_奖金    TODO 乐才表暂时没有

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY.equals(report.getItemCode())) {

        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_ONLINE_PAY.equals(report.getItemCode())) {
            getSalaryBudget(report, queryDto, budgetVos, shopScoreVos, ReportDataConstant.BudgetType.SHOULD_PAY_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_QUIT_PAY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_QUIT_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_BONUS.equals(report.getItemCode())) {
            Integer type = ReportDataConstant.BudgetType.STAFF_PAY_BONUS;
            getBudgetForShopList(report, queryDto, budgetVos, bonusList, type);
        }

        //8.02 营业费用_水电气费
        //8.02.01 营业费用_水电气费_水费 最近一年累计/12/30*天数
        //8.02.02 营业费用_电费
        //8.02.03 营业费用_燃气费-
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_ELECTRIC_GAS_MONEY.equals(report.getItemCode())) {
            //getBudget(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_QUIT_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_WATER_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ELECTRIC_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ELECTRIC_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GAS_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_GAS_MONEY);
        }

        //8.03 营业费用_租赁费
        //8.03.01 营业费用_租赁费_营业租金
        //8.03.02 营业费用_租赁费_宿舍租金

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_MONEY.equals(report.getItemCode())) {
            //getBudget(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_GAS_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_BUSINESS_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_RENT_BUSINESS_MONEY);
        }

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_HOSTEL_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_RENT_HOSTEL_MONEY);
        }

        //8.04 营业费用_物业费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PROPERTY_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PROPERTY_MONEY);
        }
        //8.05 营业费用_赠送费用
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GIVE_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_GIVE_MONEY);
        }
        //8.06	营业费用_低值易耗品
        //8.06.01	营业费用_低值易耗品_工具类
        //8.06.02	营业费用_低值易耗品_消耗类

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE.equals(report.getItemCode())) {
            //getBudget(report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_TOOL);
        }

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_TOOL.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_TOOL);
        }

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_CONSUME.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_CONSUME);
        }


        //8.07	营业费用_修理费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PEPAIR_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_CONSUME);
        }

        //8.08	营业费用_清洁费
        BigDecimal cleanMoney = new BigDecimal("0.00");
        cleanMoney = cleanMoney.add(new BigDecimal(1650 / 12 / 30 * queryDto.getDays()));
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CLEAN_MONEY.equals(report.getItemCode())) {
            BigDecimal clean = new BigDecimal("0.00");
            List<String> shopIdList = queryDto.getShopIdList();
            for (RealProfitBudgetVo vo : budgetVos) {
                for (String shopId : shopIdList) {
                    if (report.getItemName().trim().equals(vo.getProject().trim()) && shopId.equals(vo.getShopId())) {
                        clean = clean.add(vo.getBudget());
                    }
                }
            }
            report.setBudgetMoney(clean);
            report.setBudgetCompare(BigDecimalUtils.getPercent(cleanMoney.multiply(CollectionUtil.isEmpty(shopIdList) == true ? BigDecimal.ZERO : new BigDecimal(shopIdList.size())).subtract(clean), clean));
        }

        //8.09	营业费用_运输费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRANSPORT_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_TRANSPORT_MONEY);
        }

        //8.10  营业费用_折旧费
        BigDecimal oldMoney = new BigDecimal("0.00");
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_DEPRECIATION_CHARGE_MONEY.equals(report.getItemCode())) {
            Integer type = ReportDataConstant.BudgetType.SHOULDDEPR;
            getBudgetForNormal(report, queryDto, budgetVos, shouldDeprList, type);
        }
        //8.11	营业费用_劳动保险费  乐才
        BigDecimal laborInsurance = new BigDecimal("0.00");
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_INSURANCE_MONEY.equals(report.getItemCode())) {
            getSalaryBudget(report, queryDto, budgetVos, shopScoreVos, ReportDataConstant.BudgetType.SOCIAL_MONEY);
        }

        //8.12	营业费用_福利费
        //       BigDecimal welfareMoney = new BigDecimal("0.00");

        //8.12.01	营业费用_福利费_员工餐
        //      BigDecimal staffMeal = new BigDecimal("0.00");
        //8.12.02	营业费用_福利费_其他福利
//        FinReportConfigVo configVoWelfareOther = configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PEPAIR_MONEY.
//                equals(data.getFinType())).collect(Collectors.toList()).get(0);
//        BigDecimal welfareOtherMoney = shopSubject.stream().filter(data -> configVoWelfareOther.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
//                reduce(BigDecimal.ZERO, BigDecimal::add);

        //       welfareMoney = welfareOtherMoney.add(staffMeal);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS.equals(report.getItemCode())) {
            //getBudget(report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PEPAIR_MONEY);
            Integer type = ReportDataConstant.BudgetType.WINE_MEAL_MONEY;
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL.equals(report.getItemCode())) {
            List<CompanyVo> mealList = CollectionUtil.isEmpty(materialCostList) == true ? null : materialCostList.stream().filter(data -> ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL.equals(data.getDescription())).collect(Collectors.toList());
            Integer type = ReportDataConstant.BudgetType.WINE_MEAL_MONEY;
            getBudgetForShopList(report, queryDto, budgetVos, mealList, type);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_OTHER.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PEPAIR_MONEY);
        }

        //8.13	营业费用_办公费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WORK_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_OFFICE_MONEY);
        }

        //8.14	营业费用_差旅费

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRAVEL_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_TRAVEL_MONEY);
        }

        //8.15	营业费用_业务招待费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_BUSINESS_ENTERTAINMENT_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ENTERTAINMENT_MONEY);
        }

        //8.16	营业费用_广告宣传费
        BigDecimal advertisingExpenses = new BigDecimal("0.00");
        //8.16.01	营业费用_广告宣传费_广告费
        BigDecimal advertising = new BigDecimal("0.00");
        //8.16.02	营业费用_广告宣传费_企划费用
        BigDecimal planningFee = new BigDecimal("0.00");
        //8.16.03	营业费用_广告宣传费_活动宣传费
        FinReportConfigVo configVoAdvertisingActivity = configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ADVERTISING_ACTIVITY_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal advertisingActivityMoney = shopSubject.stream().filter(data -> configVoAdvertisingActivity.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        advertisingExpenses = advertising.add(planningFee).add(advertisingActivityMoney);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_MONEY.equals(report.getItemCode())) {
            //getBudget(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ADVERTISING_ACTIVITY_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY.equals(report.getItemCode())) {
            Integer type = ReportDataConstant.BudgetType.ADVERTISING_MONEY;
            List<CompanyVo> amortList = amortSchemeListByShop.stream().filter(data -> ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY.equals(data.getAmortName())).collect(Collectors.toList());
            getBudgetForShopList(report, queryDto, budgetVos, amortList, type);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PLANNING_MONEY.equals(report.getItemCode())) {
            Integer type = ReportDataConstant.BudgetType.ALLMOUNT;
            getBudgetForNormal(report, queryDto, budgetVos, layOutList, type);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PROPAGANDA_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ADVERTISING_ACTIVITY_MONEY);
        }

        //8.17	营业费用_劳动保护费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOR_PROTECTION_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LABOR_PROTECTION_MONEY);
        }

        //8.18	营业费用_装饰费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_decorate_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_DECORATE_MONEY);
        }

        //8.19	营业费用_工会经费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_UNION_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LABOUR_UNION_MONEY);
        }

        //8.20 	营业费用_职工教育经费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_EDUCATION_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_EDUCATION_MONEY);
        }

        //8.21	营业费用_住房公积金
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_HOUSING_PROVIDENT_FUND.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_HOUSING_PROVIDENT_FUND);
        }

        //8.22	营业费用_长期待摊费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY.equals(report.getItemCode())) {
            Integer type = ReportDataConstant.BudgetType.UNAMORTIZED_MONEY;
            List<CompanyVo> amortList = amortSchemeListByShop.stream().filter(data -> ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY.equals(data.getAmortName())).collect(Collectors.toList());
            getBudgetForShopList(report, queryDto, budgetVos, amortList, type);
        }

        //8.23	营业费用_咨询服务培训费
        //8.23.01	营业费用_服务咨询费_管理服务费
        //8.23.02	营业费用_服务咨询费_咨询服务费
        //8.23.03	营业费用_服务咨询费_培训费

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_MONEY.equals(report.getItemCode())) {
            //getBudget(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_HOUSING_PROVIDENT_FUND);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_MANAGE_MONEY.equals(report.getItemCode())) {
            Integer type = ReportDataConstant.BudgetType.SERVICE_MANAGE;
            getBudgetForShopList(report, queryDto, budgetVos, shopInfoList, type);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_CONSULTING_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_CONSULTING_SERVICE);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_TRAINING_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_TRAIN_MONEY);
        }
        //8.24	营业费用_商业保险

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMERCIAL_INSURANCE_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_COMMERCIAL_INSURANCE);
        }
        //8.25	营业费用_通讯费
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMUNICATION_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_COMMUNICATION);
        }

        //8.26	营业费用_其他
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_OTHER.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_OTHER);
        }
        //9	财务费用
        //9.01	财务费用_利息收入
        //9.01.01	财务费用_利息支出
        //9.01.02	财务费用_手续费
        //9.01.03	财务费用_汇兑损益

        if (ReportDataConstant.RealProfitItemCode.FINANCE_MONEY.equals(report.getItemCode())) {
            //getBudget(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_INTEREST_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_INCOME_MONEY.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_INTEREST_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_IEXPENSE.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_INTEREST_PAY);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_SERVICE_CHARGE.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_PROCEDURES_MONEY);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_EXCHANGE_GAINS_AND_LOSSES.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.DEBIT, report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_EXCHANGE_GAINS_AND_LOSSES);
        }

        //10	所得税

        if (ReportDataConstant.RealProfitItemCode.INCOME_TAX.equals(report.getItemCode())) {
            getBudget(ReportDataConstant.RealProfitGetValueType.AMOUNT, report, voForLastYearSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_EXCHANGE_GAINS_AND_LOSSES);
        }

        //11	 净利润  主营业务收入-（主营业务成本+其他业务支出+营业外支出+营业税金及附加+营业费用+所得税）
        if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(report.getItemCode())) {
            //getBudget(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_EXCHANGE_GAINS_AND_LOSSES);
        }

        //12	 折旧前利润
        if (ReportDataConstant.RealProfitItemCode.PROFIT_BEFORE_DEPRECIATION.equals(report.getItemCode())) {
            //getBudget(report, shopSubject, configVos, queryDto, budgetVos, ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_EXCHANGE_GAINS_AND_LOSSES);
        }
    }

    /**
     * 乐才薪酬分析预算方法抽取
     *
     * @param report
     * @param queryDto
     * @param budgetVos
     * @param shopScoreVos
     * @param shouldPayMoney
     */
    private void getSalaryBudget(RealTimeProfitVo report, FinQueryDto queryDto, List<RealProfitBudgetVo> budgetVos, List<ShopSalaryVo> shopScoreVos, Integer shouldPayMoney) {
        Integer type = shouldPayMoney;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //设置表有效期
        Date startTime = new Date();
        Date endTime = new Date();
        Date start = new Date();
        Date end = new Date();

        Map<String, List<ShopSalaryVo>> shopSalary = shopScoreVos.stream().filter(fin -> fin.getShopId() != null).collect(Collectors.groupingBy(ShopSalaryVo::getShopId));
        List<FinReportVo> finList = new ArrayList<>();
        Integer day = new Integer(1);
        for (RealProfitBudgetVo vo : budgetVos) {
            for (Map.Entry<String, List<ShopSalaryVo>> entry : shopSalary.entrySet()) {
                if (report.getItemName().trim().equals(vo.getProject().trim()) && entry.getKey().equals(vo.getShopId())) {
                    try {
                        startTime = sdf.parse(vo.getBeginDate());
                        endTime = sdf.parse(vo.getEndDate());
                        start = sdf.parse(queryDto.getBeginTime());
                        end = sdf.parse(queryDto.getEndTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //将字符串形式的时间转化为Date类型的时间
                    if (startTime.getTime() <= start.getTime() && endTime.getTime() >= end.getTime()) {
                        FinReportVo fin = new FinReportVo();
                        fin.setBudgetMoney(vo.getBudget().divide(new BigDecimal(day), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays())));
                        if (ReportDataConstant.BudgetType.SOCIAL_MONEY.equals(type)) {
                            fin.setBudgetCompare(BigDecimalUtils.getPercent(entry.getValue().stream().map(ShopSalaryVo::getSocialSalary).reduce(BigDecimal.ZERO, BigDecimal::add).subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        } else if (ReportDataConstant.BudgetType.SHOULD_PAY_MONEY.equals(type)) {
                            fin.setBudgetCompare(BigDecimalUtils.getPercent(entry.getValue().stream().map(ShopSalaryVo::getGrossSalary).reduce(BigDecimal.ZERO, BigDecimal::add).subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        }
                        finList.add(fin);
                    }
                }
            }
        }
        report.setBudgetMoney(finList.stream().map(FinReportVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        report.setBudgetCompare(finList.stream().map(FinReportVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    //返回类型为门店公司的vo
    private void getBudgetForShopList(RealTimeProfitVo report, FinQueryDto queryDto, List<RealProfitBudgetVo> budgetVos, List<CompanyVo> shopInfoList, Integer type) {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        //设置表有效期
        Date startTime = new Date();
        Date overTime = new Date();
        Date start = new Date();
        Date end = new Date();

        Integer day = new Integer(1);
        if (CollectionUtil.isNotEmpty(shopInfoList)) {
            for (RealProfitBudgetVo vo : budgetVos) {
                for (CompanyVo fin : shopInfoList) {
                    if (report.getItemName().trim().equals(vo.getProject().trim()) && fin.getShopId().equals(vo.getShopId())) {
                        try {
                            startTime = sdFormat.parse(vo.getBeginDate());
                            overTime = sdFormat.parse(vo.getEndDate());
                            start = sdFormat.parse(queryDto.getBeginTime());
                            end = sdFormat.parse(queryDto.getEndTime());
                            day = DateUtils.getBetweenDay(startTime, overTime);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //将字符串形式的时间转化为Date类型的时间
                        if (startTime.getTime() <= start.getTime() && overTime.getTime() >= end.getTime()) {
                            fin.setBudgetMoney(vo.getBudget().divide(new BigDecimal(day), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays())));
                            if (ReportDataConstant.BudgetType.SERVICE_MANAGE.equals(type)) {
                                fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getBusinessManageTotal().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                            } else if (ReportDataConstant.BudgetType.BEER_INTO_FACTORY.equals(type)) {
                                fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getBeerIntoFactoryFee().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                            } else if (ReportDataConstant.BudgetType.ADVERTISING_MONEY.equals(type)) {
                                fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getAmortMoney().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                            } else if (ReportDataConstant.BudgetType.UNAMORTIZED_MONEY.equals(type)) {
                                fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getAmortMoney().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                            } else if (ReportDataConstant.BudgetType.WINE_MEAL_MONEY.equals(type)) {
                                fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getMaterialCost().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                            } else if (ReportDataConstant.BudgetType.STAFF_PAY_BONUS.equals(type)) {
                                fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getBonusMoney().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                            }
                        }
                    }
                }
            }
        }
        report.setBudgetMoney(shopInfoList == null ? BigDecimal.ZERO : shopInfoList.stream().filter(data -> data.getBudgetMoney() != null).map(CompanyVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        report.setBudgetCompare(shopInfoList == null ? BigDecimal.ZERO : shopInfoList.stream().filter(data -> data.getBudgetCompare() != null).map(CompanyVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private void getBudgetForNormal(RealTimeProfitVo report, FinQueryDto queryDto, List<RealProfitBudgetVo> budgetVos, List<RealProfitVo> shouldDeprList, Integer type) {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        //设置表有效期
        Date startTime = new Date();
        Date overTime = new Date();
        Date start = new Date();
        Date end = new Date();

        Integer day = new Integer(1);
        for (RealProfitBudgetVo vo : budgetVos) {
            for (RealProfitVo fin : shouldDeprList) {
                if (report.getItemName().trim().equals(vo.getProject().trim()) && fin.getShopId().equals(vo.getShopId())) {
                    try {
                        startTime = sdFormat.parse(vo.getBeginDate());
                        overTime = sdFormat.parse(vo.getEndDate());
                        start = sdFormat.parse(queryDto.getBeginTime());
                        end = sdFormat.parse(queryDto.getEndTime());
                        day = DateUtils.getBetweenDay(startTime, overTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //将字符串形式的时间转化为Date类型的时间
                    if (startTime.getTime() <= start.getTime() && overTime.getTime() >= end.getTime()) {
                        fin.setBudgetMoney(vo.getBudget().divide(new BigDecimal(day), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays())));
                        if (ReportDataConstant.BudgetType.SHOULDDEPR.equals(type)) {
                            fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getShouldDepr().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        } else if (ReportDataConstant.BudgetType.COUNTLOSS.equals(type)) {
                            fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getAmount().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        } else if (ReportDataConstant.BudgetType.ALLMOUNT.equals(type)) {
                            fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getAllAmount().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        }

                    }
                }
            }
        }
        report.setBudgetMoney(CollectionUtil.isEmpty(shouldDeprList) == true ? BigDecimal.ZERO : shouldDeprList.stream().filter(data -> data.getBudgetMoney() != null).map(RealProfitVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        report.setBudgetCompare(CollectionUtil.isEmpty(shouldDeprList) == true ? BigDecimal.ZERO : shouldDeprList.stream().filter(data -> data.getBudgetCompare() != null).map(RealProfitVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    //food预算方法抽取
    private void getFoodBudgetMethod(String foodType, RealTimeProfitVo
            report, List<RepPosDetailFoodVo> repPosDetailFoodVos, FinQueryDto
                                             queryDto, List<RealProfitBudgetVo> budgetVos) {
        Map<String, List<RepPosDetailFoodVo>> foodList;
        if (ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY.equals(foodType)) {
            foodList = repPosDetailFoodVos.stream().collect(Collectors.groupingBy(RepPosDetailFoodVo::getShopId));
        } else {
            foodList = repPosDetailFoodVos.stream().filter(data -> Arrays.asList(report.getItemCode().split(",")).
                    contains(data.getFoodStyleName())).collect(Collectors.groupingBy(RepPosDetailFoodVo::getShopId));
        }
        Integer day = new Integer(1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //设置表有效期
        Date startTime = new Date();
        Date endTime = new Date();
        Date start = new Date();
        Date end = new Date();
        List<FinReportVo> finList = new ArrayList<>();
        for (RealProfitBudgetVo vo : budgetVos) {
            for (Map.Entry<String, List<RepPosDetailFoodVo>> entry : foodList.entrySet()) {
                if (report.getItemName().trim().equals(vo.getProject().trim()) && entry.getKey().equals(vo.getShopId())) {
                    try {
                        startTime = sdf.parse(vo.getBeginDate());
                        endTime = sdf.parse(vo.getEndDate());
                        start = sdf.parse(queryDto.getBeginTime());
                        end = sdf.parse(queryDto.getEndTime());
                        day = DateUtils.getBetweenDay(startTime, endTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //将字符串形式的时间转化为Date类型的时间
                    if (startTime.getTime() <= start.getTime() && endTime.getTime() >= end.getTime()) {
                        FinReportVo fin = new FinReportVo();
                        BigDecimal foodPrice = entry.getValue().stream().map(RepPosDetailFoodVo::getFoodAllPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                        fin.setBudgetMoney(vo.getBudget().divide(new BigDecimal(day), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays())));
                        fin.setBudgetCompare(BigDecimalUtils.getPercent(foodPrice.subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        finList.add(fin);
                    }
                }
            }

        }
        report.setBudgetMoney(CollectionUtil.isEmpty(finList) == true ? BigDecimal.ZERO : finList.stream().filter(data -> data.getBudgetMoney() != null).map(FinReportVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        report.setBudgetCompare(CollectionUtil.isEmpty(finList) == true ? BigDecimal.ZERO : finList.stream().filter(data -> data.getBudgetCompare() != null).map(FinReportVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /**
     * 获取预算方法抽取(带公式)
     *
     * @param report
     * @param shopSubject
     * @param configVos
     * @param queryDto
     * @param budgetVos
     */
    private void getBudgetHasFormulExp(RealTimeProfitVo
                                               report, List<FinReportVo> shopSubject, List<FinReportConfigVo> configVos, FinQueryDto
                                               queryDto, List<RealProfitBudgetVo> budgetVos, String realtimeProfitOtherBusinessPay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //设置表有效期
        Date startTime = new Date();
        Date endTime = new Date();
        Date start = new Date();
        Date end = new Date();
        //前端时间条件
        //根据FinType获取配置项
        FinReportConfigVo configVoTaxAndAdd = configVos.stream().filter(data -> realtimeProfitOtherBusinessPay.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> shopSubjectsTaxAndAdd = shopSubject.stream().filter(data -> Arrays.asList(configVoTaxAndAdd.getCodes().split(",")).
                contains(data.getAccountSubjectCode())).collect(Collectors.toList());
        Map<String, List<FinReportVo>> shopCollectTaxAndAdd = shopSubjectsTaxAndAdd.stream().filter(fin -> fin.getShopId() != null).collect(Collectors.groupingBy(FinReportVo::getShopId));
        //计算主公式金额合计 key为门店id value为金额
        Map<String, BigDecimal> formulaTaxAndAdd = financeSubjectService.getShopAmountByFormula(shopCollectTaxAndAdd, configVoTaxAndAdd.getFormulaExpList());
        List<FinReportVo> finList = new ArrayList<>();
        Integer day = new Integer(1);
        for (RealProfitBudgetVo vo : budgetVos) {
            for (Map.Entry<String, BigDecimal> entry : formulaTaxAndAdd.entrySet()) {
                if (report.getItemName().trim().equals(vo.getProject().trim()) && entry.getKey().equals(vo.getShopId())) {
                    try {
                        startTime = sdf.parse(vo.getBeginDate());
                        endTime = sdf.parse(vo.getEndDate());
                        start = sdf.parse(queryDto.getBeginTime());
                        end = sdf.parse(queryDto.getEndTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //将字符串形式的时间转化为Date类型的时间
                    if (startTime.getTime() <= start.getTime() && endTime.getTime() >= end.getTime()) {
                        FinReportVo fin = new FinReportVo();
                        fin.setBudgetMoney(vo.getBudget().divide(new BigDecimal(day), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays())));
                        fin.setBudgetCompare(BigDecimalUtils.getPercent(entry.getValue() == null ? BigDecimal.ZERO : entry.getValue().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        finList.add(fin);
                    }
                }
            }
        }
        report.setBudgetMoney(CollectionUtil.isEmpty(finList) == true ? BigDecimal.ZERO : finList.stream().filter(data -> data.getBudgetMoney() != null).map(FinReportVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        report.setBudgetCompare(CollectionUtil.isEmpty(finList) == true ? BigDecimal.ZERO : finList.stream().filter(data -> data.getBudgetCompare() != null).map(FinReportVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /**
     * 获取预算方法抽取(不带公式)
     *
     * @param report
     * @param shopSubject
     * @param configVos
     * @param queryDto
     * @param budgetVos
     * @param realtimeProfitOutsideBusinessMoney
     */
    private void getBudget(Integer type, RealTimeProfitVo
            report, List<FinReportVo> shopSubject, List<FinReportConfigVo> configVos, FinQueryDto
                                   queryDto, List<RealProfitBudgetVo> budgetVos, String realtimeProfitOutsideBusinessMoney) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //设置表有效期
        Date startTime = new Date();
        Date endTime = new Date();
        Date start = new Date();
        Date end = new Date();
        //前端时间条件
        FinReportConfigVo configVo = configVos.stream().filter(data -> realtimeProfitOutsideBusinessMoney.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> finList = shopSubject.stream().filter(data -> configVo.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        Integer day = new Integer(1);
        for (RealProfitBudgetVo vo : budgetVos) {
            for (FinReportVo fin : finList) {
                if (report.getItemName().trim().equals(vo.getProject().trim()) && fin.getShopId().equals(vo.getShopId())) {
                    try {
                        startTime = sdf.parse(vo.getBeginDate());
                        endTime = sdf.parse(vo.getEndDate());
                        start = sdf.parse(queryDto.getBeginTime());
                        end = sdf.parse(queryDto.getEndTime());
                        day = DateUtils.getBetweenDay(startTime, endTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //将字符串形式的时间转化为Date类型的时间new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP
                    if (startTime.getTime() <= start.getTime() && endTime.getTime() >= end.getTime()) {
                        fin.setBudgetMoney(vo.getBudget().divide(new BigDecimal(day), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays())));
                        if (ReportDataConstant.RealProfitGetValueType.DEBIT.equals(type)) {
                            fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getDebitAmount() == null ? BigDecimal.ZERO : fin.getDebitAmount().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        } else if (ReportDataConstant.RealProfitGetValueType.CREDIT.equals(type)) {
                            fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getCreditAmount() == null ? BigDecimal.ZERO : fin.getCreditAmount().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        } else if (ReportDataConstant.RealProfitGetValueType.AMOUNT.equals(type)) {
                            fin.setBudgetCompare(BigDecimalUtils.getPercent(fin.getAmount() == null ? BigDecimal.ZERO : fin.getAmount().subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        }
                    }
                }
            }
        }
        report.setBudgetMoney(CollectionUtil.isEmpty(finList) == true ? BigDecimal.ZERO : finList.stream().filter(data -> data.getBudgetMoney() != null).map(FinReportVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        report.setBudgetCompare(CollectionUtil.isEmpty(finList) == true ? BigDecimal.ZERO : finList.stream().filter(data -> data.getBudgetCompare() != null).map(FinReportVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add));
    }


    /**
     * 获取占比方法抽取
     *
     * @param report
     * @param businessMoney
     */
    private void getCurrentProportionResult(Integer dataType, RealTimeProfitVo report, BigDecimal
            businessMoney, String profitBeforeDepreciation) {
        if (profitBeforeDepreciation.equals(report.getItemCode())) {
            if (ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY.equals(dataType)) {
                BigDecimal rentPercent = BigDecimalUtils.getPercent(report.getCurrentMoney(), businessMoney);
                report.setCurrentProportion(rentPercent);
            } else if (ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR.equals(dataType)) {
                BigDecimal rentPercent = BigDecimalUtils.getPercent(report.getYearMoney(), businessMoney);
                report.setYearProportion(rentPercent);
            } else if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)) {
                BigDecimal rentPercent = BigDecimalUtils.getPercent(report.getCurrentMoney() == null ? BigDecimal.ZERO : report.getCurrentMoney().
                        subtract(report.getLastCurrentMoney() == null ? BigDecimal.ZERO : report.getLastCurrentMoney()), report.getLastCurrentMoney() == null ? BigDecimal.ZERO : report.getLastCurrentMoney());
                report.setSameCompareProportion(rentPercent);
            } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType)) {
                BigDecimal rentPercent = BigDecimalUtils.getPercent(report.getCurrentMoney() == null ? BigDecimal.ZERO : report.getCurrentMoney().
                        subtract(report.getLatelyCurrentMoney() == null ? BigDecimal.ZERO : report.getLatelyCurrentMoney()), report.getLatelyCurrentMoney() == null ? BigDecimal.ZERO : report.getLatelyCurrentMoney());
                report.setChainCompareProportion(rentPercent);
            }

        }
    }


    /**
     * 根据公式获取值
     *
     * @param shopSubject
     * @param configVos
     * @param taxAndAdd
     * @param realtimeProfitTaxesAndAddBusiness
     * @return
     */
    private BigDecimal getDataByFormula
    (List<FinReportVo> shopSubject, List<FinReportConfigVo> configVos, BigDecimal taxAndAdd, String
            realtimeProfitTaxesAndAddBusiness) {
        //根据FinType获取配置项
        FinReportConfigVo configVoTaxAndAdd = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> realtimeProfitTaxesAndAddBusiness.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> shopSubjectsTaxAndAdd = CollectionUtil.isEmpty(configVos) == true ? null : shopSubject.stream().filter(data -> Arrays.asList(configVoTaxAndAdd.getCodes().split(",")).
                contains(data.getAccountSubjectCode())).collect(Collectors.toList());
        Map<String, List<FinReportVo>> shopCollectTaxAndAdd = CollectionUtil.isEmpty(shopSubjectsTaxAndAdd) == true ? null : shopSubjectsTaxAndAdd.stream().filter(fin -> fin.getShopId() != null).collect(Collectors.groupingBy(FinReportVo::getShopId));
        //计算主公式金额合计 key为门店id value为金额
        Map<String, BigDecimal> formulaTaxAndAdd = financeSubjectService.getShopAmountByFormula(shopCollectTaxAndAdd, configVoTaxAndAdd.getFormulaExpList());

        //value
        for (BigDecimal value : formulaTaxAndAdd.values()) {
            taxAndAdd = taxAndAdd.add(value);
        }
        return taxAndAdd;
    }


    /**
     * 根据公式获取值（门店维度）
     *
     * @param shopSubject
     * @param configVos
     * @param realtimeProfitTaxesAndAddBusiness
     * @return
     */
    private List<CompanyVo> getShopDataByFormula
    (List<FinReportVo> shopSubject, List<FinReportConfigVo> configVos, String
            realtimeProfitTaxesAndAddBusiness) {
        //根据FinType获取配置项
        FinReportConfigVo configVoTaxAndAdd = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> realtimeProfitTaxesAndAddBusiness.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> shopSubjectsTaxAndAdd = CollectionUtil.isEmpty(configVos) == true ? null : shopSubject.stream().filter(data -> Arrays.asList(configVoTaxAndAdd.getCodes().split(",")).
                contains(data.getAccountSubjectCode())).collect(Collectors.toList());
        Map<String, List<FinReportVo>> shopCollectTaxAndAdd = CollectionUtil.isEmpty(shopSubjectsTaxAndAdd) == true ? null : shopSubjectsTaxAndAdd.stream().filter(fin -> fin.getShopId() != null).collect(Collectors.groupingBy(FinReportVo::getShopId));
        //计算主公式金额合计 key为门店id value为金额
        Map<String, BigDecimal> formulaTaxAndAdd = financeSubjectService.getShopAmountByFormula(shopCollectTaxAndAdd, configVoTaxAndAdd.getFormulaExpList());

        List<CompanyVo> list = new ArrayList<>();
        //value
        CompanyVo companyVo;
        for (Map.Entry<String, BigDecimal> entry : formulaTaxAndAdd.entrySet()) {
            companyVo = new CompanyVo();
            companyVo.setShopId(entry.getKey());
            companyVo.setAmount(entry.getValue());
            list.add(companyVo);
        }

        return list;
    }

    //将实时利润dto转换财务报表dto
    private FinQueryDto getFinQueryDto(RealTimeProfitDto realTimProfitDto) {
        FinQueryDto queryDto = new FinQueryDto();
        //queryDto.setSubjectCodeList(configVo.getSubjectCodeList());
        queryDto.setEnteId(realTimProfitDto.getEnteId());
        queryDto.setBeginTime(realTimProfitDto.getBeginTime());
        queryDto.setEndTime(realTimProfitDto.getEndTime());
        queryDto.setShopIdList(realTimProfitDto.getShopIdList());
        queryDto.setShopTypeIdList(realTimProfitDto.getShopTypeIdList());
        queryDto.setBeginDate(realTimProfitDto.getBeginDate());
        queryDto.setEndDate(realTimProfitDto.getEndDate());
        queryDto.setDateType(realTimProfitDto.getDateType());
        //给时间跨度赋值
        int days = DateUtils.getBetweenDay(realTimProfitDto.getBeginTime(), realTimProfitDto.getEndTime(), "yyyy-MM-dd")+1;
        queryDto.setDays(days);
        return queryDto;
    }


    /**
     * @Description: 计算本期
     * @Param: [businessReportDayItemVos, reportPosDeskVos, repPosDetailPayVos]
     * @return: void
     * @Author: liBao
     * @Date: 2020/1/2 16:13
     */
    public void calculationNetProfit(List<RealTimeProfitVo> RealTimeProfitVos, RepPosDetailFoodVo repPosDetailFoodVo,
                                     Integer dataType, List<RepPosDetailFoodVo> repPosDetailFoodVos, List<FinReportVo> shopSubject, List<FinReportConfigVo> configVos,
                                     FinQueryDto queryDto, List<FinReportVo> voForLastYearSubject, List<RealProfitVo> shouldDeprList, List<RealProfitVo> layOutList,
                                     List<RealProfitVo> countLossList, List<CompanyVo> shopInfoList, List<CompanyVo> beerList, List<DishGrossProfitVo> dishBaseList,
                                     List<ShopSalaryVo> shopScoreVos, List<CompanyVo> amortSchemeListByShop, List<CompanyVo> materialCostList) {
        //赋值
        if (!FastUtils.checkNullOrEmpty(shopSubject, configVos)) {
            RealTimeProfitVos.forEach(report -> {
                //净利润
                calculateNetProfitForBrandBouns(repPosDetailFoodVo, report, repPosDetailFoodVos, shopSubject, configVos, queryDto,
                        dataType, voForLastYearSubject, shouldDeprList, layOutList, countLossList, shopInfoList, beerList, dishBaseList, shopScoreVos, amortSchemeListByShop, materialCostList);
            });
        }
    }

    /**
     * @Description: 计算净利润
     * @Param: [reportPosDeskVos, repPosDetailPayVos, report, dataType]
     * @return: void
     * @Author: liBao
     * @Date: 2020/2/20 11:41
     */
    private void calculateNetProfitForBrandBouns(RepPosDetailFoodVo repPosDetailFoodVo, RealTimeProfitVo report, List<RepPosDetailFoodVo> repPosDetailFoodVos,
                                                 List<FinReportVo> shopSubject, List<FinReportConfigVo> configVos, FinQueryDto queryDto, Integer dataType,
                                                 List<FinReportVo> voForLastYearSubject, List<RealProfitVo> shouldDeprList, List<RealProfitVo> layOutList,
                                                 List<RealProfitVo> countLossList, List<CompanyVo> shopInfoList, List<CompanyVo> beerList, List<DishGrossProfitVo> dishBaseList,
                                                 List<ShopSalaryVo> shopScoreVos, List<CompanyVo> amortSchemeListByShop, List<CompanyVo> materialCostList) {
        //1、实收合计(主营业务收入)
        BigDecimal mainBusiness = repPosDetailFoodVo == null ? BigDecimal.ZERO : repPosDetailFoodVo.getFoodAllPrice();
        //List<RealProfitBudgetVo> budgetVos = financeSubjectService.getRealProfitBudgetList(queryDto);
        //1.2、酒水饮料
        BigDecimal beverage = CollectionUtil.isEmpty(repPosDetailFoodVos) == true ? BigDecimal.ZERO : repPosDetailFoodVos.stream().filter(data -> Arrays.asList(report.getItemCode().split(",")).
                contains(data.getFoodStyleName())).map(RepPosDetailFoodVo::getFoodAllPrice).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //1.3、杂项收入
        BigDecimal other = CollectionUtil.isEmpty(repPosDetailFoodVos) == true ? BigDecimal.ZERO : repPosDetailFoodVos.stream().filter(data -> Arrays.asList(report.getItemCode().split(",")).
                contains(data.getFoodStyleName())).map(RepPosDetailFoodVo::getFoodAllPrice).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //1.1、菜品收入= 实收合计-酒水-杂项
        BigDecimal food = mainBusiness.subtract(beverage).subtract(other);

        //1、主营业务收入
        if (ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, mainBusiness);
        }

        //1.1、菜品收入 = 实收合计-酒水-杂项
        if (ReportDataConstant.RealProfitItemCode.VARIETY_OF_DISHES_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, food);
        }

        //1.2、酒水饮料
        if (ReportDataConstant.RealProfitItemCode.WINE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, beverage);
        }

        //1.3、杂项收入
        if (ReportDataConstant.RealProfitItemCode.MISCELLANEOUS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, other);
        }
        //2、其他业务收入  -供应链
        BigDecimal otherBusinessMoney = CollectionUtil.isEmpty(beerList) == true ? BigDecimal.ZERO : beerList.stream().map(CompanyVo::getBeerIntoFactoryFee).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, otherBusinessMoney);
        }
        //3、营业外收入
        if (ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_MONEY.equals(report.getItemCode())) {
            FinReportConfigVo configVo = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_OUTSIDE_BUSINESS_MONEY.
                    equals(data.getFinType())).collect(Collectors.toList()).get(0);
            if (ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY.equals(dataType)) {
                report.setCurrentMoney(CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVo.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                        reduce(BigDecimal.ZERO, BigDecimal::add));
            } else if (ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR.equals(dataType)) {
                report.setYearMoney(CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVo.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                        reduce(BigDecimal.ZERO, BigDecimal::add));
            } else if (ReportDataConstant.BusinessAnalysisDataType.LASTCURRENTMONEY.equals(dataType)) {
                report.setLastCurrentMoney(CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVo.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                        reduce(BigDecimal.ZERO, BigDecimal::add));
            } else if (ReportDataConstant.BusinessAnalysisDataType.LATELYCURRENTMONEY.equals(dataType)) {
                report.setLatelyCurrentMoney(CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVo.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                        reduce(BigDecimal.ZERO, BigDecimal::add));
            }
        }

        //4、主营业务成本 -供应链
        BigDecimal mainBusinessCost = new BigDecimal("0.00");
        //4.1 菜品成本
        BigDecimal costOfDishes = CollectionUtil.isEmpty(dishBaseList) == true ? BigDecimal.ZERO : dishBaseList.stream().map(DishGrossProfitVo::getCostAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        //4.2 酒水成本
        BigDecimal beverageCost = new BigDecimal("0.00");
        //4.3 杂项成本
        BigDecimal miscellaneousCost = CollectionUtil.isEmpty(countLossList) == true ? BigDecimal.ZERO : countLossList.stream().map(RealProfitVo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        mainBusinessCost = costOfDishes.add(beverageCost).add(miscellaneousCost);
        if (ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, mainBusinessCost);
        }
        if (ReportDataConstant.RealProfitItemCode.COST_OF_DISHES.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, costOfDishes);
        }
        if (ReportDataConstant.RealProfitItemCode.COST_OF_WINE.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, beverageCost);
        }
        if (ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, miscellaneousCost);
        }

        //5、其他业务支出
        BigDecimal otherBusinessPay = new BigDecimal("0.00");
        //根据FinType获取配置项
        otherBusinessPay = getDataByFormula(shopSubject, configVos, otherBusinessPay, ReportDataConstant.FinType.REALTIME_PROFIT_OTHER_BUSINESS_PAY);
        if (ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, otherBusinessPay);
        }

        //6、营业外支出
        BigDecimal outsideBusinessPay = new BigDecimal("0.00");
        //根据FinType获取配置项
        outsideBusinessPay = getDataByFormula(shopSubject, configVos, outsideBusinessPay, ReportDataConstant.FinType.REALTIME_PROFIT_OUTSIDE_BUSINESS_PAY);

        if (ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, outsideBusinessPay);
        }

        //7、营业税金及附加
        BigDecimal taxAndAdd = new BigDecimal("0.00");
        taxAndAdd = getDataByFormula(shopSubject, configVos, taxAndAdd, ReportDataConstant.FinType.REALTIME_PROFIT_TAXES_AND_ADD_BUSINESS);
        if (ReportDataConstant.RealProfitItemCode.TAXES_AND_ADD_BUSINESS.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, taxAndAdd);
        }

        //8.营业费用
        BigDecimal businessMoney = new BigDecimal("0.00");
        //8.1  营业费用_职工薪酬
        BigDecimal staffSalaries = new BigDecimal("0.00");
        //8.01.01  营业费用_职工薪酬_在职工资
        BigDecimal workWages = CollectionUtil.isEmpty(shopScoreVos) == true ? BigDecimal.ZERO : shopScoreVos.stream().map(ShopSalaryVo::getGrossSalary).reduce(BigDecimal.ZERO, BigDecimal::add);
        //8.01.02  营业费用_职工薪酬_离职工资
        //根据FinType获取配置项
        FinReportConfigVo configVoQuit = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_QUIT_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal quitMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoQuit.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);

        //8.01.03 营业费用_职工薪酬_奖金
        BigDecimal bonus = new BigDecimal("0.00");
        staffSalaries = workWages.add(quitMoney).add(bonus);

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, staffSalaries);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_ONLINE_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, workWages);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_QUIT_PAY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, quitMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_BONUS.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, bonus);
        }

        //8.02 营业费用_水电气费
        BigDecimal waterElectricGasMoney = new BigDecimal("0.00");

        //8.02.01 营业费用_水电气费_水费 最近一年累计/12/30*天数
        FinReportConfigVo configVoWater = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_WATER_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal waterMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoWater.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        waterMoney = waterMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        //8.02.02 营业费用_电费
        FinReportConfigVo configVoElectric = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ELECTRIC_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal electricMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoElectric.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        electricMoney = electricMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        //8.02.03 营业费用_燃气费

        FinReportConfigVo configVoGas = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_GAS_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal gasMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoGas.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        gasMoney = gasMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        waterElectricGasMoney = waterMoney.add(electricMoney).add(gasMoney);

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_ELECTRIC_GAS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, waterElectricGasMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, waterMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ELECTRIC_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, electricMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GAS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, gasMoney);
        }

        //8.03 营业费用_租赁费
        BigDecimal rentMoney = new BigDecimal("0.00");
        //8.03.01 营业费用_租赁费_营业租金
        FinReportConfigVo configVoRentBusiness = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_RENT_BUSINESS_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal rentBusinessMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoRentBusiness.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        rentBusinessMoney = rentBusinessMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        //8.03.02 营业费用_租赁费_宿舍租金
        FinReportConfigVo configVoRentHotel = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_RENT_HOSTEL_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal rentHotelMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoRentHotel.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        rentHotelMoney = rentHotelMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        rentMoney = rentMoney.add(rentBusinessMoney).add(rentHotelMoney);

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, rentMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_BUSINESS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, rentBusinessMoney);
        }

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_HOSTEL_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, rentHotelMoney);
        }

        //8.04 营业费用_物业费
        FinReportConfigVo configVoProperty = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PROPERTY_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal propertyMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoProperty.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        propertyMoney = propertyMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PROPERTY_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, propertyMoney);
        }
        //8.05 营业费用_赠送费用

        FinReportConfigVo configVoGive = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_GIVE_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal giveMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoGive.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        giveMoney = giveMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GIVE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, giveMoney);
        }
        //8.06	营业费用_低值易耗品
        BigDecimal lowCostLabware = new BigDecimal("0.00");

        //8.06.01	营业费用_低值易耗品_工具类
        FinReportConfigVo configVoTool = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_TOOL.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal toolMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoTool.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        toolMoney = toolMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        //8.06.02	营业费用_低值易耗品_消耗类
        FinReportConfigVo configVoConsume = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_CONSUME.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal consumeMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoConsume.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        consumeMoney = consumeMoney.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));

        lowCostLabware = toolMoney.add(consumeMoney);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, lowCostLabware);
        }

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_TOOL.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, toolMoney);
        }

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_CONSUME.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, consumeMoney);
        }
        //8.07	营业费用_修理费
        FinReportConfigVo configVoRepair = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PEPAIR_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal repairMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoRepair.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PEPAIR_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, repairMoney);
        }

        //8.08	营业费用_清洁费
        BigDecimal cleanMoney = new BigDecimal("0.00");
        cleanMoney = cleanMoney.add(new BigDecimal(queryDto.getShopIdList().size() * 1650 / 12 / 30 * queryDto.getDays()));

        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CLEAN_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, cleanMoney);
        }

        //8.09	营业费用_运输费
        FinReportConfigVo configVoTransport = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_TRANSPORT_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal consumeTranSport = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoTransport.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        consumeTranSport = consumeTranSport.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRANSPORT_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, consumeTranSport);
        }

        //8.10  营业费用_折旧费
        BigDecimal oldMoney = CollectionUtil.isEmpty(shouldDeprList) == true ? BigDecimal.ZERO : shouldDeprList.stream().map(RealProfitVo::getShouldDepr).reduce(BigDecimal.ZERO, BigDecimal::add);
        oldMoney = oldMoney.divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getAdjustDays()));
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_DEPRECIATION_CHARGE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, oldMoney);
        }
        //8.11	营业费用_劳动保险费
        BigDecimal laborInsurance = CollectionUtil.isEmpty(shopScoreVos) == true ? BigDecimal.ZERO : shopScoreVos.stream().map(ShopSalary::getSocialSalary).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_INSURANCE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, laborInsurance);
        }

        //8.12	营业费用_福利费
        BigDecimal welfareMoney = new BigDecimal("0.00");

        //8.12.01	营业费用_福利费_员工餐
        BigDecimal staffMeal = new BigDecimal("0.00");
        //8.12.02	营业费用_福利费_其他福利
        FinReportConfigVo configVoWelfareOther = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PEPAIR_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal welfareOtherMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoWelfareOther.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        welfareMoney = welfareOtherMoney.add(staffMeal);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, laborInsurance);
            report.setCurrentMoney(welfareMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, staffMeal);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_OTHER.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, welfareOtherMoney);
        }

        //8.13	营业费用_办公费
        FinReportConfigVo configVoOffice = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_OFFICE_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal officeMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoOffice.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WORK_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, officeMoney);
        }

        //8.14	营业费用_差旅费
        FinReportConfigVo configVoTravel = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_TRAVEL_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal travelMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoTravel.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRAVEL_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, travelMoney);
        }

        //8.15	营业费用_业务招待费
        FinReportConfigVo configVoEntertainment = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ENTERTAINMENT_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal entertainmentMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoEntertainment.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_BUSINESS_ENTERTAINMENT_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, entertainmentMoney);
        }

        //8.16	营业费用_广告宣传费
        BigDecimal advertisingExpenses = new BigDecimal("0.00");
        //8.16.01	营业费用_广告宣传费_广告费
        BigDecimal advertising = CollectionUtil.isEmpty(amortSchemeListByShop) == true ? BigDecimal.ZERO : amortSchemeListByShop.stream().filter(data -> ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY.equals(data.getAmortName()))
                .map(CompanyVo::getAmortMoney).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(queryDto.getPeriodDays()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        //8.16.02	营业费用_广告宣传费_企划费用
        BigDecimal planningFee = CollectionUtil.isEmpty(layOutList) == true ? BigDecimal.ZERO : layOutList.stream().map(RealProfitVo::getAllAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //8.16.03	营业费用_广告宣传费_活动宣传费
        FinReportConfigVo configVoAdvertisingActivity = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ADVERTISING_ACTIVITY_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal advertisingActivityMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoAdvertisingActivity.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        advertisingExpenses = advertising.add(planningFee).add(advertisingActivityMoney);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, advertisingExpenses);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, advertising);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PLANNING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, planningFee);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PROPAGANDA_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, advertisingActivityMoney);
        }

        //8.17	营业费用_劳动保护费
        FinReportConfigVo configVoLaborProtect = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LABOR_PROTECTION_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal laborProtectMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoLaborProtect.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOR_PROTECTION_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, laborProtectMoney);
        }

        //8.18	营业费用_装饰费
        FinReportConfigVo configVoDecorate = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_DECORATE_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal decorateMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoDecorate.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_decorate_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, decorateMoney);
        }

        //8.19	营业费用_工会经费
        FinReportConfigVo configVoLabourUnion = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LABOUR_UNION_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal labourUnionMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoLabourUnion.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_UNION_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, labourUnionMoney);
        }

        //8.20 	营业费用_职工教育经费
        FinReportConfigVo configVoStaffEdu = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_EDUCATION_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal staffEduMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoStaffEdu.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_EDUCATION_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, staffEduMoney);
        }

        //8.21	营业费用_住房公积金
        FinReportConfigVo configVoHousingProvidentFund = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_HOUSING_PROVIDENT_FUND.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal housingProvidentFundMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoHousingProvidentFund.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_HOUSING_PROVIDENT_FUND.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, housingProvidentFundMoney);
        }

        //8.22	营业费用_长期待摊费
        BigDecimal longTermPending = CollectionUtil.isEmpty(amortSchemeListByShop) == true ? BigDecimal.ZERO : amortSchemeListByShop.stream().filter(data -> ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY.equals(data.getAmortName()))
                .map(CompanyVo::getAmortMoney).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(queryDto.getPeriodDays()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, longTermPending);
        }

        //8.23	营业费用_咨询服务培训费
        BigDecimal consultServiceTrainingFee = new BigDecimal("0.00");
        //8.23.01	营业费用_服务咨询费_管理服务费


        BigDecimal actualSum = CollectionUtil.isEmpty(shopInfoList) == true ? BigDecimal.ZERO : shopInfoList.stream().map(CompanyVo::getBusinessManageTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        //8.23.02	营业费用_服务咨询费_咨询服务费
        FinReportConfigVo configVoConsultingService = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_CONSULTING_SERVICE.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal consultingServiceMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoConsultingService.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //8.23.03	营业费用_服务咨询费_培训费
        FinReportConfigVo configVoTrain = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_TRAIN_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal trainMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoTrain.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, consultServiceTrainingFee);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_MANAGE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, actualSum);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_CONSULTING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, consultingServiceMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_TRAINING_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, trainMoney);
        }
        //8.24	营业费用_商业保险
        FinReportConfigVo configVoCommercialInsurance = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_COMMERCIAL_INSURANCE.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal commercialInsuranceMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoCommercialInsurance.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMERCIAL_INSURANCE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, commercialInsuranceMoney);
        }
        //8.25	营业费用_通讯费
        FinReportConfigVo configVoCommunication = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_COMMUNICATION.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal communicationMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoCommunication.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMUNICATION_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, communicationMoney);
        }

        //8.26	营业费用_其他
        FinReportConfigVo configVoOther = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_OTHER.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal otherMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoOther.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_OTHER.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, otherMoney);
        }
        businessMoney = staffSalaries.add(waterElectricGasMoney).add(rentMoney).add(propertyMoney).add(giveMoney).add(lowCostLabware).add(repairMoney).add(cleanMoney).
                add(consumeTranSport).add(oldMoney).add(laborInsurance).add(welfareMoney).add(officeMoney).add(travelMoney).add(entertainmentMoney).
                add(advertisingExpenses).add(laborProtectMoney).add(decorateMoney).add(labourUnionMoney).add(staffEduMoney).add(housingProvidentFundMoney).
                add(longTermPending).add(consultServiceTrainingFee).add(commercialInsuranceMoney).add(communicationMoney).add(otherMoney);
        if (ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, businessMoney);
        }

        //9	财务费用
        BigDecimal financeFee = new BigDecimal("0.00");
        //9.01	财务费用_利息收入
        FinReportConfigVo configVoFinanceInterestMoney = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_INTEREST_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal financeInterestMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoFinanceInterestMoney.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //9.02	财务费用_利息支出
        FinReportConfigVo configVoFinanceInterestPay = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_INTEREST_PAY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal financeInterestPay = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoFinanceInterestPay.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //9.03	财务费用_手续费
        FinReportConfigVo configVoFinancePorceduresMoney = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_PROCEDURES_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal financePorceduresMoney = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoFinancePorceduresMoney.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //9.04	财务费用_汇兑损益
        FinReportConfigVo configVoFinanceExcGainsAndLosses = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_EXCHANGE_GAINS_AND_LOSSES.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal financeExcGainsAndLosses = CollectionUtil.isEmpty(shopSubject) == true ? BigDecimal.ZERO : shopSubject.stream().filter(data -> configVoFinanceExcGainsAndLosses.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        financeFee = financeInterestMoney.add(financeInterestPay).add(financePorceduresMoney).add(financeExcGainsAndLosses);
        if (ReportDataConstant.RealProfitItemCode.FINANCE_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financeFee);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_INCOME_MONEY.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financeInterestMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_IEXPENSE.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financeInterestPay);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_SERVICE_CHARGE.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financePorceduresMoney);
        }
        if (ReportDataConstant.RealProfitItemCode.FINANCE_EXCHANGE_GAINS_AND_LOSSES.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, financeExcGainsAndLosses);
        }

        //10	所得税
        FinReportConfigVo configVoIncomeTax = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_INCOME_TAX.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        BigDecimal incomeTax = CollectionUtil.isEmpty(voForLastYearSubject) == true ? BigDecimal.ZERO : voForLastYearSubject.stream().filter(data -> configVoIncomeTax.getCodes().equals(data.getAccountSubjectCode())).map(FinReportVo::getDebitAmount).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        incomeTax = incomeTax.divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()));
        if (ReportDataConstant.RealProfitItemCode.INCOME_TAX.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, incomeTax);
        }

        //11	 净利润  主营业务收入-（主营业务成本+其他业务支出+营业外支出+营业税金及附加+营业费用+所得税）
        BigDecimal netProfit = mainBusiness.subtract(mainBusinessCost).subtract(otherBusinessPay).
                subtract(outsideBusinessPay).subtract(taxAndAdd).subtract(staffSalaries).
                subtract(incomeTax);
        if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(report.getItemCode())) {
            setCurrentOrYearMoney(report, dataType, netProfit);
        }

    }

    /**
     * @param excelExportDto
     * @param response
     * @return void
     * @Author liBao
     * @Date 2020/2/11 15:21
     * @Param [baseShopDto, response]
     * @Description 导出
     */
    @Override
    public void exportRealTimeProfit(ExcelExportDto excelExportDto, HttpServletResponse response) {
        RealTimeProfitDto realTimProfitDto = new RealTimeProfitDto();
        FastUtils.copyProperties(excelExportDto, realTimProfitDto);
        List<RealTimeProfitVo> list = findRealTimeProfitAnalysis(realTimProfitDto);

        List<RealTimeProfitVo> exportList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            //根据类型过滤
            if (ReportDataConstant.Finance.All_PROFIT.equals(realTimProfitDto.getType())) {
                exportList = list.stream().filter(info -> ReportDataConstant.Finance.All_PROFIT.equals(info.getType())).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.MAIN_PROFIT.equals(realTimProfitDto.getType())) {
                exportList = list.stream().filter(info -> ReportDataConstant.Finance.MAIN_PROFIT.equals(info.getType())).collect(Collectors.toList());
            }
        }
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            excelExportDto.setBeginDate(s.parse(excelExportDto.getBeginTime()));
            excelExportDto.setEndDate(s.parse(excelExportDto.getEndTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] firstHeader = {
                ExcelColumnConstant.RealTimeProfit.ITEM_NUMBER.getTitle(),
                ExcelColumnConstant.RealTimeProfit.ITEM_NAME.getTitle(),
                ExcelColumnConstant.RealTimeProfit.CURRENT.getTitle(),
                ExcelColumnConstant.RealTimeProfit.CURRENT.getTitle(),
                ExcelColumnConstant.RealTimeProfit.YEAR.getTitle(),
                ExcelColumnConstant.RealTimeProfit.YEAR.getTitle()
        };
        String[] secondHeader = {
                ExcelColumnConstant.RealTimeProfit.ITEM_NUMBER.getTitle(),
                ExcelColumnConstant.RealTimeProfit.ITEM_NAME.getTitle(),
                ExcelColumnConstant.RealTimeProfit.CURRENT_MONEY.getTitle(),
                ExcelColumnConstant.RealTimeProfit.CURRENT_PROPORTION.getTitle(),
                ExcelColumnConstant.RealTimeProfit.YEAR_MONEY.getTitle(),
                ExcelColumnConstant.RealTimeProfit.YEAR_PROPORTION.getTitle()
        };

        String[] fieldNames = {ExcelColumnConstant.RealTimeProfit.ITEM_NUMBER.getField(),
                ExcelColumnConstant.RealTimeProfit.ITEM_NAME.getField(),
                ExcelColumnConstant.RealTimeProfit.CURRENT_MONEY.getField(),
                ExcelColumnConstant.RealTimeProfit.CURRENT_PROPORTION.getField(),
                ExcelColumnConstant.RealTimeProfit.YEAR_MONEY.getField(),
                ExcelColumnConstant.RealTimeProfit.YEAR_PROPORTION.getField()};
        String[] convertTypes = {ExcelColumnConstant.RealTimeProfit.ITEM_NUMBER.getConvertType(),
                ExcelColumnConstant.RealTimeProfit.ITEM_NAME.getConvertType(),
                ExcelColumnConstant.RealTimeProfit.CURRENT_MONEY.getConvertType(),
                ExcelColumnConstant.RealTimeProfit.CURRENT_PROPORTION.getConvertType(),
                ExcelColumnConstant.RealTimeProfit.YEAR_MONEY.getConvertType(),
                ExcelColumnConstant.RealTimeProfit.YEAR_PROPORTION.getConvertType()};
        fileService.exportMultiRowHeaderData(response, excelExportDto, exportList, fieldNames, convertTypes, firstHeader, secondHeader);

    }

    /**
     * @param excelExportDto
     * @param response
     * @return void
     * @Author liBao
     * @Date 2020/2/11 15:21
     * @Param [baseShopDto, response]
     * @Description 同比导出
     */
    @Override
    public void exportRealTimeProfitSameCompare(ExcelExportDto excelExportDto, HttpServletResponse response) {
        RealTimeProfitDto realTimProfitDto = new RealTimeProfitDto();
        FastUtils.copyProperties(excelExportDto, realTimProfitDto);
        List<RealTimeProfitVo> list = findRealTimeProfitAnalysis(realTimProfitDto);

        List<RealTimeProfitVo> exportList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            //根据类型过滤
            if (ReportDataConstant.Finance.All_PROFIT.equals(realTimProfitDto.getType())) {
                exportList = list.stream().filter(info -> ReportDataConstant.Finance.All_PROFIT.equals(info.getType())).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.MAIN_PROFIT.equals(realTimProfitDto.getType())) {
                exportList = list.stream().filter(info -> ReportDataConstant.Finance.MAIN_PROFIT.equals(info.getType())).collect(Collectors.toList());
            }
        }
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            excelExportDto.setBeginDate(s.parse(excelExportDto.getBeginTime()));
            excelExportDto.setEndDate(s.parse(excelExportDto.getEndTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(excelExportDto.getDataType())) {
            fileService.exportExcelForQueryTerm(response, excelExportDto, exportList,
                    ExcelColumnConstant.RealTimeProfitSame.ITEM_NUMBER,
                    ExcelColumnConstant.RealTimeProfitSame.ITEM_NAME,
                    ExcelColumnConstant.RealTimeProfitSame.CURRENT_MONEY,
                    ExcelColumnConstant.RealTimeProfitSame.LAST_CURRENT_MONEY,
                    ExcelColumnConstant.RealTimeProfitSame.SAME_COMPARE_PROPORTION
            );
        } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(excelExportDto.getDataType())) {
            fileService.exportExcelForQueryTerm(response, excelExportDto, exportList,
                    ExcelColumnConstant.RealTimeProfitChain.ITEM_NUMBER,
                    ExcelColumnConstant.RealTimeProfitChain.ITEM_NAME,
                    ExcelColumnConstant.RealTimeProfitChain.CURRENT_MONEY,
                    ExcelColumnConstant.RealTimeProfitChain.LATELY_CURRENT_MONEY,
                    ExcelColumnConstant.RealTimeProfitChain.CHAIN_COMPARE_PROPORTION
            );
        }

    }


    /**
     * @param response
     * @return void
     * @Author liBao
     * @Date 2020/2/11 15:21
     * @Param [baseShopDto, response]
     * @Description 预算比导出
     */
    @Override
    public void exportRealTimeProfitBudget(ExcelExportDto excelExportDto, HttpServletResponse response) {
        RealTimeProfitDto realTimProfitDto = new RealTimeProfitDto();
        FastUtils.copyProperties(excelExportDto, realTimProfitDto);
        List<RealTimeProfitVo> list = findRealTimeProfitAnalysis(realTimProfitDto);

        List<RealTimeProfitVo> exportList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            //根据类型过滤
            if (ReportDataConstant.Finance.All_PROFIT.equals(realTimProfitDto.getType())) {
                exportList = list.stream().filter(info -> ReportDataConstant.Finance.All_PROFIT.equals(info.getType())).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.MAIN_PROFIT.equals(realTimProfitDto.getType())) {
                exportList = list.stream().filter(info -> ReportDataConstant.Finance.MAIN_PROFIT.equals(info.getType())).collect(Collectors.toList());
            }
        }
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            excelExportDto.setBeginDate(s.parse(excelExportDto.getBeginTime()));
            excelExportDto.setEndDate(s.parse(excelExportDto.getEndTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileService.exportExcelForQueryTerm(response, excelExportDto, exportList,
                ExcelColumnConstant.RealTimeProfitBudget.ITEM_NUMBER,
                ExcelColumnConstant.RealTimeProfitBudget.ITEM_NAME,
                ExcelColumnConstant.RealTimeProfitBudget.CURRENT_MONEY,
                ExcelColumnConstant.RealTimeProfitBudget.BUDGET_MONEY,
                ExcelColumnConstant.RealTimeProfitBudget.BUDGET_COMPARE
        );

    }

    /**
     * 计算毛利率 （主营业务收入-主营业务成本）/主营业务收入
     * 成本
     *
     * @param realTimProfitDto
     * @return
     */
    public RealTimeProfitVo getGrossMargin(RealTimeProfitDto realTimProfitDto) {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sf.parse(realTimProfitDto.getBeginTime());
            Date endDate = sf.parse(realTimProfitDto.getEndTime());
            realTimProfitDto.setBeginDate(startDate);
            realTimProfitDto.setEndDate(endDate);
        } catch (Exception e) {

        }
        //Dto转换
        FinQueryDto queryDto = getFinQueryDto(realTimProfitDto);
        List<RealTimeProfitVo> list = reportTableMapper.getGrossMargin(queryDto);
        BigDecimal getMoney = CollectionUtil.isEmpty(list) == true ? BigDecimal.ZERO : list.stream().filter(data->ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY.equals(data.getItemCode())).map(RealTimeProfitVo::getCurrentMoney).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal payMoney = CollectionUtil.isEmpty(list) == true ? BigDecimal.ZERO : list.stream().filter(data->ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(data.getItemCode())).map(RealTimeProfitVo::getCurrentMoney).reduce(BigDecimal.ZERO,BigDecimal::add);

//        RepPosDetailFoodVo repPosDetailFoodVo = new RepPosDetailFoodVo();
//        if (realTimProfitDto.getIsQueryGrossMargin() == null || Constant.Number.ONE.equals(realTimProfitDto.getIsQueryGrossMargin())) {
//            //查询销售菜品明细表
//            RepPosDetailFoodDto repPosDetailFoodDto = new RepPosDetailFoodDto();
//            repPosDetailFoodDto.setShopIdList(queryDto.getShopIdList());
//            repPosDetailFoodDto.setBeginDate(queryDto.getBeginDate());
//            repPosDetailFoodDto.setEndDate(queryDto.getEndDate());
//            repPosDetailFoodDto.setEnteId(queryDto.getEnteId());
//            repPosDetailFoodDto.setShopTypeIdList(queryDto.getShopTypeIdList());
//            //查询收入合计
//            List<FinRentAccountedForVo> saleList = baseDeskService.findSaleByCondition(queryDto);
//            repPosDetailFoodVo.setFoodAllPrice(CollectionUtil.isEmpty(saleList) == true ? BigDecimal.ZERO : saleList.stream().map(FinRentAccountedForVo::getMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add));
//        }
//
//
//        //4、主营业务成本 -供应链
//        BigDecimal mainBusinessCost;
//        //4.1 菜品成本
//        //查询菜品成本
//        ScmQueryDto scmQueryDto = new ScmQueryDto();
//        scmQueryDto.setShopIdList(queryDto.getShopIdList());
//        scmQueryDto.setBeginDate(queryDto.getBeginDate());
//        scmQueryDto.setEndDate(queryDto.getEndDate());
//        scmQueryDto.setEnteId(queryDto.getEnteId());
//        scmQueryDto.setShopTypeIdList(queryDto.getShopTypeIdList());
//        scmQueryDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);
//
//        List<DishGrossProfitVo> dishBaseList = footStyleAnalysisService.getDishBaseList(scmQueryDto, null);
//        BigDecimal costOfDishes = CollectionUtil.isEmpty(dishBaseList) == true ? BigDecimal.ZERO : dishBaseList.stream().filter(data -> data.getCostAmount() != null).map(DishGrossProfitVo::getCostAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        //4.2 酒水成本
//        //计算酒水成本和员工餐
//        List<CompanyVo> materialCostList = getCostOfWineAndMeal(sf, queryDto);
//        BigDecimal beverageCost = CollectionUtil.isEmpty(materialCostList) == true ? BigDecimal.ZERO : materialCostList.stream().filter(data -> data.getMaterialCost() != null && ReportDataConstant.RealProfitItemCode.COST_OF_WINE.equals(data.getDescription())).
//                map(CompanyVo::getMaterialCost).reduce(BigDecimal.ZERO, BigDecimal::add);
//        //4.3 杂项成本
//        //查询杂项成本  供应链-库存管理-盘亏单【取物料编码40.06.008、40.06.009总成本】
//
//        List<String> codeList = new ArrayList<>();
//        String[] codes = ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS.split(",");
//        for (String code : codes) {
//            codeList.add(code);
//        }
//        queryDto.setCodes(codeList);
//        List<RealProfitVo> countLossList = businessAnalysisMapper.findCountLossListByShopId(queryDto);
//        BigDecimal miscellaneousCost = CollectionUtil.isEmpty(countLossList) == true ? BigDecimal.ZERO : countLossList.stream().map(RealProfitVo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        mainBusinessCost = costOfDishes.add(beverageCost).add(miscellaneousCost);
//        RealTimeProfitVo profitVo = new RealTimeProfitVo();
//        if (realTimProfitDto.getIsQueryGrossMargin() == null || Constant.Number.ONE.equals(realTimProfitDto.getIsQueryGrossMargin())) {
//            BigDecimal rentPercent = BigDecimalUtils.getPercent(repPosDetailFoodVo.getFoodAllPrice() == null ? BigDecimal.ZERO : repPosDetailFoodVo.getFoodAllPrice().
//                    subtract(mainBusinessCost == null ? BigDecimal.ZERO : mainBusinessCost), mainBusinessCost == null ? BigDecimal.ZERO : mainBusinessCost);
//            profitVo.setItemName(ReportDataConstant.ReportRealTimeSupItemName.GROSS_PROFIT_MARGIN);
//            profitVo.setGrossMargin(rentPercent);
//        } else {
//            profitVo.setItemName(ReportDataConstant.ReportRealTimeSupItemName.COST);
//            profitVo.setDishesCost(costOfDishes);
//            profitVo.setSaleCost(mainBusinessCost);
//        }
        RealTimeProfitVo profitVo = new RealTimeProfitVo();
        BigDecimal rentPercent = payMoney == BigDecimal.ZERO ? BigDecimal.ZERO : BigDecimalUtils.getPercent(getMoney.subtract(payMoney),getMoney);
//                    subtract(mainBusinessCost == null ? BigDecimal.ZERO : mainBusinessCost), mainBusinessCost == null ? BigDecimal.ZERO : mainBusinessCost);
            profitVo.setItemName(ReportDataConstant.ReportRealTimeSupItemName.GROSS_PROFIT_MARGIN);
            profitVo.setGrossMargin(rentPercent);
        return profitVo;
    }

    /**
     * @Description: 查询实时利润分析（本年、同比、环比、预算比）
     * @Param: [realTimProfitDto]
     * @return: RealTimeProfitVo
     * @Author: 周鹏
     * @Date: 2020/04/27
     */
    @Override
    public List<RealTimeProfitVo> findRealTimeProfitAnalysis(RealTimeProfitDto queryDto) {
        //step1:设置时间范围
        if (ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR.equals(queryDto.getDataType())) {
            //设置本年累计时间范围
            queryDto.setCalculationBeginDate(DateUtils.getFirstDayDateOfYear(queryDto.getBeginDate()));
            queryDto.setCalculationEndDate(queryDto.getEndDate());
        } else if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(queryDto.getDataType())) {
            //设置去年同期时间范围
            List<Date> dates = DateUtils.getLastYearDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
            queryDto.setCalculationBeginDate(dates.get(0));
            queryDto.setCalculationEndDate(dates.get(1));
        } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(queryDto.getDataType())) {
            //设置上期时间范围
            List<Date> dates = DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
            queryDto.setCalculationBeginDate(dates.get(0));
            queryDto.setCalculationEndDate(dates.get(1));
        } else if (ReportDataConstant.BusinessAnalysisDataType.BUDGETMONEY.equals(queryDto.getDataType())) {
            //预算比,设置时间范围为当天,避免查询出错
            Date budgetDate = new Date();
            queryDto.setCalculationBeginDate(budgetDate);
            queryDto.setCalculationEndDate(budgetDate);
        }
        //step2:查询项目及金额
        List<RealTimeProfitVo> list = businessAnalysisMapper.findRealTimeProfitList(queryDto);
        //step3:计算占比、同比、环比、预算比
        if (ReportDataConstant.BusinessAnalysisDataType.BUDGETMONEY.equals(queryDto.getDataType())) {
            //预算比
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            queryDto.setBeginTime(sdf.format(queryDto.getBeginDate()));
            queryDto.setEndTime(sdf.format(queryDto.getEndDate()));
            FinQueryDto finQueryDto = getFinQueryDto(queryDto);
            //查询实时利润预算设置项
            List<RealProfitBudgetVo> budgetVos = financeSubjectService.getRealProfitBudgetList(finQueryDto);
            calculationRealTimeBudgetPercent(list, finQueryDto, budgetVos);
        } else {
            CalculationRealTimePercent(list, queryDto.getDataType());
        }
        //step4:折叠或展开
        List<RealTimeProfitVo> resultList = new ArrayList<>();
        List<RealTimeProfitVo> all = new ArrayList<>();
        list.forEach(
                data -> {
                    RealTimeProfitVo realTime = new RealTimeProfitVo();
                    FastUtils.copyProperties(data, realTime);
                    realTime.setType(ReportDataConstant.ReportRealTimePutType.All);
                    all.add(realTime);
                }
        );
        List<RealTimeProfitVo> mainList = list.stream().filter(data -> ReportDataConstant.ReportItemLevel.LEVEL_ONE.equals(data.getItemLevel())).collect(Collectors.toList());
        mainList.forEach(
                data -> data.setType(ReportDataConstant.ReportRealTimePutType.MAIN)
        );
        resultList.addAll(all);
        resultList.addAll(mainList);
        return resultList;
    }


    public static void main(String[] args) {
        String start = "2020-02-01";
        String end = "2020-02-03";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startTime = sdf.parse(start);
           Date  endTime = sdf.parse(end);

            int day = DateUtils.getBetweenDay(startTime, endTime);
            System.out.println(day);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 计算预算比
     *
     * @param list      数据集合
     * @param queryDto  查询条件
     * @param budgetVos 预算设置项
     */
    private void calculationRealTimeBudgetPercent(List<RealTimeProfitVo> list, FinQueryDto queryDto, List<RealProfitBudgetVo> budgetVos) {
        Integer day = new Integer(1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //设置表有效期
        Date startTime = new Date();
        Date endTime = new Date();
        Date start = new Date();
        Date end = new Date();
        List<FinReportVo> finList;
        for (RealTimeProfitVo report : list) {
            finList = new ArrayList<>();
            FinReportVo fin;
            for (RealProfitBudgetVo vo : budgetVos) {
                if (report.getItemName().trim().equals(vo.getProject().trim())) {
                    try {
                        startTime = sdf.parse(vo.getBeginDate());
                        endTime = sdf.parse(vo.getEndDate());
                        start = sdf.parse(queryDto.getBeginTime());
                        end = sdf.parse(queryDto.getEndTime());
                        day = DateUtils.getBetweenDay(startTime, endTime)+1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //将字符串形式的时间转化为Date类型的时间
                    if (startTime.getTime() <= start.getTime() && endTime.getTime() >= end.getTime()) {
                        fin = new FinReportVo();
                        BigDecimal foodPrice = report.getCurrentMoney();
                        fin.setBudgetMoney(vo.getBudget().divide(new BigDecimal(day), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays())));
                        fin.setBudgetCompare(BigDecimalUtils.getPercent(foodPrice.subtract(fin.getBudgetMoney()), fin.getBudgetMoney()));
                        finList.add(fin);
                    }
                }
            }
            report.setBudgetMoney(CollectionUtil.isEmpty(finList) == true ? BigDecimal.ZERO : finList.stream().filter(data -> data.getBudgetMoney() != null).map(FinReportVo::getBudgetMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
            report.setBudgetCompare(CollectionUtil.isEmpty(finList) == true ? BigDecimal.ZERO : finList.stream().filter(data -> data.getBudgetCompare() != null).map(FinReportVo::getBudgetCompare).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
    }

    /**
     * 计算实时利润占比、同比、环比
     *
     * @param list     数据集合
     * @param dataType 类型 2:同比 3:环比 4:本年累计
     */
    private void CalculationRealTimePercent(List<RealTimeProfitVo> list, Integer dataType) {
        List<RealTimeProfitVo> mainBusinessList = list.stream().filter(a -> a.getItemCode().equals(ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY))
                .collect(Collectors.toList());
        //主营业务收入本期金额
        BigDecimal currentMainBusiness = CollectionUtils.isEmpty(mainBusinessList) ? BigDecimal.ZERO : mainBusinessList.get(0).getCurrentMoney();
        //主营业务收入本年累计金额
        BigDecimal yearMainBusiness = CollectionUtils.isEmpty(mainBusinessList) ? BigDecimal.ZERO : mainBusinessList.get(0).getCalculationMoney();
        list.forEach(report -> {
            if (ReportDataConstant.BusinessAnalysisDataType.CURRENTYEAR.equals(dataType)) {
                //类型为本年累计,先计算本期占比
                calculateRealTimeProfitProportion(ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY, report, currentMainBusiness);
                //设置本年累计金额,计算本年累计占比
                report.setYearMoney(report.getCalculationMoney());
                calculateRealTimeProfitProportion(dataType, report, yearMainBusiness);
            } else if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)) {
                //类型为同比,设置去年同期金额,计算同比
                report.setLastCurrentMoney(report.getCalculationMoney());
                calculateRealTimeProfitProportion(dataType, report, currentMainBusiness);
            } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType)) {
                //类型为同比,设置上期金额,计算环比
                report.setLatelyCurrentMoney(report.getCalculationMoney());
                calculateRealTimeProfitProportion(dataType, report, currentMainBusiness);
            }
        });
    }


}

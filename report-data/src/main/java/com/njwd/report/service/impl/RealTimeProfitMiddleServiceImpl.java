package com.njwd.report.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.njwd.basedata.service.BaseDeskService;
import com.njwd.basedata.service.BaseReportItemSetService;
import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;
import com.njwd.common.ScmConstant;
import com.njwd.entity.basedata.BaseShop;
import com.njwd.entity.basedata.dto.BaseReportItemSetDto;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseReportItemSetVo;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.dto.querydto.ScmQueryDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;
import com.njwd.entity.reportdata.vo.fin.FinReportVo;
import com.njwd.entity.reportdata.vo.fin.RealProfitVo;
import com.njwd.entity.reportdata.vo.scm.DishGrossProfitVo;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.report.service.RealTimeProfitMiddleService;
import com.njwd.reportdata.mapper.BusinessAnalysisMapper;
import com.njwd.reportdata.mapper.RealTimeProfitReportTableMapper;
import com.njwd.reportdata.mapper.SaleAnalysisMapper;
import com.njwd.reportdata.service.*;
import com.njwd.reportdata.service.impl.ScmReportTableServiceImpl;
import com.njwd.service.FileService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import com.njwd.utils.MergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName RealTimeProfitMiddleServiceImpl
 * @Description RealTimeProfitMiddleServiceImpl
 * @Author admin
 * @Date 2020/4/28 14:59
 */
@Service
public class RealTimeProfitMiddleServiceImpl implements RealTimeProfitMiddleService {

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
     * @param realTimeProfitDto
     * @Description: 实时利润分析（前置）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    @Override
    public int realTimeProfitMiddle(RealTimeProfitDto realTimeProfitDto) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfHour = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = sdf.parse(DateUtils.getCurrentDate("yyyy-MM-dd"));
        int insertCount = 0;
        //跑全量
        if (realTimeProfitDto.getBeginDate() != null) {
            logger.info("开始删除" + realTimeProfitDto.getBeginDate() + "之后的数据：");
            int deleteCount = reportTableMapper.deleteByParam(realTimeProfitDto);
            logger.info("删除完成，删除" + deleteCount + "条数据！！！");
            Calendar cal = Calendar.getInstance();
            cal.setTime(realTimeProfitDto.getBeginDate());
            realTimeProfitDto.setBeginDate(cal.getTime());
            logger.info("开始插入" + realTimeProfitDto.getBeginDate() + "之后的数据：");

            while (cal.getTime().compareTo(curDate) <= Constant.Number.ZERO) {
                realTimeProfitDto.setBeginTime(sdf.format(realTimeProfitDto.getBeginDate()));
                cal.set(Calendar.HOUR,23);
                cal.set(Calendar.MINUTE,59);
                cal.set(Calendar.SECOND,59);
                cal.set(Calendar.MILLISECOND,999);
                realTimeProfitDto.setEndDate(cal.getTime());
                realTimeProfitDto.setEndTime(sdfHour.format(realTimeProfitDto.getEndDate()));

                List<BaseShopVo> resultList = findRealTimeProfitMiddle(realTimeProfitDto);
                insertCount += reportTableMapper.addBatch(resultList);
                //时间加一天
                cal.setTime(realTimeProfitDto.getBeginDate());
                cal.add(Calendar.DATE, 1);
                realTimeProfitDto.setBeginDate(cal.getTime());
            }
            logger.info("插入结束,共插入" + insertCount + "条数据！！！");
        } else {
            Calendar calAdd = Calendar.getInstance();
            calAdd.setTime(curDate);
            calAdd.add(Calendar.DATE, -3);
            realTimeProfitDto.setBeginDate(calAdd.getTime());
            logger.info("开始删除" + realTimeProfitDto.getBeginDate() + "之后的数据：");
            int deleteCount = reportTableMapper.deleteByParam(realTimeProfitDto);
            logger.info("删除完成，删除" + deleteCount + "条数据！！！");
            logger.info("开始插入" + realTimeProfitDto.getBeginDate() + "之后的数据：");
            while (calAdd.getTime().compareTo(curDate) <= Constant.Number.ZERO) {

                realTimeProfitDto.setBeginTime(sdf.format(realTimeProfitDto.getBeginDate()));
                calAdd.set(Calendar.HOUR,23);
                calAdd.set(Calendar.MINUTE,59);
                calAdd.set(Calendar.SECOND,59);
                calAdd.set(Calendar.MILLISECOND,999);
                realTimeProfitDto.setEndDate(calAdd.getTime());
                realTimeProfitDto.setEndTime(sdfHour.format(realTimeProfitDto.getEndDate()));
                List<BaseShopVo> resultList = findRealTimeProfitMiddle(realTimeProfitDto);
                insertCount += reportTableMapper.addBatch(resultList);
                //时间加一天

//                calAdd.add(Calendar.DATE, 1);
//                calAdd.set(Calendar.HOUR,0);
//                calAdd.set(Calendar.MINUTE,0);
//                calAdd.set(Calendar.SECOND,0);
//                calAdd.set(Calendar.MILLISECOND,0);
                calAdd.setTime(realTimeProfitDto.getBeginDate());
                calAdd.add(Calendar.DATE, 1);
                realTimeProfitDto.setBeginDate(calAdd.getTime());
            }
            logger.info("插入结束,共插入" + insertCount + "条数据！！！");
        }
        return insertCount;
    }



    /**
     * @param realTimeProfitDto
     * @Description: 实时利润分析（中间表）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    @Override
    public List<BaseShopVo> findRealTimeProfitMiddle(RealTimeProfitDto realTimeProfitDto) throws ParseException {

        List<RealTimeProfitVo> realTimeProfitVos = new ArrayList<>();
        //查询预置项目项,用户获取配置条件
        BaseReportItemSetDto baseReportItemSetDto = new BaseReportItemSetDto();
        baseReportItemSetDto.setEnteId(realTimeProfitDto.getEnteId());
        baseReportItemSetDto.setReportId(ReportDataConstant.ReportItemReportId.REAL_TIME_PROFIT);
        List<BaseReportItemSetVo> baseReportItemSetVos = baseReportItemSetService.findBaseReportItemSetVoByReportId(baseReportItemSetDto);
        //初始化报表左侧项目数
        initRealTimeProfit(realTimeProfitVos, baseReportItemSetVos);

        //计算本期、本年、同比、环比
        //查询支付方式明细表
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        //Dto转换
        FinQueryDto queryDto = getFinQueryDto(realTimeProfitDto);

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
        //查询菜品金额明细按照门店分组
        List<RepPosDetailFoodVo> repPosDetailFoodVos = repPosDetailFoodService.findRepPosDetailFoodList(repPosDetailFoodDto);
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
        queryDto.setPeriodDays(allPeriod.size() * queryDto.getMonthDays());
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

        //查询所有门店信息
        BaseShopDto param = new BaseShopDto();
        List<BaseShopVo> shopVos = organizationService.findShopList(param);
        //计算金额
        List<BaseShopVo> resultList = calculationMoney(realTimeProfitVos, shopVos, saleList,
                repPosDetailFoodVos, shopSubjects, configVos, queryDto,
                voForLastYearSubject, shouldDeprList, layOutList, countLossList, shopInfoList, beerList, dishBaseList, shopScoreVos,
                amortSchemeListByShop, materialCostList, bonusList);

        return resultList;
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
        //int days = DateUtils.getBetweenDay(realTimProfitDto.getBeginTime(), realTimProfitDto.getEndTime(), "yyyy-MM-dd");
        //中间表以天为单位
        int days = Constant.Number.ONE;
        queryDto.setDays(days);
        int monthDays = DateUtils.getDaysByYearMonth(realTimProfitDto.getBeginTime());
        queryDto.setMonthDays(monthDays);
        return queryDto;
    }

    /**
     * 获取7.23.01	营业费用_服务咨询费_管理服务费	（收入合计）（1+税率）*3.8%
     *
     * @param repPosDetailPayDto
     * @param queryDto
     * @return
     */
    @Override
    public List<CompanyVo> getBussnessManageFee(List<FinRentAccountedForVo> saleList, RepPosDetailPayDto repPosDetailPayDto, FinQueryDto queryDto) {
        //查询  7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
        //赠送金额
        //List<PosDiscountDetailPayVo> giveMoneyList = saleAnalysisMapper.findDetailPayListGpByShopId(repPosDetailPayDto);
        //折扣额
        //List<PosDiscountDetailPayVo> disCountMoneyList = saleAnalysisMapper.findDetailPayListTwoGPByShopId(repPosDetailPayDto);
        //查询门店信息
        List<CompanyVo> shopInfoList = businessAnalysisMapper.findShopInfoList(queryDto);

        //合并门店税额和赠送金额
//        MergeUtil.merge(shopInfoList, giveMoneyList,
//                CompanyVo::getShopId, PosDiscountDetailPayVo::getShopId,
//                (shopInfo, payInfo) -> {
//                    BigDecimal payMoney = payInfo.getCurrentMoney();
//                    shopInfo.setExemptionTotal(payMoney == null ? new BigDecimal("0.00") : payMoney);
//                });
        //合并门店税额和收入合计
        MergeUtil.merge(shopInfoList, saleList,
                CompanyVo::getShopId, FinRentAccountedForVo::getShopId,
                (shopInfo, accountInfo) -> {
                    BigDecimal businessManageTotal = accountInfo.getMoneyActual() == null ? new BigDecimal("0.00") : (accountInfo.getMoneyActual().add(accountInfo.getBusinessSurcharges())).
                                          multiply((new BigDecimal(1.00).add(shopInfo.getMdTax()))).multiply(new BigDecimal(0.038));
                    shopInfo.setBusinessManageTotal(businessManageTotal == null ? new BigDecimal("0.00") : businessManageTotal);
                });

        //合并赠送金额和折扣额为优免合计,并且计算出每个门店的营业管理费
//        MergeUtil.merge(shopInfoList, disCountMoneyList,
//                CompanyVo::getShopId, PosDiscountDetailPayVo::getShopId,
//                (shopInfo, disCountInfo) -> {
//                    BigDecimal disCountMoney = disCountInfo.getTotalMoney();
//                    BigDecimal exemptionMoney = shopInfo.getExemptionTotal().add(disCountMoney == null ? new BigDecimal("0.00") : disCountMoney);
//                    BigDecimal BusinessManageTotal = (shopInfo.getSaleTotal().subtract(exemptionMoney))
//                            .multiply((new BigDecimal(1.00).add(shopInfo.getMdTax()))).multiply(new BigDecimal(0.038));
//                    shopInfo.setBusinessManageTotal(BusinessManageTotal);
//                });
        return shopInfoList;
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
        List<BrandBonusVo> bonusList = CollectionUtil.isEmpty(bonusVos) == true ? null : bonusVos.stream().filter(data -> data != null && data.getShopId() != null && data.getAmount() != null && ReportDataConstant.Finance.TYPE_SHOP.equals(data.getType())).collect(Collectors.toList());
        List<CompanyVo> bonusVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(bonusList)) {
            bonusList.forEach(data -> {
                CompanyVo companyVo = new CompanyVo();
                companyVo.setShopId(data.getShopId());
                companyVo.setBonusMoney(data.getAmount()==null?BigDecimal.ZERO:data.getAmount().divide(new BigDecimal(queryDto.getMonthDays()),2,BigDecimal.ROUND_HALF_UP));
                bonusVoList.add(companyVo);
            });
        }
        return bonusVoList;
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
     * @Description: 计算本期
     * @Param: [reportPosDeskVos, repPosDetailPayVos, report, dataType]
     * @return: void
     * @Author: liBao
     * @Date: 2020/2/20 11:41
     */
    private List<BaseShopVo> calculationMoney(List<RealTimeProfitVo> realTimeProfitVos, List<BaseShopVo> shopVos, List<FinRentAccountedForVo> saleList, List<RepPosDetailFoodVo> repPosDetailFoodVos,
                                              List<FinReportVo> shopSubject, List<FinReportConfigVo> configVos, FinQueryDto queryDto,
                                              List<FinReportVo> voForLastYearSubject, List<RealProfitVo> shouldDeprList, List<RealProfitVo> layOutList,
                                              List<RealProfitVo> countLossList, List<CompanyVo> shopInfoList, List<CompanyVo> beerList, List<DishGrossProfitVo> dishBaseList,
                                              List<ShopSalaryVo> shopScoreVos, List<CompanyVo> amortSchemeListByShop, List<CompanyVo> materialCostList,
                                              List<CompanyVo> bonusVoList) throws ParseException {

        //存放最终结果
        List<BaseShopVo> resultList = new ArrayList<>();
        //1、实收合计(主营业务收入)
        List<FinRentAccountedForVo> businessList = new ArrayList<>();
        Map<String, List<FinRentAccountedForVo>> businessMap = CollectionUtil.isEmpty(saleList) == true ? null :
                saleList.stream().filter(data -> data.getShopId() != null).collect(Collectors.groupingBy(FinRentAccountedForVo::getShopId));
        //按照门店汇总
        if (CollectionUtil.isNotEmpty(businessMap)) {
            businessMap.forEach((key, value) -> {
                FinRentAccountedForVo vo = new FinRentAccountedForVo();
                final BigDecimal[] amount = {BigDecimal.ZERO};
                value.forEach(
                        data -> amount[0] = amount[0].add(data.getMoneyActual())
                );
                vo.setShopId(key);
                vo.setMoneyActual(amount[0]);
                businessList.add(vo);
            });
        }
        //和全部门店合并
        List<BaseShopVo> businessVos = getNewShopVoList(shopVos);
        businessVos.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY));
        if (CollectionUtil.isNotEmpty(businessList)) {
            MergeUtil.merge(businessVos, businessList,
                    BaseShopVo::getShopId, FinRentAccountedForVo::getShopId,
                    (value1, value2) -> {
                        value1.setAmount(value2.getMoneyActual());
                    });
        }
        //加入结果集
        resultList.addAll(businessVos);

        //1.2、酒水饮料

        Map<String, List<RepPosDetailFoodVo>> oldBeverageMap = CollectionUtil.isEmpty(repPosDetailFoodVos) == true ? null : repPosDetailFoodVos.stream().filter(data -> Arrays.asList(ReportDataConstant.RealProfitItemCode.WINE_MONEY.split(",")).
                contains(data.getFoodStyleName())).collect(Collectors.groupingBy(RepPosDetailFoodVo::getShopId));
        List<RepPosDetailFoodVo> oldBeverageList = new ArrayList<>();
        //按照门店汇总
        if (CollectionUtil.isNotEmpty(oldBeverageMap)) {
            oldBeverageMap.forEach((key, value) -> {
                RepPosDetailFoodVo vo = new RepPosDetailFoodVo();
                final BigDecimal[] amount = {BigDecimal.ZERO};
                value.forEach(
                        data -> amount[0] = amount[0].add(data.getFoodAllPrice()==null?BigDecimal.ZERO:data.getFoodAllPrice())
                );
                vo.setShopId(key);
                vo.setFoodAllPrice(amount[0]);
                oldBeverageList.add(vo);
            });
        }
        List<BaseShopVo> newBeverageList =getNewShopVoList(shopVos);
        newBeverageList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.WINE_MONEY));
        if (CollectionUtil.isNotEmpty(oldBeverageList)) {
            MergeUtil.merge(newBeverageList, oldBeverageList,
                    BaseShop::getShopId, RepPosDetailFoodVo::getShopId,
                    (newBeverage, oldBeverage) -> newBeverage.setAmount(oldBeverage.getFoodAllPrice()));
        }
        resultList.addAll(newBeverageList);
        //1.3、杂项收入

        Map<String, List<RepPosDetailFoodVo>> oldOtherMap = CollectionUtil.isEmpty(repPosDetailFoodVos) == true ? null : repPosDetailFoodVos.stream().filter(data -> Arrays.asList(ReportDataConstant.RealProfitItemCode.MISCELLANEOUS_MONEY.split(",")).
                contains(data.getFoodStyleName())).collect(Collectors.groupingBy(RepPosDetailFoodVo::getShopId));

        List<RepPosDetailFoodVo> oldOtherList = new ArrayList<>();
        //按照门店汇总
        if (CollectionUtil.isNotEmpty(oldOtherMap)) {
            oldOtherMap.forEach((key, value) -> {
                RepPosDetailFoodVo vo = new RepPosDetailFoodVo();
                final BigDecimal[] amount = {BigDecimal.ZERO};
                value.forEach(
                        data -> amount[0] = amount[0].add(data.getFoodAllPrice()==null?BigDecimal.ZERO:data.getFoodAllPrice())
                );
                vo.setShopId(key);
                vo.setFoodAllPrice(amount[0]);
                oldOtherList.add(vo);
            });
        }
        List<BaseShopVo> newOtherList = getNewShopVoList(shopVos);
        newOtherList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.MISCELLANEOUS_MONEY));

        if (CollectionUtil.isNotEmpty(oldOtherList)) {
            MergeUtil.merge(newOtherList, oldOtherList,
                    BaseShop::getShopId, RepPosDetailFoodVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getFoodAllPrice()));
        }
        resultList.addAll(newOtherList);

        //1.1、菜品收入= 实收合计-酒水-杂项
        List<BaseShopVo> newFoodList = getNewShopVoList(shopVos);
        newFoodList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.VARIETY_OF_DISHES_MONEY));

        MergeUtil.merge(businessVos, newFoodList,
                BaseShopVo::getShopId, BaseShop::getShopId,
                (value1, value2) -> value2.setAmount(value1.getAmount()));

        MergeUtil.merge(newFoodList, newBeverageList,
                BaseShopVo::getShopId, BaseShop::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().subtract(value2.getAmount())));

        MergeUtil.merge(newFoodList, newOtherList,
                BaseShopVo::getShopId, BaseShop::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().subtract(value2.getAmount())));
        resultList.addAll(newFoodList);

        //2、其他业务收入  -供应链
        List<BaseShopVo> newBeerList = getNewShopVoList(shopVos);
        newBeerList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_MONEY));
        if (CollectionUtil.isNotEmpty(beerList)) {
            MergeUtil.merge(newBeerList, beerList,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, value2) -> {
                        value1.setAmount(value2.getBeerIntoFactoryFee());
                    });
        }
        resultList.addAll(newBeerList);
        //3、营业外收入

        FinReportConfigVo configVo = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_OUTSIDE_BUSINESS_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        Map<String, List<FinReportVo>> outsideBussinessFeeMap = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> Arrays.asList(configVo.getCodes().split(",")).
                contains(data.getAccountSubjectCode())).collect(Collectors.groupingBy(FinReportVo::getShopId));

        //按照门店汇总
        List<FinReportVo> oldOutBussinessList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(outsideBussinessFeeMap)) {
            outsideBussinessFeeMap.forEach((key, value) -> {
                FinReportVo vo = new FinReportVo();
                final BigDecimal[] amount = {BigDecimal.ZERO};
                value.forEach(
                        data -> amount[0] = amount[0].add(data.getDebitAmount())
                );
                vo.setShopId(key);
                vo.setAmount(amount[0]);
                oldOutBussinessList.add(vo);
            });
        }
        List<BaseShopVo> newOutBussinessList = getNewShopVoList(shopVos);
        newOutBussinessList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_MONEY));
        if (CollectionUtil.isNotEmpty(oldOutBussinessList)) {
            MergeUtil.merge(newOutBussinessList, oldOutBussinessList,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getAmount()));
        }
        resultList.addAll(newOutBussinessList);
        //4、主营业务成本 -供应链
        List<BaseShopVo> newBusinessCostList = getNewShopVoList(shopVos);
        newBusinessCostList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST));
        //4.1 菜品成本
        Map<String, List<DishGrossProfitVo>> costOfDishesMap = CollectionUtil.isEmpty(dishBaseList) == true ? null : dishBaseList.stream().filter(data -> data.getCostAmount() != null).collect(Collectors.groupingBy(DishGrossProfitVo::getShopId));
        //按照门店汇总
        List<DishGrossProfitVo> oldCostOfDishesList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(costOfDishesMap)) {
            costOfDishesMap.forEach((key, value) -> {
                DishGrossProfitVo vo = new DishGrossProfitVo();
                final BigDecimal[] amount = {BigDecimal.ZERO};
                value.forEach(
                        data -> amount[0] = amount[0].add(data.getCostAmount())
                );
                vo.setShopId(key);
                vo.setCostAmount(amount[0]);
                oldCostOfDishesList.add(vo);
            });
        }
        List<BaseShopVo> newCostOfDishesList = getNewShopVoList(shopVos);
        newCostOfDishesList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.COST_OF_DISHES));
        if (CollectionUtil.isNotEmpty(oldCostOfDishesList)) {
            MergeUtil.merge(newCostOfDishesList, oldCostOfDishesList,
                    BaseShopVo::getShopId, DishGrossProfitVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getCostAmount()));
        }
        resultList.addAll(newCostOfDishesList);
        //4.2 酒水成本
        Map<String, List<CompanyVo>> beverageCostMap = CollectionUtil.isEmpty(materialCostList) == true ? null : materialCostList.stream().filter(data -> data.getMaterialCost() != null && ReportDataConstant.RealProfitItemCode.COST_OF_WINE.equals(data.getDescription()))
                .collect(Collectors.groupingBy(CompanyVo::getShopId));

        //按照门店汇总
        List<CompanyVo> oldBeverageCostList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(beverageCostMap)) {
            beverageCostMap.forEach((key, value) -> {
                CompanyVo vo = new CompanyVo();
                final BigDecimal[] amount = {BigDecimal.ZERO};
                value.forEach(
                        data -> amount[0] = amount[0].add(data.getMaterialCost())
                );
                vo.setShopId(key);
                vo.setMaterialCost(amount[0]);
                oldBeverageCostList.add(vo);
            });
        }
        List<BaseShopVo> newBeverageCostList = getNewShopVoList(shopVos);
        newBeverageCostList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.COST_OF_WINE));
        if (CollectionUtil.isNotEmpty(oldBeverageCostList)) {
            MergeUtil.merge(newBeverageCostList, oldBeverageCostList,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getMaterialCost()));
        }
        resultList.addAll(newBeverageCostList);
        //4.3 杂项成本
        List<BaseShopVo> newMiscellaneousCost = getNewShopVoList(shopVos);
        newMiscellaneousCost.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.COST_OF_MISCELLANEOUS));
        if (CollectionUtil.isNotEmpty(countLossList)) {
            MergeUtil.merge(newMiscellaneousCost, countLossList,
                    BaseShopVo::getShopId, RealProfitVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getAmount()));
        }
        resultList.addAll(newMiscellaneousCost);


        //计算 4、主营业务成本
        MergeUtil.merge(newBusinessCostList, newCostOfDishesList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value2.getAmount()));

        MergeUtil.merge(newBusinessCostList, newBeverageCostList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(newBusinessCostList, newMiscellaneousCost,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));


        resultList.addAll(newBusinessCostList);

        //5、其他业务支出
        List<BaseShopVo> newOtherBusinessPayList = getNewShopVoList(shopVos);
        newOtherBusinessPayList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.OTHER_BUSINESS_PAY));
        //根据FinType获取配置项
        List<CompanyVo> otherBusinessPayList = getShopDataByFormula(shopSubject, configVos, ReportDataConstant.FinType.REALTIME_PROFIT_OTHER_BUSINESS_PAY);
        if (CollectionUtil.isNotEmpty(otherBusinessPayList)) {
            MergeUtil.merge(newOtherBusinessPayList, otherBusinessPayList,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getAmount()));
        }
        resultList.addAll(newOtherBusinessPayList);

        //6、营业外支出
        List<BaseShopVo> newOutsidePayList = getNewShopVoList(shopVos);
        newOutsidePayList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.OUTSIDE_BUSINESS_PAY));
        //根据FinType获取配置项
        List<CompanyVo> outsideBusinessPayList = getShopDataByFormula(shopSubject, configVos, ReportDataConstant.FinType.REALTIME_PROFIT_OUTSIDE_BUSINESS_PAY);
        if (CollectionUtil.isNotEmpty(outsideBusinessPayList)) {
            MergeUtil.merge(newOutsidePayList, outsideBusinessPayList,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getAmount()));
        }
        resultList.addAll(newOutsidePayList);
        //7、营业税金及附加
        List<BaseShopVo> newTaxAndAddList = getNewShopVoList(shopVos);
        newTaxAndAddList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.TAXES_AND_ADD_BUSINESS));
        List<CompanyVo> taxAndAddList = getShopDataByFormula(shopSubject, configVos, ReportDataConstant.FinType.REALTIME_PROFIT_TAXES_AND_ADD_BUSINESS);
        if (CollectionUtil.isNotEmpty(taxAndAddList)) {
            MergeUtil.merge(newTaxAndAddList, taxAndAddList,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getAmount()));
        }
        resultList.addAll(newTaxAndAddList);

        //8.营业费用
        List<BaseShopVo> businessMoney = getNewShopVoList(shopVos);
        businessMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY));
        //8.1  营业费用_职工薪酬
        List<BaseShopVo> staffSalaries = getNewShopVoList(shopVos);
        staffSalaries.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY));
        //8.01.01  营业费用_职工薪酬_在职工资  乐才表
        List<BaseShopVo> newWorkWagesList = getNewShopVoList(shopVos);
        newWorkWagesList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_ONLINE_PAY));
        if (CollectionUtil.isNotEmpty(shopScoreVos)) {
            MergeUtil.merge(newWorkWagesList, shopScoreVos,
                    BaseShopVo::getShopId, ShopSalaryVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getGrossSalary() == null ? BigDecimal.ZERO : value2.getGrossSalary()
                            .divide(new BigDecimal(queryDto.getMonthDays()), 2, BigDecimal.ROUND_HALF_UP)));
        }
        resultList.addAll(newWorkWagesList);

        //8.01.02  营业费用_职工薪酬_离职工资
        //根据FinType获取配置项
        FinReportConfigVo configVoQuit = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_QUIT_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> quitMoneyList = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoQuit.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        List<BaseShopVo> newQuitMoneyList = getNewShopVoList(shopVos);
        newQuitMoneyList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_QUIT_PAY));
        if (CollectionUtil.isNotEmpty(quitMoneyList)) {
            if (CollectionUtil.isNotEmpty(quitMoneyList)) {
                MergeUtil.merge(newQuitMoneyList, quitMoneyList,
                        BaseShopVo::getShopId, FinReportVo::getShopId,
                        (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
            }
        }
        resultList.addAll(newQuitMoneyList);

        //8.01.03 营业费用_职工薪酬_奖金    乐才表

        List<BaseShopVo> newBonusList = getNewShopVoList(shopVos);
        newBonusList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_PAY_BONUS));
        if (CollectionUtil.isNotEmpty(bonusVoList)) {
            MergeUtil.merge(newBonusList, bonusVoList,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getBonusMoney()));
        }
        resultList.addAll(newBonusList);

        //计算 8.1  营业费用_职工薪酬
        MergeUtil.merge(staffSalaries, newWorkWagesList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(staffSalaries, newQuitMoneyList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(staffSalaries, newBonusList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        resultList.addAll(staffSalaries);

        //8.02 营业费用_水电气费
        List<BaseShopVo> waterElectricGasMoney = getNewShopVoList(shopVos);
        waterElectricGasMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_ELECTRIC_GAS_MONEY));
        //8.02.01 营业费用_水电气费_水费 最近一年累计/12/30*天数
        List<BaseShopVo> newWaterMoneyList = getNewShopVoList(shopVos);
        newWaterMoneyList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WATER_MONEY));
        FinReportConfigVo configVoWater = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_WATER_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> waterMoneyList = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoWater.getCodes().equals(data.getAccountSubjectCode())).
                collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(waterMoneyList)) {
            MergeUtil.merge(newWaterMoneyList, waterMoneyList,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
            (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                    divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newWaterMoneyList);

        //8.02.02 营业费用_电费
        List<BaseShopVo> newElectricMoneyList = getNewShopVoList(shopVos);
        newElectricMoneyList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ELECTRIC_MONEY));
        FinReportConfigVo configVoElectric = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ELECTRIC_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> electricMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().
                filter(data -> configVoElectric.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(electricMoney)) {
            MergeUtil.merge(newElectricMoneyList, electricMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newElectricMoneyList);

        //8.02.03 营业费用_燃气费
        List<BaseShopVo> newGasMoneyList = getNewShopVoList(shopVos);
        newGasMoneyList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GAS_MONEY));
        FinReportConfigVo configVoGas = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_GAS_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> gasMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoGas.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(gasMoney)) {
            MergeUtil.merge(newGasMoneyList, gasMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newGasMoneyList);

        // 计算 8.02 营业费用_水电气费
        MergeUtil.merge(waterElectricGasMoney, newWaterMoneyList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(waterElectricGasMoney, newElectricMoneyList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(waterElectricGasMoney, newGasMoneyList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        resultList.addAll(waterElectricGasMoney);


        //8.03 营业费用_租赁费
        List<BaseShopVo> newRentMoney = getNewShopVoList(shopVos);
        newRentMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_MONEY));

        //8.03.01 营业费用_租赁费_营业租金
        List<BaseShopVo> newRentBusinessMoney = getNewShopVoList(shopVos);
        newRentBusinessMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_BUSINESS_MONEY));

        FinReportConfigVo configVoRentBusiness = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_RENT_BUSINESS_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> rentBusinessMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoRentBusiness.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(rentBusinessMoney)) {
            MergeUtil.merge(newRentBusinessMoney, rentBusinessMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newRentBusinessMoney);

        //8.03.02 营业费用_租赁费_宿舍租金
        List<BaseShopVo> newRentHotelMoney = getNewShopVoList(shopVos);
        newRentHotelMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_RENT_HOSTEL_MONEY));
        FinReportConfigVo configVoRentHotel = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_RENT_HOSTEL_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> rentHotelMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoRentHotel.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(rentHotelMoney)) {
            MergeUtil.merge(newRentHotelMoney, rentHotelMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newRentHotelMoney);

        // 计算 8.03 营业费用_租赁费
        MergeUtil.merge(newRentMoney, newRentBusinessMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(newRentMoney, newRentHotelMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        resultList.addAll(newRentMoney);


        //8.04 营业费用_物业费
        List<BaseShopVo> newPropertyMoney = getNewShopVoList(shopVos);
        newPropertyMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PROPERTY_MONEY));
        FinReportConfigVo configVoProperty = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PROPERTY_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> propertyMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoProperty.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(propertyMoney)) {
            MergeUtil.merge(newPropertyMoney, propertyMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newPropertyMoney);


        //8.05 营业费用_赠送费用
        List<BaseShopVo> newGiveMoney = getNewShopVoList(shopVos);
        newGiveMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_GIVE_MONEY));

        FinReportConfigVo configVoGive = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_GIVE_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> giveMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoGive.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(giveMoney)) {
            MergeUtil.merge(newGiveMoney, giveMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newGiveMoney);


        //8.06	营业费用_低值易耗品
        List<BaseShopVo> newlowCostLabwareList = getNewShopVoList(shopVos);
        newlowCostLabwareList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE));

        //8.06.01	营业费用_低值易耗品_工具类
        List<BaseShopVo> newToolMoney = getNewShopVoList(shopVos);
        newToolMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_TOOL));
        FinReportConfigVo configVoTool = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_TOOL.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> toolMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoTool.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(toolMoney)) {
            MergeUtil.merge(newToolMoney, toolMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newToolMoney);

        //8.06.02	营业费用_低值易耗品_消耗类
        List<BaseShopVo> newConsumeMoney = getNewShopVoList(shopVos);
        newConsumeMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LOWCOSTLABWARE_CONSUME));
        FinReportConfigVo configVoConsume = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_CONSUME.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> consumeMoney = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoConsume.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(consumeMoney)) {
            MergeUtil.merge(newConsumeMoney, consumeMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newConsumeMoney);

        // 计算 8.06	营业费用_低值易耗品
        MergeUtil.merge(newlowCostLabwareList, newToolMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(newlowCostLabwareList, newConsumeMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        resultList.addAll(newlowCostLabwareList);


        //8.07	营业费用_修理费
        List<BaseShopVo> newRepairMoney = getNewShopVoList(shopVos);
        newRepairMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_PEPAIR_MONEY));
        FinReportConfigVo configVoRepair = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_PEPAIR_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> repairMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoRepair.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(repairMoney)) {
            MergeUtil.merge(newRepairMoney, repairMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newRepairMoney);

        //8.08	营业费用_清洁费
        List<BaseShopVo> nweCleanMoney = getNewShopVoList(shopVos);
        nweCleanMoney.forEach(
                data -> {
                    data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CLEAN_MONEY);
                    BigDecimal a = new BigDecimal(1650).divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP) ;
                    data.setAmount(a);
                });
        resultList.addAll(nweCleanMoney);


        //8.09	营业费用_运输费
        List<BaseShopVo> newConsumeTranSport = getNewShopVoList(shopVos);
        newConsumeTranSport.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRANSPORT_MONEY));

        FinReportConfigVo configVoTransport = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_TRANSPORT_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> consumeTranSport = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoTransport.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(consumeTranSport)) {
            MergeUtil.merge(newConsumeTranSport, consumeTranSport,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newConsumeTranSport);

        //8.10  营业费用_折旧费
        List<BaseShopVo> newOldMoney = getNewShopVoList(shopVos);
        newOldMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_DEPRECIATION_CHARGE_MONEY));
        if (CollectionUtil.isNotEmpty(shouldDeprList)) {
            MergeUtil.merge(newOldMoney, shouldDeprList,
                    BaseShopVo::getShopId, RealProfitVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getShouldDepr() == null ? BigDecimal.ZERO : value2.getShouldDepr().
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newOldMoney);


        //8.11	营业费用_劳动保险费
        List<BaseShopVo> newLaborInsurance = getNewShopVoList(shopVos);
        newLaborInsurance.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_INSURANCE_MONEY));
        if (CollectionUtil.isNotEmpty(shouldDeprList)) {
            MergeUtil.merge(newLaborInsurance, shopScoreVos,
                    BaseShopVo::getShopId, ShopSalaryVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getSocialSalary() == null ? BigDecimal.ZERO : value2.getSocialSalary().divide(new BigDecimal(queryDto.getMonthDays()), 2, BigDecimal.ROUND_HALF_UP)));
        }
        resultList.addAll(newLaborInsurance);


        //8.12	营业费用_福利费
        List<BaseShopVo> newWelfareMoney = getNewShopVoList(shopVos);
        newWelfareMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS));
        //8.12.01	营业费用_福利费_员工餐 供应链

        Map<String, List<CompanyVo>> staffMealMap = CollectionUtil.isEmpty(materialCostList) == true ? null : materialCostList.stream().filter(data -> data.getMaterialCost() != null && ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL.equals(data.getDescription()))
                .collect(Collectors.groupingBy(CompanyVo::getShopId));

        //按照门店汇总
        List<CompanyVo> oldStaffMealList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(staffMealMap)) {
            staffMealMap.forEach((key, value) -> {
                CompanyVo vo = new CompanyVo();
                final BigDecimal[] amount = {BigDecimal.ZERO};
                value.forEach(
                        data -> amount[0] = amount[0].add(data.getMaterialCost())
                );
                vo.setShopId(key);
                vo.setMaterialCost(amount[0]);
                oldStaffMealList.add(vo);
            });
        }
        List<BaseShopVo> newStaffMealList = getNewShopVoList(shopVos);
        newStaffMealList.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL));
        if (CollectionUtil.isNotEmpty(oldStaffMealList)) {
            MergeUtil.merge(newStaffMealList, oldStaffMealList,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getMaterialCost()));
        }
        resultList.addAll(newStaffMealList);


        //8.12.02	营业费用_福利费_其他福利
        List<BaseShopVo> newWelfareOtherMoney = getNewShopVoList(shopVos);
        newWelfareOtherMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WELFARE_FUNDS_OTHER));
        FinReportConfigVo configVoWelfareOther = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_WELFARE_OTHER.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> welfareOtherMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoWelfareOther.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(welfareOtherMoney)) {
            MergeUtil.merge(newWelfareOtherMoney, welfareOtherMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newWelfareOtherMoney);
        //计算    8.12	营业费用_福利费
        MergeUtil.merge(newWelfareMoney, newStaffMealList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        MergeUtil.merge(newWelfareMoney, newWelfareOtherMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        resultList.addAll(newWelfareMoney);


        //8.13	营业费用_办公费
        List<BaseShopVo> newOfficeMoney = getNewShopVoList(shopVos);
        newOfficeMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_WORK_MONEY));

        FinReportConfigVo configVoOffice = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_OFFICE_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> officeMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoOffice.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(officeMoney)) {
            MergeUtil.merge(newOfficeMoney, officeMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newOfficeMoney);


        //8.14	营业费用_差旅费
        List<BaseShopVo> newTravelMoney = getNewShopVoList(shopVos);
        newTravelMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_TRAVEL_MONEY));

        FinReportConfigVo configVoTravel = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_TRAVEL_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> travelMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoTravel.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(travelMoney)) {
            MergeUtil.merge(newTravelMoney, travelMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newTravelMoney);


        //8.15	营业费用_业务招待费
        List<BaseShopVo> newEntertainmentMoney = getNewShopVoList(shopVos);
        newEntertainmentMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_BUSINESS_ENTERTAINMENT_MONEY));


        FinReportConfigVo configVoEntertainment = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ENTERTAINMENT_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> entertainmentMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoEntertainment.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(entertainmentMoney)) {
            MergeUtil.merge(newEntertainmentMoney, entertainmentMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newEntertainmentMoney);

        //8.16	营业费用_广告宣传费
        List<BaseShopVo> newAdvertisingExpenses = getNewShopVoList(shopVos);
        newAdvertisingExpenses.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_MONEY));
        //8.16.01	营业费用_广告宣传费_广告费
        List<BaseShopVo> newAdvertising = getNewShopVoList(shopVos);
        newAdvertising.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY));

        List<CompanyVo> advertising = CollectionUtil.isEmpty(amortSchemeListByShop) == true ? null : amortSchemeListByShop.stream().filter(data -> data.getAmortName().contains(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY) == true)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(advertising)) {
            MergeUtil.mergeList(newAdvertising, advertising,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, valueList) -> valueList.forEach(data -> {
                        value1.setAmount(value1.getAmount().add(data.getAmortMoney() == null ? BigDecimal.ZERO : data.getAmortMoney().divide(new BigDecimal(queryDto.getPeriodDays()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
                    }));
        }
        resultList.addAll(newAdvertising);

        //8.16.02	营业费用_广告宣传费_企划费用
        List<BaseShopVo> newPlanningFee = getNewShopVoList(shopVos);
        newPlanningFee.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PLANNING_MONEY));
        if (CollectionUtil.isNotEmpty(layOutList)) {
            MergeUtil.merge(newPlanningFee, layOutList,
                    BaseShopVo::getShopId, RealProfitVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getAllAmount()));
        }
        resultList.addAll(newPlanningFee);


        //8.16.03	营业费用_广告宣传费_活动宣传费
        List<BaseShopVo> newAdvertisingActivityMoney = getNewShopVoList(shopVos);
        newAdvertisingActivityMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_ADVERTISING_EXPENSES_PROPAGANDA_MONEY));

        FinReportConfigVo configVoAdvertisingActivity = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_ADVERTISING_ACTIVITY_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> advertisingActivityMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoAdvertisingActivity.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(advertisingActivityMoney)) {
            MergeUtil.merge(newAdvertisingActivityMoney, advertisingActivityMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newAdvertisingActivityMoney);

        //计算  8.16	营业费用_广告宣传费
        MergeUtil.merge(newAdvertisingExpenses, newAdvertising,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(newAdvertisingExpenses, newPlanningFee,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(newAdvertisingExpenses, newAdvertisingActivityMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        resultList.addAll(newAdvertisingExpenses);


        //8.17	营业费用_劳动保护费
        List<BaseShopVo> newLaborProtectMoney = getNewShopVoList(shopVos);
        newLaborProtectMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOR_PROTECTION_MONEY));

        FinReportConfigVo configVoLaborProtect = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LABOR_PROTECTION_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> laborProtectMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoLaborProtect.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(laborProtectMoney)) {
            MergeUtil.merge(newLaborProtectMoney, laborProtectMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newLaborProtectMoney);


        //8.18	营业费用_装饰费
        List<BaseShopVo> newDecorateMoney = getNewShopVoList(shopVos);
        newDecorateMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_decorate_MONEY));

        FinReportConfigVo configVoDecorate = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_DECORATE_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> decorateMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoDecorate.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(decorateMoney)) {
            MergeUtil.merge(newDecorateMoney, decorateMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newDecorateMoney);


        //8.19	营业费用_工会经费
        List<BaseShopVo> newLabourUnionMoney = getNewShopVoList(shopVos);
        newLabourUnionMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LABOUR_UNION_MONEY));

        FinReportConfigVo configVoLabourUnion = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_LABOUR_UNION_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> labourUnionMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoLabourUnion.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(labourUnionMoney)) {
            MergeUtil.merge(newLabourUnionMoney, labourUnionMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newLabourUnionMoney);


        //8.20 	营业费用_职工教育经费
        List<BaseShopVo> newStaffEduMoney = getNewShopVoList(shopVos);
        newStaffEduMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_STAFF_EDUCATION_MONEY));

        FinReportConfigVo configVoStaffEdu = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_STAFF_EDUCATION_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> staffEduMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().
                filter(data -> configVoStaffEdu.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(staffEduMoney)) {
            MergeUtil.merge(newStaffEduMoney, staffEduMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newStaffEduMoney);


        //8.21	营业费用_住房公积金
        List<BaseShopVo> newHousingProvidentFundMoney = getNewShopVoList(shopVos);
        newHousingProvidentFundMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_HOUSING_PROVIDENT_FUND));
        FinReportConfigVo configVoHousingProvidentFund = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_HOUSING_PROVIDENT_FUND.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> housingProvidentFundMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().
                filter(data -> configVoHousingProvidentFund.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(housingProvidentFundMoney)) {
            MergeUtil.merge(newHousingProvidentFundMoney, housingProvidentFundMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newHousingProvidentFundMoney);

        //8.22	营业费用_长期待摊费
        List<BaseShopVo> newLongTermPending = getNewShopVoList(shopVos);
        newLongTermPending.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY));

        List<CompanyVo> longTermPending = CollectionUtil.isEmpty(amortSchemeListByShop) == true ? null : amortSchemeListByShop.stream().filter(data -> data.getAmortName().contains(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_LONG_TERM_PENDING_MONEY) == true)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(longTermPending)) {
            MergeUtil.mergeList(newLongTermPending, longTermPending,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, valueList) -> valueList.forEach(data -> {
                        value1.setAmount(value1.getAmount().add(data.getAmortMoney() == null ? BigDecimal.ZERO : data.getAmortMoney().divide(new BigDecimal(queryDto.getPeriodDays()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
                    }));
        }
        resultList.addAll(newLongTermPending);


        //8.23	营业费用_咨询服务培训费
        List<BaseShopVo> newConsultServiceTrainingFee = getNewShopVoList(shopVos);
        newConsultServiceTrainingFee.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_MONEY));
        //8.23.01	营业费用_服务咨询费_管理服务费
        List<BaseShopVo> newActualSum = getNewShopVoList(shopVos);
        newActualSum.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_MANAGE_MONEY));
        if (CollectionUtil.isNotEmpty(shopInfoList)) {
            MergeUtil.merge(newActualSum, shopInfoList,
                    BaseShopVo::getShopId, CompanyVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getBusinessManageTotal()));
        }
        resultList.addAll(newActualSum);


        //8.23.02	营业费用_服务咨询费_咨询服务费
        List<BaseShopVo> newConsultingServiceMoney = getNewShopVoList(shopVos);
        newConsultingServiceMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_CONSULTING_MONEY));

        FinReportConfigVo configVoConsultingService = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_CONSULTING_SERVICE.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> consultingServiceMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoConsultingService.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(consultingServiceMoney)) {
            MergeUtil.merge(newConsultingServiceMoney, consultingServiceMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newConsultingServiceMoney);

        //8.23.03	营业费用_服务咨询费_培训费
        List<BaseShopVo> newTrainMoney = getNewShopVoList(shopVos);
        newTrainMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_TRAINING_MONEY));
        FinReportConfigVo configVoTrain = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_TRAIN_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> trainMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoTrain.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(trainMoney)) {
            MergeUtil.merge(newTrainMoney, trainMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newTrainMoney);

        //计算   8.23	营业费用_咨询服务培训费
        MergeUtil.merge(newConsultServiceTrainingFee, newActualSum,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        MergeUtil.merge(newConsultServiceTrainingFee, newConsultingServiceMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        MergeUtil.merge(newConsultServiceTrainingFee, newTrainMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        resultList.addAll(newConsultServiceTrainingFee);


        //8.24	营业费用_商业保险
        List<BaseShopVo> newCommercialInsuranceMoney = getNewShopVoList(shopVos);
        newCommercialInsuranceMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMERCIAL_INSURANCE_MONEY));
        FinReportConfigVo configVoCommercialInsurance = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_COMMERCIAL_INSURANCE.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> commercialInsuranceMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoCommercialInsurance.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(commercialInsuranceMoney)) {
            MergeUtil.merge(newCommercialInsuranceMoney, commercialInsuranceMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newCommercialInsuranceMoney);

        //8.25	营业费用_通讯费
        List<BaseShopVo> newCommunicationMoney = getNewShopVoList(shopVos);
        newCommunicationMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_COMMUNICATION_MONEY));
        FinReportConfigVo configVoCommunication = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_COMMUNICATION.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> communicationMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoCommunication.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(communicationMoney)) {
            MergeUtil.merge(newCommunicationMoney, communicationMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newCommunicationMoney);


        //8.26	营业费用_其他
        List<BaseShopVo> newOtherMoney = getNewShopVoList(shopVos);
        newOtherMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY_OTHER));
        FinReportConfigVo configVoOther = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_BUSINESS_OTHER.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> otherMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoOther.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(otherMoney)) {
            MergeUtil.merge(newOtherMoney, otherMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newOtherMoney);

        //计算8、   营业费用
        if (CollectionUtil.isNotEmpty(resultList)) {
            MergeUtil.mergeList(realTimeProfitVos, resultList,
                    RealTimeProfitVo::getItemCode, BaseShopVo::getItemCode,
                    (realTimeProfitVo, baseShopVos) -> baseShopVos.forEach(
                            data -> {
                                data.setItemNumber(realTimeProfitVo.getItemNumber());
                                data.setItemLevel(realTimeProfitVo.getItemLevel());
                            }
                    ));
        }
        List<BaseShopVo> businessShop = resultList.stream().filter(data -> ReportDataConstant.RealProfitItemLevel.TWO.equals(data.getItemLevel()) && data.getItemNumber().startsWith("8.")).
                collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(businessShop)) {
            MergeUtil.mergeList(businessMoney, businessShop,
                    BaseShopVo::getShopId, BaseShopVo::getShopId,
                    (baseShopVo, baseShopVos) -> baseShopVos.forEach(data -> {
                        baseShopVo.setAmount(baseShopVo.getAmount().add(data.getAmount()));
                    })
            );
        }
        resultList.addAll(businessMoney);


        //9	财务费用
        List<BaseShopVo> newFinanceFee = getNewShopVoList(shopVos);
        newFinanceFee.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.FINANCE_MONEY));
        //9.01	财务费用_利息收入
        List<BaseShopVo> newFinanceInterestMoney = getNewShopVoList(shopVos);
        newFinanceInterestMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_INCOME_MONEY));

        FinReportConfigVo configVoFinanceInterestMoney = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_INTEREST_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> financeInterestMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoFinanceInterestMoney.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(financeInterestMoney)) {
            MergeUtil.merge(newFinanceInterestMoney, financeInterestMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newFinanceInterestMoney);

        //9.02	财务费用_利息支出
        List<BaseShopVo> newFinanceInterestPay = getNewShopVoList(shopVos);
        newFinanceInterestPay.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.FINANCE_INTEREST_IEXPENSE));
        FinReportConfigVo configVoFinanceInterestPay = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_INTEREST_PAY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> financeInterestPay = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoFinanceInterestPay.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(financeInterestPay)) {
            MergeUtil.merge(newFinanceInterestPay, financeInterestPay,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newFinanceInterestPay);

        //9.03	财务费用_手续费
        List<BaseShopVo> newFinancePorceduresMoney = getNewShopVoList(shopVos);
        newFinancePorceduresMoney.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.FINANCE_SERVICE_CHARGE));
        FinReportConfigVo configVoFinancePorceduresMoney = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_PROCEDURES_MONEY.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> financePorceduresMoney = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoFinancePorceduresMoney.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(financePorceduresMoney)) {
            MergeUtil.merge(newFinancePorceduresMoney, financePorceduresMoney,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newFinancePorceduresMoney);

        //9.04	财务费用_汇兑损益
        List<BaseShopVo> newFinanceExcGainsAndLosses = getNewShopVoList(shopVos);
        newFinanceExcGainsAndLosses.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.FINANCE_EXCHANGE_GAINS_AND_LOSSES));


        FinReportConfigVo configVoFinanceExcGainsAndLosses = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_FINANCE_EXCHANGE_GAINS_AND_LOSSES.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> financeExcGainsAndLosses = CollectionUtil.isEmpty(shopSubject) == true ? null : shopSubject.stream().filter(data -> configVoFinanceExcGainsAndLosses.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(financeExcGainsAndLosses)) {
            MergeUtil.merge(newFinanceExcGainsAndLosses, financeExcGainsAndLosses,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount()));
        }
        resultList.addAll(newFinanceExcGainsAndLosses);

        //计算9、 财务费用
        MergeUtil.merge(newFinanceFee, newFinanceInterestMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(newFinanceFee, newFinanceInterestPay,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(newFinanceFee, newFinancePorceduresMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        MergeUtil.merge(newFinanceFee, newFinanceExcGainsAndLosses,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));

        resultList.addAll(newFinanceFee);


        //10	所得税
        List<BaseShopVo> newIncomeTax = getNewShopVoList(shopVos);
        newIncomeTax.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.INCOME_TAX));

        FinReportConfigVo configVoIncomeTax = CollectionUtil.isEmpty(configVos) == true ? new FinReportConfigVo() : configVos.stream().filter(data -> ReportDataConstant.FinType.REALTIME_PROFIT_INCOME_TAX.
                equals(data.getFinType())).collect(Collectors.toList()).get(0);
        List<FinReportVo> incomeTax = CollectionUtil.isEmpty(voForLastYearSubject) == true ? null : voForLastYearSubject.stream().filter(data -> configVoIncomeTax.getCodes().equals(data.getAccountSubjectCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(incomeTax)) {
            MergeUtil.merge(newIncomeTax, incomeTax,
                    BaseShopVo::getShopId, FinReportVo::getShopId,
                    (value1, value2) -> value1.setAmount(value2.getDebitAmount() == null ? BigDecimal.ZERO : value2.getDebitAmount().divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).
                            divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(queryDto.getDays()))));
        }
        resultList.addAll(newIncomeTax);


        //11	 净利润  主营业务收入+其他业务收入+营业外收入-（主营业务成本+其他业务支出+营业外支出+营业税金及附加+营业费用+所得税+财务费用）
        List<BaseShopVo> newNetProfit = getNewShopVoList(shopVos);
        newNetProfit.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.NET_PROFIT));
        //合并主营业收入
        MergeUtil.merge(newNetProfit, businessVos,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        //合并其他业务收入
        MergeUtil.merge(newNetProfit, newBeerList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        //合并营业外收入
        MergeUtil.merge(newNetProfit, newOutBussinessList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        //合并主营业务成本
        MergeUtil.merge(newNetProfit, newBusinessCostList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().subtract(value2.getAmount())));
        //合并其他业务支出
        MergeUtil.merge(newNetProfit, newOtherBusinessPayList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().subtract(value2.getAmount())));
        //合并营业外支出
        MergeUtil.merge(newNetProfit, newOutsidePayList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().subtract(value2.getAmount())));
        //合并营业税金及附加
        MergeUtil.merge(newNetProfit, newTaxAndAddList,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().subtract(value2.getAmount())));
        //合并营业费用
        MergeUtil.merge(newNetProfit, businessMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().subtract(value2.getAmount())));
        //合并所得税
        MergeUtil.merge(newNetProfit, newIncomeTax,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().subtract(value2.getAmount())));
        //合并财务费用
        MergeUtil.merge(newNetProfit, newFinanceFee,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().subtract(value2.getAmount())));


        resultList.addAll(newNetProfit);

        //12	 折旧前利润
        List<BaseShopVo> newProfitBeforeOld = getNewShopVoList(shopVos);
        newProfitBeforeOld.forEach(data -> data.setItemCode(ReportDataConstant.RealProfitItemCode.PROFIT_BEFORE_DEPRECIATION));
        MergeUtil.merge(newProfitBeforeOld, newNetProfit,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        MergeUtil.merge(newProfitBeforeOld, newOldMoney,
                BaseShopVo::getShopId, BaseShopVo::getShopId,
                (value1, value2) -> value1.setAmount(value1.getAmount().add(value2.getAmount())));
        resultList.addAll(newProfitBeforeOld);
        resultList.forEach(data->data.setItemDate(queryDto.getBeginDate()));
        return resultList;
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


}

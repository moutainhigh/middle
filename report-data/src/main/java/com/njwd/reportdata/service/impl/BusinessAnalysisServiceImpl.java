package com.njwd.reportdata.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.njwd.basedata.service.BaseDeskService;
import com.njwd.basedata.service.BaseReportItemSetService;
import com.njwd.basedata.service.SysTabColumnService;
import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.dto.BaseDeskDto;
import com.njwd.entity.basedata.dto.BaseReportItemSetDto;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseDeskVo;
import com.njwd.entity.basedata.vo.BaseReportItemSetVo;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.poiexcel.TitleEntity;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.reportdata.mapper.BusinessAnalysisMapper;
import com.njwd.reportdata.mapper.SaleAnalysisMapper;
import com.njwd.reportdata.mapper.StaffAnalysisMapper;
import com.njwd.reportdata.service.*;
import com.njwd.service.FileService;
import com.njwd.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @Description: 经营分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Service
public class BusinessAnalysisServiceImpl implements BusinessAnalysisService {

    @Resource
    private BusinessAnalysisMapper businessAnalysisMapper;

    @Resource
    private BaseReportItemSetService baseReportItemSetService;

    @Resource
    private RepPosDeskService repPosDeskService;

    @Resource
    private RepPosDetailPayService repPosDetailPayService;

    @Resource
    private RepPosDetailFoodService repPosDetailFoodService;

    @Resource
    private BusinessDailyIndicService businessDailyIndicService;

    @Resource
    private BaseDeskService baseDeskService;

    @Resource
    private SaleAnalysisMapper saleAnalysisMapper;

    @Resource
    private MemberAnalysisService memberAnalysisService;

    @Resource
    private RepCrmTurnoverService repCrmTurnoverService;

    @Autowired
    private BaseShopService baseShopService;

    @Autowired
    private FileService fileService;

    @Resource
    private DeskTypeAnalysisService deskTypeAnalysisService;

    @Resource
    private StaffAnalysisMapper staffAnalysisMapper;

    @Resource
    private RealTimeProfitService realTimeProfitService;

    @Resource
    private ViewService viewService;
    /**
     * @return java.util.List<com.njwd.entity.basedata.vo.PsItemScoreVo>
     * @Description //查詢門店項目評分
     * @Author jds
     * @Date 2019/11/19 9:56
     * @Param [psItemScoreDto]
     **/
    @Override
    public List<ShopScoreVo> findPsItemScoreAvg(ShopScoreDto shopScoreDto) {
        return businessAnalysisMapper.findPsItemScoreAvg(shopScoreDto);
    }


    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.ShopScoreVo>
     * @Description //查詢門店項目評分汇总
     * @Author jds
     * @Date 2019/11/20 16:04
     * @Param [shopScoreDto]
     **/
    @Override
    public ShopScoreVo findScoreSummary(ShopScoreDto shopScoreDto) {
        ShopScoreVo returnVo = new ShopScoreVo();
        List<String> shopList = shopScoreDto.getShopIdList();
        List<String> shopTypeIdList = shopScoreDto.getShopTypeIdList();
        //查询企业全部门店评分
        shopScoreDto.setShopIdList(null);
        shopScoreDto.setShopTypeIdList(null);
        List<ShopScoreVo> listAll = findPsItemScoreAvg(shopScoreDto);
        //页面查询的门店 的评分
        List<ShopScoreVo> list = new ArrayList<>();
        if (!FastUtils.checkNullOrEmpty(listAll)) {
            //各店所有项目总分
            getItemSumScore(listAll, Constant.CountLastFlag.FALSE);
            //各项目 评分 根据门店分组
            Map<String, List<ShopScoreVo>> listMapSum = listAll.stream().collect(Collectors.groupingBy(ShopScoreVo::getShopId));
            if (!FastUtils.checkNullOrEmpty(shopList)) {
                //页面查询的门店 的评分集合
                shopList.forEach(item -> {
                    if (listMapSum.get(item) != null) {
                        list.addAll(listMapSum.get(item));
                    }
                });
                if (!FastUtils.checkNullOrEmpty(shopTypeIdList)) {
                    List<ShopScoreVo> shopScoreList = new ArrayList<>();
                    shopScoreList.addAll(list);
                    Map<String, Object> shopTypeMap = new HashMap<>();
                    for (String shopTypeId : shopTypeIdList) {
                        shopTypeMap.put(shopTypeId, shopTypeId);
                    }
                    //页面查询的门店 的评分集合
//                    for (ShopScoreVo shopScoreVo : shopScoreList) {
//                        String shopTypeId = shopScoreVo.getShopTypeId();
//                        if (shopTypeMap.get(shopTypeId) == null) {
//                            list.remove(shopScoreVo);
//                        }
//                    }
                    shopScoreList.forEach(item -> {
                        String shopTypeId = item.getShopTypeId();
                        if (shopTypeMap.get(shopTypeId) == null) {
                            list.remove(item);
                        }
                    });
                }
            } else {
                list.addAll(listAll);
            }
            //获取list返回的所有项目
            Map<String, String> typeIdMap = listAll.stream().collect(Collectors.toMap(ShopScoreVo::getTypeId, ShopScoreVo::getTypeId, (value1, value2) -> value2));
            //获取list返回的所有项目类型标识
            Map<String, String> typeMap = listAll.stream().collect(Collectors.toMap(ShopScoreVo::getTypeId, ShopScoreVo::getTypeId, (value1, value2) -> value2));
            //获取list返回的所有品牌
            Map<String, String> brandMap = listAll.stream().collect(Collectors.toMap(ShopScoreVo::getBrandId, ShopScoreVo::getBrandId, (value1, value2) -> value2));
            //获取list返回的所有区域
            Map<String, String> regionMap = listAll.stream().collect(Collectors.toMap(ShopScoreVo::getRegionId, ShopScoreVo::getRegionId, (value1, value2) -> value2));
            //定义排名map
            Map<String, Integer> rankEnte = new HashMap<>();
            Map<String, Integer> rankRegion = new HashMap<>();
            Map<String, Integer> rankBrand = new HashMap<>();
            //按项目id排序  (集团排序)
            listAll.sort(Comparator.comparing(ShopScoreVo::getTypeId));
            //集团排名
            setRank(listAll, rankEnte, typeIdMap, typeMap, Constant.Report.ENTEID);
            //按区域ID项目id排序  (区域排序)
            listAll.sort(Comparator.comparing(ShopScoreVo::getRegionId).thenComparing(ShopScoreVo::getTypeId));
            //区域排名
            setRank(listAll, rankRegion, typeIdMap, regionMap, Constant.Report.REGIONID);
            //按品牌ID项目id排序  (品牌排序)
            listAll.sort(Comparator.comparing(ShopScoreVo::getBrandId).thenComparing(ShopScoreVo::getTypeId));
            //品牌排名
            setRank(listAll, rankBrand, typeIdMap, brandMap, Constant.Report.BRANDID);
            //设置查询数据的集团排名
            list.forEach(data -> data.setRankEnte(rankEnte.get(data.getShopId() + Constant.Character.COLON + data.getTypeId())));
            //设置查询数据的区域排名
            list.forEach(data -> data.setRankRegion(rankRegion.get(data.getShopId() + Constant.Character.COLON + data.getTypeId())));
            //设置查询数据的品牌排名
            list.forEach(data -> data.setRankBrand(rankBrand.get(data.getShopId() + Constant.Character.COLON + data.getTypeId())));
            //总分 各项目分合计
            List<ShopScoreVo> sumScoreList = list.stream().filter(a -> a.getTypeId().equals(Constant.BusinessScore.SCORE_SUM_TYPE)).collect(Collectors.toList());
            returnVo.setItemSumScoreList(sumScoreList);
            //除总分外 其他
            returnVo.setItemScoreList(list);
        }
        return returnVo;
    }


    /**
     * @return java.util.List<com.njwd.entity.basedata.vo.PsItemScoreVo>
     * @Description //查询巡店評分對比
     * @Author jds
     * @Date 2019/11/19 14:55
     * @Param [psItemScoreDto]
     **/
    @Override
    public ShopScoreVo findScoreContrast(ShopScoreDto shopScoreDto) {
        ShopScoreVo returnVo = new ShopScoreVo();
        //查詢本期評分
        List<ShopScoreVo> listCurrentMoney = findPsItemScoreAvg(shopScoreDto);
        if (!FastUtils.checkNullOrEmpty(listCurrentMoney)) {
            //设置上期日期
            ShopScoreDto scorePriorShopDto = new ShopScoreDto();
            scorePriorShopDto.setShopIdList(shopScoreDto.getShopIdList());
            scorePriorShopDto.setShopTypeIdList(shopScoreDto.getShopTypeIdList());
            scorePriorShopDto.setEnteId(shopScoreDto.getEnteId());
            scorePriorShopDto.setDateType(shopScoreDto.getDateType());
            List<Date> dateList = DateUtils.getLastPeriodDate(shopScoreDto.getBeginDate(), shopScoreDto.getEndDate(), shopScoreDto.getDateType());
            //查詢上期評分
            List<ShopScoreVo> listPrior = null;
            if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
                scorePriorShopDto.setBeginDate(dateList.get(Constant.Number.ZERO));
                scorePriorShopDto.setEndDate(dateList.get(Constant.Number.ONE));
                //查詢上期評分
                listPrior = findPsItemScoreAvg(scorePriorShopDto);
            }
            //设置去年同期日期
            shopScoreDto.setBeginDate(DateUtil.offset(shopScoreDto.getBeginDate(), DateField.YEAR, -Constant.Number.ONE));
            shopScoreDto.setEndDate(DateUtil.offset(shopScoreDto.getEndDate(), DateField.YEAR, -Constant.Number.ONE));
            //各项目 评分 根据门店分组
            Map<String, List<ShopScoreVo>> currentMap = listCurrentMoney.stream().collect(Collectors.groupingBy(ShopScoreVo::getShopId));
            //查詢去年同期評分
            List<ShopScoreVo> listLastYear = findPsItemScoreAvg(shopScoreDto);
            if (!FastUtils.checkNullOrEmpty(listPrior)) {
                Map<String, List<ShopScoreVo>> lastPeriodMap = listPrior.stream().collect(Collectors.groupingBy(ShopScoreVo::getShopId));
                //上期 门店 对应项目评分 对应总分
                currentMap.forEach((key, value) -> {
                    //上期数据
                    List<ShopScoreVo> lastList = lastPeriodMap.get(key);
                    if (!FastUtils.checkNullOrEmpty(lastList)) {
                        //上期各项目评分 拼接到 本期数据对应 上期评分字段里
                        MergeUtil.merge(value, lastList,
                                (vev, ev) -> vev.getTypeId().equals(ev.getTypeId()),
                                (vev, ev) -> {
                                    vev.setScorePrior(ev.getScore());
                                });
                    }
                });
            }
            if (!FastUtils.checkNullOrEmpty(listLastYear)) {
                Map<String, List<ShopScoreVo>> lastYearMap = listLastYear.stream().collect(Collectors.groupingBy(ShopScoreVo::getShopId));
                //去年同期 各店与本期相同项目的总分
                currentMap.forEach((key, value) -> {
                    //去年同期数据
                    List<ShopScoreVo> lastList = lastYearMap.get(key);
                    if (!FastUtils.checkNullOrEmpty(lastList)) {
                        //去年同期各项目评分 拼接到 本期数据对应 上期评分字段里
                        MergeUtil.merge(value, lastList,
                                (vev, ev) -> vev.getTypeId().equals(ev.getTypeId()),
                                (vev, ev) -> {
                                    vev.setScoreLastYear(ev.getScore());
                                });
                    }
                });
            }
            //本期 各店所有项目总分
            getItemSumScore(listCurrentMoney, Constant.CountLastFlag.TRUE);
            //重新排序
            listCurrentMoney.sort(Comparator.comparing(ShopScoreVo::getShopId).thenComparing(ShopScoreVo::getTypeId));
            //总分 各项目分合计
            List<ShopScoreVo> sumScoreList = listCurrentMoney.stream().filter(a -> a.getTypeId().equals(Constant.BusinessScore.SCORE_SUM_TYPE)).collect(Collectors.toList());
            //各项目  同比 环比
            getPeriodAndLastYearCompareInfo(sumScoreList);
            //总分  同比 环比
            getPeriodAndLastYearCompareInfo(listCurrentMoney);
            returnVo.setItemSumScoreList(sumScoreList);
            //除总分外 其他
            returnVo.setItemScoreList(listCurrentMoney);
        }
        return returnVo;
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/1/19 17:55
     * @Param [scoreList]
     * @Description
     */
    private void getPeriodAndLastYearCompareInfo(List<ShopScoreVo> scoreList) {
        BigDecimal scoreCurrent;
        BigDecimal scorePeriod;
        BigDecimal scoreLastYear;
        for (ShopScoreVo shopScoreVo : scoreList) {
            scoreCurrent = shopScoreVo.getScore();
            scorePeriod = shopScoreVo.getScorePrior();
            scoreLastYear = shopScoreVo.getScoreLastYear();
            if (scorePeriod != null && scorePeriod.compareTo(BigDecimal.ZERO) != 0) {
                shopScoreVo.setLinkRatio(scoreCurrent.subtract(scorePeriod)
                        .divide(scorePeriod, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                        .multiply(Constant.Number.HUNDRED)
                        .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
            }
            if (scoreLastYear != null && scoreLastYear.compareTo(BigDecimal.ZERO) != 0) {
                shopScoreVo.setOverYear(scoreCurrent.subtract(scoreLastYear)
                        .divide(scoreLastYear, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                        .multiply(Constant.Number.HUNDRED)
                        .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
            }
        }
    }

    /**
     * @Description: 经营日报分析
     * @Param: [businessReportDayDto]
     * @return: com.njwd.entity.reportdata.vo.BusinessReportDayVo
     * @Author: LuoY
     * @Date: 2019/12/29 11:43
     */
    @Override
    public List<BusinessReportDayItemVo> findBusinessReportDay(BusinessReportDayDto businessReportDayDto, Integer reportItem) {
        List<BusinessReportDayItemVo> businessReportDayItemVos = new LinkedList<>();
        //查询预置项目项
        BaseReportItemSetDto baseReportItemSetDto = new BaseReportItemSetDto();
        baseReportItemSetDto.setEnteId(businessReportDayDto.getEnteId());
        baseReportItemSetDto.setReportId(reportItem);
        List<BaseReportItemSetVo> baseReportItemSetVos = baseReportItemSetService.findBaseReportItemSetVoByReportId(baseReportItemSetDto);
        //初始化报表左侧项目数
        initializationItem(businessReportDayItemVos, baseReportItemSetVos);
        //查询 类型 本期 同比 环比
        Integer dataType = businessReportDayDto.getDataType();
        if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)) {
            //根据日期类型 选择 对应同比时间
            List<Date> dateList = DateUtils.getLastYearDate(businessReportDayDto.getBeginDate(), businessReportDayDto.getEndDate(), businessReportDayDto.getDateType());
            if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
                businessReportDayDto.setBeginDate(dateList.get(Constant.Number.ZERO));
                businessReportDayDto.setEndDate(dateList.get(Constant.Number.ONE));
            }
        } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType)) {
            //环比时间
            List<Date> dates = DateUtils.getLastPeriodDate(businessReportDayDto.getBeginDate(), businessReportDayDto.getEndDate(), businessReportDayDto.getDateType());
            businessReportDayDto.setBeginDate(dates.get(Constant.Number.ZERO));
            businessReportDayDto.setEndDate(dates.get(Constant.Number.ONE));
        }
        //计算本期
        calculationBusinessAnalysisCurrentMoney(businessReportDayItemVos, businessReportDayDto,
                ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY);
        //同比环比不计算排名，直接返回
        if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)
                && ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType) ) {
           return businessReportDayItemVos;
        }
        if (ReportDataConstant.ReportItemReportId.BUSINESSREPORTDAY.equals(reportItem)) {
            //计算指标
            calculationBusinessAnalysisIndex(businessReportDayItemVos, businessReportDayDto);
            //计算指标完成率
            calculationIndexComplement(businessReportDayItemVos);
            //门店数>=2，不计算指标排名
            if(businessReportDayDto.getShopIdList().size() == Constant.Number.ONE){
                //计算指标排名
//                calculationIndexRank(businessReportDayItemVos);
            }
        }
        //计算同比
        //calculationYearCompare(businessReportDayItemVos, businessReportDayDto);
        //计算环比
        //calculationMonthCompare(businessReportDayItemVos, businessReportDayDto);
        //排序展现
        businessReportDayItemVos.sort(Comparator.comparing(BusinessReportDayItemVo::getId));
        return businessReportDayItemVos;
    }

    /**
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.StatisticsTurnoverRateVo>>
     * @Author ZhuHC
     * @Date 2019/12/30 16:47
     * @Param [baseQueryDto]
     * @Description 翻台率统计表
     */
    @Override
    public List<StatisticsTurnoverRateVo> findStatisticsTurnoverRate(BaseQueryDto baseQueryDto) {
        //门店桌数
        List<StatisticsTurnoverRateVo> deskNumList = repPosDeskService.findDeskNumByShop(baseQueryDto);
        //门店开台数
        List<StatisticsTurnoverRateVo> stationsNumList = repPosDeskService.findStationsNumByShop(baseQueryDto);
        if (!FastUtils.checkNullOrEmpty(deskNumList)) {
            MergeUtil.merge(deskNumList, stationsNumList,
                    StatisticsTurnoverRateVo::getShopId, StatisticsTurnoverRateVo::getShopId,
                    (deskNum, stationsNum) -> {
                        deskNum.setStationsNum(stationsNum.getStationsNum());
                    }
            );
            //门店数据-主要取 门店开业时
            BaseShopDto baseShopDto = new BaseShopDto();
            baseShopDto.setEnteId(baseQueryDto.getEnteId());
            baseShopDto.setShopIdList(baseQueryDto.getShopIdList());
            baseShopDto.setShopTypeIdList(baseQueryDto.getShopTypeIdList());
            List<BaseShopVo> baseShopVoList = baseShopService.findShopDate(baseShopDto);
            //查询开始时间
            Date beginDate = baseQueryDto.getBeginDate();
            //查询结束时间
            Date endDate = baseQueryDto.getEndDate();
            //营业日期- 门店开业日期在查询日期之前的，为查询结束时间减去开始时间
            BigDecimal businessDays = BigDecimal.valueOf(DateUtils.getBetweenDay(beginDate, endDate) + 1);
            //先给与门店 统计时间 默认值
            for (StatisticsTurnoverRateVo vo : deskNumList) {
                vo.setBusinessDays(businessDays);
                vo.setType(ReportDataConstant.Finance.TYPE_SHOP);
            }
            if (!FastUtils.checkNullOrEmpty(baseShopVoList)) {
                MergeUtil.merge(deskNumList, baseShopVoList,
                        StatisticsTurnoverRateVo::getShopId, BaseShopVo::getShopId,
                        (deskNum, baseShopVo) -> {
                            Date openingDate = baseShopVo.getOpeningDate();
                            Date shutdownDate = baseShopVo.getShutdownDate();
                            //开业时间录入时，营业日期- 查询结束时间减去开始时间
                            if (null != openingDate) {
                                //开业时间在查询时间之间时，营业天数=结束时间减去开业时间
                                if (openingDate.getTime() >= beginDate.getTime() && openingDate.getTime() <= endDate.getTime()) {
                                    //开业关停时间都在查询区间内时  营业天数 = 关停时间-开业时间
                                    if (null != shutdownDate && shutdownDate.getTime() <= endDate.getTime()) {
                                        deskNum.setBusinessDays(BigDecimal.valueOf(DateUtils.getBetweenDay(openingDate, shutdownDate) + 1));
                                    } else {
                                        deskNum.setBusinessDays(BigDecimal.valueOf(DateUtils.getBetweenDay(openingDate, endDate) + 1));
                                    }
                                }
                                if (openingDate.getTime() < beginDate.getTime() && null != shutdownDate && shutdownDate.getTime() >= beginDate.getTime() && shutdownDate.getTime() <= endDate.getTime()) {
                                    deskNum.setBusinessDays(BigDecimal.valueOf(DateUtils.getBetweenDay(beginDate, shutdownDate) + 1));
                                }
                            }
                        }
                );
            }
            // 品牌 区域 合计
            Map<String, List<StatisticsTurnoverRateVo>> regionMap = deskNumList.stream().collect(Collectors.groupingBy(StatisticsTurnoverRateVo::getRegionId));
            Map<String, List<StatisticsTurnoverRateVo>> brandMap = deskNumList.stream().collect(Collectors.groupingBy(StatisticsTurnoverRateVo::getBrandId));
            Map<String, List<StatisticsTurnoverRateVo>> enteMap = deskNumList.stream().collect(Collectors.groupingBy(StatisticsTurnoverRateVo::getEnteId));
            // 根据品牌区域合并计算
            getSumDeskNum(ReportDataConstant.Finance.TYPE_REGION, regionMap, deskNumList);
            getSumDeskNum(ReportDataConstant.Finance.TYPE_BRAND, brandMap, deskNumList);
            getSumDeskNum(ReportDataConstant.Finance.TYPE_ALL, enteMap, deskNumList);
            Integer deskNum;
            Integer stationsNum;
            BigDecimal businessDay;
            //计算 翻台率 翻台率计算方式应为：开台数/桌数/查询天数（需要减去未开业时间）
            for (StatisticsTurnoverRateVo vo : deskNumList) {
                if (null != vo.getDeskNum() && !Constant.Number.ZERO.equals(vo.getDeskNum())) {
                    deskNum = vo.getDeskNum();
                    stationsNum = vo.getStationsNum();
                    businessDay = vo.getBusinessDays();
                    if (StringUtil.isBlank(stationsNum)) {
                        stationsNum = Constant.Number.ZERO;
                    }
                    vo.setStatisticsTurnoverPercentage(BigDecimal.valueOf(stationsNum)
                            .divide(BigDecimal.valueOf(deskNum), Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                            .divide(businessDay, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                            .multiply(Constant.Number.HUNDRED)
                            .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
                }
            }
            return deskNumList;
        }
        return null;
    }
    /*
     * 计算会员数量统计表合计项
     * */
    private void getSumDeskNum(String type, Map<String, List<StatisticsTurnoverRateVo>> map,
                               List<StatisticsTurnoverRateVo> statisticsTurnoverRateVoList) {
        StatisticsTurnoverRateVo statisticsTurnoverRateVo;
        for (Map.Entry<String, List<StatisticsTurnoverRateVo>> entry : map.entrySet()) {
            statisticsTurnoverRateVo = entry.getValue().stream().reduce(new StatisticsTurnoverRateVo(), (te, e) -> {
                te.setDeskNum(te.getDeskNum() + (e.getDeskNum()));
                te.setStationsNum(te.getStationsNum() + (e.getStationsNum()));
                te.setBusinessDays(te.getBusinessDays().add(e.getBusinessDays()));
                return te;
            });
            statisticsTurnoverRateVo.setBrandId(entry.getValue().get(Constant.Number.ZERO).getEnteId());
            statisticsTurnoverRateVo.setBrandId(entry.getValue().get(Constant.Number.ZERO).getBrandId());
            statisticsTurnoverRateVo.setRegionId(entry.getValue().get(Constant.Number.ZERO).getRegionId());
            statisticsTurnoverRateVo.setBrandName(entry.getValue().get(Constant.Number.ZERO).getBrandName());
            statisticsTurnoverRateVo.setRegionName(entry.getValue().get(Constant.Number.ZERO).getRegionName());
            statisticsTurnoverRateVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            //type不为区域时，对象区域名称为 全部区域
            if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                statisticsTurnoverRateVo.setType(ReportDataConstant.Finance.TYPE_REGION);
            } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(type)) {
                statisticsTurnoverRateVo.setType(ReportDataConstant.Finance.TYPE_BRAND);
                statisticsTurnoverRateVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
            } else if (ReportDataConstant.Finance.TYPE_ALL.equals(type)) {
                statisticsTurnoverRateVo.setType(ReportDataConstant.Finance.TYPE_ALL);
                statisticsTurnoverRateVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
                statisticsTurnoverRateVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                statisticsTurnoverRateVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            statisticsTurnoverRateVoList.add(statisticsTurnoverRateVo);
        }
    }
    /**
     * @Description: 初始化经营日报表左侧项目树
     * @Param: [businessReportDayItemVos, baseReportItemSetVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/29 14:21
     */
    private void initializationItem(@NotNull List<BusinessReportDayItemVo> businessReportDayItemVos, @NotNull List<BaseReportItemSetVo> baseReportItemSetVos) {
        if (!FastUtils.checkNullOrEmpty(baseReportItemSetVos)) {
            //循环项目表
            List<BusinessReportDayItemVo> businessAnalysis = new LinkedList<>();
            List<BusinessReportDayItemVo> profitAnalysis = new LinkedList<>();
            List<BusinessReportDayItemVo> personAnalysis = new LinkedList<>();
            List<BusinessReportDayItemVo> memberAnalysis = new LinkedList<>();

            businessAnalysis.add(setInfo(ReportDataConstant.BusinessReportItemParam.BUSINESSANALYSIS, null, null, Constant.Number.ZERO, null, null, Constant.Number.ONE, Constant.DataType.NONE));
//            profitAnalysis.add(setInfo(ReportDataConstant.BusinessReportItemParam.PROFITANALYSIS, null, null, Constant.Number.ZERO, null, null, Constant.Number.TWENTYFOUR));
            personAnalysis.add(setInfo(ReportDataConstant.BusinessReportItemParam.PERSONANALYSIS, null, null, Constant.Number.ZERO, null, null, Constant.Number.TWENTYSIX, Constant.DataType.NONE));
            memberAnalysis.add(setInfo(ReportDataConstant.BusinessReportItemParam.MEMBERANALYSIS, null, null, Constant.Number.ZERO, null, null, Constant.Number.THIRTYFOUR, Constant.DataType.NONE));
            Integer id = Constant.Number.TWO;
            //过滤掉点菜结构
            List<BaseReportItemSetVo> baseReportItemSetVos1 = baseReportItemSetVos.stream().
                    filter(data -> !data.getItemType().equals(ReportDataConstant.BusinessReportDayItemType.ORDERANALYSIS)).
                    collect(Collectors.toList());
            //按itemNumber排序
            baseReportItemSetVos1.sort(Comparator.comparing(BaseReportItemSetVo::getItemType).
                    thenComparing(BaseReportItemSetVo::getSortNum));
            //初始化左侧项目树
            for (BaseReportItemSetVo data : baseReportItemSetVos1) {
                if (id.equals(Constant.Number.TWENTYSIX) || id.equals(Constant.Number.THIRTYFOUR)) {
                    id++;
                }
                BusinessReportDayItemVo businessReportDayItemVo = setInfo(data.getItemNumber().toString(), data.getItemCode(),
                        data.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.LUNCH) ?
                                ReportDataConstant.BusinessReportDayItemType.AMONG + data.getItemName() : data.getItemName(),
                        data.getItemLevel(), data.getItemType(), data.getItemTypeName(), id, data.getDataType());
                //营业分析
                if (ReportDataConstant.BusinessReportDayItemType.BUSINESSANALYSIS.equals(data.getItemType())) {
                    businessAnalysis.add(businessReportDayItemVo);
                }

                //员工分析
                if (ReportDataConstant.BusinessReportDayItemType.PEOPLEANALYSIS.equals(data.getItemType())) {
                    personAnalysis.add(businessReportDayItemVo);
                }

                //会员分析
                if (ReportDataConstant.BusinessReportDayItemType.MEMBERANALYSIS.equals(data.getItemType())) {
                    memberAnalysis.add(businessReportDayItemVo);
                }
                id++;
            }
            businessReportDayItemVos.addAll(businessAnalysis);
            businessReportDayItemVos.addAll(profitAnalysis);
            businessReportDayItemVos.addAll(personAnalysis);
            businessReportDayItemVos.addAll(memberAnalysis);
        }
    }

    /**
     * @Description: 项目设置
     * @Param: [itemNumber, itemCode, itemName, itemLevel, itemType, itemTypeNa]
     * @return: com.njwd.entity.reportdata.vo.BusinessReportDayItemVo
     * @Author: LuoY
     * @Date: 2019/12/31 13:36
     */
    private BusinessReportDayItemVo setInfo(String itemNumber, String itemCode, String itemName, Integer itemLevel, Integer itemType, String itemTypeName, int id, Integer dataType) {
        BusinessReportDayItemVo businessReportDayItemVo = new BusinessReportDayItemVo();
        businessReportDayItemVo.setId(id);
        businessReportDayItemVo.setItemNumber(itemNumber);
        businessReportDayItemVo.setItemType(itemType);
        businessReportDayItemVo.setItemTypeName(itemTypeName);
        businessReportDayItemVo.setItemCode(itemCode);
        businessReportDayItemVo.setItemName(itemName);
        businessReportDayItemVo.setItemLevel(itemLevel);
        businessReportDayItemVo.setDataType(dataType);
        return businessReportDayItemVo;
    }

    /**
     * @Description:计算金额
     * @Param: [businessReportDayItemVos, businessReportDayDto]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/29 14:47
     */
    private void calculationBusinessAnalysisCurrentMoney(@NotNull List<BusinessReportDayItemVo> businessReportDayItemVos,
                                                         BusinessReportDayDto businessReportDayDto, Integer dataType) {
        //查询各餐别的汇总金额
        ReportPosDeskDto reportPosDeskDto = new ReportPosDeskDto();
        reportPosDeskDto.setBeginDate(businessReportDayDto.getBeginDate());
        reportPosDeskDto.setEndDate(businessReportDayDto.getEndDate());
        reportPosDeskDto.setEnteId(businessReportDayDto.getEnteId());
        reportPosDeskDto.setShopIdList(businessReportDayDto.getShopIdList());
        reportPosDeskDto.setShopTypeIdList(businessReportDayDto.getShopTypeIdList());
        List<ReportPosDeskVo> reportPosDeskVos = businessAnalysisMapper.findReportPostDeskMealAmountByCondition(reportPosDeskDto);
        List<ReportPosDeskVo> reportPosDeskVos1 = businessAnalysisMapper.findReportPostDeskChannelAmountByCondition(reportPosDeskDto);
        //查询支付方式明细表
        RepPosDetailPayDto repPosDetailPayDto = new RepPosDetailPayDto();
        repPosDetailPayDto.setShopIdList(businessReportDayDto.getShopIdList());
        repPosDetailPayDto.setBeginDate(businessReportDayDto.getBeginDate());
        repPosDetailPayDto.setEndDate(businessReportDayDto.getEndDate());
        repPosDetailPayDto.setEnteId(businessReportDayDto.getEnteId());
        repPosDetailPayDto.setShopTypeIdList(businessReportDayDto.getShopTypeIdList());
        //查询销售实收应收
        List<RepPosDetailPayVo> repPosDetailPayVos = repPosDetailPayService.findRepPosDetailPayVoInfoByCondition(repPosDetailPayDto);
        //查询销售菜品明细表
        RepPosDetailFoodDto repPosDetailFoodDto = new RepPosDetailFoodDto();
        repPosDetailFoodDto.setShopIdList(businessReportDayDto.getShopIdList());
        repPosDetailFoodDto.setBeginDate(businessReportDayDto.getBeginDate());
        repPosDetailFoodDto.setEndDate(businessReportDayDto.getEndDate());
        repPosDetailFoodDto.setEnteId(businessReportDayDto.getEnteId());
        repPosDetailFoodDto.setShopTypeIdList(businessReportDayDto.getShopTypeIdList());
        List<RepPosDetailFoodVo> repPosDetailFoodVos = repPosDetailFoodService.findRepPosDetailFoodByCondition(repPosDetailFoodDto);
        //菜品收入
        RepPosDetailFoodVo repPosDetailFoodVo = repPosDetailFoodService.findFoodAllPrice(repPosDetailFoodDto);
        //查询赠送金额
        List<PosDiscountDetailPayVo> giveFoodPayVos = saleAnalysisMapper.findDetailPayListTwo(repPosDetailPayDto);
        //查询桌数
        BaseDeskDto baseDeskDto = new BaseDeskDto();
        baseDeskDto.setShopIdList(businessReportDayDto.getShopIdList());
        baseDeskDto.setShopTypeIdList(businessReportDayDto.getShopTypeIdList());
        BaseDeskVo baseDeskVos = baseDeskService.findDeskCountByShopId(baseDeskDto);
        //查询会员新增人数
        MemberNumAnalysisDto memberNumAnalysisDto = new MemberNumAnalysisDto();
        memberNumAnalysisDto.setBeginDate(businessReportDayDto.getBeginDate());
        memberNumAnalysisDto.setEndDate(businessReportDayDto.getEndDate());
        memberNumAnalysisDto.setEnteId(businessReportDayDto.getEnteId());
        memberNumAnalysisDto.setShopIdList(businessReportDayDto.getShopIdList());
        memberNumAnalysisDto.setShopTypeIdList(businessReportDayDto.getShopTypeIdList());
        List<MemberNumAnalysisVo> memberNumAnalysisVos = memberAnalysisService.findAddedMemberNum(memberNumAnalysisDto);
        //List<MemberNumAnalysisVo> memberNumAnalysisVos = null;
        //查询会员卡储值消费信息
        RepCrmTurnoverDto repCrmTurnoverDto = new RepCrmTurnoverDto();
        repCrmTurnoverDto.setBeginDate(businessReportDayDto.getBeginDate());
        repCrmTurnoverDto.setEndDate(businessReportDayDto.getEndDate());
        repCrmTurnoverDto.setDateType(businessReportDayDto.getDateType());
        repCrmTurnoverDto.setEnteId(businessReportDayDto.getEnteId());
        repCrmTurnoverDto.setShopIdList(businessReportDayDto.getShopIdList());
        repCrmTurnoverDto.setShopTypeIdList(businessReportDayDto.getShopTypeIdList());
        RepCrmTurnoverVo repCrmTurnoverVo = repCrmTurnoverService.findMemberMoneyInfo(repCrmTurnoverDto);
        ///实时利润数据结果集处理
        RealTimeProfitDto realTimeProfitDto = new RealTimeProfitDto();
        realTimeProfitDto.setEnteId(businessReportDayDto.getEnteId());
        realTimeProfitDto.setShopIdList(businessReportDayDto.getShopIdList());
        realTimeProfitDto.setBeginDate(businessReportDayDto.getBeginDate());
        realTimeProfitDto.setEndDate(businessReportDayDto.getEndDate());
        realTimeProfitDto.setDataType(businessReportDayDto.getDataType());
        realTimeProfitDto.setDateType(businessReportDayDto.getDateType());
        List<RealTimeProfitVo> realDataList = realTimeProfitService.findRealTimeProfitAnalysis(realTimeProfitDto);
        //计算营业分析
        calculationBusinessAnalysis(businessReportDayItemVos, reportPosDeskVos, reportPosDeskVos1, repPosDetailFoodVo,
                baseDeskVos, repPosDetailPayVos, dataType, giveFoodPayVos, memberNumAnalysisVos, repCrmTurnoverVo,realDataList);
        //点菜结构
        orderFoodAnalysis(businessReportDayItemVos, repPosDetailFoodVos, dataType);
        //员工分析-员工人数，试用期人数，当期入职人数，离职人数
        getStaffAnalysis(businessReportDayItemVos, businessReportDayDto, reportPosDeskVos,realDataList);

    }

    /**
     * @Author ZhuHC
     * @Date  2020/4/26 15:34
     * @Param [businessReportDayItemVos, businessReportDayDto, reportPosDeskVos]
     * @return void
     * @Description 员工分析
     */
    private void getStaffAnalysis(@NotNull List <BusinessReportDayItemVo> businessReportDayItemVos,
                                  BusinessReportDayDto businessReportDayDto,
                                  List <ReportPosDeskVo> reportPosDeskVos,
                                  List<RealTimeProfitVo> realDataList) {
        StaffAnalysisDto staffAnalysisDto = new StaffAnalysisDto();
        staffAnalysisDto.setBeginDate(businessReportDayDto.getBeginDate());
        staffAnalysisDto.setEndDate(businessReportDayDto.getEndDate());
        staffAnalysisDto.setEnteId(businessReportDayDto.getEnteId());
        staffAnalysisDto.setShopIdList(businessReportDayDto.getShopIdList());
        staffAnalysisDto.setShopTypeIdList(businessReportDayDto.getShopTypeIdList());
        StaffTypeInfoVo staffTypeInfoVo = staffAnalysisMapper.findStaffNumList(staffAnalysisDto);
        if(null != staffTypeInfoVo){
            //员工人数
            BigDecimal hireNum = BigDecimal.valueOf(staffTypeInfoVo.getHireNum());
            //离职人数
            BigDecimal leaveNum = BigDecimal.valueOf(staffTypeInfoVo.getLeaveNum());
            //离职率 离职人数/(离职人数+期末数）
            BigDecimal leavePer = BigDecimalUtils.getPercent(leaveNum,leaveNum.add(hireNum));
            //营业外收入
            BigDecimal nonOperatingIncome = reportPosDeskVos.stream().map(data -> data.getMoneyOverChargeSum() == null ? BigDecimal.ZERO : data.getMoneyOverChargeSum()).
                    reduce(BigDecimal.ZERO, BigDecimal::add);
            //收入
            BigDecimal incomeMoney = reportPosDeskVos.size() == Constant.Number.ZERO ? null :
                    reportPosDeskVos.stream().map(ReportPosDeskVo::getAmountSum).
                            reduce(BigDecimal.ZERO, BigDecimal::add).add(nonOperatingIncome);
            //人均创收 收入/员工人数
            BigDecimal avgStaffIncome = BigDecimalUtils.divideMethod(incomeMoney,hireNum,Constant.Number.TWO);
            //TODO 人均创利 净利润/员工人数
            BigDecimal jlr = BigDecimal.ZERO;
            if (null != realDataList && realDataList.size() > 0) {
                for (RealTimeProfitVo rv : realDataList) {
                    //净利润
                    if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(rv.getItemCode())
                            && rv.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
                        jlr =rv.getCurrentMoney();
                    }
                    continue;
                }
            }
            BigDecimal rjcl = BigDecimalUtils.divideMethod(jlr,hireNum,Constant.Number.TWO);
            //员工数据添加进数据树中
            businessReportDayItemVos.forEach(report -> {
                //员工人数
                if (ReportDataConstant.BusinessReportDayItemCode.EMPLOYEESNUM.equals(report.getItemCode())) {
                    report.setCurrentMoney(hireNum);
                    report.setDataType(Constant.DataType.NUMBER);
                }
                //试用期人数
                if (ReportDataConstant.BusinessReportDayItemCode.PROBATIONNUM.equals(report.getItemCode())) {
                    report.setCurrentMoney(BigDecimal.valueOf(staffTypeInfoVo.getPositiveNum()));
                    report.setDataType(Constant.DataType.NUMBER);
                }
                //当期入职人数
                if (ReportDataConstant.BusinessReportDayItemCode.ENTRYPERSON.equals(report.getItemCode())) {
                    report.setCurrentMoney(BigDecimal.valueOf(staffTypeInfoVo.getCurrentNum()));
                    report.setDataType(Constant.DataType.NUMBER);
                }
                //离职人数
                if (ReportDataConstant.BusinessReportDayItemCode.QUITPERSON.equals(report.getItemCode())) {
                    report.setCurrentMoney(leaveNum);
                    report.setDataType(Constant.DataType.NUMBER);
                }
                //离职率
                if (ReportDataConstant.BusinessReportDayItemCode.QUITRATE.equals(report.getItemCode())) {
                    report.setCurrentMoney(leavePer);
                    report.setDataType(Constant.DataType.PERCENT);
                }
                //人均创收
                if (ReportDataConstant.BusinessReportDayItemCode.PERSONINCOME.equals(report.getItemCode())) {
                    report.setCurrentMoney(avgStaffIncome);
                    report.setDataType(Constant.DataType.MONEY);
                }
                //人均创利
                if (ReportDataConstant.BusinessReportDayItemCode.PERSONPROFIT.equals(report.getItemCode())) {
                    report.setCurrentMoney(rjcl);
                    report.setDataType(Constant.DataType.MONEY);
                }
            });
        }
    }

    /**
     * @Description: 营业分析
     * @Param: [businessReportDayItemVos, reportPosDeskVos, repPosDetailPayVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/1/2 16:13
     */
    private void calculationBusinessAnalysis(List<BusinessReportDayItemVo> businessReportDayItemVos,
                                             List<ReportPosDeskVo> reportPosDeskMealVos,
                                             List<ReportPosDeskVo> reportPosDeskChannelVos,
                                             RepPosDetailFoodVo repPosDetailFoodVo,
                                             BaseDeskVo baseDeskVo,
                                             List<RepPosDetailPayVo> repPosDetailPayVos,
                                             Integer dataType,
                                             List<PosDiscountDetailPayVo> posDiscountDetailPayVos,
                                             List<MemberNumAnalysisVo> memberNumAnalysisVos,
                                             RepCrmTurnoverVo repCrmTurnoverVo,
                                             List<RealTimeProfitVo> realDataList) {
        //赋值
        if (!FastUtils.checkNullOrEmpty(reportPosDeskMealVos, reportPosDeskChannelVos)) {
            businessReportDayItemVos.forEach(report -> {
                if (ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY.equals(dataType)) {
                    //本期
                    calculationCurrentMoney(reportPosDeskMealVos, reportPosDeskChannelVos, repPosDetailPayVos,
                            repPosDetailFoodVo, baseDeskVo, report, posDiscountDetailPayVos, memberNumAnalysisVos,
                            repCrmTurnoverVo,realDataList);
                }
//                else if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)) {
//                    //同比
//                    calculationYearCompare(reportPosDeskMealVos, reportPosDeskChannelVos, repPosDetailPayVos,
//                            repPosDetailFoodVo, baseDeskVo, report, posDiscountDetailPayVos, memberNumAnalysisVos, repCrmTurnoverVo);
//                } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType)) {
//                    //环比
//                    calculationMonthCompare(reportPosDeskMealVos, reportPosDeskChannelVos, repPosDetailPayVos,
//                            repPosDetailFoodVo, baseDeskVo, report, posDiscountDetailPayVos, memberNumAnalysisVos, repCrmTurnoverVo);
//                }
            });
        }
    }


    /**
     * @Description: 计算本期
     * @Param: [reportPosDeskVos, repPosDetailPayVos, report, dataType]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/1/7 11:41
     */
    private void calculationCurrentMoney(List<ReportPosDeskVo> reportPosDeskMealVos, List<ReportPosDeskVo> reportPosDeskChannelVos,
                                         List<RepPosDetailPayVo> repPosDetailPayVos, RepPosDetailFoodVo repPosDetailFoodVo,
                                         BaseDeskVo baseDeskVo, BusinessReportDayItemVo report,
                                         List<PosDiscountDetailPayVo> posDiscountDetailPayVos,
                                         List<MemberNumAnalysisVo> memberNumAnalysisVos,
                                         RepCrmTurnoverVo repCrmTurnoverVo,
                                         List<RealTimeProfitVo> realDataList) {
        //营业分析
        //营业外收入
        BigDecimal nonOperatingIncome = reportPosDeskMealVos.stream().map(data -> data.getMoneyOverChargeSum() == null ? BigDecimal.ZERO : data.getMoneyOverChargeSum()).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //赠送金额
        BigDecimal giveMoney = posDiscountDetailPayVos.stream().map(a -> a.getTotalMoney() == null ? BigDecimal.ZERO : a.getTotalMoney()).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //收入
        BigDecimal incomeMoney = reportPosDeskMealVos.size() == Constant.Number.ZERO ? Constant.Number.ZEROB :
                reportPosDeskMealVos.stream().map(ReportPosDeskVo::getAmountSum).
                        reduce(BigDecimal.ZERO, BigDecimal::add).add(nonOperatingIncome);
        //折扣额
        BigDecimal discountMoney = repPosDetailPayVos.size() == Constant.Number.ZERO ? Constant.Number.ZEROB.add(giveMoney) :
                repPosDetailPayVos.stream().map(RepPosDetailPayVo::getMoneySum).
                        reduce(BigDecimal.ZERO, BigDecimal::add).
                        subtract(repPosDetailPayVos.stream().map(RepPosDetailPayVo::getMoneyActualSum).
                                reduce(BigDecimal.ZERO, BigDecimal::add)).add(giveMoney);

        //收入(包含折扣)
        if (ReportDataConstant.BusinessReportDayItemCode.INCOMEDISCOUNT.equals(report.getItemCode())) {
            report.setCurrentMoney(incomeMoney);
        }

        //中餐
        if (ReportDataConstant.BusinessReportDayItemCode.LUNCH.equals(report.getItemCode())) {
            report.setCurrentMoney(reportPosDeskMealVos.size() == Constant.Number.ZERO ? null :
                    reportPosDeskMealVos.stream().filter(data -> ReportDataConstant.posMeal.LUNCH.equals(data.getMealId())).
                            map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        //晚餐
        if (ReportDataConstant.BusinessReportDayItemCode.DINNER.equals(report.getItemCode())) {
            report.setCurrentMoney(
                    reportPosDeskMealVos.size() == Constant.Number.ZERO ? null :
                            reportPosDeskMealVos.stream().filter(data -> ReportDataConstant.posMeal.DINNER.equals(data.getMealId())).
                                    map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        //夜宵
        if (ReportDataConstant.BusinessReportDayItemCode.MIDDLENIGHT.equals(report.getItemCode())) {
            report.setCurrentMoney(
                    reportPosDeskMealVos.size() == Constant.Number.ZERO ? null :
                            reportPosDeskMealVos.stream().filter(data -> ReportDataConstant.posMeal.midNight.equals(data.getMealId())).
                                    map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        //堂食
        if (ReportDataConstant.BusinessReportDayItemCode.CANTEEN.equals(report.getItemCode())) {
            report.setCurrentMoney(
                    reportPosDeskChannelVos.size() == Constant.Number.ZERO ? null :
                            reportPosDeskChannelVos.stream().filter(data -> ReportDataConstant.ChannelData.CANTEEN.equals(data.getChannelId())).
                                    map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        //外卖
        if (ReportDataConstant.BusinessReportDayItemCode.SALEOUT.equals(report.getItemCode())) {
            report.setCurrentMoney(
                    reportPosDeskChannelVos.size() == Constant.Number.ZERO ? null :
                            reportPosDeskChannelVos.stream().filter(data -> ReportDataConstant.ChannelData.SALEOUT.equals(data.getChannelId())).
                                    map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        //外带
        if (ReportDataConstant.BusinessReportDayItemCode.TAKEOUT.equals(report.getItemCode())) {
            report.setCurrentMoney(
                    reportPosDeskChannelVos.size() == Constant.Number.ZERO ? null :
                            reportPosDeskChannelVos.stream().filter(data -> ReportDataConstant.ChannelData.TAKEOUT.equals(data.getChannelId())).
                                    map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        //营业外收入
        if (ReportDataConstant.BusinessReportDayItemCode.NONOPERATINGINCOME.equals(report.getItemCode())) {
            report.setCurrentMoney(nonOperatingIncome);
        }

        //桌数
        if (ReportDataConstant.BusinessReportDayItemCode.DESKCOUNT.equals(report.getItemCode())) {
            report.setCurrentMoney(BigDecimal.valueOf(baseDeskVo.getDeskCount()));
        }

        //开台数
        BigDecimal openDeskNum = BigDecimal.valueOf(reportPosDeskMealVos.stream().
                mapToInt(ReportPosDeskVo::getDeskAllCount).sum());
        openDeskNum = openDeskNum == null ? Constant.Number.ZEROB : openDeskNum;
        if (ReportDataConstant.BusinessReportDayItemCode.OPENDESKS.equals(report.getItemCode())) {
            report.setCurrentMoney(openDeskNum);
        }
        //客流
        BigDecimal personSum = BigDecimal.valueOf(reportPosDeskMealVos.stream().
                mapToInt(ReportPosDeskVo::getPersonSum).sum());
        personSum = personSum == null ? Constant.Number.ZEROB : personSum;
        if (ReportDataConstant.BusinessReportDayItemCode.PEOPLES.equals(report.getItemCode())) {
            report.setCurrentMoney(personSum);
        }
        //人均
        if (ReportDataConstant.BusinessReportDayItemCode.PERCAPITA.equals(report.getItemCode()) && !personSum.equals(Constant.Number.ZEROB)) {
            report.setCurrentMoney(incomeMoney.
                            divide(personSum, Constant.Number.FOUR, ROUND_HALF_UP).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //桌均
        if (ReportDataConstant.BusinessReportDayItemCode.DESCOUNTCAPITA.equals(report.getItemCode()) && !openDeskNum.equals(Constant.Number.ZEROB)) {
            report.setCurrentMoney(incomeMoney.
                            divide(openDeskNum, Constant.Number.FOUR, ROUND_HALF_UP).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //折扣额
        if (ReportDataConstant.BusinessReportDayItemCode.DISCOUNTMONEY.equals(report.getItemCode())) {
            report.setCurrentMoney(discountMoney);
        }

        //折扣率
        if (ReportDataConstant.BusinessReportDayItemCode.DISCOUNTPERCENT.equals(report.getItemCode())) {
            report.setCurrentMoney(discountMoney == null ? null : incomeMoney == null ? null : incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    discountMoney.divide(incomeMoney, Constant.Number.FOUR, ROUND_HALF_UP).
                            multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //菜品收入
        if (ReportDataConstant.BusinessReportDayItemCode.FOODINCOME.equals(report.getItemCode())) {
            report.setCurrentMoney(repPosDetailFoodVo == null ? null :
                    repPosDetailFoodVo.getFoodAllPrice().
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : repPosDetailFoodVo.getFoodAllPrice());
        }
        //销售成本
        if (ReportDataConstant.BusinessReportDayItemCode.SALECOST.equals(report.getItemCode())) {
            if (null != realDataList && realDataList.size() > 0) {
                for (RealTimeProfitVo rv : realDataList) {
                    //销售成本
                    if (ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(rv.getItemCode())
                            && rv.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
                        report.setCurrentMoney(rv.getCurrentMoney());
                    }
                    continue;
                }
            }
        }
        //毛利率
        if (ReportDataConstant.BusinessReportDayItemCode.NETRATE.equals(report.getItemCode())) {
            if (null != realDataList && realDataList.size() > 0) {
                for (RealTimeProfitVo rv : realDataList) {
                    //毛利率 = （收入-成本）/收入
                    if (ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(rv.getItemCode())
                            && rv.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
                        report.setCurrentMoney(incomeMoney == null ? null : rv.getCurrentMoney() == null ? null : incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                incomeMoney.subtract(rv.getCurrentMoney()).divide(incomeMoney, Constant.Number.FOUR, ROUND_HALF_UP).
                                        multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                    }
                    continue;
                }
            }
        }
        //菜品成本
        if (ReportDataConstant.BusinessReportDayItemCode.FOODCOS.equals(report.getItemCode())) {
            if (null != realDataList && realDataList.size() > 0) {
                for (RealTimeProfitVo rv : realDataList) {
                    //菜品成本
                    if (ReportDataConstant.RealProfitItemCode.COST_OF_DISHES.equals(rv.getItemCode())
                            && rv.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
                        report.setCurrentMoney(rv.getCurrentMoney());
                    }
                    continue;
                }
            }
        }
        //菜品毛利率
        if (ReportDataConstant.BusinessReportDayItemCode.FOODRATE.equals(report.getItemCode())) {
            if (null != realDataList && realDataList.size() > 0) {
                for (RealTimeProfitVo rv : realDataList) {
                    //菜品毛利率 = （菜品收入-菜品成本）/菜品收入
                    if (ReportDataConstant.RealProfitItemCode.COST_OF_DISHES.equals(rv.getItemCode())
                            && rv.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
                        report.setCurrentMoney(repPosDetailFoodVo == null ? null : rv.getCurrentMoney() == null ? null : incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                repPosDetailFoodVo.getFoodAllPrice().subtract(rv.getCurrentMoney()).divide(repPosDetailFoodVo.getFoodAllPrice(), Constant.Number.FOUR, ROUND_HALF_UP).
                                        multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                    }
                    continue;
                }
            }
        }
        //净利润
        if (ReportDataConstant.BusinessReportDayItemCode.NETPROFIT.equals(report.getItemCode())) {
            if (null != realDataList && realDataList.size() > 0) {
                for (RealTimeProfitVo rv : realDataList) {
                    //净利润
                    if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(rv.getItemCode())
                            && rv.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
                        report.setCurrentMoney(rv.getCurrentMoney());
                    }
                    continue;
                }
            }
        }
        //客单利
        if (ReportDataConstant.BusinessReportDayItemCode.PERSONORDERPROFIT.equals(report.getItemCode())) {
            if (null != realDataList && realDataList.size() > 0) {
                for (RealTimeProfitVo rv : realDataList) {
                    //客单利 =净利润/客流量
                    if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(rv.getItemCode())
                            && rv.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
                        report.setCurrentMoney(personSum.compareTo(Constant.Number.ZEROB) == Constant.Number.ZERO ? Constant.Number.ZEROB :
                                rv.getCurrentMoney().divide(personSum,Constant.Number.TWO, ROUND_HALF_UP));
                    }
                    continue;
                }
            }
        }
        //计算会员分析
        calculationMemberCurrentMoney(report, memberNumAnalysisVos, repCrmTurnoverVo, incomeMoney);
    }

    /**
     * Description: 计算会员分析本期金额
     *
     * @author: LuoY
     * @date: 2020/2/19 14:02
     * @param:[memberNumAnalysisVos]
     * @return:void
     */
    private void calculationMemberCurrentMoney(BusinessReportDayItemVo report,
                                               List<MemberNumAnalysisVo> memberNumAnalysisVos,
                                               RepCrmTurnoverVo repCrmTurnoverVo, BigDecimal incomeMoney) {
        Integer members = Constant.Number.ZERO;
        if (!FastUtils.checkNullOrEmpty(memberNumAnalysisVos)) {
            members = (int) memberNumAnalysisVos.stream().mapToDouble(MemberNumAnalysisVo::getMemberAddNum).sum();
        }

        //会员人数
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERNUM.equals(report.getItemCode())) {
            report.setCurrentMoney(members == null ? null :
                    BigDecimal.valueOf(members).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : BigDecimal.valueOf(members));
        }

        //会员充值
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERRECHARGE.equals(report.getItemCode())) {
            report.setCurrentMoney(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getTotalPrepaidMoney() == null ? null :
                    repCrmTurnoverVo.getTotalPrepaidMoney().
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : repCrmTurnoverVo.getTotalPrepaidMoney());
        }

        //会员消费
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERCONSUMPTION.equals(report.getItemCode())) {
            report.setCurrentMoney(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getConsumeMoney() == null ? null :
                    repCrmTurnoverVo.getConsumeMoney().
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : repCrmTurnoverVo.getConsumeMoney());
        }

        //会员卡余额
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERCARDBALANCE.equals(report.getItemCode())) {
            report.setCurrentMoney(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getMemberBalance() == null ? null :
                    repCrmTurnoverVo.getMemberBalance().
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : repCrmTurnoverVo.getMemberBalance());
        }

        //会员消费占比
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERCONSUMPTIONRATE.equals(report.getItemCode())) {
            report.setCurrentMoney(incomeMoney == null ? null :
                    incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getConsumeMoney() == null ? null :
                                    repCrmTurnoverVo.getConsumeMoney().
                                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                            repCrmTurnoverVo.getConsumeMoney().divide(incomeMoney, Constant.Number.FOUR, ROUND_HALF_UP)
                                                    .multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

    }

    /**
     * @Description: 计算同比
     * @Param: [reportPosDeskVos, repPosDetailPayVos, report]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/1/7 11:44
     */
    private void calculationYearCompare(List<ReportPosDeskVo> reportPosDeskMealVos, List<ReportPosDeskVo> reportPosDeskChannelVos,
                                        List<RepPosDetailPayVo> repPosDetailPayVos, RepPosDetailFoodVo repPosDetailFoodVo,
                                        BaseDeskVo baseDeskVo, BusinessReportDayItemVo report,
                                        List<PosDiscountDetailPayVo> posDiscountDetailPayVos,
                                        List<MemberNumAnalysisVo> memberNumAnalysisVos,
                                        RepCrmTurnoverVo repCrmTurnoverVo) {
        //营业外收入
        BigDecimal nonOperatingIncome = reportPosDeskMealVos.stream().map(data -> data.getMoneyOverChargeSum() == null ? BigDecimal.ZERO : data.getMoneyOverChargeSum()).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //赠送金额
        BigDecimal giveMoney = posDiscountDetailPayVos.stream().map(a -> a.getTotalMoney() == null ? BigDecimal.ZERO : a.getTotalMoney()).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //收入
        BigDecimal incomeMoney = reportPosDeskMealVos.stream().map(ReportPosDeskVo::getAmountSum).
                reduce(BigDecimal.ZERO, BigDecimal::add).add(nonOperatingIncome);

        //折扣额
        BigDecimal discountMoney = repPosDetailPayVos.size() == Constant.Number.ZERO ? null :
                repPosDetailPayVos.stream().map(RepPosDetailPayVo::getMoneySum).
                        reduce(BigDecimal.ZERO, BigDecimal::add).
                        subtract(repPosDetailPayVos.stream().map(RepPosDetailPayVo::getMoneyActualSum).
                                reduce(BigDecimal.ZERO, BigDecimal::add)).add(giveMoney);

        //收入(包含折扣)
        if (ReportDataConstant.BusinessReportDayItemCode.INCOMEDISCOUNT.equals(report.getItemCode())) {
            report.setLastYear(incomeMoney);
            report.setYearCompare(incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(incomeMoney).
                                    divide(incomeMoney, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //中餐
        BigDecimal luncher = reportPosDeskMealVos.stream().
                filter(data -> ReportDataConstant.posMeal.LUNCH.equals(data.getMealId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.LUNCH.equals(report.getItemCode())) {
            report.setLastYear(luncher);
            report.setYearCompare(luncher.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(luncher)).
                                    divide(luncher, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //晚餐
        BigDecimal dinner = reportPosDeskMealVos.stream().
                filter(data -> ReportDataConstant.posMeal.DINNER.equals(data.getMealId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.DINNER.equals(report.getItemCode())) {
            report.setLastYear(dinner);
            report.setYearCompare(dinner.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(dinner)).
                                    divide(dinner, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //夜宵
        BigDecimal middleNight = reportPosDeskMealVos.stream().
                filter(data -> ReportDataConstant.posMeal.midNight.equals(data.getMealId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.MIDDLENIGHT.equals(report.getItemCode())) {
            report.setLastYear(middleNight);
            report.setYearCompare(middleNight.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(middleNight)).
                                    divide(middleNight, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //堂食
        BigDecimal canteen = reportPosDeskChannelVos.stream().
                filter(data -> ReportDataConstant.ChannelData.CANTEEN.equals(data.getChannelId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.CANTEEN.equals(report.getItemCode())) {
            report.setLastYear(canteen);
            report.setYearCompare(canteen.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(canteen)).
                                    divide(canteen, ROUND_HALF_UP, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //外卖
        BigDecimal saleOut = reportPosDeskChannelVos.stream().
                filter(data -> ReportDataConstant.ChannelData.SALEOUT.equals(data.getChannelId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.SALEOUT.equals(report.getItemCode())) {
            report.setLastYear(saleOut);
            report.setYearCompare(saleOut.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(saleOut)).
                                    divide(saleOut, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //外带
        BigDecimal takeOut = reportPosDeskChannelVos.stream().
                filter(data -> ReportDataConstant.ChannelData.TAKEOUT.equals(data.getChannelId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.TAKEOUT.equals(report.getItemCode())) {
            report.setLastYear(takeOut);
            report.setYearCompare(takeOut.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(takeOut)).
                                    divide(takeOut, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //营业外收入
        if (ReportDataConstant.BusinessReportDayItemCode.NONOPERATINGINCOME.equals(report.getItemCode())) {
            report.setYearCompare(nonOperatingIncome == null ? null : nonOperatingIncome.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(nonOperatingIncome)).
                                    divide(nonOperatingIncome, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //桌数
        BigDecimal deskCount = BigDecimal.valueOf(baseDeskVo.getDeskCount());
        if (ReportDataConstant.BusinessReportDayItemCode.DESKCOUNT.equals(report.getItemCode())) {
            report.setLastYear(deskCount);
            report.setYearCompare(deskCount.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(deskCount)).
                                    divide(deskCount, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //开台数
        BigDecimal openDesks = BigDecimal.valueOf(reportPosDeskMealVos.stream().
                mapToInt(ReportPosDeskVo::getDeskAllCount).sum());
        if (ReportDataConstant.BusinessReportDayItemCode.OPENDESKS.equals(report.getItemCode())) {
            report.setLastYear(openDesks);
            report.setYearCompare(openDesks.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(openDesks)).
                                    divide(openDesks, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //客流
        BigDecimal clientCount = BigDecimal.valueOf(reportPosDeskMealVos.stream().
                mapToInt(ReportPosDeskVo::getPersonSum).sum());
        if (ReportDataConstant.BusinessReportDayItemCode.PEOPLES.equals(report.getItemCode())) {
            report.setLastYear(clientCount);
            report.setYearCompare(clientCount.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(clientCount).
                                    divide(clientCount, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //人均
        BigDecimal percapita = BigDecimal.valueOf(reportPosDeskMealVos.stream().mapToInt(ReportPosDeskVo::getPersonSum).sum())
                .compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? BigDecimal.ZERO :
                reportPosDeskMealVos.stream().map(ReportPosDeskVo::getAmountSum).
                        reduce(BigDecimal.ZERO, BigDecimal::add).
                        divide(BigDecimal.valueOf(reportPosDeskMealVos.stream().
                                mapToInt(ReportPosDeskVo::getPersonSum).sum()), Constant.Number.TWO, ROUND_HALF_UP);
        if (ReportDataConstant.BusinessReportDayItemCode.PERCAPITA.equals(report.getItemCode())) {
            report.setLastYear(percapita);
            report.setYearCompare(percapita.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(percapita).divide(percapita, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //桌均
        BigDecimal descountCaptia = BigDecimal.valueOf(reportPosDeskMealVos.stream().mapToInt(ReportPosDeskVo::getDeskAllCount).
                sum()).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? BigDecimal.ZERO :
                reportPosDeskMealVos.stream().map(ReportPosDeskVo::getAmountSum).
                        reduce(BigDecimal.ZERO, BigDecimal::add).
                        divide(BigDecimal.valueOf(reportPosDeskMealVos.stream().mapToInt(ReportPosDeskVo::getDeskAllCount).
                                sum()), Constant.Number.FOUR, ROUND_HALF_UP).setScale(Constant.Number.TWO, ROUND_HALF_UP);
        if (ReportDataConstant.BusinessReportDayItemCode.DESCOUNTCAPITA.equals(report.getItemCode())) {
            report.setLastYear(descountCaptia);
            report.setYearCompare(descountCaptia.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(descountCaptia).divide(descountCaptia, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //折扣额
        if (ReportDataConstant.BusinessReportDayItemCode.DISCOUNTMONEY.equals(report.getItemCode())) {
            report.setLastYear(discountMoney);
            report.setYearCompare(discountMoney == null ? null : discountMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : report.getCurrentMoney()
                            .subtract(discountMoney).
                                    divide(discountMoney, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //折扣率
        BigDecimal discountPercent = discountMoney == null ? null : incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                BigDecimal.ZERO : discountMoney.divide(incomeMoney, Constant.Number.FOUR, ROUND_HALF_UP).
                multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP);
        if (ReportDataConstant.BusinessReportDayItemCode.DISCOUNTPERCENT.equals(report.getItemCode())) {
            report.setLastYear(discountPercent);
            report.setYearCompare(discountMoney == null ? null : discountPercent.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : report.getCurrentMoney()
                            .subtract(discountPercent).
                                    divide(discountPercent, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //菜品收入
        BigDecimal foodIncome = repPosDetailFoodVo == null ? null :
                repPosDetailFoodVo.getFoodAllPrice().
                        compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : repPosDetailFoodVo.getFoodAllPrice();
        if (ReportDataConstant.BusinessReportDayItemCode.FOODINCOME.equals(report.getItemCode())) {
            report.setLastYear(foodIncome);
            report.setYearCompare(repPosDetailFoodVo == null ? null :
                    repPosDetailFoodVo.getFoodAllPrice().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney() == null ? null : report.getCurrentMoney()
                                    .subtract(foodIncome).
                                            divide(foodIncome, Constant.Number.FOUR, ROUND_HALF_UP).
                                            multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //会员分析同比
        calculationMemberYearCompareMoney(report, memberNumAnalysisVos, repCrmTurnoverVo, incomeMoney);
    }

    /**
     * Description: 会员分析同比
     *
     * @author: LuoY
     * @date: 2020/2/20 15:28
     * @param:[report, memberNumAnalysisVos, repCrmTurnoverVo, incomeMoney]
     * @return:void
     */
    private void calculationMemberYearCompareMoney(BusinessReportDayItemVo report,
                                                   List<MemberNumAnalysisVo> memberNumAnalysisVos,
                                                   RepCrmTurnoverVo repCrmTurnoverVo, BigDecimal incomeMoney) {
        Integer members = Constant.Number.ZERO;
        if (!FastUtils.checkNullOrEmpty(memberNumAnalysisVos)) {
            members = (int) memberNumAnalysisVos.stream().mapToDouble(MemberNumAnalysisVo::getMemberAddNum).sum();
        }
        //会员人数
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERNUM.equals(report.getItemCode())) {
            report.setLastYear(new BigDecimal(members));
            report.setYearCompare(BigDecimal.valueOf(members).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney()
                                    .subtract(BigDecimal.valueOf(members)).
                                    divide(BigDecimal.valueOf(members), Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //会员充值
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERRECHARGE.equals(report.getItemCode())) {
            report.setLastYear(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getTotalPrepaidMoney());
            report.setYearCompare(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getTotalPrepaidMoney().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney()
                                    .subtract(repCrmTurnoverVo.getTotalPrepaidMoney()).
                                    divide(repCrmTurnoverVo.getTotalPrepaidMoney(), Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //会员消费
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERCONSUMPTION.equals(report.getItemCode())) {
            report.setLastYear(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getConsumeMoney());
            report.setYearCompare(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getConsumeMoney().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney()
                                    .subtract(repCrmTurnoverVo.getConsumeMoney()).
                                    divide(repCrmTurnoverVo.getConsumeMoney(), Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //会员卡余额
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERCARDBALANCE.equals(report.getItemCode())) {
            report.setLastYear(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getMemberBalance());
            report.setYearCompare(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getMemberBalance().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney()
                                    .subtract(repCrmTurnoverVo.getMemberBalance()).
                                    divide(repCrmTurnoverVo.getMemberBalance(), Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        BigDecimal MemberConsumptionRate = repCrmTurnoverVo == null ? null : incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                repCrmTurnoverVo.getConsumeMoney() == null ? null :
                        repCrmTurnoverVo.getConsumeMoney().
                                compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                repCrmTurnoverVo.getConsumeMoney().divide(incomeMoney, Constant.Number.FOUR, ROUND_HALF_UP)
                                        .subtract(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP);
        //会员消费占比
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERCONSUMPTIONRATE.equals(report.getItemCode())) {
            report.setLastYear(MemberConsumptionRate);
            report.setYearCompare(MemberConsumptionRate == null ? null : MemberConsumptionRate.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    repCrmTurnoverVo == null ? null : report.getCurrentMoney() == null ? null : report.getCurrentMoney()
                            .subtract(MemberConsumptionRate).
                                    divide(MemberConsumptionRate, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

    }

    /**
     * @Description: 计算环比
     * @Param: [reportPosDeskVos, repPosDetailPayVos, report]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/1/7 11:46
     */
    private void calculationMonthCompare(List<ReportPosDeskVo> reportPosDeskMealVos, List<ReportPosDeskVo> reportPosDeskChannelVos,
                                         List<RepPosDetailPayVo> repPosDetailPayVos, RepPosDetailFoodVo repPosDetailFoodVo,
                                         BaseDeskVo baseDeskVo, BusinessReportDayItemVo report,
                                         List<PosDiscountDetailPayVo> posDiscountDetailPayVos,
                                         List<MemberNumAnalysisVo> memberNumAnalysisVos,
                                         RepCrmTurnoverVo repCrmTurnoverVo) {
        //营业外收入
        BigDecimal nonOperatingIncome = reportPosDeskMealVos.stream().map(data -> data.getMoneyOverChargeSum() == null ? BigDecimal.ZERO : data.getMoneyOverChargeSum()).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //赠送金额
        BigDecimal giveMoney = posDiscountDetailPayVos.stream().map(a -> a.getTotalMoney() == null ? BigDecimal.ZERO : a.getTotalMoney()).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //收入
        BigDecimal incomeMoney = reportPosDeskMealVos.stream().map(ReportPosDeskVo::getAmountSum).
                reduce(BigDecimal.ZERO, BigDecimal::add).add(nonOperatingIncome);
        //折扣额
        BigDecimal discountMoney = repPosDetailPayVos.size() == Constant.Number.ZERO ? null :
                repPosDetailPayVos.stream().map(RepPosDetailPayVo::getMoneySum).
                        reduce(BigDecimal.ZERO, BigDecimal::add).
                        subtract(repPosDetailPayVos.stream().map(RepPosDetailPayVo::getMoneyActualSum).
                                reduce(BigDecimal.ZERO, BigDecimal::add)).add(giveMoney);
        //收入(包含折扣)
        if (ReportDataConstant.BusinessReportDayItemCode.INCOMEDISCOUNT.equals(report.getItemCode())) {
            report.setLastPeriod(incomeMoney);
            report.setMonthCompare(incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(incomeMoney).divide(incomeMoney, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, BigDecimal.ROUND_CEILING));
        }
        //中餐
        BigDecimal luncher = reportPosDeskMealVos.stream().
                filter(data -> ReportDataConstant.posMeal.LUNCH.equals(data.getMealId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.LUNCH.equals(report.getItemCode())) {
            report.setLastPeriod(luncher);
            report.setMonthCompare(luncher.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(luncher).
                                    divide(luncher, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //晚餐
        BigDecimal dinner = reportPosDeskMealVos.stream().
                filter(data -> ReportDataConstant.posMeal.DINNER.equals(data.getMealId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.DINNER.equals(report.getItemCode())) {
            report.setLastPeriod(dinner);
            report.setMonthCompare(dinner.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(dinner).
                                    divide(dinner, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //夜宵
        BigDecimal middleNight = reportPosDeskMealVos.stream().
                filter(data -> ReportDataConstant.posMeal.midNight.equals(data.getMealId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.MIDDLENIGHT.equals(report.getItemCode())) {
            report.setLastPeriod(middleNight);
            report.setMonthCompare(middleNight.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().
                                    subtract(middleNight).divide(middleNight, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //堂食
        BigDecimal canteen = reportPosDeskChannelVos.stream().
                filter(data -> ReportDataConstant.ChannelData.CANTEEN.equals(data.getChannelId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.CANTEEN.equals(report.getItemCode())) {
            report.setLastPeriod(canteen);
            report.setMonthCompare(canteen.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(canteen).
                                    divide(canteen, ROUND_HALF_UP, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //外卖
        BigDecimal saleOut = reportPosDeskChannelVos.stream().
                filter(data -> ReportDataConstant.ChannelData.SALEOUT.equals(data.getChannelId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.SALEOUT.equals(report.getItemCode())) {
            report.setLastPeriod(saleOut);
            report.setMonthCompare(saleOut.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(saleOut).
                                    divide(saleOut, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //外带
        BigDecimal takeOut = reportPosDeskChannelVos.stream().
                filter(data -> ReportDataConstant.ChannelData.TAKEOUT.equals(data.getChannelId())).
                map(ReportPosDeskVo::getAmountSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (ReportDataConstant.BusinessReportDayItemCode.TAKEOUT.equals(report.getItemCode())) {
            report.setLastPeriod(takeOut);
            report.setMonthCompare(takeOut.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().
                                    subtract(takeOut).divide(takeOut, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //营业外收入
        if (ReportDataConstant.BusinessReportDayItemCode.NONOPERATINGINCOME.equals(report.getItemCode())) {
            report.setMonthCompare(nonOperatingIncome == null ? null : nonOperatingIncome.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(FastUtils.Null2Zero(nonOperatingIncome)).
                                    divide(nonOperatingIncome, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        BigDecimal deskCount = BigDecimal.valueOf(baseDeskVo.getDeskCount());
        //桌数
        if (ReportDataConstant.BusinessReportDayItemCode.DESKCOUNT.equals(report.getItemCode())) {
            report.setLastPeriod(deskCount);
            report.setMonthCompare(deskCount.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().
                                    subtract(deskCount).divide(deskCount, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //开台数
        BigDecimal openDesks = BigDecimal.valueOf(reportPosDeskMealVos.stream().
                mapToInt(ReportPosDeskVo::getDeskAllCount).sum());
        if (ReportDataConstant.BusinessReportDayItemCode.OPENDESKS.equals(report.getItemCode())) {
            report.setLastPeriod(openDesks);
            report.setMonthCompare(openDesks.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().
                                    subtract(openDesks).divide(openDesks, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //客流
        BigDecimal clientCount = BigDecimal.valueOf(reportPosDeskMealVos.stream().
                mapToInt(ReportPosDeskVo::getPersonSum).sum());
        if (ReportDataConstant.BusinessReportDayItemCode.PEOPLES.equals(report.getItemCode())) {
            report.setLastPeriod(clientCount);
            report.setMonthCompare(clientCount.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : report.getCurrentMoney().
                            subtract(clientCount).divide(clientCount, Constant.Number.FOUR, ROUND_HALF_UP).
                            multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //人均
        BigDecimal percapita = BigDecimal.valueOf(reportPosDeskMealVos.stream().mapToInt(ReportPosDeskVo::getPersonSum).
                sum()).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? BigDecimal.ZERO :
                reportPosDeskMealVos.stream().map(ReportPosDeskVo::getAmountSum).
                        reduce(BigDecimal.ZERO, BigDecimal::add).
                        divide(BigDecimal.valueOf(reportPosDeskMealVos.stream().
                                mapToInt(ReportPosDeskVo::getPersonSum).sum()), Constant.Number.TWO, ROUND_HALF_UP);
        if (ReportDataConstant.BusinessReportDayItemCode.PERCAPITA.equals(report.getItemCode())) {
            report.setLastPeriod(percapita);
            report.setMonthCompare(percapita.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : report.getCurrentMoney().subtract(percapita).
                            divide(percapita, Constant.Number.FOUR, ROUND_HALF_UP).
                            multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //桌均
        BigDecimal descountCaptia = BigDecimal.valueOf(reportPosDeskMealVos.stream().mapToInt(ReportPosDeskVo::getDeskAllCount).
                sum()).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? BigDecimal.ZERO :
                reportPosDeskMealVos.stream().map(ReportPosDeskVo::getAmountSum).
                        reduce(BigDecimal.ZERO, BigDecimal::add).
                        divide(BigDecimal.valueOf(reportPosDeskMealVos.stream().mapToInt(ReportPosDeskVo::getDeskAllCount).
                                sum()), Constant.Number.FOUR, ROUND_HALF_UP).setScale(Constant.Number.TWO, ROUND_HALF_UP);
        if (ReportDataConstant.BusinessReportDayItemCode.DESCOUNTCAPITA.equals(report.getItemCode())) {
            report.setLastPeriod(descountCaptia);
            report.setMonthCompare(descountCaptia.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(descountCaptia).
                                    divide(descountCaptia, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //折扣额
        if (ReportDataConstant.BusinessReportDayItemCode.DISCOUNTMONEY.equals(report.getItemCode())) {
            report.setLastPeriod(discountMoney);
            report.setMonthCompare(discountMoney == null ? null : discountMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(discountMoney).
                                    divide(discountMoney, Constant.Number.FOUR).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //折扣率
        BigDecimal discountPercent = discountMoney == null ? null : incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                BigDecimal.ZERO : discountMoney.divide(incomeMoney, Constant.Number.FOUR, ROUND_HALF_UP).
                multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP);
        if (ReportDataConstant.BusinessReportDayItemCode.DISCOUNTPERCENT.equals(report.getItemCode())) {
            report.setLastPeriod(discountPercent);
            report.setMonthCompare(discountPercent == null ? null : discountPercent.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney().subtract(discountPercent).
                                    divide(discountPercent, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //菜品收入
        BigDecimal foodIncome = repPosDetailFoodVo == null ? null :
                repPosDetailFoodVo.getFoodAllPrice().
                        compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : repPosDetailFoodVo.getFoodAllPrice();
        if (ReportDataConstant.BusinessReportDayItemCode.FOODINCOME.equals(report.getItemCode())) {
            report.setLastPeriod(discountPercent);
            report.setMonthCompare(repPosDetailFoodVo == null ? null : foodIncome == null ? null :
                    repPosDetailFoodVo.getFoodAllPrice().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            report.getCurrentMoney() == null ? null : FastUtils.Null2Zero(report.getCurrentMoney()).
                                    compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                    report.getCurrentMoney()
                                            .subtract(foodIncome).
                                            divide(foodIncome, Constant.Number.FOUR, ROUND_HALF_UP).
                                            multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }
        //会员卡分析
        calculationMemberMonthCompareMoney(report, memberNumAnalysisVos, repCrmTurnoverVo, incomeMoney);
    }

    /**
     * Description:会员分析环比
     *
     * @author: LuoY
     * @date: 2020/2/20 15:30
     * @param:[report, memberNumAnalysisVos, repCrmTurnoverVo, incomeMoney]
     * @return:void
     */
    private void calculationMemberMonthCompareMoney(BusinessReportDayItemVo report,
                                                    List<MemberNumAnalysisVo> memberNumAnalysisVos,
                                                    RepCrmTurnoverVo repCrmTurnoverVo, BigDecimal incomeMoney) {
        Integer members = Constant.Number.ZERO;
        if (!FastUtils.checkNullOrEmpty(memberNumAnalysisVos)) {
            members = (int) memberNumAnalysisVos.stream().mapToDouble(MemberNumAnalysisVo::getMemberAddNum).sum();
        }
        //会员人数
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERNUM.equals(report.getItemCode())) {
            report.setLastPeriod(new BigDecimal(members));
            report.setMonthCompare(BigDecimal.valueOf(members).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : report.getCurrentMoney()
                            .subtract(BigDecimal.valueOf(members)).
                                    divide(BigDecimal.valueOf(members), Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //会员充值
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERRECHARGE.equals(report.getItemCode())) {
            report.setLastPeriod(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getTotalPrepaidMoney());
            report.setMonthCompare(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getTotalPrepaidMoney().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : report.getCurrentMoney()
                            .subtract(repCrmTurnoverVo.getTotalPrepaidMoney()).
                                    divide(repCrmTurnoverVo.getTotalPrepaidMoney(), Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //会员消费
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERCONSUMPTION.equals(report.getItemCode())) {
            report.setLastPeriod(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getConsumeMoney());
            report.setMonthCompare(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getConsumeMoney().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : report.getCurrentMoney()
                            .subtract(repCrmTurnoverVo.getConsumeMoney()).
                                    divide(repCrmTurnoverVo.getConsumeMoney(), Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        //会员卡余额
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERCARDBALANCE.equals(report.getItemCode())) {
            report.setLastPeriod(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getMemberBalance());
            report.setMonthCompare(repCrmTurnoverVo == null ? null : repCrmTurnoverVo.getMemberBalance().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    report.getCurrentMoney() == null ? null : report.getCurrentMoney()
                            .subtract(repCrmTurnoverVo.getMemberBalance()).
                                    divide(repCrmTurnoverVo.getMemberBalance(), Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

        BigDecimal MemberConsumptionRate = repCrmTurnoverVo == null ? null : incomeMoney.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                repCrmTurnoverVo.getConsumeMoney() == null ? null :
                        repCrmTurnoverVo.getConsumeMoney().
                                compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                repCrmTurnoverVo.getConsumeMoney().divide(incomeMoney, Constant.Number.FOUR, ROUND_HALF_UP)
                                        .subtract(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP);
        //会员消费占比
        if (ReportDataConstant.BusinessReportDayItemCode.MEMBERCONSUMPTIONRATE.equals(report.getItemCode())) {
            report.setLastPeriod(MemberConsumptionRate);
            report.setMonthCompare(MemberConsumptionRate == null ? null : MemberConsumptionRate.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                    repCrmTurnoverVo == null ? null : report.getCurrentMoney() == null ? null : report.getCurrentMoney()
                            .subtract(MemberConsumptionRate).
                                    divide(MemberConsumptionRate, Constant.Number.FOUR, ROUND_HALF_UP).
                                    multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
        }

    }

    /**
     * @Description: 点菜结构
     * @Param: [businessReportDayItemVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/1/2 17:19
     */
    private void orderFoodAnalysis(List<BusinessReportDayItemVo> businessReportDayItemVos, List<RepPosDetailFoodVo> repPosDetailFoodVos, Integer dataType) {
        Integer id = Constant.Number.FOURTY;
        if (ReportDataConstant.BusinessAnalysisDataType.CURRENTMONEY.equals(dataType)) {
            //添加一个菜品结构大类
            businessReportDayItemVos.add(setInfo(ReportDataConstant.BusinessReportItemParam.ORDERFOODSTYTLE, null,
                    null, Constant.Number.ZERO, null, null, Constant.Number.FOURTY, Constant.DataType.NONE));
            //添加详细信息
            for (RepPosDetailFoodVo foodVo : repPosDetailFoodVos) {
                id++;
                BusinessReportDayItemVo businessReportDayItemVo = setInfo(String.valueOf(repPosDetailFoodVos.indexOf(foodVo) + Constant.Number.ONE),
                        foodVo.getFoodStyleNo(), foodVo.getFoodStyleName(),
                        Constant.Number.ONE, Constant.Number.FIVE, ReportDataConstant.BusinessReportItemParam.ORDERFOODSTYTLENAME, id, Constant.DataType.MONEY);
                businessReportDayItemVo.setCurrentMoney(foodVo.getFoodAllPrice());
                businessReportDayItemVos.add(businessReportDayItemVo);
            }
        }
//        else if (ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE.equals(dataType)) {
//            //计算同比
//            businessReportDayItemVos.forEach(item -> {
//                repPosDetailFoodVos.forEach(food -> {
//                    if (food.getFoodStyleNo().equals(item.getItemCode())) {
//                        item.setLastYear(food.getFoodAllPrice());
//                        item.setYearCompare(food.getFoodAllPrice().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
//                                item.getCurrentMoney().subtract(food.getFoodAllPrice()).
//                                        divide(food.getFoodAllPrice(), Constant.Number.FOUR, ROUND_HALF_UP).
//                                        multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, BigDecimal.ROUND_CEILING));
//                    }
//                });
//            });
//        } else if (ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE.equals(dataType)) {
//            //计算环比
//            businessReportDayItemVos.forEach(item -> {
//                repPosDetailFoodVos.forEach(food -> {
//                    if (food.getFoodStyleNo().equals(item.getItemCode())) {
//                        item.setLastPeriod(food.getFoodAllPrice());
//                        item.setMonthCompare(food.getFoodAllPrice().compareTo(BigDecimal.ZERO) == Constant.Number
//                                .ZERO ? null : item.getCurrentMoney().subtract(food.getFoodAllPrice()).
//                                divide(food.getFoodAllPrice(), Constant.Number.FOUR, ROUND_HALF_UP).
//                                multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, BigDecimal.ROUND_CEILING));
//                    }
//                });
//            });
//        }
    }

    /**
     * 计算指标
     *
     * @param businessReportDayItemVos
     * @param businessReportDayDto
     * @author LuoY
     */
    private void calculationBusinessAnalysisIndex(List<BusinessReportDayItemVo> businessReportDayItemVos, BusinessReportDayDto businessReportDayDto) {
        //调用看板中计算指标方法
        ViewManagerQueryDto viewManagerDto = new ViewManagerQueryDto();
        viewManagerDto.setEnteId(businessReportDayDto.getEnteId());
        viewManagerDto.setEndDate(businessReportDayDto.getEndDate());
        viewManagerDto.setBeginDate(businessReportDayDto.getBeginDate());
        viewManagerDto.setDateType(businessReportDayDto.getDateType());
        viewManagerDto.setShopIdList(businessReportDayDto.getShopIdList());
        List<BusinessDailyIndicVo> businessDailyIndicVos = viewService.dealWithIndic(viewManagerDto,Constant.Character.ONE);

        //计算金额
        if (!FastUtils.checkNullOrEmpty(businessDailyIndicVos)) {
           /* //收入
            BigDecimal income = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(ReportDataConstant.BusinessReportDayItemCode.INCOMEDISCOUNT))
                    .map(BusinessDailyIndicVo::getIndicatorDay).reduce(BigDecimal.ZERO, BigDecimal::add).
                            divide(BigDecimal.valueOf(businessDailyIndicVos.size()), Constant.Number.FOUR, RoundingMode.HALF_UP);
            //折扣额
            BigDecimal ZKE = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(ReportDataConstant.BusinessReportDayItemCode.DISCOUNTMONEY))
                    .map(BusinessDailyIndicVo::getIndicatorDay).reduce(BigDecimal.ZERO, BigDecimal::add).
                            divide(BigDecimal.valueOf(businessDailyIndicVos.size()), Constant.Number.FOUR, RoundingMode.HALF_UP);
            //客流量
            BigDecimal KLL = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(ReportDataConstant.BusinessReportDayItemCode.PEOPLES))
                    .map(BusinessDailyIndicVo::getIndicatorDay).reduce(BigDecimal.ZERO, BigDecimal::add).
                            divide(BigDecimal.valueOf(businessDailyIndicVos.size()), Constant.Number.FOUR, RoundingMode.HALF_UP);
            //销售成本
            BigDecimal XSCB = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(ReportDataConstant.BusinessReportDayItemCode.SALECOST))
                    .map(BusinessDailyIndicVo::getIndicatorDay).reduce(BigDecimal.ZERO, BigDecimal::add).
                            divide(BigDecimal.valueOf(businessDailyIndicVos.size()), Constant.Number.FOUR, RoundingMode.HALF_UP);
            //开台数
            BigDecimal KTS = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(ReportDataConstant.BusinessReportDayItemCode.OPENDESKS))
                    .map(BusinessDailyIndicVo::getIndicatorDay).reduce(BigDecimal.ZERO, BigDecimal::add).
                            divide(BigDecimal.valueOf(businessDailyIndicVos.size()), Constant.Number.FOUR, RoundingMode.HALF_UP);
            //菜品成本
            BigDecimal CPCB = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(ReportDataConstant.BusinessReportDayItemCode.FOODCOS))
                    .map(BusinessDailyIndicVo::getIndicatorDay).reduce(BigDecimal.ZERO, BigDecimal::add).
                            divide(BigDecimal.valueOf(businessDailyIndicVos.size()), Constant.Number.FOUR, RoundingMode.HALF_UP);
            //净利润
            BigDecimal JLR = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(ReportDataConstant.BusinessReportDayItemCode.NETPROFIT))
                    .map(BusinessDailyIndicVo::getIndicatorDay).reduce(BigDecimal.ZERO, BigDecimal::add).
                            divide(BigDecimal.valueOf(businessDailyIndicVos.size()), Constant.Number.FOUR, RoundingMode.HALF_UP);
            //采购成本
            BigDecimal CGCB = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(ReportDataConstant.BusinessReportDayItemCode.PROCUREMENTCOST))
                    .map(BusinessDailyIndicVo::getIndicatorDay).reduce(BigDecimal.ZERO, BigDecimal::add).
                            divide(BigDecimal.valueOf(businessDailyIndicVos.size()), Constant.Number.FOUR, RoundingMode.HALF_UP);*/
            //给指标赋值
            for (BusinessReportDayItemVo item : businessReportDayItemVos) {
                if (!FastUtils.checkNull(item.getItemCode())) {
                    BigDecimal other = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(item.getItemCode()))
                            .filter(Objects::nonNull)
                            .map(BusinessDailyIndicVo::getIndicator).reduce(BigDecimal.ZERO, BigDecimal::add);
                    //百分比加类型
                    if(item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.DISCOUNTPERCENT)
                    ||item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.NETRATE)
                    ||item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.FOODRATE)
                    ||item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.SALEPERCENT)){
                        item.setDataType(Constant.DataType.PERCENT);
                    }
                    //除出来的数据直接算平均，别的求和
                    if(StringUtil.getProject(item.getItemCode())){
                        long count = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(item.getItemCode()))
                                .filter(Objects::nonNull).count();
                        item.setIndex(other.divide(new BigDecimal(count), Constant.Number.TWO).setScale(Constant.Number.TWO, BigDecimal.ROUND_CEILING));
                    }else {
                        //其它
                        item.setIndex(other == null ? null : other.setScale(Constant.Number.TWO, RoundingMode.HALF_UP));
                    }
                  /*  //折扣率
                    if (item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.DISCOUNTPERCENT)) {
                        item.setIndex(income.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : ZKE.multiply(BigDecimal.valueOf(quaryDays)).divide(income.multiply(BigDecimal.valueOf(quaryDays)), Constant.Number.FOUR, ROUND_HALF_UP).multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                        item.setDataType(Constant.DataType.PERCENT);
                    } else if (item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.PERCAPITA)) {
                        //人均
                        item.setIndex(KLL.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : income.multiply(BigDecimal.valueOf(quaryDays)).divide(KLL.multiply(BigDecimal.valueOf(quaryDays)), Constant.Number.TWO, ROUND_HALF_UP));
                    } else if (item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.NETRATE)) {
                        //毛利率
                        item.setIndex(income.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : income.multiply(BigDecimal.valueOf(quaryDays)).subtract(XSCB.multiply(BigDecimal.valueOf(quaryDays))).divide(income, Constant.Number.FOUR, ROUND_HALF_UP).multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                        item.setDataType(Constant.DataType.PERCENT);
                    } else if (item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.DESCOUNTCAPITA)) {
                        //桌均
                        item.setIndex(KTS.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : income.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : income.multiply(BigDecimal.valueOf(quaryDays)).divide(KTS.multiply(BigDecimal.valueOf(quaryDays)), Constant.Number.TWO, ROUND_HALF_UP));
                    } else if (item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.FOODRATE)) {
                        //菜品毛利率
                        item.setIndex(income.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : income.multiply(BigDecimal.valueOf(quaryDays)).subtract(CPCB.multiply(BigDecimal.valueOf(quaryDays))).divide(income.multiply(BigDecimal.valueOf(quaryDays)), Constant.Number.FOUR, ROUND_HALF_UP).multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                        item.setDataType(Constant.DataType.PERCENT);
                    } else if (item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.PERSONORDERPROFIT)) {
                        //开台率
                        item.setIndex(KLL.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : JLR.multiply(BigDecimal.valueOf(quaryDays)).divide(KLL.multiply(BigDecimal.valueOf(quaryDays)), Constant.Number.FOUR, ROUND_HALF_UP).multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                        item.setDataType(Constant.DataType.PERCENT);
                    } else if (item.getItemCode().equals(ReportDataConstant.BusinessReportDayItemCode.SALEPERCENT)) {
                        //采销比
                        item.setIndex(income.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : CGCB.multiply(BigDecimal.valueOf(quaryDays)).divide(income.multiply(BigDecimal.valueOf(quaryDays)), Constant.Number.TWO, ROUND_HALF_UP));
                        item.setDataType(Constant.DataType.PERCENT);
                    } else {
                        //其它
                        BigDecimal other = businessDailyIndicVos.stream().filter(data -> data.getProjectId().equals(item.getItemCode()))
                                .map(BusinessDailyIndicVo::getIndicatorDay).reduce(BigDecimal.ZERO, BigDecimal::add);
                        item.setIndex(other.multiply(BigDecimal.valueOf(quaryDays)).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                    }*/
                }

            }
        }


    }

    /**
     * 计算指标完成率
     *
     * @param businessReportDayItemVos
     * @author LuoY
     */
    private void calculationIndexComplement(List<BusinessReportDayItemVo> businessReportDayItemVos) {
        businessReportDayItemVos.forEach(item -> {
            if (!FastUtils.checkNull(item.getItemCode())) {
                item.setCompletionIndex(item.getCurrentMoney() == null ? null : item.getIndex() == null ? null :
                        item.getIndex().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                item.getCurrentMoney().divide(item.getIndex(), Constant.Number.FOUR, ROUND_HALF_UP).
                                        multiply(Constant.Number.HUNDRED).
                                        setScale(Constant.Number.TWO, ROUND_HALF_UP));
            }
        });
    }

    /**
     * 计算指标排名
     *
     * @param businessReportDayItemVos
     * @Author LuoY
     */
    private void calculationIndexRank(List<BusinessReportDayItemVo> businessReportDayItemVos) {
        //将指标按降序排序，null得排最后
        businessReportDayItemVos.sort((v1, v2) -> {
            int result = Constant.Number.ZERO;
            if (v1.getCompletionIndex() != null && v2.getCompletionIndex() != null) {
                result = -(v1.getCompletionIndex().compareTo(v2.getCompletionIndex()));
            } else if (v2.getCompletionIndex() == null && v1.getCompletionIndex() == null) {
                result = -Constant.Number.ONE;
            } else if (v1.getCompletionIndex() != null && v2.getCompletionIndex() == null) {
                result = -Constant.Number.ONE;
            } else if (v1.getCompletionIndex() == null && v2.getCompletionIndex() != null) {
                result = Constant.Number.ONE;
            }
            return result;
        });


        //计算排名
        BigDecimal upScore = BigDecimal.ZERO;
        int rank = Constant.Number.ZERO;
        for (BusinessReportDayItemVo itemVo : businessReportDayItemVos) {
            if (!FastUtils.checkNull(itemVo.getItemCode())) {
                if (!FastUtils.checkNull(itemVo.getCompletionIndex())) {
                    if (upScore.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO) {
                        //第一个排名
                        rank = rank + Constant.Number.ONE;
                        upScore = itemVo.getCompletionIndex();
                        itemVo.setIndexRanking(rank);
                    } else {
                        //分数和上一个一样得情况
                        if (itemVo.getCompletionIndex().equals(upScore)) {
                            itemVo.setIndexRanking(rank);
                        } else {
                            //如果分数不一样，计算排名
                            itemVo.setIndexRanking(businessReportDayItemVos.indexOf(itemVo) + Constant.Number.ONE);
                            upScore = itemVo.getCompletionIndex();
                            rank = businessReportDayItemVos.indexOf(itemVo) + Constant.Number.ONE;
                        }
                    }
                } else {
                    itemVo.setIndexRanking(rank + Constant.Number.ONE);
                }
            }
        }
    }

    /**
     * 计算同比
     *
     * @param businessReportDayItemVos
     * @param businessReportDayDto
     * @Author luoY
     */
    private void calculationYearCompare(List<BusinessReportDayItemVo> businessReportDayItemVos, BusinessReportDayDto businessReportDayDto) {
        //同比,时间减一年
        BusinessReportDayDto businessReportDayDto1 = new BusinessReportDayDto();
        businessReportDayDto1.setEnteId(businessReportDayDto.getEnteId());
        businessReportDayDto1.setShopIdList(businessReportDayDto.getShopIdList());
        businessReportDayDto1.setShopTypeIdList(businessReportDayDto.getShopTypeIdList());
        businessReportDayDto1.setDateType(businessReportDayDto.getDateType());
        businessReportDayDto1.setBeginDate(DateUtils.subYears(businessReportDayDto.getBeginDate(), Constant.Number.ONE));
        businessReportDayDto1.setEndDate(DateUtils.subYears(businessReportDayDto.getEndDate(), Constant.Number.ONE));
        //计算金额
        calculationBusinessAnalysisCurrentMoney(businessReportDayItemVos, businessReportDayDto1,
                ReportDataConstant.BusinessAnalysisDataType.YEARCOMPARE);
    }

    /**
     * 计算环比
     *
     * @param businessReportDayItemVos
     * @param businessReportDayDto
     * @Author luoY
     */
    private void calculationMonthCompare(List<BusinessReportDayItemVo> businessReportDayItemVos, BusinessReportDayDto businessReportDayDto) {
        //环比，时间减一个月
        List<Date> dates = DateUtils.getLastPeriodDate(businessReportDayDto.getBeginDate(), businessReportDayDto.getEndDate(), businessReportDayDto.getDateType());
        BusinessReportDayDto businessReportDayDto1 = new BusinessReportDayDto();
        businessReportDayDto1.setEnteId(businessReportDayDto.getEnteId());
        businessReportDayDto1.setShopIdList(businessReportDayDto.getShopIdList());
        businessReportDayDto1.setShopTypeIdList(businessReportDayDto.getShopTypeIdList());
        businessReportDayDto1.setDateType(businessReportDayDto.getDateType());
        businessReportDayDto1.setBeginDate(dates.get(Constant.Number.ZERO));
        businessReportDayDto1.setEndDate(dates.get(Constant.Number.ONE));
        //计算金额
        calculationBusinessAnalysisCurrentMoney(businessReportDayItemVos, businessReportDayDto1,
                ReportDataConstant.BusinessAnalysisDataType.MONTHCOMPARE);
    }


    /**
     * @return void
     * @Author ZhuHC
     * @Date 2019/12/19 9:53
     * @Param [shopScoreVoList]
     * @Description 计算门店下所有项目得分之和  -- 总分
     */
    private void getItemSumScore(List<ShopScoreVo> shopScoreVoList, boolean flag) {
        //各项目 评分 根据门店分组
        Map<String, List<ShopScoreVo>> listMap = shopScoreVoList.stream().collect(Collectors.groupingBy(ShopScoreVo::getShopId));
        //计算 各门店 所有项目总分
        List<ShopScoreVo> totalScoreList = new ArrayList<>();
        listMap.forEach((key, value) -> {
            if (!FastUtils.checkNullOrEmpty(value)) {
                ShopScoreVo shopScoreVo = new ShopScoreVo();
                FastUtils.copyProperties(value.get(0), shopScoreVo);
                //得分  各项目得分总和
                shopScoreVo.setScore(value.stream().map(ShopScoreVo::getScore).reduce(BigDecimal.ZERO, BigDecimal::add));
                //是否需要计算 上期 去年同期总分
                if (flag) {
                    Stream<BigDecimal> sumScore = value.stream().map(ShopScoreVo::getScorePrior);
                    //上期
                    shopScoreVo.setScorePrior(value.stream().map(vo -> {
                        if (vo.getScorePrior() != null) {
                            return vo.getScorePrior();
                        } else {
                            return BigDecimal.ZERO;
                        }
                    }).reduce(BigDecimal.ZERO, BigDecimal::add));
                    //去年同期
                    shopScoreVo.setScoreLastYear(value.stream().map(vo -> {
                        if (vo.getScoreLastYear() != null) {
                            return vo.getScoreLastYear();
                        } else {
                            return BigDecimal.ZERO;
                        }
                    }).reduce(BigDecimal.ZERO, BigDecimal::add));
                }
                //项目类型ID
                shopScoreVo.setTypeId(Constant.BusinessScore.SCORE_SUM_TYPE);
                shopScoreVo.setType(Constant.BusinessScore.SCORE_SUM_TYPE);
                //项目 分类 名称
                shopScoreVo.setItemName(Constant.BusinessScore.SCORE_SUM);
                totalScoreList.add(shopScoreVo);
            }
        });
        shopScoreVoList.addAll(totalScoreList);
    }

    /**
     * @return void
     * @Description //设置期间评分   true设置月  false设置年
     * @Author jds
     * @Date 2019/11/19 17:34
     * @Param [list, listParam, bool :true设置上期、环比  false设置去年同期、同比]
     **/
    private void setScore(List<ShopScoreVo> list, List<ShopScoreVo> listParam, boolean bool) {
        //本期单门店项目打分集合
        List<ShopScoreVo> psList = new ArrayList<>();
        //本期 门店：项目
        Map<String, String> itemId = new HashMap<>();
        String shopId = null;
        //设置期间评分 循环获取本期单门店项目打分集合
        for (ShopScoreVo shopScoreVo : list) {
            if (StringUtil.isBlank(shopScoreVo.getShopId())) {
                continue;
            }
            //门店第一次进入
            if (psList.size() == Constant.Number.ZERO) {
                psList.add(shopScoreVo);
                shopId = shopScoreVo.getShopId();
            } else if (shopScoreVo.getShopId().equals(shopId)) {
                //当前项目打分门店和上一个项目是同一个门店时将该数据添加到单门店项目打分集合
                psList.add(shopScoreVo);
            } else if (!shopScoreVo.getShopId().equals(shopId)) {
                //当前数据门店和上一个门店不同时 处理上一个门店项目打分集合
                getItemScore(psList, itemId, listParam, bool);
                shopId = shopScoreVo.getShopId();
                //清空单门店项目打分集合
                psList.clear();
            }
            if (list.indexOf(shopScoreVo) == list.size() - 1) {
                //当前数据是最后一条数据时处理上一个门店项目打分集合
                getItemScore(psList, itemId, listParam, bool);
            }
        }
    }

    /**
     * @return void
     * @Description //获取单门店项目打分数据
     * @Author jds
     * @Date 2019/11/26 11:40
     * @Param [psList：本期单门店项目打分集合, itemId：本期--门店：项目, addList：本期单门店不存在的项目打分集合, listParam：非本期数据,  bool]
     **/
    private void getItemScore(List<ShopScoreVo> psList, Map<String, String> itemId, List<ShopScoreVo> listParam, boolean bool) {
        //设置门店：项目id  的map
        psList.forEach(data -> itemId.put(data.getShopId() + Constant.Character.COLON + data.getTypeId(), data.getTypeId()));
        for (ShopScoreVo shopScoreVo : psList) {
            for (ShopScoreVo shopScorePrior : listParam) {
                //相同项目 相同门店 扔参数
                if (shopScoreVo.getTypeId().equals(shopScorePrior.getTypeId()) && shopScoreVo.getShopId().equals(shopScorePrior.getShopId())) {
                    if (bool) {
                        //设置上期
                        shopScoreVo.setScorePrior(shopScorePrior.getScore());
                        //设置环比
                        shopScoreVo.setLinkRatio(calculatedFrom(shopScoreVo.getScore(), shopScorePrior.getScore()));
                    } else {
                        //设置去年同期
                        shopScoreVo.setScoreLastYear(shopScorePrior.getScore());
                        //设置同比
                        shopScoreVo.setOverYear(calculatedFrom(shopScoreVo.getScore(), shopScorePrior.getScore()));
                    }
                } else if (shopScoreVo.getShopId().equals(shopScorePrior.getShopId()) && !itemId.containsKey(shopScorePrior.getShopId() + Constant.Character.COLON + shopScorePrior.getTypeId())) {
                    //设置本期不存在项目基础数据
                    ShopScoreVo shopScoreVo1 = new ShopScoreVo();
                    shopScoreVo1.setShopName(shopScorePrior.getShopName());
                    shopScoreVo1.setItemName(shopScorePrior.getItemName());
                    shopScoreVo1.setShopId(shopScorePrior.getShopId());
                    shopScoreVo1.setTypeId(shopScorePrior.getTypeId());
                    if (bool) {
                        //设置上期
                        shopScoreVo1.setScorePrior(shopScorePrior.getScore());
                    } else {
                        //设置去年同期
                        shopScoreVo1.setScoreLastYear(shopScorePrior.getScore());
                    }
                }
            }
        }
    }

    /**
     * @return java.math.BigDecimal
     * @Description //计算  环比、同比
     * @Author jds
     * @Date 2019/11/19 17:55
     * @Param [currentMoney, old] 本期  上期/去年同期
     **/
    private BigDecimal calculatedFrom(BigDecimal currentMoney, BigDecimal old) {
        BigDecimal result = null;
        if (currentMoney == null || old == null) {
            return null;
        }
        if (old.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
            if (currentMoney.compareTo(old) == Constant.Number.ZERO) {
                result = new BigDecimal(Constant.Number.ZERO);
            } else {
                result = currentMoney.subtract(old).divide(old, Constant.Number.FOUR, ROUND_HALF_UP).multiply(BigDecimal.valueOf(Constant.Number.ONEHUNDRED));
            }
        }
        return result;
    }

    /**
     * @return void
     * @Description //分数排名
     * @Author jds
     * @Date 2019/11/22 10:20
     * @Param [listAll 集团数据, rankMap 排名 ,typeMap 项目 ,map 需要排名的区域或品牌, code : ente集团排名 region区域排名  brand品牌排名 ]
     **/
    private void setRank(List<ShopScoreVo> listAll, Map<String, Integer> rankMap, Map<String, String> typeMap, Map<String, String> map, String code) {
        Map<String, String> sign = new HashMap<>();
        sign.put(Constant.Report.TYPEID, null);
        sign.put(Constant.Report.BRANDID, null);
        sign.put(Constant.Report.REGIONID, null);
        sign.put(Constant.Report.ENTEID, null);
        List<ShopScoreVo> shopScoreVos = new ArrayList<>();
        //集团排名，则重新对list排序
        if (Constant.Report.ENTEID.equals(code)) {
            Collections.sort(listAll, Comparator.comparing(ShopScoreVo::getType));
        }
        for (ShopScoreVo shopScoreVo : listAll) {
            //判断该项目是否需要排名
            if (!typeMap.containsKey(shopScoreVo.getTypeId())) {
                continue;
            }
            //判断区域排名时该区域是否需要排名
            if (Constant.Report.REGIONID.equals(code)) {
                if (!map.containsKey(shopScoreVo.getRegionId())) {
                    continue;
                }
            }
            //判断品牌排名时该区域是否需要排名
            if (Constant.Report.BRANDID.equals(code)) {
                if (!map.containsKey(shopScoreVo.getBrandId())) {
                    continue;
                }
            }
            //第一次循环
            if (StringUtil.isEmpty(sign.get(Constant.Report.TYPEID))) {
                shopScoreVos.add(shopScoreVo);
                sign.put(Constant.Report.TYPEID, shopScoreVo.getTypeId());
                sign.put(Constant.Report.BRANDID, shopScoreVo.getBrandId());
                sign.put(Constant.Report.REGIONID, shopScoreVo.getRegionId());
                sign.put(Constant.Report.ENTEID, shopScoreVo.getEnteId());
                sign.put(Constant.Report.TYPE, shopScoreVo.getType());
            } else if (Constant.Report.ENTEID.equals(code)) {
                //判断是否是企业排名  与上一个项目相同则添加至排名集合
                //if (shopScoreVo.getTypeId().equals(sign.get(Constant.Report.TYPEID)) && shopScoreVo.getEnteId().equals(sign.get(Constant.Report.ENTEID))) {
                if (shopScoreVo.getType().equals(sign.get(Constant.Report.TYPE)) && shopScoreVo.getEnteId().equals(sign.get(Constant.Report.ENTEID))) {
                    shopScoreVos.add(shopScoreVo);
                } else {
                    //项目不同则开始对之前的排名集合排名
                    getRank(shopScoreVos, rankMap, shopScoreVo, sign);
                    //排名后清空排名集合
                    shopScoreVos.clear();
                    shopScoreVos.add(shopScoreVo);
                }
            } else if (Constant.Report.REGIONID.equals(code)) {
                //判断是否是区域排名  与上一个区域、项目相同则添加至排名集合
                if (shopScoreVo.getTypeId().equals(sign.get(Constant.Report.TYPEID)) && shopScoreVo.getRegionId().equals(sign.get(Constant.Report.REGIONID))) {
                    shopScoreVos.add(shopScoreVo);
                } else {
                    //否则对之前的排名集合排名
                    getRank(shopScoreVos, rankMap, shopScoreVo, sign);
                    //排名后清空排名集合
                    shopScoreVos.clear();
                    shopScoreVos.add(shopScoreVo);
                }
            } else if (Constant.Report.BRANDID.equals(code)) {
                //判断是否是品牌排名  与上一个品牌、项目相同则添加至排名集合
                if (shopScoreVo.getTypeId().equals(sign.get(Constant.Report.TYPEID)) && shopScoreVo.getBrandId().equals(sign.get(Constant.Report.BRANDID))) {
                    shopScoreVos.add(shopScoreVo);
                } else {
                    //否则对之前的排名集合排名
                    getRank(shopScoreVos, rankMap, shopScoreVo, sign);
                    //排名后清空排名集合
                    shopScoreVos.clear();
                    shopScoreVos.add(shopScoreVo);
                }
            }
            //判断是否是最后一条数据 是则排名
            if ((listAll.indexOf(shopScoreVo) + Constant.Number.ONE) == listAll.size()) {
                getRank(shopScoreVos, rankMap, shopScoreVo, sign);
            }
        }
    }

    /**
     * @return void
     * @Description //获取排名集合排名
     * @Author jds
     * @Date 2019/11/22 16:04
     * @Param [psItemScoreVos, rankMap, psItemScoreVo, sign]
     **/
    private void getRank(List<ShopScoreVo> shopScoreVos, Map<String, Integer> rankMap, ShopScoreVo shopScoreVo, Map<String, String> sign) {
        //根据分数排序
        shopScoreVos.sort(Comparator.comparing(ShopScoreVo::getScore).reversed());
        //上次排名
        int currentMoney = Constant.Number.ONE;
        //上次分数
        BigDecimal currentMoneyScore = BigDecimal.ZERO;
        //排名次
        for (ShopScoreVo shopScoreVo1 : shopScoreVos) {
            //如果当前分数等于上个项目分数，则排名一样
            if (shopScoreVo1.getScore().equals(currentMoneyScore)) {
                //排名放入map
                rankMap.put(shopScoreVo1.getShopId() + Constant.Character.COLON + shopScoreVo1.getTypeId(), currentMoney);
            } else {
                //如果当前项目分数不等于上个项目分数，排名等于当前坐标+1
                rankMap.put(shopScoreVo1.getShopId() + Constant.Character.COLON + shopScoreVo1.getTypeId(), shopScoreVos.indexOf(shopScoreVo1) + Constant.Number.ONE);
                currentMoney = shopScoreVos.indexOf(shopScoreVo1) + Constant.Number.ONE;
            }
            currentMoneyScore = shopScoreVo1.getScore();
        }
        sign.put(Constant.Report.TYPEID, shopScoreVo.getTypeId());
        sign.put(Constant.Report.BRANDID, shopScoreVo.getBrandId());
        sign.put(Constant.Report.REGIONID, shopScoreVo.getRegionId());
        sign.put(Constant.Report.ENTEID, shopScoreVo.getEnteId());
        sign.put(Constant.Report.TYPE, shopScoreVo.getType());
    }

    /**
     * @return java.util.Date
     * @Description //获取上期日期
     * @Author jds
     * @Date 2019/11/28 14:02
     * @Param [date1, days, dateType]
     **/
    private Date findLastDate(Date date1, int days, Byte dateType) {
        Date lastDate = new Date();
        switch (dateType) {
            //日
            case Constant.DateType.DAY:
                lastDate = DateUtils.subDays(date1, Constant.Number.ONE);
                break;
            //星期
            case Constant.DateType.WEEK:
                lastDate = DateUtils.subWeeks(date1, Constant.Number.ONE);
                break;
            //月
            case Constant.DateType.MONTH:
                lastDate = DateUtils.subMonths(date1, Constant.Number.ONE);
                break;
            //年
            case Constant.DateType.SEASON:
                lastDate = DateUtils.subYears(date1, Constant.Number.ONE);
                break;
            //自定义
            case Constant.DateType.CUSTOMIZE:
                lastDate = DateUtils.subDays(date1, days);
                break;
            default:
                break;
        }
        return lastDate;
    }



    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/3/2 16:33
     * @Param [baseQueryDto, response]
     * @Description 翻台率报表导出
     */
    @Override
    public void exportStatisticsExcel(ExcelExportDto excelExportDto, HttpServletResponse response) {
        excelExportDto.setOrgName(baseShopService.getOrgName(excelExportDto));
        BaseQueryDto baseQueryDto = new BaseQueryDto();
        baseQueryDto.setShopIdList(excelExportDto.getShopIdList());
        baseQueryDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        baseQueryDto.setEnteId(excelExportDto.getEnteId());
        baseQueryDto.setBeginDate(excelExportDto.getBeginDate());
        baseQueryDto.setEndDate(excelExportDto.getEndDate());
        baseQueryDto.setDateType(excelExportDto.getDateType());
        List<StatisticsTurnoverRateVo> statisticsTurnoverRateVoList = findStatisticsTurnoverRate(baseQueryDto);
        fileService.exportExcelForQueryTerm(response, excelExportDto, statisticsTurnoverRateVoList,
                ExcelColumnConstant.StatisticsTurnoverRateInfo.BRANDE_NAME,
                ExcelColumnConstant.StatisticsTurnoverRateInfo.REGION_NAME,
                ExcelColumnConstant.StatisticsTurnoverRateInfo.SHOP_NAME,
                ExcelColumnConstant.StatisticsTurnoverRateInfo.DESK_NUM,
                ExcelColumnConstant.StatisticsTurnoverRateInfo.STATION_SNUM,
                ExcelColumnConstant.StatisticsTurnoverRateInfo.STATISTICS_TURNOVER_PERCENTAGE);
    }

    /**
     * @return void
     * @Author ljc
     * @Date 2020/3/3
     * @Param [excelExportDto, response]
     * @Description
     */
    @Override
    public void exportScoreSummary(HttpServletResponse response, ExcelExportDto excelExportDto) {
        ShopScoreDto shopScoreDto = new ShopScoreDto();
        shopScoreDto.setBeginDate(excelExportDto.getBeginDate());
        shopScoreDto.setEndDate(excelExportDto.getEndDate());
        shopScoreDto.setDateType(excelExportDto.getDateType());
        shopScoreDto.setShopIdList(excelExportDto.getShopIdList());
        shopScoreDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        shopScoreDto.setEnteId(excelExportDto.getEnteId());
        ShopScoreVo shopScoreVo = this.findScoreSummary(shopScoreDto);
        String searchType = excelExportDto.getType() != null ? excelExportDto.getType() : Constant.Character.NULL_VALUE;
        List<ShopScoreVo> shopScoreVoList = null;
        //searchType=project 标识按总分收起；searchType!=project全部展示状态
        if (!"project".equals(searchType) && shopScoreVo.getItemScoreList() != null) {
            shopScoreVoList = shopScoreVo.getItemScoreList();
        } else if ("project".equals(searchType) && shopScoreVo.getItemSumScoreList() != null) {
            shopScoreVoList = shopScoreVo.getItemSumScoreList();
        }
        if (shopScoreVoList != null && shopScoreVoList.size() > 0) {
            fileService.exportExcelForQueryTerm(response, excelExportDto, shopScoreVoList,
                    ExcelColumnConstant.ShopScoreSummaryInfo.BRANDE_NAME,
                    ExcelColumnConstant.ShopScoreSummaryInfo.REGION_NAME,
                    ExcelColumnConstant.ShopScoreSummaryInfo.SHOP_NAME,
                    ExcelColumnConstant.ShopScoreSummaryInfo.SHOP_PERSON_NUM,
                    ExcelColumnConstant.ShopScoreSummaryInfo.ITEM_NAME,
                    ExcelColumnConstant.ShopScoreSummaryInfo.SCORE,
                    ExcelColumnConstant.ShopScoreSummaryInfo.RANK_REGION,
                    ExcelColumnConstant.ShopScoreSummaryInfo.RANK_BRAND,
                    ExcelColumnConstant.ShopScoreSummaryInfo.RANK_ENTE
            );
        }
    }

    /**
     * @return void
     * @Author ljc
     * @Date 2020/3/3
     * @Param [shopScoreDto, response]
     * @Description
     */
    @Override
    public void exportScoreContrast(HttpServletResponse response, ExcelExportDto excelExportDto) {
        ShopScoreDto shopScoreDto = new ShopScoreDto();
        shopScoreDto.setBeginDate(excelExportDto.getBeginDate());
        shopScoreDto.setEndDate(excelExportDto.getEndDate());
        shopScoreDto.setDateType(excelExportDto.getDateType());
        shopScoreDto.setShopIdList(excelExportDto.getShopIdList());
        shopScoreDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        shopScoreDto.setEnteId(excelExportDto.getEnteId());
        ShopScoreVo shopScoreVo = this.findScoreContrast(shopScoreDto);
        String searchType = excelExportDto.getType() != null ? excelExportDto.getType() : Constant.Character.NULL_VALUE;
        List<ShopScoreVo> shopScoreVoList = null;
        //searchType=project 标识按总分收起；searchType!=project全部展示状态
        if (!"project".equals(searchType) && shopScoreVo.getItemScoreList() != null) {
            shopScoreVoList = shopScoreVo.getItemScoreList();
        } else if ("project".equals(searchType) && shopScoreVo.getItemSumScoreList() != null) {
            shopScoreVoList = shopScoreVo.getItemSumScoreList();
        }
        if (shopScoreVoList != null && shopScoreVoList.size() > 0) {
            fileService.exportExcelForQueryTerm(response, excelExportDto, shopScoreVoList,
                    ExcelColumnConstant.ShopScoreContrastInfo.BRANDE_NAME,
                    ExcelColumnConstant.ShopScoreContrastInfo.REGION_NAME,
                    ExcelColumnConstant.ShopScoreContrastInfo.SHOP_NAME,
                    ExcelColumnConstant.ShopScoreContrastInfo.ITEM_NAME,
                    ExcelColumnConstant.ShopScoreContrastInfo.SCORE,
                    ExcelColumnConstant.ShopScoreContrastInfo.SCORE_PRIOR,
                    ExcelColumnConstant.ShopScoreContrastInfo.RANK_REGION,
                    ExcelColumnConstant.ShopScoreContrastInfo.RANK_BRAND,
                    ExcelColumnConstant.ShopScoreContrastInfo.RANK_ENTE
            );
        }
    }

    /**
     * @Description: 经营日报表导出
     * @Param: [response, excelExportDto]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/19 11:20
     */
    @Override
    public void exportBusinessReportDay(HttpServletResponse response, ExcelExportDto excelExportDto) {
        BusinessReportDayDto businessReportDayDto = new BusinessReportDayDto();
        businessReportDayDto.setEnteId(excelExportDto.getEnteId());
        businessReportDayDto.setBeginDate(excelExportDto.getBeginDate());
        businessReportDayDto.setEndDate(excelExportDto.getEndDate());
        businessReportDayDto.setShopIdList(excelExportDto.getShopIdList());
        businessReportDayDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        businessReportDayDto.setDateType(excelExportDto.getDateType());
        List<BusinessReportDayItemVo> businessReportDayItemVos = findBusinessReportDay(businessReportDayDto, ReportDataConstant.ReportItemReportId.BUSINESSREPORTDAY);
        if (!FastUtils.checkNullOrEmpty(businessReportDayItemVos)) {
            //表头
            List<TitleEntity> titleList = setQueryInfoTitle(excelExportDto);
            List<Map<String, Object>> rowList = new ArrayList<>();
            //加入千分位，保留2位小数，并且不够补0
            DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
            DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
            for (BusinessReportDayItemVo data : businessReportDayItemVos) {
                Map<String, Object> m = new HashMap<>();
                m.put("itemNumber", data.getItemNumber());
                if (!FastUtils.checkNullOrEmpty(data.getItemName())) {
                    m.put("itemName", data.getItemName());
                    m.put("currentMoney", data.getDataType().equals(Constant.DataType.PERCENT) ?
                            FastUtils.Null2Zero(data.getCurrentMoney()).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                                    Constant.Character.MIDDLE_LINE : FastUtils.Null2Zero(data.getCurrentMoney()).toString() + Constant.Character.Percent :
                            data.getDataType().equals(Constant.DataType.MONEY) ?
                                    getNotZeroByConvert(FastUtils.Null2Zero(data.getCurrentMoney()), df) :
                                    getNotZeroByConvert(FastUtils.Null2Zero(data.getCurrentMoney()), df2));
                    m.put("index", data.getIndex() == null ? Constant.Character.MIDDLE_LINE : data.getDataType().equals(Constant.DataType.PERCENT) ? data.getIndex() + Constant.Character.Percent : data.getIndex());
                    m.put("completionIndex", FastUtils.Null2Zero(data.getCompletionIndex()).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? Constant.Character.MIDDLE_LINE : FastUtils.Null2Zero(data.getCompletionIndex()).toString() + Constant.Character.Percent);
//                    m.put("indexRanking", FastUtils.Null2Zero(data.getIndexRanking()).equals(Constant.Number.ZERO) ? Constant.Character.MIDDLE_LINE : data.getIndexRanking());
                    m.put("yearCompare", FastUtils.Null2Zero(data.getYearCompare()).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? Constant.Character.MIDDLE_LINE : FastUtils.Null2Zero(data.getYearCompare()).toString() + Constant.Character.Percent);
                    m.put("monthCompare", FastUtils.Null2Zero(data.getMonthCompare()).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? Constant.Character.MIDDLE_LINE : FastUtils.Null2Zero(data.getMonthCompare()).toString() + Constant.Character.Percent);
                } else {
                    m.put("itemName", data.getItemName());
                    m.put("currentMoney", null);
                    m.put("index", null);
                    m.put("completionIndex", null);
//                    m.put("indexRanking", null);
                    m.put("yearCompare", null);
                    m.put("monthCompare", null);
                }
                rowList.add(m);
            }
            deskTypeAnalysisService.exportMethod(response, titleList, rowList);
        }
    }


    /**
     * @Description: 初始化
     * @Param: [excelExportDto]
     * @return: java.util.List<com.njwd.poiexcel.TitleEntity>
     * @Author: LuoY
     * @Date: 2020/3/20 10:02
     */
    private List<TitleEntity> setQueryInfoTitle(ExcelExportDto excelExportDto) {
        List<TitleEntity> titleList = new ArrayList<>();
        //空白
        TitleEntity titleEntity0 = new TitleEntity("0", null, null, null);
        //查询条件-菜单
        TitleEntity titleEntity1 = new TitleEntity("1", "0", excelExportDto.getMenuName(), null);
        //组织
        TitleEntity titleEntity2 = new TitleEntity("2", "1", ReportDataConstant.ExcelExportInfo.ORGNAME + Constant.Character.COLON + excelExportDto.getOrgTree(), null);
        //期间
        TitleEntity titleEntity3 = new TitleEntity("3", "2", ReportDataConstant.ExcelExportInfo.DATEPERIOD + Constant.Character.COLON + DateUtils.dateConvertString(excelExportDto.getBeginDate(), DateUtils.PATTERN_DAY) + Constant.Character.MIDDLE_WAVE
                + DateUtils.dateConvertString(excelExportDto.getEndDate(), DateUtils.PATTERN_DAY), null);
        //店铺类型
        TitleEntity titleEntity4 = new TitleEntity("4", "3", ReportDataConstant.ExcelExportInfo.SHOPTYPE + Constant.Character.COLON + excelExportDto.getShopTypeName(), null);
        //下载时间
        TitleEntity titleEntity5 = new TitleEntity("5", "4", ReportDataConstant.ExcelExportInfo.DOWNLOAD_TIME + Constant.Character.COLON + DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND), null);
        //空白
        TitleEntity titleEntity7 = new TitleEntity("6", "5", null, null);
        TitleEntity titleEntity8 = new TitleEntity("7", "6", null, null);
        TitleEntity titleEntity9 = new TitleEntity("8", "7", null, null);
        //人数区间
        TitleEntity titleEntity6 = new TitleEntity("9", "8", "序号", "itemNumber");
        TitleEntity titleEntity10 = new TitleEntity("10", "8", "项目", "itemName");
        TitleEntity titleEntity11 = new TitleEntity("11", "8", "本期发生", "currentMoney");
        TitleEntity titleEntity12 = new TitleEntity("12", "8", "指标", "index");
        TitleEntity titleEntity13 = new TitleEntity("13", "8", "指标完成率", "completionIndex");
//        TitleEntity titleEntity14 = new TitleEntity("14", "8", "指标完成排名", "indexRanking");
        TitleEntity titleEntity15 = new TitleEntity("14", "8", "同比", "yearCompare");
        TitleEntity titleEntity16 = new TitleEntity("15", "8", "环比", "monthCompare");
        titleList.add(titleEntity0);
        titleList.add(titleEntity1);
        titleList.add(titleEntity2);
        titleList.add(titleEntity3);
        titleList.add(titleEntity4);
        titleList.add(titleEntity5);
        titleList.add(titleEntity6);
        titleList.add(titleEntity7);
        titleList.add(titleEntity8);
        titleList.add(titleEntity9);
        titleList.add(titleEntity10);
        titleList.add(titleEntity11);
        titleList.add(titleEntity12);
        titleList.add(titleEntity13);
//        titleList.add(titleEntity14);
        titleList.add(titleEntity15);
        titleList.add(titleEntity16);
        return titleList;
    }

    /**
     * @Description: 参数格式化
     * @Param: [value, df]
     * @return: java.lang.String
     * @Author: LuoY
     * @Date: 2020/3/20 10:01
     */
    private String getNotZeroByConvert(Object value, DecimalFormat df) {
        String strValue;
        if (Constant.Character.String_ZERO.equals(String.valueOf(value)) || Constant.Character.String_ZEROB.equals(String.valueOf(value))) {
            strValue = Constant.Character.MIDDLE_LINE;
        } else {
            strValue = df.format(value);
        }
        return strValue;
    }
}

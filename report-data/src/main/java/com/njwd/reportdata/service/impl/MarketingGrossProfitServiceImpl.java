package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.MarketingGrossProfitDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.ScmQueryDto;
import com.njwd.entity.reportdata.vo.MarketingGrossProfitVo;
import com.njwd.entity.reportdata.vo.scm.DishGrossProfitVo;
import com.njwd.report.service.impl.FootStyleAnalysisServiceImpl;
import com.njwd.reportdata.mapper.BusinessAnalysisMapper;
import com.njwd.reportdata.service.FootStyleAnalysisService;
import com.njwd.reportdata.service.MarketingGrossProfitService;
import com.njwd.service.FileService;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import com.njwd.utils.MergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author lj
 * @Description 营销活动毛利统计表service
 * @Date:9:50 2020/3/30
 **/
@Service
public class MarketingGrossProfitServiceImpl implements MarketingGrossProfitService {

    @Autowired
    private FileService fileService;

    @Autowired
    private FootStyleAnalysisService footStyleAnalysisService;

    @Resource
    private BusinessAnalysisMapper businessAnalysisMapper;

    Logger logger = LoggerFactory.getLogger(FootStyleAnalysisServiceImpl.class);

    /**
     * 营销活动毛利统计表
     *
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.MarketingGrossProfitVo>
     * @Author lj
     * @Date:16:48 2020/3/26
     **/
    @Override
    public List<MarketingGrossProfitVo> findMarketingGrossProfit(MarketingGrossProfitDto queryDto) {
        //查询本期支付明细信息
        List<MarketingGrossProfitVo> list = businessAnalysisMapper.findRepPosPayList(queryDto);

        //查询本期支付菜品信息
        List<MarketingGrossProfitVo> payFoodList = businessAnalysisMapper.findRepPosPayFoodList(queryDto);

        //计算菜品成本
        ScmQueryDto scmQueryDto = new ScmQueryDto();
        FastUtils.copyProperties(queryDto,scmQueryDto);
//        List<SimpleFoodDto> foods =new ArrayList<>();
//        payFoodList.stream().forEach(data->{
//            SimpleFoodDto a = new SimpleFoodDto();
//            FastUtils.copyProperties(data,a);
//            foods.add(a);
//        });
//        scmQueryDto.setFoods(foods);
        List<DishGrossProfitVo> dishGrossProfitVos = footStyleAnalysisService.getDishBaseList(scmQueryDto,null);
        long start = System.currentTimeMillis();
        MergeUtil.merge(payFoodList,dishGrossProfitVos,(t -> t.getShopId()+Constant.Character.COMMA+
                        t.getFoodNo()+Constant.Character.COMMA+
                        t.getFoodName()),((s -> s.getShopId()+Constant.Character.COMMA+
                        s.getFoodNo()+Constant.Character.COMMA+
                        s.getFoodName())),
                (t, s) -> {
                    t.setMoneyCost(t.getFoodNum().multiply(FastUtils.Null2Zero(s.getCostPrice())));
                });

        Map<String,List<MarketingGrossProfitVo>> payFoodListMap = payFoodList.stream().collect(Collectors.groupingBy(t->t.getShopId()+Constant.Character.COMMA
                +t.getPayTypeId()));
        //融合支付明细数据得到成本信息和毛利信息
        list.forEach(data->{
            if(payFoodListMap.get(data.getShopId()+Constant.Character.COMMA
                    +data.getPayTypeId())!=null){
                BigDecimal moneyCost= payFoodListMap.get(data.getShopId()+Constant.Character.COMMA
                        +data.getPayTypeId()).stream().map(v -> v.getMoneyCost() == null ? BigDecimal.ZERO : v.getMoneyCost()).
                        reduce(BigDecimal.ZERO, BigDecimal::add);
                data.setMoneyCost(moneyCost);
                data.setGrossProfit(data.getMoneyActual().subtract(data.getMoneyCost()));
            }
        });

        //环比，时间减一个月
        List<Date> dates = DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
        MarketingGrossProfitDto monthQueryDto = new MarketingGrossProfitDto();
        monthQueryDto.setEnteId(queryDto.getEnteId());
        monthQueryDto.setShopIdList(queryDto.getShopIdList());
        monthQueryDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        monthQueryDto.setDateType(queryDto.getDateType());
        monthQueryDto.setBeginDate(dates.get(Constant.Number.ZERO));
        monthQueryDto.setEndDate(dates.get(Constant.Number.ONE));
        //查询上期支付明细信息
        List<MarketingGrossProfitVo> monthList = businessAnalysisMapper.findRepPosPayList(monthQueryDto);

        //查询上期支付菜品信息
        List<MarketingGrossProfitVo> monthPayFoodList = businessAnalysisMapper.findRepPosPayFoodList(monthQueryDto);

        //计算菜品成本
        ScmQueryDto monthScmQueryDto = new ScmQueryDto();
        FastUtils.copyProperties(monthQueryDto,monthScmQueryDto);
//        List<SimpleFoodDto> monthFoods =new ArrayList<>();
//        monthPayFoodList.stream().forEach(data->{
//            SimpleFoodDto a = new SimpleFoodDto();
//            FastUtils.copyProperties(data,a);
//            monthFoods.add(a);
//        });
//        monthScmQueryDto.setFoods(monthFoods);
        List<DishGrossProfitVo> monthDishGrossProfitVos = footStyleAnalysisService.getDishBaseList(monthScmQueryDto,null);
        MergeUtil.merge(monthPayFoodList,monthDishGrossProfitVos,(t -> t.getShopId()+Constant.Character.COMMA+
                        t.getFoodNo()+Constant.Character.COMMA+
                        t.getFoodName()),((s -> s.getShopId()+Constant.Character.COMMA+
                        s.getFoodNo()+Constant.Character.COMMA+
                        s.getFoodName())),
                (t, s) -> {
                    t.setMonthMoneyCost(t.getFoodNum().multiply(FastUtils.Null2Zero(s.getCostPrice())));
                });

        //融合支付明细数据得到成本信息和毛利信息
        Map<String,List<MarketingGrossProfitVo>> monthPayFoodListMap = monthPayFoodList.stream().collect(Collectors.groupingBy(t->t.getShopId()+Constant.Character.COMMA
                +t.getPayTypeId()));
        //融合支付明细数据得到成本信息和毛利信息
        monthList.forEach(data->{
            if(monthPayFoodListMap.get(data.getShopId()+Constant.Character.COMMA
                    +data.getPayTypeId())!=null){
                BigDecimal monthMoneyCost= monthPayFoodListMap.get(data.getShopId()+Constant.Character.COMMA
                        +data.getPayTypeId()).stream().map(v -> v.getMonthMoneyCost() == null ? BigDecimal.ZERO : v.getMonthMoneyCost()).
                        reduce(BigDecimal.ZERO, BigDecimal::add);
                data.setMonthMoneyCost(monthMoneyCost);
                data.setMonthMoneyActual(data.getMoneyActual());
                data.setMonthGrossProfit(data.getMoneyActual().subtract(data.getMonthMoneyCost()));
            }
        });

        //同比,时间减一年
        MarketingGrossProfitDto yearQueryDto = new MarketingGrossProfitDto();
        yearQueryDto.setEnteId(queryDto.getEnteId());
        yearQueryDto.setShopIdList(queryDto.getShopIdList());
        yearQueryDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        yearQueryDto.setDateType(queryDto.getDateType());
        List<Date> dateList = DateUtils.getLastYearDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
        if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
            yearQueryDto.setBeginDate(dateList.get(Constant.Number.ZERO));
            yearQueryDto.setEndDate(dateList.get(Constant.Number.ONE));
        }
        //查询去年同期支付明细信息
        List<MarketingGrossProfitVo> yearList = businessAnalysisMapper.findRepPosPayList(yearQueryDto);

        //查询去年同期支付菜品信息
        List<MarketingGrossProfitVo> yearPayFoodList = businessAnalysisMapper.findRepPosPayFoodList(yearQueryDto);

        //计算菜品成本
        ScmQueryDto yearScmQueryDto = new ScmQueryDto();
        FastUtils.copyProperties(yearQueryDto,yearScmQueryDto);
//        List<SimpleFoodDto> yearFoods =new ArrayList<>();
//        yearPayFoodList.stream().forEach(data->{
//            SimpleFoodDto a = new SimpleFoodDto();
//            FastUtils.copyProperties(data,a);
//            yearFoods.add(a);
//        });
//        yearScmQueryDto.setFoods(yearFoods);
        List<DishGrossProfitVo> yearDishGrossProfitVos = footStyleAnalysisService.getDishBaseList(yearScmQueryDto,null);
        MergeUtil.merge(yearPayFoodList,yearDishGrossProfitVos,(t -> t.getShopId()+Constant.Character.COMMA+
                        t.getFoodNo()+Constant.Character.COMMA+
                        t.getFoodName()),((s -> s.getShopId()+Constant.Character.COMMA+
                        s.getFoodNo()+Constant.Character.COMMA+
                        s.getFoodName())),
                (t, s) -> {
                    t.setYearMoneyCost(t.getFoodNum().multiply(FastUtils.Null2Zero(s.getCostPrice())));
                });

        Map<String,List<MarketingGrossProfitVo>> yearPayFoodListMap = yearPayFoodList.stream().collect(Collectors.groupingBy(t->t.getShopId()+Constant.Character.COMMA
                +t.getPayTypeId()));
        //融合支付明细数据得到成本信息和毛利信息
        yearList.forEach(data->{
            if(yearPayFoodListMap.get(data.getShopId()+Constant.Character.COMMA
                    +data.getPayTypeId())!=null){
                BigDecimal yearMoneyCost= yearPayFoodListMap.get(data.getShopId()+Constant.Character.COMMA
                        +data.getPayTypeId()).stream().map(v -> v.getYearMoneyCost() == null ? BigDecimal.ZERO : v.getYearMoneyCost()).
                        reduce(BigDecimal.ZERO, BigDecimal::add);
                data.setYearMoneyCost(yearMoneyCost);
                data.setYearMoneyActual(data.getMoneyActual());
                data.setYearGrossProfit(data.getMoneyActual().subtract(data.getYearMoneyCost()));
            }
        });

        //融合所有数据
        MergeUtil.merge(list,monthList,(t -> t.getShopId()+Constant.Character.COMMA+
                        t.getPayTypeId()),((s -> s.getShopId()+Constant.Character.COMMA+
                        s.getPayTypeId())),
                (vo, monthVo) -> {
                    vo.setMonthMoneyActual(monthVo.getMonthMoneyActual());
                    vo.setMonthMoneyCost(monthVo.getMonthMoneyCost());
                    vo.setMonthGrossProfit(monthVo.getMonthGrossProfit());
                });
        MergeUtil.merge(list,yearList,(t -> t.getShopId()+Constant.Character.COMMA+
                        t.getPayTypeId()),((s -> s.getShopId()+Constant.Character.COMMA+
                        s.getPayTypeId())),
                (vo, yearVo) -> {
                    vo.setYearMoneyActual(yearVo.getYearMoneyActual());
                    vo.setYearMoneyCost(yearVo.getYearMoneyCost());
                    vo.setYearGrossProfit(yearVo.getYearGrossProfit());
                });
        //计算同比和环比
        list.forEach(data->{
            data.setType(ReportDataConstant.Finance.TYPE_SHOP);
            // 收入同比
            calcRatio(data);
        });

        //按区域方式汇总
        Map<String,List<MarketingGrossProfitVo>> regionMap = list.stream().collect(Collectors.groupingBy(MarketingGrossProfitVo::getRegionId));

        //按品牌方式汇总
        Map<String,List<MarketingGrossProfitVo>> brandMap = list.stream().collect(Collectors.groupingBy(MarketingGrossProfitVo::getBrandId));

        List<MarketingGrossProfitVo> brandList = generateMarketingGrossProfit(brandMap,ReportDataConstant.Finance.TYPE_BRAND);

        List<MarketingGrossProfitVo> regionList = generateMarketingGrossProfit(regionMap,ReportDataConstant.Finance.TYPE_REGION);
        List<MarketingGrossProfitVo> allList = new ArrayList<>();
        allList.addAll(list);
        allList.addAll(regionList);
        allList.addAll(brandList);
        //全部数据合计
        List<MarketingGrossProfitVo> finTypeAll = getFinTypeAll(list,ReportDataConstant.Finance.TYPE_ALL);
        allList.addAll(finTypeAll);
        return allList;
    }

    /**
     * 营销活动毛利统计表
     *
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.MarketingGrossProfitVo>
     * @Author lj
     * @Date:16:48 2020/3/26
     **/
    @Override
    public List<MarketingGrossProfitVo> findMarketingGrossProfitNew(MarketingGrossProfitDto queryDto) {

        if(Constant.Number.TWO.equals(queryDto.getQueryType())){
            //环比，时间减一个月
            List<Date> dates = DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
            MarketingGrossProfitDto monthQueryDto = new MarketingGrossProfitDto();
            monthQueryDto.setEnteId(queryDto.getEnteId());
            monthQueryDto.setShopIdList(queryDto.getShopIdList());
            monthQueryDto.setShopTypeIdList(queryDto.getShopTypeIdList());
            monthQueryDto.setDateType(queryDto.getDateType());
            monthQueryDto.setBeginDate(dates.get(Constant.Number.ZERO));
            monthQueryDto.setEndDate(dates.get(Constant.Number.ONE));
            queryDto=monthQueryDto;
        }else if(Constant.Number.LENGTH.equals(queryDto.getQueryType())){
            MarketingGrossProfitDto yearQueryDto = new MarketingGrossProfitDto();
            yearQueryDto.setEnteId(queryDto.getEnteId());
            yearQueryDto.setShopIdList(queryDto.getShopIdList());
            yearQueryDto.setShopTypeIdList(queryDto.getShopTypeIdList());
            yearQueryDto.setDateType(queryDto.getDateType());
            List<Date> dateList = DateUtils.getLastYearDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
            yearQueryDto.setBeginDate(dateList.get(Constant.Number.ZERO));
            yearQueryDto.setEndDate(dateList.get(Constant.Number.ONE));
            queryDto=yearQueryDto;
        }

        //查询本期支付明细信息
        List<MarketingGrossProfitVo> list = businessAnalysisMapper.findRepPosPayList(queryDto);

        //查询本期支付菜品信息
        List<MarketingGrossProfitVo> payFoodList = businessAnalysisMapper.findRepPosPayFoodList(queryDto);

        //计算菜品成本
        ScmQueryDto scmQueryDto = new ScmQueryDto();
        FastUtils.copyProperties(queryDto,scmQueryDto);

        List<DishGrossProfitVo> dishGrossProfitVos = footStyleAnalysisService.getDishBaseList(scmQueryDto,null);
        MergeUtil.merge(payFoodList,dishGrossProfitVos,(t -> t.getShopId()+Constant.Character.COMMA+
                        t.getFoodNo()+Constant.Character.COMMA+
                        t.getFoodName()),((s -> s.getShopId()+Constant.Character.COMMA+
                        s.getFoodNo()+Constant.Character.COMMA+
                        s.getFoodName())),
                (t, s) -> {
                    t.setMoneyCost(t.getFoodNum().multiply(FastUtils.Null2Zero(s.getCostPrice())));
                });

        Map<String,List<MarketingGrossProfitVo>> payFoodListMap = payFoodList.stream().collect(Collectors.groupingBy(t->t.getShopId()+Constant.Character.COMMA
                +t.getPayTypeId()));
        //融合支付明细数据得到成本信息和毛利信息
        list.forEach(data->{
            data.setType(ReportDataConstant.Finance.TYPE_SHOP);
            if(payFoodListMap.get(data.getShopId()+Constant.Character.COMMA
                    +data.getPayTypeId())!=null){
                BigDecimal moneyCost= payFoodListMap.get(data.getShopId()+Constant.Character.COMMA
                        +data.getPayTypeId()).stream().map(v -> v.getMoneyCost() == null ? BigDecimal.ZERO : v.getMoneyCost()).
                        reduce(BigDecimal.ZERO, BigDecimal::add);
                data.setMoneyCost(moneyCost);
                data.setGrossProfit(data.getMoneyActual().subtract(data.getMoneyCost()));
            }
        });
        //按区域方式汇总
        Map<String,List<MarketingGrossProfitVo>> regionMap = list.stream().collect(Collectors.groupingBy(MarketingGrossProfitVo::getRegionId));

        //按品牌方式汇总
        Map<String,List<MarketingGrossProfitVo>> brandMap = list.stream().collect(Collectors.groupingBy(MarketingGrossProfitVo::getBrandId));

        List<MarketingGrossProfitVo> brandList = generateMarketingGrossProfit(brandMap,ReportDataConstant.Finance.TYPE_BRAND);

        List<MarketingGrossProfitVo> regionList = generateMarketingGrossProfit(regionMap,ReportDataConstant.Finance.TYPE_REGION);
        List<MarketingGrossProfitVo> allList = new ArrayList<>();
        allList.addAll(list);
        allList.addAll(regionList);
        allList.addAll(brandList);
        //全部数据合计
        List<MarketingGrossProfitVo> finTypeAll = getFinTypeAll(list,ReportDataConstant.Finance.TYPE_ALL);
        allList.addAll(finTypeAll);
        return allList;
    }

    private List<MarketingGrossProfitVo> getFinTypeAll(List<MarketingGrossProfitVo> list, String type) {
        List<MarketingGrossProfitVo> dataList = new ArrayList<>();
        MarketingGrossProfitVo marketingGrossProfitVo = new MarketingGrossProfitVo();
        marketingGrossProfitVo.setBrandId(ReportDataConstant.Finance.EMPTY_STRING);
        marketingGrossProfitVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        marketingGrossProfitVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
        marketingGrossProfitVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        marketingGrossProfitVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
        marketingGrossProfitVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        marketingGrossProfitVo.setType(type);
        //已支付方式进行分组
        Map<String,List<MarketingGrossProfitVo>> payTypeMap = list.stream().collect(Collectors.groupingBy(MarketingGrossProfitVo::getPayTypeId));
        sumData(dataList, marketingGrossProfitVo, payTypeMap);
        return dataList;
    }

    private void sumData(List<MarketingGrossProfitVo> dataList, MarketingGrossProfitVo grossProfitVo, Map<String, List<MarketingGrossProfitVo>> payTypeMap) {
        for (Map.Entry<String, List<MarketingGrossProfitVo>> v : payTypeMap.entrySet()) {
            MarketingGrossProfitVo marketingGrossProfitVo=new MarketingGrossProfitVo();
            marketingGrossProfitVo.setType(grossProfitVo.getType());
            marketingGrossProfitVo.setBrandId(grossProfitVo.getBrandId());
            marketingGrossProfitVo.setBrandName(grossProfitVo.getBrandName());
            marketingGrossProfitVo.setRegionId(grossProfitVo.getRegionId());
            marketingGrossProfitVo.setRegionName(grossProfitVo.getRegionName());
            marketingGrossProfitVo.setShopId(grossProfitVo.getShopId());
            marketingGrossProfitVo.setShopName(grossProfitVo.getShopName());
            marketingGrossProfitVo.setPayTypeId(v.getValue().get(0).getPayTypeId());
            marketingGrossProfitVo.setPayTypeName(v.getValue().get(0).getPayTypeName());
            //订单量
            BigDecimal sumOrderCount = v.getValue().stream().filter(da -> da.getOrderCount() != null).map(MarketingGrossProfitVo::getOrderCount).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setOrderCount(sumOrderCount);
            //本期收入
            BigDecimal sumMoneyActual = v.getValue().stream().filter(da -> da.getMoneyActual() != null).map(MarketingGrossProfitVo::getMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setMoneyActual(sumMoneyActual);
            //本期成本
            BigDecimal sumMoneyCost = v.getValue().stream().filter(da -> da.getMoneyCost() != null).map(MarketingGrossProfitVo::getMoneyCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setMoneyCost(sumMoneyCost);
            //本期毛利
            BigDecimal sumGrossProfit = v.getValue().stream().filter(da -> da.getGrossProfit() != null).map(MarketingGrossProfitVo::getGrossProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setGrossProfit(sumGrossProfit);
            //上一期收入
            BigDecimal sumMonthMoneyActual = v.getValue().stream().filter(da -> da.getMonthMoneyActual() != null).map(MarketingGrossProfitVo::getMonthMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setMonthMoneyActual(sumMonthMoneyActual);
            //上一期成本
            BigDecimal sumMonthMoneyCost = v.getValue().stream().filter(da -> da.getMonthMoneyCost() != null).map(MarketingGrossProfitVo::getMonthMoneyCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setMonthMoneyCost(sumMonthMoneyCost);
            //上一期毛利
            BigDecimal sumMonthGrossProfit = v.getValue().stream().filter(da -> da.getMonthGrossProfit() != null).map(MarketingGrossProfitVo::getMonthGrossProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setMonthGrossProfit(sumMonthGrossProfit);
            //去年同期收入
            BigDecimal sumYearMoneyActual = v.getValue().stream().filter(da -> da.getYearMoneyActual() != null).map(MarketingGrossProfitVo::getYearMoneyActual).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setYearMoneyActual(sumYearMoneyActual);
            //去年同期成本
            BigDecimal sumYearMoneyCost = v.getValue().stream().filter(da -> da.getYearMoneyCost() != null).map(MarketingGrossProfitVo::getYearMoneyCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setYearMoneyCost(sumYearMoneyCost);
            //去年同期毛利
            BigDecimal sumYearGrossProfit = v.getValue().stream().filter(da -> da.getYearGrossProfit() != null).map(MarketingGrossProfitVo::getYearGrossProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
            marketingGrossProfitVo.setYearGrossProfit(sumYearGrossProfit);
            calcRatio(marketingGrossProfitVo);
            dataList.add(marketingGrossProfitVo);
        }
    }

    /**
     * 按类型处理
     * @Author lj
     * @Date:10:57 2020/4/2
     * @param dataMap, type]
     * @return java.util.List<com.njwd.entity.reportdata.vo.MarketingGrossProfitVo>
     **/
    private List<MarketingGrossProfitVo> generateMarketingGrossProfit(Map<String, List<MarketingGrossProfitVo>> dataMap, String type) {
        List<MarketingGrossProfitVo> dataList = new ArrayList<>();
        for (Map.Entry<String, List<MarketingGrossProfitVo>> entry : dataMap.entrySet()) {
            //按门店维度将数据放入shopList
            MarketingGrossProfitVo vo = entry.getValue().get(0);
            MarketingGrossProfitVo marketingGrossProfitVo = new MarketingGrossProfitVo();
            marketingGrossProfitVo.setType(type);
            marketingGrossProfitVo.setBrandId(vo.getBrandId());
            marketingGrossProfitVo.setBrandName(vo.getBrandName());

            //按品牌维度
            if(ReportDataConstant.Finance.TYPE_BRAND.equals(type)){
                marketingGrossProfitVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
                marketingGrossProfitVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                marketingGrossProfitVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);;
                marketingGrossProfitVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //按区域维度
            else if(ReportDataConstant.Finance.TYPE_REGION.equals(type)){
                marketingGrossProfitVo.setRegionId(vo.getRegionId());
                marketingGrossProfitVo.setRegionName(vo.getRegionName());
                marketingGrossProfitVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                marketingGrossProfitVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }

            List<MarketingGrossProfitVo> marketingGrossProfitVoList = entry.getValue();
            //已支付方式进行分组
            Map<String,List<MarketingGrossProfitVo>> payTypeMap = marketingGrossProfitVoList.stream().collect(Collectors.groupingBy(MarketingGrossProfitVo::getPayTypeId));
            sumData(dataList, marketingGrossProfitVo, payTypeMap);
        }
        return dataList;
    }

    /**
     * 计算比例
     * @Author lj
     * @Date:15:10 2020/4/2
     * @param marketingGrossProfitVo
     * @return void
     **/
    private void calcRatio(MarketingGrossProfitVo marketingGrossProfitVo) {
        // 收入同比
        marketingGrossProfitVo.setYearMoneyActualRatio(BigDecimalUtils.divideForRatioOrPercent(FastUtils.Null2Zero(marketingGrossProfitVo.getMoneyActual()).subtract(FastUtils.Null2Zero(marketingGrossProfitVo.getYearMoneyActual())), marketingGrossProfitVo.getYearMoneyActual(), Constant.Number.TWO));
        // 收入环比
        marketingGrossProfitVo.setMonthMoneyActualRatio(BigDecimalUtils.divideForRatioOrPercent(FastUtils.Null2Zero(marketingGrossProfitVo.getMoneyActual()).subtract(FastUtils.Null2Zero(marketingGrossProfitVo.getMonthMoneyActual())), marketingGrossProfitVo.getMonthMoneyActual(), Constant.Number.TWO));
        // 成本同比
        marketingGrossProfitVo.setYearMoneyCostRatio(BigDecimalUtils.divideForRatioOrPercent(FastUtils.Null2Zero(marketingGrossProfitVo.getMoneyCost()).subtract(FastUtils.Null2Zero(marketingGrossProfitVo.getYearMoneyCost())), marketingGrossProfitVo.getYearMoneyCost(), Constant.Number.TWO));
        // 成本环比
        marketingGrossProfitVo.setMonthMoneyCostRatio(BigDecimalUtils.divideForRatioOrPercent(FastUtils.Null2Zero(marketingGrossProfitVo.getMoneyCost()).subtract(FastUtils.Null2Zero(marketingGrossProfitVo.getMonthMoneyCost())), marketingGrossProfitVo.getMonthMoneyCost(), Constant.Number.TWO));
        // 毛利同比
        marketingGrossProfitVo.setYearGrossProfitRatio(BigDecimalUtils.divideForRatioOrPercent(FastUtils.Null2Zero(marketingGrossProfitVo.getGrossProfit()).subtract(FastUtils.Null2Zero(marketingGrossProfitVo.getYearGrossProfit())), marketingGrossProfitVo.getYearGrossProfit(), Constant.Number.TWO));
        // 毛利环比
        marketingGrossProfitVo.setMonthGrossProfitRatio(BigDecimalUtils.divideForRatioOrPercent(FastUtils.Null2Zero(marketingGrossProfitVo.getGrossProfit()).subtract(FastUtils.Null2Zero(marketingGrossProfitVo.getMonthGrossProfit())), marketingGrossProfitVo.getMonthGrossProfit(), Constant.Number.TWO));
    }

    /**
     * 营销活动毛利统计表导出
     *
     * @param excelExportDto
     * @param response
     * @return void
     * @Author lj
     * @Date:11:03 2020/3/31
     **/
    @Override
    public void exportMarketingGrossProfit(ExcelExportDto excelExportDto, HttpServletResponse response) {
        MarketingGrossProfitDto marketingGrossProfitDto = new MarketingGrossProfitDto();
        FastUtils.copyProperties(excelExportDto, marketingGrossProfitDto);
        List<MarketingGrossProfitVo> marketingGrossProfitVoList = findMarketingGrossProfit(marketingGrossProfitDto);

        List<MarketingGrossProfitVo> list;
        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(marketingGrossProfitDto.getType())) {
            list = marketingGrossProfitVoList.stream().filter(info -> (info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)
                    || info.getType().equals(ReportDataConstant.Finance.TYPE_ALL))
            ).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(marketingGrossProfitDto.getType())) {
            list = marketingGrossProfitVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        }else if (ReportDataConstant.Finance.TYPE_BRAND.equals(marketingGrossProfitDto.getType())) {
            list = marketingGrossProfitVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else {
            list = marketingGrossProfitVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }

        fileService.exportExcelForQueryTerm(response, excelExportDto, list,
                ExcelColumnConstant.MarketingGrossProfit.BRAND_NAME,
                ExcelColumnConstant.MarketingGrossProfit.REGION_NAME,
                ExcelColumnConstant.MarketingGrossProfit.SHOP_NAME,
                ExcelColumnConstant.MarketingGrossProfit.PAY_TYPE_NAME,
                ExcelColumnConstant.MarketingGrossProfit.ORDER_COUNT,
                ExcelColumnConstant.MarketingGrossProfit.MONEY_ACTUAL,
                ExcelColumnConstant.MarketingGrossProfit.MONEY_COST,
                ExcelColumnConstant.MarketingGrossProfit.GROSS_PROFIT,
                ExcelColumnConstant.MarketingGrossProfit.YEAR_MONEY_ACTUAL_RATIO,
                ExcelColumnConstant.MarketingGrossProfit.MONTH_MONEY_ACTUAL_RATIO,
                ExcelColumnConstant.MarketingGrossProfit.YEAR_MONEY_COST_RATIO,
                ExcelColumnConstant.MarketingGrossProfit.MONTH_MONEY_COST_RATIO,
                ExcelColumnConstant.MarketingGrossProfit.YEAR_GROSS_PROFIT_RATIO,
                ExcelColumnConstant.MarketingGrossProfit.MONTH_GROSS_PROFIT_RATIO);
    }
}

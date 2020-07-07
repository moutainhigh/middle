package com.njwd.reportdata.service.impl;


import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.common.ScmConstant;
import com.njwd.entity.reportdata.dto.GrossProfitDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.DictVo;
import com.njwd.entity.reportdata.vo.GrossProfitVo;
import com.njwd.entity.reportdata.vo.ReportPosDeskVo;
import com.njwd.entity.reportdata.vo.scm.ScmReportTableVo;
import com.njwd.reportdata.mapper.BusinessAnalysisMapper;
import com.njwd.reportdata.mapper.ScmReportTableMapper;
import com.njwd.reportdata.service.*;
import com.njwd.service.FileService;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import com.njwd.utils.MergeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Description: 毛利分析
 * @Author 周鹏
 * @Date 2020/03/30
 */
@Service
public class GrossProfitServiceImpl implements GrossProfitService {
    @Resource
    private BusinessAnalysisMapper businessAnalysisMapper;

    @Resource
    private ScmReportTableMapper scmReportTableMapper;

    @Resource
    private FileService fileService;

    /**
     * @Description: 查询毛利分析表
     * @Param: [queryDto]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/03/02
     */
    @Override
    public List<GrossProfitVo> findGrossProfit(GrossProfitDto queryDto) {
        //step0:查询所需参数
        List<DictVo> dictList = businessAnalysisMapper.findDictList(queryDto.getEnteId());
        //step1:查询门店基础信息
        List<GrossProfitVo> shopList = businessAnalysisMapper.findShopList(queryDto);
        //step2:查询客流量和开台数信息
        List<ReportPosDeskVo> posDeskList = businessAnalysisMapper.findPosDeskList(queryDto);
        //step3:合并step1和step2的结果
        mergePosDeskResult(shopList, posDeskList);
        //step4:查询收入和销量信息
        //实收金额
        List<GrossProfitVo> receiptsList = businessAnalysisMapper.findPosReceiptsList(queryDto);
        //酒水饮料、附加、其他收入
        queryDto.setColumnName(Constant.GrossProfitColumnName.INCOME_DISH);
        queryDto.setFoodStyleName(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.GROSS_PROFIT_INCOME_DISH))
                .collect(Collectors.toList()).get(0).getModelId());
        List<GrossProfitVo> incomeDishExceptList = businessAnalysisMapper.findPosDetailFoodList(queryDto);
        //酒水收入
        queryDto.setColumnName(Constant.GrossProfitColumnName.INCOME_WINE);
        queryDto.setFoodStyleName(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.GROSS_PROFIT_INCOME_WINE))
                .collect(Collectors.toList()).get(0).getModelId());
        List<GrossProfitVo> incomeWineList = businessAnalysisMapper.findPosDetailFoodList(queryDto);
        //自选调料销量和收入
        queryDto.setColumnName(Constant.GrossProfitColumnName.SALE_INCOME_CONDIMENT);
        queryDto.setFoodNo(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.GROSS_PROFIT_INCOME_CONDIMENT))
                .collect(Collectors.toList()).get(0).getModelId());
        List<GrossProfitVo> incomeCondimentList = businessAnalysisMapper.findPosDetailFoodList(queryDto);
        //step5:合并step1和step4的结果
        mergePosDetailFoodResult(shopList, receiptsList, incomeDishExceptList, incomeWineList, incomeCondimentList);
        //step6:查询酒水成本、菜金成本、自选调料成本、赠送菜品成本、赠送水果成本和菜品库存金额
        List<ScmReportTableVo> costList = scmReportTableMapper.findGrossProfitCostList(queryDto);
        //step7:合并step1和step6的结果,并计算毛利、毛利率等信息
        mergeCostResult(shopList, costList);
        //step8:查询上期库存
        //获取上期时间
        List<Date> lastPeriodDate = DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
        queryDto.setBeginDate(lastPeriodDate.get(0));
        queryDto.setEndDate(lastPeriodDate.get(1));
        List<ScmReportTableVo> lastPeriodStockList = scmReportTableMapper.findLastPeriodStockList(queryDto);
        //step9:合并step1和step8的结果,并计算平均库存和菜品库存周转天数
        mergeDishStockResult(shopList, lastPeriodStockList);
        //step10:生成区域、品牌维度和合计的数据
        shopList = mergeDimensionList(shopList);

        return shopList;
    }

    /**
     * 生成区域、品牌维度和合计的数据
     *
     * @param shopList
     * @return
     */
    private List<GrossProfitVo> mergeDimensionList(List<GrossProfitVo> shopList) {
        //区域维度
        Map<String, List<GrossProfitVo>> regionMap = shopList.stream().filter(region -> region.getRegionId() != null).
                collect(Collectors.groupingBy(GrossProfitVo::getRegionId));
        List<GrossProfitVo> regionList = getDimensionList(regionMap, ReportDataConstant.Finance.TYPE_REGION);
        //品牌维度
        Map<String, List<GrossProfitVo>> brandMap = regionList.stream().filter(shop -> shop.getBrandId() != null).
                collect(Collectors.groupingBy(GrossProfitVo::getBrandId));
        List<GrossProfitVo> brandList = getDimensionList(brandMap, ReportDataConstant.Finance.TYPE_BRAND);
        //生成一条合计信息
        Map<String, List<GrossProfitVo>> totalMap = new HashMap<>();
        totalMap.put(ReportDataConstant.Finance.TYPE_ALL, brandList);
        List<GrossProfitVo> totalList = getDimensionList(totalMap, ReportDataConstant.Finance.TYPE_ALL);
        //填充品牌和门店数据
        shopList.addAll(regionList);
        shopList.addAll(brandList);
        //数据排序
        shopList = shopList.stream()
                .filter(shop -> shop.getBrandId() != null && shop.getRegionId() != null && shop.getShopId() != null)
                .sorted(Comparator.comparing(GrossProfitVo::getBrandId).
                        thenComparing(GrossProfitVo::getRegionId).
                        thenComparing(GrossProfitVo::getShopId))
                .collect(Collectors.toList());
        //填充合计数据
        shopList.addAll(totalList);

        return shopList;
    }

    /**
     * @Description: 合并门店、客流量和开台数信息
     * @Param: [list, posDeskList]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/03/02
     */
    private void mergePosDeskResult(List<GrossProfitVo> list, List<ReportPosDeskVo> posDeskList) {
        MergeUtil.mergeList(list, posDeskList,
                GrossProfitVo::getShopId, ReportPosDeskVo::getShopId,
                (grossProfitVo, posDeskVoList) -> {
                    Integer clientCountTotal = 0;
                    Integer deskCountTotal = 0;
                    for (ReportPosDeskVo item : posDeskVoList) {
                        if (Constant.MealId.afternoonMarket.equals(item.getMealId())) {
                            grossProfitVo.setAfternoonMarket(item.getClientCount() == null ? Constant.Number.ZERO : item.getClientCount());
                        } else if (Constant.MealId.eveningMarket.equals(item.getMealId())) {
                            grossProfitVo.setEveningMarket(item.getClientCount() == null ? Constant.Number.ZERO : item.getClientCount());
                        } else if (Constant.MealId.midnightSnack.equals(item.getMealId())) {
                            grossProfitVo.setMidnightSnack(item.getClientCount() == null ? Constant.Number.ZERO : item.getClientCount());
                        }
                        clientCountTotal += item.getClientCount() == null ? Constant.Number.ZERO : item.getClientCount();
                        deskCountTotal += item.getDeskCount() == null ? Constant.Number.ZERO : Integer.valueOf(item.getDeskCount().toString());
                    }
                    grossProfitVo.setClientCountTotal(clientCountTotal);
                    grossProfitVo.setDeskCountTotal(deskCountTotal);
                });
    }

    /**
     * @Description: 合并门店、收入和销量信息
     * @Param: [list, receiptsList, incomeDishExceptList, incomeWineList, incomeCondimentList]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/03/03
     */
    private void mergePosDetailFoodResult(List<GrossProfitVo> list, List<GrossProfitVo> receiptsList, List<GrossProfitVo> incomeDishExceptList,
                                          List<GrossProfitVo> incomeWineList, List<GrossProfitVo> incomeCondimentList) {
        //设置菜品合计(实收-酒水饮料类、附加、其他)
        MergeUtil.merge(receiptsList, incomeDishExceptList,
                GrossProfitVo::getShopId, GrossProfitVo::getShopId,
                (grossProfitVo, posDetailFoodInfo) -> grossProfitVo.setIncomeDish(
                        BigDecimalUtils.getSubtract(grossProfitVo.getReceipts(), posDetailFoodInfo.getIncomeDishExcept())));
        MergeUtil.merge(list, receiptsList,
                GrossProfitVo::getShopId, GrossProfitVo::getShopId,
                (grossProfitVo, posDetailFoodInfo) -> grossProfitVo.setIncomeDish(posDetailFoodInfo.getIncomeDish()));
        //设置酒水收入和综合收入
        MergeUtil.merge(list, incomeWineList,
                GrossProfitVo::getShopId, GrossProfitVo::getShopId,
                (grossProfitVo, posDetailFoodInfo) -> {
                    grossProfitVo.setIncomeWine(posDetailFoodInfo.getIncomeWine());
                    grossProfitVo.setIncomeTotal(BigDecimalUtils.getAdd(grossProfitVo.getIncomeDish(), posDetailFoodInfo.getIncomeWine()));
                });
        MergeUtil.merge(list, incomeCondimentList,
                GrossProfitVo::getShopId, GrossProfitVo::getShopId,
                (grossProfitVo, posDetailFoodInfo) -> {
                    grossProfitVo.setSaleCondiment(posDetailFoodInfo.getSaleCondiment());
                    grossProfitVo.setIncomeCondiment(posDetailFoodInfo.getIncomeCondiment());
                });
    }

    /**
     * @Description: 合并菜金、自选调料和赠送成本,并计算毛利、毛利率等信息
     * @Param: [list, costList]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/03/26
     */
    private void mergeCostResult(List<GrossProfitVo> list, List<ScmReportTableVo> costList) {
        MergeUtil.merge(list, costList,
                GrossProfitVo::getShopId, ScmReportTableVo::getShopId,
                (grossProfitVo, costInfo) -> {
                    grossProfitVo.setCostWine(costInfo.getCostWine());
                    grossProfitVo.setCostDish(costInfo.getCostDish());
                    grossProfitVo.setCostCondiment(costInfo.getCostCondiment());
                    grossProfitVo.setDishGive(costInfo.getDishGive());
                    grossProfitVo.setFruitGive(costInfo.getFruitGive());
                    grossProfitVo.setDishStockAmount(costInfo.getDishStockAmount());
                    //计算菜肴信息
                    grossProfitVo.setGrossProfitDish(BigDecimalUtils.getSubtract(grossProfitVo.getIncomeDish(), grossProfitVo.getCostDish()));
                    grossProfitVo.setGrossProfitPercentDish(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitDish(), grossProfitVo.getIncomeDish()));
                    grossProfitVo.setGrossProfitExceptDish(BigDecimalUtils.getSubtract(grossProfitVo.getGrossProfitDish(), grossProfitVo.getDishGive(),
                            grossProfitVo.getFruitGive()));
                    //计算酒水信息
                    grossProfitVo.setGrossProfitWine(BigDecimalUtils.getSubtract(grossProfitVo.getIncomeWine(), grossProfitVo.getCostWine()));
                    grossProfitVo.setGrossProfitPercentWine(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitWine(), grossProfitVo.getIncomeWine()));
                    //计算自选调料信息
                    grossProfitVo.setGrossProfitCondiment(BigDecimalUtils.getSubtract(grossProfitVo.getIncomeCondiment(), grossProfitVo.getCostCondiment()));
                    grossProfitVo.setGrossProfitPercentCondiment(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitCondiment(), grossProfitVo.getIncomeCondiment()));
                    //计算综合信息
                    grossProfitVo.setCostTotal(BigDecimalUtils.getAdd(grossProfitVo.getCostDish(), grossProfitVo.getCostWine()));
                    grossProfitVo.setGrossProfitTotal(BigDecimalUtils.getSubtract(grossProfitVo.getIncomeTotal(), grossProfitVo.getCostTotal()));
                    grossProfitVo.setGrossProfitPercentTotal(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitTotal(), grossProfitVo.getIncomeTotal()));
                    grossProfitVo.setPersonGrossProfitPercentTotal(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitPercentTotal(),
                            grossProfitVo.getClientCountTotal() == null ? BigDecimal.ZERO : new BigDecimal(grossProfitVo.getClientCountTotal())));
                    grossProfitVo.setDeskGrossProfitPercentTotal(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitPercentTotal(),
                            grossProfitVo.getClientCountTotal() == null ? BigDecimal.ZERO : new BigDecimal(grossProfitVo.getDeskCountTotal())));
                });
    }

    /**
     * @Description: 合并门店和平均库存信息, 并计算菜品库存周转天数
     * @Param: [list, posDetailFoodList]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/04/02
     */
    private void mergeDishStockResult(List<GrossProfitVo> list, List<ScmReportTableVo> stockList) {
        MergeUtil.merge(list, stockList,
                GrossProfitVo::getShopId, ScmReportTableVo::getShopId,
                (grossProfitVo, stockInfo) -> {
                    //平均库存
                    grossProfitVo.setStockAverage(BigDecimalUtils.getAdd(grossProfitVo.getDishStockAmount(), stockInfo.getLastPeriodStock())
                            .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP));
                    //计算菜品库存周转天数=30/（本月菜金成本-平均库存）
                    BigDecimal num = BigDecimalUtils.getSubtract(grossProfitVo.getCostDish(), grossProfitVo.getStockAverage());
                    grossProfitVo.setDishStockTurnoverDays(num.compareTo(BigDecimal.ZERO) == 0 ? null :
                            ScmConstant.GrossProfitAnalysis.DISH_STOCK_TURNOVER_DAYS.divide(num, 4, RoundingMode.HALF_UP));
                });
    }

    /**
     * 生成各维度毛利分析表数据
     *
     * @param list
     * @param type
     * @return
     */
    private List<GrossProfitVo> getDimensionList(Map<String, List<GrossProfitVo>> list, String type) {
        List<GrossProfitVo> resultList = new LinkedList<>();
        //遍历Map中的数据
        list.forEach((k, v) -> {
            GrossProfitVo grossProfitVo = new GrossProfitVo();
            //设置品牌、区域、门店信息
            initDimensionInfo(type, v, grossProfitVo);
            //求和汇总
            getSumInfo(v, grossProfitVo);
            resultList.add(grossProfitVo);
        });
        return resultList;
    }

    /**
     * 求和汇总
     *
     * @param v
     * @param grossProfitVo
     */
    private void getSumInfo(List<GrossProfitVo> v, GrossProfitVo grossProfitVo) {
        //求和
        grossProfitVo.setAfternoonMarket(v.stream().collect(Collectors.summingInt(data -> data.getAfternoonMarket() == null
                ? Constant.Number.ZERO : data.getAfternoonMarket())));
        grossProfitVo.setEveningMarket(v.stream().collect(Collectors.summingInt(data -> data.getEveningMarket() == null
                ? Constant.Number.ZERO : data.getEveningMarket())));
        grossProfitVo.setMidnightSnack(v.stream().collect(Collectors.summingInt(data -> data.getMidnightSnack() == null
                ? Constant.Number.ZERO : data.getMidnightSnack())));
        grossProfitVo.setClientCountTotal(v.stream().collect(Collectors.summingInt(data -> data.getClientCountTotal() == null
                ? Constant.Number.ZERO : data.getClientCountTotal())));
        grossProfitVo.setDeskCountTotal(v.stream().collect(Collectors.summingInt(data -> data.getDeskCountTotal() == null
                ? Constant.Number.ZERO : data.getDeskCountTotal())));
        grossProfitVo.setIncomeTotal(v.stream().map(data -> data.getIncomeTotal() == null
                ? BigDecimal.ZERO : data.getIncomeTotal()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setCostTotal(v.stream().map(data -> data.getCostTotal() == null
                ? BigDecimal.ZERO : data.getCostTotal()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setGrossProfitTotal(v.stream().map(data -> data.getGrossProfitTotal() == null
                ? BigDecimal.ZERO : data.getGrossProfitTotal()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setIncomeDish(v.stream().map(data -> data.getIncomeDish() == null
                ? BigDecimal.ZERO : data.getIncomeDish()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setCostDish(v.stream().map(data -> data.getCostDish() == null
                ? BigDecimal.ZERO : data.getCostDish()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setGrossProfitDish(v.stream().map(data -> data.getGrossProfitDish() == null
                ? BigDecimal.ZERO : data.getGrossProfitDish()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setGrossProfitExceptDish(v.stream().map(data -> data.getGrossProfitExceptDish() == null
                ? BigDecimal.ZERO : data.getGrossProfitExceptDish()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setIncomeWine(v.stream().map(data -> data.getIncomeWine() == null
                ? BigDecimal.ZERO : data.getIncomeWine()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setCostWine(v.stream().map(data -> data.getCostWine() == null
                ? BigDecimal.ZERO : data.getCostWine()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setGrossProfitWine(v.stream().map(data -> data.getGrossProfitWine() == null
                ? BigDecimal.ZERO : data.getGrossProfitWine()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setSaleCondiment(v.stream().map(data -> data.getSaleCondiment() == null
                ? BigDecimal.ZERO : data.getSaleCondiment()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setIncomeCondiment(v.stream().map(data -> data.getIncomeCondiment() == null
                ? BigDecimal.ZERO : data.getIncomeCondiment()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setCostCondiment(v.stream().map(data -> data.getCostCondiment() == null
                ? BigDecimal.ZERO : data.getCostCondiment()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setGrossProfitCondiment(v.stream().map(data -> data.getGrossProfitCondiment() == null
                ? BigDecimal.ZERO : data.getGrossProfitCondiment()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setDishGive(v.stream().map(data -> data.getDishGive() == null
                ? BigDecimal.ZERO : data.getDishGive()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setFruitGive(v.stream().map(data -> data.getFruitGive() == null
                ? BigDecimal.ZERO : data.getFruitGive()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setDishStockAmount(v.stream().map(data -> data.getDishStockAmount() == null
                ? BigDecimal.ZERO : data.getDishStockAmount()).reduce(BigDecimal.ZERO, BigDecimal::add));
        grossProfitVo.setStockAverage(v.stream().map(data -> data.getStockAverage() == null
                ? BigDecimal.ZERO : data.getStockAverage()).reduce(BigDecimal.ZERO, BigDecimal::add));
        //计算毛利率
        grossProfitVo.setGrossProfitPercentDish(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitDish(), grossProfitVo.getIncomeDish()));
        grossProfitVo.setGrossProfitPercentWine(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitWine(), grossProfitVo.getIncomeWine()));
        grossProfitVo.setGrossProfitPercentCondiment(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitCondiment(), grossProfitVo.getIncomeCondiment()));
        grossProfitVo.setGrossProfitPercentTotal(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitTotal(), grossProfitVo.getIncomeTotal()));
        grossProfitVo.setPersonGrossProfitPercentTotal(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitPercentTotal(),
                grossProfitVo.getClientCountTotal() == null ? BigDecimal.ZERO : new BigDecimal(grossProfitVo.getClientCountTotal())));
        grossProfitVo.setDeskGrossProfitPercentTotal(BigDecimalUtils.getPercent(grossProfitVo.getGrossProfitPercentTotal(),
                grossProfitVo.getClientCountTotal() == null ? BigDecimal.ZERO : new BigDecimal(grossProfitVo.getDeskCountTotal())));
        BigDecimal num = BigDecimalUtils.getSubtract(grossProfitVo.getCostDish(), grossProfitVo.getStockAverage());
        grossProfitVo.setDishStockTurnoverDays(num.compareTo(BigDecimal.ZERO) == 0 ? null :
                ScmConstant.GrossProfitAnalysis.DISH_STOCK_TURNOVER_DAYS.divide(num, 4, RoundingMode.HALF_UP));
    }

    /**
     * 设置品牌、区域、门店信息
     *
     * @param type
     * @param v
     * @param grossProfitVo
     */
    private void initDimensionInfo(String type, List<GrossProfitVo> v, GrossProfitVo grossProfitVo) {
        grossProfitVo.setType(type);
        if (ReportDataConstant.Finance.TYPE_ALL.equals(type)) {
            //合计维度,把品牌、区域和门店属性设置为全部
            grossProfitVo.setBrandId(ReportDataConstant.Finance.EMPTY_STRING);
            grossProfitVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
            grossProfitVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
            grossProfitVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
            grossProfitVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
            grossProfitVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(type)) {
            //品牌维度,把区域和门店属性设置为全部
            grossProfitVo.setBrandId(v.get(0).getBrandId());
            grossProfitVo.setBrandName(v.get(0).getBrandName());
            grossProfitVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
            grossProfitVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
            grossProfitVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
            grossProfitVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
            //区域维度,把门店属性设置为全部
            grossProfitVo.setBrandId(v.get(0).getBrandId());
            grossProfitVo.setBrandName(v.get(0).getBrandName());
            grossProfitVo.setRegionId(v.get(0).getRegionId());
            grossProfitVo.setRegionName(v.get(0).getRegionName());
            grossProfitVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
            grossProfitVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        }
    }

    /**
     * 导出毛利分析表
     *
     * @param excelExportDto,response
     * @return GrossProfitVo
     * @Author 周鹏
     * @Date 2020/03/30
     */
    @Override
    public void exportGrossProfit(ExcelExportDto excelExportDto, HttpServletResponse response) {
        GrossProfitDto queryDto = new GrossProfitDto();
        FastUtils.copyProperties(excelExportDto, queryDto);
        List<GrossProfitVo> list = findGrossProfit(queryDto);
        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(queryDto.getType())) {
            list = list.stream().filter(info -> (info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP))).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(queryDto.getType())) {
            list = list.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(queryDto.getType())) {
            list = list.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        }
        String[] firstHeader = {ExcelColumnConstant.GrossProfit.BRAND_NAME.getTitle(), ExcelColumnConstant.GrossProfit.REGION_NAME.getTitle(),
                ExcelColumnConstant.GrossProfit.SHOP_NAME.getTitle(), ExcelColumnConstant.GrossProfit.CLIENT_COUNT.getTitle(),
                ExcelColumnConstant.GrossProfit.CLIENT_COUNT.getTitle(), ExcelColumnConstant.GrossProfit.CLIENT_COUNT.getTitle(),
                ExcelColumnConstant.GrossProfit.CLIENT_COUNT.getTitle(), ExcelColumnConstant.GrossProfit.DESK_COUNT_TOTAL.getTitle(),
                ExcelColumnConstant.GrossProfit.TOTAL.getTitle(), ExcelColumnConstant.GrossProfit.TOTAL.getTitle(),
                ExcelColumnConstant.GrossProfit.TOTAL.getTitle(), ExcelColumnConstant.GrossProfit.TOTAL.getTitle(),
                ExcelColumnConstant.GrossProfit.TOTAL.getTitle(), ExcelColumnConstant.GrossProfit.TOTAL.getTitle(),
                ExcelColumnConstant.GrossProfit.DISH.getTitle(), ExcelColumnConstant.GrossProfit.DISH.getTitle(),
                ExcelColumnConstant.GrossProfit.DISH.getTitle(), ExcelColumnConstant.GrossProfit.DISH.getTitle(),
                ExcelColumnConstant.GrossProfit.DISH.getTitle(), ExcelColumnConstant.GrossProfit.WINE.getTitle(),
                ExcelColumnConstant.GrossProfit.WINE.getTitle(), ExcelColumnConstant.GrossProfit.WINE.getTitle(),
                ExcelColumnConstant.GrossProfit.WINE.getTitle(), ExcelColumnConstant.GrossProfit.CONDIMENT.getTitle(),
                ExcelColumnConstant.GrossProfit.CONDIMENT.getTitle(), ExcelColumnConstant.GrossProfit.CONDIMENT.getTitle(),
                ExcelColumnConstant.GrossProfit.CONDIMENT.getTitle(), ExcelColumnConstant.GrossProfit.CONDIMENT.getTitle(),
                ExcelColumnConstant.GrossProfit.GIVE.getTitle(), ExcelColumnConstant.GrossProfit.GIVE.getTitle(),
                ExcelColumnConstant.GrossProfit.DISH_STOCK_AMOUNT.getTitle(), ExcelColumnConstant.GrossProfit.STOCK_AVERAGE.getTitle(),
                ExcelColumnConstant.GrossProfit.DISH_STOCK_TURNOVER_DAYS.getTitle()};
        String[] secondHeader = {ExcelColumnConstant.GrossProfit.BRAND_NAME.getTitle(), ExcelColumnConstant.GrossProfit.REGION_NAME.getTitle(),
                ExcelColumnConstant.GrossProfit.SHOP_NAME.getTitle(), ExcelColumnConstant.GrossProfit.AFTERNOON_MARKET.getTitle(),
                ExcelColumnConstant.GrossProfit.EVENING_MARKET.getTitle(), ExcelColumnConstant.GrossProfit.MIDNIGHT_SNACK.getTitle(),
                ExcelColumnConstant.GrossProfit.CLIENT_COUNT_TOTAL.getTitle(), ExcelColumnConstant.GrossProfit.DESK_COUNT_TOTAL.getTitle(),
                ExcelColumnConstant.GrossProfit.INCOME_TOTAL.getTitle(), ExcelColumnConstant.GrossProfit.COST_TOTAL.getTitle(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_TOTAL.getTitle(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_TOTAL.getTitle(),
                ExcelColumnConstant.GrossProfit.PERSON_GROSS_PROFIT_PERCENT_TOTAL.getTitle(), ExcelColumnConstant.GrossProfit.DESK_GROSS_PROFIT_PERCENT_TOTAL.getTitle(),
                ExcelColumnConstant.GrossProfit.INCOME_DISH.getTitle(), ExcelColumnConstant.GrossProfit.COST_DISH.getTitle(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_DISH.getTitle(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_DISH.getTitle(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_EXCEPT_DISH.getTitle(), ExcelColumnConstant.GrossProfit.INCOME_WINE.getTitle(),
                ExcelColumnConstant.GrossProfit.COST_WINE.getTitle(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_WINE.getTitle(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_WINE.getTitle(), ExcelColumnConstant.GrossProfit.SALE_CONDIMENT.getTitle(),
                ExcelColumnConstant.GrossProfit.INCOME_CONDIMENT.getTitle(), ExcelColumnConstant.GrossProfit.COST_CONDIMENT.getTitle(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_CONDIMENT.getTitle(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_CONDIMENT.getTitle(),
                ExcelColumnConstant.GrossProfit.DISH_GIVE.getTitle(), ExcelColumnConstant.GrossProfit.FRUIT_GIVE.getTitle(),
                ExcelColumnConstant.GrossProfit.DISH_STOCK_AMOUNT.getTitle(), ExcelColumnConstant.GrossProfit.STOCK_AVERAGE.getTitle(),
                ExcelColumnConstant.GrossProfit.DISH_STOCK_TURNOVER_DAYS.getTitle()};
        String[] fieldNames = {ExcelColumnConstant.GrossProfit.BRAND_NAME.getField(), ExcelColumnConstant.GrossProfit.REGION_NAME.getField(),
                ExcelColumnConstant.GrossProfit.SHOP_NAME.getField(), ExcelColumnConstant.GrossProfit.AFTERNOON_MARKET.getField(),
                ExcelColumnConstant.GrossProfit.EVENING_MARKET.getField(), ExcelColumnConstant.GrossProfit.MIDNIGHT_SNACK.getField(),
                ExcelColumnConstant.GrossProfit.CLIENT_COUNT_TOTAL.getField(), ExcelColumnConstant.GrossProfit.DESK_COUNT_TOTAL.getField(),
                ExcelColumnConstant.GrossProfit.INCOME_TOTAL.getField(), ExcelColumnConstant.GrossProfit.COST_TOTAL.getField(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_TOTAL.getField(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_TOTAL.getField(),
                ExcelColumnConstant.GrossProfit.PERSON_GROSS_PROFIT_PERCENT_TOTAL.getField(), ExcelColumnConstant.GrossProfit.DESK_GROSS_PROFIT_PERCENT_TOTAL.getField(),
                ExcelColumnConstant.GrossProfit.INCOME_DISH.getField(), ExcelColumnConstant.GrossProfit.COST_DISH.getField(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_DISH.getField(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_DISH.getField(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_EXCEPT_DISH.getField(), ExcelColumnConstant.GrossProfit.INCOME_WINE.getField(),
                ExcelColumnConstant.GrossProfit.COST_WINE.getField(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_WINE.getField(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_WINE.getField(), ExcelColumnConstant.GrossProfit.SALE_CONDIMENT.getField(),
                ExcelColumnConstant.GrossProfit.INCOME_CONDIMENT.getField(), ExcelColumnConstant.GrossProfit.COST_CONDIMENT.getField(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_CONDIMENT.getField(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_CONDIMENT.getField(),
                ExcelColumnConstant.GrossProfit.DISH_GIVE.getField(), ExcelColumnConstant.GrossProfit.FRUIT_GIVE.getField(),
                ExcelColumnConstant.GrossProfit.DISH_STOCK_AMOUNT.getField(), ExcelColumnConstant.GrossProfit.STOCK_AVERAGE.getField(),
                ExcelColumnConstant.GrossProfit.DISH_STOCK_TURNOVER_DAYS.getField()};
        String[] convertTypes = {ExcelColumnConstant.GrossProfit.BRAND_NAME.getConvertType(), ExcelColumnConstant.GrossProfit.REGION_NAME.getConvertType(),
                ExcelColumnConstant.GrossProfit.SHOP_NAME.getConvertType(), ExcelColumnConstant.GrossProfit.AFTERNOON_MARKET.getConvertType(),
                ExcelColumnConstant.GrossProfit.EVENING_MARKET.getConvertType(), ExcelColumnConstant.GrossProfit.MIDNIGHT_SNACK.getConvertType(),
                ExcelColumnConstant.GrossProfit.CLIENT_COUNT_TOTAL.getConvertType(), ExcelColumnConstant.GrossProfit.DESK_COUNT_TOTAL.getConvertType(),
                ExcelColumnConstant.GrossProfit.INCOME_TOTAL.getConvertType(), ExcelColumnConstant.GrossProfit.COST_TOTAL.getConvertType(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_TOTAL.getConvertType(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_TOTAL.getConvertType(),
                ExcelColumnConstant.GrossProfit.PERSON_GROSS_PROFIT_PERCENT_TOTAL.getConvertType(),
                ExcelColumnConstant.GrossProfit.DESK_GROSS_PROFIT_PERCENT_TOTAL.getConvertType(),
                ExcelColumnConstant.GrossProfit.INCOME_DISH.getConvertType(), ExcelColumnConstant.GrossProfit.COST_DISH.getConvertType(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_DISH.getConvertType(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_DISH.getConvertType(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_EXCEPT_DISH.getConvertType(), ExcelColumnConstant.GrossProfit.INCOME_WINE.getConvertType(),
                ExcelColumnConstant.GrossProfit.COST_WINE.getConvertType(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_WINE.getConvertType(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_WINE.getConvertType(), ExcelColumnConstant.GrossProfit.SALE_CONDIMENT.getConvertType(),
                ExcelColumnConstant.GrossProfit.INCOME_CONDIMENT.getConvertType(), ExcelColumnConstant.GrossProfit.COST_CONDIMENT.getConvertType(),
                ExcelColumnConstant.GrossProfit.GROSS_PROFIT_CONDIMENT.getConvertType(), ExcelColumnConstant.GrossProfit.GROSS_PROFIT_PERCENT_CONDIMENT.getConvertType(),
                ExcelColumnConstant.GrossProfit.DISH_GIVE.getConvertType(), ExcelColumnConstant.GrossProfit.FRUIT_GIVE.getConvertType(),
                ExcelColumnConstant.GrossProfit.DISH_STOCK_AMOUNT.getConvertType(), ExcelColumnConstant.GrossProfit.STOCK_AVERAGE.getConvertType(),
                ExcelColumnConstant.GrossProfit.DISH_STOCK_TURNOVER_DAYS.getConvertType()};
        fileService.exportMultiRowHeaderData(response, excelExportDto, list, fieldNames, convertTypes, firstHeader, secondHeader);
    }
}

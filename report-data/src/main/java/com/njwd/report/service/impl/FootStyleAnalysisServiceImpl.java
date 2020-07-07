package com.njwd.report.service.impl;

import com.njwd.common.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.ScmQueryDto;
import com.njwd.entity.reportdata.dto.querydto.SimpleScmQueryDto;
import com.njwd.entity.reportdata.dto.scm.SimpleFoodDto;
import com.njwd.entity.reportdata.vo.DictVo;
import com.njwd.entity.reportdata.vo.RepPosDetailFoodVo;
import com.njwd.entity.reportdata.vo.scm.DishGrossProfitVo;
import com.njwd.entity.reportdata.vo.scm.GrossWeightVo;
import com.njwd.entity.reportdata.vo.scm.SubMaterialPriceVo;
import com.njwd.reportdata.mapper.FoodStyleAnalysisMapper;
import com.njwd.reportdata.service.FootStyleAnalysisService;
import com.njwd.service.FileService;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 *@description: 菜品毛利分析实现类
 *@author: fancl
 *@create: 2020-03-01 
 */
@Service
public class FootStyleAnalysisServiceImpl implements FootStyleAnalysisService {

    @Autowired
    FoodStyleAnalysisMapper foodStyleAnalysisMapper;
    Logger logger = LoggerFactory.getLogger(FootStyleAnalysisServiceImpl.class);
    @Autowired
    private FileService fileService;

    //菜品维度key
    private static String foodDimens(RepPosDetailFoodVo detail) {
        return detail.getFoodNo();
    }

    //菜品门店维度key
    private static String foodAndShopDimens(RepPosDetailFoodVo detail) {
        return detail.getFoodNo() + detail.getShopId();
    }

    //菜品门店维度key
    private static String foodAndShopDimens(GrossWeightVo weightVo) {
        return weightVo.getFoodNo() + weightVo.getShopId();
    }

    //本期数据
    @Override
    public List<DishGrossProfitVo> getFoodGrossProfit(ScmQueryDto queryDto) {
        //日志开关
        queryDto.setNeedLog(false);
        //查菜品汇总信息,设置不包含酒水的
        queryDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);
        List<RepPosDetailFoodVo> detailFoodList = getBaseFoodSaleList(queryDto);
        if (LogConstant.Scm.IS_LOG.equals(queryDto.getNeedLog())) {
            logger.info("菜品销量:" + detailFoodList);
        }
        //得到foodNo,shopId维度成本基本数据
        List<GrossWeightVo> costList = getDishCost(queryDto, detailFoodList);
        //把门店中包含的子物料放在list中,作为央厨查询的条件
        //List<String> subIds = getSubMaterial(costList);
        //菜品维度的销售数据
        List<DishGrossProfitVo> sales = getSale(detailFoodList, ScmConstant.DimensionType.FOOD);
        //菜品维度将两者数据整合
        mergeShopByFoodNo(sales, costList);
        //查询央厨数据
        //将门店置为空
        queryDto.setShopIdList(null);
        //设置子物料集合
        //queryDto.setSubMaterialIds(subIds);
        //央厨本地数仓的company_id
        DictVo centVo = foodStyleAnalysisMapper.findDict(queryDto.getEnteId(), ScmConstant.FoodAnalysis.CENTER_CODE_CONFIG);
        queryDto.setCenterCode(centVo.getModelId());
        //央厨的成本数据
        List<GrossWeightVo> costCenterList = getDishCost(queryDto, detailFoodList);
        mergeCenterByFoodNo(sales, costCenterList);
        return sales;
    }


    //得到所有子物料
    private List<String> getSubMaterial(List<GrossWeightVo> costList) {
        List<String> subIds = new LinkedList<>();
        for (GrossWeightVo grossWeightVo : costList) {
            if (grossWeightVo.getSubMaterialId() != null) {
                subIds.add(grossWeightVo.getSubMaterialId());
            }
        }
        return subIds;
    }

    //查询基本销售数据
    private List<DishGrossProfitVo> getBaseCompareList(ScmQueryDto queryDto) {
        //查菜品汇总信息,设置不包含酒水的
        queryDto.setFoodStyleName(ScmConstant.FoodAnalysis.WINE);
        List<RepPosDetailFoodVo> detailFoodList = getBaseFoodSaleList(queryDto);
        //得到foodNo,shopId维度成本基本数据
        List<GrossWeightVo> costList = getDishCost(queryDto, detailFoodList);
        //菜品维度的销售数据
        List<DishGrossProfitVo> sales = getSale(detailFoodList, ScmConstant.DimensionType.FOOD);
        //菜品维度将两者数据整合
        mergeShopByFoodNo(sales, costList);
        return sales;
    }


    /**
     * @description 同比数据(去年同期与本期相比)
     * @author fancl
     * @date 2020/3/30
     * @param
     * @return
     */
    public List<DishGrossProfitVo> getTheSame(ScmQueryDto queryDto) {
        List<DishGrossProfitVo> currentDataList = getBaseCompareList(queryDto);
        //再查去年同期数据
        Date lastBeginDate = DateUtils.subYears(queryDto.getBeginDate(), Constant.Number.ONE);
        Date lastEndDate = DateUtils.subYears(queryDto.getEndDate(), Constant.Number.ONE);
        queryDto.setBeginDate(lastBeginDate);
        queryDto.setEndDate(lastEndDate);
        List<DishGrossProfitVo> theSameDataList = getBaseCompareList(queryDto);
        //整合本期和同期数据
        combine(currentDataList, theSameDataList);
        return currentDataList;
    }


    /**
     * @description 环比数据(上期同期与本期相比)
     * @author fancl
     * @date 2020/3/30
     * @param
     * @return
     */
    public List<DishGrossProfitVo> getChain(ScmQueryDto queryDto) {
        List<DishGrossProfitVo> currentDataList = getBaseCompareList(queryDto);
        //再查去年同期数据
        //把开始结束时间转化为同比时间
        List<Date> lastPeriodDate = DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
        queryDto.setBeginDate(lastPeriodDate.get(0));
        queryDto.setEndDate(lastPeriodDate.get(1));
        List<DishGrossProfitVo> chainDataList = getBaseCompareList(queryDto);
        //整合本期和上期数据
        combine(currentDataList, chainDataList);
        return currentDataList;
    }

    //合并同比数据
    private void combine(List<DishGrossProfitVo> currentList, List<DishGrossProfitVo> lastList) {
        //遍历本期数据,在同比数据中找到对应的进行合并计算
        for (DishGrossProfitVo current : currentList) {
            //如果上期没数据,那么本期的占比均设为0
            if (lastList.size() == 0) {
                current.setShopPercent(ReportDataConstant.Finance.PERCENT_ZERO_SIGN);
            } else {
                //如果在last中没匹配到,那么就把current中的shopPercent设置为0
                boolean isFind = false;
                for (DishGrossProfitVo last : lastList) {
                    if (current.getFoodNo().equals(last.getFoodNo()) && current.getFoodName().equals(last.getFoodName())) {
                        //本期毛利(shopProfit) 去年同期或上期毛利(centerProfit)
                        //设置去年同期或上期毛利
                        current.setCenterCost(last.getShopCost() == null ? BigDecimal.ZERO : last.getShopCost());
                        current.setCenterProfit(last.getShopProfit() == null ? BigDecimal.ZERO : last.getShopProfit());
                        //毛利同比或环比(（本期-去年同期）/去年同期)
                        //target:本期  source:上期
                        BigDecimal currentProfit = current.getShopProfit() == null ? BigDecimal.ZERO : current.getShopProfit();
                        BigDecimal lastProfit = last.getShopProfit() == null ? BigDecimal.ZERO : last.getShopProfit();
                        BigDecimal percent = BigDecimalUtils.getPercent(currentProfit.subtract(lastProfit), lastProfit);
                        current.setShopPercent(percent);
                        isFind = true;
                        break;
                    }
                }
                //没找到, 百分比设置为0
                if(!isFind){
                    current.setShopPercent(ReportDataConstant.Finance.PERCENT_ZERO_SIGN);
                }
            }

        }
    }


    //按照自定义维度分组 然后计算的平均销售价格和销量(销售价格是平均值,销量是汇总值)
    private List<DishGrossProfitVo> getSale(List<RepPosDetailFoodVo> detailFoodList, String dimension) {
        List<DishGrossProfitVo> sales = new ArrayList<>(128);
        Map<String, List<RepPosDetailFoodVo>> dimensionMap = null;
        //菜品维度
        if (ScmConstant.DimensionType.FOOD.equals(dimension)) {
            dimensionMap = detailFoodList.stream().collect(Collectors.groupingBy((d) -> foodDimens(d)));
        } else if (ScmConstant.DimensionType.FOOD_SHOP.equals(dimension)) {
            dimensionMap = detailFoodList.stream().collect(Collectors.groupingBy((d) -> foodAndShopDimens(d)));
        }
        dimensionMap.forEach((k, v) -> {
            DishGrossProfitVo dish = new DishGrossProfitVo();
            RepPosDetailFoodVo detailVo = v.get(0);
            //复制属性
            dish.setFoodStyleName(detailVo.getFoodStyleName());
            dish.setFoodNo(detailVo.getFoodNo());
            dish.setFoodName(detailVo.getFoodName());
            dish.setUnitName(detailVo.getUnitName());
            //门店维度,把门店信息加上
            if (ScmConstant.DimensionType.FOOD_SHOP.equals(dimension)) {
                dish.setShopId(detailVo.getShopId());
                dish.setShopName(detailVo.getShopName());
            }
            //将门店下的售价和销量进行计算
            BigDecimal avg = BigDecimal.ZERO;
            BigDecimal avgAll = BigDecimal.ZERO;
            BigDecimal saleNum = BigDecimal.ZERO;
            for (RepPosDetailFoodVo detailFoodVo : v) {
                avgAll = avgAll.add(BigDecimalUtils.ifNull2Zero(detailFoodVo.getFoodAvgPrice()));
                saleNum = saleNum.add(BigDecimalUtils.ifNull2Zero(detailFoodVo.getFoodNum()));
            }
            if (v.size() > 0) {
                avg = BigDecimalUtils.divideMethod(avgAll, new BigDecimal(String.valueOf(v.size())), 2);
            }
            //销售价格
            dish.setSalePrice(avg);
            //销量
            dish.setSaleNum(saleNum);
            sales.add(dish);
        });
        return sales;
    }

    //门店数据,菜品维度整合销售数据,生成最终格式
    private void mergeShopByFoodNo(List<DishGrossProfitVo> sales, List<GrossWeightVo> costVos) {
        Map<String, List<GrossWeightVo>> foodMap = costVos.stream().collect(Collectors.groupingBy(GrossWeightVo::getFoodNo));
        for (DishGrossProfitVo sale : sales) {
            if (foodMap.containsKey(sale.getFoodNo())) {
                //门店成本
                //成本=单个成本*销量
                sale.setShopCost(BigDecimalUtils.multiplyMethod(sale.getSaleNum(), getAmount(foodMap.get(sale.getFoodNo())), 2));
                //毛利=卖价*销量-成本
                sale.setShopProfit(BigDecimalUtils.multiplyMethod(sale.getSaleNum(), sale.getSalePrice(), 2).subtract(sale.getShopCost()));
                //毛利率= (毛利/销售金额)
                sale.setShopPercent(BigDecimalUtils.getPercent(sale.getShopProfit(),
                        BigDecimalUtils.multiplyMethod(sale.getSaleNum(), sale.getSalePrice(), 2)));
            }
        }
    }


    //央厨数据,菜品维度整合销售数据,生成最终格式
    private void mergeCenterByFoodNo(List<DishGrossProfitVo> sales, List<GrossWeightVo> costVos) {
        Map<String, List<GrossWeightVo>> foodMap = costVos.stream().collect(Collectors.groupingBy(GrossWeightVo::getFoodNo));
        for (DishGrossProfitVo sale : sales) {
            if (foodMap.containsKey(sale.getFoodNo())) {
                //央厨的成本
                sale.setCenterCost(BigDecimalUtils.multiplyMethod(sale.getSaleNum(), getAmount(foodMap.get(sale.getFoodNo())), 2));
                sale.setCenterProfit(BigDecimalUtils.multiplyMethod(sale.getSaleNum(), sale.getSalePrice(), 2).subtract(sale.getCenterCost()));
                BigDecimal centerCost = sale.getCenterCost() == null ? BigDecimal.ZERO : sale.getCenterCost();
                BigDecimal centerProfit = sale.getCenterProfit() == null ? BigDecimal.ZERO : sale.getCenterProfit();
                //央厨毛利率= 央厨的毛利/ (销量*价格)
                if (centerProfit.compareTo(BigDecimal.ZERO) != 0) {
                    sale.setCenterPercent(BigDecimalUtils.getPercent(centerProfit, sale.getSaleNum().multiply(sale.getSalePrice())));
                }
                //差异成本,差异毛利率
                //差异成本 =门店成本-央厨成本,(门店毛利-央厨毛利)/销售价格
                BigDecimal shopCost = sale.getShopCost() == null ? BigDecimal.ZERO : sale.getShopCost();
                sale.setDifferentCost(shopCost.subtract(centerCost));
                //差异毛利=门店毛利-央厨毛利
                BigDecimal shopProfit = sale.getShopProfit() == null ? BigDecimal.ZERO : sale.getShopProfit();
                sale.setDifferentProfit(shopProfit.subtract(centerProfit));
                sale.setDifferentPercent(BigDecimalUtils.getPercent(
                        sale.getDifferentProfit(),
                        sale.getSalePrice().multiply(sale.getSaleNum())
                ));
            }
        }
    }

    //合计成本金额
    private BigDecimal getAmount(List<GrossWeightVo> detailList) {
        BigDecimal amount = BigDecimal.ZERO;
        for (GrossWeightVo detail : detailList) {
            amount = amount.add(detail.getCost() == null ? BigDecimal.ZERO : detail.getCost());
        }
        return amount;
    }

    //查询POS基本菜品销量
    private List<RepPosDetailFoodVo> getBaseFoodSaleList(ScmQueryDto queryDto) {
        //维度是foodNo,shopId
        List<RepPosDetailFoodVo> detailFoodList = foodStyleAnalysisMapper.findFoodGrossProfit(queryDto);
        //如果没有菜品,返回空数据
        if (detailFoodList == null || detailFoodList.isEmpty()) {
            return new ArrayList<>();
        }
        return detailFoodList;
    }

    /**
     * @description 计算菜品总成本的公共方法
     * @author fancl
     * @date 2020/3/27
     * 入参:
     * queryDto.shopIdList:门店ids
     * queryDto.beginDate:开始日期
     * queryDto.endDate:开始日期
     * queryDto.shopIdList:门店id集合
     * queryDto.foods: (foodNo,foodName)的list
     * @param detailFoodList 菜品销量数据
     * @return GrossWeightVo 门店foodNo维度的结果集
     */
    @Override
    public List<DishGrossProfitVo> getDishBaseList(ScmQueryDto queryDto, List<RepPosDetailFoodVo> detailFoodList) {
        //外部方法调用 ,detailFoodList 为null
        if (detailFoodList == null) {
            detailFoodList = getBaseFoodSaleList(queryDto);
        }
        //得到成本基本数据
        List<GrossWeightVo> dishCost = getDishCost(queryDto, detailFoodList);
        //得到菜品和门店维度的销售数据
        List<DishGrossProfitVo> sales = getSale(detailFoodList, ScmConstant.DimensionType.FOOD_SHOP);
        //合并两者数据
        mergeFoodAndShop(sales, dishCost);
        return sales;
    }

    //将菜品成本放入目标对象
    private void mergeFoodAndShop(List<DishGrossProfitVo> sales, List<GrossWeightVo> dishCost) {
        Map<String, List<GrossWeightVo>> costMap = dishCost.stream().collect(Collectors.groupingBy((dish) -> foodAndShopDimens(dish)));
        for (DishGrossProfitVo sale : sales) {
            if (costMap.containsKey(sale.getFoodNo() + sale.getShopId())) {
                //菜品成本
                sale.setCostAmount(BigDecimalUtils.multiplyMethod(sale.getSaleNum(), getAmount(costMap.get(sale.getFoodNo() + sale.getShopId())), 2));
                sale.setCostPrice(getAmount(costMap.get(sale.getFoodNo() + sale.getShopId())));
            }
        }
    }

    //成本数据列表
    private List<GrossWeightVo> getDishCost(ScmQueryDto queryDto, List<RepPosDetailFoodVo> detailFoodList) {
        //查毛重
        //构造查询实体
        SimpleScmQueryDto simpleDto = new SimpleScmQueryDto();
        simpleDto.setOrgIds(queryDto.getShopIdList());
        simpleDto.setCenterCode(queryDto.getCenterCode());
        //如果是央厨,限定子物料范围
        if (queryDto.getCenterCode() != null) {
            simpleDto.setIsCenter(true);

        }
        List<GrossWeightVo> grossWeightList = getGrossWeightList(simpleDto, detailFoodList);

        if (LogConstant.Scm.IS_LOG.equals(queryDto.getNeedLog())) {
            logger.info("毛重原始数据:" + grossWeightList);
        }
        //如果是央厨,将毛重中包含的子物料id去重
        List<String> materialNumbers = null;
        if (queryDto.getCenterCode() != null) {
            Map<String, List<GrossWeightVo>> subIdMap = grossWeightList.stream().collect(Collectors.groupingBy(GrossWeightVo::getMaterialNumber));
            materialNumbers = new LinkedList<>();
            for (String mateNumber : subIdMap.keySet()) {
                materialNumbers.add(mateNumber);
            }
            queryDto.setMaterialNumbers(materialNumbers);
        }

        //将毛重list中的子物料id抽取出来,作为查询菜品单价
        long start = System.currentTimeMillis();
        //查单价时,以结束时间上个月的最后一天
        Date endDate = queryDto.getEndDate();
        Date[] dates = DateUtils.lastMonth(endDate);
        ScmQueryDto query = new ScmQueryDto();
        query.setEnteId(queryDto.getEnteId());
        query.setShopIdList(queryDto.getShopIdList());
        query.setCenterCode(queryDto.getCenterCode());
        query.setMaterialNumbers(queryDto.getMaterialNumbers());
        query.setEndDate(dates[1]);
        List<SubMaterialPriceVo> subMaterialsPrice = null;
        //央厨的价格
        if (queryDto.getCenterCode() != null) {
            subMaterialsPrice = foodStyleAnalysisMapper.getPriceBySubMaterialsByCenter(query);
        } else {
            //2020-04-08update发现查询门店的要把发货组织设定为央厨
            //央厨本地数仓的company_id
            DictVo centVo = foodStyleAnalysisMapper.findDict(queryDto.getEnteId(), ScmConstant.FoodAnalysis.CENTER_CODE_CONFIG);
            query.setCenterCode(centVo.getModelId());
            subMaterialsPrice = foodStyleAnalysisMapper.getPriceBySubMaterials(query);
        }
        if (LogConstant.Scm.IS_LOG.equals(queryDto.getNeedLog())) {
            logger.info("单价原始数据:" + subMaterialsPrice);
        }
        long end = System.currentTimeMillis();
        logger.info("查询单价耗时(毫秒):" + (end - start));
        //过滤掉单价为null或为0的
        subMaterialsPrice = subMaterialsPrice.stream().filter(price -> price.getPrice() != null && price.getPrice().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
        start = System.currentTimeMillis();
        List<GrossWeightVo> grossWeightVos = calcDishPrice(grossWeightList, subMaterialsPrice);
        if (LogConstant.Scm.IS_LOG.equals(queryDto.getNeedLog())) {
            logger.info("grossWeightVos:" + grossWeightVos);
        }
        end = System.currentTimeMillis();
        logger.info("匹配成本耗时(毫秒):" + (end - start));
        //过滤掉结果为null或为0的
        grossWeightVos = grossWeightVos.stream().filter(weightVo -> weightVo.getCost() != null && weightVo.getCost().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
        return grossWeightVos;

    }

    //计算菜品单价,生成菜品,门店维度的基本数据类型
    private List<GrossWeightVo> calcDishPrice(List<GrossWeightVo> grossWeightList, List<SubMaterialPriceVo> subMaterials) {
        //维度为shopid foodNo的菜品价格list
        final List<GrossWeightVo> costList = new LinkedList<>();
        //子物料价格,按shopid(也可能是组织id)分组,即门店的子物料价格
        Map<String, List<SubMaterialPriceVo>> shopPriceMap = subMaterials.stream().filter(sub -> sub.getShopId() != null)
                .collect(Collectors.groupingBy(SubMaterialPriceVo::getShopId));
        //将毛重list 和子物料list分别按子物料id分组
        //毛重list两次分组,第一层是foodNo,第二层是shopid
        Map<String, List<GrossWeightVo>> grossFoodNoMap = grossWeightList.stream().filter(go -> go.getFoodNo() != null)
                .collect(Collectors.groupingBy(GrossWeightVo::getFoodNo));
        //二次分组后,将和shopPriceMap中的数据进行匹配计算,得到每个门店下的菜品价格合计
        grossFoodNoMap.forEach((k, v) -> {
            Map<String, List<GrossWeightVo>> shopWeight = v.stream().filter((g -> g.getShopId() != null))
                    .collect(Collectors.groupingBy(GrossWeightVo::getShopId));
            //遍历菜品下的门店维度的各个子物料, 形成最终的价格
            for (Map.Entry<String, List<GrossWeightVo>> entry : shopWeight.entrySet()) {
                //门店id
                String k1 = entry.getKey();
                //子物料毛重list
                List<GrossWeightVo> v1 = entry.getValue();
                //如果价格map没有该门店,那么抛异常
                if (!shopPriceMap.containsKey(k1)) {
                    //先注释掉
                    //throw new ServiceException(ResultCode.SCM_SHOP_PRICE_CONFIG_LACK);

                }
                //计算门店子物料价格
                //shopPriceMap.get(k1)：价格数组中shopId下面的所有子物料列表
                //v1:毛重数据中shopId下面所有数据
                //weightCostVo是同一个菜品同一个门店下的数据
                GrossWeightVo weightCostVo = matchPrice(shopPriceMap.get(k1), v1);
                costList.add(weightCostVo);
            }
        });
        //logger.info("计算成本后的数据:" + costList);
        return costList;
    }


    //根据子物料毛重和价格匹配计算,返回一个包含菜品,门店信息的菜品成本
    private GrossWeightVo matchPrice(List<SubMaterialPriceVo> priceList, List<GrossWeightVo> weightList) {

        GrossWeightVo weightCostVo = new GrossWeightVo();
        GrossWeightVo weight0 = weightList.get(0);
        //设置附属信息
        weightCostVo.setShopId(weight0.getShopId());
        weightCostVo.setFoodNo(weight0.getFoodNo());
        weightCostVo.setFoodName(weight0.getFoodName());
        BigDecimal cost = BigDecimal.ZERO;
        //非空判断
        if (priceList == null || weightList == null) {
            return weightCostVo;
        }
        //logger.info("priceList:" + priceList);
        //priceList = priceList.stream().filter(pr->pr.getSubMaterialId().equals("26592213341787453")).collect(Collectors.toList());
        for (GrossWeightVo weight : weightList) {
            //在价格中匹配到物料,然后计算
            for (SubMaterialPriceVo price : priceList) {
                if (price.getMaterialNumber() != null && weight.getMaterialNumber() != null) {
                    if (price.getMaterialNumber().equals(weight.getMaterialNumber())) {
                        BigDecimal TempCost = BigDecimalUtils.multiplyMethod(weight.getGrossWeight(), price.getPrice(), 6);
                        cost = cost.add(TempCost);
                        //匹配到就可以进行下一次外层循环了
                        break;
                    }
                }
            }
        }
        weightCostVo.setCost(cost);

        return weightCostVo;
    }

    //查毛重
    private List<GrossWeightVo> getGrossWeightList(SimpleScmQueryDto simpleScmQueryDto, List<RepPosDetailFoodVo> detailFoodList) {
        //如果是央厨的 按foodNo分组,按foodNo去重
        if (simpleScmQueryDto.getIsCenter() != null && simpleScmQueryDto.getIsCenter() == true) {
            Map<String, List<RepPosDetailFoodVo>> foodNoMap = detailFoodList.stream()
                    .filter(detail -> detail.getFoodNo() != null)
                    .collect(Collectors.groupingBy(RepPosDetailFoodVo::getFoodNo));
            //将map中的数据放到list作为参数
            List<SimpleFoodDto> foodQueryList = new LinkedList<>();
//            foodNoMap.forEach((k, v) -> {
//                SimpleFoodDto simpleFoodDto = new SimpleFoodDto();
//                simpleFoodDto.setFoodNo(k);
//                simpleFoodDto.setFoodName(v.get(0).getFoodName());
//                foodQueryList.add(simpleFoodDto);
//            });
            simpleScmQueryDto.setFoods(foodQueryList);
        }
        //查毛重
        return foodStyleAnalysisMapper.getGrossWeight(simpleScmQueryDto);
    }

    @Override
    public void exportFoodGrossProfit(ExcelExportDto excelExportDto, HttpServletResponse response) {
        ScmQueryDto queryDto = convert2ScmQueryDto(excelExportDto);
        List<DishGrossProfitVo> foodGrossProfit = getFoodGrossProfit(queryDto);
        //构造一级表头
        List<String> oneLevelHead = Arrays.asList(
                ExcelColumnConstant.FoodGrossProfit.FOOD_STYLE_NAME.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.FOOD_NAME.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.UNIT_NAME.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.SALE_PRICE.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.SALE_NUM.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.SHOP.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.SHOP.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.SHOP.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.CENTER.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.CENTER.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.CENTER.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.DIFF.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.DIFF.getTitle(),
                ExcelColumnConstant.FoodGrossProfit.DIFF.getTitle());
        //导出
        fileService.exportExcelForQueryTerm(response, excelExportDto, foodGrossProfit, oneLevelHead,
                ExcelColumnConstant.FoodGrossProfit.FOOD_STYLE_NAME,
                ExcelColumnConstant.FoodGrossProfit.FOOD_NAME,
                ExcelColumnConstant.FoodGrossProfit.UNIT_NAME,
                ExcelColumnConstant.FoodGrossProfit.SALE_PRICE,
                ExcelColumnConstant.FoodGrossProfit.SALE_NUM,
                ExcelColumnConstant.FoodGrossProfit.SHOP_COST,
                ExcelColumnConstant.FoodGrossProfit.SHOP_PROFIT,
                ExcelColumnConstant.FoodGrossProfit.SHOP_PERCENT,
                ExcelColumnConstant.FoodGrossProfit.CENTER_COST,
                ExcelColumnConstant.FoodGrossProfit.CENTER_PROFIT,
                ExcelColumnConstant.FoodGrossProfit.CENTER_PERCENT,
                ExcelColumnConstant.FoodGrossProfit.DIFFERENT_COST,
                ExcelColumnConstant.FoodGrossProfit.DIFFERENT_PROFIT,
                ExcelColumnConstant.FoodGrossProfit.DIFFERENT_PERCENT
        );


    }

    private ScmQueryDto convert2ScmQueryDto(ExcelExportDto excelExportDto) {
        ScmQueryDto scmQueryDto = new ScmQueryDto();
        scmQueryDto.setEnteId(excelExportDto.getEnteId());
        scmQueryDto.setBeginDate(excelExportDto.getBeginDate());
        scmQueryDto.setEndDate(excelExportDto.getEndDate());
        scmQueryDto.setShopIdList(excelExportDto.getShopIdList());
        return scmQueryDto;
    }

    @Override
    public void exportFoodGrossProfitTheSame(ExcelExportDto excelExportDto, HttpServletResponse response) {
        ScmQueryDto queryDto = convert2ScmQueryDto(excelExportDto);
        List<DishGrossProfitVo> foodGrossProfit = getTheSame(queryDto);
        //导出
        fileService.exportExcelForQueryTerm(response, excelExportDto, foodGrossProfit,
                ExcelColumnConstant.FoodGrossProfitTheSame.FOOD_STYLE_NAME,
                ExcelColumnConstant.FoodGrossProfitTheSame.FOOD_NAME,
                ExcelColumnConstant.FoodGrossProfitTheSame.UNIT_NAME,
                ExcelColumnConstant.FoodGrossProfitTheSame.SHOP_PROFIT,
                ExcelColumnConstant.FoodGrossProfitTheSame.CENTER_PROFIT,
                ExcelColumnConstant.FoodGrossProfitTheSame.SHOP_PERCENT
        );
    }

    @Override
    public void exportFoodGrossProfitChain(ExcelExportDto excelExportDto, HttpServletResponse response) {
        ScmQueryDto queryDto = convert2ScmQueryDto(excelExportDto);
        queryDto.setDateType(excelExportDto.getDateType());
        List<DishGrossProfitVo> foodGrossProfit = getChain(queryDto);
        //导出
        fileService.exportExcelForQueryTerm(response, excelExportDto, foodGrossProfit,
                ExcelColumnConstant.FoodGrossProfitChain.FOOD_STYLE_NAME,
                ExcelColumnConstant.FoodGrossProfitChain.FOOD_NAME,
                ExcelColumnConstant.FoodGrossProfitChain.UNIT_NAME,
                ExcelColumnConstant.FoodGrossProfitChain.SHOP_PROFIT,
                ExcelColumnConstant.FoodGrossProfitChain.CENTER_PROFIT,
                ExcelColumnConstant.FoodGrossProfitChain.SHOP_PERCENT
        );
    }
}

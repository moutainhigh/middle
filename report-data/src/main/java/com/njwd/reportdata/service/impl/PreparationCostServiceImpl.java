package com.njwd.reportdata.service.impl;

import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.LogConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.FinBalanceSubjectVo;
import com.njwd.entity.reportdata.vo.fin.*;
import com.njwd.report.mapper.FinBalanceSubjectMapper;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.reportdata.service.BaseShopService;
import com.njwd.reportdata.service.PreparationCostService;
import com.njwd.service.FileService;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.DateUtils;
import com.njwd.utils.MergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 筹建成本实现类
 * @Author fancl
 * @Date 2019/11/20
 */
@Service
public class PreparationCostServiceImpl implements PreparationCostService {

    @Autowired
    FinanceSubjectService financeSubjectService;

    @Autowired
    BaseShopService baseShopService;

    @Autowired
    FinBalanceSubjectMapper finBalanceSubjectMapper;

    @Autowired
    private FileService fileService;
    //日志对象
    private Logger logger = LoggerFactory.getLogger(PreparationCostServiceImpl.class);

    /**
     * @description 工程明细查询
     * @author fancl
     * @date 2020/1/13
     * @param
     * @return java.util.List<java.lang.String>
     */
    @Override
    public List<FinSubjectVo> getProjectDetail(FinQueryDto queryDto) {

        //1604取查询期间末级科目借方发生额合计,并不需要公式配置
        //取得科目配置
        FinReportConfigVo finConfigAndFormula = financeSubjectService.getConfigByGroupAndType(queryDto.getEnteId(),
                ReportDataConstant.FinType.PROJECT_DETAIL_GROUP, ReportDataConstant.FinType.PROJECT_DETAIL);
        //只配了1604 ,就把1604放在请求参数即可
        List<String> subjectCodeList = new ArrayList<>();
        String[] codeArr = finConfigAndFormula.getCodes().split("\\,");
        for (String code : codeArr) {
            subjectCodeList.add(code);
        }
        queryDto.setSubjectCodeList(subjectCodeList);
        queryDto.setSubjectCodeLen(ReportDataConstant.Finance.SUBJECT_CODE_LENGTH);
        //需要从科目余额表中查询的科目
        List<FinReportVo> listAll = financeSubjectService.getSubjectData(queryDto);
        listAll = listAll.stream().filter(fin -> fin.getDebitAmount() != null
                && fin.getDebitAmount().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());

        //门店 科目维度行
        //现在数据仍是门店 / 科目 维度
        //按门店分组
        Map<String, List<FinReportVo>> shopMap = listAll.stream().filter(shop -> shop.getShopId() != null).collect(Collectors.groupingBy(FinReportVo::getShopId));

        //原始list中一共包含多少个科目,用于后续判断
        Map<String, FinReportVo> subjectIdMap = new LinkedHashMap<>();
        listAll.stream().forEach(vo -> subjectIdMap.computeIfAbsent(vo.getAccountSubjectId(), k -> vo));

        //按品牌分组
        Map<String, List<FinReportVo>> brandMap = listAll.stream().filter(brand -> brand.getBrandId() != null).collect(Collectors.groupingBy(FinReportVo::getBrandId));
        //按区域分组
        Map<String, List<FinReportVo>> regionMap = listAll.stream().filter(region -> region.getRegionId() != null).collect(Collectors.groupingBy(FinReportVo::getRegionId));

        List<FinSubjectVo> shopSubjectList = generateProductDetailList(shopMap, subjectIdMap, ReportDataConstant.Finance.TYPE_SHOP);
        //门店数据排序
        List<FinSubjectVo> shopSubjectListSorted = sortShopListProjectDetail(shopSubjectList);
        //品牌数据排序
        List<FinSubjectVo> brandSubjectList = generateProductDetailList(brandMap, subjectIdMap, ReportDataConstant.Finance.TYPE_BRAND);
        List<FinSubjectVo> brandSubjectListSorted = sortBrandListProjectDetail(brandSubjectList);
        //区域数据排序
        List<FinSubjectVo> regionSubjectList = generateProductDetailList(regionMap, subjectIdMap, ReportDataConstant.Finance.TYPE_REGION);
        List<FinSubjectVo> regionSubjectListSorted = sortRegionListProjectDetail(regionSubjectList);
        //结果集
        List<FinSubjectVo> finList = new ArrayList<>();

        finList.addAll(shopSubjectListSorted);
        finList.addAll(brandSubjectListSorted);
        finList.addAll(regionSubjectListSorted);

        //全部数据合计
        FinSubjectVo finTypeIsAll = getFinTypeIsAll(shopSubjectList);
        finList.add(finTypeIsAll);

        //打印科目发生额
        //Gson gson = new Gson();
        //String s = gson.toJson(finList);
        //System.out.println(s);

        return finList;
    }

    //门店数据排序
    private List<FinSubjectVo> sortShopListProjectDetail(List<FinSubjectVo> shopSubjectList) {
        return shopSubjectList.stream().filter(shop -> shop.getBrandId() != null && shop.getRegionId() != null && shop.getShopId() != null)
                .sorted(Comparator.comparing(FinSubjectVo::getBrandId)
                        .thenComparing(FinSubjectVo::getRegionId)
                        .thenComparing(FinSubjectVo::getShopId))
                .collect(Collectors.toList());
    }


    //品牌数据排序
    private List<FinSubjectVo> sortBrandListProjectDetail(List<FinSubjectVo> brandSubjectList) {
        return brandSubjectList.stream().filter(brand -> brand.getBrandId() != null)
                .sorted(Comparator.comparing(FinSubjectVo::getBrandId)).collect(Collectors.toList());
    }

    //区域数据排序
    private List<FinSubjectVo> sortRegionListProjectDetail(List<FinSubjectVo> regionSubjectList) {
        return regionSubjectList.stream().filter(region -> region.getBrandId() != null && region.getRegionId() != null)
                .sorted(Comparator.comparing(FinSubjectVo::getBrandId)
                        .thenComparing(FinSubjectVo::getRegionId))
                .collect(Collectors.toList());
    }

    //把科目余额对象转为finReport对象
    private List<FinReportVo> trans2FinReport(List<FinBalanceSubjectVo> balanceSubjectList) {
        List<FinReportVo> finReportVoList = new ArrayList<>();
        for (FinBalanceSubjectVo balance : balanceSubjectList) {
            FinReportVo reportVo = new FinReportVo();
            reportVo.setBrandId(balance.getBrandId());
            reportVo.setBrandName(balance.getBrandName());
            reportVo.setRegionId(balance.getRegionId());
            reportVo.setRegionName(balance.getRegionName());
            reportVo.setShopId(balance.getShopId());
            reportVo.setShopName(balance.getShopName());
            reportVo.setAccountSubjectId(balance.getAccountSubjectId());
            reportVo.setAccountSubjectCode(balance.getAccountSubjectCode());
            reportVo.setAccountSubjectName(balance.getAccountSubjectName());
            reportVo.setAmount(balance.getCloseBalance());
            finReportVoList.add(reportVo);
        }
        return finReportVoList;
    }

    private FinSubjectVo getFinTypeIsAll(List<FinSubjectVo> shopSubjectList) {
        FinSubjectVo finTypeIsAll = new FinSubjectVo();
        finTypeIsAll.setType(ReportDataConstant.Finance.ALL_SUBJECT);
        finTypeIsAll.setBrandId(ReportDataConstant.Finance.EMPTY_STRING);
        finTypeIsAll.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        finTypeIsAll.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
        finTypeIsAll.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        finTypeIsAll.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
        finTypeIsAll.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        //总金额
        final BigDecimal[] allAmount = {BigDecimal.ZERO};
        //各科目合计金额
        LinkedHashMap<String, FinSubjectInner> subjectMap = new LinkedHashMap<>();
        //先算总金额
        shopSubjectList.stream().forEach(subject -> {
            allAmount[0] = allAmount[0].add(subject.getAllAmount());
        });
        //再算每个子项的金额及占比
        shopSubjectList.stream().forEach(subject -> {
            subject.getSubjectMap().forEach((k, v) -> {
                //如有没有就直接取科目对象 ,否则进行累加
                //包含这个科目时 ,金额累加
                if (subjectMap.containsKey(k)) {
                    FinSubjectInner finSubjectInner = subjectMap.get(k);
                    finSubjectInner.setAmount(finSubjectInner.getAmount().add(v.getAmount()));
                    subjectMap.put(k, finSubjectInner);
                }
                //第一次出现就新建
                else {
                    FinSubjectInner finSubjectInner = new FinSubjectInner();
                    finSubjectInner.setAccountSubjectId(k);
                    finSubjectInner.setAccountSubjectName(v.getAccountSubjectName());
                    finSubjectInner.setAmount(v.getAmount());
                    subjectMap.put(k, finSubjectInner);
                }
            });
        });
        //最后算一下每一项的占比
        subjectMap.forEach((k, v) -> {
            BigDecimal percent = BigDecimalUtils.getPercent(v.getAmount(), allAmount[0]);
            v.setRatio(percent);
        });

        //设置总金额
        finTypeIsAll.setAllAmount(allAmount[0]);
        //设置科目集合
        finTypeIsAll.setSubjectMap(subjectMap);
        return finTypeIsAll;

    }

    /**
     * @description 计算工程明细各维度
     * @author fancl
     * @date 2020/1/13
     * @param dimension 维度map
     * @param subjectIdMap 科目包含的id Map
     * @param type 维度类型  shop brand region
     * @return
     */
    private List<FinSubjectVo> generateProductDetailList(Map<String, List<FinReportVo>> dimension, Map<String, FinReportVo> subjectIdMap, String type) {
        //按照维度生成list
        List<FinSubjectVo> dimensionList = new ArrayList<>();
        final String[] regionId = {ReportDataConstant.Finance.EMPTY_STRING};
        final String[] regionName = {ReportDataConstant.Finance.ALL_REGION};
        final String[] shopId = {ReportDataConstant.Finance.EMPTY_STRING};
        final String[] shopName = {ReportDataConstant.Finance.ALL_SHOP};
        dimension.forEach((k, v) -> {
            //按门店维度将数据放入shopList
            FinReportVo vo = v.get(0);
            FinSubjectVo subjectVo = new FinSubjectVo();
            subjectVo.setType(type);
            subjectVo.setBrandId(vo.getBrandId());
            subjectVo.setBrandName(vo.getBrandName());
            //按门店维度
            if (ReportDataConstant.Finance.TYPE_SHOP.equals(type)) {
                regionId[0] = vo.getRegionId();
                regionName[0] = vo.getRegionName();
                shopId[0] = vo.getShopId();
                shopName[0] = vo.getShopName();
            }
            //按品牌维度
            else if (ReportDataConstant.Finance.TYPE_BRAND.equals(type)) {
                regionId[0] = ReportDataConstant.Finance.EMPTY_STRING;
                regionName[0] = ReportDataConstant.Finance.ALL_REGION;
                shopId[0] = ReportDataConstant.Finance.EMPTY_STRING;
                shopName[0] = ReportDataConstant.Finance.ALL_SHOP;
            }
            //按区域维度
            else if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                regionId[0] = vo.getRegionId();
                regionName[0] = vo.getRegionName();
                shopId[0] = ReportDataConstant.Finance.EMPTY_STRING;
                shopName[0] = ReportDataConstant.Finance.ALL_SHOP;
            }
            subjectVo.setRegionId(regionId[0]);
            subjectVo.setRegionName(regionName[0]);
            subjectVo.setShopId(shopId[0]);
            subjectVo.setShopName(shopName[0]);
            //总金额
            final BigDecimal[] allAmount = {BigDecimal.ZERO};
            v.stream().forEach(finReportVo -> allAmount[0] = allAmount[0].add(finReportVo.getDebitAmount().compareTo(BigDecimal.ZERO)<0?finReportVo.getDebitAmount().abs():finReportVo.getDebitAmount()));
            subjectVo.setAllAmount(allAmount[0]);

            LinkedHashMap<String, FinSubjectInner> inner = new LinkedHashMap<>();
            //把门店Key包含的list,按照科目id汇总,每个map<key List<FinReportVo>> 中的金额合计就是该科目的金额
            Map<String, List<FinReportVo>> collectBySubject = v.stream().filter(fin -> fin.getAccountSubjectId() != null)
                    .collect(Collectors.groupingBy(FinReportVo::getAccountSubjectId));
            //遍历全部科目id,目的是生成包含全部subjectId key 的map数据
            subjectIdMap.forEach((k1, v1) -> {
                //按科目id顺序,如果包含则计算其中的金额合计,否则金额为0
                if (collectBySubject.containsKey(k1)) {
                    //把collectBySubject中对应list的金额相加
                    final BigDecimal[] amount = {BigDecimal.ZERO};
                    collectBySubject.get(k1).stream().forEach(rep -> {
                        BigDecimal debitAmount = rep.getDebitAmount();
                        //null转为0
                        if(debitAmount==null){
                            debitAmount = BigDecimal.ZERO;
                        }
                        //金额合计用绝对值
                        if(debitAmount.compareTo(BigDecimal.ZERO)<0){
                            debitAmount=debitAmount.abs();
                        }
                        amount[0] = amount[0].add(debitAmount);
                    });
                    //构造科目对象
                    inner.put(k1, new FinSubjectInner(k1, collectBySubject.get(k1).get(0).getAccountSubjectName(), amount[0], BigDecimalUtils.getPercent(amount[0], allAmount[0])));

                } else {
                    //科目id对应金额和比例
                    inner.put(k1, new FinSubjectInner(k1, subjectIdMap.get(k1).getAccountSubjectName(), BigDecimal.ZERO, ReportDataConstant.Finance.PERCENT_ZERO_SIGN));

                }
            });

            //将科目Map对象放入对象中
            subjectVo.setSubjectMap(inner);
            //将对象放入返回list
            dimensionList.add(subjectVo);
        });

        return dimensionList;
    }


    /**
     * @description 筹建成本对比
     * @author fancl
     * @date 2020/1/14
     * @param
     * @return
     */
    @Override
    public List<FinCostCompareVo> compareCost(FinQueryDto queryDto) {
        /***
         * 以下三个科目code是精确匹配
         * 1601 <=0220的累计
         * 长期待摊费用1801(资产类)的借方发生额: <=0220 的累计
         * 管理费6602.67(费用科目)的借方发生额: <=0220的累计
         * 先用三个科目的发生额合计  ,然后减去1601凭证摘要包含'增补'的发生额
         */

        //查科目主公式配置,该公式是标准公式: subjectCode:direction:运算符
        FinReportConfigVo finConfigMain = financeSubjectService.getFinConfigAndFormula(queryDto.getEnteId(),
                ReportDataConstant.FinType.COMPARE_COST_GROUP,
                ReportDataConstant.FinType.COMPARE_COST);
        //把原始的科目code保存 ,因为区域数据查询时会用到
        List<String> regionSubjectCodes = finConfigMain.getSubjectCodeList();
        queryDto.setSubjectCodeList(regionSubjectCodes);
        //科目方式 :左匹配
        queryDto.setMatchSubjectType(ReportDataConstant.Finance.MATCH_SUBJECT_LEFT);
        //因为只查截止日期 ,把end设置为true
        queryDto.setOnlyEnd(true);
        List<FinReportVo> shopSubjects = financeSubjectService.getSubjectData(queryDto);
        //只留下金额不为null同时大于0的
        //List<FinReportVo> listAll = shopSubjects.stream().filter(subject -> subject.getAmount() != null && subject.getAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
        //将list 按shopId分组
        Map<String, List<FinReportVo>> shopCollect = shopSubjects.stream().filter(fin -> fin.getShopId() != null).collect(Collectors.groupingBy(FinReportVo::getShopId));

        //计算主公式金额合计 key为门店id value为金额
        Map<String, BigDecimal> shopAmountByFormula = financeSubjectService.getShopAmountByFormula(shopCollect, finConfigMain.getFormulaExpList());
        //遍历
        //得到需要减去 '增补'摘要的金额 map
        Map<String, BigDecimal> shopAmountReduceMap = getReduceAmount(queryDto, ReportDataConstant.Finance.TYPE_SHOP);

        //查询门店数据
        BaseShopDto baseShopDto = new BaseShopDto();
        baseShopDto.setEnteId(queryDto.getEnteId());
        baseShopDto.setShopIdList(queryDto.getShopIdList());
        baseShopDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<BaseShopVo> shopInfos = baseShopService.findShopInfoForArea(baseShopDto);

        //将科目类型数据转换为成本类型  costListAll为成本类型的list
        List<FinCostCompareVo> costListAll = new ArrayList<>();
        fillCost(shopSubjects, costListAll);

        //区域平均成本=区域筹建成本/区域店面积
        //得到区域维度的平均数据,要根据shopId列表取出regionId列表 再算一遍
        //此处重新new一个查询实体
        FinQueryDto queryRegionDto = getQueryRegionDto(queryDto);
        //使用原始的科目codes
        queryRegionDto.setSubjectCodeList(regionSubjectCodes);
        Map<String, BigDecimal> regionAvgCostMap = getRegionAvgAmount(shopInfos, queryRegionDto, finConfigMain.getFormulaExpList());

        //按门店分组
        Map<String, List<FinCostCompareVo>> shopMap = costListAll.stream().filter(shop -> shop.getShopId() != null).collect(Collectors.groupingBy(FinCostCompareVo::getShopId));
        //生成门店维度的成本数据
        List<FinCostCompareVo> shopCostList = getCostCompare(shopMap, ReportDataConstant.Finance.TYPE_SHOP, shopAmountByFormula, regionAvgCostMap, shopAmountReduceMap);

        //将门店面积字段填充到 门店维度数据
        MergeUtil.merge(shopCostList, shopInfos,
                FinCostCompareVo::getShopId, BaseShopVo::getShopId,
                (costCompareVo, baseShopVo) -> {
                    //将店面积和单位成本设置一下
                    costCompareVo.setShopArea(baseShopVo.getShopArea() == null ? BigDecimal.ZERO : baseShopVo.getShopArea());
                    costCompareVo.setUnitCost(BigDecimalUtils.getDivide(costCompareVo.getCost(), costCompareVo.getShopArea()));
                    //区域平均成本弄过来
                    if (regionAvgCostMap.containsKey(costCompareVo.getRegionId())) {
                        costCompareVo.setAvgRegionCost(regionAvgCostMap.get(costCompareVo.getRegionId()));
                    }
                }
        );

        //差异率=（单位筹建成本-区域平均成本）/区域平均成本
        //按品牌分组
        Map<String, List<FinCostCompareVo>> brandMap = shopCostList.stream().filter(shop -> shop.getBrandId() != null).collect(Collectors.groupingBy(FinCostCompareVo::getBrandId));

        //按区域分组
        Map<String, List<FinCostCompareVo>> regionMap = shopCostList.stream().filter(region -> region.getRegionId() != null).collect(Collectors.groupingBy(FinCostCompareVo::getRegionId));

        //区域维度数据,此时把区域的的筹建成本计算出来
        List<FinCostCompareVo> costRegionList = getCostCompare(regionMap, ReportDataConstant.Finance.TYPE_REGION, null, regionAvgCostMap, null);


        //品牌维度,区域平均成本和差异率这两个值为空,先写0
        List<FinCostCompareVo> costBrandList = getCostCompare(brandMap, ReportDataConstant.Finance.TYPE_BRAND, null, regionAvgCostMap, null);


        //把门店维度数据中的 区域平均成本 差异率 赋值
        FinCostCompareVo fin = fillAvgAndDiff(shopCostList, regionAvgCostMap);

        //结果集
        List<FinCostCompareVo> finList = new LinkedList<>();
        //门店数据排序
        List<FinCostCompareVo> costShopListSorted = shopCostList.stream()
                .filter(shop -> shop.getBrandId() != null && shop.getRegionId() != null && shop.getShopId() != null)
                .sorted(Comparator.comparing(FinCostCompareVo::getBrandId).
                        thenComparing(FinCostCompareVo::getRegionId).
                        thenComparing(FinCostCompareVo::getShopId))
                .collect(Collectors.toList());
        //将门店维度数据加入结果集
        finList.addAll(costShopListSorted);
        //品牌数据排序
        List<FinCostCompareVo> costBrandListSorted = costBrandList.stream().filter(brand -> brand.getBrandId() != null)
                .sorted(Comparator.comparing(FinCostCompareVo::getBrandId)
                ).collect(Collectors.toList());

        finList.addAll(costBrandListSorted);

        List<FinCostCompareVo> costRegionListSorted = costRegionList.stream()
                .filter(region -> region.getBrandId() != null && region.getRegionId() != null)
                .sorted(Comparator.comparing(FinCostCompareVo::getBrandId)
                        .thenComparing(FinCostCompareVo::getRegionId)).collect(Collectors.toList());
        //填充区域
        finList.addAll(costRegionListSorted);

        //以门店数据为基准汇总出一条all类型的数据
        FinCostCompareVo finTypeIsAll = getFinCostTypeIsAll(fin);
        //把这条汇总数据加进来
        finList.add(finTypeIsAll);
        return finList;
    }

    //重新赋值一个查询区域的实体
    private FinQueryDto getQueryRegionDto(FinQueryDto oldQueryDto) {
        FinQueryDto regionQueryDto = new FinQueryDto();
        regionQueryDto.setEnteId(oldQueryDto.getEnteId());
        regionQueryDto.setOnlyEnd(true);
        regionQueryDto.setEndTime(oldQueryDto.getEndTime());
        regionQueryDto.setSubjectCodeList(oldQueryDto.getSubjectCodeList());
        regionQueryDto.setMatchSubjectType(ReportDataConstant.Finance.MATCH_SUBJECT_IS);
        return regionQueryDto;
    }

    //查询出区域的 区域金额
    private Map<String, BigDecimal> getRegionAvgAmount(List<BaseShopVo> shopInfos, FinQueryDto queryDto, List<FormulaVo> formulaExpList) {
        //查询区域数据,根据门店数据, 查询出所包含的全部区域
        Map<String, List<BaseShopVo>> shopInfoRegionMap = shopInfos.stream().filter(shopInfo -> shopInfo.getRegionId() != null).collect(Collectors.groupingBy(BaseShopVo::getRegionId));
        //查询门店数据
        List<String> regionIds = new ArrayList<>();
        for (String regionKey : shopInfoRegionMap.keySet()) {
            regionIds.add(regionKey);
        }
        //根据所包含的regionId,再次查一次门店数据
        BaseShopDto baseShopDto = new BaseShopDto();
        baseShopDto.setEnteId(queryDto.getEnteId());
        baseShopDto.setRegionIdList(regionIds);
        List<BaseShopVo> shopInfosNew = baseShopService.findShopInfoForArea(baseShopDto);
        shopInfoRegionMap = shopInfosNew.stream().filter(shopInfo -> shopInfo.getRegionId() != null).collect(Collectors.groupingBy(BaseShopVo::getRegionId));
        Map<String, BigDecimal> shopAreaMap = new LinkedHashMap<>();
        shopInfoRegionMap.forEach((k, v) -> {
            BigDecimal shopArea = BigDecimal.ZERO;
            for (BaseShopVo baseShopVo : v) {
                shopArea = shopArea.add(baseShopVo.getShopArea() == null ? BigDecimal.ZERO : baseShopVo.getShopArea());
            }
            shopAreaMap.put(k, shopArea);
        });
        queryDto.setRegionIdList(regionIds);
        List<FinReportVo> regionSubjects = financeSubjectService.getSubjectData(queryDto);
        Map<String, List<FinReportVo>> regionCollect = regionSubjects.stream().filter(region -> region.getRegionId() != null).collect(Collectors.groupingBy(FinReportVo::getRegionId));
        Map<String, BigDecimal> regionAmountByFormula = financeSubjectService.getShopAmountByFormula(regionCollect, formulaExpList);

        //计算需要减去的金额
        Map<String, BigDecimal> regionReduceAmount = getReduceAmount(queryDto, ReportDataConstant.Finance.TYPE_REGION);
        //将区域的数据合并
        regionAmountByFormula.forEach((k, v) -> {
            BigDecimal amount = v;
            if (regionReduceAmount.containsKey(k)) {
                amount = v.subtract(regionReduceAmount.get(k));
            }
            BigDecimal avg = BigDecimalUtils.getDivide(amount, shopAreaMap.get(k));
            regionAmountByFormula.put(k, avg);
        });

        return regionAmountByFormula;
    }

    //得到需要减去的那部分金额 摘要内容包含增补的 key为门店id  value为金额
    private Map<String, BigDecimal> getReduceAmount(FinQueryDto queryDto, String dimensionType) {
        //查科目主公式配置,该公式是标准公式: subjectCode:direction:运算符
        List<FinReportVo> vouchers = getVouchersForCostDetail(queryDto);
        Map<String, List<FinReportVo>> collect = null;
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(dimensionType)) {
            collect = vouchers.stream().filter(voucher -> voucher.getShopId() != null).collect(Collectors.groupingBy(FinReportVo::getShopId));
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(dimensionType)) {
            collect = vouchers.stream().filter(voucher -> voucher.getRegionId() != null).collect(Collectors.groupingBy(FinReportVo::getRegionId));
        }
        Map<String, BigDecimal> amountReduceMap = new LinkedHashMap<>();

        collect.forEach((k, v) -> {
            BigDecimal amount = BigDecimal.ZERO;
            for (FinReportVo fin : v) {
                amount = amount.add(fin.getDebitAmount());
            }
            amountReduceMap.put(k, amount);
        });
        //记录
        logger.info(LogConstant.Finance.INVEST_REDUCE_AMOUNT + amountReduceMap);
        return amountReduceMap;
    }

    //获取需要去除的金额 注意 是截止时间 还是 时间段
    private List<FinReportVo> getVouchersForCostDetail(FinQueryDto queryDto) {
        FinReportConfigVo finConfigMain = financeSubjectService.getConfigByGroupAndType(queryDto.getEnteId(),
                ReportDataConstant.FinType.COMPARE_COST_GROUP,
                ReportDataConstant.FinType.COMPARE_COST_REDUCE);

        //重新设置科目
        String codes = finConfigMain.getCodes();
        List<String> subjectCodeList = new ArrayList<>();
        subjectCodeList.add(codes);
        queryDto.setSubjectCodeList(subjectCodeList);
        //个性化科目
        queryDto.setPersonal(ReportDataConstant.FinType.PERSONAL_GROUP);
        queryDto.setFlag(ReportDataConstant.FinType.ABSTRACT_CONTENT);
        //dayType 传入before,指截止时间之前的
        List<FinReportVo> vouchers = financeSubjectService.getSubjectData(queryDto);
        return vouchers;
    }


    //生成all的一条合计数据
    private FinCostCompareVo getFinCostTypeIsAll(FinCostCompareVo fin) {
        FinCostCompareVo finCostTypeIsAll = new FinCostCompareVo();
        finCostTypeIsAll.setType(ReportDataConstant.Finance.ALL_SUBJECT);
        finCostTypeIsAll.setBrandId(ReportDataConstant.Finance.EMPTY_STRING);
        finCostTypeIsAll.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        finCostTypeIsAll.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
        finCostTypeIsAll.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        finCostTypeIsAll.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
        finCostTypeIsAll.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        finCostTypeIsAll.setCost(fin.getCost());
        finCostTypeIsAll.setShopArea(fin.getShopArea());
        finCostTypeIsAll.setUnitCost(fin.getUnitCost());
        finCostTypeIsAll.setAvgRegionCost(fin.getAvgRegionCost());
        return finCostTypeIsAll;
    }

    //填充平均成本 和 差异率
    private FinCostCompareVo fillAvgAndDiff(List<FinCostCompareVo> shopCostList,
                                            Map<String, BigDecimal> regionAvgCostMap) {
        FinCostCompareVo fin = new FinCostCompareVo();
        //成本 和 店面积这两个值是全部shop维度的汇总值
        BigDecimal allCost = BigDecimal.ZERO;
        BigDecimal allShopArea = BigDecimal.ZERO;

        //把门店维度数据中的 区域平均成本 差异率 赋值
        for (FinCostCompareVo shop : shopCostList) {
            //如果包含区域面积
            if (regionAvgCostMap.containsKey(shop.getRegionId())) {
                //区域平均成本
                BigDecimal avgRegionCost = regionAvgCostMap.get(shop.getRegionId());
                shop.setAvgRegionCost(avgRegionCost);
                //差异率=（单位筹建成本-区域平均成本）/区域平均成本
                //等于0时 设置为null
                if (avgRegionCost.compareTo(BigDecimal.ZERO) == 0) {
                    shop.setDiffRate(null);
                } else {
                    BigDecimal unitCost = shop.getUnitCost() == null ? BigDecimal.ZERO : shop.getUnitCost();
                    BigDecimal diff = BigDecimalUtils.getPercent(unitCost.subtract(avgRegionCost), avgRegionCost);
                    shop.setDiffRate(diff);
                }
            }
            //累计成本
            allCost = allCost.add(shop.getCost() == null ? BigDecimal.ZERO : shop.getCost());
            //累计面积
            allShopArea = allShopArea.add(shop.getShopArea() == null ? BigDecimal.ZERO : shop.getShopArea());
        }
        fin.setCost(allCost);
        fin.setShopArea(allShopArea);
        //单位筹建成本
        BigDecimal unitCost = BigDecimalUtils.getDivide(allCost, allShopArea);
        fin.setUnitCost(unitCost);
        fin.setAvgRegionCost(unitCost);

        return fin;
    }

    /**
     * 生成shop维度的成本对比数据
     */
    private void fillCost(List<FinReportVo> listAll, List<FinCostCompareVo> costShopList) {
        for (FinReportVo reportVo : listAll) {
            FinCostCompareVo costVo = new FinCostCompareVo();
            costVo.setBrandId(reportVo.getBrandId());
            costVo.setBrandName(reportVo.getBrandName());
            costVo.setRegionId(reportVo.getRegionId());
            costVo.setRegionName(reportVo.getRegionName());
            costVo.setShopId(reportVo.getShopId());
            costVo.setShopName(reportVo.getShopName());
            costVo.setCost(reportVo.getAmount());
            costShopList.add(costVo);
        }
    }


    /**
     * 生成各维度成本数据
     * type: shop brand region
     */
    private List<FinCostCompareVo> getCostCompare(Map<String, List<FinCostCompareVo>> dimension,
                                                  String type,
                                                  Map<String, BigDecimal> shopAmountMap,
                                                  Map<String, BigDecimal> regionAvgCostMap,
                                                  Map<String, BigDecimal> shopAmountReduceMap) {
        List<FinCostCompareVo> costList = new LinkedList<>();
        //遍历Map中的数据
        dimension.forEach((k, v) -> {
            //第一步 将门店维度信息赋值给成本对象
            FinCostCompareVo costCompareVo = new FinCostCompareVo();
            costCompareVo.setType(type);
            costCompareVo.setBrandId(v.get(0).getBrandId());
            costCompareVo.setBrandName(v.get(0).getBrandName());
            //表头信息
            if (ReportDataConstant.Finance.TYPE_SHOP.equals(type)) {
                costCompareVo.setRegionId(v.get(0).getRegionId());
                costCompareVo.setRegionName(v.get(0).getRegionName());
                costCompareVo.setShopId(v.get(0).getShopId());
                costCompareVo.setShopName(v.get(0).getShopName());
            }
            //品牌维度,把区域和门店属性设置为全部
            else if (ReportDataConstant.Finance.TYPE_BRAND.equals(type)) {
                costCompareVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
                costCompareVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                costCompareVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                costCompareVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //区域维度,把门店属性设置为全部
            else if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                costCompareVo.setRegionId(v.get(0).getRegionId());
                costCompareVo.setRegionName(v.get(0).getRegionName());
                costCompareVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                costCompareVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //门店维度的筹建成本
            if (ReportDataConstant.Finance.TYPE_SHOP.equals(type)) {
                //定义成本金额
                BigDecimal cost = BigDecimal.ZERO;
                //从shopAmountMap中获取成本金额
                if (shopAmountMap.containsKey(k)) {
                    cost = shopAmountMap.get(k);
                }
                if (shopAmountReduceMap.containsKey(k)) {
                    //减去这个金额
                    cost = cost.subtract(shopAmountReduceMap.get(k));
                }
                //门店维度的成本
                costCompareVo.setCost(cost);
                //赋值区域平均成本和差异率
                String regionKey = costCompareVo.getRegionId();
                if (regionAvgCostMap.containsKey(regionKey)) {
                    //区域平均成本:直接从map中拿
                    BigDecimal avgCost = regionAvgCostMap.get(regionKey);
                    costCompareVo.setAvgRegionCost(avgCost);
                    // 差异率:(单位筹建成本-区域平均成本)/区域平均成本
                    BigDecimal diffRate = BigDecimalUtils.getDivide(cost.subtract(avgCost), avgCost);
                    costCompareVo.setDiffRate(diffRate);
                }
            }
            //不是门店维度 ,那么筹建成本,店面积是累加值
            //区域维度的:单位筹建成本和 区域平均成本相同 差异率为0
            //门店维度的:单位筹建成本和 区域平均成本相同,差异率为0
            else if (!ReportDataConstant.Finance.TYPE_SHOP.equals(type)) {
                //不是门店维度 ,那么筹建成本和店面积使用循环汇总的即可
                BigDecimal cost = BigDecimal.ZERO;
                BigDecimal shopArea = BigDecimal.ZERO; //此shopArea是门店的面积 和shop类型无关
                for (FinCostCompareVo finCost : v) {
                    cost = cost.add(finCost.getCost());
                    shopArea = shopArea.add(finCost.getShopArea() == null ? BigDecimal.ZERO : finCost.getShopArea());
                }
                //筹建成本
                costCompareVo.setCost(cost);
                //店面积
                costCompareVo.setShopArea(shopArea);
                //单位筹建成本=单位陈本/店面积
                BigDecimal unitCost = BigDecimalUtils.getDivide(cost, shopArea);
                costCompareVo.setUnitCost(unitCost);
                //区域平均成本=区域筹建成本/区域店面积
                //只有区域时才用到
                if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                    BigDecimal regionAvgCost = regionAvgCostMap.get(k) == null ? BigDecimal.ZERO : regionAvgCostMap.get(k);
                    costCompareVo.setAvgRegionCost(regionAvgCost);
                    //差异率:=(单位成本-区域平均成本)/区域平均成本
                    BigDecimal diffRate = BigDecimalUtils.getDivide(unitCost.subtract(regionAvgCost), regionAvgCost);
                    costCompareVo.setDiffRate(diffRate);
                }
            }
            costList.add(costCompareVo);
        });

        return costList;
    }


    /**
     * @description 筹建成本明细
     * @author fancl
     * @date 2020/1/14
     * @param
     * @return
     */
    @Override
    public List<FinSubjectVo> costDetail(FinQueryDto queryDto) {
        //成本明细的配置科目和筹建成本一样, 但它不需要汇总计算 ,而是分别展示
        //三个科目是精确匹配
        FinReportConfigVo finConfigMain = financeSubjectService.getConfigByGroupAndType(queryDto.getEnteId(),
                ReportDataConstant.FinType.COMPARE_COST_GROUP,
                ReportDataConstant.FinType.COMPARE_COST);
        //科目code需要手工处理
        String codes = finConfigMain.getCodes();
        String[] codeArr = codes.split("\\,");
        List<String> subjectCodeList = new ArrayList<>();
        for (String code : codeArr) {
            subjectCodeList.add(code);
        }
        queryDto.setSubjectCodeList(subjectCodeList);
        //因为只查截止日期 ,把end设置为true
        queryDto.setOnlyEnd(true);
        queryDto.setMatchSubjectType(ReportDataConstant.Finance.MATCH_SUBJECT_LEFT);
        List<FinReportVo> shopSubjects = financeSubjectService.getSubjectData(queryDto);
        //只留下金额不为null同时大于0的
        //和筹建成本对比的区别是 : 各个金额保留原始即可
        List<FinReportVo> listAll = shopSubjects.stream().filter(subject -> subject.getDebitAmount() != null && subject.getDebitAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
        //查询需要减去的科目数据
        List<FinReportVo> vouchers = getVouchersForCostDetail(queryDto);
        //减去vouchers中对应的金额
        mergeCost(listAll, vouchers);
        //按门店分组汇总出总金额
        Map<String, List<FinReportVo>> shopCollect = listAll.stream().collect(Collectors.groupingBy(FinReportVo::getShopId));
        //按shopId汇总金额作为总金额
        Map<String, BigDecimal> shopAmountMap = new LinkedHashMap<>();
        shopCollect.forEach((k, v) -> {
            BigDecimal amount = BigDecimal.ZERO;
            for (FinReportVo fin : v) {
                amount = amount.add(fin.getDebitAmount());
            }
            shopAmountMap.put(k, amount);
        });

        return fillDetail(listAll, shopAmountMap);

    }

    //合并两个list中的数据,根据shopId和accountSubjectId相同 来匹配
    void mergeCost(List<FinReportVo> targetList, List<FinReportVo> sourceList) {
        for (FinReportVo target : targetList) {
            for (FinReportVo source : sourceList) {
                if (source.getShopId().equals(target.getShopId()) &&
                        source.getAccountSubjectId().equals(source.getAccountSubjectId())) {
                    target.setDebitAmount(target.getDebitAmount().subtract(source.getDebitAmount()));
                    //跳出内层循环
                    break;
                }
            }
        }
    }

    //将报表类型转换为科目类型
    private List<FinSubjectVo> fillDetail(List<FinReportVo> reportList, Map<String, BigDecimal> shopAmountMap) {
        List<FinSubjectVo> subjectList = new ArrayList<>();
        //赋值新对象 并设置百分比
        reportList.stream().forEach(report -> {
            FinSubjectVo subjectVo = copyFin2Subject(report);
            //明细数据默认是shop类型
            subjectVo.setType(ReportDataConstant.Finance.TYPE_SHOP);
            //金额
            BigDecimal amount = report.getDebitAmount();
            subjectVo.setAmount(amount);
            if (shopAmountMap.containsKey(report.getShopId())) {
                //总金额
                BigDecimal allAmount = shopAmountMap.get(report.getShopId());
                subjectVo.setAllAmount(allAmount);
                //计算总比例
                BigDecimal percent = BigDecimalUtils.getPercent(amount, allAmount);
                subjectVo.setPercent(percent);
            }
            subjectList.add(subjectVo);
        });
        //过滤无效数据并排序
        List<FinSubjectVo> finList = subjectList.stream()
                .filter(report -> report.getBrandId() != null && report.getRegionId() != null && report.getShopId() != null)
                .sorted(Comparator.comparing(FinSubjectVo::getBrandId)
                        .thenComparing(FinSubjectVo::getRegionId)
                        .thenComparing(FinSubjectVo::getShopId)
                        .thenComparing(FinSubjectVo::getAccountSubjectName)).collect(Collectors.toList());

        return finList;
    }

    //赋值基本属性
    private FinSubjectVo copyFin2Subject(FinReportVo finReportVo) {
        FinSubjectVo subjectVo = new FinSubjectVo();
        subjectVo.setBrandId(finReportVo.getBrandId());
        subjectVo.setBrandName(finReportVo.getBrandName());
        subjectVo.setRegionId(finReportVo.getRegionId());
        subjectVo.setRegionName(finReportVo.getRegionName());
        subjectVo.setShopId(finReportVo.getShopId());
        subjectVo.setShopName(finReportVo.getShopName());
        subjectVo.setAccountSubjectName(finReportVo.getAccountSubjectName());
        return subjectVo;
    }

    //筹建投资回报
    @Override
    public List<FinPrepaInvestVo> prepaInvest(FinQueryDto queryDto) {

        //累计利润 公式配置对象
        FinReportConfigVo finConfig = financeSubjectService.getFinConfigAndFormula(queryDto.getEnteId(), ReportDataConstant.FinType.PREPARATION_INVEST_GROUP, ReportDataConstant.FinType.PREPARATION_INVEST_PROFIT);
        //构造查询条件
        queryDto.setSubjectCodeList(finConfig.getSubjectCodeList());
        //设置仅包含截止日期为true,这样查询语句的开始时间将被忽略
        queryDto.setOnlyEnd(true);
        //使用左匹配,凭证里面的科目可能是所配置科目的子科目
        queryDto.setMatchSubjectType(ReportDataConstant.Finance.MATCH_SUBJECT_LEFT);
        //累计利润的科目发生额
        List<FinReportVo> reportList = financeSubjectService.getSubjectData(queryDto);
        //按门店分组
        Map<String, List<FinReportVo>> shopMap = reportList.stream().filter(shop -> shop.getShopId() != null).collect(Collectors.groupingBy(FinReportVo::getShopId));
        //查询门店数据
        BaseShopDto baseShopDto = new BaseShopDto();
        baseShopDto.setEnteId(queryDto.getEnteId());
        baseShopDto.setShopIdList(queryDto.getShopIdList());
        baseShopDto.setShopTypeIdList(queryDto.getShopTypeIdList());
        List<BaseShopVo> shopInfos = baseShopService.findShopInfoForArea(baseShopDto);

        //计算shoId维度的 累计例如金额
        Map<String, BigDecimal> shopProfitAmountMap = financeSubjectService.getShopAmountByFormula(shopMap, finConfig.getFormulaExpList());
        //门店维度填充表头,并匹配累计利润
        List<FinPrepaInvestVo> shopProfitList = getPrepaData(shopMap, ReportDataConstant.Finance.TYPE_SHOP, shopProfitAmountMap);
        //筹建投入金额
        Map<String, BigDecimal> investAmountMap = getInvestAmount(queryDto);
        //需要减去的那部分
        Map<String, BigDecimal> reduceAmountMap = getReduceAmount(queryDto, ReportDataConstant.Finance.TYPE_SHOP);
        //门店基本信息和门店list合并 计算门店营业期
        MergeUtil.merge(shopProfitList, shopInfos,
                FinPrepaInvestVo::getShopId, BaseShopVo::getShopId,
                (profitVo, shopVo) -> {
                    //营业日期
                    BigDecimal busiYear = getBusiYear(shopVo, queryDto.getEndTime());
                    //如果是0年, 那么按一年算
                    if(busiYear.compareTo(BigDecimal.ZERO)==0){
                        busiYear = new BigDecimal("1");
                    }
                    profitVo.setBusinessPeriod(busiYear);
                    BigDecimal accumuProfit = profitVo.getAccumulatedProfit();
                    //2018-01-01之前的例如需加上门店设置的值
                    String configStartYear = ReportDataConstant.Finance.BEGIN_YEAR_DAY;
                    Date startYear = DateUtils.stringConvertDate(configStartYear);
                    Date openingDate = shopVo.getOpeningDate();
                    BigDecimal balanceStart = shopVo.getAddProfit();
                    if (openingDate != null && balanceStart != null && openingDate.before(startYear)) {
                        //加上18年这个值
                        accumuProfit = accumuProfit.add(balanceStart);
                        //累计利润要加上去
                        profitVo.setAccumulatedProfit(accumuProfit);
                    }


                    //计算年均利润
                    BigDecimal avgProfit = accumuProfit
                            .divide(new BigDecimal(String.valueOf(busiYear)), 2, BigDecimal.ROUND_HALF_UP);
                    profitVo.setAvgYearProfit(avgProfit);


                    //筹建投入
                    BigDecimal investAmount = investAmountMap.get(profitVo.getShopId());
                    if (investAmountMap.containsKey(profitVo.getShopId())) {
                        //减去摘要的
                        if (reduceAmountMap.containsKey(profitVo.getShopId())) {
                            investAmount = investAmount.subtract(reduceAmountMap.get(profitVo.getShopId()));
                        }
                        profitVo.setInvestPreparation(investAmount);
                    }
                    //投资回报率=年均利润/筹建投入
                    //只有当筹建投入!=null 同时不为0时才计算投资回报率
                    if (avgProfit != null && investAmount != null) {
                        BigDecimal retRate = BigDecimalUtils.getPercent(avgProfit, investAmount);
                        profitVo.setRetRate(retRate);
                    }
                });

        //按品牌分组
        Map<String, List<FinPrepaInvestVo>> brandMap = shopProfitList.stream().filter(brand -> brand.getBrandId() != null).collect(Collectors.groupingBy(FinPrepaInvestVo::getBrandId));
        //按区域分组
        Map<String, List<FinPrepaInvestVo>> regionMap = shopProfitList.stream().filter(region -> region.getRegionId() != null).collect(Collectors.groupingBy(FinPrepaInvestVo::getRegionId));
        //品牌数据
        List<FinPrepaInvestVo> brandInvestList = getListByDimension(brandMap, ReportDataConstant.Finance.TYPE_BRAND);
        //区域数据
        List<FinPrepaInvestVo> regionInvestList = getListByDimension(regionMap, ReportDataConstant.Finance.TYPE_REGION);
        //处理那条汇总的数据 先注释 ,前端估计可以做
        List<FinPrepaInvestVo> allList = new LinkedList<>();
        //将三个List放入返回对象
        allList.addAll(shopProfitList);
        allList.addAll(brandInvestList);
        allList.addAll(regionInvestList);
        //把all的那条放进来
        FinPrepaInvestVo finAll = getAllByDimension(shopProfitList);
        allList.add(finAll);

        return allList;
    }


    //计算营业时间
    private BigDecimal getBusiYear(BaseShopVo shopVo, String endTime) {
        BigDecimal busiYear = null;
        if (shopVo.getOpeningDate() == null)
            return new BigDecimal("1");
        //计算两个时间差 ,结果换算为年
        LocalDate today = LocalDate.now();
        Instant openInstant = shopVo.getOpeningDate().toInstant();
        //关停日期不为空的,取关停时间到开业时间的日期差
        Date shutdownDate = shopVo.getShutdownDate();
        ZoneId zoneId = ZoneId.systemDefault();
        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        LocalDate openDate = openInstant.atZone(zoneId).toLocalDate();
        LocalDate closeDate = null;
        boolean haveShutdownDay = false;
        //将endTime字符串类型转为日期类型
        Date endDay = DateUtils.parseDate(endTime, DateUtils.PATTERN_DAY);
        LocalDate endLocalDate = endDay.toInstant().atZone(zoneId).toLocalDate();
        if (shutdownDate != null) {
            closeDate = shutdownDate.toInstant().atZone(zoneId).toLocalDate();
            haveShutdownDay = true;
        }

        //从开业到查询结束日期的天数
        long days = 0;
        //如果关店了
        if (haveShutdownDay) {
            days = ChronoUnit.DAYS.between(closeDate, today);
        } else {
            //没关店,从查询结束日期到开业日期
            days = ChronoUnit.DAYS.between(openDate, endLocalDate);
        }
        //将天数转为年
        busiYear = new BigDecimal(days).divide(ReportDataConstant.Finance.DAYS_365, 2, BigDecimal.ROUND_HALF_UP);
        return busiYear;
    }

    //获取筹建成本金额
    private Map<String, BigDecimal> getInvestAmount(FinQueryDto queryDto) {
        //筹建成本使用 筹建成本的公共配置
        FinReportConfigVo finConfigMain = financeSubjectService.getFinConfigAndFormula(queryDto.getEnteId(),
                ReportDataConstant.FinType.COMPARE_COST_GROUP,
                ReportDataConstant.FinType.COMPARE_COST);
        queryDto.setSubjectCodeList(finConfigMain.getSubjectCodeList());
        //科目方式 :精确匹配
        queryDto.setMatchSubjectType(ReportDataConstant.Finance.MATCH_SUBJECT_IS);
        //因为只查截止日期 ,把end设置为true
        queryDto.setOnlyEnd(true);
        List<FinReportVo> investList = financeSubjectService.getSubjectData(queryDto);
        //按门店分组
        Map<String, List<FinReportVo>> shopCollect = investList.stream().filter(shop -> shop.getShopId() != null).collect(Collectors.groupingBy(FinReportVo::getShopId));
        //计算主公式金额合计 key为门店id value为金额
        Map<String, BigDecimal> shopAmountByFormula = financeSubjectService.getShopAmountByFormula(shopCollect, finConfigMain.getFormulaExpList());
        //记录筹建投入金额:第一部分金额(未减去摘要部分)
        logger.info(LogConstant.Finance.INVEST_AMOUNT + shopAmountByFormula);
        return shopAmountByFormula;

        //第二步 计算科目余额
//        String investType = finConfigMain.getCodesType();
//        String[] typeArr = investType.split("\\,");
//        List<String> colTypes = new ArrayList<>();
//        for (String colType : typeArr) {
//            colTypes.add(colType);
//        }
//        queryDto.setSubjectCodeList(colTypes);
//        //根据截止年月得到所在会计期间
//        String endTime = queryDto.getEndTime();
//        endTime = endTime.substring(0, 7).replaceAll("-", "");
//        Integer yearNum = Integer.valueOf(endTime);
//        queryDto.setYearNum(yearNum);
//        //需要从科目余额表中查询的科目
//        List<FinBalanceSubjectVo> balanceSubjectList = finBalanceSubjectMapper.findBalanceBySubjectCodes(queryDto);
//        Map<String, List<FinBalanceSubjectVo>> shopBalanceMap = balanceSubjectList.stream().filter(balance -> balance.getShopId() != null).collect(Collectors.groupingBy(FinBalanceSubjectVo::getShopId));
//        Map<String, BigDecimal> balanceMap = new LinkedHashMap<>();
//        shopBalanceMap.forEach((k, v) -> {
//            BigDecimal balance = BigDecimal.ZERO;
//            for (int i = 0; i < v.size(); i++) {
//                balance = balance.add(v.get(i).getCloseBalance() == null ? BigDecimal.ZERO : v.get(i).getCloseBalance());
//            }
//            balanceMap.put(k, balance);
//        });
//
//        //将发生额和科目余额的金额累加
//        investMap.forEach((k, v) -> {
//            if (balanceMap.containsKey(k)) {
//                balanceMap.put(k, balanceMap.get(k).add(v));
//            } else {
//                balanceMap.put(k, v);
//            }
//        });

        //return balanceMap;
    }


    //按不同维度处理
    private List<FinPrepaInvestVo> getPrepaData(Map<String, List<FinReportVo>> dimension, String type, Map<String, BigDecimal> shopProfitAmountMap) {
        List<FinPrepaInvestVo> investList = new LinkedList<>();
        //遍历Map中的数据
        dimension.forEach((k, v) -> {
            //第一步 将门店维度信息赋值给成本对象
            FinPrepaInvestVo investVo = new FinPrepaInvestVo();
            investVo.setType(type);
            investVo.setBrandId(v.get(0).getBrandId());
            investVo.setBrandName(v.get(0).getBrandName());
            //表头信息
            investVo.setRegionId(v.get(0).getRegionId());
            investVo.setRegionName(v.get(0).getRegionName());
            investVo.setShopId(v.get(0).getShopId());
            investVo.setShopName(v.get(0).getShopName());
            BigDecimal profit = BigDecimal.ZERO;
            //根据计算后的金额map匹配
            if (shopProfitAmountMap.containsKey(k)) {
                profit = shopProfitAmountMap.get(k);
            }
            investVo.setAccumulatedProfit(profit);
            investList.add(investVo);
        });
        return investList;
    }

    //处理根据维度汇总
    private List<FinPrepaInvestVo> getListByDimension(Map<String, List<FinPrepaInvestVo>> mapData, String type) {
        List<FinPrepaInvestVo> investList = new LinkedList<>();
        mapData.forEach((k, v) -> {
            FinPrepaInvestVo investVo = new FinPrepaInvestVo();
            investVo.setType(type);
            investVo.setBrandId(v.get(0).getBrandId());
            investVo.setBrandName(v.get(0).getBrandName());
            //表头信息
            //品牌维度,把区域和门店属性设置为全部
            if (ReportDataConstant.Finance.TYPE_BRAND.equals(type)) {
                investVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
                investVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                investVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                investVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //区域维度,把门店属性设置为全部
            else if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                investVo.setRegionId(v.get(0).getRegionId());
                investVo.setRegionName(v.get(0).getRegionName());
                investVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                investVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //得到各个列数值
            FinPrepaInvestVo finPrepaInvestVo = this.calcEveryAmount(v);
            //investVo.setBusinessPeriod(finPrepaInvestVo.getBusinessPeriod());
            investVo.setAccumulatedProfit(finPrepaInvestVo.getAccumulatedProfit());
            investVo.setInvestPreparation(finPrepaInvestVo.getInvestPreparation());
            //investVo.setAvgYearProfit(finPrepaInvestVo.getAvgYearProfit());
            //investVo.setRetRate(finPrepaInvestVo.getRetRate());
            investList.add(investVo);
        });

        return investList;
    }


    private FinPrepaInvestVo calcEveryAmount(List<FinPrepaInvestVo> investList) {
        FinPrepaInvestVo fin = new FinPrepaInvestVo();
        //营业期
        BigDecimal businessPeriod = BigDecimal.ZERO;
        //累计利润
        BigDecimal accumulatedProfit = BigDecimal.ZERO;
        //年均利润
        BigDecimal avgYearProfit = null;
        //筹建投入
        BigDecimal investPreparation = BigDecimal.ZERO;
        ;
        //投资回报率
        BigDecimal retRate = BigDecimal.ZERO;
        for (FinPrepaInvestVo vo : investList) {
            //三个值分别累加
            businessPeriod = businessPeriod.add(ifNull2Zero(vo.getBusinessPeriod()));
            accumulatedProfit = accumulatedProfit.add(ifNull2Zero(vo.getAccumulatedProfit()));
            investPreparation = investPreparation.add(ifNull2Zero(vo.getInvestPreparation()));
        }
        //年均利润=累计利润/营业期
        avgYearProfit = getDivide(accumulatedProfit, businessPeriod);
        //投资回报率 = 年均利润/筹建投入
        retRate = getDivide(avgYearProfit, investPreparation);
        fin.setBusinessPeriod(businessPeriod);
        fin.setAccumulatedProfit(accumulatedProfit);
        fin.setAvgYearProfit(avgYearProfit);
        fin.setInvestPreparation(investPreparation);
        fin.setRetRate(retRate);
        return fin;
    }

    //生成全部的那条汇总数据1
    private FinPrepaInvestVo getAllByDimension(List<FinPrepaInvestVo> investList) {
        FinPrepaInvestVo fin = new FinPrepaInvestVo();
        fin.setType(ReportDataConstant.Finance.TYPE_ALL);
        FinPrepaInvestVo finPrepaInvestVo = this.calcEveryAmount(investList);
        fin.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        fin.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        fin.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        //fin.setBusinessPeriod(finPrepaInvestVo.getBusinessPeriod());
        fin.setAccumulatedProfit(finPrepaInvestVo.getAccumulatedProfit());
        //fin.setAvgYearProfit(finPrepaInvestVo.getAvgYearProfit());
        fin.setInvestPreparation(finPrepaInvestVo.getInvestPreparation());
        //fin.setRetRate(finPrepaInvestVo.getRetRate());
        return fin;
    }

    //非0处理
    private BigDecimal ifNull2Zero(BigDecimal big) {
        if (big == null) return BigDecimal.ZERO;
        return big;
    }

    //两个数的除法
    private BigDecimal getDivide(BigDecimal small, BigDecimal big) {
        if (big == null || big.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return small.divide(big, 2, RoundingMode.HALF_UP);
    }


    //工程明细导出Excel
    @Override
    public void exportProjectDetail(ExcelExportDto excelExportDto, HttpServletResponse response) {
        //把字符串日期赋值给日期字段
        excelExportDto.setBeginDate(DateUtils.parseDate(excelExportDto.getBeginTime(), DateUtils.PATTERN_DAY));
        excelExportDto.setEndDate(DateUtils.parseDate(excelExportDto.getEndTime(), DateUtils.PATTERN_DAY));
        FinQueryDto finQueryDto = convert2FinQuery(excelExportDto);
        List<FinSubjectVo> subjectVoList = getProjectDetail(finQueryDto);
        List<FinSubjectVo> projectDetailList;
        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(finQueryDto.getType())) {
            projectDetailList = subjectVoList.stream().filter(info -> (info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)
                    || info.getType().equals(ReportDataConstant.Finance.TYPE_ALL))
            ).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(finQueryDto.getType())) {
            projectDetailList = subjectVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(finQueryDto.getType())) {
            projectDetailList = subjectVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        } else {
            projectDetailList = subjectVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }
        fileService.exportPorjectDetail(response, excelExportDto, projectDetailList);

    }

    //筹建成本对比 导出Excel
    @Override
    public void exportCompareCost(ExcelExportDto excelExportDto, HttpServletResponse response) {
        //把字符串日期赋值给日期字段
        excelExportDto.setBeginDate(DateUtils.parseDate(excelExportDto.getBeginTime(), DateUtils.PATTERN_DAY));
        excelExportDto.setEndDate(DateUtils.parseDate(excelExportDto.getEndTime(), DateUtils.PATTERN_DAY));
        FinQueryDto finQueryDto = convert2FinQuery(excelExportDto);
        List<FinCostCompareVo> costCompareList = compareCost(finQueryDto);
        List<FinCostCompareVo> costList;
        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(finQueryDto.getType())) {
            costList = costCompareList.stream().filter(
                    info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)
                            || info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(finQueryDto.getType())) {
            costList = costCompareList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(finQueryDto.getType())) {
            costList = costCompareList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        } else {
            costList = costCompareList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }
        fileService.exportExcelForQueryTerm(response, excelExportDto, costList,
                ExcelColumnConstant.CostCompareInfo.BRAND_NAME,
                ExcelColumnConstant.CostCompareInfo.REGION_NAME,
                ExcelColumnConstant.CostCompareInfo.SHOP_NAME,
                ExcelColumnConstant.CostCompareInfo.SHOP_AREA,
                ExcelColumnConstant.CostCompareInfo.COST,
                ExcelColumnConstant.CostCompareInfo.UNIT_COST,
                ExcelColumnConstant.CostCompareInfo.AVG_REGION_COST,
                ExcelColumnConstant.CostCompareInfo.DIFF_RATE);

    }

    //筹建成本明细 导出Excel
    @Override
    public void exportCostDetail(ExcelExportDto excelExportDto, HttpServletResponse response) {
        //把字符串日期赋值给日期字段
        excelExportDto.setBeginDate(DateUtils.parseDate(excelExportDto.getBeginTime(), DateUtils.PATTERN_DAY));
        excelExportDto.setEndDate(DateUtils.parseDate(excelExportDto.getEndTime(), DateUtils.PATTERN_DAY));
        FinQueryDto finQueryDto = convert2FinQuery(excelExportDto);
        List<FinSubjectVo> costDetailList = costDetail(finQueryDto);
        List<FinSubjectVo> detailList;
        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(finQueryDto.getType())) {
            detailList = costDetailList.stream().filter(
                    info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)
                            || info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)
            ).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(finQueryDto.getType())) {
            detailList = costDetailList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(finQueryDto.getType())) {
            detailList = costDetailList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        } else {
            detailList = costDetailList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }
        fileService.exportExcelForQueryTerm(response, excelExportDto, detailList,
                ExcelColumnConstant.CostDetailInfo.BRAND_NAME,
                ExcelColumnConstant.CostDetailInfo.REGION_NAME,
                ExcelColumnConstant.CostDetailInfo.SHOP_NAME,
                ExcelColumnConstant.CostDetailInfo.ACCOUNT_SUBJECT_NAME,
                ExcelColumnConstant.CostDetailInfo.AMOUNT,
                ExcelColumnConstant.CostDetailInfo.ALL_AMOUNT,
                ExcelColumnConstant.CostDetailInfo.PERCENT);
    }

    //筹建投资回报导出Excel
    @Override
    public void exportInvest(ExcelExportDto excelExportDto, HttpServletResponse response) {
        //把字符串日期赋值给日期字段
        excelExportDto.setBeginDate(DateUtils.parseDate(excelExportDto.getBeginTime(), DateUtils.PATTERN_DAY));
        excelExportDto.setEndDate(DateUtils.parseDate(excelExportDto.getEndTime(), DateUtils.PATTERN_DAY));
        FinQueryDto finQueryDto = convert2FinQuery(excelExportDto);
        List<FinPrepaInvestVo> subjectVoList = prepaInvest(finQueryDto);
        List<FinPrepaInvestVo> investList;
        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(finQueryDto.getType())) {
            investList = subjectVoList.stream().filter(
                    info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)
            ).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(finQueryDto.getType())) {
            investList = subjectVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(finQueryDto.getType())) {
            investList = subjectVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        } else {
            investList = subjectVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }
        fileService.exportExcelForQueryTerm(response, excelExportDto, investList,
                ExcelColumnConstant.PrepaInvestInfo.BRAND_NAME,
                ExcelColumnConstant.PrepaInvestInfo.REGION_NAME,
                ExcelColumnConstant.PrepaInvestInfo.SHOP_NAME,
                ExcelColumnConstant.PrepaInvestInfo.BUSINESS_PERIOD,
                ExcelColumnConstant.PrepaInvestInfo.ACCUMULATED_PROFIT,
                ExcelColumnConstant.PrepaInvestInfo.AVG_YEAR_PROFIT,
                ExcelColumnConstant.PrepaInvestInfo.INVEST_PREPARATION,
                ExcelColumnConstant.PrepaInvestInfo.RET_RATE);
    }

    //将ExcelExportDto 转换为FinQueryDto
    private FinQueryDto convert2FinQuery(ExcelExportDto excelExportDto) {
        FinQueryDto finQueryDto = new FinQueryDto();
        finQueryDto.setEnteId(excelExportDto.getEnteId());
        finQueryDto.setBeginTime(excelExportDto.getBeginTime());
        finQueryDto.setEndTime(excelExportDto.getEndTime());
        finQueryDto.setShopIdList(excelExportDto.getShopIdList());
        finQueryDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        finQueryDto.setType(excelExportDto.getType());
        return finQueryDto;
    }
}

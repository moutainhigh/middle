package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.CostAggregation;
import com.njwd.entity.reportdata.FinReportConfig;
import com.njwd.entity.reportdata.dto.BalanceDto;
import com.njwd.entity.reportdata.dto.FinCashFlowReportTableDto;
import com.njwd.entity.reportdata.dto.FinProfitReportTableDto;
import com.njwd.entity.reportdata.dto.FinReportTableDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;
import com.njwd.entity.reportdata.vo.fin.FinReportVo;
import com.njwd.report.mapper.FinReportTableMapper;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.reportdata.service.BaseShopService;
import com.njwd.reportdata.service.FinancialReportService;
import com.njwd.service.FileService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 财务报表service
 * @Author LuoY
 * @Date 2019/11/20
 */
@Service
public class FinancialReportServiceImpl implements FinancialReportService {
    private Logger logger = LoggerFactory.getLogger(FinancialReportServiceImpl.class);

    @Resource
    private FinReportTableMapper finReportTableMapper;

    @Autowired
    private FileService fileService;

    @Resource
    private FinanceSubjectService financeSubjectService;

    @Resource
    private BaseShopService baseShopService;

    /**
     * 资产负债表 assets debt
     *
     * @param balanceDto
     * @return com.njwd.entity.reportdata.vo.FinReportTableVo
     * @Author lj
     * @Date:17:40 2020/1/13
     **/
    @Override
    public FinReportTableVo findBalanceReport(BalanceDto balanceDto) {
        FinReportTableDto finReportTableDto = new FinReportTableDto();
        finReportTableDto.setShopIdList(balanceDto.getShopIdList());
        finReportTableDto.setEnteId(balanceDto.getEnteId());

        //处理期间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        Integer time = Integer.valueOf(simpleDateFormat.format(balanceDto.getBeginDate()));
        finReportTableDto.setPeriodYearNum(time);
        finReportTableDto.setReportId(ReportDataConstant.ReportItemReportId.BALANCE_REPORT);
        List<FinReportTableVo> finReportTableList = finReportTableMapper.findFinReportTableList(finReportTableDto);
        //封装成资产数据和负债数据
        List<FinReportTableVo> assetsList = new ArrayList<>();
        List<FinReportTableVo> debtList = new ArrayList<>();
        FinReportTableVo finReportTableVo = new FinReportTableVo();
        finReportTableVo.setAssetsList(assetsList);
        finReportTableVo.setDebtList(debtList);
        int i = 1;
        for (FinReportTableVo v : finReportTableList) {
            if (ReportDataConstant.ItemType.ASSETS.equals(v.getItemType())) {
                FinReportTableVo f = new FinReportTableVo();
                f.setAssetsCloseBalance(v.getCloseBalance());
                f.setAssetsYearBalance(v.getYearBalance());
                f.setAssetsItemName(v.getItemName());
                if (!ReportDataConstant.ItemLevel.TITLE.equals(v.getItemLevel())
                        && !ReportDataConstant.ItemLevel.SUBTOTAL.equals(v.getItemLevel()) && !ReportDataConstant.ItemLevel.TOTAL.equals(v.getItemLevel())) {
                    f.setAssetsLineNumber(i);
                    i++;
                }
                f.setAssetsItemNumber(v.getItemNumber());
                f.setAssetsItemType(v.getItemType());
                f.setAssetsItemLevel(v.getItemLevel());
                assetsList.add(f);
            }
            if (ReportDataConstant.ItemType.DEBT.equals(v.getItemType())) {
                FinReportTableVo f = new FinReportTableVo();
                f.setDebtCloseBalance(v.getCloseBalance());
                f.setDebtYearBalance(v.getYearBalance());
                f.setDebtItemName(v.getItemName());
                if (!ReportDataConstant.ItemLevel.TITLE.equals(v.getItemLevel())
                        && !ReportDataConstant.ItemLevel.SUBTOTAL.equals(v.getItemLevel()) && !ReportDataConstant.ItemLevel.TOTAL.equals(v.getItemLevel())) {
                    f.setDebtLineNumber(i);
                    i++;
                }
                f.setDebtItemNumber(v.getItemNumber());
                f.setDebtItemType(v.getItemType());
                f.setDebtItemLevel(v.getItemLevel());
                debtList.add(f);
            }
        }

        return finReportTableVo;
    }

    /**
     * @Description: 利润表
     * @Param: [balanceDto]
     * @return: com.njwd.entity.reportdata.FinProfitReportTable
     * @Author: LuoY
     * @Date: 2020/2/10 9:57
     */
    @Override
    public List<FinProfitReportTableVo> findProfitReport(BalanceDto balanceDto) {
        FinProfitReportTableDto finProfitReportTableDto = new FinProfitReportTableDto();
        finProfitReportTableDto.setShopIdList(balanceDto.getShopIdList());
        finProfitReportTableDto.setShopTypeIdList(balanceDto.getShopTypeIdList());
        finProfitReportTableDto.setReportId(ReportDataConstant.ReportItemReportId.PROFIT_REPORT);
        finProfitReportTableDto.setEnteId(balanceDto.getEnteId());
        //处理期间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        Integer time = Integer.valueOf(simpleDateFormat.format(balanceDto.getBeginDate()));
        finProfitReportTableDto.setPeriodYearNum(time);
        List<FinProfitReportTableVo> finProfitReportTableVos = finReportTableMapper.findProfitReportTableVo(finProfitReportTableDto);
        return finProfitReportTableVos;
    }

    /**
     * 现金流量表
     *
     * @param balanceDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinReportTableVo>
     * @Author lj
     * @Date:17:40 2020/1/13
     **/
    @Override
    public List<FinCashFlowReportTableVo> findCashFlowReport(BalanceDto balanceDto) {
        FinCashFlowReportTableDto finCashFlowReportTableDto = new FinCashFlowReportTableDto();
        finCashFlowReportTableDto.setShopIdList(balanceDto.getShopIdList());
        finCashFlowReportTableDto.setShopTypeIdList(balanceDto.getShopTypeIdList());
        finCashFlowReportTableDto.setReportId(ReportDataConstant.ReportItemReportId.CASHFLOW_REPORT);
        finCashFlowReportTableDto.setEnteId(balanceDto.getEnteId());
        //处理期间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        Integer time = Integer.valueOf(simpleDateFormat.format(balanceDto.getBeginDate()));
        finCashFlowReportTableDto.setPeriodYearNum(time);
        List<FinCashFlowReportTableVo> finCashFlowReportTableVos = finReportTableMapper.findCashFlowReportTableVo(finCashFlowReportTableDto);
        int i = 1;
        for (FinCashFlowReportTableVo finCashFlowReportTableVo : finCashFlowReportTableVos) {
            finCashFlowReportTableVo.setItemLine(String.valueOf(i));
            i++;
        }
        return finCashFlowReportTableVos;
    }

    /**
     * Description: 查询费用汇总表
     *
     * @author: LuoY
     * @date: 2020/2/26 0026 10:41
     * @param:[balanceDto]
     * @return:java.util.List<com.njwd.entity.reportdata.CostAggregation>
     */
    @Override
    public List<CostAggregationVo> findCostAggregationReport(BalanceDto balanceDto) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.PATTERN_DAY);
        List<CostAggregationVo> costAggregationVos = new LinkedList<>();
        //查询项目公式配置
        Map<String, List<FinReportConfigVo>> reportConfigMap = financeSubjectService.getConfigByGroup(balanceDto.getEnteId(),
                ReportDataConstant.CostAggregation.COST_GROUP).stream().collect(Collectors.groupingBy(FinReportConfig::getFinType));
        //查询指定期间得凭证发生数
        List<String> subjectCodeListAll = new LinkedList<>();
        reportConfigMap.forEach((shop, data) -> {
            data.stream().forEach(data1 -> {
                if (!FastUtils.checkNullOrEmpty(data1.getSubjectCodeList())) {
                    subjectCodeListAll.addAll(data1.getSubjectCodeList());
                }
            });
        });
        FinQueryDto queryDto = new FinQueryDto();
        queryDto.setEnteId(balanceDto.getEnteId());
        queryDto.setBeginTime(dateFormat.format(balanceDto.getBeginDate()));
        queryDto.setEndTime(dateFormat.format(balanceDto.getEndDate()));
        queryDto.setShopIdList(balanceDto.getShopIdList());
        queryDto.setShopTypeIdList(balanceDto.getShopTypeIdList());
        queryDto.setMatchSubjectType(ReportDataConstant.Finance.MATCH_SUBJECT_IS);
        queryDto.setSubjectCodeList(subjectCodeListAll);
        List<FinReportVo> finReportVos = financeSubjectService.getSubjectData(queryDto);
        //初始化报表组织架构信息
        BaseShopDto baseShopDto = new BaseShopDto();
        baseShopDto.setEnteId(balanceDto.getEnteId());
        baseShopDto.setShopIdList(balanceDto.getShopIdList());
        baseShopDto.setShopTypeIdList(balanceDto.getShopTypeIdList());
        List<BaseShopVo> baseShops = baseShopService.findShopInfo(baseShopDto);
        baseShops.forEach(shop -> {
            CostAggregationVo costAggregationVo = new CostAggregationVo();
            costAggregationVo.setType(ReportDataConstant.Finance.TYPE_SHOP);
            costAggregationVo.setBrandName(shop.getBrandName());
            costAggregationVo.setBrandId(shop.getBrandId());
            costAggregationVo.setRegionName(shop.getRegionName());
            costAggregationVo.setRegionId(shop.getRegionId());
            costAggregationVo.setShopName(shop.getShopName());
            costAggregationVo.setShopId(shop.getShopId());
            costAggregationVos.add(costAggregationVo);
        });
        //将凭证数据按门店分组
        Map<String, List<FinReportVo>> finReportMap = finReportVos.stream().collect(Collectors.groupingBy(FinReportVo::getShopId));
        //处理结果集变为Map<门店id，Map<项目code,金额>>
        Long startTime = System.currentTimeMillis();
        Map<String, Map<String, BigDecimal>> resultMap = new HashMap<>();
        Map<String, Map<String, BigDecimal>> resultMap1 = new HashMap<>();
        //先计算需要参与计算的指标
        reportConfigMap.forEach((code, config) -> {
            Map<String, BigDecimal> mapData = new HashMap<>();
            for (FinReportConfigVo data : config) {
                //如果当前code需要根据公式计算，计算出所有门店当前code的值
                if (StringUtil.isNotEmpty(data.getCodesType())) {
                    mapData.putAll(financeSubjectService.getShopAmountByFormula(finReportMap,
                            data.getFormulaExpList()));

                }
            }
            if(mapData.size()>Constant.Number.ZERO){
                resultMap1.put(code,mapData);
            }
        });
        //循环门店凭证数据
        for (Map.Entry<String, List<FinReportVo>> entry : finReportMap.entrySet()) {
            //循环配置公式
            Map<String, BigDecimal> map = new HashMap<>();
            reportConfigMap.forEach((code, config) -> {
                //计算的数据直接从mapData里面取
                if (StringUtil.isNotEmpty(config.stream().findFirst().get().getCodesType())) {
                    map.put(code, resultMap1.get(code).get(entry.getKey()));
                } else {
                    //不需要计算的数据直接从凭证数据里面获取当前会计科目code的值
                    BigDecimal result = entry.getValue().stream().filter(data -> data.getAccountSubjectCode().startsWith(config.stream().
                            findFirst().get().getCodes())).map(FinReportVo::getDebitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    map.put(code, result);
                }
            });
            resultMap.put(entry.getKey(), map);
        }
        logger.info("计算时间：2" + String.valueOf(System.currentTimeMillis() - startTime));
        //给门店赋值
        costItemValue(costAggregationVos, resultMap);
        //计算汇总数据
        dataHandle(costAggregationVos);
        List<CostAggregationVo> newCostAggregations = new LinkedList<>();
        calculationCountData(costAggregationVos, newCostAggregations);
        costAggregationVos.addAll(newCostAggregations);
        return costAggregationVos;
    }

    /**
     * Description: 费用汇总表给门店赋值
     *
     * @author: LuoY
     * @date: 2020/2/27 0027 11:05
     * @param:[costAggregationVos, resultMap]
     * @return:void
     */
    private void costItemValue(List<CostAggregationVo> costAggregationVos, Map<String, Map<String, BigDecimal>> resultMap) {
        costAggregationVos.forEach(cost -> {
            //获取当前门店所有数据
            Map<String, BigDecimal> result = resultMap.get(cost.getShopId());
            //工资费用
            cost.setWageCost(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.WAGE_COST)));
            //水电费
            cost.setWalterElectricity(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.WALTER_ELECTRICITY)));
            //燃气费
            cost.setGasFee(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.GAS_FEE)));
            //营业租金
            cost.setOpenratingRent(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.OPENRATING_RENT)));
            //宿舍租金
            cost.setDormitoryRent(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.DORMITORY_RENT)));
            //物业费
            cost.setPropertyFee(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.PROPERTY_FEE)));
            //赠送费用
            cost.setGiveMoney(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.GIVE_MONEY)));
            //低耗品工具类
            cost.setLowConsumptionTools(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.LOW_CONSUMPTION_TOOLS)));
            //低耗品消耗类
            cost.setLowConsumableConsumption(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.LOW_CONSUMABLE_CONSUMPTION)));
            //修理费
            cost.setRepairCost(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.REPAIR_COST)));
            //清洁费
            cost.setCleaningCharge(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.CLEANING_CHARGE)));
            //运输费
            cost.setFreight(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.FREIGHT)));
            //装饰费
            cost.setDecorationExpenses(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.DECORATION_EXPENSES)));
            //劳动保险费
            cost.setLaborInsurance(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.LABOR_INSURANCE)));
            //员工餐
            cost.setStaffMeal(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.STAFF_MEAL)));
            //其他福利
            cost.setOtherBenefits(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.OTHER_BENEFITS)));
            //办公费
            cost.setOfficeExpenses(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.OFFICE_EXPENSES)));
            //劳动保护费
            cost.setLaborProtectionFee(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.LABOR_PROTECTION_FEE)));
            //工会经费
            cost.setTradeUnionFunds(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.TRADE_UNION_FUNDS)));
            //职工教育经费
            cost.setStaffTrainingExpense(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.STAFF_TRAINING_EXPENSE)));
            //住房公积金
            cost.setHousingProvidentFund(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.HOUSING_PROVIDENT_FUND)));
            //长期待摊费
            cost.setLongTermUnamortizedExpenses(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.LONG_TERM_UNAMORTIZED_EXPENSES)));
            //通讯费
            cost.setCommunicationFee(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.COMUNICATION_FEE)));
            //折旧费
            cost.setDepreciationCharge(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.DEPRECIATION_CHAREGE)));
            //咨询费
            cost.setConsultationFee(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.CONSULATION_FEE)));
            //差旅费
            cost.setTravelExpenses(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.TRRAVEL_EXPENSES)));
            //商业保险
            cost.setCommercialInsurance(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.COMMERICAL_INSURANCE)));
            //业务招待费
            cost.setBusinessEntertainment(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.BUSINESS_ENTERTAINMENT)));
            //广告宣传费
            cost.setAdvertisingExpenses(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.ADVERTISING_EXPENSES)));
            //服务费
            cost.setServiceCharge(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.SERVICE_CHARGE)));
            //手续费
            cost.setServiceFee(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.SERVICE_FEE)));
            //其他费用
            cost.setOtherExpenses(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.OTHER_EXPENSES)));
            //利息收入
            cost.setInterestIncome(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.get(ReportDataConstant.CostAggregation.INTEREST_INCOME)));
            //合计
            cost.setCountMoney(result == null ? BigDecimal.ZERO : FastUtils.Null2Zero(result.entrySet().stream().map(data -> data.getValue()).reduce(BigDecimal.ZERO, BigDecimal::add)));
        });
    }

    /**
     * @Description: 去除空门店数据
     * @Param: [costAggregationVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/24 16:14
     */
    private void dataHandle(List<CostAggregationVo> costAggregationVos) {
        List<CostAggregationVo> costAggregationVos1 = new LinkedList<>();
        costAggregationVos.forEach(data -> {
            //如果合计金额大于0
            if (FastUtils.Null2Zero(data.getCountMoney()).compareTo(BigDecimal.ZERO) <= Constant.Number.ZERO) {
                costAggregationVos1.add(data);
            }
        });
        costAggregationVos.removeAll(costAggregationVos1);
    }

    /**
     * @Description: 计算费用汇总表汇总数据
     * @Param: [costAggregationVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/3 16:19
     */
    private void calculationCountData(List<CostAggregationVo> costAggregationVos, List<CostAggregationVo> newCostAggregationVos) {
        Map<String, List<CostAggregationVo>> brandCostAggregation = costAggregationVos.stream().collect(Collectors.groupingBy(CostAggregation::getBrandId));
        //计算总部
        CostAggregationVo costAggregationVo = new CostAggregationVo();
        costAggregationVo.setType(ReportDataConstant.Finance.TYPE_ALL);
        costAggregationVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        costAggregationVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        costAggregationVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        costAggregationDataHandle(costAggregationVo, costAggregationVos);
        newCostAggregationVos.add(costAggregationVo);
        //计算品牌
        brandCostAggregation.forEach((brand, data) -> {
            CostAggregationVo costAggregationVo1 = new CostAggregationVo();
            costAggregationVo1.setType(ReportDataConstant.Finance.TYPE_BRAND);
            costAggregationVo1.setBrandId(brand);
            costAggregationVo1.setBrandName(data.stream().findFirst().get().getBrandName());
            costAggregationVo1.setRegionName(ReportDataConstant.Finance.ALL_REGION);
            costAggregationVo1.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            costAggregationDataHandle(costAggregationVo1, data);
            newCostAggregationVos.add(costAggregationVo1);
        });
        //计算区域
        brandCostAggregation.forEach((brand, data) -> {
            Map<String, List<CostAggregationVo>> regionMap = data.stream().collect(Collectors.groupingBy(CostAggregationVo::getRegionId));
            regionMap.forEach((region, regionData) -> {
                CostAggregationVo costAggregationVo1 = new CostAggregationVo();
                costAggregationVo1.setType(ReportDataConstant.Finance.TYPE_REGION);
                costAggregationVo1.setBrandId(brand);
                costAggregationVo1.setBrandName(data.stream().findFirst().get().getBrandName());
                costAggregationVo1.setRegionId(region);
                costAggregationVo1.setRegionName(regionData.stream().findFirst().get().getRegionName());
                costAggregationVo1.setShopName(ReportDataConstant.Finance.ALL_SHOP);
                costAggregationDataHandle(costAggregationVo1, regionData);
                newCostAggregationVos.add(costAggregationVo1);
            });
        });
    }

    ;

    /**
     * @Description: 费用汇总表设置值
     * @Param: [costAggregationVo, costAggregationVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/3 16:48
     */
    private void costAggregationDataHandle(CostAggregationVo costAggregationVo, List<CostAggregationVo> costAggregationVos) {
        //工资费用
        costAggregationVo.setWageCost(costAggregationVos.stream().map(CostAggregationVo::getWageCost).reduce(BigDecimal.ZERO, BigDecimal::add));
        //水电费
        costAggregationVo.setWalterElectricity(costAggregationVos.stream().map(CostAggregationVo::getWalterElectricity).reduce(BigDecimal.ZERO, BigDecimal::add));
        //燃气费
        costAggregationVo.setGasFee(costAggregationVos.stream().map(CostAggregationVo::getGasFee).reduce(BigDecimal.ZERO, BigDecimal::add));
        //营业租金
        costAggregationVo.setOpenratingRent(costAggregationVos.stream().map(CostAggregationVo::getOpenratingRent).reduce(BigDecimal.ZERO, BigDecimal::add));
        //宿舍租金
        costAggregationVo.setDormitoryRent(costAggregationVos.stream().map(CostAggregationVo::getDormitoryRent).reduce(BigDecimal.ZERO, BigDecimal::add));
        //物业费
        costAggregationVo.setPropertyFee(costAggregationVos.stream().map(CostAggregationVo::getPropertyFee).reduce(BigDecimal.ZERO, BigDecimal::add));
        //赠送费用
        costAggregationVo.setGiveMoney(costAggregationVos.stream().map(CostAggregationVo::getGiveMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
        //低耗品工具类
        costAggregationVo.setLowConsumptionTools(costAggregationVos.stream().map(CostAggregationVo::getLowConsumptionTools).reduce(BigDecimal.ZERO, BigDecimal::add));
        //低耗品消耗类
        costAggregationVo.setLowConsumableConsumption(costAggregationVos.stream().map(CostAggregationVo::getLowConsumableConsumption).reduce(BigDecimal.ZERO, BigDecimal::add));
        //修理费
        costAggregationVo.setRepairCost(costAggregationVos.stream().map(CostAggregationVo::getLowConsumableConsumption).reduce(BigDecimal.ZERO, BigDecimal::add));
        //清洁费
        costAggregationVo.setCleaningCharge(costAggregationVos.stream().map(CostAggregationVo::getCleaningCharge).reduce(BigDecimal.ZERO, BigDecimal::add));
        //运输费
        costAggregationVo.setFreight(costAggregationVos.stream().map(CostAggregationVo::getFreight).reduce(BigDecimal.ZERO, BigDecimal::add));
        //装饰费
        costAggregationVo.setDecorationExpenses(costAggregationVos.stream().map(CostAggregationVo::getDecorationExpenses).reduce(BigDecimal.ZERO, BigDecimal::add));
        //劳动保险费
        costAggregationVo.setLaborInsurance(costAggregationVos.stream().map(CostAggregationVo::getLaborInsurance).reduce(BigDecimal.ZERO, BigDecimal::add));
        //员工餐
        costAggregationVo.setStaffMeal(costAggregationVos.stream().map(CostAggregationVo::getStaffMeal).reduce(BigDecimal.ZERO, BigDecimal::add));
        //其他福利
        costAggregationVo.setOtherBenefits(costAggregationVos.stream().map(CostAggregationVo::getOtherBenefits).reduce(BigDecimal.ZERO, BigDecimal::add));
        //办公费
        costAggregationVo.setOfficeExpenses(costAggregationVos.stream().map(CostAggregationVo::getOfficeExpenses).reduce(BigDecimal.ZERO, BigDecimal::add));
        //劳动保护费
        costAggregationVo.setLaborProtectionFee(costAggregationVos.stream().map(CostAggregationVo::getLaborInsurance).reduce(BigDecimal.ZERO, BigDecimal::add));
        //工会经费
        costAggregationVo.setTradeUnionFunds(costAggregationVos.stream().map(CostAggregationVo::getTradeUnionFunds).reduce(BigDecimal.ZERO, BigDecimal::add));
        //职工教育经费
        costAggregationVo.setStaffTrainingExpense(costAggregationVos.stream().map(CostAggregationVo::getStaffTrainingExpense).reduce(BigDecimal.ZERO, BigDecimal::add));
        //住房公积金
        costAggregationVo.setHousingProvidentFund(costAggregationVos.stream().map(CostAggregationVo::getHousingProvidentFund).reduce(BigDecimal.ZERO, BigDecimal::add));
        //长期待摊费
        costAggregationVo.setLongTermUnamortizedExpenses(costAggregationVos.stream().map(CostAggregationVo::getLongTermUnamortizedExpenses).reduce(BigDecimal.ZERO, BigDecimal::add));
        //通讯费
        costAggregationVo.setCommunicationFee(costAggregationVos.stream().map(CostAggregationVo::getCommunicationFee).reduce(BigDecimal.ZERO, BigDecimal::add));
        //折旧费
        costAggregationVo.setDepreciationCharge(costAggregationVos.stream().map(CostAggregationVo::getDepreciationCharge).reduce(BigDecimal.ZERO, BigDecimal::add));
        //咨询费
        costAggregationVo.setConsultationFee(costAggregationVos.stream().map(CostAggregationVo::getConsultationFee).reduce(BigDecimal.ZERO, BigDecimal::add));
        //差旅费
        costAggregationVo.setTravelExpenses(costAggregationVos.stream().map(CostAggregationVo::getTravelExpenses).reduce(BigDecimal.ZERO, BigDecimal::add));
        //商业保险
        costAggregationVo.setCommercialInsurance(costAggregationVos.stream().map(CostAggregationVo::getCommercialInsurance).reduce(BigDecimal.ZERO, BigDecimal::add));
        //业务招待费
        costAggregationVo.setBusinessEntertainment(costAggregationVos.stream().map(CostAggregationVo::getBusinessEntertainment).reduce(BigDecimal.ZERO, BigDecimal::add));
        //广告宣传费
        costAggregationVo.setAdvertisingExpenses(costAggregationVos.stream().map(CostAggregationVo::getAdvertisingExpenses).reduce(BigDecimal.ZERO, BigDecimal::add));
        //服务费
        costAggregationVo.setServiceCharge(costAggregationVos.stream().map(CostAggregationVo::getServiceCharge).reduce(BigDecimal.ZERO, BigDecimal::add));
        //手续费
        costAggregationVo.setServiceFee(costAggregationVos.stream().map(CostAggregationVo::getServiceFee).reduce(BigDecimal.ZERO, BigDecimal::add));
        //其他费用
        costAggregationVo.setOtherExpenses(costAggregationVos.stream().map(CostAggregationVo::getOtherExpenses).reduce(BigDecimal.ZERO, BigDecimal::add));
        //利息收入
        costAggregationVo.setInterestIncome(costAggregationVos.stream().map(CostAggregationVo::getInterestIncome).reduce(BigDecimal.ZERO, BigDecimal::add));
        //合计
        costAggregationVo.setCountMoney(costAggregationVos.stream().map(CostAggregationVo::getCountMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /**
     * 导出现金流量表
     *
     * @param excelExportDto
     * @param response
     * @return void
     * @Author lj
     * @Date:9:09 2020/2/12
     **/
    @Override
    public void exportCashExcel(ExcelExportDto excelExportDto, HttpServletResponse response) {
        BalanceDto balanceDto = new BalanceDto();
        FastUtils.copyProperties(excelExportDto, balanceDto);
        List<FinCashFlowReportTableVo> finCashFlowReportTableVos = findCashFlowReport(balanceDto);
        //没有数据，就显示“—”
        finCashFlowReportTableVos.stream().forEach(data -> {
            data.setAmountBalance(formatData(1, new BigDecimal(data.getAmountBalance())));
            data.setLastAmountBalance(formatData(1, new BigDecimal(data.getLastAmountBalance())));
        });

        fileService.exportExcelForQueryTerm(response, excelExportDto, finCashFlowReportTableVos,
                ExcelColumnConstant.CashFlowInfo.ITEM_NAME,
                ExcelColumnConstant.CashFlowInfo.AMOUNT_BALANCE,
                ExcelColumnConstant.CashFlowInfo.LAST_AMOUNT_BALANCE);
    }

    /**
     * 导出资产负债表
     *
     * @param response
     * @return void
     * @Author lj
     * @Date:11:38 2020/2/12
     **/
    @Override
    public void exportBalanceExcel(ExcelExportDto excelExportDto, HttpServletResponse response) {
        BalanceDto balanceDto = new BalanceDto();
        FastUtils.copyProperties(excelExportDto, balanceDto);
        FinReportTableVo finReportTableVo = findBalanceReport(balanceDto);
        //获取资产列表
        List<FinReportTableVo> assetsList = finReportTableVo.getAssetsList();
        //获取负债列表
        List<FinReportTableVo> debtList = finReportTableVo.getDebtList();
        List<FinReportTableVo> finReportTableVoList = new ArrayList<>();
        if (assetsList.size() > debtList.size()) {
            for (int i = 0; i < assetsList.size(); i++) {
                Integer assetsItemLevel = assetsList.get(i).getAssetsItemLevel();
                FinReportTableVo finReportTableVo1 = new FinReportTableVo();
                finReportTableVo1.setAssetsItemName(assetsList.get(i).getAssetsItemName());
                finReportTableVo1.setAssetsLineNumber(assetsList.get(i).getAssetsLineNumber());
                finReportTableVo1.setAssetsCloseBalanceS(formatData(assetsItemLevel, assetsList.get(i).getAssetsCloseBalance()));
                finReportTableVo1.setAssetsYearBalanceS(formatData(assetsItemLevel, assetsList.get(i).getAssetsYearBalance()));
                if (i < debtList.size() - 1 || i == assetsList.size() - 1) {
                    if (i == assetsList.size() - 1) {
                        Integer debtItemLevel = debtList.get(debtList.size() - 1).getDebtItemLevel();
                        finReportTableVo1.setDebtItemName(debtList.get(debtList.size() - 1).getDebtItemName());
                        finReportTableVo1.setDebtLineNumber(debtList.get(debtList.size() - 1).getDebtLineNumber());
                        finReportTableVo1.setDebtCloseBalanceS(formatData(debtItemLevel, debtList.get(debtList.size() - 1).getDebtCloseBalance()));
                        finReportTableVo1.setDebtYearBalanceS(formatData(debtItemLevel, debtList.get(debtList.size() - 1).getDebtYearBalance()));
                    } else {
                        Integer debtItemLevel = debtList.get(i).getDebtItemLevel();
                        finReportTableVo1.setDebtItemName(debtList.get(i).getDebtItemName());
                        finReportTableVo1.setDebtLineNumber(debtList.get(i).getDebtLineNumber());
                        finReportTableVo1.setDebtCloseBalanceS(formatData(debtItemLevel, debtList.get(i).getDebtCloseBalance()));
                        finReportTableVo1.setDebtYearBalanceS(formatData(debtItemLevel, debtList.get(i).getDebtYearBalance()));
                    }
                }
                finReportTableVoList.add(finReportTableVo1);
            }
        } else {
            for (int i = 0; i < debtList.size(); i++) {
                Integer debtItemLevel = debtList.get(i).getDebtItemLevel();
                FinReportTableVo finReportTableVo1 = new FinReportTableVo();
                finReportTableVo1.setDebtItemName(debtList.get(i).getDebtItemName());
                finReportTableVo1.setDebtLineNumber(debtList.get(i).getDebtLineNumber());
                finReportTableVo1.setDebtCloseBalanceS(formatData(debtItemLevel, debtList.get(i).getDebtCloseBalance()));
                finReportTableVo1.setDebtYearBalanceS(formatData(debtItemLevel, debtList.get(i).getDebtYearBalance()));
                if (i < assetsList.size() - 1 || i == debtList.size() - 1) {
                    if (i == debtList.size() - 1) {
                        Integer assetsItemLevel = assetsList.get(assetsList.size() - 1).getAssetsItemLevel();
                        finReportTableVo1.setAssetsItemName(assetsList.get(assetsList.size() - 1).getAssetsItemName());
                        finReportTableVo1.setAssetsLineNumber(assetsList.get(assetsList.size() - 1).getAssetsLineNumber());
                        finReportTableVo1.setAssetsCloseBalanceS(formatData(assetsItemLevel, assetsList.get(assetsList.size() - 1).getAssetsCloseBalance()));
                        finReportTableVo1.setAssetsYearBalanceS(formatData(assetsItemLevel, assetsList.get(assetsList.size() - 1).getAssetsYearBalance()));
                    } else {
                        Integer assetsItemLevel = assetsList.get(i).getAssetsItemLevel();
                        finReportTableVo1.setAssetsItemName(assetsList.get(i).getAssetsItemName());
                        finReportTableVo1.setAssetsLineNumber(assetsList.get(i).getAssetsLineNumber());
                        finReportTableVo1.setAssetsCloseBalanceS(formatData(assetsItemLevel, assetsList.get(i).getAssetsCloseBalance()));
                        finReportTableVo1.setAssetsYearBalanceS(formatData(assetsItemLevel, assetsList.get(i).getAssetsYearBalance()));
                    }
                }
                finReportTableVoList.add(finReportTableVo1);
            }
        }

        fileService.exportExcelForQueryTerm(response, excelExportDto, finReportTableVoList,
                ExcelColumnConstant.BalanceInfo.ASSETS_ITEM_NAME,
                ExcelColumnConstant.BalanceInfo.ASSETS_CLOSE_BALANCE,
                ExcelColumnConstant.BalanceInfo.ASSETS_YEAR_BALANCE,
                ExcelColumnConstant.BalanceInfo.DEBT_ITEM_NAME,
                ExcelColumnConstant.BalanceInfo.DEBT_CLOSE_BALANCE,
                ExcelColumnConstant.BalanceInfo.DEBT_YEAR_BALANCE);
    }

    /**
     * 格式化数据
     *
     * @param itemLevel, num
     * @return java.lang.String
     * @Author lj
     * @Date:15:12 2020/3/3
     **/
    private String formatData(Integer itemLevel, BigDecimal num) {
        String result = null;
        BigDecimal zero = new BigDecimal(BigInteger.ZERO);
        DecimalFormat format = new DecimalFormat("###,##0.00");
        if (itemLevel == 0) {
            result = "";
        }
        if (itemLevel != 0 && num.compareTo(zero) == 0) {
            result = "一";
        }
        if (itemLevel != 0 && num.compareTo(zero) != 0) {
            result = format.format(num);
        }
        return result;
    }

    /**
     * Description: 导出利润表
     *
     * @author: LuoY
     * @date: 2020/2/17 0017 16:01
     * @param:[balanceDto, response]
     * @return:void
     */
    @Override
    public void exportProfitExcel(ExcelExportDto excelExportDto, HttpServletResponse response) {
        BalanceDto balanceDto = new BalanceDto();
        FastUtils.copyProperties(excelExportDto, balanceDto);
        balanceDto.setReportId(ReportDataConstant.ReportItemReportId.PROFIT_REPORT);
        List<FinProfitReportTableVo> finProfitReportTableVos = findProfitReport(balanceDto);
        finProfitReportTableVos.stream().forEach(data -> {
            data.setAmountBalance(formatData(1, new BigDecimal(data.getAmountBalance())));
            data.setTotalAmountBalance(formatData(1, new BigDecimal(data.getTotalAmountBalance())));
        });
        fileService.exportExcelForQueryTerm(response, excelExportDto, finProfitReportTableVos,
                ExcelColumnConstant.ProfitReport.ITEM_NAME,
                ExcelColumnConstant.ProfitReport.AMOUNT_BALANCE,
                ExcelColumnConstant.ProfitReport.TOTAL_AMOUNT_BALANCE);
    }

    /**
     * @Description: 导出费用汇总表
     * @Param: [balanceDto, response]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/2 11:
     */
    @Override
    public void exportCostExcel(ExcelExportDto excelExportDto, HttpServletResponse response) {
        BalanceDto balanceDto = new BalanceDto();
        FastUtils.copyProperties(excelExportDto, balanceDto);
        //查询费用汇总表
        List<CostAggregationVo> costAggregationVos = findCostAggregationReport(balanceDto);
        List<CostAggregationVo> costAggregationVos1 = new LinkedList<>();
        if (!FastUtils.checkNullOrEmpty(costAggregationVos)) {
            //按品牌
            if (excelExportDto.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)) {
                costAggregationVos1 = costAggregationVos.stream().filter(data -> data.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
            } else if (excelExportDto.getType().equals(ReportDataConstant.Finance.TYPE_REGION)) {
                //按区域
                costAggregationVos1 = costAggregationVos.stream().filter(data -> data.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
            } else if (excelExportDto.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)) {
                //按门店
                costAggregationVos1 = costAggregationVos.stream().filter(data -> data.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)).collect(Collectors.toList());
            } else {
                //全部
                costAggregationVos1 = costAggregationVos.stream().filter(data -> data.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
            }
            fileService.exportExcelForQueryTerm(response, excelExportDto, costAggregationVos1, ExcelColumnConstant.CostAggregation.BRANDE_NAME,
                    ExcelColumnConstant.CostAggregation.REGION_NAME, ExcelColumnConstant.CostAggregation.SHOP_NAME,
                    ExcelColumnConstant.CostAggregation.WAGE_COST, ExcelColumnConstant.CostAggregation.WALTER_ELECTRICITY,
                    ExcelColumnConstant.CostAggregation.GAS_FEE, ExcelColumnConstant.CostAggregation.OPENRATING_RENT,
                    ExcelColumnConstant.CostAggregation.DORMITORY_RENT, ExcelColumnConstant.CostAggregation.PROPERTY_FEE,
                    ExcelColumnConstant.CostAggregation.GIVE_MONEY, ExcelColumnConstant.CostAggregation.LOW_CONSUMPTION_TOOLS,
                    ExcelColumnConstant.CostAggregation.LOW_CONSUMABLE_CONSUMPTION, ExcelColumnConstant.CostAggregation.REPAIR_COST,
                    ExcelColumnConstant.CostAggregation.CLEANING_CHARGE, ExcelColumnConstant.CostAggregation.FREIGHT,
                    ExcelColumnConstant.CostAggregation.DECORATION_EXPENSES, ExcelColumnConstant.CostAggregation.LABOR_INSURANCE, ExcelColumnConstant.CostAggregation.STAFF_MEAL,
                    ExcelColumnConstant.CostAggregation.OTHER_BENEFITS, ExcelColumnConstant.CostAggregation.OFFICE_EXPENSES,
                    ExcelColumnConstant.CostAggregation.LABOR_PROTECTION_FEE, ExcelColumnConstant.CostAggregation.TRADE_UNION_FUNDS,
                    ExcelColumnConstant.CostAggregation.STAFF_TRAINING_EXPENSES, ExcelColumnConstant.CostAggregation.HOUSING_PROVIDENT_FUND,
                    ExcelColumnConstant.CostAggregation.LONG_TERN_UNAMORTIZED_EXPENSES, ExcelColumnConstant.CostAggregation.COMMUNICATION_FEE,
                    ExcelColumnConstant.CostAggregation.DEPRECIATION_CHARGE, ExcelColumnConstant.CostAggregation.CONSULTATION_FEE,
                    ExcelColumnConstant.CostAggregation.TRAVEL_EXPENSES, ExcelColumnConstant.CostAggregation.COMMERCIAL_INSURANCE,
                    ExcelColumnConstant.CostAggregation.BUSINESS_ENTERTAINMENT, ExcelColumnConstant.CostAggregation.ADVERTISING_EXPENSES,
                    ExcelColumnConstant.CostAggregation.SERVICE_CHARGE, ExcelColumnConstant.CostAggregation.OTHER_EXPENSES,
                    ExcelColumnConstant.CostAggregation.INTEREST_INCOME, ExcelColumnConstant.CostAggregation.SERVICE_FEE,
                    ExcelColumnConstant.CostAggregation.COUNT_MONEY);

        }
    }
}

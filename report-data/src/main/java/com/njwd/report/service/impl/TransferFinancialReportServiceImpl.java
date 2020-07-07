package com.njwd.report.service.impl;

import com.njwd.basedata.mapper.BaseAccountBookMapper;
import com.njwd.basedata.service.BaseAccountSubjectService;
import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.dto.BaseAccountBookDto;
import com.njwd.entity.basedata.dto.BaseAccountSubjectDto;
import com.njwd.entity.basedata.vo.BaseAccountBookVo;
import com.njwd.entity.basedata.vo.BaseAccountSubjectVo;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.dto.BalanceDto;
import com.njwd.entity.reportdata.dto.FinBalanceCashFlowDto;
import com.njwd.entity.reportdata.dto.FinBalanceSubjectDto;
import com.njwd.entity.reportdata.vo.FinBalanceCashFlowVo;
import com.njwd.entity.reportdata.vo.FinBalanceSubjectVo;
import com.njwd.entity.reportdata.vo.FinancialReportItemFormulaVo;
import com.njwd.entity.reportdata.vo.FinancialReportItemSetVo;
import com.njwd.report.mapper.FinBalanceCashFlowMapper;
import com.njwd.report.mapper.FinBalanceSubjectMapper;
import com.njwd.report.mapper.FinReportTableMapper;
import com.njwd.report.mapper.FinancialReportMapper;
import com.njwd.report.service.TransferFinancialReportService;
import com.njwd.support.Result;
import com.njwd.utils.CollectionUtil;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author lj
 * @Description 财务报表_触发Job
 * @Date:17:11 2020/1/9
 **/
@Service
public class TransferFinancialReportServiceImpl implements TransferFinancialReportService {

    @Resource
    private FinancialReportMapper financialReportMapper;

    @Resource
    private BaseAccountBookMapper baseAccountBookMapper;

    @Resource
    private FinBalanceSubjectMapper finBalanceSubjectMapper;

    @Resource
    private FinReportTableMapper finReportTableMapper;

    @Resource
    private BaseAccountSubjectService baseAccountSubjectService;

    @Resource
    private FinBalanceCashFlowMapper finBalanceCashFlowMapper;

    /**
     * 资产负债表/利润表（基准数据表生成）
     *
     * @param transferReportDto
     * @return com.njwd.support.Result<java.lang.String>
     * @Author lj
     * @Date:17:03 2020/1/9
     **/
    @Override
    public String transferBalanceReport(TransferReportSimpleDto transferReportDto) {
        String result = Constant.ReqResult.SUCCESS;
        Calendar calendar = Calendar.getInstance();
        try {
            //查询资产负债表报表项目配置及公式
            BalanceDto balanceDto = new BalanceDto();
            balanceDto.setReportId(ReportDataConstant.ReportItemReportId.BALANCE_REPORT);
            balanceDto.setEnteId(transferReportDto.getEnteId());
            List<Integer> reportIdList = new LinkedList<>();
            reportIdList.add(ReportDataConstant.ReportItemReportId.BALANCE_REPORT);
            reportIdList.add(ReportDataConstant.ReportItemReportId.PROFIT_REPORT);
            balanceDto.setReportIdList(reportIdList);
            List<FinancialReportItemSetVo> financialReportItemList = financialReportMapper.findFinancialReportItemList(balanceDto);
            //查询利润表的项目计算公式
            balanceDto.setReportId(ReportDataConstant.ReportItemReportId.PROFIT_REPORT);
            List<FinancialReportItemSetVo> financialProfitReportItemList = financialReportMapper.findFinancialReportItemList(balanceDto);
            //查询企业的所有店铺的账簿信息列表
            BaseAccountBookDto baseAccountBookDto = new BaseAccountBookDto();
            baseAccountBookDto.setEnteId(transferReportDto.getEnteId());
            List<BaseAccountBookVo> baseAccountBookVoList = baseAccountBookMapper.findBaseAccountBook(baseAccountBookDto);
            //查询当前期间科目余额数据
            String transDay = transferReportDto.getTransDay();
            if (StringUtil.isEmpty(transDay)){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                transDay = simpleDateFormat.format(DateUtils.subMonths(calendar.getTime(),Constant.Number.THREE));
            }
            Integer periodYearNum = DateUtils.getPeriodYearNum(transDay);
            //先删除已有资产负载和利润表数据
            balanceDto.setPeriodYearNum(periodYearNum);
            finReportTableMapper.deleteReportTableDataByCondition(balanceDto);
            for (BaseAccountBookVo baseAccountBookVo : baseAccountBookVoList) {
                FinBalanceSubjectDto finBalanceSubjectDto = new FinBalanceSubjectDto();
                finBalanceSubjectDto.setEnteId(transferReportDto.getEnteId());
                finBalanceSubjectDto.setAccountBookId(baseAccountBookVo.getAccountBookId());
                finBalanceSubjectDto.setPeriodYearNum(periodYearNum);
                List<FinBalanceSubjectVo> finBalanceSubjectVoList = finBalanceSubjectMapper.findFinBalanceSubjectList(finBalanceSubjectDto);
                List<FinancialReportItemSetVo> financialReportItemListAll = new LinkedList<>();
                //根据记账期间年号分组
                Map<Integer, List<FinBalanceSubjectVo>> finBalanceSubjectVoMap = finBalanceSubjectVoList.stream().collect(Collectors.groupingBy(FinBalanceSubjectVo::getPeriodYearNum));
                for (Map.Entry<Integer, List<FinBalanceSubjectVo>> entry : finBalanceSubjectVoMap.entrySet()) {
                    Integer periodYear = entry.getKey() / 100;
                    //获取期末的反值
                    entry.getValue().forEach(date -> {
                        if (date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.FT) || date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.FF)) {
                            date.setCloseBalance(date.getCloseBalanceY().negate());
                        }
                        if ((date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.TT)||date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.TO)) && date.getCloseBalanceY().compareTo(new BigDecimal(BigInteger.ZERO)) > 0) {
                            date.setCloseBalance(date.getCloseBalance().negate());
                        }
                        if (date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.OO) && date.getCloseBalanceY().compareTo(new BigDecimal(BigInteger.ZERO)) < 0) {
                            date.setCloseBalance(date.getCloseBalance().negate());
                        }
                    });
                    //期末余额map，key 科目code,value期末余额
                    Map<String, BigDecimal> closeBalanceSubjectMap = entry.getValue().stream().collect(Collectors.toMap(FinBalanceSubjectVo::getAccountSubjectCode, FinBalanceSubjectVo::getCloseBalance));

                    //查询当年最小期间科目余额数据
                    FinBalanceSubjectDto yearfinBalanceSubjectDto = new FinBalanceSubjectDto();
                    yearfinBalanceSubjectDto.setEnteId(transferReportDto.getEnteId());
                    yearfinBalanceSubjectDto.setAccountBookId(baseAccountBookVo.getAccountBookId());
                    yearfinBalanceSubjectDto.setPeriodYear(periodYear);
                    List<FinBalanceSubjectVo> finYearBalanceSubjectList = finBalanceSubjectMapper.findYearFinBalanceSubjectList(yearfinBalanceSubjectDto);
                    //年初余额的反值
                    finYearBalanceSubjectList.forEach(date -> {
                        if (date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.FT) || date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.FF)) {
                            date.setOpenBalance(date.getOpenBalanceY().negate());
                        }
                        if ((date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.TT)||date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.TO)) && date.getOpenBalanceY().compareTo(new BigDecimal(BigInteger.ZERO)) > 0) {
                            date.setOpenBalance(date.getOpenBalance().negate());
                        }
                        if (date.getAccountSubjectCode().equals(ReportDataConstant.ReportTable.OO) && date.getOpenBalanceY().compareTo(new BigDecimal(BigInteger.ZERO)) < 0) {
                            date.setOpenBalance(date.getOpenBalance().negate());
                        }
                    });
                    //年初余额map，key 科目code,value年初余额
                    Map<String, BigDecimal> yearBalanceSubjectMap = finYearBalanceSubjectList.stream().collect(Collectors.toMap(FinBalanceSubjectVo::getAccountSubjectCode, FinBalanceSubjectVo::getOpenBalance));
                    //通过公式计算报表项目的期末余额
                    Map<String, BigDecimal> closeFinancialReportMap = financialReportCalculationResult(financialReportItemList, closeBalanceSubjectMap);
                    //通过公式计算报表项目的年初余额
                    Map<String, BigDecimal> yearFinancialReportMap = financialReportCalculationResult(financialReportItemList, yearBalanceSubjectMap);
                    //循环给报表项目设置计算好的值
                    if (!FastUtils.checkNullOrEmpty(baseAccountBookVo.getShopId())) {
                        for (FinancialReportItemSetVo financialReportItemSetVo : financialReportItemList) {
                            financialReportItemSetVo.setCloseBalance(closeFinancialReportMap.get(financialReportItemSetVo.getItemCode()));
                            financialReportItemSetVo.setYearBalance(yearFinancialReportMap.get(financialReportItemSetVo.getItemCode()));
                            financialReportItemSetVo.setBrandId(baseAccountBookVo.getBrandId());
                            financialReportItemSetVo.setRegionId(baseAccountBookVo.getRegionId());
                            financialReportItemSetVo.setShopId(baseAccountBookVo.getShopId());
                            financialReportItemSetVo.setShopTypeId(baseAccountBookVo.getShopTypeId());
                            financialReportItemSetVo.setPeriodYearNum(entry.getKey());
                        }
                        List<FinancialReportItemSetVo> tempList = CollectionUtil.deepCopy(financialReportItemList);
                        financialReportItemListAll.addAll(tempList);
                    }
                }
                //计算利润表
                profitTableData(transferReportDto, baseAccountBookVo, finBalanceSubjectVoMap, financialProfitReportItemList, balanceDto, financialReportItemListAll);
                if (!FastUtils.checkNullOrEmpty(financialReportItemListAll)) {
                    //插入计算结果
                    if (financialReportItemListAll.size() > 500) {
                        Map<Integer, List<FinancialReportItemSetVo>> map = FastUtils.splitList(financialReportItemListAll, 500);
                        for (int i : map.keySet()) {
                            List<FinancialReportItemSetVo> financialReportItemSetVos = map.get(i);
                            finReportTableMapper.refreshFinReportTable(financialReportItemSetVos);
                        }
                    } else {
                        finReportTableMapper.refreshFinReportTable(financialReportItemListAll);
                    }
                }

            }
        } catch (Exception e) {
            result = Constant.ReqResult.FAIL;
        }
        return result;
    }


    /**
     * 现金流量表SaleAnalysisMapper
     *
     * @param transferReportDto
     * @return com.njwd.support.Result<java.lang.String>
     * @Author lj
     * @Date:11:53 2020/2/10
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String transferCashFlowReport(TransferReportSimpleDto transferReportDto) {
        String result = Constant.ReqResult.SUCCESS;
        try {
            //查询现金流量表报表项目配置及公式
            BalanceDto balanceDto = new BalanceDto();
            balanceDto.setReportId(ReportDataConstant.ReportItemReportId.CASHFLOW_REPORT);
            balanceDto.setEnteId(transferReportDto.getEnteId());
            List<FinancialReportItemSetVo> financialCashReportItemList = financialReportMapper.findFinancialReportItemList(balanceDto);
            //获取特殊项目的编码
            List<String> subList = cashFlowSpecial(financialCashReportItemList);
            //查询企业的所有店铺的账簿信息列表
            BaseAccountBookDto baseAccountBookDto = new BaseAccountBookDto();
            baseAccountBookDto.setEnteId(transferReportDto.getEnteId());
            List<BaseAccountBookVo> baseAccountBookVoList = baseAccountBookMapper.findBaseAccountBook(baseAccountBookDto);
            //查询当前期间科目余额数据
            String transDay = transferReportDto.getTransDay();
            Integer periodYearNum = DateUtils.getPeriodYearNum(transDay);
            List<Integer> reportIdList = new LinkedList<>();
            reportIdList.add(ReportDataConstant.ReportItemReportId.CASHFLOW_REPORT);
            balanceDto.setReportIdList(reportIdList);
            //先删除已有现金流量表数据
            balanceDto.setPeriodYearNum(periodYearNum);
            finReportTableMapper.deleteReportTableDataByCondition(balanceDto);
            for (BaseAccountBookVo baseAccountBookVo : baseAccountBookVoList) {
                List<FinancialReportItemSetVo> financialReportItemListAll = new LinkedList<>();
                FinBalanceSubjectDto finBalanceSubjectDto = new FinBalanceSubjectDto();
                finBalanceSubjectDto.setEnteId(transferReportDto.getEnteId());
                finBalanceSubjectDto.setAccountBookId(baseAccountBookVo.getAccountBookId());
                finBalanceSubjectDto.setPeriodYearNum(periodYearNum);
                finBalanceSubjectDto.setSubList(subList);
                List<FinBalanceSubjectVo> finBalanceSubjectVoList = finBalanceSubjectMapper.findFinBalanceSubjectList(finBalanceSubjectDto);
                //根据记账期间年号分组
                Map<Integer, List<FinBalanceSubjectVo>> finBalanceSubjectVoMap = finBalanceSubjectVoList.stream().collect(Collectors.groupingBy(FinBalanceSubjectVo::getPeriodYearNum));
                for (Map.Entry<Integer, List<FinBalanceSubjectVo>> entry : finBalanceSubjectVoMap.entrySet()) {
                    Integer yearNum = entry.getKey();
                    //科目的本期map，key 科目code,value本期
                    Map<String, BigDecimal> subjectAmountMap = entry.getValue().stream().collect(Collectors.toMap(FinBalanceSubjectVo::getAccountSubjectCode, FinBalanceSubjectVo::getOpenBalance));
                    //查询本期现金流量数据
                    FinBalanceCashFlowDto cashFlowDto = new FinBalanceCashFlowDto();
                    cashFlowDto.setEnteId(transferReportDto.getEnteId());
                    cashFlowDto.setAccountBookId(baseAccountBookVo.getAccountBookId());
                    cashFlowDto.setPeriodYearNum(yearNum);
                    cashFlowDto.setFlag("1");
                    List<FinBalanceCashFlowVo> finBalanceCashFlowList = finBalanceCashFlowMapper.findFinBalanceCashFlowList(cashFlowDto);
                    //现金流量本期map，key 现金code,value本期金额
                    Map<String, BigDecimal> cashMap = finBalanceCashFlowList.stream().collect(Collectors.toMap(FinBalanceCashFlowVo::getCashFlowItemCode, FinBalanceCashFlowVo::getOccurAmount));
                    cashMap.putAll(subjectAmountMap);
                    //通过公式计算报表项目的本期金额
                    Map<String, BigDecimal> financialReportMap = financialReportCalculationResult(financialCashReportItemList, cashMap);

                    //获取上一期的期间
                    Integer lastYearNum = yearNum - 1;
                    if (lastYearNum % 100 == 0) {
                        lastYearNum = lastYearNum - 100 + 12;
                    }
                    //查询上期科目余额数据
                    FinBalanceSubjectDto subjectDto = new FinBalanceSubjectDto();
                    subjectDto.setEnteId(transferReportDto.getEnteId());
                    subjectDto.setAccountBookId(baseAccountBookVo.getAccountBookId());
                    subjectDto.setPeriodYearNum(lastYearNum);
                    subjectDto.setSubList(subList);
                    subjectDto.setFlag("1");
                    List<FinBalanceSubjectVo> lastBalanceSubjectVoList = finBalanceSubjectMapper.findFinBalanceSubjectList(subjectDto);
                    //科目的上期map，key 科目code,value本期金额
                    Map<String, BigDecimal> lastSubjectAmountMap = lastBalanceSubjectVoList.stream().collect(Collectors.toMap(FinBalanceSubjectVo::getAccountSubjectCode, FinBalanceSubjectVo::getOpenBalance));
                    //查询上期现金流量数据
                    cashFlowDto.setPeriodYearNum(lastYearNum);
                    List<FinBalanceCashFlowVo> lastBalanceCashFlowList = finBalanceCashFlowMapper.findFinBalanceCashFlowList(cashFlowDto);
                    //现金流量上期map，key 现金code,value本期金额
                    Map<String, BigDecimal> lastCashMap = lastBalanceCashFlowList.stream().collect(Collectors.toMap(FinBalanceCashFlowVo::getCashFlowItemCode, FinBalanceCashFlowVo::getOccurAmount));
                    lastCashMap.putAll(lastSubjectAmountMap);
                    //通过公式计算报表项目的上期金额
                    Map<String, BigDecimal> lastFinancialReportMap = financialReportCalculationResult(financialCashReportItemList, lastCashMap);
                    //循环给报表项目设置计算好的值
                    if (!FastUtils.checkNullOrEmpty(baseAccountBookVo.getBrandId(), baseAccountBookVo.getRegionId(), baseAccountBookVo.getShopId())) {
                        for (FinancialReportItemSetVo financialCashReportItem : financialCashReportItemList) {
                            financialCashReportItem.setAmountBalance(financialReportMap.get(financialCashReportItem.getItemCode()));
                            financialCashReportItem.setLastAmountBalance(lastFinancialReportMap.get(financialCashReportItem.getItemCode()));
                            financialCashReportItem.setBrandId(baseAccountBookVo.getBrandId());
                            financialCashReportItem.setRegionId(baseAccountBookVo.getRegionId());
                            financialCashReportItem.setShopId(baseAccountBookVo.getShopId());
                            financialCashReportItem.setShopTypeId(baseAccountBookVo.getShopTypeId());
                            financialCashReportItem.setPeriodYearNum(entry.getKey());
                        }
                        List<FinancialReportItemSetVo> tempList = CollectionUtil.deepCopy(financialCashReportItemList);
                        financialReportItemListAll.addAll(tempList);
                    }
                }
                if (!FastUtils.checkNullOrEmpty(financialReportItemListAll)) {

                    finReportTableMapper.refreshFinReportTable(financialReportItemListAll);
                }
            }

        } catch (Exception e) {
            result = Constant.ReqResult.FAIL;
        }
        return result;
    }

    /**
     * 获取特殊项目的编码
     *
     * @param financialReportItemSetVos
     * @return java.util.List<java.lang.String>
     * @Author lj
     * @Date:17:37 2020/2/10
     **/
    private List<String> cashFlowSpecial(List<FinancialReportItemSetVo> financialReportItemSetVos) {
        List<String> subList = new ArrayList<>();
        for (FinancialReportItemSetVo financialReportItemSetVo1 : financialReportItemSetVos) {
            for (FinancialReportItemFormulaVo financialReportItemFormulaVo : financialReportItemSetVo1.getFinancialReportItemFormulaVoList()) {
                if (ReportDataConstant.FormulaType.SPECIALCASHFLOW.equals(financialReportItemFormulaVo.getFormulaType())) {
                    subList.add(financialReportItemFormulaVo.getFormulaItemCode());
                }
            }
        }
        return subList;
    }

    /**
     * @Description: 按账簿生成利润表基准数据
     * @Param: [baseAccountBookVo]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/2/6 10:51
     */
    private void profitTableData(TransferReportSimpleDto transferReportDto, BaseAccountBookVo baseAccountBookVo,
                                 Map<Integer, List<FinBalanceSubjectVo>> finBalanceSubjectVoMap,
                                 List<FinancialReportItemSetVo> financialProfitReportItemList, BalanceDto balanceDto,
                                 List<FinancialReportItemSetVo> financialReportItemListAll) {
        balanceDto.setReportId(ReportDataConstant.ReportItemReportId.PROFIT_REPORT);
        //查询科目方向
        BaseAccountSubjectDto baseAccountSubjectDto = new BaseAccountSubjectDto();
        //baseAccountSubjectDto.setSubjectId();
        baseAccountSubjectDto.setEnteId(transferReportDto.getEnteId());
        //查询科目信息
        List<BaseAccountSubjectVo> baseAccountSubjectVos = baseAccountSubjectService.findBaseAccountSubjectList(baseAccountSubjectDto);
        //科目list转map
        Map<String, Integer> baseAccountSubject = baseAccountSubjectVos.stream().collect(Collectors.toMap(BaseAccountSubjectVo::getAccountSubjectCode, BaseAccountSubjectVo::getBalanceDirection, (key1, key2) -> key2));
        //获取项目序号
        Map<String, String> reportItemNumber = financialProfitReportItemList.stream().collect(Collectors.toMap(FinancialReportItemSetVo::getItemCode, FinancialReportItemSetVo::getItemNumber));
        //循环科目余额表
        for (Map.Entry<Integer, List<FinBalanceSubjectVo>> entry : finBalanceSubjectVoMap.entrySet()) {
            //获取科目的本期和本年累计发生额
            entry.getValue().forEach(data -> {
                //根据科目方向计算金额，科目借方：+借-贷；科目贷方：+贷-借
                if (baseAccountSubject.get(data.getAccountSubjectCode()).equals(ReportDataConstant.BalanceDirection.DEBIT)) {
                    data.setSubjectAmount(data.getDebitAmount());
                    data.setSubjectTotalAmount(data.getTotalDebitAmount());
                } else if (baseAccountSubject.get(data.getAccountSubjectCode()).equals(ReportDataConstant.BalanceDirection.CREDIT)) {
                    data.setSubjectAmount(data.getCreditAmount());
                    data.setSubjectTotalAmount(data.getTotalCreditAmount());
                }
            });
            //获取科目本期发生金额Map
            Map<String, BigDecimal> subjectAmount = entry.getValue().stream().collect(Collectors.toMap(FinBalanceSubjectVo::getAccountSubjectCode, FinBalanceSubjectVo::getSubjectAmount));
            //获取科目本年累计金额Map
            Map<String, BigDecimal> subjectTotalAmount = entry.getValue().stream().collect(Collectors.toMap(FinBalanceSubjectVo::getAccountSubjectCode, FinBalanceSubjectVo::getSubjectTotalAmount));
            //通过公式计算报表项目本期发生金额
            Map<String, BigDecimal> itemAmount = financialReportCalculationResult(financialProfitReportItemList, subjectAmount);
            //通过公式计算报表项目的本年累计余额
            Map<String, BigDecimal> itemTotalAmount = financialReportCalculationResult(financialProfitReportItemList, subjectTotalAmount);

            //循环给报表项目设置计算好的值
            if (!FastUtils.checkNullOrEmpty(baseAccountBookVo.getShopId())) {
                for (FinancialReportItemSetVo financialReportItemSetVo : financialProfitReportItemList) {
                    financialReportItemSetVo.setAmountBalance(itemAmount.get(financialReportItemSetVo.getItemCode()));
                    financialReportItemSetVo.setTotalAmountBalance(itemTotalAmount.get(financialReportItemSetVo.getItemCode()));
                    financialReportItemSetVo.setBrandId(baseAccountBookVo.getBrandId());
                    financialReportItemSetVo.setRegionId(baseAccountBookVo.getRegionId());
                    financialReportItemSetVo.setShopId(baseAccountBookVo.getShopId());
                    financialReportItemSetVo.setPeriodYearNum(entry.getKey());
                    financialReportItemSetVo.setShopTypeId(baseAccountBookVo.getShopTypeId());
                    financialReportItemSetVo.setItemNumber(reportItemNumber.get(financialReportItemSetVo.getItemCode()));
                }
                List<FinancialReportItemSetVo> tempList = CollectionUtil.deepCopy(financialProfitReportItemList);
                financialReportItemListAll.addAll(tempList);
            }
        }
    }

    /**
     * @param financialReportItemSetVos 财务报告公式计算
     * @param balanceSubject            科目余额信息
     * @return java.util.Map<java.lang.String, java.math.BigDecimal>  map<财务报告行号,财务报告计算金额>
     * @description: 财务报告计算公式
     * @author LuoY
     * @date 2019/8/6 10:42
     */
    @Override
    public Map<String, BigDecimal> financialReportCalculationResult(@NotNull List<FinancialReportItemSetVo> financialReportItemSetVos,
                                                                    @NotNull Map<String, BigDecimal> balanceSubject) {
        Map<String, BigDecimal> financialReportResult = new HashMap<>();
        //循环财务报告
        for (FinancialReportItemSetVo platformFinancialReportItemSetVo : financialReportItemSetVos) {
            BigDecimal result = BigDecimal.ZERO;
            if (!FastUtils.checkNullOrEmpty(platformFinancialReportItemSetVo.getFinancialReportItemFormulaVoList())) {
                //如果报告公式列表不为空,循环计算公式
                for (FinancialReportItemFormulaVo financialReportItemFormulaVo : platformFinancialReportItemSetVo.getFinancialReportItemFormulaVoList()) {
                    if (!StringUtil.isBlank(financialReportItemFormulaVo.getId())) {
                        if (ReportDataConstant.FormulaType.SUBECTORITEM.equals(financialReportItemFormulaVo.getFormulaType())) {
                            //按code计算,从科目余额取
                            if (ReportDataConstant.Operator.ADD.equals(financialReportItemFormulaVo.getOperator())) {
                                //加
                                result = result.add(FastUtils.Null2Zero(balanceSubject.get(financialReportItemFormulaVo.getFormulaItemCode())));
                            } else if (ReportDataConstant.Operator.SUBTRACT.equals(financialReportItemFormulaVo.getOperator())) {
                                //减
                                result = result.subtract(FastUtils.Null2Zero(balanceSubject.get(financialReportItemFormulaVo.getFormulaItemCode())));
                            }
                        } else if (ReportDataConstant.FormulaType.ITEMLINE.equals(financialReportItemFormulaVo.getFormulaType())) {
                            //按行计算,从计算结果集取
                            if (ReportDataConstant.Operator.ADD.equals(financialReportItemFormulaVo.getOperator())) {
                                //加
                                result = result.add(FastUtils.Null2Zero(financialReportResult.get(financialReportItemFormulaVo.getFormulaItemCode())));
                            } else if (ReportDataConstant.Operator.SUBTRACT.equals(financialReportItemFormulaVo.getOperator())) {
                                //减
                                result = result.subtract(FastUtils.Null2Zero(financialReportResult.get(financialReportItemFormulaVo.getFormulaItemCode())));
                            }
                        } else if (ReportDataConstant.FormulaType.SPECIALCASHFLOW.equals(financialReportItemFormulaVo.getFormulaType())) {
                            //现金流量特殊项
                            //按code计算,从科目余额取
                            if (ReportDataConstant.Operator.ADD.equals(financialReportItemFormulaVo.getOperator())) {
                                //加
                                result = result.add(FastUtils.Null2Zero(balanceSubject.get(financialReportItemFormulaVo.getFormulaItemCode())));
                            } else if (ReportDataConstant.Operator.SUBTRACT.equals(financialReportItemFormulaVo.getOperator())) {
                                //减
                                result = result.subtract(FastUtils.Null2Zero(balanceSubject.get(financialReportItemFormulaVo.getFormulaItemCode())));
                            }
                        }
                    }
                }
            }
            financialReportResult.put(platformFinancialReportItemSetVo.getItemCode(), result);
        }
        return financialReportResult;
    }
}

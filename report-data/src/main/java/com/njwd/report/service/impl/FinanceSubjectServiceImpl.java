package com.njwd.report.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.util.CollectionUtils;
import com.njwd.common.LogConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.FinReport;
import com.njwd.entity.reportdata.FinReportConfig;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.fin.*;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.report.mapper.FinReportConfigMapper;
import com.njwd.report.mapper.FinReportMapper;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *@description: 科目及凭证数据实现类
 *@author: fancl
 *@create: 2020-01-04 
 */
@Service
public class FinanceSubjectServiceImpl implements FinanceSubjectService {

    @Resource
    FinReportConfigMapper finReportConfigMapper;
    @Resource
    FinReportMapper finReportMapper;
    //Log对象
    private Logger logger = LoggerFactory.getLogger(FinanceSubjectServiceImpl.class);

    @Override
    @Deprecated
    public FinReportConfigVo getFinReportConfig(String enteId, String finType) {
        FinReportConfig query = new FinReportConfig();
        query.setEnteId(enteId);
        query.setFinType(finType);
        FinReportConfigVo reportConfig = finReportConfigMapper.findConfigByType(query);
        return reportConfig;
    }

    @Override
    public FinReportConfigVo getConfigByGroupAndType(String enteId, String finGroup, String finType) {
        List<FinReportConfigVo> configVoList = getConfigVoList(enteId, finGroup, finType);
        if (configVoList == null || configVoList.size() != 1) {
            throw new ServiceException(ResultCode.FIN_REPORT_CONFIG_ERROR);
        }
        return configVoList.get(0);
    }

    /**
     * @description 根据企业id 和 group查询多个对象
     * @author fancl
     * @date 2020/2/21
     * @param
     * @return
     */
    @Override
    public List<FinReportConfigVo> getConfigByGroup(String enteId, String finGroup) {
        List<FinReportConfigVo> configVoList = getConfigVoList(enteId, finGroup, null);
        //将每个元素中的公式处理下
        for (FinReportConfigVo reportConfigVo : configVoList) {
            //codeList
            String codesStr = reportConfigVo.getCodes();
            List<String> codesList = Arrays.asList(codesStr.split("\\,"));
            reportConfigVo.setSubjectCodeList(codesList);
            //公式字符串
            String formulaStr = reportConfigVo.getCodesType();
            //将价格表达式进行处理后 ,设置在list对象中
            //包含价格表达式元素的List
            if (StringUtil.isNotEmpty(formulaStr)) {
                List<FormulaVo> formulaVos = getFormula(formulaStr);
                reportConfigVo.setFormulaExpList(formulaVos);
            }

        }

        return configVoList;
    }

    /**
     * @description 获取标准公式配置对象
     * 标准的意思: fin_report表的codes列是多个科目 ,code_type是科目计算表达式
     *
     * @author fancl
     * @date 2020/2/19
     * @param
     * @return
     */
    @Override
    public FinReportConfigVo getFinConfigAndFormula(String enteId, String finGroup, String finType) {
        //参数校验
        FastUtils.checkParams(enteId, finGroup, finType);
        List<FinReportConfigVo> configVoList = getConfigVoList(enteId, finGroup, finType);
        //公式配置错误
        if (configVoList == null || configVoList.isEmpty() || StringUtil.isEmpty(configVoList.get(0).getCodes())) {
            throw new ServiceException(ResultCode.FIN_REPORT_CONFIG_ERROR);
        }

        //包含的科目 ,用于查询科目余额信息
        List<String> subjectCodeList = new ArrayList<>();
        FinReportConfigVo reportConfigVo = configVoList.get(0);
        String[] arr = reportConfigVo.getCodes().split("\\,");
        for (String str : arr) {
            subjectCodeList.add(str);
        }
        reportConfigVo.setSubjectCodeList(subjectCodeList);
        //公式字符串
        String formulaStr = reportConfigVo.getCodesType();
        //将价格表达式进行处理后 ,设置在list对象中
        //包含价格表达式元素的List
        List<FormulaVo> formulaVos = getFormula(formulaStr);
        reportConfigVo.setFormulaExpList(formulaVos);

        return reportConfigVo;
    }

    //获取废弃凭证id
    @Override
    @Deprecated
    public List<String> discardVoucherIdList(TransferReportSimpleDto simpleDto) {
        List<FinVoucherVo> discardVouchers = finReportMapper.getDiscardVouchers(simpleDto);
        List<String> voucherIdList = new ArrayList<>();
        if (discardVouchers != null) {
            for (FinVoucherVo finVoucherVo : discardVouchers) {
                voucherIdList.add(finVoucherVo.getVoucherId());
            }
        }
        return voucherIdList;
    }

    //先获取list对象
    private List<FinReportConfigVo> getConfigVoList(String enteId, String finGroup, String finType) {
        FinReportConfig queryDto = new FinReportConfig();
        queryDto.setEnteId(enteId);
        queryDto.setFinGroup(finGroup);
        queryDto.setFinType(finType);
        List<FinReportConfigVo> configVoList = finReportConfigMapper.findConfigByCondition(queryDto);

        return configVoList;
    }

    //处理价格表达式, 返回以科目code为key的 map
    private List<FormulaVo> getFormula(String formulaStr) {
        //6401:debit:-
        List<FormulaVo> formulaVos = new ArrayList<>();
        //处理公式,并将
        String[] elements = formulaStr.split("\\,");
        int len;
        for (int i = 0; i < elements.length; i++) {
            FormulaVo formulaVo = new FormulaVo();
            String[] subArr = elements[i].split("\\:");
            //可能配置了借贷方向,那么长度就为3 ,没配置方向长度为2
            len = subArr.length;
            if (i != elements.length - 1) {
                formulaVo.setSubjectCode(subArr[0]);
                if (len == 3) {
                    formulaVo.setDirection(subArr[1]);
                    formulaVo.setSignOperation(subArr[2]);
                } else if (len == 2) {
                    formulaVo.setSignOperation(subArr[1]);
                }
            }
            //最后一个元素
            else {
                //如果长度为0 是不带方向的
                if (len == 1) {
                    //啥都不做
                }
                //长度为1 是带科目方向的
                if (len == 2) {
                    formulaVo.setDirection(subArr[1]);
                }
                formulaVo.setSubjectCode(subArr[0]);
            }
            formulaVos.add(formulaVo);
        }
        return formulaVos;
    }

    /**
     * @description 取得科目明细汇总数据 并做更新
     * *
     * @author fancl
     * @date 2020/1/10
     * @param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int calcAndRefreshSubjectData(TransferReportSimpleDto simpleDto) {
        String enteId = simpleDto.getEnteId();
        //财务基准配置
        FinReportConfigVo configVo = getConfigByGroupAndType(enteId, simpleDto.getFinGroup(), simpleDto.getFinType());
        //科目列表
        String[] arr = configVo.getCodes().split("\\,");
        List<String> subjectList = new ArrayList<>();
        for (String str : arr) {
            subjectList.add(str);
        }
        //更新
        this.calcAndRefresh(simpleDto, subjectList, null);
        return 1;
    }

    //更新个性化数据
    @Override
    public int calcAndRefreshPersonal(TransferReportSimpleDto simpleDto) {
        //查询配置,查询的是多个,一次性循环更新
        List<FinReportConfigVo> groupList = getConfigByGroup(simpleDto.getEnteId(),
                ReportDataConstant.FinType.PERSONAL_GROUP);
        if (groupList == null || groupList.isEmpty())
            return 0;
        //循环更新数据
        for (FinReportConfigVo configVo : groupList) {
            String finType = configVo.getFinType();
            String codeType = configVo.getCodesType();
            //摘要类型
            if (ReportDataConstant.FinType.ABSTRACT_CONTENT.equals(finType)) {
                String[] typeArr = codeType.split("\\,");
                //每个
                for (String personal : typeArr) {
                    String[] personalArr = personal.split("\\:");
                    String subjectCode = personalArr[0];
                    String content = personalArr[1];
                    simpleDto.setAbstractContent(content);
                    //全匹配
                    simpleDto.setMatchSubjectType(ReportDataConstant.Finance.MATCH_SUBJECT_IS);
                    List<String> accountSubjectList = new ArrayList<>();
                    accountSubjectList.add(subjectCode);
                    //更新个性化科目数据
                    this.calcAndRefresh(simpleDto, accountSubjectList,
                            ReportDataConstant.FinType.ABSTRACT_CONTENT);
                }
            }
        }
        return 0;
    }

    //公共的更新方法  personal==null是为主科目数据更新
    private int calcAndRefresh(TransferReportSimpleDto simpleDto, List<String> subjectList, String personal) {
        logger.info("calcAndRefresh:===================");
        logger.info("personal:" + personal);
        //时间类型: allData/transDay/between/before
        String dayType = getDayType(simpleDto);
        //如果没匹配到正确的参数则抛异常
        if (dayType == null) {
            throw new ServiceException(ResultCode.PARAMS_NOT_RIGHT);
        }
        //凭证汇总数据
        List<FinReportVo> vouchers = finReportMapper.getVouchers(dayType, simpleDto, subjectList);
        //科目发生额汇总数据 (按天/科目/金额维度)
        //主科目数据
        List<FinReportVo> reports = null;
        if (personal == null) {
            reports = finReportMapper.getReports(dayType, simpleDto);
        } else {
            //从个性化表中
            reports = finReportMapper.getPersonalReports(dayType, simpleDto, personal);
        }
        //比较的原则:
        /**
         * 1.查原表数据生成list(表1)
         * 2.查目的表数据list(表2)
         * 3.新建一个用于新增数据的list (addList) 和一个用于修改数据的list(updateList)
         * 4.java代码对比两个list,双层循环 ,在表1中匹配表2的每个元素 (即外层是vouchers,内层是reports)
         *
         *  a.如果在表2没找到的,放到addList里面;
         *  b.如果表2找到了 ,但符合更新条件的(例如金额发生了变化等),放到updateList
         *  c.其他的数据丢弃
         * 5.最后将那两个list 分别执行insert  和update
         */
        //全量,则只需trancate表然后将vouchers数据插入到表中
        if (ReportDataConstant.Finance.ALL_DATA.equals(dayType)) {
            //主科目
            if (personal == null) {
                finReportMapper.truncateReport();
                //因为Postgre对参数数量有限制,将list拆分为多个
                //转换为FinReport
                List<FinReport> addList = new ArrayList<>();
                for (FinReport voucher : vouchers) {
                    addList.add(voucher);
                }
                Map<Integer, List<FinReport>> subMap = FastUtils.splitList(addList, 2000);
                if (personal == null) {
                    for (Integer i : subMap.keySet()) {
                        List<FinReport> subReports = subMap.get(i);
                        finReportMapper.addBatch(subReports);
                    }
                }
            }
            //个性化科目数据
            else {
                finReportMapper.truncatePersonalReport();

                finReportMapper.addPersonalBatch(vouchers, personal);
            }
        }
        //增量更新
        else {
            //用于新增的list
            List<FinReport> addList = new LinkedList<>();
            //用于update的list
            List<FinReport> updateList = new LinkedList<>();
            //匹配记录并放到集合中
            for (FinReport voucher : vouchers) {
                boolean hasThisVoucher = false;
                //需要把从凭证查出来的对象的flag设置为本次使用的 ,因为原始数据中是不包含这个字段的
                voucher.setFlag(personal);
                for (FinReport report : reports) {
                    //当匹配条件相同,表明有这条记录
                    if (report.toString().equals(voucher.toString())) {
                        //置匹配到的标识为true
                        hasThisVoucher = true;
                        //进一步比较金额,不同则放入update,否则丢弃
                        if (report.getAmount() != null && voucher.getAmount() != null
                                && report.getAmount().compareTo(voucher.getAmount()) != 0) {
                            updateList.add(voucher);
                        }
                        //为提高效率,此时可以跳出层循环
                        break;
                    }
                }
                //如果未找到,放入add中
                if (hasThisVoucher == false) {
                    addList.add(voucher);
                }
            }

            //分别执行两个list
            if (!addList.isEmpty()) {
                logger.info("新增的记录数为:" + addList.size());
                //因为Postgre对参数数量有限制,将list拆分为多个
                Map<Integer, List<FinReport>> subMap = FastUtils.splitList(addList, 2000);
                if (personal == null) {
                    for (Integer i : subMap.keySet()) {
                        List<FinReport> subReports = subMap.get(i);
                        finReportMapper.addBatch(subReports);
                    }
                } else {
                    for (Integer i : subMap.keySet()) {
                        List<FinReport> subReports = subMap.get(i);
                        finReportMapper.addPersonalBatch(subReports, personal);
                    }
                }
            }
            //更新
            if (!updateList.isEmpty()) {
                logger.info("更新的记录数为:" + updateList.size());
                if (personal == null) {
                    finReportMapper.updateBatch(updateList);
                } else {
                    finReportMapper.updatePersonalBatch(updateList);
                }
            }
        }
        return 1;
    }


    //处理时间标识
    private String getDayType(TransferReportSimpleDto simpleDtoDto) {
        /***
         * 时间参数说明:
         * 1.(allData)第一次加工全部的数据:时间参数全为null 该过程一般只在初始化时执行
         * 2.(transDay)加工某个时间之后的:transDay有值,beforDay和endDay为null,即:加工业务表中数据>=transDay
         * 3.(between)某个时间段内的:transDay和endDay有值 beforDay为null
         * 4.(before)加工某个时间段之前,beforDay有值,transDay和endDay为null
         */
        String dayType = null;
        //全部数据
        if (StringUtil.isEmpty(simpleDtoDto.getBeforDay())
                && StringUtil.isEmpty(simpleDtoDto.getTransDay())
                && StringUtil.isEmpty(simpleDtoDto.getBeforDay())) {
            dayType = ReportDataConstant.Finance.ALL_DATA;
        }
        //时间点之后的
        else if (!StringUtil.isEmpty(simpleDtoDto.getTransDay())
                && StringUtil.isEmpty(simpleDtoDto.getBeforDay())
                && StringUtil.isEmpty(simpleDtoDto.getEndDay())) {
            dayType = ReportDataConstant.Finance.TRANS_DAY;
        }
        //时间段之内的
        else if (!StringUtil.isEmpty(simpleDtoDto.getTransDay())
                && !StringUtil.isEmpty(simpleDtoDto.getEndDay())
                && StringUtil.isEmpty(simpleDtoDto.getBeforDay())) {
            dayType = ReportDataConstant.Finance.BETWEEN;
        }
        //时间点之前的
        else if (!StringUtil.isEmpty(simpleDtoDto.getBeforDay())
                && StringUtil.isEmpty(simpleDtoDto.getTransDay())
                && StringUtil.isEmpty(simpleDtoDto.getBeforDay())) {
            dayType = ReportDataConstant.Finance.BEFORE;
        }
        return dayType;
    }


    /**
     * @description 获取科目发生额
     * @author fancl
     * @date 2020/1/11
     * @param
     * @return
     */
    @Override
    public List<FinReportVo> getSubjectData(FinQueryDto queryDto) {
        FastUtils.checkParams(queryDto, queryDto.getEnteId(),
                //queryDto.getBeginTime(),
                queryDto.getEndTime()
                //, queryDto.getShopIdList()
        );

        return finReportMapper.getSubjectDataByCondition(queryDto);
    }

    /**
     * @description 门店为维度的金额 根据价格公式计算后生成
     * @author fancl
     * @date 2020/2/19
     * @param finReportListMap: key为门店id, value为门店下的凭证数据列表
     * @param formulaVos: 公式表达式列表
     * @return key为shopId ,value是该门店下根据公式计算后的金额
     */
    @Override
    public Map<String, BigDecimal> getShopAmountByFormula(Map<String, List<FinReportVo>> finReportListMap, List<FormulaVo> formulaVos) {
        //打印公式表达式list
        logger.info(LogConstant.Finance.FORMULA + formulaVos);
        //返回的对象
        Map<String, BigDecimal> shopAmountMap = new LinkedHashMap<>();
        //按shopId的维度去遍历,然后再list中再按照科目code再次分组, 汇总各金额 ,最后用公式中的来算
        if (CollectionUtil.isNotEmpty(finReportListMap)) {
            finReportListMap.forEach((k, v) -> {
                Map<String, List<FinReportVo>> subjectCollect = v.size() == 0 ? null : v.stream().filter(fin -> fin.getAccountSubjectCode() != null).collect(Collectors.groupingBy(FinReportVo::getAccountSubjectCode));
                //遍历公式,在subjetCollect中汇总金额
                // 这个公式是的格式:code:direct:运算符
                //定义个拼接字符串
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < formulaVos.size(); i++) {
                    FormulaVo formulaVo = formulaVos.get(i);
                    //本次科目code对应金额
                    BigDecimal codeAmount = BigDecimal.ZERO;
                    //科目code
                    String subjectCode = formulaVo.getSubjectCode();
                    //包含这个科目code的配置才计算并添加进公式字符串
                    //要再次遍历科目code的map(subjectCollect)
                    for (Map.Entry<String, List<FinReportVo>> entry : subjectCollect.entrySet()) {
                        //如果以这个科目开头,那么就可以累加
                        if (entry.getKey().startsWith(subjectCode)) {
                            codeAmount = codeAmount.add(getSubjectAmount(entry.getValue(), formulaVo.getDirection()));
                        }
                    }
                    //精度
                    codeAmount = codeAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
                    //不是最后一个的才拼接运算符
                    if (i < formulaVos.size() - 1) {
                        sb.append(codeAmount);
                        sb.append(formulaVo.getSignOperation());
                    } else {
                        sb.append(codeAmount);
                    }
                }
                //打印原始公式:
                logger.info(LogConstant.Finance.FORMULA_RESULT + sb.toString());
                //计算
                BigDecimal amount = FastUtils.calcFormulaStr(sb.toString());
                amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                shopAmountMap.put(k, amount);

            });
        }
        return shopAmountMap;
    }

    //遍历reportList合计中的金额
    private BigDecimal getSubjectAmount(List<FinReportVo> reportVos, String direction) {
        BigDecimal amount = BigDecimal.ZERO;
        //累计借方金额
        if (ReportDataConstant.Finance.DEBIT.equals(direction)) {
            for (FinReportVo fin : reportVos) {
                amount = amount.add(fin.getDebitAmount());
            }
            //累计贷方金额
        } else if (ReportDataConstant.Finance.CREDIT.equals(direction)) {
            for (FinReportVo fin : reportVos) {
                amount = amount.add(fin.getCreditAmount());
            }
            //如果没有方向 ,默认计算借方科目
        } else if (direction == null) {
            for (FinReportVo fin : reportVos) {
                amount = amount.add(fin.getDebitAmount());
            }
        }
        return amount;
    }

    /**
     * @description 查询凭证发生数据
     * @author fancl
     * @date 2020/2/21
     * @param dayType:日期类型 simpleDtoDto:包含查询条件的dto subjectList:科目codes
     * @return
     */
    @Override
    public List<FinReportVo> getVouchersByCondition(String dayType, TransferReportSimpleDto simpleDtoDto, List<String> subjectList) {
        List<FinReportVo> vouchers = finReportMapper.getVouchers(dayType, simpleDtoDto, subjectList);
        return vouchers;
    }

    /**
     * @return
     * @description 查询实时利润分析预算
     * @author liBao
     */
    @Override
    public List<RealProfitBudgetVo> getRealProfitBudgetList(FinQueryDto queryDto) {
        return finReportMapper.getRealProfitBudgetList(queryDto);
    }


}

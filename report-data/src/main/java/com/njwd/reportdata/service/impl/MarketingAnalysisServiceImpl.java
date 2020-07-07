package com.njwd.reportdata.service.impl;

import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.FinReportConfig;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.fin.FinReportVo;
import com.njwd.entity.reportdata.vo.fin.FinSubjectInner;
import com.njwd.entity.reportdata.vo.fin.FinSubjectVo;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.reportdata.service.BaseShopService;
import com.njwd.reportdata.service.MarketingAnalysisService;
import com.njwd.service.FileService;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/11/20
 */
@Service
public class MarketingAnalysisServiceImpl implements MarketingAnalysisService {

    @Autowired
    FinanceSubjectService financeSubjectService;

    @Autowired
    BaseShopService baseShopService;

    @Autowired
    private FileService fileService;

    /**
     * 营销费用统计
     *
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinSubjectVo>
     * @Author lj
     * @Date:14:10 2020/1/14
     **/
    @Override
    public List<FinSubjectVo> marketingCost(FinQueryDto queryDto) {
        //查询营销费用统计的配置科目
        FinReportConfig reportConfig = financeSubjectService.getConfigByGroupAndType(queryDto.getEnteId(),ReportDataConstant.FinType.MARKING_COST,ReportDataConstant.FinType.MARKING_COST);
        //获取科目列表
        String codes = reportConfig.getCodes();
        List<String> codeList =Arrays.asList(codes.split(","));
        String codesTypes = reportConfig.getCodesType();
        List<String> codeTypeList =Arrays.asList(codesTypes.split(","));
        //key codetype类型,科目编码集合
        Map<String,List<String>> codeTypeMap = new HashMap<>();
        for(int i=0;i<codeTypeList.size();i++){
            if(codeTypeMap.containsKey(codeTypeList.get(i))){
                codeTypeMap.get(codeTypeList.get(i)).add(codeList.get(i));
            }else {
                List<String> code = new ArrayList<>();
                code.add(codeList.get(i));
                codeTypeMap.put(codeTypeList.get(i),code);
            }
        }
        queryDto.setSubjectCodeList(codeList);
        List<FinReportVo> shopSubjects = financeSubjectService.getSubjectData(queryDto);
        List<FinSubjectVo> finList = new ArrayList<>();
        List<String> costCodes= codeTypeMap.get(ReportDataConstant.CodeType.COST);
        //过滤没有成本的数据
        Boolean flag = null;
        for(FinReportVo v : shopSubjects){
            flag = filterCode(costCodes,v.getAccountSubjectCode());
            if(flag){
                break;
            }
        }
        if (!shopSubjects.isEmpty()&&flag) {
            //只留下金额不为null同时大于0的
            List<FinReportVo> listAll = shopSubjects.stream().filter(subject -> subject.getAmount() != null && subject.getAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

            //门店 科目维度行
            //现在数据仍是门店 / 科目 维度
            //按门店分组
            Map<String, List<FinReportVo>> shopMap = listAll.stream().collect(Collectors.groupingBy(FinReportVo::getShopId));
            //按品牌分组
            Map<String, List<FinReportVo>> brandMap = listAll.stream().collect(Collectors.groupingBy(FinReportVo::getBrandId));
            //按区域分组
            Map<String, List<FinReportVo>> regionMap = listAll.stream().collect(Collectors.groupingBy(FinReportVo::getRegionId));

            //原始list中一共包含多少个科目,用于后续判断
            Map<String, FinReportVo> subjectIdMap = new LinkedHashMap<>();

            listAll.stream().filter(subject ->filterCode(costCodes,subject.getAccountSubjectCode())).forEach(vo -> subjectIdMap.computeIfAbsent(vo.getAccountSubjectId(), k -> vo));

            List<FinSubjectVo> shopSubjectList = generateListByType(codeTypeMap,shopMap, subjectIdMap, ReportDataConstant.Finance.TYPE_SHOP);

            List<FinSubjectVo> brandSubjectList = generateListByType(codeTypeMap,brandMap, subjectIdMap, ReportDataConstant.Finance.TYPE_BRAND);

            List<FinSubjectVo> regionSubjectList = generateListByType(codeTypeMap,regionMap, subjectIdMap, ReportDataConstant.Finance.TYPE_REGION);

            finList.addAll(shopSubjectList);
            finList.addAll(brandSubjectList);
            finList.addAll(regionSubjectList);
            //全部数据
            FinSubjectVo all = getFinAll(shopSubjectList);
            finList.add(all);
        }
        return finList;
    }

    /**
     * 导出营销费用统计
     *
     * @param excelExportDto
     * @param response
     * @return void
     * @Author lj
     * @Date:14:43 2020/3/6
     **/
    @Override
    public void exportMarketingCost(ExcelExportDto excelExportDto, HttpServletResponse response) {
        //把字符串日期赋值给日期字段
        excelExportDto.setBeginDate(DateUtils.parseDate(excelExportDto.getBeginTime(), DateUtils.PATTERN_DAY));
        excelExportDto.setEndDate(DateUtils.parseDate(excelExportDto.getEndTime(), DateUtils.PATTERN_DAY));
        FinQueryDto finQueryDto = new FinQueryDto();
        FastUtils.copyProperties(excelExportDto,finQueryDto);
        List<FinSubjectVo> finSubjectVoList = marketingCost(finQueryDto);
        List<FinSubjectVo> marketingCostList;

        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(finQueryDto.getType())) {
            marketingCostList = finSubjectVoList.stream().filter(info -> (info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)
                    || info.getType().equals(ReportDataConstant.Finance.TYPE_ALL))
            ).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(finQueryDto.getType())) {
            marketingCostList = finSubjectVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(finQueryDto.getType())) {
            marketingCostList = finSubjectVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        } else {
            marketingCostList = finSubjectVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }

        fileService.exportMarketingCost(response, excelExportDto, marketingCostList,true);
    }

    private FinSubjectVo getFinAll(List<FinSubjectVo> shopSubjectList) {
        FinSubjectVo finTypeIsAll = new FinSubjectVo();
        finTypeIsAll.setType(ReportDataConstant.Finance.ALL_SUBJECT);
        finTypeIsAll.setBrandId(ReportDataConstant.Finance.EMPTY_STRING);
        finTypeIsAll.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        finTypeIsAll.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
        finTypeIsAll.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        finTypeIsAll.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
        finTypeIsAll.setShopName(ReportDataConstant.Finance.ALL_SHOP);

        //总收入金额
        final BigDecimal[] allIncomeAmount = {BigDecimal.ZERO};
        //总金额
        final BigDecimal[] allAmount = {BigDecimal.ZERO};
        //各科目合计金额
        LinkedHashMap<String, FinSubjectInner> subjectMap = new LinkedHashMap<>();
        shopSubjectList.stream().forEach(subject -> {
            allIncomeAmount[0] = allIncomeAmount[0].add(subject.getAmount());
            allAmount[0] = allAmount[0].add(subject.getAllAmount());
            subject.getSubjectMap().forEach((k, v) -> {
                FinSubjectInner init =new FinSubjectInner();
                FastUtils.copyProperties(v,init);
                init.setAmount(BigDecimal.ZERO);
                //如有没有就直接取科目对象 ,否则进行累加
                subjectMap.computeIfAbsent(k, key -> init).setAmount(subjectMap.get(k).getAmount().add(v.getAmount()));
                //计算占比
                subjectMap.computeIfAbsent(k, key -> init).setRatio(BigDecimalUtils.getPercent(subjectMap.get(k).getAmount(),allIncomeAmount[0]));
            });
        });
        //设置总金额
        finTypeIsAll.setAllAmount(allAmount[0]);
        finTypeIsAll.setAllPercent(BigDecimalUtils.getPercent(allAmount[0],allIncomeAmount[0]));
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
    private  List<FinSubjectVo> generateListByType(Map<String,List<String>> codeTypeMap,Map<String, List<FinReportVo>> dimension,Map<String, FinReportVo> subjectIdMap,String type){
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
            if(ReportDataConstant.Finance.TYPE_SHOP.equals(type)){
                regionId[0] = vo.getRegionId();
                regionName[0] = vo.getRegionName();
                shopId[0] = vo.getShopId();
                shopName[0] = vo.getShopName();
            }
            //按品牌维度
            else if(ReportDataConstant.Finance.TYPE_BRAND.equals(type)){
                regionId[0] = ReportDataConstant.Finance.EMPTY_STRING;
                regionName[0] = ReportDataConstant.Finance.ALL_REGION;
                shopId[0] = ReportDataConstant.Finance.EMPTY_STRING;
                shopName[0] = ReportDataConstant.Finance.ALL_SHOP;
            }
            //按区域维度
            else if(ReportDataConstant.Finance.TYPE_REGION.equals(type)){
                regionId[0] = vo.getRegionId();
                regionName[0] = vo.getRegionName();
                shopId[0] = ReportDataConstant.Finance.EMPTY_STRING;
                shopName[0] = ReportDataConstant.Finance.ALL_SHOP;
            }
            subjectVo.setRegionId(regionId[0]);
            subjectVo.setRegionName(regionName[0]);
            subjectVo.setShopId(shopId[0]);
            subjectVo.setShopName(shopName[0]);
            //收入金额
            List<String> incomeCodes= codeTypeMap.get(ReportDataConstant.CodeType.INCOME);
            List<String> costCodes= codeTypeMap.get(ReportDataConstant.CodeType.COST);
            final BigDecimal[] allAmount = {BigDecimal.ZERO};
            v.stream().filter(subject ->filterCode(incomeCodes,subject.getAccountSubjectCode())).forEach(finReportVo -> allAmount[0] = allAmount[0].add(finReportVo.getCreditAmount()));
            final BigDecimal[] allCostAmount = {BigDecimal.ZERO};
            v.stream().filter(subject ->filterCode(costCodes,subject.getAccountSubjectCode())).forEach(finReportVo -> allCostAmount[0] = allCostAmount[0].add(finReportVo.getDebitAmount()));
            subjectVo.setAllAmount(allCostAmount[0]);
            subjectVo.setAmount(allAmount[0]);
            subjectVo.setAllPercent(BigDecimalUtils.getPercent(allCostAmount[0],allAmount[0]));

            LinkedHashMap<String, FinSubjectInner> inner = new LinkedHashMap<>();
            //把门店Key包含的list,按照科目id汇总,每个map<key List<FinReportVo>> 中的金额合计就是该科目的金额
            Map<String, List<FinReportVo>> collectBySubject = v.stream().filter(subject ->filterCode(costCodes,subject.getAccountSubjectCode())).collect(Collectors.groupingBy(FinReportVo::getAccountSubjectId));
            //遍历全部科目id,目的是生成包含全部subjectId key 的map数据
            subjectIdMap.forEach((k1, v1) -> {
                //按科目id顺序,如果包含则计算其中的金额合计,否则金额为0
                if (collectBySubject.containsKey(k1)) {
                    //把collectBySubject中对应list的金额相加
                    final BigDecimal[] amount = {BigDecimal.ZERO};
                    collectBySubject.get(k1).stream().forEach(rep -> {
                        amount[0] = amount[0].add(rep.getAmount() == null ? BigDecimal.ZERO : rep.getDebitAmount());
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
     * 校验版本是否匹配
     * @Author lj
     * @Date:10:05 2020/1/15
     * @param codes, code
     * @return java.lang.Boolean
     **/
    private Boolean filterCode(List<String> codes,String code){
        Boolean flag=false;
        for(String temp:codes){
            if(code.startsWith(temp)){
                flag=true;
            }
        }
        return flag;
    }

}

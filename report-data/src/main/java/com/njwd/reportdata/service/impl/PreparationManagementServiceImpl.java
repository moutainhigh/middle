package com.njwd.reportdata.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.util.CollectionUtils;
import com.njwd.basedata.service.BaseDeskService;
import com.njwd.basedata.service.BaseReportItemSetService;
import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.dto.BaseDeskDto;
import com.njwd.entity.basedata.dto.BaseReportItemSetDto;
import com.njwd.entity.basedata.vo.BaseDeskVo;
import com.njwd.entity.basedata.vo.BaseReportItemSetVo;
import com.njwd.entity.reportdata.BaseYearMomThreshold;
import com.njwd.entity.reportdata.FinReportConfig;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.dto.querydto.PreparationAnalysisDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.entity.reportdata.vo.PreparationAnalysisVo;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;
import com.njwd.entity.reportdata.vo.fin.FinReportVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.reportdata.mapper.BusinessAnalysisMapper;
import com.njwd.reportdata.mapper.DeskTypeAnalysisMapper;
import com.njwd.reportdata.mapper.SaleAnalysisMapper;
import com.njwd.reportdata.service.*;
import com.njwd.service.FileService;
import com.njwd.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @ClassName PreparationManagementServiceImpl
 * @Description 租金占销实现类
 * @Author liBao
 * @Date 2020/2/7 12:27
 */
@Service
public class PreparationManagementServiceImpl implements PreparationManagementService {

    @Autowired
    FinanceSubjectService financeSubjectService;

    @Autowired
    private BusinessAnalysisMapper businessAnalysisMapper;

    @Autowired
    private BaseReportItemSetService baseReportItemSetService;

    @Autowired
    private RepPosDetailPayService repPosDetailPayService;

    @Autowired
    private RepPosDetailFoodService repPosDetailFoodService;

    @Autowired
    private BaseDeskService baseDeskService;

    @Autowired
    private SaleAnalysisMapper saleAnalysisMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private MemberAnalysisService memberAnalysisService;

    @Autowired
    private RepCrmTurnoverService repCrmTurnoverService;

    @Autowired
    private DeskTypeAnalysisMapper deskTypeAnalysisMapper;

    @Autowired
    private BaseYearMomThresholdService baseYearMomThresholdService;

    @Resource
    private BaseShopService baseShopService;

    /**
     * @param queryDto
     * @return list
     * @description 租金占销
     * @author liBao
     * @date 2020/2/7
     */
    @Override
    public List<FinRentAccountedForVo> findRentAccountedFor(FinQueryDto queryDto) {
        //获取科目配置数据
        List<String> subjectCodes = getSubjectCodesConfig(queryDto.getEnteId(), ReportDataConstant.FinType.RENT_ACCOUNTED);
        queryDto.setSubjectCodeList(subjectCodes);
        List<FinReportVo> shopSubjects = financeSubjectService.getSubjectData(queryDto);
        if (CollectionUtils.isEmpty(shopSubjects)) {
            return null;
        }
        //过滤掉物业科目数据
        List<FinReportVo> listRent = shopSubjects.stream().filter(subject -> subject.getAccountSubjectCode().equals(ReportDataConstant.SubjectCode.RENT_SUBJECT_CODE)).collect(Collectors.toList());
        //过滤掉租金科目数据
        List<FinReportVo> listProperty = shopSubjects.stream().filter(subject -> subject.getAccountSubjectCode().equals(ReportDataConstant.SubjectCode.PROPERTY_SUBJECT_CODE)).collect(Collectors.toList());
        //合并租金和物业信息
        if (!CollectionUtil.isEmpty(listRent) && !CollectionUtils.isEmpty(listProperty)) {
            MergeUtil.merge(listRent, listProperty,
                    FinReportVo::getShopId, FinReportVo::getShopId,
                    (listVo1, listVo2) -> {
                        listVo1.setProperty(listVo2.getDebitAmount());
                        BigDecimal total = listVo1.getDebitAmount().add(listVo2.getDebitAmount());
                        listVo1.setTotal(total);
                    });
        }
        //将科目类型数据转换为租金类型  rentListAll为成本类型的list
        List<FinRentAccountedForVo> rentListAll = new ArrayList<>();
        if (CollectionUtils.isEmpty(listRent)) {
            return null;
        }
        fillCost(listRent, rentListAll);
        //获取销售额数据
        //List<FinRentAccountedForVo> saleList = repPosDetailPayService.findRepPosPayVoInfoByCondition(queryDto);
        List<FinRentAccountedForVo> saleList  = baseDeskService.findSaleByCondition(queryDto);
        //合并销售额和租金信息
        if (!CollectionUtils.isEmpty(saleList)) {
            MergeUtil.merge(rentListAll, saleList,
                    FinRentAccountedForVo::getShopId, FinRentAccountedForVo::getShopId,
                    (rent, sale) -> {
                        rent.setSalesVolume(sale.getSalesVolume().add(sale.getBusinessSurcharges()));
                    });
        }

        //按门店分组
        Map<String, List<FinRentAccountedForVo>> shopMap = rentListAll.stream().filter(shop -> shop.getShopId() != null).collect(Collectors.groupingBy(FinRentAccountedForVo::getShopId));
        //生成门店维度的成本数据
        List<FinRentAccountedForVo> shopRentList = getRentCompare(shopMap, ReportDataConstant.Finance.TYPE_SHOP);

        //按品牌分组
        Map<String, List<FinRentAccountedForVo>> brandMap = shopRentList.stream().filter(shop -> shop.getBrandId() != null).collect(Collectors.groupingBy(FinRentAccountedForVo::getBrandId));

        //按区域分组
        Map<String, List<FinRentAccountedForVo>> regionMap = shopRentList.stream().filter(region -> region.getRegionId() != null).collect(Collectors.groupingBy(FinRentAccountedForVo::getRegionId));

        //品牌维度
        List<FinRentAccountedForVo> costBrandList = getRentCompare(brandMap, ReportDataConstant.Finance.TYPE_BRAND);

        //区域维度数据
        List<FinRentAccountedForVo> costRegionList = getRentCompare(regionMap, ReportDataConstant.Finance.TYPE_REGION);

        //生成一条所有数据的统计，类型为all
        FinRentAccountedForVo fin = fillAllRent(shopRentList);

        //结果集
        List<FinRentAccountedForVo> finList = new LinkedList<>();
        //门店数据排序
        List<FinRentAccountedForVo> rentShopListSorted = shopRentList.stream()
                .filter(shop -> shop.getBrandId() != null && shop.getRegionId() != null && shop.getShopId() != null)
                .sorted(Comparator.comparing(FinRentAccountedForVo::getBrandId).
                        thenComparing(FinRentAccountedForVo::getRegionId).
                        thenComparing(FinRentAccountedForVo::getShopId))
                .collect(Collectors.toList());
        //将门店维度数据加入结果集
        finList.addAll(rentShopListSorted);

        //品牌数据排序
        List<FinRentAccountedForVo> rentBrandListSorted = costBrandList.stream().filter(brand -> brand.getBrandId() != null)
                .sorted(Comparator.comparing(FinRentAccountedForVo::getBrandId)
                ).collect(Collectors.toList());

        finList.addAll(rentBrandListSorted);
        //区域数据排序
        List<FinRentAccountedForVo> rentRegionListSorted = costRegionList.stream()
                .filter(region -> region.getBrandId() != null && region.getRegionId() != null)
                .sorted(Comparator.comparing(FinRentAccountedForVo::getBrandId)
                        .thenComparing(FinRentAccountedForVo::getRegionId)).collect(Collectors.toList());
        //填充区域
        finList.addAll(rentRegionListSorted);

        //以门店数据为基准汇总出一条all类型的数据
        FinRentAccountedForVo finTypeIsAll = getFinCostTypeIsAll(fin);
        //把这条汇总数据加进来
        finList.add(finTypeIsAll);

        return finList;
    }

    /**
     * 获取科目配置数据
     * 私有方法
     */
    private List<String> getSubjectCodesConfig(String enteId, String finType) {
        //获取报表配置数据,得到科目配置
        FinReportConfig reportConfig = financeSubjectService.getFinReportConfig(enteId, finType);
        if (reportConfig == null || reportConfig.getCodes() == null || StringUtil.isEmpty(reportConfig.getCodes())) {
            //抛出异常
            throw new ServiceException(ResultCode.SUBJECT_CONFIG_ERROR);
        }
        //将配置的科目数据放入参数
        List<String> subjectCodeList = new ArrayList<>();
        String[] arr = reportConfig.getCodes().split("\\,");
        for (String str : arr) {
            subjectCodeList.add(str);
        }
        return subjectCodeList;
    }

    /**
     * 生成shop维度的成本对比数据
     */
    private void fillCost(List<FinReportVo> listAll, List<FinRentAccountedForVo> rentShopList) {
        for (FinReportVo reportVo : listAll) {
            FinRentAccountedForVo restVo = new FinRentAccountedForVo();
            restVo.setBrandId(reportVo.getBrandId());
            restVo.setBrandName(reportVo.getBrandName());
            restVo.setRegionId(reportVo.getRegionId());
            restVo.setRegionName(reportVo.getRegionName());
            restVo.setShopId(reportVo.getShopId());
            restVo.setShopName(reportVo.getShopName());
            restVo.setRent(reportVo.getDebitAmount());
            restVo.setProperty(reportVo.getProperty());
            restVo.setTotal(reportVo.getTotal());
            rentShopList.add(restVo);
        }
    }

    /**
     * 生成各维度租金数据
     * type: shop brand region
     */
    private List<FinRentAccountedForVo> getRentCompare(Map<String, List<FinRentAccountedForVo>> dimension, String type) {
        List<FinRentAccountedForVo> rentList = new LinkedList<>();
        //遍历Map中的数据
        dimension.forEach((k, v) -> {
            //第一步 将门店维度信息赋值给成本对象
            FinRentAccountedForVo rentCompareVo = new FinRentAccountedForVo();
            rentCompareVo.setType(type);
            rentCompareVo.setBrandId(v.get(0).getBrandId());
            rentCompareVo.setBrandName(v.get(0).getBrandName());
            //表头信息
            if (ReportDataConstant.Finance.TYPE_SHOP.equals(type)) {
                rentCompareVo.setRegionId(v.get(0).getRegionId());
                rentCompareVo.setRegionName(v.get(0).getRegionName());
                rentCompareVo.setShopId(v.get(0).getShopId());
                rentCompareVo.setShopName(v.get(0).getShopName());
//                rentCompareVo.setRent(v.get(0).getRent());
//                rentCompareVo.setProperty(v.get(0).getProperty());
//                rentCompareVo.setTotal(v.get(0).getTotal());
            }
            //品牌维度,把区域和门店属性设置为全部
            else if (ReportDataConstant.Finance.TYPE_BRAND.equals(type)) {
                rentCompareVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
                rentCompareVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                rentCompareVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                rentCompareVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //区域维度,把门店属性设置为全部
            else if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                rentCompareVo.setRegionId(v.get(0).getRegionId());
                rentCompareVo.setRegionName(v.get(0).getRegionName());
                rentCompareVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                rentCompareVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }

            //按不同维度合计多个科目租金
            final BigDecimal[] rent = {BigDecimal.ZERO};
            //按不同维度合计多个科目物业
            final BigDecimal[] property = {BigDecimal.ZERO};
            //按不同维度合计多个科目的合计
            final BigDecimal[] total = {BigDecimal.ZERO};

            //销售金额
            final BigDecimal[] sale = {BigDecimal.ZERO};

            v.stream().forEach(re -> {
                //合计成本
                rent[0] = rent[0].add(re.getRent() == null ? new BigDecimal(0) : re.getRent());
                property[0] = property[0].add(re.getProperty() == null ? new BigDecimal(0) : re.getProperty());
                total[0] = total[0].add(re.getTotal() == null ? new BigDecimal(0) : re.getTotal());
                sale[0] = sale[0].add(re.getSalesVolume() == null ? new BigDecimal(0) : re.getSalesVolume());
            });
            //成本
            rentCompareVo.setRent(rent[0]);
            rentCompareVo.setProperty(property[0]);
            rentCompareVo.setTotal(total[0]);
            rentCompareVo.setSalesVolume(sale[0]);
            //填充租金占销比
            BigDecimal rentPercent = BigDecimalUtils.getPercent(rentCompareVo.getRent(), rentCompareVo.getSalesVolume());
            rentCompareVo.setRentSalesRatio(rentPercent);
            //填充物业占销比
            BigDecimal propertyPercent = BigDecimalUtils.getPercent(rentCompareVo.getProperty(), rentCompareVo.getSalesVolume());
            rentCompareVo.setPropertySalesRatio(propertyPercent);
            //填充合计占销比
            BigDecimal totalPercent = BigDecimalUtils.getPercent(rentCompareVo.getTotal(), rentCompareVo.getSalesVolume());
            rentCompareVo.setTotalSalesRatio(totalPercent);
//            DecimalFormat df =new DecimalFormat("0.00%");
//            rentCompareVo.getRent().
//            df.format((rentCompareVo.getRent()/rentCompareVo.getSalesVolume()))
//
//            df.format(rentCompareVo.getRent()*100/rentCompareVo.getSalesVolume())+"%";
//            rentCompareVo.setRentSalesRatio();

            rentList.add(rentCompareVo);
        });
        return rentList;
    }

    //填充租金统计
    private FinRentAccountedForVo fillAllRent(List<FinRentAccountedForVo> rentList) {
        FinRentAccountedForVo allRent = new FinRentAccountedForVo();
        BigDecimal rent = BigDecimal.ZERO;
        BigDecimal property = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        //统计一条为all的所有销售额数据
        FinRentAccountedForVo finRentAccountedForVo = fillAllsale(rentList);
        //把门店维度数据中的 区域平均成本 差异率 赋值
        for (FinRentAccountedForVo rentAccountedForVo : rentList) {

            //累计租金
            rent = rent.add(rentAccountedForVo.getRent() == null ? BigDecimal.ZERO : rentAccountedForVo.getRent());
            property = property.add(rentAccountedForVo.getProperty() == null ? BigDecimal.ZERO : rentAccountedForVo.getProperty());
            total = total.add(rentAccountedForVo.getTotal() == null ? BigDecimal.ZERO : rentAccountedForVo.getTotal());

        }
        allRent.setRent(rent);
        allRent.setProperty(property);
        allRent.setTotal(total);
        if (finRentAccountedForVo != null) {
            allRent.setSalesVolume(finRentAccountedForVo.getSalesVolume());
            BigDecimal rentPercent = BigDecimalUtils.getPercent(allRent.getRent(), finRentAccountedForVo.getSalesVolume());
            allRent.setRentSalesRatio(rentPercent);
            //填充物业占销比
            BigDecimal propertyPercent = BigDecimalUtils.getPercent(allRent.getProperty(), finRentAccountedForVo.getSalesVolume());
            allRent.setPropertySalesRatio(propertyPercent);
            //填充合计占销比
            BigDecimal totalPercent = BigDecimalUtils.getPercent(allRent.getTotal(), finRentAccountedForVo.getSalesVolume());
            allRent.setTotalSalesRatio(totalPercent);
        }
        return allRent;
    }

    //计算所有门店支付金额
    private FinRentAccountedForVo fillAllsale(List<FinRentAccountedForVo> rentList) {
        FinRentAccountedForVo allsale = new FinRentAccountedForVo();
        BigDecimal sale = BigDecimal.ZERO;
        //遍历信息，累加支付金额
        for (FinRentAccountedForVo rentAccountedForVo : rentList) {
            //累计租金
            sale = sale.add(rentAccountedForVo.getSalesVolume() == null ? BigDecimal.ZERO : rentAccountedForVo.getSalesVolume());
        }
        allsale.setSalesVolume(sale);
        return allsale;
    }


    //生成all的一条合计数据
    private FinRentAccountedForVo getFinCostTypeIsAll(FinRentAccountedForVo fin) {
        FinRentAccountedForVo finCostTypeIsAll = new FinRentAccountedForVo();
        finCostTypeIsAll.setType(ReportDataConstant.Finance.ALL_SUBJECT);
        finCostTypeIsAll.setBrandId(ReportDataConstant.Finance.EMPTY_STRING);
        finCostTypeIsAll.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        finCostTypeIsAll.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
        finCostTypeIsAll.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        finCostTypeIsAll.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
        finCostTypeIsAll.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        finCostTypeIsAll.setRent(fin.getRent());
        finCostTypeIsAll.setProperty(fin.getProperty());
        finCostTypeIsAll.setTotal(fin.getTotal());
        finCostTypeIsAll.setSalesVolume(fin.getSalesVolume());
        finCostTypeIsAll.setRentSalesRatio(fin.getRentSalesRatio());
        finCostTypeIsAll.setPropertySalesRatio(fin.getPropertySalesRatio());
        finCostTypeIsAll.setTotalSalesRatio(fin.getTotalSalesRatio());
        return finCostTypeIsAll;
    }

    /**
     * @Description: 导出筹建经营分析表
     * @Param: [queryDto]
     * @return: void
     * @Author: 周鹏
     * @Date: 2020/2/11 11:37
     */
    @Override
    public void exportPreparationAnalysis(List<BusinessReportDayItemVo> list, ExcelExportDto excelExportDto, HttpServletResponse response) {
        fileService.exportExcelForQueryTerm(response, excelExportDto, list,
                ExcelColumnConstant.PreparationAnalysis.ITEM_NUMBER,
                ExcelColumnConstant.PreparationAnalysis.ITEM_NAME,
                ExcelColumnConstant.PreparationAnalysis.CURRENT_MONEY,
                ExcelColumnConstant.PreparationAnalysis.LAST_PERIOD,
                ExcelColumnConstant.PreparationAnalysis.MONTH_COMPARE,
                ExcelColumnConstant.PreparationAnalysis.LAST_YEAR,
                ExcelColumnConstant.PreparationAnalysis.YEAR_COMPARE);
    }

    /**
     * @Description: 导出筹租金占销分析
     * @Param: [queryDto]
     * @return: void
     * @Author: liBao
     * @Date: 2020/2/11 11:37
     */
    @Override
    public void exportExcel(ExcelExportDto excelExportDto, HttpServletResponse response) {
        FinQueryDto finQueryDto = new  FinQueryDto();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try{
            excelExportDto.setBeginDate(s.parse(excelExportDto.getBeginTime()));
            excelExportDto.setEndDate(s.parse(excelExportDto.getEndTime()));
        }catch (Exception e){e.printStackTrace();}
        FastUtils.copyProperties(excelExportDto,finQueryDto);
        List<FinRentAccountedForVo> list = findRentAccountedFor(finQueryDto);
        List<FinRentAccountedForVo> listRent = new ArrayList<>();
        if(list != null && list.size()>0){
            //根据类型过滤
            if (ReportDataConstant.Finance.TYPE_SHOP.equals(finQueryDto.getType())) {
                listRent = list.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(finQueryDto.getType())) {
                listRent = list.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_REGION.equals(finQueryDto.getType())) {
                listRent = list.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
            } else {
                listRent = list.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
            }
        }

        fileService.exportExcelForQueryTerm(response, excelExportDto,listRent,
                ExcelColumnConstant.RentSaleInfo.BRAND_NAME,
                ExcelColumnConstant.RentSaleInfo.REGION_NAME,
                ExcelColumnConstant.RentSaleInfo.SHOP_NAME,
                ExcelColumnConstant.RentSaleInfo.RENT,
                ExcelColumnConstant.RentSaleInfo.PROPERTY,
                ExcelColumnConstant.RentSaleInfo.TOTAL,
                ExcelColumnConstant.RentSaleInfo.SALESVOLUME,
                ExcelColumnConstant.RentSaleInfo.RENT_SALE_RATIO,
                ExcelColumnConstant.RentSaleInfo.PROPERTY_SALES_RATIO,
                ExcelColumnConstant.RentSaleInfo.TOTAL_SALES_RATIO);

    }

}

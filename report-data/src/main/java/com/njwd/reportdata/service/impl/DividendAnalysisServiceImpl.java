package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.ShareholderDividendDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.NetProfitVo;
import com.njwd.entity.reportdata.vo.ShareholderDividendVo;
import com.njwd.report.mapper.FinBalanceSubjectMapper;
import com.njwd.report.mapper.FinReportTableMapper;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.reportdata.service.DividendAnalysisService;
import com.njwd.service.FileService;
import com.njwd.utils.FastUtils;
import com.njwd.utils.MergeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author lj
 * @Description 分红分析
 * @Date:15:05 2020/2/29
 **/
@Service
public class DividendAnalysisServiceImpl implements DividendAnalysisService {

    @Autowired
    private FinBalanceSubjectMapper finBalanceSubjectMapper;

    @Autowired
    private FinReportTableMapper finReportTableMapper;

    @Autowired
    FinanceSubjectService financeSubjectService;

    @Autowired
    private FileService fileService;

    /**
     * 股东分红表
     *
     * @param shareholderDividendDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.ShareholderDividendVo>
     * @Author lj
     * @Date:15:11 2020/2/29
     **/
    @Override
    public List<ShareholderDividendVo> findShareholderDividend(ShareholderDividendDto shareholderDividendDto) {
        List<ShareholderDividendVo> list = getShareholderDividendVos(shareholderDividendDto);
        //合并结果
        List<ShareholderDividendVo> finList = new ArrayList<>();
        if (!list.isEmpty()) {
            mergeListData(shareholderDividendDto, list);
            //以门店分组
            Map<String, List<ShareholderDividendVo>> shopMap = list.stream().collect(Collectors.groupingBy(ShareholderDividendVo::getShopId));

            List<ShareholderDividendVo> userList = new ArrayList<>();
            //计算每个门店股东的可分利润
            for (Map.Entry<String, List<ShareholderDividendVo>> entry : shopMap.entrySet()) {
                List<ShareholderDividendVo> v = entry.getValue();
                //根据股东比例排序
                v.sort(Comparator.comparing(ShareholderDividendVo::getPercent).reversed());
                //循环计算百分比
                int i = 1;
                BigDecimal lastCutProfit = null;
                for (ShareholderDividendVo shareholderDividendVo : v) {
                    shareholderDividendVo.setType(ReportDataConstant.Finance.TYPE_SHOP);
                    shareholderDividendVo.setLineNum(i);
                    shareholderDividendVo.setNetProfit(shareholderDividendVo.getNetProfit()==null?Constant.Number.ZEROB:shareholderDividendVo.getNetProfit());
                    shareholderDividendVo.setPercent(shareholderDividendVo.getPercent().divide(Constant.Number.ONE_B, 2, RoundingMode.HALF_UP));
                    shareholderDividendVo.setCutProfit(shareholderDividendVo.getNetProfit().multiply(shareholderDividendVo.getPercent())
                            .divide(Constant.Number.HUNDRED, 2, RoundingMode.HALF_UP));
                    if(i==Constant.Number.ONE){
                        lastCutProfit=shareholderDividendVo.getNetProfit();
                    }
                    i++;
                    //计算尾差
                    if(i==v.size()+Constant.Number.ONE){
                        shareholderDividendVo.setCutProfit(lastCutProfit);
                    }else {
                        lastCutProfit=lastCutProfit.subtract(shareholderDividendVo.getCutProfit());
                    }
                }
                userList.addAll(v);
            }
            //以品牌分组
            Map<String, List<ShareholderDividendVo>> brandMap = list.stream().collect(Collectors.groupingBy(ShareholderDividendVo::getBrandId));
            //以区域分组
            Map<String, List<ShareholderDividendVo>> regionMap = list.stream().collect(Collectors.groupingBy(ShareholderDividendVo::getRegionId));

            //股东数据排序
            List<ShareholderDividendVo> userListSorted = userList.stream()
                    .sorted(Comparator.comparing(ShareholderDividendVo::getBrandId)
                            .thenComparing(ShareholderDividendVo::getRegionId)
                            .thenComparing(ShareholderDividendVo::getShopId)
                            .thenComparing(ShareholderDividendVo::getLineNum)).collect(Collectors.toList());

            //品牌数据排序
            List<ShareholderDividendVo> brandList = generateShareholderDividend(brandMap, ReportDataConstant.Finance.TYPE_BRAND);
            List<ShareholderDividendVo> brandListSorted = brandList.stream()
                    .sorted(Comparator.comparing(ShareholderDividendVo::getBrandId)).collect(Collectors.toList());
            //区域数据排序
            List<ShareholderDividendVo> regionList = generateShareholderDividend(regionMap, ReportDataConstant.Finance.TYPE_REGION);
            List<ShareholderDividendVo> regionListSorted = regionList.stream()
                    .sorted(Comparator.comparing(ShareholderDividendVo::getBrandId)
                            .thenComparing(ShareholderDividendVo::getRegionId)).collect(Collectors.toList());

            //合并结果
            finList.addAll(userListSorted);
//            finList.addAll(shopListSorted);
            finList.addAll(brandListSorted);
            finList.addAll(regionListSorted);

            //全部数据合计
            ShareholderDividendVo finTypeAll = getFinTypeAll(userListSorted,null);
            finList.add(finTypeAll);
        }
        return finList;
    }

    private void mergeListData(ShareholderDividendDto shareholderDividendDto, List<ShareholderDividendVo> list) {
        shareholderDividendDto.setReportId(ReportDataConstant.ReportItemReportId.PROFIT_REPORT);
        List<NetProfitVo> netProfitVoList = finReportTableMapper.findNetProfit(shareholderDividendDto);
        MergeUtil.merge(list, netProfitVoList, ShareholderDividendVo::getShopId, NetProfitVo::getShopId,
                (dividendVo, netProfitVo) -> {
                    dividendVo.setNetProfit(netProfitVo.getNetProfit());
                });
    }

    private List<ShareholderDividendVo> getShareholderDividendVos(ShareholderDividendDto shareholderDividendDto) {
        //处理期间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        Integer time = Integer.valueOf(simpleDateFormat.format(shareholderDividendDto.getBeginDate()));
        shareholderDividendDto.setPeriodYearNum(time);
        //查询股东分红表基础数据
        return finBalanceSubjectMapper.findShareholderInfo(shareholderDividendDto)
                .stream().filter(data -> data.getShopId() != null).collect(Collectors.toList());
    }

    /**
     * 导出股东分红表
     *
     * @param excelExportDto
     * @param response
     * @return void
     * @Author lj
     * @Date:16:43 2020/3/12
     **/
    @Override
    public void exportShareholderDividend(ExcelExportDto excelExportDto, HttpServletResponse response) {
        ShareholderDividendDto shareholderDividendDto = new ShareholderDividendDto();
        FastUtils.copyProperties(excelExportDto, shareholderDividendDto);
        List<ShareholderDividendVo> shareholderDividendVoList = findShareholderDividend(shareholderDividendDto);
        List<ShareholderDividendVo> list;

        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(shareholderDividendDto.getType())) {
            list = shareholderDividendVoList.stream().filter(info -> (info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)
                    || info.getType().equals(ReportDataConstant.Finance.TYPE_ALL))
            ).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(shareholderDividendDto.getType())) {
            list = shareholderDividendVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(shareholderDividendDto.getType())) {
            list = shareholderDividendVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        } else {
            list = shareholderDividendVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }

        fileService.exportExcelForQueryTerm(response, excelExportDto, list,
                ExcelColumnConstant.ShareholderDividend.BRAND_NAME,
                ExcelColumnConstant.ShareholderDividend.REGION_NAME,
                ExcelColumnConstant.ShareholderDividend.SHOP_NAME,
                ExcelColumnConstant.ShareholderDividend.LINE_NUM,
                ExcelColumnConstant.ShareholderDividend.USER_NAME,
                ExcelColumnConstant.ShareholderDividend.PERCENT,
                ExcelColumnConstant.ShareholderDividend.NET_PROFIT,
                ExcelColumnConstant.ShareholderDividend.CUT_PROFIT);
    }

    /**
     * 股东分红单
     *
     * @param shareholderDividendDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.ShareholderDividendVo>
     * @Author lj
     * @Date:14:21 2020/3/17
     **/
    @Override
    public List<ShareholderDividendVo> findShareholderSheet(ShareholderDividendDto shareholderDividendDto) {
        List<ShareholderDividendVo> list = getShareholderDividendVos(shareholderDividendDto);
        //合并结果
        List<ShareholderDividendVo> finList = new ArrayList<>();
        List<ShareholderDividendVo> userList = new ArrayList<>();
        if (!list.isEmpty()) {
            mergeListData(shareholderDividendDto, list);
            for (ShareholderDividendVo shareholderDividendVo : list) {
                shareholderDividendVo.setType(ReportDataConstant.Finance.TYPE_USER);
                shareholderDividendVo.setNetProfit(shareholderDividendVo.getNetProfit()==null?Constant.Number.ZEROB:shareholderDividendVo.getNetProfit());
                shareholderDividendVo.setCutProfit(shareholderDividendVo.getNetProfit().multiply(shareholderDividendVo.getPercent())
                        .divide(Constant.Number.HUNDRED, 2, RoundingMode.HALF_UP));
                userList.add(shareholderDividendVo);
            }
            //股东数据排序
            List<ShareholderDividendVo> userListSorted = userList.stream().filter(data -> data.getUserName() != null)
                    .sorted(Comparator.comparing(ShareholderDividendVo::getUserName)
                            .thenComparing(ShareholderDividendVo::getBrandName)
                            .thenComparing(ShareholderDividendVo::getRegionName)
                            .thenComparing(ShareholderDividendVo::getShopName)).collect(Collectors.toList());
            //以股东名称进行分组
            Map<String, List<ShareholderDividendVo>> userMap = userListSorted.stream().collect(Collectors.groupingBy(ShareholderDividendVo::getUserName));
            //品牌数据排序
            List<ShareholderDividendVo> userTotalList = generateShareholderDividend(userMap, ReportDataConstant.Finance.TYPE_USER_TOTAL);
            List<ShareholderDividendVo> userTotalListSorted = userTotalList.stream()
                    .sorted(Comparator.comparing(ShareholderDividendVo::getUserName)).collect(Collectors.toList());
            finList.addAll(userListSorted);
            finList.addAll(userTotalListSorted);
            //全部数据合计
            ShareholderDividendVo finTypeAll = getFinTypeAll(userListSorted,ReportDataConstant.Finance.TYPE_USER_TOTAL);
            finList.add(finTypeAll);
        }
        return finList;
    }

    /**
     * 导出股东分红单
     *
     * @param excelExportDto
     * @param response
     * @return void
     * @Author lj
     * @Date:15:03 2020/3/18
     **/
    @Override
    public void exportShareholderSheet(ExcelExportDto excelExportDto, HttpServletResponse response) {
        ShareholderDividendDto shareholderDividendDto = new ShareholderDividendDto();
        FastUtils.copyProperties(excelExportDto, shareholderDividendDto);
        List<ShareholderDividendVo> shareholderDividendVoList = findShareholderSheet(shareholderDividendDto);
        List<ShareholderDividendVo> list;
        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_USER.equals(shareholderDividendDto.getType())) {
            list = shareholderDividendVoList.stream().filter(info -> (info.getType().equals(ReportDataConstant.Finance.TYPE_USER)
                    || info.getType().equals(ReportDataConstant.Finance.TYPE_ALL))
            ).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_USER_TOTAL.equals(shareholderDividendDto.getType())) {
            list = shareholderDividendVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_USER_TOTAL)).collect(Collectors.toList());
        } else {
            list = shareholderDividendVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }

        fileService.exportExcelForQueryTerm(response, excelExportDto, list,
                ExcelColumnConstant.ShareholderDividend.USER_NAME,
                ExcelColumnConstant.ShareholderDividend.BRAND_NAME,
                ExcelColumnConstant.ShareholderDividend.REGION_NAME,
                ExcelColumnConstant.ShareholderDividend.SHOP_NAME,
                ExcelColumnConstant.ShareholderDividend.BEN_PROFIT);
    }

    private List<ShareholderDividendVo> generateShareholderSheet(Map<String, List<ShareholderDividendVo>> dataMap, String type) {
        List<ShareholderDividendVo> dataList = new ArrayList<>();
        for (Map.Entry<String, List<ShareholderDividendVo>> entry : dataMap.entrySet()) {
            //按门店维度将数据放入shopList
            ShareholderDividendVo vo = entry.getValue().get(0);
            ShareholderDividendVo shareholderDividendVo = new ShareholderDividendVo();
            shareholderDividendVo.setType(type);
            shareholderDividendVo.setCutProfit(vo.getCutProfit());
            //按门店维度
            if (ReportDataConstant.Finance.TYPE_USER.equals(type)) {
                shareholderDividendVo.setRegionId(vo.getRegionId());
                shareholderDividendVo.setRegionName(vo.getRegionName());
                shareholderDividendVo.setShopId(vo.getShopId());
                shareholderDividendVo.setShopName(vo.getShopName());
                shareholderDividendVo.setBrandId(vo.getBrandId());
                shareholderDividendVo.setBrandName(vo.getBrandName());
            }
            //按品牌维度
            else if (ReportDataConstant.Finance.TYPE_USER_TOTAL.equals(type)) {
                shareholderDividendVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
                shareholderDividendVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                shareholderDividendVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                shareholderDividendVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //按区域维度
            else if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                shareholderDividendVo.setRegionId(vo.getRegionId());
                shareholderDividendVo.setRegionName(vo.getRegionName());
                shareholderDividendVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                shareholderDividendVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //合计cutProfit
            BigDecimal sumCutProfit = entry.getValue().stream().map(ShareholderDividendVo::getCutProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
            shareholderDividendVo.setCutProfit(sumCutProfit);
            dataList.add(shareholderDividendVo);
        }
        return dataList;
    }

    /**
     * 全部数据合计
     *
     * @param shopList
     * @return com.njwd.entity.reportdata.vo.ShareholderDividendVo
     * @Author lj
     * @Date:15:11 2020/3/12
     **/
    private ShareholderDividendVo getFinTypeAll(List<ShareholderDividendVo> shopList,String type) {
        ShareholderDividendVo shareholderDividendVo = new ShareholderDividendVo();
        shareholderDividendVo.setType(ReportDataConstant.Finance.ALL_SUBJECT);
        if(type!=null){
            shareholderDividendVo.setUserName(ReportDataConstant.Finance.ALL_USER);
        }
        generateData(shareholderDividendVo);
        BigDecimal sumCutProfit = shopList.stream().map(ShareholderDividendVo::getCutProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
        shareholderDividendVo.setCutProfit(sumCutProfit);
        return shareholderDividendVo;

    }

    /**
     * 按类型处理
     *
     * @param dataMap, type
     * @return java.util.List<com.njwd.entity.reportdata.vo.ShareholderDividendVo>
     * @Author lj
     * @Date:11:53 2020/3/12
     **/
    private List<ShareholderDividendVo> generateShareholderDividend(Map<String, List<ShareholderDividendVo>> dataMap, String type) {
        List<ShareholderDividendVo> dataList = new ArrayList<>();
        for (Map.Entry<String, List<ShareholderDividendVo>> entry : dataMap.entrySet()) {
            //按门店维度将数据放入shopList
            ShareholderDividendVo vo = entry.getValue().get(0);
            ShareholderDividendVo shareholderDividendVo = new ShareholderDividendVo();
            shareholderDividendVo.setType(type);
            shareholderDividendVo.setUserName(vo.getUserName());
            shareholderDividendVo.setBrandId(vo.getBrandId());
            shareholderDividendVo.setBrandName(vo.getBrandName());

            //按品牌维度
            if (ReportDataConstant.Finance.TYPE_USER_TOTAL.equals(type)) {
                generateData(shareholderDividendVo);
            }
            //按品牌维度
            else if(ReportDataConstant.Finance.TYPE_BRAND.equals(type)){
                shareholderDividendVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
                shareholderDividendVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                shareholderDividendVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                shareholderDividendVo.setUserName(ReportDataConstant.Finance.EMPTY_STRING);
                shareholderDividendVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //按区域维度
            else if(ReportDataConstant.Finance.TYPE_REGION.equals(type)){
                shareholderDividendVo.setRegionId(vo.getRegionId());
                shareholderDividendVo.setRegionName(vo.getRegionName());
                shareholderDividendVo.setUserName(ReportDataConstant.Finance.EMPTY_STRING);
                shareholderDividendVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                shareholderDividendVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //合计cutProfit
            BigDecimal sumCutProfit = entry.getValue().stream().map(ShareholderDividendVo::getCutProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
            shareholderDividendVo.setCutProfit(sumCutProfit);
            dataList.add(shareholderDividendVo);
        }
        return dataList;
    }

    private void generateData(ShareholderDividendVo shareholderDividendVo) {
        shareholderDividendVo.setBrandId(ReportDataConstant.Finance.EMPTY_STRING);
        shareholderDividendVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        shareholderDividendVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
        shareholderDividendVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        shareholderDividendVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
        shareholderDividendVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
    }

}

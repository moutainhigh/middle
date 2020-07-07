package com.njwd.reportdata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.AbnormalOrderDto;
import com.njwd.entity.reportdata.dto.OrderDetailDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.AbnormalOrderVo;
import com.njwd.entity.reportdata.vo.DictVo;
import com.njwd.entity.reportdata.vo.OrderDetailVo;
import com.njwd.reportdata.mapper.BusinessAnalysisMapper;
import com.njwd.reportdata.mapper.OrderStatisticsMapper;
import com.njwd.reportdata.service.OrderStatisticsService;
import com.njwd.service.FileService;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.FastUtils;
import com.njwd.utils.MergeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author lj
 * @Description 账单统计
 * @Date:14:27 2020/4/14
 **/
@Service
public class OrderStatisticsServiceImpl implements OrderStatisticsService {

    @Resource
    private BusinessAnalysisMapper businessAnalysisMapper;

    @Resource
    private OrderStatisticsMapper billStatisticsMapper;

    @Resource
    private FileService fileService;

    /**
     * 异常账单统计表
     *
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.AbnormalOrderVo>
     * @Author lj
     * @Date:18:19 2020/4/14
     **/
    @Override
    public List<AbnormalOrderVo> findAbnormalOrder(AbnormalOrderDto queryDto) {
        //查询所需参数
        List<DictVo> dictList = businessAnalysisMapper.findDictList(queryDto.getEnteId());
        queryDto.setCity(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.POS_ORDER_CASH_AVG))
                .collect(Collectors.toList()).get(0).getModelId().split(Constant.Character.COMMA)[0]);
        queryDto.setValue(new BigDecimal(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.POS_ORDER_CASH_AVG))
                .collect(Collectors.toList()).get(0).getModelId().split(Constant.Character.COMMA)[1]));
        queryDto.setValueTwo(new BigDecimal(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.POS_ORDER_CASH_AVG))
                .collect(Collectors.toList()).get(0).getModelId().split(Constant.Character.COMMA)[2]));
        //查询异常账单数
        List<AbnormalOrderVo> abnormalOrderVoList = billStatisticsMapper.findAbnormalOrderList(queryDto);
        //查询总账单数
        List<AbnormalOrderVo> totalBallList = billStatisticsMapper.findTotalOrderList(queryDto);
        //合并异常账单数到总账单数
        MergeUtil.merge(totalBallList,abnormalOrderVoList,
                AbnormalOrderVo::getShopId, AbnormalOrderVo::getShopId,
                (s,t) -> {
                    s.setAbnormalCount(t.getAbnormalCount());
                    s.setAbnormalRatio(BigDecimalUtils.divideForRatioOrPercent(FastUtils.Null2Zero(s.getAbnormalCount()), s.getOrderCount(), Constant.Number.TWO));
                });
        //设置类型为门店
        totalBallList.forEach(data->{
            List<String> shopIdList = new ArrayList<>();
            shopIdList.add(data.getShopId());
            data.setType(ReportDataConstant.Finance.TYPE_SHOP);
            data.setShopIdList(shopIdList);
            if(data.getAbnormalCount()==null){
                data.setAbnormalCount(BigDecimal.ZERO);
            }
        });


        //按区域方式汇总
        Map<String,List<AbnormalOrderVo>> regionMap = totalBallList.stream().collect(Collectors.groupingBy(AbnormalOrderVo::getRegionId));

        //按品牌方式汇总
        Map<String,List<AbnormalOrderVo>> brandMap = totalBallList.stream().collect(Collectors.groupingBy(AbnormalOrderVo::getBrandId));

        List<AbnormalOrderVo> brandList = generateAbnormalOrder(brandMap,ReportDataConstant.Finance.TYPE_BRAND);

        List<AbnormalOrderVo> regionList = generateAbnormalOrder(regionMap,ReportDataConstant.Finance.TYPE_REGION);
        List<AbnormalOrderVo> allList = new ArrayList<>();
        allList.addAll(totalBallList);
        allList.addAll(regionList);
        allList.addAll(brandList);
        //全部数据合计
        AbnormalOrderVo finTypeAll = getFinTypeAll(totalBallList,ReportDataConstant.Finance.TYPE_ALL);
        allList.add(finTypeAll);
        return allList;
    }

    /**
     * 账单明细表
     *
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.OrderDetailVo>
     * @Author lj
     * @Date:17:49 2020/4/15
     **/
    @Override
    public Page<OrderDetailVo> findOrderDetail(OrderDetailDto queryDto) {
        //查询所需参数
        setQueryDto(queryDto);
        Page<OrderDetailVo> page = queryDto.getPage();
        //查询异常账单数
        Page<OrderDetailVo> orderDetailList = billStatisticsMapper.findOrderDetailList(page,queryDto);
        return orderDetailList;
    }

    /**
     * 导出异常账单统计表
     *
     * @param excelExportDto
     * @param response
     * @return void
     * @Author lj
     * @Date:10:46 2020/4/16
     **/
    @Override
    public void exportAbnormalOrder(ExcelExportDto excelExportDto, HttpServletResponse response) {
        AbnormalOrderDto abnormalOrderDto = new AbnormalOrderDto();
        FastUtils.copyProperties(excelExportDto, abnormalOrderDto);
        List<AbnormalOrderVo> abnormalOrderVoList = findAbnormalOrder(abnormalOrderDto);

        List<AbnormalOrderVo> list;
        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(abnormalOrderDto.getType())) {
            list = abnormalOrderVoList.stream().filter(info -> (info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)
                    || info.getType().equals(ReportDataConstant.Finance.TYPE_ALL))
            ).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(abnormalOrderDto.getType())) {
            list = abnormalOrderVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        }else if (ReportDataConstant.Finance.TYPE_BRAND.equals(abnormalOrderDto.getType())) {
            list = abnormalOrderVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else {
            list = abnormalOrderVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }

        fileService.exportExcelForQueryTerm(response, excelExportDto, list,
                ExcelColumnConstant.AbnormalOrder.BRAND_NAME,
                ExcelColumnConstant.AbnormalOrder.REGION_NAME,
                ExcelColumnConstant.AbnormalOrder.SHOP_NAME,
                ExcelColumnConstant.AbnormalOrder.ABNORMAL_COUNT,
                ExcelColumnConstant.AbnormalOrder.ORDER_COUNT,
                ExcelColumnConstant.AbnormalOrder.ABNORMAL_RATIO);
    }

    /**
     * 导出账单明细表
     *
     * @param excelExportDto
     * @param response
     * @return void
     * @Author lj
     * @Date:10:46 2020/4/16
     **/
    @Override
    public void exportOrderDetail(ExcelExportDto excelExportDto, HttpServletResponse response) {
        OrderDetailDto queryDto = new OrderDetailDto();
        FastUtils.copyProperties(excelExportDto, queryDto);
        //查询所需参数
        setQueryDto(queryDto);
        List<OrderDetailVo> orderDetailVoList = billStatisticsMapper.findOrderDetailList(queryDto);

        fileService.exportExcelForQueryTerm(response, excelExportDto, orderDetailVoList,
                ExcelColumnConstant.OrderDetail.SHOP_NAME,
                ExcelColumnConstant.OrderDetail.ORDER_CODE,
                ExcelColumnConstant.OrderDetail.CONSUME,
                ExcelColumnConstant.OrderDetail.PEOPLE_NUM,
                ExcelColumnConstant.OrderDetail.AVG_CONSUME,
                ExcelColumnConstant.OrderDetail.FOOD_NUM,
                ExcelColumnConstant.OrderDetail.IS_ABNORMAL);
    }

    private void setQueryDto(OrderDetailDto queryDto) {
        List<DictVo> dictList = businessAnalysisMapper.findDictList(queryDto.getEnteId());
        queryDto.setCity(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.POS_ORDER_CASH_AVG))
                .collect(Collectors.toList()).get(0).getModelId().split(Constant.Character.COMMA)[0]);
        queryDto.setValue(new BigDecimal(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.POS_ORDER_CASH_AVG))
                .collect(Collectors.toList()).get(0).getModelId().split(Constant.Character.COMMA)[1]));
        queryDto.setValueTwo(new BigDecimal(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.POS_ORDER_CASH_AVG))
                .collect(Collectors.toList()).get(0).getModelId().split(Constant.Character.COMMA)[2]));
        queryDto.setFoodName(dictList.stream().filter(a -> a.getModelName().equals(Constant.DictValue.POS_ORDER_DETAIL_FOOD))
                .collect(Collectors.toList()).get(0).getModelId()
        );
    }

    /**
     * 按类型处理
     * @Author lj
     * @Date:14:06 2020/4/15
     * @param dataMap, type
     * @return java.util.List<com.njwd.entity.reportdata.vo.AbnormalOrderVo>
     **/
    private List<AbnormalOrderVo> generateAbnormalOrder(Map<String, List<AbnormalOrderVo>> dataMap, String type) {
        List<AbnormalOrderVo> dataList = new ArrayList<>();
        for (Map.Entry<String, List<AbnormalOrderVo>> entry : dataMap.entrySet()) {
            List<AbnormalOrderVo> abnormalOrderVoList = entry.getValue();
            //按门店维度将数据放入shopList
            AbnormalOrderVo vo = entry.getValue().get(0);
            AbnormalOrderVo abnormalOrderVo = new AbnormalOrderVo();
            abnormalOrderVo.setType(type);
            abnormalOrderVo.setBrandId(vo.getBrandId());
            abnormalOrderVo.setBrandName(vo.getBrandName());
            //计算合计
            calcTotal(abnormalOrderVoList, abnormalOrderVo);

            //按品牌维度
            if(ReportDataConstant.Finance.TYPE_BRAND.equals(type)){
                abnormalOrderVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
                abnormalOrderVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                abnormalOrderVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);;
                abnormalOrderVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            //按区域维度
            else if(ReportDataConstant.Finance.TYPE_REGION.equals(type)){
                abnormalOrderVo.setRegionId(vo.getRegionId());
                abnormalOrderVo.setRegionName(vo.getRegionName());
                abnormalOrderVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
                abnormalOrderVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            }
            dataList.add(abnormalOrderVo);
        }
        return dataList;
    }

    /**
     * 全部数据合计
     *
     * @param abnormalOrderVoList
     * @return com.njwd.entity.reportdata.vo.ShareholderDividendVo
     * @Author lj
     * @Date:15:11 2020/3/12
     **/
    private AbnormalOrderVo getFinTypeAll(List<AbnormalOrderVo> abnormalOrderVoList, String type) {
        AbnormalOrderVo abnormalOrderVo = new AbnormalOrderVo();
        abnormalOrderVo.setType(ReportDataConstant.Finance.TYPE_ALL);
        abnormalOrderVo.setBrandId(ReportDataConstant.Finance.EMPTY_STRING);
        abnormalOrderVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        abnormalOrderVo.setRegionId(ReportDataConstant.Finance.EMPTY_STRING);
        abnormalOrderVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        abnormalOrderVo.setShopId(ReportDataConstant.Finance.EMPTY_STRING);
        abnormalOrderVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        //计算合计
        calcTotal(abnormalOrderVoList, abnormalOrderVo);
        return abnormalOrderVo;

    }

    private void calcTotal(List<AbnormalOrderVo> abnormalOrderVoList, AbnormalOrderVo abnormalOrderVo) {
        List<String> shopIdList = new ArrayList<>();
        BigDecimal orderCount = BigDecimal.ZERO;
        BigDecimal abnormalCount = BigDecimal.ZERO;
        for (AbnormalOrderVo data : abnormalOrderVoList) {
            shopIdList.add(data.getShopId());
            if (data.getAbnormalCount() != null) {
                abnormalCount = abnormalCount.add(data.getAbnormalCount());
            }
            if (data.getOrderCount() != null) {
                orderCount = orderCount.add(data.getOrderCount());
            }
        }
        abnormalOrderVo.setShopIdList(shopIdList);
        abnormalOrderVo.setAbnormalCount(abnormalCount);
        abnormalOrderVo.setOrderCount(orderCount);
        abnormalOrderVo.setAbnormalRatio(BigDecimalUtils.divideForRatioOrPercent(FastUtils.Null2Zero(abnormalOrderVo.getAbnormalCount()),
                abnormalOrderVo.getOrderCount(), Constant.Number.TWO));
    }
}

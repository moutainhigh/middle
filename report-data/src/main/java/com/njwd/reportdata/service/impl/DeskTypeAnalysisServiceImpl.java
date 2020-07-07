package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.BaseYearMomThreshold;
import com.njwd.entity.reportdata.dto.BaseYearMomThresholdDto;
import com.njwd.entity.reportdata.dto.DeskTypeAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo;
import com.njwd.poiexcel.Column;
import com.njwd.poiexcel.ExcelTool;
import com.njwd.poiexcel.TitleEntity;
import com.njwd.reportdata.mapper.DeskTypeAnalysisMapper;
import com.njwd.reportdata.service.BaseYearMomThresholdService;
import com.njwd.reportdata.service.DeskTypeAnalysisService;
import com.njwd.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/18 16:20
 */
@Service
public class DeskTypeAnalysisServiceImpl implements DeskTypeAnalysisService {

    @Autowired
    private DeskTypeAnalysisMapper deskTypeAnalysisMapper;

    @Autowired
    private BaseYearMomThresholdService baseYearMomThresholdService;

    @Value("${constant.file.excelRootPath}")
    private String excelRootPath;

    /**
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @Description 查询 台型分析报表
     */
    @Override
    public Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReport(DeskTypeAnalysisDto deskTypeAnalysisDto) {
        //查询 对应区域类型（大厅 or 包厢 or ...） 对应台型(2人桌 or 4人桌 or ...) 开台数 客流 营业额
        List<DeskTypeAnalysisVo> deskTypeAnalysisVo = deskTypeAnalysisMapper.findDeskTypeAnalysisReport(deskTypeAnalysisDto);
        //查询 对应区域类型 对应台型 桌数
        List<DeskTypeAnalysisVo> deskNumByTypeList = deskTypeAnalysisMapper.findDeskNumByType(deskTypeAnalysisDto);
        //桌数 与  客流，营业额等数据拼接
        List<DeskTypeAnalysisVo> deskTypeAnalysisVoInfo = getDeskTypeAnalysisInfo(deskTypeAnalysisVo, deskNumByTypeList);
        //键：台型区域  值：台型区域对应统计数据
        Map<String, List<DeskTypeAnalysisVo>> resultList = new HashMap<>();
        //将 不同台型区域 数据拼接，同种台型 整合为一条数据
        if (!FastUtils.checkNullOrEmpty(deskTypeAnalysisVoInfo)) {
            resultList = deskTypeAnalysisVoInfo.stream().collect(Collectors.groupingBy(vos -> vos.getDeskAreaTypeName()));
        }
        //计算桌数 开台数 客流 营业额 占比
        resultList.forEach((k, analysisList) -> {
            //取得合计数据
            DeskTypeAnalysisVo totalVo = analysisList.stream().filter(vo -> vo.getDeskTypeId() != null && vo.getDeskTypeId().equals(Constant.CruiseDesk.TOTAL_NUMBER_ID)).findFirst().orElse(null);
            if (!FastUtils.checkNull(totalVo)) {
                totalVo.setDeskNumPer(Constant.Number.HUNDRED);
                totalVo.setStationsNumPer(Constant.Number.HUNDRED);
                totalVo.setCustomNumPer(Constant.Number.HUNDRED);
                totalVo.setTurnoverPer(Constant.Number.HUNDRED);
                //当前 桌数 开台 客流 营业额数
                BigDecimal deskNum = BigDecimal.ZERO;
                BigDecimal stationsNum = BigDecimal.ZERO;
                BigDecimal customNum = BigDecimal.ZERO;
                BigDecimal turnoverNum = BigDecimal.ZERO;
                //各台型所占百分比累加
                BigDecimal deskNumPerCount = new BigDecimal(BigDecimal.ROUND_UP);
                BigDecimal stationsNumPerCount = new BigDecimal(BigDecimal.ROUND_UP);
                BigDecimal customNumPerCount = new BigDecimal(BigDecimal.ROUND_UP);
                BigDecimal turnoverPerCount = new BigDecimal(BigDecimal.ROUND_UP);
                DeskTypeAnalysisVo currentVo;
                //总计 桌数 开台 客流数
                BigDecimal deskTotal = new BigDecimal(totalVo.getDeskNum());
                BigDecimal stationsTotal = new BigDecimal(totalVo.getStationsNum());
                BigDecimal customTotal = new BigDecimal(totalVo.getCustomNum());
                BigDecimal turnoverTotal = totalVo.getTurnover();
                //当前桌型 桌数 开台 客流数
                BigDecimal deskCurrent;
                BigDecimal stationsCurrent;
                BigDecimal customCurrent;
                BigDecimal turnoverCurrent;
                //占比 桌数 开台 客流数 营业额
                BigDecimal deskPer;
                BigDecimal stationsPer;
                BigDecimal customPer;
                BigDecimal turnoverPer;
                for (int i = 0; i < analysisList.size() - 1; i++) {
                    currentVo = analysisList.get(i);
                    deskCurrent = new BigDecimal(currentVo.getDeskNum());
                    stationsCurrent = new BigDecimal(currentVo.getStationsNum());
                    customCurrent = new BigDecimal(currentVo.getCustomNum());
                    turnoverCurrent = currentVo.getTurnover();
                    deskPer = BigDecimalUtils.getPercent(deskCurrent, deskTotal);
                    stationsPer = BigDecimalUtils.getPercent(stationsCurrent, stationsTotal);
                    customPer = BigDecimalUtils.getPercent(customCurrent, customTotal);
                    turnoverPer = BigDecimalUtils.getPercent(turnoverCurrent,turnoverTotal);
                    //当不是总计 也不是除总计的最后一条数据时
                    deskNum = deskNum.add(deskCurrent);
                    //累加值未达到总值时
                    if(deskNum.compareTo(deskTotal) == ReportDataConstant.BigdecimalCompare.LESS_THAN){
                        //占比 存入对象中
                        currentVo.setDeskNumPer(deskPer);
                        //百分比累加
                        deskNumPerCount = deskNumPerCount.add(deskPer);
                    }else{
                        //占比 存入对象中
                        currentVo.setDeskNumPer(Constant.Number.HUNDRED.subtract(deskNumPerCount));
                        deskNumPerCount = deskNumPerCount.add(currentVo.getDeskNumPer());
                    }
                    stationsNum = stationsNum.add(stationsCurrent);
                    if(stationsNum.compareTo(stationsTotal) == ReportDataConstant.BigdecimalCompare.LESS_THAN){
                        currentVo.setStationsNumPer(stationsPer);
                        stationsNumPerCount = stationsNumPerCount.add(stationsPer);
                    }else{
                        currentVo.setStationsNumPer(Constant.Number.HUNDRED.subtract(stationsNumPerCount));
                        stationsNumPerCount = stationsNumPerCount.add(currentVo.getStationsNumPer());
                    }
                    customNum = customNum.add(customCurrent);
                    if(customNum.compareTo(customTotal) == ReportDataConstant.BigdecimalCompare.LESS_THAN){
                        currentVo.setCustomNumPer(customPer);
                        customNumPerCount = customNumPerCount.add(customPer);
                    }else{
                        currentVo.setCustomNumPer(Constant.Number.HUNDRED.subtract(customNumPerCount));
                        customNumPerCount = customNumPerCount.add(currentVo.getCustomNumPer());
                    }
                    turnoverNum = turnoverNum.add(turnoverCurrent);
                    if(turnoverNum.compareTo(turnoverTotal) == ReportDataConstant.BigdecimalCompare.LESS_THAN){
                        currentVo.setTurnoverPer(turnoverPer);
                        turnoverPerCount = turnoverPerCount.add(turnoverPer);
                    }else{
                        currentVo.setTurnoverPer(Constant.Number.HUNDRED.subtract(turnoverPerCount));
                        turnoverPerCount = turnoverPerCount.add(currentVo.getTurnoverPer());
                    }
                }
            }
        });
        for(Map.Entry<String, List<DeskTypeAnalysisVo>> entry : resultList.entrySet()) {
            //按桌子名称降序 --Lamdba表达式
            Collections.sort(entry.getValue(), (a, b) -> getNumByString(a.getDeskTypeName()) - (getNumByString(b.getDeskTypeName())));
        }
        return resultList;
    }

    /**
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @Description 查询 台型分析报表 同比
     */
    @Override
    public Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReportCompareLastYear(DeskTypeAnalysisDto deskTypeAnalysisDto) {
        //查询 对应区域类型 对应台型 桌数
        List<DeskTypeAnalysisVo> deskNumByTypeList = deskTypeAnalysisMapper.findDeskNumByType(deskTypeAnalysisDto);
        //键：台型区域  值：台型区域对应统计数据
        Map<String, List<DeskTypeAnalysisVo>> resultList = new HashMap<>();
        if (!FastUtils.checkNullOrEmpty(deskNumByTypeList)) {
            List<DeskTypeAnalysisVo> lastDeskNumByTypeList = getDeskNumByTypeCopy(deskNumByTypeList);
            //查询 对应区域类型（大厅 or 包厢 or ...） 对应台型(2人桌 or 4人桌 or ...) 开台数 客流 营业额
            List<DeskTypeAnalysisVo> deskTypeAnalysisVo = deskTypeAnalysisMapper.findDeskTypeAnalysisReport(deskTypeAnalysisDto);
            //本期
            List<DeskTypeAnalysisVo> deskTypeAnalysisNow = getDeskTypeAnalysisInfo(deskTypeAnalysisVo, deskNumByTypeList);
            //根据日期类型 选择 对应同比时间
            List<Date> dateList = DateUtils.getLastYearDate(deskTypeAnalysisDto.getStartDate(), deskTypeAnalysisDto.getEndDate(), deskTypeAnalysisDto.getDateType());
            if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
                deskTypeAnalysisDto.setStartDate(dateList.get(Constant.Number.ZERO));
                deskTypeAnalysisDto.setEndDate(dateList.get(Constant.Number.ONE));
            }
            List<DeskTypeAnalysisVo> deskTypeAnalysisVoLastYear = deskTypeAnalysisMapper.findDeskTypeAnalysisReport(deskTypeAnalysisDto);
            //去年同期
            List<DeskTypeAnalysisVo> deskTypeAnalysisLastYear = getDeskTypeAnalysisInfo(deskTypeAnalysisVoLastYear, lastDeskNumByTypeList);
            dealCurrentAndLastAnalysis(deskTypeAnalysisNow, deskTypeAnalysisLastYear);
            //阀值查询条件
            BaseYearMomThresholdDto dto = getBaseYearMomThresholdDto(deskTypeAnalysisDto);
            dto.setTableCode(Constant.ThresholdCode.deskTypeAnalysis);
            BaseYearMomThreshold baseYearMomThreshold = baseYearMomThresholdService.findBaseYearMomThresholdByCode(dto);
            //计算阀值 生成状态
            if (!FastUtils.checkNullOrEmpty(baseYearMomThreshold)) {
                //同比
                BigDecimal threshold = baseYearMomThreshold.getYearThreshold();
                countThresholdStatus(deskTypeAnalysisNow, threshold);
            }
            //将 不同台型区域 数据拼接，同种台型 整合为一条数据
            if (!FastUtils.checkNullOrEmpty(deskTypeAnalysisNow)) {
                resultList = deskTypeAnalysisNow.stream().collect(Collectors.groupingBy(vos -> vos.getDeskAreaTypeName()));
                for(Map.Entry<String, List<DeskTypeAnalysisVo>> entry : resultList.entrySet()) {
                    //按桌子名称降序 --Lamdba表达式
                    Collections.sort(entry.getValue(), (a, b) -> getNumByString(a.getDeskTypeName()) - (getNumByString(b.getDeskTypeName())));
                }
            }
        }
        return resultList;
    }

    /**
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @Description 查询 手机-台型分析报表 同比
     */
    @Override
    public Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReportCompareLastYearApp(DeskTypeAnalysisDto deskTypeAnalysisDto) {
        //查询 对应区域类型 对应台型 桌数
        List<DeskTypeAnalysisVo> deskNumByTypeList = deskTypeAnalysisMapper.findDeskNumByType(deskTypeAnalysisDto);
        //键：台型区域  值：台型区域对应统计数据
        Map<String, List<DeskTypeAnalysisVo>> resultList = new HashMap<>();
        if (!FastUtils.checkNullOrEmpty(deskNumByTypeList)) {
            List<DeskTypeAnalysisVo> lastDeskNumByTypeList = getDeskNumByTypeCopy(deskNumByTypeList);
            //查询 对应区域类型（大厅 or 包厢 or ...） 对应台型(2人桌 or 4人桌 or ...) 开台数 客流 营业额
            List<DeskTypeAnalysisVo> deskTypeAnalysisVo = deskTypeAnalysisMapper.findDeskTypeAnalysisReport(deskTypeAnalysisDto);
            //本期
            List<DeskTypeAnalysisVo> deskTypeAnalysisNow = getDeskTypeAnalysisInfo(deskTypeAnalysisVo, deskNumByTypeList);
            //根据日期类型 选择 对应同比时间
            List<Date> dateList = DateUtils.getLastYearDate(deskTypeAnalysisDto.getStartDate(), deskTypeAnalysisDto.getEndDate(), deskTypeAnalysisDto.getDateType());
            if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
                deskTypeAnalysisDto.setStartDate(dateList.get(Constant.Number.ZERO));
                deskTypeAnalysisDto.setEndDate(dateList.get(Constant.Number.ONE));
            }
            List<DeskTypeAnalysisVo> deskTypeAnalysisVoLastYear = deskTypeAnalysisMapper.findDeskTypeAnalysisReport(deskTypeAnalysisDto);
            //去年同期
            List<DeskTypeAnalysisVo> deskTypeAnalysisLastYear = getDeskTypeAnalysisInfo(deskTypeAnalysisVoLastYear, lastDeskNumByTypeList);
            //本期 key: 台型区域
            Map<String, List<DeskTypeAnalysisVo>> currentMap = deskTypeAnalysisNow.stream().collect(Collectors.groupingBy(vos -> vos.getDeskAreaTypeName()));
            //去年同期 key: 台型区域
            Map<String, List<DeskTypeAnalysisVo>> lastYearMap = deskTypeAnalysisLastYear.stream().collect(Collectors.groupingBy(vos -> vos.getDeskAreaTypeName()));
            //计算同比
            dealCurrentAndLastAnalysis(deskTypeAnalysisNow, deskTypeAnalysisLastYear);
            //阀值查询条件
            BaseYearMomThresholdDto dto = getBaseYearMomThresholdDto(deskTypeAnalysisDto);
            dto.setTableCode(Constant.ThresholdCode.deskTypeAnalysis);
            BaseYearMomThreshold baseYearMomThreshold = baseYearMomThresholdService.findBaseYearMomThresholdByCode(dto);
            //计算阀值 生成状态
            if (!FastUtils.checkNullOrEmpty(baseYearMomThreshold)) {
                //同比
                BigDecimal threshold = baseYearMomThreshold.getYearThreshold();
                countThresholdStatus(deskTypeAnalysisNow, threshold);
            }
            Map<String, List<DeskTypeAnalysisVo>> percentageMap = deskTypeAnalysisNow.stream().collect(Collectors.groupingBy(vos -> vos.getDeskAreaTypeName()));
            //去年同期 与 本期 台型相同的数据
            Map<String, List<DeskTypeAnalysisVo>> mapTemp = new HashMap<>();
            Map<String, List<DeskTypeAnalysisVo>> mapPercentage = new HashMap<>();
            for (Map.Entry<String, List<DeskTypeAnalysisVo>> entry : currentMap.entrySet()) {
                resultList.put(Constant.ComparePrefixFlag.CURRENT + entry.getKey(), entry.getValue());
                mapTemp.put(Constant.ComparePrefixFlag.LAST_YEAR + entry.getKey(), lastYearMap.get(entry.getKey()));
                mapPercentage.put(Constant.ComparePrefixFlag.LAST_YEAR_COMPARE + entry.getKey(), percentageMap.get(entry.getKey()));
            }
            resultList.putAll(mapTemp);
            resultList.putAll(mapPercentage);
            for(Map.Entry<String, List<DeskTypeAnalysisVo>> entry : resultList.entrySet()) {
                //按桌子名称降序 --Lamdba表达式
                Collections.sort(entry.getValue(), (a, b) -> getNumByString(a.getDeskTypeName()) - (getNumByString(b.getDeskTypeName())));
            }
        }
        return resultList;
    }

    /**
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @Description 查询 台型分析报表 环比
     */
    @Override
    public Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReportCompareLastPeriod(DeskTypeAnalysisDto deskTypeAnalysisDto) {
        //查询 对应区域类型 对应台型 桌数
        List<DeskTypeAnalysisVo> deskNumByTypeList = deskTypeAnalysisMapper.findDeskNumByType(deskTypeAnalysisDto);
        //键：台型区域  值：台型区域对应统计数据
        Map<String, List<DeskTypeAnalysisVo>> resultList = new HashMap<>();
        if (!FastUtils.checkNullOrEmpty(deskNumByTypeList)) {
            List<DeskTypeAnalysisVo> lastDeskNumByTypeList = getDeskNumByTypeCopy(deskNumByTypeList);
            //查询 对应区域类型（大厅 or 包厢 or ...） 对应台型(2人桌 or 4人桌 or ...) 开台数 客流 营业额
            List<DeskTypeAnalysisVo> deskTypeAnalysisVo = deskTypeAnalysisMapper.findDeskTypeAnalysisReport(deskTypeAnalysisDto);
            //本期
            List<DeskTypeAnalysisVo> deskTypeAnalysisNow = getDeskTypeAnalysisInfo(deskTypeAnalysisVo, deskNumByTypeList);
            //根据日期类型 选择 对应环比时间
            List<Date> dateList = DateUtils.getLastPeriodDate(deskTypeAnalysisDto.getStartDate(), deskTypeAnalysisDto.getEndDate(), deskTypeAnalysisDto.getDateType());
            if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
                deskTypeAnalysisDto.setStartDate(dateList.get(Constant.Number.ZERO));
                deskTypeAnalysisDto.setEndDate(dateList.get(Constant.Number.ONE));
            }
            List<DeskTypeAnalysisVo> deskTypeAnalysisVoLastPeriod = deskTypeAnalysisMapper.findDeskTypeAnalysisReport(deskTypeAnalysisDto);
            //上期
            List<DeskTypeAnalysisVo> deskTypeAnalysisLastPeriod = getDeskTypeAnalysisInfo(deskTypeAnalysisVoLastPeriod, lastDeskNumByTypeList);
            dealCurrentAndLastAnalysis(deskTypeAnalysisNow, deskTypeAnalysisLastPeriod);
            //阀值查询条件
            BaseYearMomThresholdDto dto = getBaseYearMomThresholdDto(deskTypeAnalysisDto);
            dto.setTableCode(Constant.ThresholdCode.deskTypeAnalysis);
            BaseYearMomThreshold baseYearMomThreshold = baseYearMomThresholdService.findBaseYearMomThresholdByCode(dto);
            //计算阀值 生成状态
            if (!FastUtils.checkNullOrEmpty(baseYearMomThreshold)) {
                //环比
                BigDecimal threshold = baseYearMomThreshold.getMomThreshold();
                countThresholdStatus(deskTypeAnalysisNow, threshold);
            }
            //将 不同台型区域 数据拼接，同种台型 整合为一条数据
            if (!FastUtils.checkNullOrEmpty(deskTypeAnalysisNow)) {
                resultList = deskTypeAnalysisNow.stream().collect(Collectors.groupingBy(vos -> vos.getDeskAreaTypeName()));
                for(Map.Entry<String, List<DeskTypeAnalysisVo>> entry : resultList.entrySet()) {
                    //按桌子名称降序 --Lamdba表达式
                    Collections.sort(entry.getValue(), (a, b) -> getNumByString(a.getDeskTypeName()) - (getNumByString(b.getDeskTypeName())));
                }
            }
        }
        return resultList;
    }

    /**
     * @return java.util.Map<java.lang.String,java.util.List<com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Author ZhuHC
     * @Date 2019/12/23 10:08
     * @Param [deskTypeAnalysisDto]
     * @Description app 环比
     */
    @Override
    public Map<String, List<DeskTypeAnalysisVo>> findDeskTypeAnalysisReportCompareLastPeriodApp(DeskTypeAnalysisDto deskTypeAnalysisDto) {
        //查询 对应区域类型 对应台型 桌数
        List<DeskTypeAnalysisVo> deskNumByTypeList = deskTypeAnalysisMapper.findDeskNumByType(deskTypeAnalysisDto);
        //键：台型区域  值：台型区域对应统计数据
        Map<String, List<DeskTypeAnalysisVo>> resultList = new HashMap<>();
        if (!FastUtils.checkNullOrEmpty(deskNumByTypeList)) {
            List<DeskTypeAnalysisVo> lastDeskNumByTypeList = getDeskNumByTypeCopy(deskNumByTypeList);
            //查询 对应区域类型（大厅 or 包厢 or ...） 对应台型(2人桌 or 4人桌 or ...) 开台数 客流 营业额
            List<DeskTypeAnalysisVo> deskTypeAnalysisVo = deskTypeAnalysisMapper.findDeskTypeAnalysisReport(deskTypeAnalysisDto);
            //本期
            List<DeskTypeAnalysisVo> deskTypeAnalysisNow = getDeskTypeAnalysisInfo(deskTypeAnalysisVo, deskNumByTypeList);
            //根据日期类型 选择 对应环比时间
            List<Date> dateList = DateUtils.getLastPeriodDate(deskTypeAnalysisDto.getStartDate(), deskTypeAnalysisDto.getEndDate(), deskTypeAnalysisDto.getDateType());
            if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
                deskTypeAnalysisDto.setStartDate(dateList.get(Constant.Number.ZERO));
                deskTypeAnalysisDto.setEndDate(dateList.get(Constant.Number.ONE));
            }
            List<DeskTypeAnalysisVo> deskTypeAnalysisVoLastPeriod = deskTypeAnalysisMapper.findDeskTypeAnalysisReport(deskTypeAnalysisDto);
            //上期
            List<DeskTypeAnalysisVo> deskTypeAnalysisLastPeriod = getDeskTypeAnalysisInfo(deskTypeAnalysisVoLastPeriod, lastDeskNumByTypeList);
            //本期 key: 台型区域
            Map<String, List<DeskTypeAnalysisVo>> currentMap = deskTypeAnalysisNow.stream().collect(Collectors.groupingBy(vos -> vos.getDeskAreaTypeName()));
            //上期 key: 台型区域
            Map<String, List<DeskTypeAnalysisVo>> lastPeriodMap = deskTypeAnalysisLastPeriod.stream().collect(Collectors.groupingBy(vos -> vos.getDeskAreaTypeName()));
            //计算同比
            dealCurrentAndLastAnalysis(deskTypeAnalysisNow, deskTypeAnalysisLastPeriod);
            //阀值查询条件
            BaseYearMomThresholdDto dto = getBaseYearMomThresholdDto(deskTypeAnalysisDto);
            dto.setTableCode(Constant.ThresholdCode.deskTypeAnalysis);
            BaseYearMomThreshold baseYearMomThreshold = baseYearMomThresholdService.findBaseYearMomThresholdByCode(dto);
            //计算阀值 生成状态
            if (!FastUtils.checkNullOrEmpty(baseYearMomThreshold)) {
                //环比
                BigDecimal threshold = baseYearMomThreshold.getMomThreshold();
                countThresholdStatus(deskTypeAnalysisNow, threshold);
            }
            Map<String, List<DeskTypeAnalysisVo>> percentageMap = deskTypeAnalysisNow.stream().collect(Collectors.groupingBy(vos -> vos.getDeskAreaTypeName()));
            //上期 与 本期 台型相同的数据
            Map<String, List<DeskTypeAnalysisVo>> mapTemp = new HashMap<>();
            Map<String, List<DeskTypeAnalysisVo>> mapPercentage = new HashMap<>();
            for (Map.Entry<String, List<DeskTypeAnalysisVo>> entry : currentMap.entrySet()) {
                resultList.put(Constant.ComparePrefixFlag.CURRENT + entry.getKey(), entry.getValue());
                mapTemp.put(Constant.ComparePrefixFlag.LAST_PERIOD + entry.getKey(), lastPeriodMap.get(entry.getKey()));
                mapPercentage.put(Constant.ComparePrefixFlag.LAST_PERIOD_COMPARE + entry.getKey(), percentageMap.get(entry.getKey()));
            }
            resultList.putAll(mapTemp);
            resultList.putAll(mapPercentage);
            for(Map.Entry<String, List<DeskTypeAnalysisVo>> entry : resultList.entrySet()) {
                //按桌子名称降序 --Lamdba表达式
                Collections.sort(entry.getValue(), (a, b) -> getNumByString(a.getDeskTypeName()) - (getNumByString(b.getDeskTypeName())));
            }
        }
        return resultList;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/4 15:26
     * @Param [deskTypeAnalysisDto, response]
     * @return void
     * @Description 导出
     */
    @Override
    public void exportDeskAnalysisExcel(ExcelExportDto excelExportDto, HttpServletResponse response) {
        //设置表格数据 查询条件
        DeskTypeAnalysisDto deskTypeAnalysisDto =  setDeskAnalysisQueryDto(excelExportDto);
        Map<String, List<DeskTypeAnalysisVo>> listMap = findDeskTypeAnalysisReport(deskTypeAnalysisDto);
        if(null != listMap && listMap.size() > 0){
            //表头
            List<TitleEntity> titleList=setQueryInfoTitle(excelExportDto);
            int mapIndex = Constant.Number.ZERO;
            int tId = Constant.Number.EIGHT;
            String pId = String.valueOf(tId);
            List<Map<String,Object>> rowList=new ArrayList<>();
            //加入千分位，保留2位小数，并且不够补0
            DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
            DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
            for(Map.Entry<String, List<DeskTypeAnalysisVo>> entry : listMap.entrySet()) {
                //对数据进行排序
                List<DeskTypeAnalysisVo> analysisVoList = entry.getValue();
                //按桌子名称降序 --Lamdba表达式
                Collections.sort(analysisVoList, (a, b) -> getNumByString(a.getDeskTypeName())-(getNumByString(b.getDeskTypeName())));
                String indexStr = String.valueOf(mapIndex);
                tId = tId+2;
                TitleEntity entity=new TitleEntity(String.valueOf(tId),pId,entry.getKey(),null);
                String typeId = String.valueOf(tId);
                tId++;
                TitleEntity entity0=new TitleEntity(String.valueOf(tId),typeId,"桌数","deskNum"+indexStr);
                tId++;
                TitleEntity entity1=new TitleEntity(String.valueOf(tId),typeId,"开台数","stationsNum"+indexStr);
                tId++;
                TitleEntity entity2=new TitleEntity(String.valueOf(tId),typeId,"客流","customNum"+indexStr);
                tId++;
                TitleEntity entity3=new TitleEntity(String.valueOf(tId),typeId,"营业额","turnover"+indexStr);
                tId++;
                TitleEntity entity4=new TitleEntity(String.valueOf(tId),typeId,"桌数占比","deskNumPer"+indexStr);
                tId++;
                TitleEntity entity5=new TitleEntity(String.valueOf(tId),typeId,"开台数占比","stationsNumPer"+indexStr);
                tId++;
                TitleEntity entity6=new TitleEntity(String.valueOf(tId),typeId,"客流占比","customNumPer"+indexStr);
                tId++;
                TitleEntity entity7=new TitleEntity(String.valueOf(tId),typeId,"营业额占比","turnoverPer"+indexStr);
                tId++;
                titleList.add(entity);titleList.add(entity0);titleList.add(entity1);titleList.add(entity2);titleList.add(entity3);
                titleList.add(entity4);titleList.add(entity5);titleList.add(entity6);titleList.add(entity7);
                DeskTypeAnalysisVo vo;
                Map<String,Object> mapNext;
                for(int i = 0; i < analysisVoList.size(); i++){
                    vo = analysisVoList.get(i);
                    if(Constant.Number.ZERO == mapIndex){
                        Map m = new HashMap<String,Object>();
                        m.put("deskTypeName",vo.getDeskTypeName());
                        setDeskAnalysisExcelData(df, df2, indexStr, vo, m);
                        rowList.add(m);
                    }else{
                        // 存在大厅以外区域时  例如包厢  需将数据加入map中
                        mapNext = rowList.get(i);
                        setDeskAnalysisExcelData(df, df2, indexStr, vo, mapNext);
                    }
                }
                mapIndex++;
            }
            exportMethod(response, titleList, rowList);
        }
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/5 9:23
     * @Param [deskTypeAnalysisDto, response]
     * @return void
     * @Description 台型分析报表（同比 / 环比）导出
     */
    @Override
    public void exportDeskAnalysisLastExcel(ExcelExportDto excelExportDto, HttpServletResponse response,String exportType) {
        //设置表格数据 查询条件
        DeskTypeAnalysisDto deskTypeAnalysisDto =  setDeskAnalysisQueryDto(excelExportDto);
        Map<String, List<DeskTypeAnalysisVo>> listMap;
        //同比
        if(ReportDataConstant.ExcelExportInfo.DESKANALYSISLASTYEARXLS.equals(exportType)){
            listMap  = findDeskTypeAnalysisReportCompareLastYear(deskTypeAnalysisDto);
        }else{
            //环比
            listMap  = findDeskTypeAnalysisReportCompareLastPeriod(deskTypeAnalysisDto);
        }
        if(null != listMap && listMap.size() > 0){
            //表头
            List<TitleEntity> titleList= setQueryInfoTitle(excelExportDto);
            int mapIndex = Constant.Number.ZERO;
            //下载时间的 ID 作为以下标题的根ID
            int tId = Constant.Number.EIGHT;
            String pId = String.valueOf(tId);
            List<Map<String,Object>> rowList=new ArrayList<>();
            //加入千分位，保留2位小数，并且不够补0
            DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
            DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
            for(Map.Entry<String, List<DeskTypeAnalysisVo>> entry : listMap.entrySet()) {
                //对数据进行排序
                List<DeskTypeAnalysisVo> analysisVoList = entry.getValue();
                //按桌子名称降序 --Lamdba表达式
                Collections.sort(analysisVoList, (a, b) -> getNumByString(a.getDeskTypeName())-(getNumByString(b.getDeskTypeName())));
                tId = tId+2;
                String indexStr = String.valueOf(mapIndex);
                TitleEntity entity=new TitleEntity(String.valueOf(tId),pId,entry.getKey(),null);
                //大厅-区域类型ID
                String sId = String.valueOf(tId);
                tId++;
                TitleEntity entity0=new TitleEntity(String.valueOf(tId),sId,"本期",null);
                //保存2级表头ID-本期
                String secondId = String.valueOf(tId);
                tId++;
                TitleEntity entity1=new TitleEntity(String.valueOf(tId),secondId,"桌数","deskNum"+indexStr);
                tId++;
                TitleEntity entity2=new TitleEntity(String.valueOf(tId),secondId,"开台数","stationsNum"+indexStr);
                tId++;
                TitleEntity entity3=new TitleEntity(String.valueOf(tId),secondId,"客流","customNum"+indexStr);
                tId++;
                TitleEntity entity4=new TitleEntity(String.valueOf(tId),secondId,"营业额","turnover"+indexStr);
                tId++;
                TitleEntity entity5=new TitleEntity(String.valueOf(tId),sId,"去年同期",null);
                //保存2级表头ID-去年同期
                String thirdId = String.valueOf(tId);
                tId++;
                TitleEntity entity6=new TitleEntity(String.valueOf(tId),thirdId,"开台数","lastStationsNum"+indexStr);
                tId++;
                TitleEntity entity7=new TitleEntity(String.valueOf(tId),thirdId,"客流","lastCustomNum"+indexStr);
                tId++;
                TitleEntity entity8=new TitleEntity(String.valueOf(tId),thirdId,"营业额","lastTurnover"+indexStr);
                tId++;
                TitleEntity entity9;
                if(ReportDataConstant.ExcelExportInfo.DESKANALYSISLASTYEARXLS.equals(exportType)){
                    entity9=new TitleEntity(String.valueOf(tId),sId,"同比分析",null);
                }else{
                    //环比
                    entity9=new TitleEntity(String.valueOf(tId),sId,"环比分析",null);
                }
                //保存2级表头ID-同比/环比分析
                String fourthId = String.valueOf(tId);
                tId++;
                TitleEntity entity10=new TitleEntity(String.valueOf(tId),fourthId,"开台数","stationsNumPercentage"+indexStr);
                tId++;
                TitleEntity entity11=new TitleEntity(String.valueOf(tId),fourthId,"客流","customNumPercentage"+indexStr);
                tId++;
                TitleEntity entity12=new TitleEntity(String.valueOf(tId),fourthId,"营业额","turnoverPercentage"+indexStr);
                tId++;
                titleList.add(entity);titleList.add(entity0);titleList.add(entity1);titleList.add(entity2);titleList.add(entity3);
                titleList.add(entity4);titleList.add(entity5);titleList.add(entity6);titleList.add(entity7);titleList.add(entity8);
                titleList.add(entity9);titleList.add(entity10);titleList.add(entity11);titleList.add(entity12);
                DeskTypeAnalysisVo vo;
                Map<String,Object> mapNext;
                for(int i = 0; i < analysisVoList.size(); i++){
                    vo = analysisVoList.get(i);
                    //第一个map时，需加入’人数区间‘
                    if(Constant.Number.ZERO == mapIndex){
                        Map m = new HashMap<String,Object>();
                        m.put("deskTypeName",vo.getDeskTypeName());
                        setMap(df,df2, indexStr, vo, m);
                        rowList.add(m);
                    }else{
                        //从第二个map开始，值添加到第一个map里
                        mapNext = rowList.get(i);
                        setMap(df,df2, indexStr, vo, mapNext);
                    }
                }
                mapIndex++;
            }
            exportMethod(response, titleList, rowList);
        }
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/9 11:28
     * @Param [str]
     * @return int
     * @Description  根据台型名称排序
     */
    public int getNumByString(String str){
        Matcher m = Pattern.compile("\\d+").matcher(str);
        int currentWind = ReportDataConstant.SortNum.NO_NUMBER;
        while(m.find()){
            currentWind = Integer.parseInt(m.group());
        }
        if(str.equals(ReportDataConstant.ReportConstant.COUNT)){
            currentWind = ReportDataConstant.SortNum.TOTAL;
        }
        return currentWind;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/9 10:13
     * @Param [response, titleList, rowList]
     * @return void
     * @Description  导出通用
     */
    @Override
    public void exportMethod(HttpServletResponse response, List<TitleEntity> titleList, List<Map<String, Object>> rowList) {
        ExcelTool excelTool = new ExcelTool(ReportDataConstant.ExcelExportInfo.DESKANALYSIS, 20, 20);
        try {
            response.setContentType("application/octet-stream;");
            response.addHeader("Content-Disposition", "attachment;filename=" + System.currentTimeMillis() + ".xlsx");
            BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
            List<Column> titleData = excelTool.columnTransformer(titleList, "t_id", "t_pid", "t_content", "t_fieldName", "0");
            excelTool.exportExcel(titleData, rowList, output, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/9 10:12
     * @Param [df, df2, indexStr, vo, m]
     * @return void
     * @Description 转换
     */
    private void setDeskAnalysisExcelData(DecimalFormat df, DecimalFormat df2, String indexStr, DeskTypeAnalysisVo vo, Map m) {
        //值为零时 转换为 ‘-'
        m.put("deskNum" + indexStr, getNotZeroByConvert(vo.getDeskNum(), df2));
        m.put("stationsNum" + indexStr, getNotZeroByConvert(vo.getStationsNum(), df2));
        m.put("customNum" + indexStr, getNotZeroByConvert(vo.getCustomNum(), df2));
        m.put("turnover" + indexStr, getNotZeroByConvert(vo.getTurnover(), df));
        //值为零时 转换为 ‘-' 不为0时，值加百分号、
        m.put("deskNumPer" + indexStr, getStringByConvert(vo.getDeskNumPer(), df));
        m.put("stationsNumPer" + indexStr, getStringByConvert(vo.getStationsNumPer(), df));
        m.put("customNumPer" + indexStr, getStringByConvert(vo.getCustomNumPer(), df));
        m.put("turnoverPer" + indexStr, getStringByConvert(vo.getTurnoverPer(), df));
    }


    /**
     * @Author ZhuHC
     * @Date  2020/3/6 15:45
     * @Param [excelExportDto, titleList]
     * @return void
     * @Description 设置 查询条件 excel数据
     */
    private List<TitleEntity> setQueryInfoTitle(ExcelExportDto excelExportDto) {
        List<TitleEntity> titleList=new ArrayList<>();
        //空白
        TitleEntity titleEntity0=new TitleEntity("0",null,null,null);
        //查询条件-菜单
        TitleEntity titleEntity1=new TitleEntity("1","0",excelExportDto.getMenuName(),null);
        //组织
        TitleEntity titleEntity2=new TitleEntity("2","1", ReportDataConstant.ExcelExportInfo.ORGNAME+ Constant.Character.COLON+excelExportDto.getOrgTree(),null);
        //期间
        TitleEntity titleEntity3=new TitleEntity("3","2",ReportDataConstant.ExcelExportInfo.DATEPERIOD+Constant.Character.COLON+ DateUtils.dateConvertString(excelExportDto.getBeginDate(),DateUtils.PATTERN_DAY)+Constant.Character.MIDDLE_WAVE
                +DateUtils.dateConvertString(excelExportDto.getEndDate(),DateUtils.PATTERN_DAY),null);
        //店铺类型
        TitleEntity titleEntity4=new TitleEntity("4","3",ReportDataConstant.ExcelExportInfo.SHOPTYPE+Constant.Character.COLON+excelExportDto.getShopTypeName(),null);
        //下载时间
        TitleEntity titleEntity5=new TitleEntity("5","4",ReportDataConstant.ExcelExportInfo.DOWNLOAD_TIME+Constant.Character.COLON+DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND),null);
        //空白
        TitleEntity titleEntity7=new TitleEntity("6","5",null,null);
        TitleEntity titleEntity8=new TitleEntity("7","6",null,null);
        TitleEntity titleEntity9=new TitleEntity("8","7",null,null);
        //人数区间
        TitleEntity titleEntity6=new TitleEntity("9","8","人数区间","deskTypeName");
        titleList.add(titleEntity0);
        titleList.add(titleEntity1);
        titleList.add(titleEntity2);
        titleList.add(titleEntity3);
        titleList.add(titleEntity4);
        titleList.add(titleEntity5);
        titleList.add(titleEntity6);
        titleList.add(titleEntity7);
        titleList.add(titleEntity8);
        titleList.add(titleEntity9);
        return titleList;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/6 15:46
     * @Param [excelExportDto]
     * @return com.njwd.entity.reportdata.dto.DeskTypeAnalysisDto
     * @Description 设置台型查询条件
     */
    private DeskTypeAnalysisDto setDeskAnalysisQueryDto(ExcelExportDto excelExportDto) {
        DeskTypeAnalysisDto deskTypeAnalysisDto = new DeskTypeAnalysisDto();
        deskTypeAnalysisDto.setShopIdList(excelExportDto.getShopIdList());
        deskTypeAnalysisDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        deskTypeAnalysisDto.setEnteId(excelExportDto.getEnteId());
        deskTypeAnalysisDto.setStartDate(excelExportDto.getBeginDate());
        deskTypeAnalysisDto.setEndDate(excelExportDto.getEndDate());
        deskTypeAnalysisDto.setDateType(excelExportDto.getDateType());
        return deskTypeAnalysisDto;
    }


    /**
     * @Author ZhuHC
     * @Date  2020/3/5 16:01
     * @Param [df, indexStr, vo, mapNext]
     * @return void
     * @Description 给表格数据赋值
     */
    private void setMap(DecimalFormat df,DecimalFormat df2, String indexStr, DeskTypeAnalysisVo vo, Map<String, Object> mapNext) {
        //值为零时 转换为 ‘-'
        mapNext.put("deskNum" + indexStr, getNotZeroByConvert(vo.getDeskNum(), df2));
        mapNext.put("stationsNum" + indexStr, getNotZeroByConvert(vo.getStationsNum(), df2));
        mapNext.put("customNum" + indexStr, getNotZeroByConvert(vo.getCustomNum(), df2));
        mapNext.put("turnover" + indexStr, getNotZeroByConvert(vo.getTurnover(), df));
        mapNext.put("lastStationsNum" + indexStr, getNotZeroByConvert(vo.getLastStationsNum(), df2));
        mapNext.put("lastCustomNum" + indexStr, getNotZeroByConvert(vo.getLastCustomNum(), df2));
        mapNext.put("lastTurnover" + indexStr, getNotZeroByConvert(vo.getLastTurnover(), df));
        //值为零时 转换为 ‘-' 不为0时，值加百分号、
        mapNext.put("stationsNumPercentage" + indexStr, getStringByConvert(vo.getStationsNumPercentage(), df));
        mapNext.put("customNumPercentage" + indexStr, getStringByConvert(vo.getCustomNumPercentage(), df));
        mapNext.put("turnoverPercentage" + indexStr, getStringByConvert(vo.getTurnoverPercentage(), df));
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/4 11:48
     * @Param [bigDecimal]
     * @return java.lang.String
     * @Description 百分比为 0.00 时转换为 ’-‘ ，不为时 加百分号 加千分位
     */
    private String getStringByConvert(BigDecimal bigDecimal, DecimalFormat df) {
        String strValue;
        if(null == bigDecimal || bigDecimal.toString().equals(Constant.Character.String_ZEROB)
                || bigDecimal.toString().equals(Constant.Character.String_ZERO)){
            strValue = Constant.Character.MIDDLE_LINE;
        }else{
            strValue = df.format(bigDecimal) + Constant.Character.Percent;
        }
        return strValue;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/5 15:11
     * @Param [value, df]
     * @return java.lang.String
     * @Description 为 0 时转换为 ’-‘ ，不为时  加千分位
     */
    private String getNotZeroByConvert(Object value, DecimalFormat df) {
        if(null == value){
            return Constant.Character.MIDDLE_LINE;
        }
        String strValue;
        if(Constant.Character.String_ZERO.equals(String.valueOf(value))){
            strValue = Constant.Character.MIDDLE_LINE;
        }else{
            strValue = df.format(value);
        }
        return strValue;
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2019/11/26 10:17
     * @Param [deskTypeAnalysisNow, deskTypeAnalysisLastYear]
     * @Description 处理本期上期数据，计算增长百分比
     */
    private void dealCurrentAndLastAnalysis(List<DeskTypeAnalysisVo> deskTypeAnalysisNow, List<DeskTypeAnalysisVo> deskTypeAnalysisLastYear) {
        //匹配到的上期数据
        BigDecimal newNumber;
        BigDecimal oldNumber;
        if (!FastUtils.checkNullOrEmpty(deskTypeAnalysisNow) && !FastUtils.checkNullOrEmpty(deskTypeAnalysisLastYear)) {
            for (int i = 0; i < deskTypeAnalysisNow.size(); i++) {
                DeskTypeAnalysisVo newVo = deskTypeAnalysisNow.get(i);
                DeskTypeAnalysisVo lastVo = deskTypeAnalysisLastYear.get(i);
                //上期桌数
                newVo.setLastDeskNum(lastVo.getDeskNum());
                //上期客流
                newVo.setLastCustomNum(lastVo.getCustomNum());
                //开台数
                newVo.setLastStationsNum(lastVo.getStationsNum());
                //上期营业额
                newVo.setLastTurnover(lastVo.getTurnover());
                if(!Constant.CruiseDesk.TOTAL_NUMBER_ID.equals(newVo.getDeskTypeId())){
                    //客流百分比 （本期或者上期没数据时，都不计算增长）
                    if (StringUtil.isNotBlank(newVo.getLastCustomNum()) && !Constant.Number.ZERO.equals(newVo.getLastCustomNum())) {
                        if (StringUtil.isBlank(newVo.getCustomNum())) {
                            newVo.setCustomNum(Constant.Number.ZERO);
                        }
                        newNumber = BigDecimal.valueOf(newVo.getCustomNum());
                        oldNumber = BigDecimal.valueOf(newVo.getLastCustomNum());
                        newVo.setCustomNumPercentage(newNumber.subtract(oldNumber)
                                .divide(oldNumber, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                                .multiply(Constant.Number.HUNDRED)
                                .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
                    }
                    //开台数百分比
                    if (StringUtil.isNotBlank(newVo.getLastStationsNum()) && !Constant.Number.ZERO.equals(newVo.getLastStationsNum())) {
                        if (StringUtil.isBlank(newVo.getStationsNum())) {
                            newVo.setStationsNum(Constant.Number.ZERO);
                        }
                        newNumber = BigDecimal.valueOf(newVo.getStationsNum());
                        oldNumber = BigDecimal.valueOf(newVo.getLastStationsNum());
                        newVo.setStationsNumPercentage(newNumber.subtract(oldNumber)
                                .divide(oldNumber, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                                .multiply(Constant.Number.HUNDRED)
                                .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
                    }
                    //营业额百分比
                    if (StringUtil.isNotBlank(newVo.getLastTurnover()) && !Constant.Number.ZEROB.equals(newVo.getLastTurnover())) {
                        if (StringUtil.isBlank(newVo.getTurnover())) {
                            newVo.setTurnover(Constant.Number.ZEROB);
                        }
                        newNumber = newVo.getTurnover();
                        oldNumber = newVo.getLastTurnover();
                        newVo.setTurnoverPercentage(newNumber.subtract(oldNumber)
                                .divide(oldNumber, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                                .multiply(Constant.Number.HUNDRED)
                                .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
                    }
                }
            }
        }
    }

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>
     * @Author ZhuHC
     * @Date 2019/11/25 9:53
     * @Param [deskTypeAnalysisDto]
     * @Description 桌数 与  客流，营业额等数据拼接
     */
    private List<DeskTypeAnalysisVo> getDeskTypeAnalysisInfo(List<DeskTypeAnalysisVo> deskTypeAnalysisVoInfo, List<DeskTypeAnalysisVo> deskNumByTypeList) {
        // 数据拼接到主体数据上
        if (!FastUtils.checkNullOrEmpty(deskNumByTypeList)) {
            MergeUtil.merge(deskNumByTypeList, deskTypeAnalysisVoInfo,
                    (num, info) -> info.getDeskAreaTypeNo().equals(num.getDeskAreaTypeNo()) && info.getDeskTypeName().equals(num.getDeskTypeName()),
                    (num, info) -> {
                        num.setDeskAreaTypeName(info.getDeskAreaTypeName());
                        num.setDeskTypeName(info.getDeskTypeName());
                        num.setStationsNum(info.getStationsNum());
                        num.setCustomNum(info.getCustomNum());
                        num.setTurnover(info.getTurnover());
                    }
            );
            //合计 开台数;客流;营业额;桌数
            DeskTypeAnalysisVo totalSum = new DeskTypeAnalysisVo();
            //开台数
            Integer stationsNum = Constant.Number.ZERO;
            //客流
            Integer customNum = Constant.Number.ZERO;
            //营业额
            BigDecimal turnover = Constant.Number.ZEROB;
            //桌数
            Integer deskNum = Constant.Number.ZERO;
            DeskTypeAnalysisVo vo;
            DeskTypeAnalysisVo nextVo;
            //存储 台型统计数据
            List<DeskTypeAnalysisVo> deskTypeAnalysisVoList = new LinkedList<>();
            deskTypeAnalysisVoList.addAll(deskNumByTypeList);
            for (int i = 0; i < deskTypeAnalysisVoList.size(); i++) {
                vo = deskTypeAnalysisVoList.get(i);
                stationsNum = stationsNum + (StringUtil.isBlank(vo.getStationsNum()) ? Constant.Number.ZERO : vo.getStationsNum());
                customNum = customNum + (StringUtil.isBlank(vo.getCustomNum()) ? Constant.Number.ZERO : vo.getCustomNum());
                turnover = turnover.add(StringUtil.isBlank(vo.getTurnover()) ? Constant.Number.ZEROB : vo.getTurnover());
                deskNum = deskNum + (StringUtil.isBlank(vo.getDeskNum()) ? Constant.Number.ZERO : vo.getDeskNum());
                if (i == (deskTypeAnalysisVoList.size() - 1)) {
                    totalSum.setStationsNum(stationsNum);
                    totalSum.setCustomNum(customNum);
                    totalSum.setTurnover(turnover);
                    totalSum.setDeskNum(deskNum);
                    totalSum.setDeskAreaTypeName(vo.getDeskAreaTypeName());
                    totalSum.setDeskAreaTypeNo(vo.getDeskAreaTypeNo());
                    totalSum.setDeskTypeName(Constant.CruiseDesk.TOTAL_NUMBER);
                    totalSum.setDeskTypeId(Constant.CruiseDesk.TOTAL_NUMBER_ID);
                    deskNumByTypeList.add(totalSum);
                } else {
                    nextVo = deskTypeAnalysisVoList.get(i + 1);
                    if (!vo.getDeskAreaTypeNo().equals(nextVo.getDeskAreaTypeNo())) {
                        totalSum.setStationsNum(stationsNum);
                        totalSum.setCustomNum(customNum);
                        totalSum.setTurnover(turnover);
                        totalSum.setDeskNum(deskNum);
                        totalSum.setDeskAreaTypeName(vo.getDeskAreaTypeName());
                        totalSum.setDeskAreaTypeNo(vo.getDeskAreaTypeNo());
                        totalSum.setDeskTypeName(Constant.CruiseDesk.TOTAL_NUMBER);
                        totalSum.setDeskTypeId(Constant.CruiseDesk.TOTAL_NUMBER_ID);
                        deskNumByTypeList.add(totalSum);
                        stationsNum = Constant.Number.ZERO;
                        customNum = Constant.Number.ZERO;
                        turnover = Constant.Number.ZEROB;
                        deskNum = Constant.Number.ZERO;
                    }
                }
            }
        }
        Collections.sort(deskNumByTypeList, (a, b) -> getNumByString(a.getDeskTypeName()) - (getNumByString(b.getDeskTypeName())));
        return deskNumByTypeList;
    }

    /**
     * @return com.njwd.entity.reportdata.dto.BaseYearMomThresholdDto
     * @Author ZhuHC
     * @Date 2020/1/11 19:49
     * @Param [deskTypeAnalysisDto]
     * @Description 设置阀值查询条件
     */
    private BaseYearMomThresholdDto getBaseYearMomThresholdDto(DeskTypeAnalysisDto deskTypeAnalysisDto) {
        BaseYearMomThresholdDto dto = new BaseYearMomThresholdDto();
        dto.setEnteId(deskTypeAnalysisDto.getEnteId());
        dto.setStartTime(deskTypeAnalysisDto.getStartDate());
        dto.setEndTime(deskTypeAnalysisDto.getEndDate());
        return dto;
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/1/11 20:46
     * @Param [deskTypeAnalysisNow, threshold]
     * @Description 计算同比/环比是否达到阀值
     */
    private void countThresholdStatus(List<DeskTypeAnalysisVo> deskTypeAnalysisNow, BigDecimal threshold) {
        BigDecimal stationsNumPercentage;
        BigDecimal customNumPercentage;
        BigDecimal turnoverPercentage;
        int flag;
        for (DeskTypeAnalysisVo vo : deskTypeAnalysisNow) {
            stationsNumPercentage = vo.getStationsNumPercentage();
            customNumPercentage = vo.getCustomNumPercentage();
            turnoverPercentage = vo.getTurnoverPercentage();
            if (StringUtil.isNotBlank(stationsNumPercentage)) {
                //flag = -1,小于；flag = 0,表示等于；flag = 1,大于；
                flag = stationsNumPercentage.compareTo(threshold);
                if (flag > 0) {
                    vo.setStationsNumStatus(Constant.ThresholdCompareStatus.gt);
                } else {
                    vo.setStationsNumStatus(Constant.ThresholdCompareStatus.le);
                }
            }
            if (StringUtil.isNotBlank(customNumPercentage)) {
                flag = customNumPercentage.compareTo(threshold);
                if (flag > 0) {
                    vo.setCustomNumNumStatus(Constant.ThresholdCompareStatus.gt);
                } else {
                    vo.setCustomNumNumStatus(Constant.ThresholdCompareStatus.le);
                }
            }
            if (StringUtil.isNotBlank(turnoverPercentage)) {
                flag = turnoverPercentage.compareTo(threshold);
                if (flag > 0) {
                    vo.setTurnoverNumStatus(Constant.ThresholdCompareStatus.gt);
                } else {
                    vo.setTurnoverNumStatus(Constant.ThresholdCompareStatus.le);
                }
            }
        }
    }

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>
     * @Author ZhuHC
     * @Date 2020/1/11 20:47
     * @Param [deskNumByTypeList]
     * @Description 复制桌台信息
     */
    private List<DeskTypeAnalysisVo> getDeskNumByTypeCopy(List<DeskTypeAnalysisVo> deskNumByTypeList) {
        List<DeskTypeAnalysisVo> lastDeskNumByTypeList = new ArrayList<>();
        for (DeskTypeAnalysisVo vo : deskNumByTypeList) {
            DeskTypeAnalysisVo newVo = new DeskTypeAnalysisVo();
            FastUtils.copyProperties(vo, newVo);
            lastDeskNumByTypeList.add(newVo);
        }
        return lastDeskNumByTypeList;
    }
}

package com.njwd.reportdata.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.vo.BaseUserVo;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.dto.StaffAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.poiexcel.TitleEntity;
import com.njwd.reportdata.mapper.StaffAnalysisMapper;
import com.njwd.reportdata.service.BaseShopService;
import com.njwd.reportdata.service.DeskTypeAnalysisService;
import com.njwd.reportdata.service.StaffAnalysisService;
import com.njwd.service.FileService;
import com.njwd.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 人资分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Service
public class StaffAnalysisServiceImpl implements StaffAnalysisService {

    @Autowired
    private StaffAnalysisMapper staffAnalysisMapper;
    @Autowired
    private DeskTypeAnalysisService deskTypeAnalysisService;
    @Autowired
    private FileService fileService;
    @Resource
    private BaseShopService baseShopService;


    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.StaffOnJobAnalysisVo>
     * @Author ZhuHC
     * @Date 2020/2/7 17:52
     * @Param [queryDto]
     * @Description 在职员工分析
     */
    @Override
    public List<StaffAnalysisVo> staffOnJobAnalysis(StaffAnalysisDto queryDto) {
        //获取查询时间内所有在职员工
        List<BaseUserVo> onJobStaffList = staffAnalysisMapper.findOnJobStaffList(queryDto);
        //根据门店 区域 品牌 处理过的在职员工数据
        List<StaffAnalysisVo> staffOnJobAnalysisVoList = new LinkedList<>();
        //校验数据
        if (!FastUtils.checkNullOrEmpty(onJobStaffList)) {
            //统计员工 工龄 、是否当期入职、年龄
            getCountInfo(queryDto, onJobStaffList);
            //根据门店 将职工分类  map键值为 门店ID+"-"+门店名称+区域ID+"-"+区域名称+“-”+品牌ID+"-"+品牌名称
            Map<String, List<BaseUserVo>> userVoListByShopMap = onJobStaffList.stream().collect(Collectors.groupingBy(vos -> vos.getShopId()
                    + Constant.Character.MIDDLE_LINE + vos.getShopName() + Constant.Character.MIDDLE_LINE + vos.getRegionId() + Constant.Character.MIDDLE_LINE
                    + vos.getRegionName() + Constant.Character.MIDDLE_LINE + vos.getBrandId() + Constant.Character.MIDDLE_LINE + vos.getBrandName()));
            //门店维度的 在职员工数据
            getStaffOnJobList(staffOnJobAnalysisVoList, userVoListByShopMap,queryDto.getEnteId());
            //根据区域 将职工数据分类
            Map<String, List<StaffAnalysisVo>> voListByRegionMap = staffOnJobAnalysisVoList.stream().collect(Collectors.groupingBy(t -> t.getRegionId()));
            //type 区域
            List<StaffAnalysisVo> regionStaffList = getStaffInfoList(voListByRegionMap, ReportDataConstant.Finance.TYPE_REGION);
            //品牌 职工数据
            Map<String, List<StaffAnalysisVo>> voListByBrandMap = staffOnJobAnalysisVoList.stream().collect(Collectors.groupingBy(t -> t.getBrandId()));
            //type 品牌
            List<StaffAnalysisVo> brandStaffList = getStaffInfoList(voListByBrandMap, ReportDataConstant.Finance.TYPE_BRAND);
            //全部 职工数据
            Map<String, List<StaffAnalysisVo>> voListByAllMap = staffOnJobAnalysisVoList.stream().collect(Collectors.groupingBy(t -> t.getEnteId()));
            //type 全部
            List<StaffAnalysisVo> allStaffList = getStaffInfoList(voListByAllMap, ReportDataConstant.Finance.TYPE_ALL);
            staffOnJobAnalysisVoList.addAll(regionStaffList);
            staffOnJobAnalysisVoList.addAll(brandStaffList);
            staffOnJobAnalysisVoList.addAll(allStaffList);
        }
        return staffOnJobAnalysisVoList;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 14:28
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.StaffAnalysisVo>
     * @Description 离职人员分析
     */
    @Override
    public List<StaffAnalysisVo> separatedStaffAnalysis(StaffAnalysisDto queryDto) {
        //获取查询时间前所有离职员工
        List<BaseUserVo> separatedStaffList = staffAnalysisMapper.findSeparatedStaffList(queryDto);
        //获取查询时间内所有在职员工
        List<BaseUserVo> onJobStaffList = staffAnalysisMapper.findOnJobStaffList(queryDto);
        //根据门店 区域 品牌 处理过的离职员工数据
        List<StaffAnalysisVo> separatedStaffAnalysisVoList = new LinkedList<>();
        if (!FastUtils.checkNullOrEmpty(separatedStaffList)) {
            Date beginDate = queryDto.getBeginDate();
            Date endDate = queryDto.getEndDate();
            //统计员工 工龄 、是否当期离职
            separatedStaffList.stream().forEach(vo -> {
                //入职时间
                Date hireDate = vo.getHireDate();
                //离职时间
                Date leaveDate = vo.getLeaveDate();
                //工龄  -- 月份
                Integer monthsDiff = DateUtils.betweenMonth(leaveDate, hireDate);
                Integer standingType = getStandingType(monthsDiff);
                vo.setStandingType(standingType);
                //当期离职分析- 是否当期离职  0否 1是
                if (null != hireDate) {
                    if (leaveDate.getTime() >= beginDate.getTime() && leaveDate.getTime() <= endDate.getTime()) {
                        vo.setIsCurrentEntry(Constant.Is.YES_INT);
                    } else {
                        vo.setIsCurrentEntry(Constant.Is.NO_INT);
                    }
                }
            });
            //根据门店 将职工分类  map键值为 门店ID+"-"+门店名称+区域ID+"-"+区域名称+“-”+品牌ID+"-"+品牌名称
            Map<String, List<BaseUserVo>> userVoListByShopMap = separatedStaffList.stream().collect(Collectors.groupingBy(vos -> vos.getShopId()
                    + Constant.Character.MIDDLE_LINE + vos.getShopName() + Constant.Character.MIDDLE_LINE + vos.getRegionId() + Constant.Character.MIDDLE_LINE
                    + vos.getRegionName() + Constant.Character.MIDDLE_LINE + vos.getBrandId() + Constant.Character.MIDDLE_LINE + vos.getBrandName()));
            //根据门店 将职工分类  map键值为 门店ID
            Map<String, List<BaseUserVo>> staffListByShopMap = onJobStaffList.stream().collect(Collectors.groupingBy(vos -> vos.getShopId()));
            //范围内总在职人数
            List<BaseUserVo> userVoList;
            String enteId = queryDto.getEnteId();
            //门店维度的 离职员工数据 -计算 占比等各项数据
            for (Map.Entry<String, List<BaseUserVo>> entry : userVoListByShopMap.entrySet()) {
                userVoList = entry.getValue();
                StaffAnalysisVo staffAnalysisVo = new StaffAnalysisVo();
                //根据map键值获得 门店 区域 品牌 ID和名称
                String[] keyValue = entry.getKey().split(Constant.Character.MIDDLE_LINE);
                //类型 类型 shop 为门店 brand 品牌 region区域
                staffAnalysisVo.setType(ReportDataConstant.Finance.TYPE_SHOP);
                staffAnalysisVo.setShopId(keyValue[Constant.Number.ZERO]);
                staffAnalysisVo.setShopName(keyValue[Constant.Number.ONE]);
                staffAnalysisVo.setRegionId(keyValue[Constant.Number.TWO]);
                staffAnalysisVo.setRegionName(keyValue[Constant.Number.THREE]);
                staffAnalysisVo.setBrandId(keyValue[Constant.Number.FOUR]);
                staffAnalysisVo.setBrandName(keyValue[Constant.Number.FIVE]);
                staffAnalysisVo.setEnteId(enteId);
                //范围内离职总人数
                BigDecimal allStaff = new BigDecimal(userVoList.size());
                staffAnalysisVo.setStaffNum(allStaff);
                //范围内在职人数
                BigDecimal staffOnJob = new BigDecimal(staffListByShopMap.get(staffAnalysisVo.getShopId()).size());
                //根据男女分组
                setGroupBySex(userVoList, staffAnalysisVo, allStaff);
                //工龄分析
                setGroupByStandingType(userVoList, staffAnalysisVo, allStaff);
                //岗位分析-根据是否管理岗分组
                setGroupByIsManager(userVoList, staffAnalysisVo, allStaff);
                //当期离职分析
                setGroupByIsCurrent(userVoList,staffAnalysisVo,staffOnJob);
                //数据添加进list
                separatedStaffAnalysisVoList.add(staffAnalysisVo);
            }
            //根据区域 将在职职工数据分类
            Map<String, List<BaseUserVo>> staffByRegionMap = onJobStaffList.stream().collect(Collectors.groupingBy(t -> t.getRegionId()));
            //根据区域 将职工数据分类
            Map<String, List<StaffAnalysisVo>> voListByRegionMap = separatedStaffAnalysisVoList.stream().collect(Collectors.groupingBy(t -> t.getRegionId()));
            //type 区域
            List<StaffAnalysisVo> regionStaffList = getSeparatedStaffInfoList(voListByRegionMap,staffByRegionMap, ReportDataConstant.Finance.TYPE_REGION);
            //品牌 在职职工数据
            Map<String, List<BaseUserVo>> staffByBrandMap = onJobStaffList.stream().collect(Collectors.groupingBy(t -> t.getBrandId()));
            //品牌 职工数据
            Map<String, List<StaffAnalysisVo>> voListByBrandMap = separatedStaffAnalysisVoList.stream().collect(Collectors.groupingBy(t -> t.getBrandId()));
            //type 品牌
            List<StaffAnalysisVo> brandStaffList = getSeparatedStaffInfoList(voListByBrandMap,staffByBrandMap, ReportDataConstant.Finance.TYPE_BRAND);
            //全部 职工数据
            Map<String, List<BaseUserVo>> staffByAllMap = onJobStaffList.stream().collect(Collectors.groupingBy(t -> t.getEnteId()));
            //全部  在职职工数据
            Map<String, List<StaffAnalysisVo>> voListByAllMap = separatedStaffAnalysisVoList.stream().collect(Collectors.groupingBy(t -> t.getEnteId()));
            //type 全部
            List<StaffAnalysisVo> allStaffList = getSeparatedStaffInfoList(voListByAllMap,staffByAllMap, ReportDataConstant.Finance.TYPE_ALL);
            separatedStaffAnalysisVoList.addAll(regionStaffList);
            separatedStaffAnalysisVoList.addAll(brandStaffList);
            separatedStaffAnalysisVoList.addAll(allStaffList);
        }
        return separatedStaffAnalysisVoList;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/20 15:00
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.StaffWorkHoursVo>
     * @Description 员工工作时长分析表
     */
    @Override
    public List <StaffWorkHoursVo> staffWorkHoursAnalysis(StaffAnalysisDto queryDto) {
        List <StaffWorkHoursVo> resultList = new ArrayList <>();
        MembershipCardAnalysisDto dto = new MembershipCardAnalysisDto();
        dto.setShopIdList(queryDto.getShopIdList());
        dto.setShopTypeIdList(queryDto.getShopTypeIdList());
        dto.setEnteId(queryDto.getEnteId());
        //查询 门店信息
        List<MembershipCardAnalysisVo> voList = baseShopService.findShopDetail(dto);
        if(!FastUtils.checkNullOrEmpty(voList)) {
            //数据类型转换
            List <StaffWorkHoursVo> staffWorkHoursVoList = new ArrayList <>();
            StaffWorkHoursVo workHoursVo;
            for (MembershipCardAnalysisVo vo : voList) {
                workHoursVo = new StaffWorkHoursVo();
                workHoursVo.setShopId(vo.getShopId());
                workHoursVo.setShopName(vo.getShopName());
                workHoursVo.setRegionId(vo.getRegionId());
                workHoursVo.setRegionName(vo.getRegionName());
                workHoursVo.setBrandId(vo.getBrandId());
                workHoursVo.setBrandName(vo.getBrandName());
                workHoursVo.setEnteId(vo.getEnteId());
                staffWorkHoursVoList.add(workHoursVo);
            }
            List <StaffWorkHoursVo> workHoursVoList = staffAnalysisMapper.findStaffWorkHoursList(queryDto);
            MergeUtil.merge(staffWorkHoursVoList, workHoursVoList,
                    StaffWorkHoursVo::getShopId, StaffWorkHoursVo::getShopId,
                    (vo, workVo) -> {
                        vo.setWorkHours(workVo.getWorkHours());
                        vo.setPeopleNum(workVo.getPeopleNum());
                    }
            );
            if(!FastUtils.checkNullOrEmpty(staffWorkHoursVoList)){
                //门店平均工作时长
                for(StaffWorkHoursVo vo : staffWorkHoursVoList){
                    vo.setAvgWorkHours(BigDecimalUtils.getDivide(vo.getWorkHours(),BigDecimal.valueOf(vo.getPeopleNum())));
                }
                //全部维度 时长 平均时长
                StaffWorkHoursVo allVo = new StaffWorkHoursVo();
                allVo.setType(ReportDataConstant.Finance.TYPE_ALL);
                allVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
                allVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
                allVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                BigDecimal allWorkHours = staffWorkHoursVoList.stream().map(StaffWorkHoursVo::getWorkHours).reduce(BigDecimal.ZERO, BigDecimal::add);
                Integer allPeopleNum = staffWorkHoursVoList.stream().mapToInt(StaffWorkHoursVo::getPeopleNum).sum();
                allVo.setWorkHours(allWorkHours);
                allVo.setPeopleNum(allPeopleNum);
                allVo.setAvgWorkHours(BigDecimalUtils.getDivide(allWorkHours,BigDecimal.valueOf(allPeopleNum)));
                resultList.add(allVo);
                //集团内 门店排名
                getSortInEnte(staffWorkHoursVoList);
                //按品牌分类 排序
                Map<String, List<StaffWorkHoursVo>> brandMap = staffWorkHoursVoList.stream().collect(Collectors.groupingBy(t -> t.getBrandId()));
                List <StaffWorkHoursVo> brandList = new ArrayList <>();
                List <StaffWorkHoursVo> regionList = new ArrayList <>();
                //门店计算品牌内排名
                brandMap.forEach((k,v) -> {
                    getSortInBrand(v);
                    //品牌维度 时长 平均时长
                    StaffWorkHoursVo brandVo = new StaffWorkHoursVo();
                    brandVo.setType(ReportDataConstant.Finance.TYPE_BRAND);
                    brandVo.setBrandId(v.get(0).getBrandId());
                    brandVo.setBrandName(v.get(0).getBrandName());
                    brandVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
                    brandVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
                    BigDecimal workHours = v.stream().map(StaffWorkHoursVo::getWorkHours).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Integer peopleNum = v.stream().mapToInt(StaffWorkHoursVo::getPeopleNum).sum();
                    brandVo.setWorkHours(workHours);
                    brandVo.setPeopleNum(peopleNum);
                    brandVo.setAvgWorkHours(BigDecimalUtils.getDivide(workHours,BigDecimal.valueOf(peopleNum)));
                    brandList.add(brandVo);
                });
                //品牌维度 按 集团排名
                getSortInEnte(brandList);
                //品牌汇总 加入集合中
                resultList.addAll(brandList);
                //按区域分类 排序
                Map<String, List<StaffWorkHoursVo>> regionMap = staffWorkHoursVoList.stream().collect(Collectors.groupingBy(t -> t.getRegionId()));
                //门店计算区域内排名
                regionMap.forEach((k,v) -> {
                    v.sort(Comparator.comparing(StaffWorkHoursVo::getAvgWorkHours).reversed());
                    //名次
                    int sortNum = Constant.Number.ONE;
                    StaffWorkHoursVo vo;
                    StaffWorkHoursVo nextVo;
                    for(int i = 0; i < v.size(); i++){
                        vo = v.get(i);
                        vo.setSortRegion(sortNum);
                        if( i < v.size() - 1){
                            nextVo = v.get(i+1);
                            //平均时长一样时，排名不变；否则+1
                            if(!ReportDataConstant.BigdecimalCompare.EQUALS .equals(vo.getAvgWorkHours().compareTo(nextVo.getAvgWorkHours()))){
                                sortNum = i+2;
                            }
                        }
                    }
                    //区域维度 时长 平均时长
                    StaffWorkHoursVo regionVo = new StaffWorkHoursVo();
                    regionVo.setType(ReportDataConstant.Finance.TYPE_REGION);
                    regionVo.setBrandId(v.get(0).getBrandId());
                    regionVo.setBrandName(v.get(0).getBrandName());
                    regionVo.setRegionId(v.get(0).getRegionId());
                    regionVo.setRegionName(v.get(0).getRegionName());
                    regionVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
                    BigDecimal workHours = v.stream().map(StaffWorkHoursVo::getWorkHours).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Integer peopleNum = v.stream().mapToInt(StaffWorkHoursVo::getPeopleNum).sum();
                    regionVo.setWorkHours(workHours);
                    regionVo.setPeopleNum(peopleNum);
                    regionVo.setAvgWorkHours(BigDecimalUtils.getDivide(workHours,BigDecimal.valueOf(peopleNum)));
                    regionList.add(regionVo);
                });
                //区域维度 按 集团排名
                getSortInEnte(regionList);
                //区域维度按品牌分类 排序
                Map<String, List<StaffWorkHoursVo>> brandRegionMap = regionList.stream().collect(Collectors.groupingBy(t -> t.getBrandId()));
                //区域计算品牌内排名
                brandRegionMap.forEach((k,v) -> {
                    getSortInBrand(v);
                });
                //区域汇总 加入集合中
                resultList.addAll(regionList);
                //门店汇总 加入集合中
                resultList.addAll(staffWorkHoursVoList);
            }
        }
        return resultList;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/24 10:44
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.EfficiencyPerCapitaVo>
     * @Description 人均效率分析表
     */
    @Override
    public List <EfficiencyPerCapitaVo> efficiencyPerCapitaAnalysis(StaffAnalysisDto queryDto) {
        List<EfficiencyPerCapitaVo> perVos = new ArrayList <>();
        MembershipCardAnalysisDto dto = new MembershipCardAnalysisDto();
        dto.setShopIdList(queryDto.getShopIdList());
        dto.setShopTypeIdList(queryDto.getShopTypeIdList());
        dto.setEnteId(queryDto.getEnteId());
        //查询 门店信息
        List<MembershipCardAnalysisVo> voList = baseShopService.findShopDetail(dto);
        if(!FastUtils.checkNullOrEmpty(voList)){
            //数据类型转换
            List<EfficiencyPerCapitaVo> efficiencyList = new ArrayList <>();
            EfficiencyPerCapitaVo efficiency;
            for(MembershipCardAnalysisVo vo : voList){
                efficiency = new EfficiencyPerCapitaVo();
                efficiency.setShopId(vo.getShopId());
                efficiency.setShopName(vo.getShopName());
                efficiency.setRegionId(vo.getRegionId());
                efficiency.setRegionName(vo.getRegionName());
                efficiency.setBrandId(vo.getBrandId());
                efficiency.setBrandName(vo.getBrandName());
                efficiency.setEnteId(vo.getEnteId());
                efficiencyList.add(efficiency);
            }
            //获得门店维度员工效率数据
            getDataInfoList(queryDto, efficiencyList);
            BigDecimal staffNum;
            BigDecimal lastStaffNum;
            BigDecimal backStaffNum;
            BigDecimal lastBackStaffNum;
            //计算本期、上期人均；人数和工作量同比
            for(EfficiencyPerCapitaVo vo : efficiencyList){
                staffNum = BigDecimal.valueOf(vo.getStaffNum());
                lastStaffNum = BigDecimal.valueOf(vo.getLastStaffNum());
                backStaffNum = BigDecimal.valueOf(vo.getBackStaffNum());
                lastBackStaffNum = BigDecimal.valueOf(vo.getLastBackStaffNum());
                //前厅
                vo.setAvgWork(BigDecimalUtils.getDivide(BigDecimal.valueOf(vo.getCustomNum()),staffNum));
                //后厨
                vo.setBackAvgWork(BigDecimalUtils.getDivide(vo.getDishesNum(),backStaffNum));
                //去年
                vo.setLastAvgWork(BigDecimalUtils.getDivide(BigDecimal.valueOf(vo.getLastCustomNum()),lastStaffNum));
                vo.setLastBackAvgWork(BigDecimalUtils.getDivide(vo.getLastDishesNum(),lastBackStaffNum));
                //员工人数同比
                vo.setStaffNumPercentage(BigDecimalUtils.getPercent(staffNum.subtract(lastStaffNum),lastStaffNum));
                vo.setBackStaffNumPercentage(BigDecimalUtils.getPercent(backStaffNum.subtract(lastBackStaffNum),lastBackStaffNum));
                //工作量同比
                vo.setAvgWorkPercentage(BigDecimalUtils.getPercent(vo.getAvgWork().subtract(vo.getLastAvgWork()),vo.getLastAvgWork()));
                vo.setBackAvgWorkPercentage(BigDecimalUtils.getPercent(vo.getBackAvgWork().subtract(vo.getLastBackAvgWork()),vo.getLastBackAvgWork()));
            }
            //区域
            Map<String, List<EfficiencyPerCapitaVo>> regionAnalysisMap = efficiencyList.stream().collect(Collectors.groupingBy(t -> t.getRegionId()));
            //type 区域
            List<EfficiencyPerCapitaVo> regionAnalysisList = getAnalysisList(regionAnalysisMap, ReportDataConstant.Finance.TYPE_REGION);
            //品牌
            Map<String, List<EfficiencyPerCapitaVo>> brandAnalysisMap = efficiencyList.stream().collect(Collectors.groupingBy(t -> t.getBrandId()));
            //type 品牌
            List<EfficiencyPerCapitaVo> brandAnalysisList = getAnalysisList(brandAnalysisMap, ReportDataConstant.Finance.TYPE_BRAND);
            //全部
            Map<String, List<EfficiencyPerCapitaVo>> allAnalysisMap = efficiencyList.stream().collect(Collectors.groupingBy(t -> t.getEnteId()));
            //type 全部
            List<EfficiencyPerCapitaVo> allAnalysisList = getAnalysisList(allAnalysisMap, ReportDataConstant.Finance.TYPE_ALL);
            perVos.addAll(efficiencyList);
            perVos.addAll(regionAnalysisList);
            perVos.addAll(brandAnalysisList);
            perVos.addAll(allAnalysisList);
        }
        return perVos;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/30 9:59
     * @Param [excelExportDto, response]
     * @return void
     * @Description 人均效率导出
     */
    @Override
    public void exportEfficiencyPerAnalysis(ExcelExportDto excelExportDto, HttpServletResponse response) {
        StaffAnalysisDto queryDto = getStaffAnalysisDto(excelExportDto);
        List <EfficiencyPerCapitaVo> efficiencyVos = efficiencyPerCapitaAnalysis(queryDto);
        //表头
        List<TitleEntity> titleList=setQueryInfoTitle(excelExportDto);
        //下载时间的 ID 作为以下标题的根ID
        int tId = Constant.Number.EIGHT;
        String pId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity=new TitleEntity(String.valueOf(tId),pId,"品牌","brandName");tId = tId+1;
        TitleEntity entity1=new TitleEntity(String.valueOf(tId),pId,"区域","regionName");tId = tId+1;
        TitleEntity entity2=new TitleEntity(String.valueOf(tId),pId,"门店","shopName");tId = tId+1;
        TitleEntity entity4=new TitleEntity(String.valueOf(tId),pId,"本期",null);
        //本期 根ID
        String currentId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity5=new TitleEntity(String.valueOf(tId),currentId,"前厅",null);
        //本期前厅 根ID
        String curFrontId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity6=new TitleEntity(String.valueOf(tId),curFrontId,"客流人数","customNum");tId = tId+1;
        TitleEntity entity7=new TitleEntity(String.valueOf(tId),curFrontId,"员工人数","staffNum");tId = tId+1;
        TitleEntity entity8=new TitleEntity(String.valueOf(tId),curFrontId,"人均工作量","avgWork");tId = tId+1;
        TitleEntity entity9=new TitleEntity(String.valueOf(tId),currentId,"后厨",null);
        //本期后厨 根ID
        String curBackId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity10=new TitleEntity(String.valueOf(tId),curBackId,"菜品份数","dishesNum");tId = tId+1;
        TitleEntity entity11=new TitleEntity(String.valueOf(tId),curBackId,"员工人数","backStaffNum");tId = tId+1;
        TitleEntity entity12=new TitleEntity(String.valueOf(tId),curBackId,"人均工作量","backAvgWork");tId = tId+1;
        TitleEntity entity13=new TitleEntity(String.valueOf(tId),pId,"上年同期",null);
        //上期 根ID
        String lastId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity14=new TitleEntity(String.valueOf(tId),lastId,"前厅",null);
        //上期前厅 根ID
        String lastFrontId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity15=new TitleEntity(String.valueOf(tId),lastFrontId,"客流人数","lastCustomNum");tId = tId+1;
        TitleEntity entity16=new TitleEntity(String.valueOf(tId),lastFrontId,"员工人数","lastStaffNum");tId = tId+1;
        TitleEntity entity17=new TitleEntity(String.valueOf(tId),lastFrontId,"人均工作量","lastAvgWork");tId = tId+1;
        TitleEntity entity18=new TitleEntity(String.valueOf(tId),lastId,"后厨",null);
        //上期后厨 根ID
        String lastBackId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity19=new TitleEntity(String.valueOf(tId),lastBackId,"菜品份数","lastDishesNum");tId = tId+1;
        TitleEntity entity20=new TitleEntity(String.valueOf(tId),lastBackId,"员工人数","lastBackStaffNum");tId = tId+1;
        TitleEntity entity21=new TitleEntity(String.valueOf(tId),lastBackId,"人均工作量","lastBackAvgWork");tId = tId+1;
        TitleEntity entity22=new TitleEntity(String.valueOf(tId),pId,"较上年人数（百分比）",null);
        //较上年人数 根ID
        String compareNumId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity23=new TitleEntity(String.valueOf(tId),compareNumId,"前厅(%)","staffNumPercentage");tId = tId+1;
        TitleEntity entity24=new TitleEntity(String.valueOf(tId),compareNumId,"后厨(%)","backStaffNumPercentage");tId = tId+1;
        TitleEntity entity25=new TitleEntity(String.valueOf(tId),pId,"较上年工作量（百分比）",null);
        //较上年工作量 根ID
        String compareWorkId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity26=new TitleEntity(String.valueOf(tId),compareWorkId,"前厅(%)","avgWorkPercentage");tId = tId+1;
        TitleEntity entity27=new TitleEntity(String.valueOf(tId),compareWorkId,"后厨(%)","backAvgWorkPercentage");
        titleList.add(entity);titleList.add(entity1);titleList.add(entity2);titleList.add(entity4);titleList.add(entity5);
        titleList.add(entity6);titleList.add(entity7);titleList.add(entity8);titleList.add(entity9);titleList.add(entity10);titleList.add(entity11);
        titleList.add(entity12);titleList.add(entity13);titleList.add(entity14);titleList.add(entity15);titleList.add(entity16);titleList.add(entity17);
        titleList.add(entity18);titleList.add(entity19);titleList.add(entity20);titleList.add(entity21);titleList.add(entity22);titleList.add(entity23);
        titleList.add(entity24);titleList.add(entity25);titleList.add(entity26);titleList.add(entity27);
        List<Map<String,Object>> rowList=new ArrayList<>();
        if(!FastUtils.checkNullOrEmpty(efficiencyVos)){
            //加入千分位，保留2位小数，并且不够补0
            DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
            DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
            List<EfficiencyPerCapitaVo> voList;
            //根据类型过滤
            if (ReportDataConstant.Finance.TYPE_SHOP.equals(excelExportDto.getType())) {
                voList = efficiencyVos.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(excelExportDto.getType())) {
                voList = efficiencyVos.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_REGION.equals(excelExportDto.getType())) {
                voList = efficiencyVos.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
            } else {
                voList = efficiencyVos.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
            }
            for(EfficiencyPerCapitaVo vo : voList){
                Map map = new HashMap<String,Object>();
                map.put("brandName",vo.getBrandName());map.put("regionName",vo.getRegionName());map.put("shopName",vo.getShopName());
                map.put("customNum",getNotZeroByConvert(vo.getCustomNum(), df2));
                map.put("staffNum",getNotZeroByConvert(vo.getStaffNum(),df2));
                map.put("avgWork",getNotZeroByConvert(vo.getAvgWork(),df));
                map.put("dishesNum",getNotZeroByConvert(vo.getDishesNum(),df));
                map.put("backStaffNum",getNotZeroByConvert(vo.getBackStaffNum(),df2));
                map.put("backAvgWork",getNotZeroByConvert(vo.getBackAvgWork(),df));
                map.put("lastCustomNum",getNotZeroByConvert(vo.getLastCustomNum(),df2));
                map.put("lastStaffNum",getNotZeroByConvert(vo.getLastStaffNum(),df2));
                map.put("lastAvgWork",getNotZeroByConvert(vo.getLastAvgWork(),df));
                map.put("lastDishesNum",getNotZeroByConvert(vo.getLastDishesNum(),df));
                map.put("lastBackStaffNum",getNotZeroByConvert(vo.getLastBackStaffNum(), df2));
                map.put("lastBackAvgWork",getNotZeroByConvert(vo.getLastBackAvgWork(),df));
                map.put("staffNumPercentage",getStringByConvert(vo.getStaffNumPercentage(),df));
                map.put("backStaffNumPercentage",getStringByConvert(vo.getBackStaffNumPercentage(),df));
                map.put("avgWorkPercentage",getStringByConvert(vo.getAvgWorkPercentage(),df));
                map.put("backAvgWorkPercentage",getStringByConvert(vo.getBackAvgWorkPercentage(),df));
                rowList.add(map);
            }
        }
        deskTypeAnalysisService.exportMethod(response, titleList, rowList);
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/23 17:42
     * @Param [excelExportDto, response]
     * @return void
     * @Description 员工工作时长导出
     */
    @Override
    public void exportStaffWorkHoursAnalysis(ExcelExportDto excelExportDto, HttpServletResponse response) {
        StaffAnalysisDto queryDto = getStaffAnalysisDto(excelExportDto);
        List<StaffWorkHoursVo> staffWorkHoursVoList = staffWorkHoursAnalysis(queryDto);
        List<StaffWorkHoursVo> voList = new ArrayList <>();
        //根据类型过滤
        if (ReportDataConstant.Finance.TYPE_SHOP.equals(excelExportDto.getType())) {
            voList = staffWorkHoursVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(excelExportDto.getType())) {
            voList = staffWorkHoursVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
        } else if (ReportDataConstant.Finance.TYPE_REGION.equals(excelExportDto.getType())) {
            voList = staffWorkHoursVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
        } else {
            voList = staffWorkHoursVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
        }
        fileService.exportExcelForQueryTerm(response, excelExportDto, voList,
                ExcelColumnConstant.StaffWorkHoursAnalysisInfo.BRANDE_NAME,
                ExcelColumnConstant.StaffWorkHoursAnalysisInfo.REGION_NAME,
                ExcelColumnConstant.StaffWorkHoursAnalysisInfo.SHOP_NAME,
                ExcelColumnConstant.StaffWorkHoursAnalysisInfo.WORK_HOURS,
                ExcelColumnConstant.StaffWorkHoursAnalysisInfo.PEOPLE_NUM,
                ExcelColumnConstant.StaffWorkHoursAnalysisInfo.AVG_WORK_HOURS,
                ExcelColumnConstant.StaffWorkHoursAnalysisInfo.SORT_REGION,
                ExcelColumnConstant.StaffWorkHoursAnalysisInfo.SORT_BRAND,
                ExcelColumnConstant.StaffWorkHoursAnalysisInfo.SORT_ENTE);
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 10:17
     * @Param [excelExportDto, response]
     * @return void
     * @Description 在职人员分析导出
     */
    @Override
    public void exportStaffOnJobAnalysisExcel(ExcelExportDto excelExportDto, HttpServletResponse response) {
        StaffAnalysisDto queryDto = getStaffAnalysisDto(excelExportDto);
        List<StaffAnalysisVo> staffOnJobAnalysisVoList =  staffOnJobAnalysis(queryDto);
        //表头
        List<TitleEntity> titleList=setQueryInfoTitle(excelExportDto);
        //下载时间的 ID 作为以下标题的根ID
        int tId = Constant.Number.EIGHT;
        String pId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity=new TitleEntity(String.valueOf(tId),pId,"品牌","brandName");tId = tId+1;
        TitleEntity entity1=new TitleEntity(String.valueOf(tId),pId,"区域","regionName");tId = tId+1;
        TitleEntity entity2=new TitleEntity(String.valueOf(tId),pId,"门店","shopName");tId = tId+1;
        TitleEntity entity3=new TitleEntity(String.valueOf(tId),pId,"在职人数","staffNum");tId = tId+1;
        TitleEntity entity4=new TitleEntity(String.valueOf(tId),pId,"性别分析",null);
        //性别分析 根ID
        String sexId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity5=new TitleEntity(String.valueOf(tId),sexId,"男性员工占比","manNumPercentage");tId = tId+1;
        TitleEntity entity6=new TitleEntity(String.valueOf(tId),sexId,"女性员工占比","womanNumPercentage");tId = tId+1;
        TitleEntity entity7=new TitleEntity(String.valueOf(tId),pId,"工龄分析",null);
        //工龄分析 根ID
        String standId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity8=new TitleEntity(String.valueOf(tId),standId,"3个月内","leThreeMonthPercentage");tId = tId+1;
        TitleEntity entity9=new TitleEntity(String.valueOf(tId),standId,"6个月内","leSixMonthPercentage");tId = tId+1;
        TitleEntity entity10=new TitleEntity(String.valueOf(tId),standId,"1年内","leOneYearPercentage");tId = tId+1;
        TitleEntity entity11=new TitleEntity(String.valueOf(tId),standId,"3年内","leThreeYearPercentage");tId = tId+1;
        TitleEntity entity12=new TitleEntity(String.valueOf(tId),standId,"5年以上","geFiveYearPercentage");tId = tId+1;
        TitleEntity entity13=new TitleEntity(String.valueOf(tId),pId,"岗位分析",null);
        //岗位分析 根ID
        String roleId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity14=new TitleEntity(String.valueOf(tId),roleId,"管理人员占比","managerNumPercentage");tId = tId+1;
        TitleEntity entity15=new TitleEntity(String.valueOf(tId),roleId,"员工占比","generalStaffNumPercentage");tId = tId+1;
        TitleEntity entity16=new TitleEntity(String.valueOf(tId),pId,"当期入职分析",null);
        //当期入职分析 根ID
        String currentEntryId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity17=new TitleEntity(String.valueOf(tId),currentEntryId,"当期入职人数","currentNum");tId = tId+1;
        TitleEntity entity18=new TitleEntity(String.valueOf(tId),currentEntryId,"当期入职人数占比","currentNumPercentage");tId = tId+1;
        TitleEntity entity19=new TitleEntity(String.valueOf(tId),pId,"年龄占比分析",null);
        //年龄占比分析 根ID
        String ageId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity20=new TitleEntity(String.valueOf(tId),ageId,"16-25","firstKindNumPercentage");tId = tId+1;
        TitleEntity entity21=new TitleEntity(String.valueOf(tId),ageId,"25-35","secondKindNumPercentage");tId = tId+1;
        TitleEntity entity22=new TitleEntity(String.valueOf(tId),ageId,"35-45","thirdKindNumPercentage");tId = tId+1;
        TitleEntity entity23=new TitleEntity(String.valueOf(tId),ageId,"45-55","fourthKindNumPercentage");tId = tId+1;
        TitleEntity entity24=new TitleEntity(String.valueOf(tId),ageId,"55以上","fifthKindNumPercentage");tId = tId+1;
        TitleEntity entity25=new TitleEntity(String.valueOf(tId),pId,"平均年龄","avgAge");tId = tId+1;
        TitleEntity entity26=new TitleEntity(String.valueOf(tId),pId,"学历占比分析",null);
        //学历占比分析 根ID
        String eduLevelId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity27=new TitleEntity(String.valueOf(tId),eduLevelId,"小学","primaryDegreePercentage");tId = tId+1;
        TitleEntity entity28=new TitleEntity(String.valueOf(tId),eduLevelId,"初中","juniorHighDegreePercentage");tId = tId+1;
        TitleEntity entity29=new TitleEntity(String.valueOf(tId),eduLevelId,"高中","highDegreePercentage");tId = tId+1;
        TitleEntity entity30=new TitleEntity(String.valueOf(tId),eduLevelId,"中专","secondaryDegreePercentage");tId = tId+1;
        TitleEntity entity31=new TitleEntity(String.valueOf(tId),eduLevelId,"大专","collegeDegreePercentage");tId = tId+1;
        TitleEntity entity32=new TitleEntity(String.valueOf(tId),eduLevelId,"本科","universityDegreePercentage");tId = tId+1;
        TitleEntity entity33=new TitleEntity(String.valueOf(tId),eduLevelId,"硕士","masterDegreePercentage");tId = tId+1;
        TitleEntity entity34=new TitleEntity(String.valueOf(tId),eduLevelId,"博士","doctoralDegreePercentage");
        titleList.add(entity);titleList.add(entity1);titleList.add(entity2);titleList.add(entity3);titleList.add(entity4);titleList.add(entity5);
        titleList.add(entity6);titleList.add(entity7);titleList.add(entity8);titleList.add(entity9);titleList.add(entity10);titleList.add(entity11);
        titleList.add(entity12);titleList.add(entity13);titleList.add(entity14);titleList.add(entity15);titleList.add(entity16);titleList.add(entity17);
        titleList.add(entity18);titleList.add(entity19);titleList.add(entity20);titleList.add(entity21);titleList.add(entity22);titleList.add(entity23);
        titleList.add(entity24);titleList.add(entity25);titleList.add(entity26);titleList.add(entity27);titleList.add(entity28);titleList.add(entity29);
        titleList.add(entity30);titleList.add(entity31);titleList.add(entity32);titleList.add(entity33);titleList.add(entity34);
        List<Map<String,Object>> rowList=new ArrayList<>();
        if(!FastUtils.checkNullOrEmpty(staffOnJobAnalysisVoList)){
            //加入千分位，保留2位小数，并且不够补0
            DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
            DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
            List<StaffAnalysisVo> voList;
            //根据类型过滤
            if (ReportDataConstant.Finance.TYPE_SHOP.equals(excelExportDto.getType())) {
                voList = staffOnJobAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(excelExportDto.getType())) {
                voList = staffOnJobAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_REGION.equals(excelExportDto.getType())) {
                voList = staffOnJobAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
            } else {
                voList = staffOnJobAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
            }
            for(StaffAnalysisVo vo : voList){
                Map map = new HashMap<String,Object>();
                map.put("brandName",vo.getBrandName());map.put("regionName",vo.getRegionName());map.put("shopName",vo.getShopName());
                map.put("staffNum",getNotZeroByConvert(vo.getStaffNum(), df2));
                map.put("manNumPercentage",getStringByConvert(vo.getManNumPercentage(),df));
                map.put("womanNumPercentage",getStringByConvert(vo.getWomanNumPercentage(),df));
                map.put("leThreeMonthPercentage",getStringByConvert(vo.getLeThreeMonthPercentage(),df));
                map.put("leSixMonthPercentage",getStringByConvert(vo.getLeSixMonthPercentage(),df));
                map.put("leOneYearPercentage",getStringByConvert(vo.getLeOneYearPercentage(),df));
                map.put("leThreeYearPercentage",getStringByConvert(vo.getLeThreeYearPercentage(),df));
                map.put("geFiveYearPercentage",getStringByConvert(vo.getGeFiveYearPercentage(),df));
                map.put("managerNumPercentage",getStringByConvert(vo.getManagerNumPercentage(),df));
                map.put("generalStaffNumPercentage",getStringByConvert(vo.getGeneralStaffNumPercentage(),df));
                map.put("currentNum",getNotZeroByConvert(vo.getCurrentNum(), df2));
                map.put("currentNumPercentage",getStringByConvert(vo.getCurrentNumPercentage(),df));
                map.put("firstKindNumPercentage",getStringByConvert(vo.getFirstKindNumPercentage(),df));
                map.put("secondKindNumPercentage",getStringByConvert(vo.getSecondKindNumPercentage(),df));
                map.put("thirdKindNumPercentage",getStringByConvert(vo.getThirdKindNumPercentage(),df));
                map.put("fourthKindNumPercentage",getStringByConvert(vo.getFourthKindNumPercentage(),df));
                map.put("fifthKindNumPercentage",getStringByConvert(vo.getFifthKindNumPercentage(),df));
                map.put("avgAge",getNotZeroByConvert(vo.getAvgAge(), df));
                map.put("primaryDegreePercentage",getStringByConvert(vo.getPrimaryDegreePercentage(),df));
                map.put("juniorHighDegreePercentage",getStringByConvert(vo.getJuniorHighDegreePercentage(),df));
                map.put("highDegreePercentage",getStringByConvert(vo.getHighDegreePercentage(),df));
                map.put("secondaryDegreePercentage",getStringByConvert(vo.getSecondaryDegreePercentage(),df));
                map.put("collegeDegreePercentage",getStringByConvert(vo.getCollegeDegreePercentage(),df));
                map.put("universityDegreePercentage",getStringByConvert(vo.getUniversityDegreePercentage(),df));
                map.put("masterDegreePercentage",getStringByConvert(vo.getMasterDegreePercentage(),df));
                map.put("doctoralDegreePercentage",getStringByConvert(vo.getDoctoralDegreePercentage(),df));
                rowList.add(map);
            }
        }
        deskTypeAnalysisService.exportMethod(response, titleList, rowList);
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 15:08
     * @Param [excelExportDto, response]
     * @return void
     * @Description 离职人员导出
     */
    @Override
    public void exportSeparatedStaffAnalysisExcel(ExcelExportDto excelExportDto, HttpServletResponse response) {
        StaffAnalysisDto queryDto = getStaffAnalysisDto(excelExportDto);
        List<StaffAnalysisVo> staffOnJobAnalysisVoList = separatedStaffAnalysis(queryDto);
        //表头
        List<TitleEntity> titleList=setQueryInfoTitle(excelExportDto);
        //下载时间的 ID 作为以下标题的根ID
        int tId = Constant.Number.EIGHT;
        String pId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity=new TitleEntity(String.valueOf(tId),pId,"品牌","brandName");tId = tId+1;
        TitleEntity entity1=new TitleEntity(String.valueOf(tId),pId,"区域","regionName");tId = tId+1;
        TitleEntity entity2=new TitleEntity(String.valueOf(tId),pId,"门店","shopName");tId = tId+1;
        TitleEntity entity3=new TitleEntity(String.valueOf(tId),pId,"离职员工人数","staffNum");tId = tId+1;
        TitleEntity entity4=new TitleEntity(String.valueOf(tId),pId,"性别分析",null);
        //性别分析 根ID
        String sexId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity5=new TitleEntity(String.valueOf(tId),sexId,"男性员工占比","manNumPercentage");tId = tId+1;
        TitleEntity entity6=new TitleEntity(String.valueOf(tId),sexId,"女性员工占比","womanNumPercentage");tId = tId+1;
        TitleEntity entity7=new TitleEntity(String.valueOf(tId),pId,"工龄分析",null);
        //工龄分析 根ID
        String standId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity8=new TitleEntity(String.valueOf(tId),standId,"3个月内","leThreeMonthPercentage");tId = tId+1;
        TitleEntity entity9=new TitleEntity(String.valueOf(tId),standId,"6个月内","leSixMonthPercentage");tId = tId+1;
        TitleEntity entity10=new TitleEntity(String.valueOf(tId),standId,"1年内","leOneYearPercentage");tId = tId+1;
        TitleEntity entity11=new TitleEntity(String.valueOf(tId),standId,"3年内","leThreeYearPercentage");tId = tId+1;
        TitleEntity entity12=new TitleEntity(String.valueOf(tId),standId,"5年以上","geFiveYearPercentage");tId = tId+1;
        TitleEntity entity13=new TitleEntity(String.valueOf(tId),pId,"岗位分析",null);
        //岗位分析 根ID
        String roleId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity14=new TitleEntity(String.valueOf(tId),roleId,"管理人员占比","managerNumPercentage");tId = tId+1;
        TitleEntity entity15=new TitleEntity(String.valueOf(tId),roleId,"员工占比","generalStaffNumPercentage");tId = tId+1;
        TitleEntity entity16=new TitleEntity(String.valueOf(tId),pId,"当月离职分析",null);
        //当月离职分析 根ID
        String currentEntryId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity17=new TitleEntity(String.valueOf(tId),currentEntryId,"当期离职人数","currentNum");tId = tId+1;
        TitleEntity entity18=new TitleEntity(String.valueOf(tId),currentEntryId,"当期离职人数占比","currentNumPercentage");
        titleList.add(entity);titleList.add(entity1);titleList.add(entity2);titleList.add(entity3);titleList.add(entity4);titleList.add(entity5);
        titleList.add(entity6);titleList.add(entity7);titleList.add(entity8);titleList.add(entity9);titleList.add(entity10);titleList.add(entity11);
        titleList.add(entity12);titleList.add(entity13);titleList.add(entity14);titleList.add(entity15);titleList.add(entity16);titleList.add(entity17);
        titleList.add(entity18);
        List<Map<String,Object>> rowList=new ArrayList<>();
        if(!FastUtils.checkNullOrEmpty(staffOnJobAnalysisVoList)){
            //加入千分位，保留2位小数，并且不够补0
            DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
            DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
            List<StaffAnalysisVo> voList;
            //根据类型过滤
            if (ReportDataConstant.Finance.TYPE_SHOP.equals(excelExportDto.getType())) {
                voList = staffOnJobAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(excelExportDto.getType())) {
                voList = staffOnJobAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_REGION.equals(excelExportDto.getType())) {
                voList = staffOnJobAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
            } else {
                voList = staffOnJobAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
            }
            for(StaffAnalysisVo vo : voList){
                Map map = new HashMap<String,Object>();
                map.put("brandName",vo.getBrandName());map.put("regionName",vo.getRegionName());map.put("shopName",vo.getShopName());
                map.put("staffNum",getNotZeroByConvert(vo.getStaffNum(), df2));
                map.put("manNumPercentage",getStringByConvert(vo.getManNumPercentage(),df));
                map.put("womanNumPercentage",getStringByConvert(vo.getWomanNumPercentage(),df));
                map.put("leThreeMonthPercentage",getStringByConvert(vo.getLeThreeMonthPercentage(),df));
                map.put("leSixMonthPercentage",getStringByConvert(vo.getLeSixMonthPercentage(),df));
                map.put("leOneYearPercentage",getStringByConvert(vo.getLeOneYearPercentage(),df));
                map.put("leThreeYearPercentage",getStringByConvert(vo.getLeThreeYearPercentage(),df));
                map.put("geFiveYearPercentage",getStringByConvert(vo.getGeFiveYearPercentage(),df));
                map.put("managerNumPercentage",getStringByConvert(vo.getManagerNumPercentage(),df));
                map.put("generalStaffNumPercentage",getStringByConvert(vo.getGeneralStaffNumPercentage(),df));
                map.put("currentNum",getNotZeroByConvert(vo.getCurrentNum(), df2));
                map.put("currentNumPercentage",getStringByConvert(vo.getCurrentNumPercentage(),df));
                rowList.add(map);
            }
        }
        deskTypeAnalysisService.exportMethod(response, titleList, rowList);
    }


    /**
     * @Author ZhuHC
     * @Date  2020/3/25 17:24
     * @Param [queryDto, efficiencyList]
     * @return void
     * @Description 获得门店维度员工效率数据
     */
    private void getDataInfoList(StaffAnalysisDto queryDto, List <EfficiencyPerCapitaVo> efficiencyList) {
        //根据日期类型 选择 对应同比时间
        List<Date> dateList = DateUtils.getLastYearDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
        if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
            queryDto.setLastYearBeginDate(dateList.get(Constant.Number.ZERO));
            queryDto.setLastYearEndDate(dateList.get(Constant.Number.ONE));
        }
        //取得各门店前厅后厨员工人数
        List<EfficiencyPerCapitaVo> staffList = staffAnalysisMapper.findStaffListByNo(queryDto);
        //门店客流人数
        List<EfficiencyPerCapitaVo>  deskTypeAnalysisVos = staffAnalysisMapper.findCustomNumByShop(queryDto);
        //门店菜品份数
        List<EfficiencyPerCapitaVo> perCapitaVoList =  staffAnalysisMapper.findDishesNumByShop(queryDto);
        MergeUtil.merge(efficiencyList, staffList,
                EfficiencyPerCapitaVo::getShopId, EfficiencyPerCapitaVo::getShopId,
                (perVo, vo) -> {
                    perVo.setStaffNum(vo.getStaffNum());
                    perVo.setBackStaffNum(vo.getBackStaffNum());
                    perVo.setLastStaffNum(vo.getLastStaffNum());
                    perVo.setLastBackStaffNum(vo.getLastBackStaffNum());
                }
        );
        MergeUtil.merge(efficiencyList, deskTypeAnalysisVos,
                EfficiencyPerCapitaVo::getShopId, EfficiencyPerCapitaVo::getShopId,
                (perVo, vo) -> {
                    perVo.setCustomNum(vo.getCustomNum());
                    perVo.setLastCustomNum(vo.getLastCustomNum());
                }
        );
        MergeUtil.merge(efficiencyList, perCapitaVoList,
                EfficiencyPerCapitaVo::getShopId, EfficiencyPerCapitaVo::getShopId,
                (perVo, vo) -> {
                    perVo.setDishesNum(vo.getDishesNum());
                    perVo.setLastDishesNum(vo.getLastDishesNum());
                }
        );
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/25 17:24
     * @Param [analysisMap, type]
     * @return java.util.List<com.njwd.entity.reportdata.vo.EfficiencyPerCapitaVo>
     * @Description 品牌区域全部维度  员工效率计算
     */
    private List<EfficiencyPerCapitaVo> getAnalysisList(Map<String, List<EfficiencyPerCapitaVo>> analysisMap, String type) {
        List<EfficiencyPerCapitaVo> voList = new LinkedList<>();
        analysisMap.forEach((k, analysisList) -> {
            EfficiencyPerCapitaVo regionAnalysisVo = new EfficiencyPerCapitaVo();
            Integer customNum = analysisList.stream().mapToInt(EfficiencyPerCapitaVo::getCustomNum).sum();
            Integer staffNum = analysisList.stream().mapToInt(EfficiencyPerCapitaVo::getStaffNum).sum();
            BigDecimal dishesNum = analysisList.stream().map(EfficiencyPerCapitaVo::getDishesNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            Integer backStaffNum = analysisList.stream().mapToInt(EfficiencyPerCapitaVo::getBackStaffNum).sum();
            Integer lastCustomNum = analysisList.stream().mapToInt(EfficiencyPerCapitaVo::getLastCustomNum).sum();
            Integer lastStaffNum = analysisList.stream().mapToInt(EfficiencyPerCapitaVo::getLastStaffNum).sum();
            BigDecimal lastDishesNum = analysisList.stream().map(EfficiencyPerCapitaVo::getLastDishesNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            Integer lastBackStaffNum = analysisList.stream().mapToInt(EfficiencyPerCapitaVo::getLastBackStaffNum).sum();
            regionAnalysisVo.setType(type);
            //全部门店
            regionAnalysisVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            regionAnalysisVo.setRegionId(analysisList.get(Constant.Number.ZERO).getRegionId());
            //type不为区域时，对象区域名称为 全部区域
            if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                regionAnalysisVo.setRegionName(analysisList.get(Constant.Number.ZERO).getRegionName());
            } else {
                regionAnalysisVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
            }
            regionAnalysisVo.setBrandId(analysisList.get(Constant.Number.ZERO).getBrandId());
            //type类型为all时，品牌名称为 全部品牌
            if (ReportDataConstant.Finance.TYPE_ALL.equals(type)) {
                regionAnalysisVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
            } else {
                regionAnalysisVo.setBrandName(analysisList.get(Constant.Number.ZERO).getBrandName());
            }
            regionAnalysisVo.setCustomNum(customNum);
            regionAnalysisVo.setStaffNum(staffNum);
            regionAnalysisVo.setDishesNum(dishesNum);
            regionAnalysisVo.setBackStaffNum(backStaffNum);
            regionAnalysisVo.setLastCustomNum(lastCustomNum);
            regionAnalysisVo.setLastStaffNum(lastStaffNum);
            regionAnalysisVo.setLastDishesNum(lastDishesNum);
            regionAnalysisVo.setLastBackStaffNum(lastBackStaffNum);
            //前厅
            regionAnalysisVo.setAvgWork(BigDecimalUtils.getDivide(BigDecimal.valueOf(customNum),BigDecimal.valueOf(staffNum)));
            //后厨
            regionAnalysisVo.setBackAvgWork(BigDecimalUtils.getDivide(dishesNum,BigDecimal.valueOf(backStaffNum)));
            //去年
            regionAnalysisVo.setLastAvgWork(BigDecimalUtils.getDivide(BigDecimal.valueOf(lastCustomNum),BigDecimal.valueOf(lastStaffNum)));
            regionAnalysisVo.setLastBackAvgWork(BigDecimalUtils.getDivide(lastDishesNum,BigDecimal.valueOf(lastBackStaffNum)));
            //员工人数同比
            regionAnalysisVo.setStaffNumPercentage(BigDecimalUtils.getPercent(BigDecimal.valueOf(staffNum).subtract(BigDecimal.valueOf(lastStaffNum)),BigDecimal.valueOf(lastStaffNum)));
            regionAnalysisVo.setBackStaffNumPercentage(BigDecimalUtils.getPercent(BigDecimal.valueOf(backStaffNum).subtract(BigDecimal.valueOf(lastBackStaffNum)),BigDecimal.valueOf(lastBackStaffNum)));
            //工作量同比
            regionAnalysisVo.setAvgWorkPercentage(BigDecimalUtils.getPercent(regionAnalysisVo.getAvgWork().subtract(regionAnalysisVo.getLastAvgWork()),regionAnalysisVo.getLastAvgWork()));
            regionAnalysisVo.setBackAvgWorkPercentage(BigDecimalUtils.getPercent(regionAnalysisVo.getBackAvgWork().subtract(regionAnalysisVo.getLastBackAvgWork()),regionAnalysisVo.getLastBackAvgWork()));
            voList.add(regionAnalysisVo);
        });
        return voList;
    }


    /**
     * @Author ZhuHC
     * @Date  2020/3/20 16:45
     * @Param [v]
     * @return void
     * @Description 计算品牌内排名
     */
    private void getSortInBrand(List <StaffWorkHoursVo> v) {
        v.sort(Comparator.comparing(StaffWorkHoursVo::getAvgWorkHours).reversed());
        //名次
        int sortNum = Constant.Number.ONE;
        StaffWorkHoursVo vo;
        StaffWorkHoursVo nextVo;
        for (int i = 0; i < v.size(); i++) {
            vo = v.get(i);
            vo.setSortBrand(sortNum);
            if (i < v.size() - 1) {
                nextVo = v.get(i + 1);
                //平均时长一样时，排名不变；否则取当前下标+2
                if (!ReportDataConstant.BigdecimalCompare.EQUALS.equals(vo.getAvgWorkHours().compareTo(nextVo.getAvgWorkHours()))) {
                    sortNum = i+2;
                }
            }
        }
    }

    private void getSortInEnte(List <StaffWorkHoursVo> v) {
        v.sort(Comparator.comparing(StaffWorkHoursVo::getAvgWorkHours).reversed());
        //名次
        int sortNum = Constant.Number.ONE;
        StaffWorkHoursVo vo;
        StaffWorkHoursVo nextVo;
        for (int i = 0; i < v.size(); i++) {
            vo = v.get(i);
            vo.setSortEnte(sortNum);
            if (i < v.size() - 1) {
                nextVo = v.get(i + 1);
                //平均时长一样时，排名不变；否则取当前下标+2
                if (!ReportDataConstant.BigdecimalCompare.EQUALS.equals(vo.getAvgWorkHours().compareTo(nextVo.getAvgWorkHours()))) {
                    sortNum = i+2;
                }
            }
        }
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
     * @Author ZhuHC
     * @Date  2020/3/12 14:27
     * @Param [excelExportDto]
     * @return java.util.List<com.njwd.poiexcel.TitleEntity>
     * @Description 导出-标题通用方法
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
        TitleEntity titleEntity6=new TitleEntity("6","5",null,null);
        TitleEntity titleEntity7=new TitleEntity("7","6",null,null);
        TitleEntity titleEntity8=new TitleEntity("8","7",null,null);
        titleList.add(titleEntity0);
        titleList.add(titleEntity1);
        titleList.add(titleEntity2);
        titleList.add(titleEntity3);
        titleList.add(titleEntity4);
        titleList.add(titleEntity5);
        titleList.add(titleEntity6);
        titleList.add(titleEntity7);
        titleList.add(titleEntity8);
        return titleList;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/11 19:09
     * @Param [excelExportDto]
     * @return com.njwd.entity.reportdata.dto.StaffAnalysisDto
     * @Description 查询条件转换
     */
    private StaffAnalysisDto getStaffAnalysisDto(ExcelExportDto excelExportDto) {
        StaffAnalysisDto queryDto = new StaffAnalysisDto();
        queryDto.setShopIdList(excelExportDto.getShopIdList());
        queryDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        queryDto.setEnteId(excelExportDto.getEnteId());
        queryDto.setBeginDate(excelExportDto.getBeginDate());
        queryDto.setEndDate(excelExportDto.getEndDate());
        queryDto.setDateType(excelExportDto.getDateType());
        return queryDto;
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 14:51
     * @Param [voListMap, type]
     * @return java.util.List<com.njwd.entity.reportdata.vo.StaffAnalysisVo>
     * @Description 离职员工数处理
     */
    private List<StaffAnalysisVo> getSeparatedStaffInfoList(Map<String, List<StaffAnalysisVo>> voListMap,Map<String, List<BaseUserVo>> staffByRegionMap, String type) {
        List<StaffAnalysisVo> staffList = new LinkedList<>();
        voListMap.forEach((k, voList) -> {
            StaffAnalysisVo staffOnJobAnalysisVo = new StaffAnalysisVo();
            staffOnJobAnalysisVo.setType(type);
            //全部门店
            staffOnJobAnalysisVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            staffOnJobAnalysisVo.setRegionId(voList.get(Constant.Number.ZERO).getRegionId());
            //type不为区域时，对象区域名称为 全部区域
            if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                staffOnJobAnalysisVo.setRegionName(voList.get(Constant.Number.ZERO).getRegionName());
            } else {
                staffOnJobAnalysisVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
            }
            staffOnJobAnalysisVo.setBrandId(voList.get(Constant.Number.ZERO).getBrandId());
            //type类型为all时，品牌名称为 全部品牌
            if (ReportDataConstant.Finance.TYPE_ALL.equals(type)) {
                staffOnJobAnalysisVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
            } else {
                staffOnJobAnalysisVo.setBrandName(voList.get(Constant.Number.ZERO).getBrandName());
            }
            //范围内在职人数
            BigDecimal staffOnJob = new BigDecimal(staffByRegionMap.get(k).size());
            //范围内总人数
            BigDecimal allStaff = voList.stream().map(StaffAnalysisVo::getStaffNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            int manNum = voList.stream().mapToInt(StaffAnalysisVo::getManNum).sum();
            int womanNum = voList.stream().mapToInt(StaffAnalysisVo::getWomanNum).sum();
            int leThreeMonthNum = voList.stream().mapToInt(StaffAnalysisVo::getLeThreeMonthNum).sum();
            int leSixMonthNum = voList.stream().mapToInt(StaffAnalysisVo::getLeSixMonthNum).sum();
            int leOneYearNum = voList.stream().mapToInt(StaffAnalysisVo::getLeOneYearNum).sum();
            int leThreeYearNum = voList.stream().mapToInt(StaffAnalysisVo::getLeThreeYearNum).sum();
            int geFiveYearNum = voList.stream().mapToInt(StaffAnalysisVo::getGeFiveYearNum).sum();
            int managerNum = voList.stream().mapToInt(StaffAnalysisVo::getManagerNum).sum();
            int generalStaffNum = voList.stream().mapToInt(StaffAnalysisVo::getGeneralStaffNum).sum();
            //当期离职人数
            BigDecimal currentNum = voList.stream().map(StaffAnalysisVo::getCurrentNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            //范围内总人数
            staffOnJobAnalysisVo.setStaffNum(allStaff);
            //男
            staffOnJobAnalysisVo.setManNum(manNum);
            staffOnJobAnalysisVo.setManNumPercentage(BigDecimalUtils.getPercent(getBigVal(manNum), allStaff));
            //女
            staffOnJobAnalysisVo.setWomanNum(womanNum);
            staffOnJobAnalysisVo.setWomanNumPercentage(BigDecimalUtils.getPercent(getBigVal(womanNum), allStaff));
            //3月内
            staffOnJobAnalysisVo.setLeThreeMonthNum(leThreeMonthNum);
            staffOnJobAnalysisVo.setLeThreeMonthPercentage(BigDecimalUtils.getPercent(getBigVal(leThreeMonthNum), allStaff));
            //3-6 月
            staffOnJobAnalysisVo.setLeSixMonthNum(leSixMonthNum);
            staffOnJobAnalysisVo.setLeSixMonthPercentage(BigDecimalUtils.getPercent(getBigVal(leSixMonthNum), allStaff));
            //6月-1年
            staffOnJobAnalysisVo.setLeOneYearNum(leOneYearNum);
            staffOnJobAnalysisVo.setLeOneYearPercentage(BigDecimalUtils.getPercent(getBigVal(leOneYearNum), allStaff));
            //1-3年
            staffOnJobAnalysisVo.setLeThreeYearNum(leThreeYearNum);
            staffOnJobAnalysisVo.setLeThreeYearPercentage(BigDecimalUtils.getPercent(getBigVal(leThreeYearNum), allStaff));
            //5年以上
            staffOnJobAnalysisVo.setGeFiveYearNum(geFiveYearNum);
            staffOnJobAnalysisVo.setGeFiveYearPercentage(BigDecimalUtils.getPercent(getBigVal(geFiveYearNum), allStaff));
            //管理人员占比
            staffOnJobAnalysisVo.setManagerNum(managerNum);
            staffOnJobAnalysisVo.setManagerNumPercentage(BigDecimalUtils.getPercent(getBigVal(managerNum), allStaff));
            //员工占比
            staffOnJobAnalysisVo.setGeneralStaffNum(generalStaffNum);
            staffOnJobAnalysisVo.setGeneralStaffNumPercentage(BigDecimalUtils.getPercent(getBigVal(generalStaffNum), allStaff));
            //当期离职人数占比
            staffOnJobAnalysisVo.setCurrentNum(currentNum);
            staffOnJobAnalysisVo.setCurrentNumPercentage(BigDecimalUtils.getPercent(currentNum, staffOnJob));
            staffList.add(staffOnJobAnalysisVo);
        });
        return staffList;
    }

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.StaffOnJobAnalysisVo>
     * @Author ZhuHC
     * @Date 2020/3/11 14:33
     * @Param [voListByRegionMap, type]
     * @Description 门店在职员工数据 处理成 区域 品牌以及全部 数据
     */
    private List<StaffAnalysisVo> getStaffInfoList(Map<String, List<StaffAnalysisVo>> voListMap, String type) {
        List<StaffAnalysisVo> staffList = new LinkedList<>();
        voListMap.forEach((k, voList) -> {
            StaffAnalysisVo staffOnJobAnalysisVo = new StaffAnalysisVo();
            staffOnJobAnalysisVo.setType(type);
            //全部门店
            staffOnJobAnalysisVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            staffOnJobAnalysisVo.setRegionId(voList.get(Constant.Number.ZERO).getRegionId());
            //type不为区域时，对象区域名称为 全部区域
            if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
                staffOnJobAnalysisVo.setRegionName(voList.get(Constant.Number.ZERO).getRegionName());
            } else {
                staffOnJobAnalysisVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
            }
            staffOnJobAnalysisVo.setBrandId(voList.get(Constant.Number.ZERO).getBrandId());
            //type类型为all时，品牌名称为 全部品牌
            if (ReportDataConstant.Finance.TYPE_ALL.equals(type)) {
                staffOnJobAnalysisVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
            } else {
                staffOnJobAnalysisVo.setBrandName(voList.get(Constant.Number.ZERO).getBrandName());
            }
            //范围内总人数
            BigDecimal allStaff = voList.stream().map(StaffAnalysisVo::getStaffNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            int manNum = voList.stream().mapToInt(StaffAnalysisVo::getManNum).sum();
            int womanNum = voList.stream().mapToInt(StaffAnalysisVo::getWomanNum).sum();
            int leThreeMonthNum = voList.stream().mapToInt(StaffAnalysisVo::getLeThreeMonthNum).sum();
            int leSixMonthNum = voList.stream().mapToInt(StaffAnalysisVo::getLeSixMonthNum).sum();
            int leOneYearNum = voList.stream().mapToInt(StaffAnalysisVo::getLeOneYearNum).sum();
            int leThreeYearNum = voList.stream().mapToInt(StaffAnalysisVo::getLeThreeYearNum).sum();
            int geFiveYearNum = voList.stream().mapToInt(StaffAnalysisVo::getGeFiveYearNum).sum();
            int managerNum = voList.stream().mapToInt(StaffAnalysisVo::getManagerNum).sum();
            int generalStaffNum = voList.stream().mapToInt(StaffAnalysisVo::getGeneralStaffNum).sum();
            //当期入职人数
            BigDecimal currentEntryNum = voList.stream().map(StaffAnalysisVo::getCurrentNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            int firstKindNum = voList.stream().mapToInt(StaffAnalysisVo::getFifthKindNum).sum();
            int secondKindNum = voList.stream().mapToInt(StaffAnalysisVo::getSecondKindNum).sum();
            int thirdKindNum = voList.stream().mapToInt(StaffAnalysisVo::getThirdKindNum).sum();
            int fourthKindNum = voList.stream().mapToInt(StaffAnalysisVo::getFourthKindNum).sum();
            int fifthKindNum = voList.stream().mapToInt(StaffAnalysisVo::getFifthKindNum).sum();
            int primaryDegreeNum = voList.stream().mapToInt(StaffAnalysisVo::getPrimaryDegreeNum).sum();
            int juniorHighDegreeNum = voList.stream().mapToInt(StaffAnalysisVo::getJuniorHighDegreeNum).sum();
            int highDegreeNum = voList.stream().mapToInt(StaffAnalysisVo::getHighDegreeNum).sum();
            int secondaryDegreeNum = voList.stream().mapToInt(StaffAnalysisVo::getSecondaryDegreeNum).sum();
            int collegeDegreeNum = voList.stream().mapToInt(StaffAnalysisVo::getCollegeDegreeNum).sum();
            int universityDegreeNum = voList.stream().mapToInt(StaffAnalysisVo::getUniversityDegreeNum).sum();
            int masterDegreeNum = voList.stream().mapToInt(StaffAnalysisVo::getMasterDegreeNum).sum();
            int doctoralDegreeNum = voList.stream().mapToInt(StaffAnalysisVo::getDoctoralDegreeNum).sum();
            int sumAge = voList.stream().mapToInt(StaffAnalysisVo::getSumAge).sum();
            //范围内总人数
            staffOnJobAnalysisVo.setStaffNum(allStaff);
            //男
            staffOnJobAnalysisVo.setManNum(manNum);
            staffOnJobAnalysisVo.setManNumPercentage(BigDecimalUtils.getPercent(getBigVal(manNum), allStaff));
            //女
            staffOnJobAnalysisVo.setWomanNum(womanNum);
            staffOnJobAnalysisVo.setWomanNumPercentage(BigDecimalUtils.getPercent(getBigVal(womanNum), allStaff));
            //3月内
            staffOnJobAnalysisVo.setLeThreeMonthNum(leThreeMonthNum);
            staffOnJobAnalysisVo.setLeThreeMonthPercentage(BigDecimalUtils.getPercent(getBigVal(leThreeMonthNum), allStaff));
            //3-6 月
            staffOnJobAnalysisVo.setLeSixMonthNum(leSixMonthNum);
            staffOnJobAnalysisVo.setLeSixMonthPercentage(BigDecimalUtils.getPercent(getBigVal(leSixMonthNum), allStaff));
            //6月-1年
            staffOnJobAnalysisVo.setLeOneYearNum(leOneYearNum);
            staffOnJobAnalysisVo.setLeOneYearPercentage(BigDecimalUtils.getPercent(getBigVal(leOneYearNum), allStaff));
            //1-3年
            staffOnJobAnalysisVo.setLeThreeYearNum(leThreeYearNum);
            staffOnJobAnalysisVo.setLeThreeYearPercentage(BigDecimalUtils.getPercent(getBigVal(leThreeYearNum), allStaff));
            //5年以上
            staffOnJobAnalysisVo.setGeFiveYearNum(geFiveYearNum);
            staffOnJobAnalysisVo.setGeFiveYearPercentage(BigDecimalUtils.getPercent(getBigVal(geFiveYearNum), allStaff));
            //管理人员占比
            staffOnJobAnalysisVo.setManagerNum(managerNum);
            staffOnJobAnalysisVo.setManagerNumPercentage(BigDecimalUtils.getPercent(getBigVal(managerNum), allStaff));
            //员工占比
            staffOnJobAnalysisVo.setGeneralStaffNum(generalStaffNum);
            staffOnJobAnalysisVo.setGeneralStaffNumPercentage(BigDecimalUtils.getPercent(getBigVal(generalStaffNum), allStaff));
            //当期入职人数占比
            staffOnJobAnalysisVo.setCurrentNum(currentEntryNum);
            staffOnJobAnalysisVo.setCurrentNumPercentage(BigDecimalUtils.getPercent(currentEntryNum, allStaff));
            //[16-25)
            staffOnJobAnalysisVo.setFirstKindNum(firstKindNum);
            staffOnJobAnalysisVo.setFirstKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(firstKindNum), allStaff));
            //[25-35)
            staffOnJobAnalysisVo.setSecondKindNum(secondKindNum);
            staffOnJobAnalysisVo.setSecondKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(secondKindNum), allStaff));
            //[35-45)
            staffOnJobAnalysisVo.setThirdKindNum(thirdKindNum);
            staffOnJobAnalysisVo.setThirdKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(thirdKindNum), allStaff));
            //[45-55)
            staffOnJobAnalysisVo.setFourthKindNum(fourthKindNum);
            staffOnJobAnalysisVo.setFourthKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(fourthKindNum), allStaff));
            //[55-
            staffOnJobAnalysisVo.setFifthKindNum(fifthKindNum);
            staffOnJobAnalysisVo.setFifthKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(fifthKindNum), allStaff));
            //年龄之和
            staffOnJobAnalysisVo.setSumAge(sumAge);
            //平均年龄
            staffOnJobAnalysisVo.setAvgAge(BigDecimalUtils.getDivide(new BigDecimal(sumAge), allStaff));
            //小学 （初中以下）
            staffOnJobAnalysisVo.setPrimaryDegreeNum(primaryDegreeNum);
            staffOnJobAnalysisVo.setPrimaryDegreePercentage(BigDecimalUtils.getPercent(getBigVal(primaryDegreeNum), allStaff));
            //初中
            staffOnJobAnalysisVo.setJuniorHighDegreeNum(juniorHighDegreeNum);
            staffOnJobAnalysisVo.setJuniorHighDegreePercentage(BigDecimalUtils.getPercent(getBigVal(juniorHighDegreeNum), allStaff));
            //高中
            staffOnJobAnalysisVo.setHighDegreeNum(highDegreeNum);
            staffOnJobAnalysisVo.setHighDegreePercentage(BigDecimalUtils.getPercent(getBigVal(highDegreeNum), allStaff));
            //中专
            staffOnJobAnalysisVo.setSecondaryDegreeNum(secondaryDegreeNum);
            staffOnJobAnalysisVo.setSecondaryDegreePercentage(BigDecimalUtils.getPercent(getBigVal(secondaryDegreeNum), allStaff));
            //大专
            staffOnJobAnalysisVo.setCollegeDegreeNum(collegeDegreeNum);
            staffOnJobAnalysisVo.setCollegeDegreePercentage(BigDecimalUtils.getPercent(getBigVal(collegeDegreeNum), allStaff));
            //本科
            staffOnJobAnalysisVo.setUniversityDegreeNum(universityDegreeNum);
            staffOnJobAnalysisVo.setUniversityDegreePercentage(BigDecimalUtils.getPercent(getBigVal(universityDegreeNum), allStaff));
            //硕士
            staffOnJobAnalysisVo.setMasterDegreeNum(masterDegreeNum);
            staffOnJobAnalysisVo.setMasterDegreePercentage(BigDecimalUtils.getPercent(getBigVal(masterDegreeNum), allStaff));
            //博士（及以上）
            staffOnJobAnalysisVo.setDoctoralDegreeNum(doctoralDegreeNum);
            staffOnJobAnalysisVo.setDoctoralDegreePercentage(BigDecimalUtils.getPercent(getBigVal(doctoralDegreeNum), allStaff));
            staffList.add(staffOnJobAnalysisVo);
        });
        return staffList;
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/3/11 14:30
     * @Param [staffOnJobAnalysisVoList, userVoListByShopMap]
     * @Description 人员数据转换成 在职员工 数据
     */
    private void getStaffOnJobList(List<StaffAnalysisVo> staffOnJobAnalysisVoList, Map<String, List<BaseUserVo>> userVoListByShopMap,String enteId) {
        //范围内总在职人数
        List<BaseUserVo> userVoList;
        //计算 占比等各项数据
        for (Map.Entry<String, List<BaseUserVo>> entry : userVoListByShopMap.entrySet()) {
            userVoList = entry.getValue();
            StaffAnalysisVo staffOnJobAnalysisVo = new StaffAnalysisVo();
            //根据map键值获得 门店 区域 品牌 ID和名称
            String[] keyValue = entry.getKey().split(Constant.Character.MIDDLE_LINE);
            //类型 类型 shop 为门店 brand 品牌 region区域
            staffOnJobAnalysisVo.setType(ReportDataConstant.Finance.TYPE_SHOP);
            //门店ID
            staffOnJobAnalysisVo.setShopId(keyValue[Constant.Number.ZERO]);
            //门店名称
            staffOnJobAnalysisVo.setShopName(keyValue[Constant.Number.ONE]);
            //区域ID
            staffOnJobAnalysisVo.setRegionId(keyValue[Constant.Number.TWO]);
            //区域名称
            staffOnJobAnalysisVo.setRegionName(keyValue[Constant.Number.THREE]);
            //品牌ID
            staffOnJobAnalysisVo.setBrandId(keyValue[Constant.Number.FOUR]);
            //品牌名称
            staffOnJobAnalysisVo.setBrandName(keyValue[Constant.Number.FIVE]);
            staffOnJobAnalysisVo.setEnteId(enteId);
            //范围内总人数
            BigDecimal allStaff = new BigDecimal(userVoList.size());
            staffOnJobAnalysisVo.setStaffNum(allStaff);
            //根据男女分组
            setGroupBySex(userVoList, staffOnJobAnalysisVo, allStaff);
            //工龄分析
            setGroupByStandingType(userVoList, staffOnJobAnalysisVo, allStaff);
            //岗位分析-根据是否管理岗分组
            setGroupByIsManager(userVoList, staffOnJobAnalysisVo, allStaff);
            //是否当期入/离职  0否 1是
            setGroupByIsCurrent(userVoList,staffOnJobAnalysisVo, allStaff);
            //年龄占比分析
            setGroupByAge(userVoList, staffOnJobAnalysisVo, allStaff);
            //年龄和
            int sumAge = userVoList.stream().mapToInt(BaseUserVo::getAge).sum();
            staffOnJobAnalysisVo.setSumAge(sumAge);
            //平均年龄
            staffOnJobAnalysisVo.setAvgAge(BigDecimalUtils.getDivide(new BigDecimal(sumAge), allStaff));
            //学历占比分析
            setGroupByEduLevel(userVoList, staffOnJobAnalysisVo, allStaff);
            //数据添加进list
            staffOnJobAnalysisVoList.add(staffOnJobAnalysisVo);
        }
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 14:26
     * @Param [userVoList, staffOnJobAnalysisVo, allStaff]
     * @return void
     * @Description 当期入/离职分析
     */
    private void setGroupByIsCurrent(List<BaseUserVo> userVoList,StaffAnalysisVo staffOnJobAnalysisVo, BigDecimal allStaff) {
        //当期入/离职分析
        Map<Integer, List<BaseUserVo>> groupByIsCurrentEntry = userVoList.stream().collect(Collectors.groupingBy(BaseUserVo::getIsCurrentEntry));
        if (null != groupByIsCurrentEntry && groupByIsCurrentEntry.size() > 0) {
            //当期入/离职人数
            BigDecimal currentEntryNum = new BigDecimal(BigDecimal.ROUND_UP);
            if (!FastUtils.checkNullOrEmpty(groupByIsCurrentEntry.get(Constant.Is.YES_INT))) {
                currentEntryNum = getBigVal(groupByIsCurrentEntry.get(Constant.Is.YES_INT).size());
                staffOnJobAnalysisVo.setCurrentNum(currentEntryNum);
            }
            //当期入/离职人数占比
            staffOnJobAnalysisVo.setCurrentNumPercentage(BigDecimalUtils.getPercent(currentEntryNum, allStaff));
        }
    }
    /**
     * @Author ZhuHC
     * @Date  2020/3/12 14:26
     * @Param [userVoList, staffOnJobAnalysisVo, allStaff]
     * @return void
     * @Description 是否管理岗
     */
    private void setGroupByIsManager(List<BaseUserVo> userVoList, StaffAnalysisVo staffOnJobAnalysisVo, BigDecimal allStaff) {
        int num;
        Map<Integer, List<BaseUserVo>> groupByIsManager = userVoList.stream().collect(Collectors.groupingBy(BaseUserVo::getIsManager));
        //是否管理岗  0否 1是
        if (null != groupByIsManager && groupByIsManager.size() > 0) {
            //管理人员占比
            if (!FastUtils.checkNullOrEmpty(groupByIsManager.get(Constant.Is.YES_INT))) {
                num = groupByIsManager.get(Constant.Is.YES_INT).size();
                staffOnJobAnalysisVo.setManagerNum(num);
                staffOnJobAnalysisVo.setManagerNumPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //员工占比
            if (!FastUtils.checkNullOrEmpty(groupByIsManager.get(Constant.Is.NO_INT))) {
                num = groupByIsManager.get(Constant.Is.NO_INT).size();
                staffOnJobAnalysisVo.setGeneralStaffNum(num);
                staffOnJobAnalysisVo.setGeneralStaffNumPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
        }
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/3/11 15:44
     * @Param [userVoList, staffOnJobAnalysisVo, allStaff]
     * @Description 学历占比分析
     */
    private void setGroupByEduLevel(List<BaseUserVo> userVoList, StaffAnalysisVo staffOnJobAnalysisVo, BigDecimal allStaff) {
        int num;
        Map<Integer, List<BaseUserVo>> groupByEduLevelType = userVoList.stream().collect(Collectors.groupingBy(BaseUserVo::getEduLevelType));
        if (null != groupByEduLevelType && groupByEduLevelType.size() > 0) {
            //小学 （初中以下）
            if (!FastUtils.checkNullOrEmpty(groupByEduLevelType.get(ReportDataConstant.EducationalBackground.PRIMARY_DEGREE))) {
                num = groupByEduLevelType.get(ReportDataConstant.EducationalBackground.PRIMARY_DEGREE).size();
                staffOnJobAnalysisVo.setPrimaryDegreeNum(num);
                staffOnJobAnalysisVo.setPrimaryDegreePercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //初中
            if (!FastUtils.checkNullOrEmpty(groupByEduLevelType.get(ReportDataConstant.EducationalBackground.JUNIOR_HIGH_DEGREE))) {
                num = groupByEduLevelType.get(ReportDataConstant.EducationalBackground.JUNIOR_HIGH_DEGREE).size();
                staffOnJobAnalysisVo.setJuniorHighDegreeNum(num);
                staffOnJobAnalysisVo.setJuniorHighDegreePercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //高中
            if (!FastUtils.checkNullOrEmpty(groupByEduLevelType.get(ReportDataConstant.EducationalBackground.HIGH_DEGREE))) {
                num = groupByEduLevelType.get(ReportDataConstant.EducationalBackground.HIGH_DEGREE).size();
                staffOnJobAnalysisVo.setHighDegreeNum(num);
                staffOnJobAnalysisVo.setHighDegreePercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //中专
            if (!FastUtils.checkNullOrEmpty(groupByEduLevelType.get(ReportDataConstant.EducationalBackground.SECONDARY_DEGREE))) {
                num = groupByEduLevelType.get(ReportDataConstant.EducationalBackground.SECONDARY_DEGREE).size();
                staffOnJobAnalysisVo.setSecondaryDegreeNum(num);
                staffOnJobAnalysisVo.setSecondaryDegreePercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //大专
            if (!FastUtils.checkNullOrEmpty(groupByEduLevelType.get(ReportDataConstant.EducationalBackground.COLLEGE_DEGREE))) {
                num = groupByEduLevelType.get(ReportDataConstant.EducationalBackground.COLLEGE_DEGREE).size();
                staffOnJobAnalysisVo.setCollegeDegreeNum(num);
                staffOnJobAnalysisVo.setCollegeDegreePercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //本科
            if (!FastUtils.checkNullOrEmpty(groupByEduLevelType.get(ReportDataConstant.EducationalBackground.UNIVERSITY_DEGREE))) {
                num = groupByEduLevelType.get(ReportDataConstant.EducationalBackground.UNIVERSITY_DEGREE).size();
                staffOnJobAnalysisVo.setUniversityDegreeNum(num);
                staffOnJobAnalysisVo.setUniversityDegreePercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //硕士
            if (!FastUtils.checkNullOrEmpty(groupByEduLevelType.get(ReportDataConstant.EducationalBackground.MASTER_DEGREE))) {
                num = groupByEduLevelType.get(ReportDataConstant.EducationalBackground.MASTER_DEGREE).size();
                staffOnJobAnalysisVo.setMasterDegreeNum(num);
                staffOnJobAnalysisVo.setMasterDegreePercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //博士（及以上）
            if (!FastUtils.checkNullOrEmpty(groupByEduLevelType.get(ReportDataConstant.EducationalBackground.DOCTORAL_DEGREE))) {
                num = groupByEduLevelType.get(ReportDataConstant.EducationalBackground.DOCTORAL_DEGREE).size();
                staffOnJobAnalysisVo.setDoctoralDegreeNum(num);
                staffOnJobAnalysisVo.setDoctoralDegreePercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
        }
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/3/11 15:44
     * @Param [userVoList, staffOnJobAnalysisVo, allStaff]
     * @Description 年龄占比分析
     */
    private void setGroupByAge(List<BaseUserVo> userVoList, StaffAnalysisVo staffOnJobAnalysisVo, BigDecimal allStaff) {
        int num;
        Map<Integer, List<BaseUserVo>> groupByAgeType = userVoList.stream().collect(Collectors.groupingBy(BaseUserVo::getAgeType));
        if (null != groupByAgeType && groupByAgeType.size() > 0) {
            //[16-25)
            if (!FastUtils.checkNullOrEmpty(groupByAgeType.get(ReportDataConstant.AgeType.FIRST_KIND))) {
                num = groupByAgeType.get(ReportDataConstant.AgeType.FIRST_KIND).size();
                staffOnJobAnalysisVo.setFirstKindNum(num);
                staffOnJobAnalysisVo.setFirstKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //[25-35)
            if (!FastUtils.checkNullOrEmpty(groupByAgeType.get(ReportDataConstant.AgeType.SECOND_KIND))) {
                num = groupByAgeType.get(ReportDataConstant.AgeType.SECOND_KIND).size();
                staffOnJobAnalysisVo.setSecondKindNum(num);
                staffOnJobAnalysisVo.setSecondKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //[35-45)
            if (!FastUtils.checkNullOrEmpty(groupByAgeType.get(ReportDataConstant.AgeType.THIRD_KIND))) {
                num = groupByAgeType.get(ReportDataConstant.AgeType.THIRD_KIND).size();
                staffOnJobAnalysisVo.setThirdKindNum(num);
                staffOnJobAnalysisVo.setThirdKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //[45-55)
            if (!FastUtils.checkNullOrEmpty(groupByAgeType.get(ReportDataConstant.AgeType.FOURTH_KIND))) {
                num = groupByAgeType.get(ReportDataConstant.AgeType.FOURTH_KIND).size();
                staffOnJobAnalysisVo.setFourthKindNum(num);
                staffOnJobAnalysisVo.setFourthKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //[55-
            if (!FastUtils.checkNullOrEmpty(groupByAgeType.get(ReportDataConstant.AgeType.FIFTH_KIND))) {
                num = groupByAgeType.get(ReportDataConstant.AgeType.FIFTH_KIND).size();
                staffOnJobAnalysisVo.setFifthKindNum(num);
                staffOnJobAnalysisVo.setFifthKindNumPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
        }
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/3/11 15:45
     * @Param [userVoList, staffOnJobAnalysisVo, allStaff]
     * @Description 工龄分析
     */
    private void setGroupByStandingType(List<BaseUserVo> userVoList, StaffAnalysisVo staffOnJobAnalysisVo, BigDecimal allStaff) {
        int num;
        Map<Integer, List<BaseUserVo>> groupByStandingType = userVoList.stream().collect(Collectors.groupingBy(BaseUserVo::getStandingType));
        if (null != groupByStandingType && groupByStandingType.size() > 0) {
            //3月内
            if (!FastUtils.checkNullOrEmpty(groupByStandingType.get(ReportDataConstant.StandingType.THREE_MOUTH))) {
                num = groupByStandingType.get(ReportDataConstant.StandingType.THREE_MOUTH).size();
                staffOnJobAnalysisVo.setLeThreeMonthNum(num);
                staffOnJobAnalysisVo.setLeThreeMonthPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //3-6 月
            if (!FastUtils.checkNullOrEmpty(groupByStandingType.get(ReportDataConstant.StandingType.SIX_MOUTH))) {
                num = groupByStandingType.get(ReportDataConstant.StandingType.SIX_MOUTH).size();
                staffOnJobAnalysisVo.setLeSixMonthNum(num);
                staffOnJobAnalysisVo.setLeSixMonthPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //6月-1年
            if (!FastUtils.checkNullOrEmpty(groupByStandingType.get(ReportDataConstant.StandingType.ONE_YEAR))) {
                num = groupByStandingType.get(ReportDataConstant.StandingType.ONE_YEAR).size();
                staffOnJobAnalysisVo.setLeOneYearNum(num);
                staffOnJobAnalysisVo.setLeOneYearPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //1-3年
            if (!FastUtils.checkNullOrEmpty(groupByStandingType.get(ReportDataConstant.StandingType.THREE_YEAR))) {
                num = groupByStandingType.get(ReportDataConstant.StandingType.THREE_YEAR).size();
                staffOnJobAnalysisVo.setLeThreeYearNum(num);
                staffOnJobAnalysisVo.setLeThreeYearPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //5年以上
            if (!FastUtils.checkNullOrEmpty(groupByStandingType.get(ReportDataConstant.StandingType.FIVE_YEAR))) {
                num = groupByStandingType.get(ReportDataConstant.StandingType.FIVE_YEAR).size();
                staffOnJobAnalysisVo.setGeFiveYearNum(num);
                staffOnJobAnalysisVo.setGeFiveYearPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
        }
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/3/11 15:45
     * @Param [userVoList, staffOnJobAnalysisVo, allStaff]
     * @Description 根据男女分组
     */
    private void setGroupBySex(List<BaseUserVo> userVoList, StaffAnalysisVo staffOnJobAnalysisVo, BigDecimal allStaff) {
        int num;
        Map<String, List<BaseUserVo>> groupBySex = userVoList.stream().collect(Collectors.groupingBy(BaseUserVo::getSex));
        //性别分析 - 计数/在职人数
        if (null != groupBySex && groupBySex.size() > 0) {
            //男员工占比
            if (!FastUtils.checkNullOrEmpty(groupBySex.get(ReportDataConstant.Sex.MAN))) {
                num = groupBySex.get(ReportDataConstant.Sex.MAN).size();
                staffOnJobAnalysisVo.setManNum(num);
                staffOnJobAnalysisVo.setManNumPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
            //女员工占比
            if (!FastUtils.checkNullOrEmpty(groupBySex.get(ReportDataConstant.Sex.WOMAN))) {
                num = groupBySex.get(ReportDataConstant.Sex.WOMAN).size();
                staffOnJobAnalysisVo.setWomanNum(num);
                staffOnJobAnalysisVo.setWomanNumPercentage(BigDecimalUtils.getPercent(getBigVal(num), allStaff));
            }
        }
    }

    /**
     * @return java.math.BigDecimal
     * @Author ZhuHC
     * @Date 2020/3/11 14:30
     * @Param [num]
     * @Description 数字转换成 BigDecimal
     */
    private BigDecimal getBigVal(int num) {
        return new BigDecimal(num);
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2020/3/11 14:31
     * @Param [queryDto, onJobStaffList]
     * @Description 在职员工数据做处理
     */
    private void getCountInfo(StaffAnalysisDto queryDto, List<BaseUserVo> onJobStaffList) {
        //查询开始时间
        Date beginDate = queryDto.getBeginDate();
        //查询结束时间
        Date endDate = queryDto.getEndDate();
        //对在职员工数据做处理
        onJobStaffList.stream().forEach(vo -> {
            //当教育等级为null时，转成-1，防止报错
            if(null == vo.getEduLevelType()){
                vo.setEduLevelType(ReportDataConstant.EducationalBackground.NOT_COUNT);
            }
            //入职时间
            Date hireDate = vo.getHireDate();
            //工龄分析 按照3月，3月-6月，6月-1年，1年-3年，5年
            if (null != hireDate) {
                //截止结束时间工龄  -- 月份
                Integer monthsDiff = DateUtils.betweenMonth(endDate, hireDate);
                Integer standingType = getStandingType(monthsDiff);
                vo.setStandingType(standingType);
            }
            //当期入职分析- 是否当期入职  0否 1是
            if (null != hireDate) {
                if (hireDate.getTime() >= beginDate.getTime() && hireDate.getTime() <= endDate.getTime()) {
                    vo.setIsCurrentEntry(Constant.Is.YES_INT);
                } else {
                    vo.setIsCurrentEntry(Constant.Is.NO_INT);
                }
            }
            //年龄占比分析 -   16-25, 25-35, 35-45, 45-55, 55以上
            Date birthday = vo.getBirthday();
            //生日不为null 且 小于查询截止日期
            if (null != birthday) {
                Integer age = DateUtils.countAge(endDate, birthday);
                //默认0, 0不参与统计
                Integer ageType = ReportDataConstant.AgeType.NOT_COUNT;
                if (age >= ReportDataConstant.StaffAgeRegion.FIRST_KIND_BEGIN && age < ReportDataConstant.StaffAgeRegion.FIRST_KIND_END) {
                    ageType = ReportDataConstant.AgeType.FIRST_KIND;
                } else if (age >= ReportDataConstant.StaffAgeRegion.FIRST_KIND_END && age < ReportDataConstant.StaffAgeRegion.SECOND_KIND_END) {
                    ageType = ReportDataConstant.AgeType.SECOND_KIND;
                } else if (age >= ReportDataConstant.StaffAgeRegion.SECOND_KIND_END && age < ReportDataConstant.StaffAgeRegion.THIRD_KIND_END) {
                    ageType = ReportDataConstant.AgeType.THIRD_KIND;
                } else if (age >= ReportDataConstant.StaffAgeRegion.THIRD_KIND_END && age < ReportDataConstant.StaffAgeRegion.FOURTH_KIND_END) {
                    ageType = ReportDataConstant.AgeType.FOURTH_KIND;
                } else if (age >= ReportDataConstant.StaffAgeRegion.FOURTH_KIND_END) {
                    ageType = ReportDataConstant.AgeType.FIFTH_KIND;
                }
                vo.setAgeType(ageType);
                vo.setAge(age);
            }
        });
    }

    /**
     * @Author ZhuHC
     * @Date  2020/3/12 11:52
     * @Param [monthsDiff]
     * @return java.lang.Integer
     * @Description 区分工龄类型
     */
    private Integer getStandingType(Integer monthsDiff) {
        Integer standingType = ReportDataConstant.StandingType.NOT_COUNT;
        if (monthsDiff <= ReportDataConstant.Standing.THREE_MOUTH) {
            standingType = ReportDataConstant.StandingType.THREE_MOUTH;
        } else if (monthsDiff > ReportDataConstant.Standing.THREE_MOUTH && monthsDiff <= ReportDataConstant.Standing.SIX_MOUTH) {
            standingType = ReportDataConstant.StandingType.SIX_MOUTH;
        } else if (monthsDiff > ReportDataConstant.Standing.SIX_MOUTH && monthsDiff <= ReportDataConstant.Standing.ONE_YEAR) {
            standingType = ReportDataConstant.StandingType.ONE_YEAR;
        } else if (monthsDiff > ReportDataConstant.Standing.ONE_YEAR && monthsDiff <= ReportDataConstant.Standing.THREE_YEAR) {
            standingType = ReportDataConstant.StandingType.THREE_YEAR;
        } else if (monthsDiff >= ReportDataConstant.Standing.FIVE_YEAR) {
            standingType = ReportDataConstant.StandingType.FIVE_YEAR;
        }
        return standingType;
    }

    /**
     * 人效分析
     * @param queryDto
     * @return
     */
    @Override
    public List<EffectAnalysisVo> findEffectAnalysis(StaffAnalysisDto queryDto){
        //上期时间
        List<Date> dateList = DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
        if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
            //上期时间
            queryDto.setPrevBeginDate(dateList.get(Constant.Number.ZERO));
            queryDto.setPrevEndDate(dateList.get(Constant.Number.ONE));
        }
        //去年同期
        List<Date> lastYearDateList = DateUtils.getLastYearDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
        if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
            queryDto.setLastYearBeginDate(lastYearDateList.get(Constant.Number.ZERO));
            queryDto.setLastYearEndDate(lastYearDateList.get(Constant.Number.ONE));
        }
        //查询门店员工人数
        List<BaseUserVo> userInfoList = staffAnalysisMapper.findUserInfoByShopIds(queryDto);
        Map<String,BaseUserVo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(BaseUserVo::getUserId,BaseUserVo->BaseUserVo));
        List<String> userIdList = new ArrayList<>();
        for (BaseUserVo userVo : userInfoList){
            String userId =userVo.getUserId()!=null?userVo.getUserId():Constant.Character.NULL_VALUE;
            userIdList.add(userId);
        }
        //门店员工出勤人数
        queryDto.setUserIdList(userIdList);
        List<EffectAnalysisVo> userNumList = staffAnalysisMapper.findUserNumByShopIds(queryDto);
        for(EffectAnalysisVo effectAnalysisVo : userNumList){
            BaseUserVo userVo = userInfoMap.get(effectAnalysisVo.getUserId());
            effectAnalysisVo.setShopId(userVo.getShopId());
        }
        //按门店统计合计
        Map<String, List<EffectAnalysisVo>> shopUserMap = userNumList.stream().collect(Collectors.groupingBy(EffectAnalysisVo::getShopId));
        List<EffectAnalysisVo> shopUserNumList = getShopUserNumList(shopUserMap);
        //门店员工人数
        Map<String,EffectAnalysisVo> userNumMap = shopUserNumList.stream().collect(Collectors.toMap(EffectAnalysisVo::getShopId,EffectAnalysisVo->EffectAnalysisVo));
        //营业额
        List<EffectAnalysisVo> effectAnalysisList = staffAnalysisMapper.findEffectAnalysis(queryDto);
        for(EffectAnalysisVo effectAnalysisVo : effectAnalysisList) {
            EffectAnalysisVo effUserNum = userNumMap.get(effectAnalysisVo.getShopId());
            if (effUserNum != null) {
                effectAnalysisVo.setCurrStaffNum(effUserNum.getCurrStaffNum());
                effectAnalysisVo.setPrevStaffNum(effUserNum.getPrevStaffNum());
                effectAnalysisVo.setLastYearStaffNum(effUserNum.getLastYearStaffNum());
            } else {
                effectAnalysisVo.setCurrStaffNum(Constant.Number.ZERO);
                effectAnalysisVo.setPrevStaffNum(Constant.Number.ZERO);
                effectAnalysisVo.setLastYearStaffNum(Constant.Number.ZERO);
            }
        }
        //区域统计
        Map<String, List<EffectAnalysisVo>> regionMap = effectAnalysisList.stream().collect(Collectors.groupingBy(EffectAnalysisVo::getRegionId));
        //品牌统计
        Map<String, List<EffectAnalysisVo>> brandMap = effectAnalysisList.stream().collect(Collectors.groupingBy(EffectAnalysisVo::getBrandId));
        effectAnalysisList.addAll(getGroupEffectAnalysisList("region", regionMap));
        List<EffectAnalysisVo> brandEffctList = getGroupEffectAnalysisList("brand", brandMap);
        effectAnalysisList.addAll(getGroupEffectAnalysisList("brand", brandMap));
        //全部品牌合计统计
        effectAnalysisList.add(getSumEffect(brandEffctList));
        if(effectAnalysisList!=null && effectAnalysisList.size()>Constant.Number.ZERO){
            //计算人效
            for(EffectAnalysisVo effectAnalysisVo : effectAnalysisList){
                getStaffEffectInfo(effectAnalysisVo);
            }
        }
        return effectAnalysisList;
    }

    /**
     * 计算人效
     * @param effectAnalysisVo
     * @return
     */
    public EffectAnalysisVo getStaffEffectInfo(EffectAnalysisVo effectAnalysisVo) {
        //本期人效
        BigDecimal currEfficiency = BigDecimalUtils.divideMethod(effectAnalysisVo.getCurrAmount(),new BigDecimal(effectAnalysisVo.getCurrStaffNum()),Constant.Number.TWO);
        effectAnalysisVo.setCurrEffect(currEfficiency);
        //上期人效
        BigDecimal prevEfficiency = BigDecimalUtils.divideMethod(effectAnalysisVo.getPrevAmount(),new BigDecimal(effectAnalysisVo.getPrevStaffNum()),Constant.Number.TWO);
        effectAnalysisVo.setPrevEffect(prevEfficiency);
        //去年同期人效
        BigDecimal lastYearEfficiency = BigDecimalUtils.divideMethod(effectAnalysisVo.getLastYearAmount(),new BigDecimal(effectAnalysisVo.getLastYearStaffNum()),Constant.Number.TWO);
        effectAnalysisVo.setLastYearEffect(lastYearEfficiency);
        //同比-较上年(%)-员工人数=（本期员工人数-上年同期员工人数）/上年同期员工人数
        BigDecimal overYearStaffNum =BigDecimalUtils.divideForRatioOrPercent((new BigDecimal(effectAnalysisVo.getCurrStaffNum()).subtract(new BigDecimal(effectAnalysisVo.getLastYearStaffNum()))), new BigDecimal(effectAnalysisVo.getLastYearStaffNum()), Constant.Number.TWO);
        effectAnalysisVo.setOverYearStaffNum(overYearStaffNum);
        //同比-较上年(%)-人效=（本期人效-上年同期人效）/上年人效
        BigDecimal overYearEffect =BigDecimalUtils.divideForRatioOrPercent(((effectAnalysisVo.getCurrEffect()).subtract(effectAnalysisVo.getLastYearEffect())), effectAnalysisVo.getLastYearEffect(), Constant.Number.TWO);
        effectAnalysisVo.setOverYearEffect(overYearEffect);
        //环比-较上期(%)-员工人数=（本期员工人数-上期员工人数）/上期员工人数
        BigDecimal linkRatioStaffNum =BigDecimalUtils.divideForRatioOrPercent(((new BigDecimal(effectAnalysisVo.getCurrStaffNum())).subtract(new BigDecimal(effectAnalysisVo.getPrevStaffNum()))), new BigDecimal(effectAnalysisVo.getPrevStaffNum()), Constant.Number.TWO);
        effectAnalysisVo.setLinkRatioStaffNum(linkRatioStaffNum);
        //同比-较上期(%)-人效=（本期人效-上期人效）/上期人效
        BigDecimal linkRatioPerEffect =BigDecimalUtils.divideForRatioOrPercent(((effectAnalysisVo.getCurrEffect()).subtract(effectAnalysisVo.getPrevEffect())), effectAnalysisVo.getPrevEffect(), Constant.Number.TWO);
        effectAnalysisVo.setLinkRatioEffect(linkRatioPerEffect);
        return effectAnalysisVo;
    }

    /**
     * 全部品牌合计
     * @param effectList
     * @return
     */
    private EffectAnalysisVo getSumEffect(List<EffectAnalysisVo> effectList) {
        EffectAnalysisVo resEffectVo = new EffectAnalysisVo();
        resEffectVo.setType(ReportDataConstant.ReportConstant.ALL);
        resEffectVo.setTypeId(ReportDataConstant.ReportConstant.NEGATIVE_ONE_STR);
        resEffectVo.setShopId("");
        resEffectVo.setShopName("全部门店");
        resEffectVo.setRegionId("");
        resEffectVo.setRegionName("全部区域");
        resEffectVo.setBrandId("");
        resEffectVo.setBrandName("全部品牌");
        //本期营业额
        BigDecimal currAmount = Constant.Number.ZEROB;
        //本期人数
        Integer currStaffNum = Constant.Number.ZERO;
         //上期营业额
        BigDecimal prevAmount = Constant.Number.ZEROB;
        //上期员工人数
        Integer prevStaffNum = Constant.Number.ZERO;
        //去年同期营业额
        BigDecimal lastYearAmount =Constant.Number.ZEROB;
        //去年同期员工人数
        Integer lastYearStaffNum= Constant.Number.ZERO;
        for (EffectAnalysisVo effectAnalysisVo : effectList) {
            currAmount=currAmount.add(effectAnalysisVo.getCurrAmount());
            currStaffNum=currStaffNum+effectAnalysisVo.getCurrStaffNum();
            prevAmount=prevAmount.add(effectAnalysisVo.getPrevAmount());
            prevStaffNum=prevStaffNum+effectAnalysisVo.getPrevStaffNum();
            lastYearAmount=lastYearAmount.add(effectAnalysisVo.getLastYearAmount());
            lastYearStaffNum=lastYearStaffNum+effectAnalysisVo.getLastYearStaffNum();
        }
        resEffectVo.setCurrAmount(currAmount);
        resEffectVo.setCurrStaffNum(currStaffNum);
        resEffectVo.setPrevAmount(prevAmount);
        resEffectVo.setPrevStaffNum(prevStaffNum);
        resEffectVo.setLastYearAmount(lastYearAmount);
        resEffectVo.setLastYearStaffNum(lastYearStaffNum);
        return resEffectVo;
    }

    /**
     * 统计门店出勤人数
     * @param groupEffectMap
     * @return
     */
    private List<EffectAnalysisVo> getShopUserNumList( Map<String, List<EffectAnalysisVo>> groupEffectMap) {
        List<EffectAnalysisVo> effectStatiList = new ArrayList<>();
        for (Map.Entry<String, List<EffectAnalysisVo>> groupEffect : groupEffectMap.entrySet()) {
            EffectAnalysisVo groupEffectVo = new EffectAnalysisVo();
            List<EffectAnalysisVo> list = groupEffect.getValue();
            groupEffectVo.setShopId(groupEffect.getKey());
            //本期员工人数
            Integer currStaffNum = Constant.Number.ZERO;
            //上期员工人数
            Integer prevStaffNum = Constant.Number.ZERO;
            //去年同期员工人数
            Integer lastYearStaffNum= Constant.Number.ZERO;
            for (EffectAnalysisVo effectVo : list) {
                currStaffNum=currStaffNum+effectVo.getCurrStaffNum();
                prevStaffNum=prevStaffNum+effectVo.getPrevStaffNum();
                lastYearStaffNum=lastYearStaffNum+effectVo.getLastYearStaffNum();
            }
            groupEffectVo.setCurrStaffNum(currStaffNum);
            groupEffectVo.setPrevStaffNum(prevStaffNum);
            groupEffectVo.setLastYearStaffNum(lastYearStaffNum);
            effectStatiList.add(groupEffectVo);
        }
        return effectStatiList;
    }


    /**
     * 人效分析报表根据品牌/区域分组处理
     *
     * @param type
     * @param groupEffectMap
     * @return
     */
    private List<EffectAnalysisVo> getGroupEffectAnalysisList(String type, Map<String, List<EffectAnalysisVo>> groupEffectMap) {
        List<EffectAnalysisVo> effectStatiList = new ArrayList<>();
        for (Map.Entry<String, List<EffectAnalysisVo>> groupEffect : groupEffectMap.entrySet()) {
            String type_id = groupEffect.getKey();
            EffectAnalysisVo groupEffectVo = new EffectAnalysisVo();
            groupEffectVo.setType(type);
            groupEffectVo.setTypeId(type_id);
            List<EffectAnalysisVo> list = groupEffect.getValue();
            //本期营业额
            BigDecimal currAmount = Constant.Number.ZEROB;
            //本期人数
            Integer currStaffNum = Constant.Number.ZERO;
            //上期营业额
            BigDecimal prevAmount = Constant.Number.ZEROB;
            //上期员工人数
            Integer prevStaffNum = Constant.Number.ZERO;
            //去年同期营业额
            BigDecimal lastYearAmount =Constant.Number.ZEROB;
            //去年同期员工人数
            Integer lastYearStaffNum= Constant.Number.ZERO;
            for (EffectAnalysisVo effectVo : list) {
                groupEffectVo.setShopId("");
                groupEffectVo.setShopName("全部门店");
                if ("region".equals(type)) {
                    groupEffectVo.setRegionId(effectVo.getRegionId());
                    groupEffectVo.setRegionName(effectVo.getRegionName());
                } else {
                    groupEffectVo.setRegionId("");
                    groupEffectVo.setRegionName("全部区域");
                }
                groupEffectVo.setBrandId(effectVo.getBrandId());
                groupEffectVo.setBrandName(effectVo.getBrandName());
                currAmount=currAmount.add(effectVo.getCurrAmount());
                currStaffNum=currStaffNum+effectVo.getCurrStaffNum();
                prevAmount=prevAmount.add(effectVo.getPrevAmount());
                prevStaffNum=prevStaffNum+effectVo.getPrevStaffNum();
                lastYearAmount=lastYearAmount.add(effectVo.getLastYearAmount());
                lastYearStaffNum=lastYearStaffNum+effectVo.getLastYearStaffNum();
            }
            groupEffectVo.setCurrAmount(currAmount);
            groupEffectVo.setCurrStaffNum(currStaffNum);
            groupEffectVo.setPrevAmount(prevAmount);
            groupEffectVo.setPrevStaffNum(prevStaffNum);
            groupEffectVo.setLastYearAmount(lastYearAmount);
            groupEffectVo.setLastYearStaffNum(lastYearStaffNum);
            getStaffEffectInfo(groupEffectVo);
            effectStatiList.add(groupEffectVo);
        }
        return effectStatiList;
    }

    /**
     * @Author ljc
     * @Date  2020/3/26
     * @Param [excelExportDto, response]
     * @return void
     * @Description 人效分析导出
     */
    @Override
    public void exportEffectAnalysis(ExcelExportDto excelExportDto, HttpServletResponse response) {
        StaffAnalysisDto queryDto = getStaffAnalysisDto(excelExportDto);
        List<EffectAnalysisVo> effectAnalysisVoList = findEffectAnalysis(queryDto);
        //表头
        List<TitleEntity> titleList=setQueryInfoTitle(excelExportDto);
        //下载时间的 ID 作为以下标题的根ID
        int tId = Constant.Number.EIGHT;
        String pId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity=new TitleEntity(String.valueOf(tId),pId,"品牌","brandName");tId = tId+1;
        TitleEntity entity1=new TitleEntity(String.valueOf(tId),pId,"区域","regionName");tId = tId+1;
        TitleEntity entity2=new TitleEntity(String.valueOf(tId),pId,"门店","shopName");tId = tId+1;
        TitleEntity entity4=new TitleEntity(String.valueOf(tId),pId,"本期",null);
        //本期分析 根ID
        String currId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity5=new TitleEntity(String.valueOf(tId),currId,"营业额","currAmount");tId = tId+1;
        TitleEntity entity6=new TitleEntity(String.valueOf(tId),currId,"员工人数","currStaffNum");tId = tId+1;
        TitleEntity entity7=new TitleEntity(String.valueOf(tId),currId,"人效(元/人)","currEffect");tId = tId+1;
        TitleEntity entity8=new TitleEntity(String.valueOf(tId),pId,"上期",null);
        //上期 根ID
        String prevId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity9=new TitleEntity(String.valueOf(tId),prevId,"营业额","prevAmount");tId = tId+1;
        TitleEntity entity10=new TitleEntity(String.valueOf(tId),prevId,"员工人数","prevStaffNum");tId = tId+1;
        TitleEntity entity11=new TitleEntity(String.valueOf(tId),prevId,"人效(元/人)","prevEffect");tId = tId+1;
        TitleEntity entity12=new TitleEntity(String.valueOf(tId),pId,"上年同期",null);
        //去年同期
        String lastYearId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity13=new TitleEntity(String.valueOf(tId),lastYearId,"营业额","lastYearAmount");tId = tId+1;
        TitleEntity entity14=new TitleEntity(String.valueOf(tId),lastYearId,"员工人数","lastYearStaffNum");tId = tId+1;
        TitleEntity entity15=new TitleEntity(String.valueOf(tId),lastYearId,"人效(元/人)","lastYearEffect");tId = tId+1;
        TitleEntity entity16=new TitleEntity(String.valueOf(tId),pId,"同比",null);
        //较上年(百分比)
        String overYearId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity17=new TitleEntity(String.valueOf(tId),overYearId,"员工人数","overYearStaffNum");tId = tId+1;
        TitleEntity entity18=new TitleEntity(String.valueOf(tId),overYearId,"人效","overYearEffect");tId = tId+1;
        TitleEntity entity19=new TitleEntity(String.valueOf(tId),pId,"环比",null);
        //较上期(百分比)
        String linkRatioId = String.valueOf(tId);tId = tId+1;
        TitleEntity entity20=new TitleEntity(String.valueOf(tId),linkRatioId,"员工人数","linkRatioStaffNum");tId = tId+1;
        TitleEntity entity21=new TitleEntity(String.valueOf(tId),linkRatioId,"人效","linkRatioEffect");

        titleList.add(entity);titleList.add(entity1);titleList.add(entity2);titleList.add(entity4);titleList.add(entity5);
        titleList.add(entity6);titleList.add(entity7);titleList.add(entity8);titleList.add(entity9);titleList.add(entity10);titleList.add(entity11);
        titleList.add(entity12);titleList.add(entity13);titleList.add(entity14);titleList.add(entity15);titleList.add(entity16);titleList.add(entity17);
        titleList.add(entity18);titleList.add(entity19);titleList.add(entity20);titleList.add(entity21);
        List<Map<String,Object>> rowList=new ArrayList<>();
        if(!FastUtils.checkNullOrEmpty(effectAnalysisVoList)){
            //加入千分位，保留2位小数，并且不够补0
            DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
            DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
            List<EffectAnalysisVo> voList;
            //根据类型过滤
            if (ReportDataConstant.Finance.TYPE_SHOP.equals(excelExportDto.getType())) {
                voList = effectAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_BRAND.equals(excelExportDto.getType())) {
                voList = effectAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
            } else if (ReportDataConstant.Finance.TYPE_REGION.equals(excelExportDto.getType())) {
                voList = effectAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
            } else {
                voList = effectAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
            }
            for(EffectAnalysisVo vo : voList){
                Map map = new HashMap<String,Object>();
                map.put("brandName",vo.getBrandName());
                map.put("regionName",vo.getRegionName());
                map.put("shopName",vo.getShopName());
                map.put("currAmount",getNotZeroByConvert(vo.getCurrAmount(), df));
                map.put("currStaffNum",getNotZeroByConvert(vo.getCurrStaffNum(),df2));
                map.put("currEffect",getNotZeroByConvert(vo.getCurrEffect(),df));
                map.put("prevAmount",getNotZeroByConvert(vo.getPrevAmount(), df));
                map.put("prevStaffNum",getNotZeroByConvert(vo.getPrevStaffNum(),df2));
                map.put("prevEffect",getNotZeroByConvert(vo.getPrevEffect(),df));
                map.put("lastYearAmount",getNotZeroByConvert(vo.getLastYearAmount(), df));
                map.put("lastYearStaffNum",getNotZeroByConvert(vo.getLastYearStaffNum(),df2));
                map.put("lastYearEffect",getNotZeroByConvert(vo.getLastYearEffect(),df));
                map.put("overYearStaffNum",getStringByConvert(vo.getOverYearStaffNum(),df));
                map.put("overYearEffect",getStringByConvert(vo.getOverYearEffect(),df));
                map.put("linkRatioStaffNum",getStringByConvert(vo.getLinkRatioStaffNum(),df));
                map.put("linkRatioEffect",getStringByConvert(vo.getLinkRatioEffect(),df));
                rowList.add(map);
            }
        }
        deskTypeAnalysisService.exportMethod(response, titleList, rowList);
    }

    /**
     * 品牌奖金
     * @param queryDto
     * @return
     */
    @Override
    public List<BrandBonusVo> findBrandBonusAnalysis(StaffAnalysisDto queryDto){
        //品牌奖金-门店信息
        List<BrandBonusVo> brandBonusShopList = staffAnalysisMapper.findShopInfo(queryDto);
        try{
            queryDto.setPostName(Constant.Character.Percent+ReportDataConstant.BrandBonus.FAMILYLEADER+Constant.Character.Percent);
            //查询奖金比例/年终比例/实发比例
            Map<String,Object> paramMap = new HashMap<>();
            List<String> modelNameList = new ArrayList<>();
            modelNameList.add(ReportDataConstant.BrandBonus.HR_REWARD_RATIO);
            modelNameList.add(ReportDataConstant.BrandBonus.HR_REAL_RATIO);
            modelNameList.add(ReportDataConstant.BrandBonus.HR_END_YEAR_RATIO);
            paramMap.put("enteId",queryDto.getEnteId());
            paramMap.put("modelNameList",modelNameList);
            List<DictVo> dictList = staffAnalysisMapper.findDictListByModelName(paramMap);
            Map<String,DictVo> dictMap = dictList.stream().collect(Collectors.toMap(DictVo::getModelName,DictVo->DictVo));
            DictVo dictVo = dictMap.get(ReportDataConstant.BrandBonus.HR_REWARD_RATIO);
            //奖金比列
            BigDecimal rewardRatio = new BigDecimal(dictVo!=null?dictVo.getModelValue():"0");
            //实发比列
            DictVo dictRealRatioVo = dictMap.get(ReportDataConstant.BrandBonus.HR_REAL_RATIO);
            BigDecimal realRatio = new BigDecimal(dictRealRatioVo.getModelValue());
            //年终比列
            DictVo dictEndYearRatioVo = dictMap.get(ReportDataConstant.BrandBonus.HR_END_YEAR_RATIO);
            BigDecimal endYearRatio = new BigDecimal(dictEndYearRatioVo.getModelValue());
            //查询员工信息(家族长)
            List<BrandBonusVo> brandBonusUserList = staffAnalysisMapper.findUserInfoListByShopIds(queryDto);
            Map<String,BrandBonusVo> brandBonusUserMap = null;
            //存储按月查询的会员出勤及请假
            Map<String,Map<String,BrandBonusVo>> userHourMap = new HashMap<>();
            List<String> userIdList = new ArrayList<>();
            Date endDate = queryDto.getEndDate();
            paramMap.clear();
            paramMap.put("startDate",DateUtils.dateConvertString(queryDto.getBeginDate(),DateUtils.PATTERN_DAY));
            paramMap.put("endDate",DateUtils.dateConvertString(queryDto.getEndDate(),DateUtils.PATTERN_DAY));
            //按月分割日期，返回值为[2019-08-30, 2019-08-31, 2019-09-01, 2019-09-13]，每两个时间为一个时间段
            List<String> listMonth = DateUtils.getDateByStatisticsType("month",paramMap);
            if(brandBonusUserList!=null && brandBonusUserList.size()>Constant.Number.ZERO){
                for(BrandBonusVo brandBonusUserVo : brandBonusUserList){
                    userIdList.add(brandBonusUserVo.getUserId());
                }
                brandBonusUserMap=brandBonusUserList.stream().collect(Collectors.toMap(BrandBonusVo::getRegionId,user->user ,(oldValue,newValue)->newValue));
                queryDto.setUserIdList(userIdList);
                StaffAnalysisDto staffDto = new StaffAnalysisDto();

                for(int i=0;i<listMonth.size();i+=2){
                    Date begionDateAtr = DateUtils.parseDate(listMonth.get(i),DateUtils.PATTERN_DAY);
                    Date endDateAtr = DateUtils.parseDate(listMonth.get(i+1),DateUtils.PATTERN_DAY);
                    staffDto.setShopIdList(queryDto.getShopIdList());
                    staffDto.setShopTypeIdList(queryDto.getShopTypeIdList());
                    staffDto.setBeginDate(begionDateAtr);
                    staffDto.setEndDate(endDateAtr);
                    staffDto.setEnteId(queryDto.getEnteId());
                    staffDto.setUserIdList(userIdList);
                    //出勤查询
                    //门店员工时间段内出勤天数
                    List<BrandBonusVo> userAttendHourList = staffAnalysisMapper.findUserStandardHour(staffDto);
                    Map<String,BrandBonusVo> userAttendHourMap  = userAttendHourList.stream().collect(Collectors.toMap(BrandBonusVo::getUserId,BrandBonusVo->BrandBonusVo));
                    //请假查询
                    //门店员工请假时长
                    String endDateStr = DateUtils.format(staffDto.getEndDate(),DateUtils.PATTERN_MONTH)+ReportDataConstant.BrandBonus.DATE_STR;
                    staffDto.setEndDate(DateUtils.parseDate(endDateStr,DateUtils.PATTERN_SECOND));
                    List<BrandBonusVo> userLeaveHourList = staffAnalysisMapper.findUserLeaveHour(staffDto);
                    Map<String,BrandBonusVo> userLeaveHourMap  = userLeaveHourList.stream().collect(Collectors.toMap(BrandBonusVo::getUserId,BrandBonusVo->BrandBonusVo));
                    userHourMap.put(begionDateAtr+Constant.Character.MIDDLE_LINE+ReportDataConstant.BrandBonus.USERATTEND,userAttendHourMap);
                    userHourMap.put(begionDateAtr+Constant.Character.MIDDLE_LINE+ReportDataConstant.BrandBonus.USERLEAVE,userLeaveHourMap);
                    staffDto.setEndDate(endDateAtr);
                    List<BrandBonusVo> netProfitList = getNetProfit(staffDto);
                    Map<String,BrandBonusVo> netProfitMap  = netProfitList.stream().collect(Collectors.toMap(BrandBonusVo::getShopId,BrandBonusVo->BrandBonusVo));
                    userHourMap.put(begionDateAtr+Constant.Character.MIDDLE_LINE+ReportDataConstant.BrandBonus.NETPROFIT,netProfitMap);
                }
            }

            if(brandBonusShopList!=null && brandBonusShopList.size()>Constant.Number.ZERO){
                //基数--计算各门店净利润
                queryDto.setEndDate(endDate);
                getNetProfit(queryDto,brandBonusShopList);
                for (BrandBonusVo brandBonus : brandBonusShopList){
                    //奖金比例
                    brandBonus.setRewardRatio(rewardRatio);
                    //缺勤天数统计
                    BigDecimal totalAbsence = BigDecimal.ZERO;
                    //总额统计
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    //净利润
                    BigDecimal totalNetProfit = BigDecimal.ZERO;
                    if(brandBonusUserMap!=null){
                        //匹配人员信息
                        BrandBonusVo brandBonusUserVo = brandBonusUserMap.get(brandBonus.getRegionId());
                        //计算每月的 人事比例及总额
                        BigDecimal finalPersonnelRatio = BigDecimal.ZERO;
                        int datePeriodNum = 0;

                        for(int i=0;i<listMonth.size();i+=2) {
                            Date begionDateAtr = DateUtils.parseDate(listMonth.get(i), DateUtils.PATTERN_DAY);
                            Date endDateAtr = DateUtils.parseDate(listMonth.get(i + 1), DateUtils.PATTERN_DAY);
                            BigDecimal personnelRatio = BigDecimal.ZERO;
                            if(brandBonusUserVo!=null){
                                //入职时间
                                Date hiredate = brandBonusUserVo.getHiredate();
                                //入职时间<=查询结束日期，则展示家族长信息
                                if(queryDto.getEndDate().compareTo(hiredate)>=Constant.Number.ZERO ){
                                    String userId = brandBonusUserVo!=null?brandBonusUserVo.getUserId():Constant.Character.NULL_VALUE;
                                    String userName = brandBonusUserVo!=null?brandBonusUserVo.getUserName():Constant.Character.NULL_VALUE;
                                    Date positiveTime = brandBonusUserVo!=null?brandBonusUserVo.getPositiveTime():null;
                                    brandBonus.setUserId(userId);
                                    brandBonus.setUserName(userName);
                                    brandBonus.setPositiveTime(positiveTime);
                                    Date positiveTimeMonth = DateUtils.parseDate(DateUtils.dateConvertString(positiveTime,DateUtils.PATTERN_MONTH),DateUtils.PATTERN_MONTH);
                                    Date endDateMonth = DateUtils.parseDate(DateUtils.dateConvertString(endDateAtr,DateUtils.PATTERN_MONTH),DateUtils.PATTERN_MONTH);
                                    //人事比例
                                    if(endDateAtr.compareTo(hiredate)>=Constant.Number.ZERO){
                                        datePeriodNum++;
                                        if(StringUtil.isNotBlank(brandBonus.getPositiveTime())){
                                            //转正月份>查询结束日期的月份，则人事比例为100%；（转正当月为80%，次月100%）
                                            if(positiveTimeMonth.getTime()<endDateMonth.getTime()){
                                                //存在转正时间表示已转正人事比例为100%
                                                personnelRatio=ReportDataConstant.BrandBonus.ONEHUNDREDB;
                                            }else{
                                                //未转正员工及转正当月人事比例为80%
                                                personnelRatio=ReportDataConstant.BrandBonus.EIGHTYB;
                                            }
                                        }else{
                                            //未转正员工人事比例为80%
                                            personnelRatio=ReportDataConstant.BrandBonus.EIGHTYB;
                                        }
                                    }
                                }
                            }
                            finalPersonnelRatio = finalPersonnelRatio.add(personnelRatio);
                            //计算请假时长
                            //缺勤天数
                            Map<String,BrandBonusVo> userLeaveHourMap = userHourMap!=null?userHourMap.get(begionDateAtr+Constant.Character.MIDDLE_LINE+ReportDataConstant.BrandBonus.USERLEAVE):null;
                            BrandBonusVo userLeaveHour = userLeaveHourMap!=null?userLeaveHourMap.get(brandBonus.getUserId()):null;
                            BigDecimal absence = Constant.Number.ZEROB;
                            //时长为小时，计算缺勤天数需除8
                            if(userLeaveHour!=null){
                                BigDecimal leaveHour = userLeaveHour.getLeaveHour();
                                absence = leaveHour.divide(new BigDecimal(Constant.Number.EIGHT),Constant.Number.TWO,BigDecimal.ROUND_HALF_UP);
                            }
                            totalAbsence = totalAbsence.add(absence);
                            //出勤天数
                            //缺勤天数
                            Map<String,BrandBonusVo> userAttendHourMap = userHourMap!=null?userHourMap.get(begionDateAtr+Constant.Character.MIDDLE_LINE+ReportDataConstant.BrandBonus.USERATTEND):null;
                            BrandBonusVo userAttendHour = userAttendHourMap!=null?userAttendHourMap.get(brandBonus.getUserId()):null;
                            //取标准工时
                            BigDecimal attendHour = userAttendHour!=null?userAttendHour.getStandardHour():Constant.Number.ZEROB;
                            BigDecimal attendDays = BigDecimal.ZERO;
                            if(attendHour.compareTo(BigDecimal.ZERO)>Constant.Number.ZERO){
                                attendDays=attendHour.divide(new BigDecimal(Constant.Number.EIGHT),Constant.Number.TWO,BigDecimal.ROUND_HALF_UP);
                            }
                            //净利润
                            Map<String,BrandBonusVo> netProfitMap = userHourMap!=null?userHourMap.get(begionDateAtr+Constant.Character.MIDDLE_LINE+ReportDataConstant.BrandBonus.NETPROFIT):null;
                            BrandBonusVo netProfitVo = netProfitMap!=null?netProfitMap.get(brandBonus.getShopId()):null;
                            BigDecimal netProfit =(netProfitVo!=null&& netProfitVo.getNetProfit()!=null)?netProfitVo.getNetProfit():BigDecimal.ZERO;
                            totalNetProfit = totalNetProfit.add(netProfit);
                            //总额 基数*人事比例*奖励比例*（对应期间的总出勤天数-缺勤天数）/对应期间总出勤天数
                            BigDecimal amount = BigDecimal.ZERO;
                            //总出勤天数-缺勤天数
                            BigDecimal dayNum = attendDays.subtract(absence);
//                            if(attendDays.compareTo(BigDecimal.ZERO)==0){
//                                amount = BigDecimal.ZERO;
//                            }else
                            if(dayNum.compareTo(BigDecimal.ZERO)>Constant.Number.ZERO || attendDays.compareTo(BigDecimal.ZERO)<Constant.Number.ZERO){
                                amount = netProfit.multiply(personnelRatio).multiply(brandBonus.getRewardRatio()).multiply(dayNum)
                                        .divide(attendDays,Constant.Number.TWO,BigDecimal.ROUND_HALF_UP).divide(Constant.Number.HUNDRED).divide(Constant.Number.HUNDRED,Constant.Number.TWO,BigDecimal.ROUND_HALF_UP);
                            }else{
                                amount = netProfit.multiply(personnelRatio).multiply(brandBonus.getRewardRatio())
                                        .divide(Constant.Number.HUNDRED).divide(Constant.Number.HUNDRED,Constant.Number.TWO,BigDecimal.ROUND_HALF_UP);
                            }
                            totalAmount = totalAmount.add(amount);
                        }
                        if(finalPersonnelRatio.compareTo(BigDecimal.ZERO)>Constant.Number.ZERO && datePeriodNum>Constant.Number.ONE){
                            finalPersonnelRatio = finalPersonnelRatio.divide(new BigDecimal(datePeriodNum),Constant.Number.TWO,BigDecimal.ROUND_HALF_UP);
                        }
                        brandBonus.setPersonnelRatio(finalPersonnelRatio);
                    }
                    //净利润
                    brandBonus.setNetProfit(totalNetProfit);
                    //缺勤天数
                    brandBonus.setAbsence(totalAbsence);
                    //总额
                    brandBonus.setAmount(totalAmount);
                    //年终待发=总额*对应比例(30%)
                    BigDecimal endYearAmount = totalAmount.multiply(endYearRatio).divide(Constant.Number.HUNDRED,Constant.Number.TWO,BigDecimal.ROUND_HALF_UP);
                    brandBonus.setEndYearAmount(endYearAmount);
                    //当期实发=总额*对应比例(70%)
                    BigDecimal currentAmount = totalAmount.multiply(realRatio).divide(Constant.Number.HUNDRED,Constant.Number.TWO,BigDecimal.ROUND_HALF_UP);
                    brandBonus.setCurrentAmount(currentAmount);
                }
                //区域统计
                Map<String, List<BrandBonusVo>> regionMap = brandBonusShopList.stream().collect(Collectors.groupingBy(BrandBonusVo::getRegionId));
                List<BrandBonusVo> brandListForRegion = getGroupBrandBonusList("region", regionMap);
                brandBonusShopList.addAll(brandListForRegion);
                //品牌统计
                Map<String, List<BrandBonusVo>> brandMap = brandListForRegion.stream().collect(Collectors.groupingBy(BrandBonusVo::getBrandId));
                List<BrandBonusVo> brandList = getGroupBrandBonusList("brand", brandMap);
                brandBonusShopList.addAll(getGroupBrandBonusList("brand", brandMap));
                //全部品牌合计统计
                brandBonusShopList.add(getSumBrandBonus(brandList));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return brandBonusShopList;

    }


    /**
     * @Author ljc
     * @Date 2020/4/13
     * @Param [queryDto, shareAnalysisVos, beginDate, endDate]
     * @return void
     * @Description 净利润
     */
    private void getNetProfit(StaffAnalysisDto queryDto, List<BrandBonusVo> brandBonusList) {
        //查询时间格式转化
        queryDto.setBeginNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getBeginDate(), DateUtils.PATTERN_DAY)));
        queryDto.setEndNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getEndDate(), DateUtils.PATTERN_DAY)));
        //净利润
        List<BrandBonusVo> profitList = staffAnalysisMapper.findNetProfitList(queryDto);
        MergeUtil.merge(brandBonusList, profitList,
                BrandBonusVo::getShopId, BrandBonusVo::getShopId,
                (brandBonusVo, vo) -> {
                    brandBonusVo.setNetProfit(vo.getNetProfit());
                }
        );
    }

    /**
     * @Author ljc
     * @Date 2020/4/13
     * @Param [queryDto, shareAnalysisVos, beginDate, endDate]
     * @return void
     * @Description 净利润
     */
    private List<BrandBonusVo> getNetProfit(StaffAnalysisDto queryDto) {
        //查询时间格式转化
        queryDto.setBeginNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getBeginDate(), DateUtils.PATTERN_DAY)));
        queryDto.setEndNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getEndDate(), DateUtils.PATTERN_DAY)));
        //净利润
        List<BrandBonusVo> profitList = staffAnalysisMapper.findNetProfitList(queryDto);
        return profitList;
    }

    /**
     * 品牌奖金报表根据品牌/区域分组处理
     * @param type
     * @param groupBrandBonusMap
     * @return
     */
    private List<BrandBonusVo> getGroupBrandBonusList(String type, Map<String, List<BrandBonusVo>> groupBrandBonusMap) {
        List<BrandBonusVo> brandBonusList = new ArrayList<>();
        for (Map.Entry<String, List<BrandBonusVo>> groupBrandBonus : groupBrandBonusMap.entrySet()) {
            String type_id = groupBrandBonus.getKey();
            BrandBonusVo groupBrandBonusVo = new BrandBonusVo();
            groupBrandBonusVo.setType(type);
            groupBrandBonusVo.setTypeId(type_id);
            List<BrandBonusVo> list = groupBrandBonus.getValue();
            //区域合并后计算总额/年终待发/当期实发
            //净利润
            BigDecimal netProfit = BigDecimal.ZERO;
            //总额
            BigDecimal amount = BigDecimal.ZERO;
            //年终待发
            BigDecimal endYearAmount = BigDecimal.ZERO;
            //当期实发
            BigDecimal currentAmount= BigDecimal.ZERO;
            for (BrandBonusVo brandBonusVo : list) {
                groupBrandBonusVo.setShopId("");
                groupBrandBonusVo.setShopName("全部门店");
                if ("region".equals(type)) {
                    groupBrandBonusVo.setUserId(brandBonusVo.getUserId());
                    groupBrandBonusVo.setUserName(brandBonusVo.getUserName());
                    groupBrandBonusVo.setRegionId(brandBonusVo.getRegionId());
                    groupBrandBonusVo.setRegionName(brandBonusVo.getRegionName());
                } else {
                    groupBrandBonusVo.setRegionId("");
                    groupBrandBonusVo.setRegionName("全部区域");
                }
                netProfit = netProfit.add(brandBonusVo.getNetProfit());
                amount = amount.add(brandBonusVo.getAmount());
                endYearAmount = endYearAmount.add(brandBonusVo.getEndYearAmount());
                currentAmount = currentAmount.add(brandBonusVo.getCurrentAmount());
                groupBrandBonusVo.setNetProfit(netProfit);
                groupBrandBonusVo.setAmount(amount);
                groupBrandBonusVo.setEndYearAmount(endYearAmount);
                groupBrandBonusVo.setCurrentAmount(currentAmount);
                groupBrandBonusVo.setBrandId(brandBonusVo.getBrandId());
                groupBrandBonusVo.setBrandName(brandBonusVo.getBrandName());
            }
            brandBonusList.add(groupBrandBonusVo);
        }
        return brandBonusList;
    }

    /**
     * 全部品牌合计
     * @param brandBonusList
     * @return
     */
    private BrandBonusVo getSumBrandBonus(List<BrandBonusVo> brandBonusList) {
        BrandBonusVo brandBonusVo = new BrandBonusVo();
        brandBonusVo.setType(ReportDataConstant.ReportConstant.ALL);
        brandBonusVo.setTypeId(ReportDataConstant.ReportConstant.NEGATIVE_ONE_STR);
        brandBonusVo.setShopId("");
        brandBonusVo.setShopName("全部门店");
        brandBonusVo.setRegionId("");
        brandBonusVo.setRegionName("全部区域");
        brandBonusVo.setBrandId("");
        brandBonusVo.setBrandName("全部品牌");
        //净利润
        BigDecimal netProfit = BigDecimal.ZERO;
        //总额
        BigDecimal amount = BigDecimal.ZERO;
        //年终待发
        BigDecimal endYearAmount = BigDecimal.ZERO;
        //当期实发
        BigDecimal currentAmount= BigDecimal.ZERO;
        for (BrandBonusVo bonusVo : brandBonusList) {
            netProfit = netProfit.add(bonusVo.getNetProfit());
            amount = amount.add(bonusVo.getAmount());
            endYearAmount = endYearAmount.add(bonusVo.getEndYearAmount());
            currentAmount = currentAmount.add(bonusVo.getCurrentAmount());

        }
        brandBonusVo.setNetProfit(netProfit);
        brandBonusVo.setAmount(amount);
        brandBonusVo.setEndYearAmount(endYearAmount);
        brandBonusVo.setCurrentAmount(currentAmount);
        return brandBonusVo;
    }

    /**
     * @Author ljc
     * @Param [excelExportDto, response]
     * @return void
     * @Description 品牌奖金导出
     */
    @Override
    public void exportBrandBonusAnalysis(ExcelExportDto excelExportDto, HttpServletResponse response) {
        StaffAnalysisDto queryDto = new StaffAnalysisDto();
        queryDto.setEnteId(excelExportDto.getEnteId());
        queryDto.setShopIdList(excelExportDto.getShopIdList());
        queryDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        queryDto.setBeginDate(excelExportDto.getBeginDate());
        queryDto.setEndDate(excelExportDto.getEndDate());
        List<BrandBonusVo> brandBonusVoList = findBrandBonusAnalysis(queryDto);
        //过滤结果
        if (StringUtil.isNotBlank(excelExportDto.getType())) {
            brandBonusVoList = brandBonusVoList.stream().filter(obj -> obj.getType().equals(excelExportDto.getType())).collect(Collectors.toList());
        }
        fileService.exportExcelForQueryTerm(response, excelExportDto, brandBonusVoList,
                ExcelColumnConstant.BrandBonusInfo.BRANDE_NAME,
                ExcelColumnConstant.BrandBonusInfo.REGION_NAME,
                ExcelColumnConstant.BrandBonusInfo.USER_NAME,
                ExcelColumnConstant.BrandBonusInfo.SHOP_NAME,
                ExcelColumnConstant.BrandBonusInfo.NET_PROFIT,
                ExcelColumnConstant.BrandBonusInfo.PERSONNEL_RATIO,
                ExcelColumnConstant.BrandBonusInfo.REWARD_RATIO,
                ExcelColumnConstant.BrandBonusInfo.ABSENCE,
                ExcelColumnConstant.BrandBonusInfo.AMOUNT,
                ExcelColumnConstant.BrandBonusInfo.END_YEAR_AMOUNT,
                ExcelColumnConstant.BrandBonusInfo.CURRENT_AMOUNT
        );
    }



}

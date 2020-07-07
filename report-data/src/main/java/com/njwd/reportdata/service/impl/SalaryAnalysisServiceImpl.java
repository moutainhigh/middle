package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.dto.SalaryAnalysisDto;
import com.njwd.entity.reportdata.dto.ShopSalaryDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.MembershipCardAnalysisVo;
import com.njwd.entity.reportdata.vo.ShopSalaryVo;
import com.njwd.entity.reportdata.vo.WageShareAnalysisVo;
import com.njwd.poiexcel.TitleEntity;
import com.njwd.reportdata.mapper.SalaryAnalysisMapper;
import com.njwd.reportdata.mapper.ShopSalaryMapper;
import com.njwd.reportdata.service.BaseShopService;
import com.njwd.reportdata.service.DeskTypeAnalysisService;
import com.njwd.reportdata.service.SalaryAnalysisService;
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
 * @Description: todo
 * @Author LuoY
 * @Date 2019/11/20
 */
@Service
public class SalaryAnalysisServiceImpl implements SalaryAnalysisService {

	@Resource
	private FileService fileService;

	@Resource
	private ShopSalaryMapper shopSalaryMapper;

	@Resource
	private SalaryAnalysisMapper salaryAnalysisMapper;

	@Resource
	private BaseShopService baseShopService;

	@Autowired
	private DeskTypeAnalysisService deskTypeAnalysisService;

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/30 16:05
	 * @Param [queryDto]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.WageShareAnalysisVo>
	 * @Description 工资占销比
	 */
	@Override
	public List<WageShareAnalysisVo> wageShareAnalysis(SalaryAnalysisDto queryDto) {
		List<WageShareAnalysisVo> analysisVoList = new ArrayList<>();
		MembershipCardAnalysisDto dto = new MembershipCardAnalysisDto();
		dto.setShopIdList(queryDto.getShopIdList());
		dto.setShopTypeIdList(queryDto.getShopTypeIdList());
		dto.setEnteId(queryDto.getEnteId());
		//查询 门店信息
		List<MembershipCardAnalysisVo> voList = baseShopService.findShopDetail(dto);
		if (!FastUtils.checkNullOrEmpty(voList)) {
			//数据类型转换
			List<WageShareAnalysisVo> shareAnalysisVos = getWageShareAnalysisVos(voList);
			Date beginDate = queryDto.getBeginDate();
			Date endDate = queryDto.getEndDate();
			Byte dateType = queryDto.getDateType();
			//上期时间
			List<Date> dateList = DateUtils.getLastPeriodDate(beginDate, endDate, dateType);
			if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
				queryDto.setPrevBeginDate(dateList.get(Constant.Number.ZERO));
				queryDto.setPrevEndDate(dateList.get(Constant.Number.ONE));
			}
			//去年时间
			List<Date> lastList = DateUtils.getLastYearDate(beginDate, endDate, dateType);
			if (!FastUtils.checkNullOrEmpty(lastList) && lastList.size() == Constant.Number.TWO) {
				queryDto.setLastYearBeginDate(lastList.get(Constant.Number.ZERO));
				queryDto.setLastYearEndDate(lastList.get(Constant.Number.ONE));
			}
			//营业额
			List<WageShareAnalysisVo> turnoverList = salaryAnalysisMapper.findTurnoverList(queryDto);
			MergeUtil.merge(shareAnalysisVos, turnoverList,
					WageShareAnalysisVo::getShopId, WageShareAnalysisVo::getShopId,
					(perVo, vo) -> {
						perVo.setTurnover(vo.getTurnover());
						perVo.setLastPeriodTurnover(vo.getLastPeriodTurnover());
						perVo.setLastYearTurnover(vo.getLastYearTurnover());
					}
			);
			//应发实际工资
			getWage(queryDto, shareAnalysisVos);
			//净利润
			getProfit(queryDto, shareAnalysisVos, beginDate, endDate);
			BigDecimal wage;
			BigDecimal lastPeriodWage;
			BigDecimal lastYearWage;
			//计算占销比、占净利润比
			for (WageShareAnalysisVo vo : shareAnalysisVos) {
				wage = vo.getWage();
				lastPeriodWage = vo.getLastPeriodWage();
				lastYearWage = vo.getLastYearWage();
				vo.setSalesRatio(BigDecimalUtils.getPercent(wage, vo.getTurnover()));
				vo.setProfitRatio(BigDecimalUtils.getPercent(wage, vo.getProfit()));
				vo.setLastPeriodSalesRatio(BigDecimalUtils.getPercent(lastPeriodWage, vo.getLastPeriodTurnover()));
				vo.setLastPeriodProfitRatio(BigDecimalUtils.getPercent(lastPeriodWage, vo.getLastPeriodProfit()));
				vo.setLastYearSalesRatio(BigDecimalUtils.getPercent(lastYearWage, vo.getLastYearTurnover()));
				vo.setLastYearProfitRatio(BigDecimalUtils.getPercent(lastYearWage, vo.getLastYearProfit()));
			}
			//区域
			Map<String, List<WageShareAnalysisVo>> regionAnalysisMap = shareAnalysisVos.stream().collect(Collectors.groupingBy(t -> t.getRegionId()));
			//type 区域
			List<WageShareAnalysisVo> regionAnalysisList = getAnalysisList(regionAnalysisMap, ReportDataConstant.Finance.TYPE_REGION);
			//品牌
			Map<String, List<WageShareAnalysisVo>> brandAnalysisMap = shareAnalysisVos.stream().collect(Collectors.groupingBy(t -> t.getBrandId()));
			//type 品牌
			List<WageShareAnalysisVo> brandAnalysisList = getAnalysisList(brandAnalysisMap, ReportDataConstant.Finance.TYPE_BRAND);
			//全部
			Map<String, List<WageShareAnalysisVo>> allAnalysisMap = shareAnalysisVos.stream().collect(Collectors.groupingBy(t -> t.getEnteId()));
			//type 全部
			List<WageShareAnalysisVo> allAnalysisList = getAnalysisList(allAnalysisMap, ReportDataConstant.Finance.TYPE_ALL);
			analysisVoList.addAll(shareAnalysisVos);
			analysisVoList.addAll(regionAnalysisList);
			analysisVoList.addAll(brandAnalysisList);
			analysisVoList.addAll(allAnalysisList);
		}
		return analysisVoList;
	}

	/**
	 * @Description 薪酬分析(带社保)
	 * @Author 郑勇浩
	 * @Data 2020/4/14 15:01
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.ShopSalaryVo>
	 */
	@Override
	public List<ShopSalaryVo> findSampleShopSalaryList(ShopSalaryDto param) {
		return shopSalaryMapper.findSampleShopSalaryList(param);
	}

	/**
	 * @Description 薪酬分析
	 * @Author 郑勇浩
	 * @Data 2020/3/30 14:52
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.ShopSalaryVo>
	 */
	@Override
	public List<ShopSalaryVo> findShopSalaryVoList(ShopSalaryDto param) {
		//门店员工
		List<ShopSalaryVo> shopEmployeeList = shopSalaryMapper.findShopEmployeeList(param);
		if (shopEmployeeList == null || shopEmployeeList.size() == Constant.Number.ZERO) {
			return new ArrayList<>();
		}
		// 门店薪酬
		List<ShopSalaryVo> shopSalaryList = shopSalaryMapper.findShopSalaryList(param);
		MergeUtil.merge(shopEmployeeList, shopSalaryList,
				ShopSalaryVo::getShopId,
				ShopSalaryVo::getShopId,
				(shopEmployee, shopSalary) -> {
					shopEmployee.setActualSalary(shopSalary.getActualSalary());
					shopEmployee.setGrossSalary(shopSalary.getGrossSalary());
					shopEmployee.setAbnormalSalary(shopSalary.getAbnormalSalary());
					shopEmployee.setIncome(shopSalary.getIncome());
				}
		);
		// 品牌 区域 合计
		Map<String, List<ShopSalaryVo>> regionMap = shopEmployeeList.stream().collect(Collectors.groupingBy(ShopSalaryVo::getRegionId));
		Map<String, List<ShopSalaryVo>> brandMap = shopEmployeeList.stream().collect(Collectors.groupingBy(ShopSalaryVo::getBrandId));
		Map<String, List<ShopSalaryVo>> enteMap = shopEmployeeList.stream().collect(Collectors.groupingBy(ShopSalaryVo::getEnteId));
		// 根据品牌区域合并计算
		getSumMemberNum(ReportDataConstant.Finance.TYPE_REGION, regionMap, shopEmployeeList);
		getSumMemberNum(ReportDataConstant.Finance.TYPE_BRAND, brandMap, shopEmployeeList);
		getSumMemberNum(ReportDataConstant.Finance.TYPE_ALL, enteMap, shopEmployeeList);
		// 合计
		calcData(shopEmployeeList);
		return shopEmployeeList;
	}

	/**
	 * @Description 导出薪酬分析
	 * @Author 郑勇浩
	 * @Data 2020/3/31 16:15
	 * @Param [response, param]
	 */
	@Override
	public void exportShopSalaryVoList(HttpServletResponse response, ShopSalaryDto param) {
		List<ShopSalaryVo> list = this.findShopSalaryVoList(param);
		//导出参数
		ExcelExportDto excelExportDto = new ExcelExportDto();
		excelExportDto.setEnteId(param.getEnteId());
		excelExportDto.setBeginDate(param.getBeginDate());
		excelExportDto.setEndDate(param.getEndDate());
		excelExportDto.setModelType(param.getModelType());
		excelExportDto.setMenuName(param.getMenuName());
		excelExportDto.setShopIdList(param.getShopIdList());
		excelExportDto.setShopTypeIdList(param.getShopTypeIdList());
		excelExportDto.setShopTypeName(param.getShopTypeName());
		excelExportDto.setOrgTree(param.getOrgTree());
		//过滤结果
		if (StringUtil.isNotBlank(param.getType())) {
			list = list.stream().filter(obj -> obj.getType().equals(param.getType())).collect(Collectors.toList());//过滤
		}
		fileService.exportExcelForQueryTerm(response, excelExportDto, list, new ArrayList<>(),
				ExcelColumnConstant.ShopSalary.BRAND_NAME,
				ExcelColumnConstant.ShopSalary.REGION_NAME,
				ExcelColumnConstant.ShopSalary.SHOP_NAME,
				ExcelColumnConstant.ShopSalary.ACTUAL_SALARY,
				ExcelColumnConstant.ShopSalary.GROSS_SALARY,
				ExcelColumnConstant.ShopSalary.ABNORMAL_SALARY,
				ExcelColumnConstant.ShopSalary.TOTAL_SALARY,
				ExcelColumnConstant.ShopSalary.EMPLOYEE_NUM,
				ExcelColumnConstant.ShopSalary.PER_PERSON,
				ExcelColumnConstant.ShopSalary.INCOME,
				ExcelColumnConstant.ShopSalary.MIX);

	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/4/1 16:04
	 * @Param [response, excelExportDto]
	 * @return void
	 * @Description 工资占销分析 导出
	 */
	@Override
	public void exportWageShareAnalysis(HttpServletResponse response, ExcelExportDto excelExportDto) {
		SalaryAnalysisDto queryDto = new SalaryAnalysisDto();
		queryDto.setShopIdList(excelExportDto.getShopIdList());
		queryDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
		queryDto.setEnteId(excelExportDto.getEnteId());
		queryDto.setBeginDate(excelExportDto.getBeginDate());
		queryDto.setEndDate(excelExportDto.getEndDate());
		queryDto.setDateType(excelExportDto.getDateType());
		List<WageShareAnalysisVo> wageShareAnalysisVos = wageShareAnalysis(queryDto);
		//表头
		List<TitleEntity> titleList = setQueryInfoTitle(excelExportDto);
		//下载时间的 ID 作为以下标题的根ID
		int tId = Constant.Number.EIGHT;
		String pId = String.valueOf(tId);
		tId = tId + 1;
		TitleEntity entity = new TitleEntity(String.valueOf(tId), pId, "品牌", "brandName");
		tId = tId + 1;
		TitleEntity entity1 = new TitleEntity(String.valueOf(tId), pId, "区域", "regionName");
		tId = tId + 1;
		TitleEntity entity2 = new TitleEntity(String.valueOf(tId), pId, "门店", "shopName");
		tId = tId + 1;
		TitleEntity entity3 = new TitleEntity(String.valueOf(tId), pId, "本期", null);
		//本期 根ID
		String curId = String.valueOf(tId);
		tId = tId + 1;
		TitleEntity entity4 = new TitleEntity(String.valueOf(tId), curId, "营业额(万）", "turnover");
		tId = tId + 1;
		TitleEntity entity5 = new TitleEntity(String.valueOf(tId), curId, "工资总额", "wage");
		tId = tId + 1;
		TitleEntity entity6 = new TitleEntity(String.valueOf(tId), curId, "占销比（%）", "salesRatio");
		tId = tId + 1;
		TitleEntity entity7 = new TitleEntity(String.valueOf(tId), curId, "占净利润比（%）", "profitRatio");
		tId = tId + 1;
		TitleEntity entity8 = new TitleEntity(String.valueOf(tId), pId, "去年同期", null);
		//去年同期 根ID
		String lastId = String.valueOf(tId);
		tId = tId + 1;
		TitleEntity entity9 = new TitleEntity(String.valueOf(tId), lastId, "营业额(万）", "lastYearTurnover");
		tId = tId + 1;
		TitleEntity entity10 = new TitleEntity(String.valueOf(tId), lastId, "工资总额", "lastYearWage");
		tId = tId + 1;
		TitleEntity entity11 = new TitleEntity(String.valueOf(tId), lastId, "占销比（%）", "lastYearSalesRatio");
		tId = tId + 1;
		TitleEntity entity12 = new TitleEntity(String.valueOf(tId), lastId, "占净利润比（%）", "lastYearProfitRatio");
		tId = tId + 1;
		TitleEntity entity13 = new TitleEntity(String.valueOf(tId), pId, "上期", null);
		//上期 根ID
		String perId = String.valueOf(tId);
		tId = tId + 1;
		TitleEntity entity14 = new TitleEntity(String.valueOf(tId), perId, "营业额(万）", "lastPeriodTurnover");
		tId = tId + 1;
		TitleEntity entity15 = new TitleEntity(String.valueOf(tId), perId, "工资总额", "lastPeriodWage");
		tId = tId + 1;
		TitleEntity entity16 = new TitleEntity(String.valueOf(tId), perId, "占销比（%）", "lastPeriodSalesRatio");
		tId = tId + 1;
		TitleEntity entity17 = new TitleEntity(String.valueOf(tId), perId, "占净利润比（%）", "lastPeriodProfitRatio");
		titleList.add(entity);
		titleList.add(entity1);
		titleList.add(entity2);
		titleList.add(entity3);
		titleList.add(entity4);
		titleList.add(entity5);
		titleList.add(entity6);
		titleList.add(entity7);
		titleList.add(entity8);
		titleList.add(entity9);
		titleList.add(entity10);
		titleList.add(entity11);
		titleList.add(entity12);
		titleList.add(entity13);
		titleList.add(entity14);
		titleList.add(entity15);
		titleList.add(entity16);
		titleList.add(entity17);
		List<Map<String, Object>> rowList = new ArrayList<>();
		if (!FastUtils.checkNullOrEmpty(wageShareAnalysisVos)) {
			//加入千分位，保留2位小数，并且不够补0
			DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
			List<WageShareAnalysisVo> voList;
			//根据类型过滤
			if (ReportDataConstant.Finance.TYPE_SHOP.equals(excelExportDto.getType())) {
				voList = wageShareAnalysisVos.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)).collect(Collectors.toList());
			} else if (ReportDataConstant.Finance.TYPE_BRAND.equals(excelExportDto.getType())) {
				voList = wageShareAnalysisVos.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
			} else if (ReportDataConstant.Finance.TYPE_REGION.equals(excelExportDto.getType())) {
				voList = wageShareAnalysisVos.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
			} else {
				voList = wageShareAnalysisVos.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
			}
			for (WageShareAnalysisVo vo : voList) {
				Map map = new HashMap<String, Object>();
				map.put("brandName", vo.getBrandName());
				map.put("regionName", vo.getRegionName());
				map.put("shopName", vo.getShopName());
				map.put("turnover", getNotZeroByConvert(vo.getTurnover(), df));
				map.put("wage", getNotZeroByConvert(vo.getWage(), df));
				map.put("salesRatio", getStringByConvert(vo.getSalesRatio(), df));
				map.put("profitRatio", getStringByConvert(vo.getProfitRatio(), df));
				map.put("lastYearTurnover", getNotZeroByConvert(vo.getLastYearTurnover(), df));
				map.put("lastYearWage", getNotZeroByConvert(vo.getLastYearWage(), df));
				map.put("lastYearSalesRatio", getStringByConvert(vo.getLastYearSalesRatio(), df));
				map.put("lastYearProfitRatio", getStringByConvert(vo.getLastYearProfitRatio(), df));
				map.put("lastPeriodTurnover", getNotZeroByConvert(vo.getLastPeriodTurnover(), df));
				map.put("lastPeriodWage", getNotZeroByConvert(vo.getLastPeriodWage(), df));
				map.put("lastPeriodSalesRatio", getStringByConvert(vo.getLastPeriodSalesRatio(), df));
				map.put("lastPeriodProfitRatio", getStringByConvert(vo.getLastPeriodProfitRatio(), df));
				rowList.add(map);
			}
		}
		deskTypeAnalysisService.exportMethod(response, titleList, rowList);
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/4 11:48
	 * @Param [bigDecimal]
	 * @return java.lang.String
	 * @Description 百分比为 0.00 时转换为 ’-‘ ，不为时 加百分号 加千分位
	 */
	private String getStringByConvert(BigDecimal bigDecimal, DecimalFormat df) {
		String strValue;
		if (null == bigDecimal || bigDecimal.toString().equals(Constant.Character.String_ZEROB)
				|| bigDecimal.toString().equals(Constant.Character.String_ZERO)) {
			strValue = Constant.Character.MIDDLE_LINE;
		} else {
			strValue = df.format(bigDecimal) + Constant.Character.Percent;
		}
		return strValue;
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/5 15:11
	 * @Param [value, df]
	 * @return java.lang.String
	 * @Description 为 0 时转换为 ’-‘ ，不为时  加千分位
	 */
	private String getNotZeroByConvert(Object value, DecimalFormat df) {
		if (null == value) {
			return Constant.Character.MIDDLE_LINE;
		}
		String strValue;
		if (Constant.Character.String_ZERO.equals(String.valueOf(value))) {
			strValue = Constant.Character.MIDDLE_LINE;
		} else {
			strValue = df.format(value);
		}
		return strValue;
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/12 14:27
	 * @Param [excelExportDto]
	 * @return java.util.List<com.njwd.poiexcel.TitleEntity>
	 * @Description 导出-标题通用方法
	 */
	private List<TitleEntity> setQueryInfoTitle(ExcelExportDto excelExportDto) {
		List<TitleEntity> titleList = new ArrayList<>();
		TitleEntity titleEntity0 = new TitleEntity("0", null, null, null);
		TitleEntity titleEntity1 = new TitleEntity("1", "0", excelExportDto.getMenuName(), null);
		TitleEntity titleEntity2 = new TitleEntity("2", "1", ReportDataConstant.ExcelExportInfo.ORGNAME + Constant.Character.COLON + excelExportDto.getOrgTree(), null);
		TitleEntity titleEntity3 = new TitleEntity("3", "2", ReportDataConstant.ExcelExportInfo.DATEPERIOD + Constant.Character.COLON + DateUtils.dateConvertString(excelExportDto.getBeginDate(), DateUtils.PATTERN_DAY) + Constant.Character.MIDDLE_WAVE
				+ DateUtils.dateConvertString(excelExportDto.getEndDate(), DateUtils.PATTERN_DAY), null);
		TitleEntity titleEntity4 = new TitleEntity("4", "3", ReportDataConstant.ExcelExportInfo.SHOPTYPE + Constant.Character.COLON + excelExportDto.getShopTypeName(), null);
		TitleEntity titleEntity5 = new TitleEntity("5", "4", ReportDataConstant.ExcelExportInfo.DOWNLOAD_TIME + Constant.Character.COLON + DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND), null);
		TitleEntity titleEntity6 = new TitleEntity("6", "5", null, null);
		TitleEntity titleEntity7 = new TitleEntity("7", "6", null, null);
		TitleEntity titleEntity8 = new TitleEntity("8", "7", null, null);
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
	 * @Description 结果分类
	 * @Author 郑勇浩
	 * @Data 2020/3/23 18:04
	 * @Param [type, map, dateList]
	 */
	private void getSumMemberNum(String type, Map<String, List<ShopSalaryVo>> map, List<ShopSalaryVo> dateList) {
		ShopSalaryVo vo;
		for (Map.Entry<String, List<ShopSalaryVo>> entry : map.entrySet()) {
			vo = entry.getValue().stream().reduce(new ShopSalaryVo(), (te, e) -> {
				te.setActualSalary(te.getActualSalary().add(e.getActualSalary()));
				te.setGrossSalary(te.getGrossSalary().add(e.getGrossSalary()));
				te.setAbnormalSalary(te.getAbnormalSalary().add(e.getAbnormalSalary()));
				te.setEmployeeNum(te.getEmployeeNum() + e.getEmployeeNum());
				te.setIncome(te.getIncome().add(e.getIncome()));
				return te;
			});
			vo.setEnteId(entry.getValue().get(Constant.Number.ZERO).getEnteId());
			vo.setBrandId(entry.getValue().get(Constant.Number.ZERO).getBrandId());
			vo.setRegionId(entry.getValue().get(Constant.Number.ZERO).getRegionId());
			vo.setBrandName(entry.getValue().get(Constant.Number.ZERO).getBrandName());
			vo.setRegionName(entry.getValue().get(Constant.Number.ZERO).getRegionName());
			vo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
			//type不为区域时，对象区域名称为 全部区域
			if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
				vo.setType(ReportDataConstant.Finance.TYPE_REGION);
			} else if (ReportDataConstant.Finance.TYPE_BRAND.equals(type)) {
				vo.setType(ReportDataConstant.Finance.TYPE_BRAND);
				vo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
			} else if (ReportDataConstant.Finance.TYPE_ALL.equals(type)) {
				vo.setType(ReportDataConstant.Finance.TYPE_ALL);
				vo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
				vo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
				vo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
			}
			dateList.add(vo);
		}
	}

	/**
	 * @Description 合计
	 * @Author 郑勇浩
	 * @Data 2020/3/31 15:46
	 * @Param [shopEmployeeList]
	 */
	private void calcData(List<ShopSalaryVo> dataList) {
		for (ShopSalaryVo data : dataList) {
			data.setTotalSalary(data.getGrossSalary().add(data.getAbnormalSalary()));
			data.setPerPerson(BigDecimalUtils.divideMethod(data.getTotalSalary(), BigDecimal.valueOf(data.getEmployeeNum()), Constant.Number.TWO));
			data.setMix(BigDecimalUtils.multiplyMethod(BigDecimalUtils.divideMethod(data.getTotalSalary(), data.getIncome(), Constant.Number.FOUR), new BigDecimal(100), Constant.Number.TWO));
		}
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/31 10:52
	 * @Param [queryDto, shareAnalysisVos, beginDate, endDate]
	 * @return void
	 * @Description 净利润
	 */
	private void getProfit(SalaryAnalysisDto queryDto, List<WageShareAnalysisVo> shareAnalysisVos, Date beginDate, Date endDate) {
		//查询时间格式转化
		queryDto.setBeginNum(DateUtils.getPeriodYearNum(DateUtils.format(beginDate, DateUtils.PATTERN_DAY)));
		queryDto.setEndNum(DateUtils.getPeriodYearNum(DateUtils.format(endDate, DateUtils.PATTERN_DAY)));
		queryDto.setPrevBeginNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getPrevBeginDate(), DateUtils.PATTERN_DAY)));
		queryDto.setPrevEndNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getPrevEndDate(), DateUtils.PATTERN_DAY)));
		queryDto.setLastYearBeginNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getLastYearBeginDate(), DateUtils.PATTERN_DAY)));
		queryDto.setLastYearEndNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getLastYearEndDate(), DateUtils.PATTERN_DAY)));
		//净利润
		List<WageShareAnalysisVo> profitList = salaryAnalysisMapper.findProfitList(queryDto);
		MergeUtil.merge(shareAnalysisVos, profitList,
				WageShareAnalysisVo::getShopId, WageShareAnalysisVo::getShopId,
				(perVo, vo) -> {
					perVo.setProfit(vo.getProfit());
					perVo.setLastPeriodProfit(vo.getLastPeriodProfit());
					perVo.setLastYearProfit(vo.getLastYearProfit());
				}
		);
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/31 9:22
	 * @Param [voList]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.WageShareAnalysisVo>
	 * @Description
	 */
	private List<WageShareAnalysisVo> getWageShareAnalysisVos(List<MembershipCardAnalysisVo> voList) {
		List<WageShareAnalysisVo> shareAnalysisVos = new ArrayList<>();
		WageShareAnalysisVo wageShare;
		for (MembershipCardAnalysisVo vo : voList) {
			wageShare = new WageShareAnalysisVo();
			wageShare.setShopId(vo.getShopId());
			wageShare.setShopName(vo.getShopName());
			wageShare.setRegionId(vo.getRegionId());
			wageShare.setRegionName(vo.getRegionName());
			wageShare.setBrandId(vo.getBrandId());
			wageShare.setBrandName(vo.getBrandName());
			wageShare.setEnteId(vo.getEnteId());
			shareAnalysisVos.add(wageShare);
		}
		return shareAnalysisVos;
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/31 9:21
	 * @Param [queryDto, shareAnalysisVos]
	 * @return void
	 * @Description 应发实际工资
	 */
	private void getWage(SalaryAnalysisDto queryDto, List<WageShareAnalysisVo> shareAnalysisVos) {
		//时间转换
		queryDto.setBeginNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getBeginDate(), DateUtils.PATTERN_DAY)));
		queryDto.setEndNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getEndDate(), DateUtils.PATTERN_DAY)));
		queryDto.setPrevBeginNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getPrevBeginDate(), DateUtils.PATTERN_DAY)));
		queryDto.setPrevEndNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getPrevEndDate(), DateUtils.PATTERN_DAY)));
		queryDto.setLastYearBeginNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getLastYearBeginDate(), DateUtils.PATTERN_DAY)));
		queryDto.setLastYearEndNum(DateUtils.getPeriodYearNum(DateUtils.format(queryDto.getLastYearEndDate(), DateUtils.PATTERN_DAY)));
		List<WageShareAnalysisVo> salaryVo = shopSalaryMapper.findSalaryListByShop(queryDto);
		MergeUtil.merge(shareAnalysisVos, salaryVo,
				WageShareAnalysisVo::getShopId, WageShareAnalysisVo::getShopId,
				(perVo, vo) -> {
					perVo.setWage(vo.getWage());
					perVo.setLastPeriodWage(vo.getLastPeriodWage());
					perVo.setLastYearWage(vo.getLastYearWage());
				}
		);
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/31 11:42
	 * @Param [analysisMap, type]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.WageShareAnalysisVo>
	 * @Description 区域 品牌 全部 维度 占销比、利润比计算
	 */
	private List<WageShareAnalysisVo> getAnalysisList(Map<String, List<WageShareAnalysisVo>> analysisMap, String type) {
		List<WageShareAnalysisVo> voList = new LinkedList<>();
		analysisMap.forEach((k, analysisList) -> {
			WageShareAnalysisVo regionAnalysisVo = new WageShareAnalysisVo();
			BigDecimal turnover = analysisList.stream().map(WageShareAnalysisVo::getTurnover).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal wage = analysisList.stream().map(WageShareAnalysisVo::getWage).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal profit = analysisList.stream().map(WageShareAnalysisVo::getProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal lastPeriodTurnover = analysisList.stream().map(WageShareAnalysisVo::getLastPeriodTurnover).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal lastPeriodWage = analysisList.stream().map(WageShareAnalysisVo::getLastPeriodWage).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal lastPeriodProfit = analysisList.stream().map(WageShareAnalysisVo::getLastPeriodProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal lastYearTurnover = analysisList.stream().map(WageShareAnalysisVo::getLastYearTurnover).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal lastYearWage = analysisList.stream().map(WageShareAnalysisVo::getLastYearWage).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal lastYearProfit = analysisList.stream().map(WageShareAnalysisVo::getLastYearProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
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
			regionAnalysisVo.setTurnover(turnover);
			regionAnalysisVo.setWage(wage);
			regionAnalysisVo.setProfit(profit);
			regionAnalysisVo.setLastPeriodTurnover(lastPeriodTurnover);
			regionAnalysisVo.setLastPeriodWage(lastPeriodWage);
			regionAnalysisVo.setLastPeriodProfit(lastPeriodProfit);
			regionAnalysisVo.setLastYearTurnover(lastYearTurnover);
			regionAnalysisVo.setLastYearWage(lastYearWage);
			regionAnalysisVo.setLastYearProfit(lastYearProfit);
			regionAnalysisVo.setSalesRatio(BigDecimalUtils.getPercent(wage, turnover));
			regionAnalysisVo.setProfitRatio(BigDecimalUtils.getPercent(wage, profit));
			regionAnalysisVo.setLastPeriodSalesRatio(BigDecimalUtils.getPercent(lastPeriodWage, lastPeriodTurnover));
			regionAnalysisVo.setLastPeriodProfitRatio(BigDecimalUtils.getPercent(lastPeriodWage, lastPeriodProfit));
			regionAnalysisVo.setLastYearSalesRatio(BigDecimalUtils.getPercent(lastYearWage, lastYearTurnover));
			regionAnalysisVo.setLastYearProfitRatio(BigDecimalUtils.getPercent(lastYearWage, lastYearProfit));
			voList.add(regionAnalysisVo);
		});
		return voList;
	}

}

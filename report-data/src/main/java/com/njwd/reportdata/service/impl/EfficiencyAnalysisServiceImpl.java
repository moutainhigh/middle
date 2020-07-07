package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.EmployeeProfitDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.EmployeeProfitVo;
import com.njwd.entity.reportdata.vo.ShopSalaryVo;
import com.njwd.reportdata.mapper.EmployProfitMapper;
import com.njwd.reportdata.service.EfficiencyAnalysisService;
import com.njwd.service.FileService;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.DateUtils;
import com.njwd.utils.MergeUtil;
import com.njwd.utils.StringUtil;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 人均人效分析 service impl
 * @Author LuoY 郑勇浩
 * @Date 2019/11/20
 */
@Service
public class EfficiencyAnalysisServiceImpl implements EfficiencyAnalysisService {

	@Resource
	private EmployProfitMapper employProfitMapper;
	@Resource
	private FileService fileService;

	/**
	 * @Description 人均创利分析列表查询
	 * @Author 郑勇浩
	 * @Data 2020/3/23 17:12
	 * @Param [employeeProfitDto]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.EmployeeProfitVo>
	 */
	@Override
	public List<EmployeeProfitVo> findEmployeeProfitReport(EmployeeProfitDto param) {
		param.setReportId(ReportDataConstant.ReportItemReportId.PROFIT_REPORT);
		param.setPeriodYearNum(DateUtils.getPeriodYearNum(DateUtils.format(param.getBeginDate(), DateUtils.PATTERN_DAY)));
		// 查询结果
		List<EmployeeProfitVo> returnDataList = employProfitMapper.findEmployProfitList(param);
		// 查询标工时
		param.setEndDate(DateUtils.addDays(param.getEndDate(), 1));
		List<EmployeeProfitVo> employeeNumList = employProfitMapper.findEmployeeNum(param);
		MergeUtil.merge(returnDataList, employeeNumList,
				EmployeeProfitVo::getShopId,
				EmployeeProfitVo::getShopId,
				(returnData, employeeNum) -> {
					returnData.setEmployeeNum(employeeNum.getEmployeeNum());
					returnData.setWaiterNum(employeeNum.getWaiterNum());
					returnData.setKitchenNum(employeeNum.getKitchenNum());
				}
		);
		if (returnDataList == null || returnDataList.size() == Constant.Number.ZERO) {
			return new ArrayList<>();
		}
		// 品牌 区域 合计
		Map<String, List<EmployeeProfitVo>> regionMap = returnDataList.stream().collect(Collectors.groupingBy(EmployeeProfitVo::getRegionId));
		Map<String, List<EmployeeProfitVo>> brandMap = returnDataList.stream().collect(Collectors.groupingBy(EmployeeProfitVo::getBrandId));
		Map<String, List<EmployeeProfitVo>> enteMap = returnDataList.stream().collect(Collectors.groupingBy(EmployeeProfitVo::getEnteId));
		// 根据品牌区域合并计算
		getSumMemberNum(ReportDataConstant.Finance.TYPE_REGION, regionMap, returnDataList);
		getSumMemberNum(ReportDataConstant.Finance.TYPE_BRAND, brandMap, returnDataList);
		getSumMemberNum(ReportDataConstant.Finance.TYPE_ALL, enteMap, returnDataList);
		// 计算差值
		//获取当月
		int days = DateUtils.getBetweenDay(param.getBeginDate(), param.getEndDate());
		calcData(returnDataList, days);
		return returnDataList;
	}

	/**
	 * @Description 导出人均创利分析列表
	 * @Author 郑勇浩
	 * @Data 2020/3/23 17:12
	 * @Param [employeeProfitDto]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.EmployeeProfitVo>
	 */
	@Override
	public void exportEmployeeProfitReport(HttpServletResponse response, EmployeeProfitDto param) {
		List<EmployeeProfitVo> list = this.findEmployeeProfitReport(param);
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
				ExcelColumnConstant.EmployeeProfit.BRAND_NAME,
				ExcelColumnConstant.EmployeeProfit.REGION_NAME,
				ExcelColumnConstant.EmployeeProfit.SHOP_NAME,
				ExcelColumnConstant.EmployeeProfit.AMOUNT_BALANCE,
				ExcelColumnConstant.EmployeeProfit.EMPLOYEE_NUM,
				ExcelColumnConstant.EmployeeProfit.WAITER_NUM,
				ExcelColumnConstant.EmployeeProfit.KITCHEN_NUM,
				ExcelColumnConstant.EmployeeProfit.EMPLOYEE_PER_AMOUNT,
				ExcelColumnConstant.EmployeeProfit.WAITER_PER_AMOUNT,
				ExcelColumnConstant.EmployeeProfit.KITCHEN_PER_AMOUNT);
	}

	/**
	 * @Description 结果分类
	 * @Author 郑勇浩
	 * @Data 2020/3/23 18:04
	 * @Param [type, map, dateList]
	 */
	private void getSumMemberNum(String type, Map<String, List<EmployeeProfitVo>> map, List<EmployeeProfitVo> dateList) {
		EmployeeProfitVo vo;
		for (Map.Entry<String, List<EmployeeProfitVo>> entry : map.entrySet()) {
			vo = entry.getValue().stream().reduce(new EmployeeProfitVo(), (te, e) -> {
				te.setAmountBalance(te.getAmountBalance().add(e.getAmountBalance()));
				te.setEmployeeNum(te.getEmployeeNum() + (e.getEmployeeNum()));
				te.setWaiterNum(te.getWaiterNum() + (e.getWaiterNum()));
				te.setKitchenNum(te.getKitchenNum() + (e.getKitchenNum()));
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
	 * @Description 计算差值
	 * @Author 郑勇浩
	 * @Data 2020/3/23 18:05
	 * @Param []
	 */
	private void calcData(List<EmployeeProfitVo> dataList, int days) {
		for (EmployeeProfitVo data : dataList) {
			//人数
			data.setEmployeeNum(data.getEmployeeNum() / days / Constant.Number.EIGHT);
			data.setKitchenNum(data.getKitchenNum() / days / Constant.Number.EIGHT);
			data.setWaiterNum(data.getWaiterNum() / days / Constant.Number.EIGHT);
			data.setEmployeePerAmount(BigDecimalUtils.divideMethod(data.getAmountBalance(), BigDecimal.valueOf(data.getEmployeeNum()), Constant.Number.TWO));
			data.setWaiterPerAmount(BigDecimalUtils.divideMethod(data.getAmountBalance(), BigDecimal.valueOf(data.getWaiterNum()), Constant.Number.TWO));
			data.setKitchenPerAmount(BigDecimalUtils.divideMethod(data.getAmountBalance(), BigDecimal.valueOf(data.getKitchenNum()), Constant.Number.TWO));
		}
	}


}

package com.njwd.reportdata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingProfit;
import com.njwd.entity.reportdata.dto.SettingProfitDto;
import com.njwd.entity.reportdata.vo.SettingProfitVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.fileexcel.check.SampleExcelCheck;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.mapper.SettingProfitMapper;
import com.njwd.reportdata.service.SettingProfitService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description 设置模块 实时利润预算 Service Impl
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Service
public class SettingProfitServiceImpl implements SettingProfitService {

	@Resource
	private SettingProfitMapper settingProfitMapper;
	@Resource
	private UserController userController;

	/**
	 * @Description 实时利润预算信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:58
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingProfitVo
	 */
	@Override
	public SettingProfitVo findSettingProfit(SettingProfitDto param) {
		return settingProfitMapper.findSettingProfit(param);
	}

	/**
	 * @Description 查询实时利润预算列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@Override
	public Page<SettingProfitVo> findSettingProfitList(SettingProfitDto param) {
		//传入格式类型 0 字符 1 数字 2日期
		param.setQueryType(0);
		if (param.getQuery() != null) {
			if (StringUtil.isNumeric(param.getQuery())) {
				param.setQueryType(1);
			} else if (DateUtils.isValidDate(param.getQuery())) {
				param.setQueryType(2);
			}
		}
		return settingProfitMapper.findSettingProfitList(param.getPage(), param);
	}

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	@Override
	public List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingProfit> valueList) {
		return settingProfitMapper.findDuplicateDataList(enteId, valueList);
	}

	/**
	 * @Description 更新实时利润预算
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:06
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingProfit(SettingProfitDto param) {
		// 格式化日期 判断日期是否前面小于后面
		param.setBeginDate(DateUtils.parseDate(param.getDateList()[0], DateUtils.PATTERN_DAY));
		param.setEndDate(DateUtils.parseDate(param.getDateList()[1], DateUtils.PATTERN_DAY));
		if (DateUtils.compareDate(param.getBeginDate(), param.getEndDate()) != -1) {
			throw new ServiceException(ResultCode.PARAMS_NOT_RIGHT);
		}
		//是否存在重复数据
		List<SettingProfitVo> mergerDataList = settingProfitMapper.findDuplicateData(param);
		if (mergerDataList != null && mergerDataList.size() > 0) {
			for (SettingProfitVo mergerData : mergerDataList) {
				if (DateUtils.isDateCross(param.getBeginDate(), param.getEndDate(), mergerData.getBeginDate(), mergerData.getEndDate())) {
					throw new ServiceException(ResultCode.DATE_DUPLICATE_ERROR);
				}
			}
		}
		param.setUpdateTime(new Date());
		return settingProfitMapper.updateSettingProfit(param);
	}

	/**
	 * @Description 更新实时利润预算状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingProfitStatus(SettingProfitDto param) {
		param.setUpdateTime(new Date());
		return settingProfitMapper.updateSettingProfitStatus(param);
	}

	/**
	 * @Description 实时利润预算EXCEL数据校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 19:43
	 * @Param [excelData]
	 * @return void
	 */
	@Override
	public void checkData(ExcelData excelData) {
		SampleExcelCheck.checkNull(excelData, new int[]{0, 0, 0, 0, 0, 0, 0, 0});
		SampleExcelCheck.checkFormat(excelData, new int[]{0, 0, 0, 0, 2, 5, 4, 4});
		SampleExcelCheck.checkLength(excelData, new int[]{30, 30, 30, 50, 10, -1, -1, -1});
		int[] checkExcelDuplicate = {3, 4};
		int[] date = {6, 7};
		SampleExcelCheck.checkExcelDuplicate(excelData, checkExcelDuplicate, date);
		//转化数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<ConvertData> convertDataList = settingProfitMapper.findConvertDataList(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//			List<ConvertData> convertDataList = settingProfitMapper.findConvertDataList("999");
			SampleExcelCheck.checkConvert(excelData, new int[]{0, 1, 2, 3}, convertDataList);
		}
		//重复数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<SettingProfit> duplicateList = new ArrayList<>();
			for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
				SettingProfit data = new SettingProfit();
				data.setShopId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(2).getOldData().toString());
				data.setProjectId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(3).getOldData().toString());
				duplicateList.add(data);
			}
			List<Map<String, String>> duplicateDataList = this.findDuplicateDataList(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString(), duplicateList);
//			List<Map<String, String>> duplicateDataList = this.findDuplicateDataList("999", duplicateList);
			SampleExcelCheck.checkDataBaseDuplicate(excelData, checkExcelDuplicate, date, duplicateDataList);
		}
	}

	/**
	 * @Description 批量新增表格数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:14
	 * @Param [excelData]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer insertExcelBatch(ExcelData excelData) {
		List<SettingProfitVo> dataList = new ArrayList<>();
		SettingProfitVo data;
		List<ExcelCellData> cellDataList;
		Date updateTime = new Date();
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			cellDataList = excelData.getExcelRowDataList().get(i).getExcelCellDataList();
			data = new SettingProfitVo();
			data.setId(StringUtil.genUniqueKey());
			data.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//			data.setEnteId("999");
			data.setBrandId(cellDataList.get(0).getOldData().toString());
			data.setRegionId(cellDataList.get(1).getOldData().toString());
			data.setShopId(cellDataList.get(2).getOldData().toString());
			data.setProjectId(cellDataList.get(3).getOldData().toString());
			data.setBudget(new BigDecimal(cellDataList.get(4).getData().toString()));
			data.setPercentage(new BigDecimal(cellDataList.get(5).getData().toString()));
			data.setBeginDate(DateUtils.parseDate(cellDataList.get(6).getData().toString(), DateUtils.PATTERN_DAY));
			data.setEndDate(DateUtils.parseDate(cellDataList.get(7).getData().toString(), DateUtils.PATTERN_DAY));
			data.setCreateTime(updateTime);
			dataList.add(data);
		}
		return settingProfitMapper.insertDataBatch(dataList);
	}

}

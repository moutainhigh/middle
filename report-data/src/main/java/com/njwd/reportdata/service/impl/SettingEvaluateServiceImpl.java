package com.njwd.reportdata.service.impl;

import com.alibaba.nacos.client.utils.StringUtils;
import com.njwd.common.Constant;
import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.basedata.excel.ExcelRowData;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingEvaluate;
import com.njwd.entity.reportdata.dto.SettingEvaluateDto;
import com.njwd.entity.reportdata.vo.SettingEvaluateVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.fileexcel.check.SampleExcelCheck;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.mapper.SettingEvaluateMapper;
import com.njwd.reportdata.service.SettingEvaluateService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.StringUtil;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @Description 设置模块 Service Impl
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Service
public class SettingEvaluateServiceImpl implements SettingEvaluateService {

	@Resource
	private SettingEvaluateMapper settingEvaluateMapper;
	@Resource
	private UserController userController;

	/**
	 * @Description 评价汇总阀值信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:58
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingEvaluateVo
	 */
	@Override
	public SettingEvaluateVo findSettingEvaluate(SettingEvaluateDto param) {
		return settingEvaluateMapper.findSettingEvaluate(param);
	}

	/**
	 * @Description 查询评价汇总阀值列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@Override
	public List<SettingEvaluateVo> findSettingEvaluateList(SettingEvaluateDto param) {
		//传入格式类型 0 字符 1 数字 2日期
		param.setQueryType(0);
		if (param.getQuery() != null) {
			if (StringUtil.isNumeric(param.getQuery())) {
				param.setQueryType(1);
			} else if (DateUtils.isValidDate(param.getQuery())) {
				param.setQueryType(2);
			}
		}
		return settingEvaluateMapper.findSettingEvaluateList(param);
	}

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	@Override
	public List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingEvaluate> valueList) {
		return settingEvaluateMapper.findDuplicateDataList(enteId, valueList);
	}

	/**
	 * @Description 更新评价汇总阀值
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:06
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingEvaluate(SettingEvaluateDto param) {
		// 格式化日期 判断日期是否前面小于后面
		param.setBeginDate(DateUtils.parseDate(param.getDateList()[0], DateUtils.PATTERN_DAY));
		param.setEndDate(DateUtils.parseDate(param.getDateList()[1], DateUtils.PATTERN_DAY));
		if (DateUtils.compareDate(param.getBeginDate(), param.getEndDate()) != -1) {
			throw new ServiceException(ResultCode.PARAMS_NOT_RIGHT);
		}
		param.setUpdateTime(new Date());
		return settingEvaluateMapper.updateSettingEvaluate(param);
	}

	/**
	 * @Description 更新评价汇总阀值状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingEvaluateStatus(SettingEvaluateDto param) {
		param.setUpdateTime(new Date());
		return settingEvaluateMapper.updateSettingEvaluateStatus(param);
	}

	/**
	 * @Description 评价汇总阀值EXCEL数据校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 19:43
	 * @Param [excelData]
	 * @return void
	 */
	@Override
	public void checkData(ExcelData excelData) {
		SampleExcelCheck.checkNull(excelData, new int[]{0, 0, 0, 0, 0});
		SampleExcelCheck.checkFormat(excelData, new int[]{0, 0, 0, 4, 4});
		SampleExcelCheck.checkLength(excelData, new int[]{30, 30, 30, -1, -1});
		int[] checkExcelDuplicate = {0, 1, 2};
		int[] date = {3, 4};
		SampleExcelCheck.checkExcelDuplicate(excelData, checkExcelDuplicate, date);
		this.checkRange(excelData);
		//重复数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<SettingEvaluate> duplicateList = new ArrayList<>();
			for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
				SettingEvaluate data = new SettingEvaluate();
				data.setDimension(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(0).getData().toString());
				data.setRange(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(1).getData().toString());
				data.setWarn(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(2).getData().toString());
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
		List<SettingEvaluateVo> dataList = new ArrayList<>();
		SettingEvaluateVo data;
		List<ExcelCellData> cellDataList;
		Date updateTime = new Date();
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			cellDataList = excelData.getExcelRowDataList().get(i).getExcelCellDataList();
			data = new SettingEvaluateVo();
			data.setId(StringUtil.genUniqueKey());
			data.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//			data.setEnteId("999");
			data.setDimension(cellDataList.get(0).getData().toString());
			data.setRange(cellDataList.get(1).getData().toString());
			data.setWarn(cellDataList.get(2).getData().toString());
			data.setBeginDate(DateUtils.parseDate(cellDataList.get(3).getData().toString(), DateUtils.PATTERN_DAY));
			data.setEndDate(DateUtils.parseDate(cellDataList.get(4).getData().toString(), DateUtils.PATTERN_DAY));
			data.setCreateTime(updateTime);
			dataList.add(data);
		}
		return settingEvaluateMapper.insertDataBatch(dataList);
	}


	/**
	 * @Description 检查合理范围
	 * @Author 郑勇浩
	 * @Data 2020/3/5 13:59
	 * @Param
	 */
	public void checkRange(ExcelData excelData) {
		if (excelData.getExcelRowDataList().size() == 0) {
			return;
		}
		List<ExcelRowData> excelRowData = excelData.getExcelRowDataList();
		ExcelRowData rowData;
		String rangeCellData;
		String warnCellData;

		List<String> valueList = Arrays.asList("1星", "2星", "3星", "4星", "5星");
		String[] rangeValues;
		String[] warnValues;

		//行
		for (int i = 0; i < excelRowData.size(); i++) {
			rowData = excelRowData.get(i);
			rangeCellData = rowData.getExcelCellDataList().get(1).getData().toString();
			rangeValues = rangeCellData.split(",");

			boolean isDelete = false;
			for (String value : rangeValues) {
				if (!valueList.contains(value)) {
					isDelete = true;
					break;
				}
			}

			if (isDelete) {
				excelData.getExcelErrorList().add(SampleExcelCheck.excelError(rowData, rowData.getExcelCellDataList().get(1), "请输入正确的合理范围"));
				excelData.getExcelRowDataList().remove(i);
				i--;
				continue;
			}

			warnCellData = rowData.getExcelCellDataList().get(2).getData().toString();
			warnValues = warnCellData.split(",");
			for (String value : warnValues) {
				if (!valueList.contains(value)) {
					isDelete = true;
					break;
				}
			}

			if (isDelete) {
				excelData.getExcelErrorList().add(SampleExcelCheck.excelError(rowData, rowData.getExcelCellDataList().get(2), "请输入正确的预警值"));
				excelData.getExcelRowDataList().remove(i);
				i--;
			}

		}
	}
}

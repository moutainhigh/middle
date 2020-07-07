package com.njwd.reportdata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.basedata.excel.ExcelRowData;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingEntryFree;
import com.njwd.entity.reportdata.dto.SettingEntryFreeDto;
import com.njwd.entity.reportdata.vo.SettingEntryFreeVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.fileexcel.check.SampleExcelCheck;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.mapper.SettingEntryFreeMapper;
import com.njwd.reportdata.service.SettingEntryFreeService;
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
 * @Description 设置模块 Service Impl
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Service
public class SettingEntryFreeServiceImpl implements SettingEntryFreeService {

	@Resource
	private SettingEntryFreeMapper settingEntryFreeMapper;
	@Resource
	private UserController userController;

	/**
	 * @Description 啤酒进场费信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:58
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingEntryFreeVo
	 */
	@Override
	public SettingEntryFreeVo findSettingEntryFree(SettingEntryFreeDto param) {
		return settingEntryFreeMapper.findSettingEntryFree(param);
	}

	/**
	 * @Description 查询啤酒进场费列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@Override
	public Page<SettingEntryFreeVo> findSettingEntryFreeList(SettingEntryFreeDto param) {
		//传入格式类型 0 字符 1 数字 2日期
		param.setQueryType(0);
		if (param.getQuery() != null) {
			if (StringUtil.isNumeric(param.getQuery())) {
				param.setQueryType(1);
			} else if (DateUtils.isValidDate(param.getQuery())) {
				param.setQueryType(2);
			}
		}
		return settingEntryFreeMapper.findSettingEntryFreeList(param.getPage(), param);
	}

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	@Override
	public List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingEntryFree> valueList) {
		return settingEntryFreeMapper.findDuplicateDataList(enteId, valueList);
	}

	/**
	 * @Description 更新啤酒进场费
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingEntryFree(SettingEntryFreeDto param) {
		// 格式化日期 判断日期是否前面小于后面
		param.setBeginDate(DateUtils.parseDate(param.getDateList()[0], DateUtils.PATTERN_DAY));
		param.setEndDate(DateUtils.parseDate(param.getDateList()[1], DateUtils.PATTERN_DAY));
		if (DateUtils.compareDate(param.getBeginDate(), param.getEndDate()) != -1) {
			throw new ServiceException(ResultCode.PARAMS_NOT_RIGHT);
		}

		//是否存在重复数据
		List<SettingEntryFreeVo> mergerDataList = settingEntryFreeMapper.findDuplicateData(param);
		if (mergerDataList != null && mergerDataList.size() > 0) {
			for (SettingEntryFreeVo mergerData : mergerDataList) {
				if (DateUtils.isDateCross(param.getBeginDate(), param.getEndDate(), mergerData.getBeginDate(), mergerData.getEndDate())) {
					throw new ServiceException(ResultCode.DATE_DUPLICATE_ERROR);
				}
			}
		}
		param.setUpdateTime(new Date());
		return settingEntryFreeMapper.updateSettingEntryFree(param);
	}

	/**
	 * @Description 更新啤酒进场费状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingEntryFreeStatus(SettingEntryFreeDto param) {
		param.setUpdateTime(new Date());
		return settingEntryFreeMapper.updateSettingEntryFreeStatus(param);
	}

	/**
	 * @Description 啤酒进场费EXCEL数据校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 19:43
	 * @Param [excelData]
	 * @return void
	 */
	@Override
	public void checkData(ExcelData excelData) {
		SampleExcelCheck.checkNull(excelData, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		SampleExcelCheck.checkFormat(excelData, new int[]{0, 0, 0, 0, 0, 0, 0, 2, 4, 4});
		SampleExcelCheck.checkLength(excelData, new int[]{30, 30, 30, 30, 30, 30, 30, 10, -1, -1});
		int[] checkExcelDuplicate = {2, 3, 5};
		int[] date = {8, 9};
		SampleExcelCheck.checkExcelDuplicate(excelData, checkExcelDuplicate, date);
		//转化数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<ConvertData> convertDataList = settingEntryFreeMapper.findConvertDataList(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//			List<ConvertData> convertDataList = settingEntryFreeMapper.findConvertDataList("999");
			SampleExcelCheck.checkConvert(excelData, new int[]{0, 1, 2}, convertDataList);
			if (excelData.getExcelRowDataList().size() == 0) {
				return;
			}

			// 自定义的转化
			List<SettingEntryFreeVo> convertList = new ArrayList<>();
			for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
				SettingEntryFreeVo data = new SettingEntryFreeVo();
				data.setShopId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(2).getOldData().toString());
				data.setNumber(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(3).getData().toString());
				data.setMaterialNumber(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(5).getData().toString());
				convertList.add(data);
			}
			// 供应商转化
			List<Map<String, String>> convertDataMap = settingEntryFreeMapper.findSupplierInfo(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString(), convertList);
//			List<Map<String, String>> convertDataMap = settingEntryFreeMapper.findSupplierInfo("999", convertList);
			this.checkSupplierConvert(excelData, convertDataMap);
			if (excelData.getExcelRowDataList().size() == 0) {
				return;
			}
			// 物料转化
			convertDataMap = settingEntryFreeMapper.findMaterialInfo(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString(), convertList);
//			convertDataMap = settingEntryFreeMapper.findMaterialInfo("999", convertList);
			this.checkMaterialConvert(excelData, convertDataMap);
		}

		//重复数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<SettingEntryFree> duplicateList = new ArrayList<>();
			for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
				SettingEntryFree data = new SettingEntryFree();
				data.setShopId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(2).getOldData().toString());
				data.setSupplierId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(3).getOldData().toString());
				data.setMaterialId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(5).getOldData().toString());
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
		List<SettingEntryFreeVo> dataList = new ArrayList<>();
		SettingEntryFreeVo data;
		List<ExcelCellData> cellDataList;
		Date updateTime = new Date();
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			cellDataList = excelData.getExcelRowDataList().get(i).getExcelCellDataList();
			data = new SettingEntryFreeVo();
			data.setId(StringUtil.genUniqueKey());
			data.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//			data.setEnteId("999");
			data.setBrandId(cellDataList.get(0).getOldData().toString());
			data.setBrandName(cellDataList.get(0).getData().toString());
			data.setRegionId(cellDataList.get(1).getOldData().toString());
			data.setRegionName(cellDataList.get(1).getData().toString());
			data.setShopId(cellDataList.get(2).getOldData().toString());
			data.setSupplierId(cellDataList.get(3).getOldData().toString());
			data.setSupplierNo(cellDataList.get(3).getData().toString());
			data.setMaterialId(cellDataList.get(5).getOldData().toString());
			data.setMaterialNo(cellDataList.get(5).getData().toString());
			data.setMoney(new BigDecimal(cellDataList.get(7).getData().toString()));
			data.setBeginDate(DateUtils.parseDate(cellDataList.get(8).getData().toString(), DateUtils.PATTERN_DAY));
			data.setEndDate(DateUtils.parseDate(cellDataList.get(9).getData().toString(), DateUtils.PATTERN_DAY));
			data.setCreateTime(updateTime);
			dataList.add(data);
		}
		return settingEntryFreeMapper.insertDataBatch(dataList);
	}

	/**
	 * @Description: 查询啤酒进场费
	 * @Param: [settingEntryFreeDto]
	 * @return: java.util.List<com.njwd.entity.reportdata.vo.SettingEntryFreeVo>
	 * @Author: LuoY
	 * @Date: 2020/3/27 18:12
	 */
	@Override
	public List<SettingEntryFreeVo> findBearSettingInfo(SettingEntryFreeDto settingEntryFreeDto) {
		return settingEntryFreeMapper.findBearSettingInfo(settingEntryFreeDto);
	}


	/**
	 * @Description 供应商转化
	 * @Author 郑勇浩
	 * @Data 2020/3/27 17:32
	 * @Param [excelData, ints, convertDataMap]
	 */
	private void checkSupplierConvert(ExcelData excelData, List<Map<String, String>> dataList) {
		if (excelData.getExcelRowDataList().size() == 0) {
			return;
		}

		// 获取需要匹配的值
		ExcelRowData rowData;
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			rowData = excelData.getExcelRowDataList().get(i);
			//进行行匹配
			for (Map<String, String> compareData : dataList) {
				if (rowData.getExcelCellDataList().get(2).getOldData().toString().equals(compareData.get("2"))
						&& rowData.getExcelCellDataList().get(3).getData().toString().equals(compareData.get("3"))) {
					rowData.getExcelCellDataList().get(3).setOldData(compareData.get("convertData"));
				}
			}
			//如果没匹配值
			if (rowData.getExcelCellDataList().get(3).getOldData() == null) {
				excelData.getExcelErrorList().add(SampleExcelCheck.excelError(rowData, rowData.getExcelCellDataList().get(3),
						"门店" + rowData.getExcelCellDataList().get(2).getData() + "不存在该供应商"));
				excelData.getExcelRowDataList().remove(i);
				i--;
			}
		}
	}

	/**
	 * @Description 物料转化
	 * @Author 郑勇浩
	 * @Data 2020/3/27 17:32
	 * @Param [excelData, ints, convertDataMap]
	 */
	private void checkMaterialConvert(ExcelData excelData, List<Map<String, String>> dataList) {
		// 获取需要匹配的值
		ExcelRowData rowData;
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			rowData = excelData.getExcelRowDataList().get(i);
			//进行行匹配
			for (Map<String, String> compareData : dataList) {
				if (rowData.getExcelCellDataList().get(2).getOldData().toString().equals(compareData.get("2"))
						&& rowData.getExcelCellDataList().get(5).getData().toString().equals(compareData.get("5"))) {
					rowData.getExcelCellDataList().get(5).setOldData(compareData.get("convertData"));
				}
			}
			//如果没匹配值
			if (rowData.getExcelCellDataList().get(5).getOldData() == null) {
				excelData.getExcelErrorList().add(SampleExcelCheck.excelError(rowData, rowData.getExcelCellDataList().get(5),
						"门店" + rowData.getExcelCellDataList().get(2).getData() + "不存在该物料"));
				excelData.getExcelRowDataList().remove(i);
				i--;
			}
		}
	}

}

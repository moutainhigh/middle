package com.njwd.reportdata.service.impl;

import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.basedata.excel.ExcelRowData;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingBaseShop;
import com.njwd.entity.reportdata.dto.SettingBaseShopDto;
import com.njwd.entity.reportdata.vo.SettingBaseShopVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.fileexcel.check.SampleExcelCheck;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.mapper.SettingBaseShopMapper;
import com.njwd.reportdata.service.SettingBaseShopService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
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
public class SettingBaseShopServiceImpl implements SettingBaseShopService {

	@Resource
	private SettingBaseShopMapper settingBaseShopMapper;
	@Resource
	private UserController userController;

	/**
	 * @Description 门店基础信息信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:58
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingBaseShopVo
	 */
	@Override
	public SettingBaseShopVo findSettingBaseShop(SettingBaseShopDto param) {
		return settingBaseShopMapper.findSettingBaseShop(param);
	}

	/**
	 * @Description 查询门店基础信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@Override
	public List<SettingBaseShopVo> findSettingBaseShopList(SettingBaseShopDto param) {
		//传入格式类型 0 字符 1 数字 2日期
		param.setQueryType(0);
		if (param.getQuery() != null) {
			if (StringUtil.isNumeric(param.getQuery())) {
				param.setQueryType(1);
			} else if (DateUtils.isValidDate(param.getQuery())) {
				param.setQueryType(2);
			}
		}
		return settingBaseShopMapper.findSettingBaseShopList(param);
	}

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	@Override
	public List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingBaseShop> valueList) {
		return settingBaseShopMapper.findDuplicateDataList(enteId, valueList);
	}

	/**
	 * @Description 更新门店基础信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:06
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingBaseShop(SettingBaseShopDto param) {
		// 格式化日期 判断日期是否前面小于后面
		if (StringUtils.isNotBlank(param.getOpeningDateStr())) {
			param.setOpeningDate(DateUtils.parseDate(param.getOpeningDateStr(), DateUtils.PATTERN_DAY));
		}
		if (StringUtils.isNotBlank(param.getShutdownDateStr())) {
			param.setShutdownDate(DateUtils.parseDate(param.getShutdownDateStr(), DateUtils.PATTERN_DAY));
		}
		if (param.getOpeningDate() != null && param.getShutdownDate() != null) {
			if (DateUtils.compareDate(param.getOpeningDate(), param.getShutdownDate()) != -1) {
				throw new ServiceException(ResultCode.SHUT_DOWN_DATE_ERROR);
			}
		}
		param.setUpdateTime(new Date());
		return settingBaseShopMapper.updateSettingBaseShop(param);
	}

	/**
	 * @Description 更新门店基础信息状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingBaseShopStatus(SettingBaseShopDto param) {
		param.setUpdateTime(new Date());
		return settingBaseShopMapper.updateSettingBaseShopStatus(param);
	}

	/**
	 * @Description 门店基础信息EXCEL数据校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 19:43
	 * @Param [excelData]
	 * @return void
	 */
	@Override
	public void checkData(ExcelData excelData) {
		SampleExcelCheck.checkNull(excelData, new int[]{0, 0, 0, 0, 0, 1, 1});
		SampleExcelCheck.checkFormat(excelData, new int[]{0, 0, 0, 2, 2, 3, 3});
		SampleExcelCheck.checkLength(excelData, new int[]{30, 30, 30, 10, 15, -1, -1});
		int[] checkExcelDuplicate = {2};
		SampleExcelCheck.checkExcelDuplicate(excelData, checkExcelDuplicate, null);
		//转化数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<ConvertData> convertDataList = settingBaseShopMapper.findConvertDataList(userController.getCurrLoginUserInfo().getRootEnterpriseId());
//			List<ConvertData> convertDataList = settingBaseShopMapper.findConvertDataList(999L);
			SampleExcelCheck.checkConvert(excelData, new int[]{0, 1, 2}, convertDataList);
		}
		//重复数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<SettingBaseShop> duplicateList = new ArrayList<>();
			for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
				SettingBaseShop data = new SettingBaseShop();
				data.setShopId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(2).getOldData().toString());
				duplicateList.add(data);
			}
			List<Map<String, String>> duplicateDataList = this.findDuplicateDataList(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString(), duplicateList);
//			List<Map<String, String>> duplicateDataList = this.findDuplicateDataList("999", duplicateList);
			SampleExcelCheck.checkDataBaseDuplicate(excelData, checkExcelDuplicate, null, duplicateDataList);
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
		List<SettingBaseShopVo> dataList = new ArrayList<>();
		SettingBaseShopVo data;
		List<ExcelCellData> cellDataList;
		Date createTime = new Date();
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			cellDataList = excelData.getExcelRowDataList().get(i).getExcelCellDataList();
			data = new SettingBaseShopVo();
			data.setId(StringUtil.genUniqueKey());
//			data.setEnteId(999L);
			data.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
			data.setBrandId(cellDataList.get(0).getOldData().toString());
			data.setBrandName(cellDataList.get(0).getData().toString());
			data.setRegionId(cellDataList.get(1).getOldData().toString());
			data.setRegionName(cellDataList.get(1).getData().toString());
			data.setShopId(cellDataList.get(2).getOldData().toString());
			data.setShopName(cellDataList.get(2).getData().toString());
			data.setShopArea(new BigDecimal(cellDataList.get(3).getData().toString()));
			data.setAddProfit(new BigDecimal(cellDataList.get(4).getData().toString()));
			if (cellDataList.get(5).getData() == null) {
				data.setOpeningDate(null);
			} else {
				data.setOpeningDate(DateUtils.parseDate(cellDataList.get(5).getData().toString(), DateUtils.PATTERN_DAY));
			}
			if (cellDataList.get(6).getData() == null) {
				data.setShutdownDate(null);
			} else {
				data.setShutdownDate(DateUtils.parseDate(cellDataList.get(6).getData().toString(), DateUtils.PATTERN_DAY));
			}
			data.setCreateTime(createTime);
			dataList.add(data);
		}
		return settingBaseShopMapper.insertDataBatch(dataList);
	}

	/**
	 * @Description 校验Excel状态列
	 * @Author 郑勇浩
	 * @Data 2020/3/5 0:30
	 * @Param [excelData]
	 * @return void
	 */
	private void checkExcelStatus(ExcelData excelData) {
		if (excelData.getExcelRowDataList().size() == 0) {
			return;
		}

		List<ExcelRowData> excelRowData = excelData.getExcelRowDataList();
		ExcelRowData excelRowDatum;
		ExcelCellData cellData;
		//非空验证
		for (int i = 0; i < excelRowData.size(); i++) {
			excelRowDatum = excelRowData.get(i);
			cellData = excelRowDatum.getExcelCellDataList().get(7);
			if (cellData.getData() == null || StringUtil.isEmpty(cellData.getData().toString())) {
				continue;
			}
			if (excelRowDatum.getExcelCellDataList().get(7).getData().toString().equals("营业")) {
				excelRowDatum.getExcelCellDataList().get(7).setOldData(1);
			} else if (excelRowDatum.getExcelCellDataList().get(7).getData().toString().equals("关停")) {
				excelRowDatum.getExcelCellDataList().get(7).setOldData(0);
			} else {
				excelData.getExcelErrorList().add(SampleExcelCheck.excelError(excelRowDatum, excelRowDatum.getExcelCellDataList().get(7), "状态列值仅能为营业/关停"));
				excelData.getExcelRowDataList().remove(i);
				i--;
			}
		}
	}
}

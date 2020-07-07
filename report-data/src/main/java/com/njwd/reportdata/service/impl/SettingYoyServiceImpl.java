package com.njwd.reportdata.service.impl;

import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.SettingYoy;
import com.njwd.entity.reportdata.dto.SettingYoyDto;
import com.njwd.entity.reportdata.vo.SettingYoyVo;
import com.njwd.fileexcel.check.SampleExcelCheck;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.mapper.SettingYoyMapper;
import com.njwd.reportdata.service.SettingYoyService;
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
public class SettingYoyServiceImpl implements SettingYoyService {

	@Resource
	private SettingYoyMapper settingYoyMapper;
	@Resource
	private UserController userController;

	/**
	 * @Description 同比环比阀值信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:58
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingYoyVo
	 */
	@Override
	public SettingYoyVo findSettingYoy(SettingYoyDto param) {
		return settingYoyMapper.findSettingYoy(param);
	}

	/**
	 * @Description 查询同比环比阀值列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@Override
	public List<SettingYoyVo> findSettingYoyList(SettingYoyDto param) {
		//传入格式类型 0 字符 1 数字 2日期
		param.setQueryType(0);
		if (param.getQuery() != null) {
			if (StringUtil.isNumeric(param.getQuery())) {
				param.setQueryType(1);
			} else if (DateUtils.isValidDate(param.getQuery())) {
				param.setQueryType(2);
			}
		}
		return settingYoyMapper.findSettingYoyList(param);
	}

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	@Override
	public List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingYoy> valueList) {
		return settingYoyMapper.findDuplicateDataList(enteId, valueList);
	}

	/**
	 * @Description 同比环比阀值EXCEL数据校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 19:43
	 * @Param [excelData]
	 * @return void
	 */
	@Override
	public void checkData(ExcelData excelData) {
		SampleExcelCheck.checkNull(excelData, new int[]{0, 0, 0, 0, 0});
		SampleExcelCheck.checkFormat(excelData, new int[]{0, 5, 5, 4, 4});
		SampleExcelCheck.checkLength(excelData, new int[]{30, 10, 10, -1, -1});
		int[] checkExcelDuplicate = {0};
		int[] date = {3, 4};
		SampleExcelCheck.checkExcelDuplicate(excelData, checkExcelDuplicate, date);
		//重复数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<SettingYoy> duplicateList = new ArrayList<>();
			for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
				SettingYoy data = new SettingYoy();
				data.setTableName(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(0).getData().toString());
				duplicateList.add(data);
			}
			List<Map<String, String>> duplicateDataList = this.findDuplicateDataList(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString(), duplicateList);
//			List<Map<String, String>> duplicateDataList = this.findDuplicateDataList("999", duplicateList);
			SampleExcelCheck.checkDataBaseDuplicate(excelData, checkExcelDuplicate, date, duplicateDataList);
		}
	}

	/**
	 * @Description 更新状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingYoyStatus(SettingYoyDto param) {
		param.setUpdateTime(new Date());
		return settingYoyMapper.updateSettingYoyStatus(param);
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
		List<SettingYoyVo> dataList = new ArrayList<>();
		SettingYoyVo data;
		List<ExcelCellData> cellDataList;
		Date updateTime = new Date();
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			cellDataList = excelData.getExcelRowDataList().get(i).getExcelCellDataList();
			data = new SettingYoyVo();
			data.setId(StringUtil.genUniqueKey());
			data.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//			data.setEnteId("999");
			data.setTableName(cellDataList.get(0).getData().toString());
			data.setYoy(new BigDecimal(cellDataList.get(1).getData().toString()));
			data.setMom(new BigDecimal(cellDataList.get(2).getData().toString()));
			data.setBeginDate(DateUtils.parseDate(cellDataList.get(3).getData().toString(), DateUtils.PATTERN_DAY));
			data.setEndDate(DateUtils.parseDate(cellDataList.get(4).getData().toString(), DateUtils.PATTERN_DAY));
			data.setCreateTime(updateTime);
			dataList.add(data);
		}
		return settingYoyMapper.insertDataBatch(dataList);
	}

}

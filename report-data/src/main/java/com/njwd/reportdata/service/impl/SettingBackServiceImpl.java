package com.njwd.reportdata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingBack;
import com.njwd.entity.reportdata.dto.SettingBackDto;
import com.njwd.entity.reportdata.vo.SettingBackVo;
import com.njwd.fileexcel.check.SampleExcelCheck;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.mapper.SettingBackMapper;
import com.njwd.reportdata.service.SettingBackService;
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
 * @Description 设置模块 退赠优免安全阀值 Service Impl
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Service
public class SettingBackServiceImpl implements SettingBackService {

	@Resource
	private SettingBackMapper settingBackMapper;
	@Resource
	private UserController userController;

	/**
	 * @Description 退赠优免安全阀值信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:58
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingBackVo
	 */
	@Override
	public SettingBackVo findSettingBack(SettingBackDto param) {
		return settingBackMapper.findSettingBack(param);
	}

	/**
	 * @Description 查询退赠优免安全阀值列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@Override
	public Page<SettingBackVo> findSettingBackList(SettingBackDto param) {
		//传入格式类型 0 字符 1 数字 2日期
		param.setQueryType(0);
		if (param.getQuery() != null) {
			if (StringUtil.isNumeric(param.getQuery())) {
				param.setQueryType(1);
			} else if (DateUtils.isValidDate(param.getQuery())) {
				param.setQueryType(2);
			}
		}
		return settingBackMapper.findSettingBackList(param.getPage(), param);
	}

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	@Override
	public List<Map<String, String>> findDuplicateDataList(Long enteId, List<SettingBack> valueList) {
		return settingBackMapper.findDuplicateDataList(enteId, valueList);
	}

	/**
	 * @Description 更新退赠优免安全阀值
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:06
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingBack(SettingBackDto param) {
		param.setUpdateTime(new Date());
		return settingBackMapper.updateSettingBack(param);
	}

	/**
	 * @Description 更新退赠优免安全阀值状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingBackStatus(SettingBackDto param) {
		param.setUpdateTime(new Date());
		return settingBackMapper.updateSettingBackStatus(param);
	}

	/**
	 * @Description 退赠优免安全阀值EXCEL数据校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 19:43
	 * @Param [excelData]
	 * @return void
	 */
	@Override
	public void checkData(ExcelData excelData) {
		setDefaultValue(excelData);
		SampleExcelCheck.checkNull(excelData, new int[]{0, 0, 0, 0, 0, 0});
		SampleExcelCheck.checkFormat(excelData, new int[]{0, 0, 0, 0, 0, 2});
		SampleExcelCheck.checkLength(excelData, new int[]{30, 30, 30, 30, 50, 15});
		int[] checkExcelDuplicate = {2, 3};
		SampleExcelCheck.checkExcelDuplicate(excelData, checkExcelDuplicate, null);
		int[] checkConvert = {0, 1, 2, 3};
		//转化数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<ConvertData> convertDataList = settingBackMapper.findConvertDataList(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//			List<ConvertData> convertDataList = settingBackMapper.findConvertDataList("999");
			SampleExcelCheck.checkConvert(excelData, checkConvert, convertDataList);
		}
		//重复数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<SettingBack> duplicateList = new ArrayList<>();
			for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
				SettingBack data = new SettingBack();
				data.setShopId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(2).getOldData().toString());
				data.setFoodId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(3).getOldData().toString());
				duplicateList.add(data);
			}
			List<Map<String, String>> duplicateDataList = this.findDuplicateDataList(userController.getCurrLoginUserInfo().getRootEnterpriseId(), duplicateList);
//			List<Map<String, String>> duplicateDataList = this.findDuplicateDataList(999L, duplicateList);
			SampleExcelCheck.checkDataBaseDuplicate(excelData, checkExcelDuplicate, null, duplicateDataList);
		}
	}

	/**
	 * @Description 设置默认值
	 * @Author 郑勇浩
	 * @Data 2020/3/27 17:32
	 * @Param [excelData, ints, convertDataMap]
	 */
	private void setDefaultValue(ExcelData excelData) {
		// 获取需要匹配的值
		ExcelCellData shopData;
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			//门店默认值
			shopData = excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(2);
			if (shopData == null || StringUtil.isBlank(shopData.getData())) {
				excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(0).setData("默认");
				excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(1).setData("默认");
				excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(2).setData("默认");
			}
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
		List<SettingBackVo> dataList = new ArrayList<>();
		SettingBackVo data;
		List<ExcelCellData> cellDataList;
		Date updateTime = new Date();
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			cellDataList = excelData.getExcelRowDataList().get(i).getExcelCellDataList();
			data = new SettingBackVo();
			data.setId(StringUtil.genUniqueKey());
			data.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId());
//			data.setEnteId(999L);
			data.setBrandId(cellDataList.get(0).getOldData().toString());
			data.setBrandName(cellDataList.get(0).getData().toString());
			data.setRegionId(cellDataList.get(1).getOldData().toString());
			data.setRegionName(cellDataList.get(1).getData().toString());
			data.setShopId(cellDataList.get(2).getOldData().toString());
			data.setShopName(cellDataList.get(2).getData().toString());
			data.setFoodId(cellDataList.get(3).getOldData().toString());
			data.setThreshold(new BigDecimal(cellDataList.get(5).getData().toString()));
			data.setCreateTime(updateTime);
			dataList.add(data);
		}
		return settingBackMapper.insertDataBatch(dataList);
	}

}

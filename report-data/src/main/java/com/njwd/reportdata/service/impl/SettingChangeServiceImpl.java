package com.njwd.reportdata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingChange;
import com.njwd.entity.reportdata.dto.SettingChangeDto;
import com.njwd.entity.reportdata.vo.SettingChangeVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.fileexcel.check.SampleExcelCheck;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.mapper.SettingChangeMapper;
import com.njwd.reportdata.service.SettingChangeService;
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
public class SettingChangeServiceImpl implements SettingChangeService {

	@Resource
	private SettingChangeMapper settingChangeMapper;
	@Resource
	private UserController userController;

	/**
	 * @Description 异动工资信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:58
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingChangeVo
	 */
	@Override
	public SettingChangeVo findSettingChange(SettingChangeDto param) {
		return settingChangeMapper.findSettingChange(param);
	}

	/**
	 * @Description 查询异动工资列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@Override
	public Page<SettingChangeVo> findSettingChangeList(SettingChangeDto param) {
		//传入格式类型 0 字符 1 数字 2日期
		param.setQueryType(0);
		if (param.getQuery() != null) {
			if (StringUtil.isNumeric(param.getQuery())) {
				param.setQueryType(1);
			} else if (DateUtils.isValidDate(param.getQuery())) {
				param.setQueryType(2);
			}
			//转化为日期格式
			if (param.getQuery().contains("-") && DateUtils.isValidDate(param.getQuery() + "-01")) {
				Date date;
				date = DateUtils.parseDate(param.getQuery() + "-01", DateUtils.PATTERN_DAY);
				if (date != null) {
					param.setDataQuery(DateUtils.getPeriodYearNum(DateUtils.format(date, DateUtils.PATTERN_DAY)));
				}
			}
		}
		return settingChangeMapper.findSettingChangeList(param.getPage(), param);
	}

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	@Override
	public List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingChange> valueList) {
		return settingChangeMapper.findDuplicateDataList(enteId, valueList);
	}

	/**
	 * @Description 更新异动工资
	 * @Author 郑勇浩
	 * @Data 2020/4/1 9:33
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingChange(SettingChangeDto param) {
		//是否存在重复数据
		Integer count = settingChangeMapper.findDuplicateDataCount(param);
		if (count > 0) {
			throw new ServiceException(ResultCode.DATE_DUPLICATE_ERROR);
		}
		return settingChangeMapper.updateSettingChange(param);
	}

	/**
	 * @Description 更新异动工资状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingChangeStatus(SettingChangeDto param) {
		param.setUpdateTime(new Date());
		return settingChangeMapper.updateSettingChangeStatus(param);
	}

	/**
	 * @Description 异动工资EXCEL数据校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 19:43
	 * @Param [excelData]
	 * @return void
	 */
	@Override
	public void checkData(ExcelData excelData) {
		SampleExcelCheck.checkNull(excelData, new int[]{0, 0, 0, 0, 0});
		SampleExcelCheck.checkFormat(excelData, new int[]{0, 0, 0, 2, 6});
		SampleExcelCheck.checkLength(excelData, new int[]{30, 30, 30, 10, 6});
		int[] checkExcelDuplicate = {2, 4};
		SampleExcelCheck.checkExcelDuplicate(excelData, checkExcelDuplicate, null);
		//转化数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<ConvertData> convertDataList = settingChangeMapper.findConvertDataList(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//			List<ConvertData> convertDataList = settingChangeMapper.findConvertDataList("999");
			SampleExcelCheck.checkConvert(excelData, new int[]{0, 1, 2}, convertDataList);
		}
		//重复数据
		if (excelData.getExcelRowDataList().size() != 0) {
			List<SettingChange> duplicateList = new ArrayList<>();
			for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
				SettingChange data = new SettingChange();
				data.setShopId(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(2).getOldData().toString());
				data.setPeriodYearNum(Integer.parseInt(excelData.getExcelRowDataList().get(i).getExcelCellDataList().get(4).getData().toString()));
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
		List<SettingChangeVo> dataList = new ArrayList<>();
		SettingChangeVo data;
		List<ExcelCellData> cellDataList;
		Date updateTime = new Date();
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			cellDataList = excelData.getExcelRowDataList().get(i).getExcelCellDataList();
			data = new SettingChangeVo();
			data.setId(StringUtil.genUniqueKey());
			data.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//			data.setEnteId("999");
			data.setBrandId(cellDataList.get(0).getOldData().toString());
			data.setBrandName(cellDataList.get(0).getData().toString());
			data.setRegionId(cellDataList.get(1).getOldData().toString());
			data.setRegionName(cellDataList.get(1).getData().toString());
			data.setShopId(cellDataList.get(2).getOldData().toString());
			data.setMoney(new BigDecimal(cellDataList.get(3).getData().toString()));
			data.setPeriodYearNum(Integer.parseInt(cellDataList.get(4).getData().toString()));
			data.setCreateTime(updateTime);
			dataList.add(data);
		}
		return settingChangeMapper.insertDataBatch(dataList);
	}

}

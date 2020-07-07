package com.njwd.reportdata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.basedata.mapper.BaseShopAllInfoMapper;
import com.njwd.basedata.service.BaseShopTypeService;
import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.BaseShopAllInfo;
import com.njwd.entity.basedata.ReferenceDescription;
import com.njwd.entity.basedata.dto.BaseShopAllInfoDto;
import com.njwd.entity.basedata.dto.BaseShopTypeDto;
import com.njwd.entity.basedata.vo.BaseShopAllInfoVo;
import com.njwd.entity.basedata.vo.BaseShopTypeVo;
import com.njwd.entity.reportdata.SettingDaily;
import com.njwd.entity.reportdata.dto.BusinessReportDayDto;
import com.njwd.entity.reportdata.dto.SettingDailyDto;
import com.njwd.entity.reportdata.vo.BusinessReportDayItemVo;
import com.njwd.entity.reportdata.vo.SettingDailyVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.mapper.SettingDailyMapper;
import com.njwd.reportdata.service.BusinessAnalysisService;
import com.njwd.reportdata.service.SettingDailyService;
import com.njwd.support.BatchResult;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.DateUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @Description 设置模块 Service Impl
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Service
public class SettingDailyServiceImpl implements SettingDailyService {

	private final static Logger logger = LoggerFactory.getLogger(SettingDailyServiceImpl.class);

	@Resource
	private SettingDailyMapper settingDailyMapper;
	@Resource
	private UserController userController;
	@Resource
	private BaseShopAllInfoMapper baseShopAllInfoMapper;
	@Resource
	private BusinessAnalysisService businessAnalysisService;
	@Resource
	private BaseShopTypeService baseShopTypeService;

	//线程池
	private static ExecutorService executor = Executors.newFixedThreadPool(Constant.Number.TWENTYFOUR);

	/**
	 * @Description 查询经营日报列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@Override
	public List<SettingDailyVo> findDailyList(SettingDailyDto param) {
		// 查询门店类型
		BaseShopTypeDto baseShopTypeDto = new BaseShopTypeDto();
		baseShopTypeDto.setEnteId(param.getEnteId());
		List<BaseShopTypeVo> baseShopTypeVoList = baseShopTypeService.findBaseShopTypeByEnteId(baseShopTypeDto);
		if (baseShopTypeVoList == null) {
			return new ArrayList<>();
		}
		param.setShopTypeIdList(baseShopTypeVoList.stream().map(BaseShopTypeVo::getShopTypeId).collect(Collectors.toList()));
		// 查询对应的门店信息
		BaseShopAllInfoDto baseShopParam = new BaseShopAllInfoDto();
		baseShopParam.setEnteId(param.getEnteId());
		baseShopParam.setShopIdList(param.getShopIdList());
		List<BaseShopAllInfoVo> baseShopVoList = baseShopAllInfoMapper.findBaseShopAllInfoByOrgId(baseShopParam);
		if (baseShopVoList == null || baseShopVoList.size() == Constant.Number.ZERO) {
			return new ArrayList<>();
		}

		//多线程
		CompletionService<List<SettingDailyVo>> threadService = new ExecutorCompletionService<>(executor);
		for (BaseShopAllInfo baseShop : baseShopVoList) {
			threadService.submit(() -> {
				List<SettingDailyVo> settingDailyVoList = new ArrayList<>();

				//查询每个门店对应的经营日报
				BusinessReportDayDto brParam = new BusinessReportDayDto();
				brParam.setEnteId(param.getEnteId());
				brParam.setBeginDate(DateUtils.parseDate(param.getPeriodYearNumStr() + ReportDataConstant.DateType.FIRST_DAY, DateUtils.PATTERN_DAY));
				brParam.setBeginDate(DateUtils.addMonths(brParam.getBeginDate(), Constant.Number.ONE));
				brParam.setEndDate(DateUtils.getLastDayOfMonth(brParam.getBeginDate()));
				brParam.setDataType(Constant.Number.THREE);
				brParam.setDateType(Constant.Number.TWOB);
				brParam.setShopTypeIdList(param.getShopTypeIdList());
				brParam.setShopIdList(Collections.singletonList(baseShop.getShopId()));
				List<BusinessReportDayItemVo> dataList = businessAnalysisService.findBusinessReportDay(brParam, ReportDataConstant.ReportItemReportId.BUSINESSREPORTDAY);
				if (dataList == null || dataList.size() == Constant.Number.ZERO) {
					return new ArrayList<>();
				}
				SettingDailyVo settingDailyVo;
				for (BusinessReportDayItemVo data : dataList) {
					if (data.getItemCode() == null) {
						continue;
					}
					settingDailyVo = new SettingDailyVo();
					settingDailyVo.setBrandId(baseShop.getBrandId());
					settingDailyVo.setBrandName(baseShop.getBrandName());
					settingDailyVo.setRegionId(baseShop.getRegionId());
					settingDailyVo.setRegionName(baseShop.getRegionName());
					settingDailyVo.setShopId(baseShop.getShopId());
					settingDailyVo.setShopName(baseShop.getShopName());
					settingDailyVo.setItemNumber(data.getItemNumber());
					settingDailyVo.setItemName(data.getItemName());
					settingDailyVo.setProjectId(data.getItemCode());
					settingDailyVo.setProjectName(data.getItemName());
					settingDailyVo.setDefaultIndicator(data.getCurrentMoney() == null ? new BigDecimal(Constant.Number.ZERO) : data.getCurrentMoney());
					settingDailyVo.setType(data.getDataType());
					settingDailyVoList.add(settingDailyVo);
				}
				return settingDailyVoList;
			});

		}
		List<SettingDailyVo> returnDataList = new ArrayList<>();
		for (int i = 0; i < baseShopVoList.size(); i++) {
			try {
				returnDataList.addAll(threadService.take().get());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR);
			}
		}
		return returnDataList;
	}

	/**
	 * @Description 查询经营日报分页
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@Override
	public Page<SettingDailyVo> findSettingDailyList(SettingDailyDto param) {
		//传入格式类型 0 字符 1 数字 2日期
		param.setQueryType(Constant.Number.ZERO);
		if (param.getQuery() != null) {
			if (StringUtil.isNumeric(param.getQuery())) {
				param.setQueryType(Constant.Number.ONE);
			} else if (DateUtils.isValidDate(param.getQuery())) {
				param.setQueryType(Constant.Number.TWO);
			}
			//转化为日期格式
			if (param.getQuery().contains(Constant.Character.MIDDLE_LINE) && DateUtils.isValidDate(param.getQuery() + ReportDataConstant.DateType.FIRST_DAY)) {
				Date date;
				date = DateUtils.parseDate(param.getQuery() + ReportDataConstant.DateType.FIRST_DAY, DateUtils.PATTERN_DAY);
				if (date != null) {
					param.setDataQuery(DateUtils.getPeriodYearNum(DateUtils.format(date, DateUtils.PATTERN_DAY)));
				}
			}
		}
		return settingDailyMapper.findSettingDailyList(param.getPage(), param);
	}

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	@Override
	public List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingDaily> valueList) {
		return settingDailyMapper.findDuplicateDataList(enteId, valueList);
	}

	/**
	 * @Description 批量新增经营日报
	 * @Author 郑勇浩
	 * @Data 2020/4/27 9:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer insertSettingDailyBatch(SettingDailyDto param) {
		param.setPeriodYearNumList(new ArrayList<>());
		int startPeriodYearNum = Integer.parseInt(param.getStartDateStart().replace(Constant.Character.MIDDLE_LINE, Constant.Character.NULL_VALUE));
		int endPeriodYearNum = Integer.parseInt(param.getEndDateStr().replace(Constant.Character.MIDDLE_LINE, Constant.Character.NULL_VALUE));
		// 计算中间的月份
		while (startPeriodYearNum <= endPeriodYearNum) {
			param.getPeriodYearNumList().add(startPeriodYearNum);
			if (startPeriodYearNum % Constant.Number.ONEHUNDRED >= 12) {
				startPeriodYearNum += 89;
			} else {
				startPeriodYearNum += 1;
			}
		}
		// 删除原有的数据
		settingDailyMapper.deleteSettingDailyList(param);
		// 先更改值类型
		for (SettingDailyVo data : param.getDataList()) {
			// 把type是3的四舍五入
			if (data.getType().equals(Constant.Number.THREE)) {
				data.setIndicator(BigDecimalUtils.roundProcess(data.getIndicator(), Constant.Number.ZERO));
			}
			data.setEnteId(param.getEnteId());
		}
		// 根据月份生成对应月份的数据
		int result = Constant.Number.ZERO;
		Map<String, List<SettingDailyVo>> insertMap;
		for (Integer periodYearNum : param.getPeriodYearNumList()) {
			List<SettingDailyVo> insertDataList = new ArrayList<>(param.getDataList());
			for (SettingDailyVo data : insertDataList) {
				data.setDailyIndicId(StringUtil.genUniqueKey());
				data.setPeriodYearNum(periodYearNum);
			}
			// 根据shopId分组录入
			insertMap = insertDataList.stream().collect(Collectors.groupingBy(SettingDailyVo::getShopId));
			for (List<SettingDailyVo> insertList : insertMap.values()) {
				result += settingDailyMapper.insertDataBatch(insertList);
			}
		}

		return result;
	}

	/**
	 * @Description 更新经营日报
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:06
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingDaily(SettingDailyDto param) {
		//查询状态
		SettingDailyVo dataStatus = settingDailyMapper.findSettingDailyStatus(param);
		if (dataStatus == null) {
			return Constant.Number.ZERO;
		}
		if (dataStatus.getStatus().equals(Constant.Number.ZERO)) {
			throw new ServiceException(ResultCode.SETTING_MODEL_IS_DISABLE);
		}
		param.setUpdateTime(new Date());
		return settingDailyMapper.updateSettingDaily(param);
	}

	/**
	 * @Description 批量更新经营日报
	 * @Author 郑勇浩
	 * @Data 2020/4/27 9:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public BatchResult updateSettingDailyBatch(SettingDailyDto param) {
		//批量操作状态验证
		BatchResult batchResult = this.checkBatchStatus(Constant.Number.ZERO, param);
		if (batchResult.getSuccessList().size() == 0) {
			return batchResult;
		}
		//更新能更新的列
		settingDailyMapper.updateSettingDailyBatch(param);
		return batchResult;
	}

	/**
	 * @Description 更新经营日报状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingDailyStatus(SettingDailyDto param) {
		// 查询状态
		SettingDailyVo dataStatus = settingDailyMapper.findSettingDailyStatus(param);
		if (dataStatus == null) {
			return Constant.Number.ZERO;
		}
		if (param.getStatus().equals(Constant.Number.ZERO) && dataStatus.getStatus().equals(Constant.Number.ZERO)) {
			return Constant.Number.ZERO;
		} else if (param.getStatus().equals(Constant.Number.ONE) && dataStatus.getStatus().equals(Constant.Number.ONE)) {
			return Constant.Number.ONE;
		}
		return settingDailyMapper.updateSettingDailyStatus(param);
	}

	/**
	 * @Description 批量更新经营日报状态
	 * @Author 郑勇浩
	 * @Data 2020/4/27 11:05
	 * @Param [param]
	 * @return com.njwd.support.BatchResult
	 */
	@Override
	public BatchResult updateSettingDailyStatusBatch(SettingDailyDto param) {
		//批量操作状态验证
		BatchResult batchResult = this.checkBatchStatus(param.getStatus(), param);
		if (batchResult.getSuccessList().size() == 0) {
			return batchResult;
		}
		//更新能更新的列
		settingDailyMapper.updateSettingDailyStatusBatch(param);
		return batchResult;
	}

	/**
	 * @Description 批量状态查询
	 * @Author 郑勇浩
	 * @Data 2020/4/27 10:10
	 * @Param [type, param]
	 * @return com.njwd.support.BatchResult
	 */
	private BatchResult checkBatchStatus(int type, SettingDailyDto param) {
		//初始化
		BatchResult result = new BatchResult();
		result.setFailList(new ArrayList<>());
		result.setSuccessList(new ArrayList<>());
		//查询idList 的状态
		List<SettingDailyVo> settingDailyVoList = settingDailyMapper.findSettingDailyListStatus(param);
		//循环添加错误
		for (SettingDailyVo settingDailyVo : settingDailyVoList) {
			//如果是禁用，则要求是启用状态 反之则反
			if (type == Constant.Number.ONE && settingDailyVo.getStatus().equals(Constant.Number.ONE)) {
				ReferenceDescription<SettingDailyVo> fd = new ReferenceDescription<>();
				fd.setBusinessId(settingDailyVo.getDailyIndicId());
				//返回已启用
				fd.setReferenceDescription(ResultCode.SETTING_MODEL_IS_ENABLE.message);
				result.getFailList().add(fd);
				continue;
			} else if (type == Constant.Number.ZERO && settingDailyVo.getStatus().equals(Constant.Number.ZERO)) {
				ReferenceDescription<SettingDailyVo> fd = new ReferenceDescription<>();
				fd.setBusinessId(settingDailyVo.getDailyIndicId());
				//返回已禁用
				fd.setReferenceDescription(ResultCode.SETTING_MODEL_IS_DISABLE.message);
				result.getFailList().add(fd);
				continue;
			}
			//添加成功的
			result.getSuccessList().add(settingDailyVo.getDailyIndicId());
		}
		return result;
	}


}

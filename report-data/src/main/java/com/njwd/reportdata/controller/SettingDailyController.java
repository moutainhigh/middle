package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.SettingDailyDto;
import com.njwd.entity.reportdata.vo.SettingDailyVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.service.SettingDailyService;
import com.njwd.support.BaseController;
import com.njwd.support.BatchResult;
import com.njwd.support.Result;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 设置 经营日报 controller
 * @Date 2020/3/4 14:58
 * @Author 郑勇浩
 */
@Api(value = "SettingDailyController", tags = "设置-经营日报")
@RestController
@RequestMapping("settingDaily")
public class SettingDailyController extends BaseController {

	@Resource
	private SettingDailyService settingDailyService;

	/**
	 * @Description 查询往期经营日报列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询往期经营日报列表", notes = "查询往期经营日报列表")
	@PostMapping("findDailyList")
	public Result<List<SettingDailyVo>> findDailyList(@RequestBody SettingDailyDto param) {
		// 必填校验
		FastUtils.checkParams(param.getPeriodYearNumStr());
		FastUtils.checkListNullOrEmpty(param.getShopIdList());
		// 日期格式
		if (!DateUtils.isValidDate(param.getPeriodYearNumStr() + ReportDataConstant.DateType.FIRST_DAY)) {
			throw new ServiceException(ResultCode.PARAMS_NOT);
		}
		param.setPeriodYearNum(Integer.parseInt(param.getPeriodYearNumStr().replace(Constant.Character.MIDDLE_LINE, Constant.Character.NULL_VALUE)));
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		return ok(settingDailyService.findDailyList(param));
	}

	/**
	 * @Description 查询经营日报分页
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询经营日报分页", notes = "查询经营日报分页")
	@PostMapping("findSettingDailyList")
	public Result<Page<SettingDailyVo>> findSettingDailyList(@RequestBody SettingDailyDto param) {
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		return ok(settingDailyService.findSettingDailyList(param));
	}


	/**
	 * @Description 批量保存经营日报
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "批量保存经营日报", notes = "批量保存经营日报")
	@PostMapping("insertSettingDailyBatch")
	public Result<Integer> insertSettingDailyBatch(@RequestBody SettingDailyDto param) {
		// 校验
		FastUtils.checkParams(param.getStartDateStart(), param.getEndDateStr());
		FastUtils.checkListNullOrEmpty(param.getDataList());
		for (SettingDailyVo data : param.getDataList()) {
			FastUtils.checkParams(data.getBrandId(), data.getRegionId(), data.getShopId(), data.getProjectId(), data.getProjectName(), data.getIndicator());
		}
		if (!DateUtils.isValidDate(param.getStartDateStart() + ReportDataConstant.DateType.FIRST_DAY)) {
			throw new ServiceException(ResultCode.PARAMS_NOT);
		}
		if (!DateUtils.isValidDate(param.getEndDateStr() + ReportDataConstant.DateType.FIRST_DAY)) {
			throw new ServiceException(ResultCode.PARAMS_NOT);
		}
		if (DateUtils.compareDate(
				param.getStartDateStart() + ReportDataConstant.DateType.FIRST_DAY,
				param.getEndDateStr() + ReportDataConstant.DateType.FIRST_DAY, DateUtils.PATTERN_DAY) == Constant.Number.ONE) {
			throw new ServiceException(ResultCode.CELL_DATE_FORMAT_ERROR);
		}

		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingDailyService.insertSettingDailyBatch(param);
		return ok(result);
	}


	/**
	 * @Description 更新经营日报
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新经营日报", notes = "更新经营日报")
	@PostMapping("updateSettingDaily")
	public Result<Integer> updateSettingDaily(@RequestBody SettingDailyDto param) {
		FastUtils.checkParams(param.getDailyIndicId(), param.getIndicator());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingDailyService.updateSettingDaily(param);
		return ok(result);
	}

	/**
	 * @Description 批量更新经营日报
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "批量更新经营日报", notes = "批量更新经营日报")
	@PostMapping("updateSettingDailyBatch")
	public BatchResult updateSettingDailyBatch(@RequestBody SettingDailyDto param) {
		// 校验
		FastUtils.checkListNullOrEmpty(param.getDataList());
		for (SettingDailyVo data : param.getDataList()) {
			FastUtils.checkParams(data.getDailyIndicId(), data.getIndicator());
		}
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		return settingDailyService.updateSettingDailyBatch(param);
	}


	/**
	 * @Description 更新经营日报状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新经营日报状态", notes = "更新经营日报状态")
	@PostMapping("updateSettingDailyStatus")
	public Result<Integer> updateSettingDailyStatus(@RequestBody SettingDailyDto param) {
		FastUtils.checkParams(param.getDailyIndicId(), param.getStatus());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingDailyService.updateSettingDailyStatus(param);
		return ok(result);
	}

	/**
	 * @Description 批量更新经营日报状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "批量更新经营日报状态", notes = "批量更新经营日报状态")
	@PostMapping("updateSettingDailyStatusBatch")
	public BatchResult updateSettingDailyStatusBatch(@RequestBody SettingDailyDto param) {
		FastUtils.checkParams(param.getStatus());
		FastUtils.checkListNullOrEmpty(param.getDataIdList());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		return settingDailyService.updateSettingDailyStatusBatch(param);
	}

}

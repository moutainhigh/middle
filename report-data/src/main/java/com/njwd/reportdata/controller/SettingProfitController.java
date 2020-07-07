package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.Constant;
import com.njwd.entity.admin.User;
import com.njwd.entity.reportdata.dto.SettingProfitDto;
import com.njwd.entity.reportdata.vo.SettingProfitVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.service.SettingProfitService;
import com.njwd.support.BaseController;
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

/**
 * @Description 设置 实时利润预算 controller
 * @Date 2020/3/4 14:58
 * @Author 郑勇浩
 */
@Api(value = "SettingProfitController", tags = "设置-实时利润")
@RestController
@RequestMapping("settingProfit")
public class SettingProfitController extends BaseController {

	@Resource
	private SettingProfitService settingProfitService;

	/**
	 * @Description 查询实时利润预算信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询实时利润预算信息", notes = "查询实时利润预算信息")
	@PostMapping("findSettingProfit")
	public Result<SettingProfitVo> findSettingProfit(@RequestBody SettingProfitDto param) {
		FastUtils.checkParams(param.getId());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		SettingProfitVo result = settingProfitService.findSettingProfit(param);
		return ok(result);
	}

	/**
	 * @Description 查询实时利润预算列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询实时利润预算列表", notes = "查询实时利润预算列表")
	@PostMapping("findSettingProfitList")
	public Result<Page<SettingProfitVo>> findSettingProfitList(@RequestBody SettingProfitDto param) {
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("2002595340009472");
		return ok(settingProfitService.findSettingProfitList(param));
	}

	/**
	 * @Description 更新实时利润预算
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新实时利润预算", notes = "更新实时利润预算")
	@PostMapping("updateSettingProfit")
	public Result<Integer> updateSettingProfit(@RequestBody SettingProfitDto param) {
		FastUtils.checkParams(param.getId());
		if (param.getDateList() == null || param.getDateList().length != Constant.Number.TWO) {
			throw new ServiceException(ResultCode.PARAMS_NOT);
		}
		if (!param.getDateList()[0].contains("-") || !param.getDateList()[1].contains("-")
				|| !DateUtils.isValidDate(param.getDateList()[0]) || !DateUtils.isValidDate(param.getDateList()[1])) {
			throw new ServiceException(ResultCode.PARAMS_NOT_RIGHT);
		}
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingProfitService.updateSettingProfit(param);
		return ok(result);
	}

	/**
	 * @Description 更新实时利润预算状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新实时利润预算状态", notes = "更新实时利润预算状态")
	@PostMapping("updateSettingProfitStatus")
	public Result<Integer> updateSettingProfitStatus(@RequestBody SettingProfitDto param) {
		FastUtils.checkParams(param.getId(), param.getStatus());
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingProfitService.updateSettingProfitStatus(param);
		return ok(result);
	}

}

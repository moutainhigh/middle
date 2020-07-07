package com.njwd.reportdata.controller;

import com.njwd.common.Constant;
import com.njwd.entity.admin.User;
import com.njwd.entity.reportdata.dto.SettingEvaluateDto;
import com.njwd.entity.reportdata.vo.SettingEvaluateVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.service.SettingEvaluateService;
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
import java.util.List;

/**
 * @Description 设置 评价汇总阀值 controller
 * @Date 2020/3/4 14:58
 * @Author 郑勇浩
 */
@Api(value = "SettingEvaluateController", tags = "设置-评价汇总阀值")
@RestController
@RequestMapping("settingEvaluate")
public class SettingEvaluateController extends BaseController {

	@Resource
	private SettingEvaluateService settingEvaluateService;

	/**
	 * @Description 查询评价汇总阀值信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询评价汇总阀值信息", notes = "查询评价汇总阀值信息")
	@PostMapping("findSettingEvaluate")
	public Result<SettingEvaluateVo> findSettingEvaluate(@RequestBody SettingEvaluateDto param) {
		FastUtils.checkParams(param.getId());
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
//		param.setEnteId("999");
		SettingEvaluateVo result = settingEvaluateService.findSettingEvaluate(param);
		return ok(result);
	}

	/**
	 * @Description 查询评价汇总阀值列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询评价汇总阀值列表", notes = "查询评价汇总阀值列表")
	@PostMapping("findSettingEvaluateList")
	public Result<List<SettingEvaluateVo>> findSettingEvaluateList(@RequestBody SettingEvaluateDto param) {
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
//		param.setEnteId("999");
		List<SettingEvaluateVo> result = settingEvaluateService.findSettingEvaluateList(param);
		return ok(result);
	}

	/**
	 * @Description 更新评价汇总阀值
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新评价汇总阀值", notes = "更新评价汇总阀值")
	@PostMapping("updateSettingEvaluate")
	public Result<Integer> updateSettingEvaluate(@RequestBody SettingEvaluateDto param) {
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
		Integer result = settingEvaluateService.updateSettingEvaluate(param);
		return ok(result);
	}

	/**
	 * @Description 更新评价汇总阀值状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新评价汇总阀值状态", notes = "更新评价汇总阀值状态")
	@PostMapping("updateSettingEvaluateStatus")
	public Result<Integer> updateSettingEvaluateStatus(@RequestBody SettingEvaluateDto param) {
		FastUtils.checkParams(param.getId(), param.getStatus());
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingEvaluateService.updateSettingEvaluateStatus(param);
		return ok(result);
	}

}

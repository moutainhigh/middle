package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.SettingChangeDto;
import com.njwd.entity.reportdata.vo.SettingChangeVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.service.SettingChangeService;
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
 * @Description 设置 异动工资 controller
 * @Date 2020/3/4 14:58
 * @Author 郑勇浩
 */
@Api(value = "SettingChangeController", tags = "设置-异动工资")
@RestController
@RequestMapping("settingChange")
public class SettingChangeController extends BaseController {

	@Resource
	private SettingChangeService settingChangeService;

	/**
	 * @Description 查询异动工资信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询异动工资信息", notes = "查询异动工资信息")
	@PostMapping("findSettingChange")
	public Result<SettingChangeVo> findSettingChange(@RequestBody SettingChangeDto param) {
		FastUtils.checkParams(param.getId());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		SettingChangeVo result = settingChangeService.findSettingChange(param);
		return ok(result);
	}

	/**
	 * @Description 查询异动工资列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询异动工资列表", notes = "查询异动工资列表")
	@PostMapping("findSettingChangeList")
	public Result<Page<SettingChangeVo>> findSettingChangeList(@RequestBody SettingChangeDto param) {
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		return ok(settingChangeService.findSettingChangeList(param));
	}

	/**
	 * @Description 更新异动工资
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新异动工资", notes = "更新异动工资")
	@PostMapping("updateSettingChange")
	public Result<Integer> updateSettingChange(@RequestBody SettingChangeDto param) {
		FastUtils.checkParams(param.getId(), param.getShopId(), param.getPeriodYearNumStr(), param.getMoney());
		if (!DateUtils.isValidDate(param.getPeriodYearNumStr() + "-01")) {
			throw new ServiceException(ResultCode.PARAMS_NOT);
		}
		param.setPeriodYearNum(Integer.parseInt(param.getPeriodYearNumStr().replace("-", "")));
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingChangeService.updateSettingChange(param);
		return ok(result);
	}

	/**
	 * @Description 更新异动工资状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新异动工资状态", notes = "更新异动工资状态")
	@PostMapping("updateSettingChangeStatus")
	public Result<Integer> updateSettingChangeStatus(@RequestBody SettingChangeDto param) {
		FastUtils.checkParams(param.getId(), param.getStatus());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
		Integer result = settingChangeService.updateSettingChangeStatus(param);
		return ok(result);
	}

}

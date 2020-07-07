package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.Constant;
import com.njwd.entity.reportdata.dto.SettingEntryFreeDto;
import com.njwd.entity.reportdata.vo.SettingEntryFreeVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.service.SettingEntryFreeService;
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
 * @Description 设置 啤酒进场费 controller
 * @Date 2020/3/4 14:58
 * @Author 郑勇浩
 */
@Api(value = "SettingEntryFreeController", tags = "设置-啤酒进场费")
@RestController
@RequestMapping("settingEntryFree")
public class SettingEntryFreeController extends BaseController {

	@Resource
	private SettingEntryFreeService settingEntryFreeService;

	/**
	 * @Description 查询啤酒进场费信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询啤酒进场费信息", notes = "查询啤酒进场费信息")
	@PostMapping("findSettingEntryFree")
	public Result<SettingEntryFreeVo> findSettingEntryFree(@RequestBody SettingEntryFreeDto param) {
		FastUtils.checkParams(param.getId());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		SettingEntryFreeVo result = settingEntryFreeService.findSettingEntryFree(param);
		return ok(result);
	}

	/**
	 * @Description 查询啤酒进场费列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询啤酒进场费列表", notes = "查询啤酒进场费列表")
	@PostMapping("findSettingEntryFreeList")
	public Result<Page<SettingEntryFreeVo>> findSettingEntryFreeList(@RequestBody SettingEntryFreeDto param) {
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		return ok(settingEntryFreeService.findSettingEntryFreeList(param));
	}

	/**
	 * @Description 更新啤酒进场费
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新啤酒进场费", notes = "更新啤酒进场费")
	@PostMapping("updateSettingEntryFree")
	public Result<Integer> updateSettingEntryFree(@RequestBody SettingEntryFreeDto param) {
		FastUtils.checkParams(param.getId(), param.getMoney(), param.getSupplierId(), param.getShopId());
		if (param.getDateList() == null || param.getDateList().length != Constant.Number.TWO) {
			throw new ServiceException(ResultCode.PARAMS_NOT);
		}
		if (!param.getDateList()[0].contains("-") || !param.getDateList()[1].contains("-")
				|| !DateUtils.isValidDate(param.getDateList()[0]) || !DateUtils.isValidDate(param.getDateList()[1])) {
			throw new ServiceException(ResultCode.PARAMS_NOT_RIGHT);
		}
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingEntryFreeService.updateSettingEntryFree(param);
		return ok(result);
	}

	/**
	 * @Description 更新啤酒进场费状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新啤酒进场费状态", notes = "更新啤酒进场费状态")
	@PostMapping("updateSettingEntryFreeStatus")
	public Result<Integer> updateSettingEntryFreeStatus(@RequestBody SettingEntryFreeDto param) {
		FastUtils.checkParams(param.getId(), param.getStatus());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
		Integer result = settingEntryFreeService.updateSettingEntryFreeStatus(param);
		return ok(result);
	}

}

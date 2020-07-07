package com.njwd.reportdata.controller;

import com.njwd.entity.admin.User;
import com.njwd.entity.reportdata.dto.SettingBaseShopDto;
import com.njwd.entity.reportdata.vo.SettingBaseShopVo;
import com.njwd.reportdata.service.SettingBaseShopService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
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
 * @Description 设置 - 门店 controller
 * @Date 2020/3/4 14:58
 * @Author 郑勇浩
 */
@Api(value = "SettingBaseShopController", tags = "设置-门店")
@RestController
@RequestMapping("settingBaseShop")
public class SettingBaseShopController extends BaseController {

	@Resource
	private SettingBaseShopService settingBaseShopService;

	/**
	 * @Description 查询门店基础信息信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询门店基础信息信息", notes = "查询门店基础信息信息")
	@PostMapping("findSettingBaseShop")
	public Result<SettingBaseShopVo> findSettingBaseShop(@RequestBody SettingBaseShopDto param) {
		FastUtils.checkParams(param.getId());
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
		SettingBaseShopVo result = settingBaseShopService.findSettingBaseShop(param);
		return ok(result);
	}

	/**
	 * @Description 查询门店基础信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询门店基础信息列表", notes = "查询门店基础信息列表")
	@PostMapping("findSettingBaseShopList")
	public Result<List<SettingBaseShopVo>> findSettingBaseShopList(@RequestBody SettingBaseShopDto param) {
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		List<SettingBaseShopVo> result = settingBaseShopService.findSettingBaseShopList(param);
		return ok(result);
	}

	/**
	 * @Description 更新门店基础信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新门店基础信息", notes = "更新门店基础信息")
	@PostMapping("updateSettingBaseShop")
	public Result<Integer> updateSettingBaseShop(@RequestBody SettingBaseShopDto param) {
		FastUtils.checkParams(param.getId());
		if (param.getOpeningDateStr().equals("NaN-NaN-NaN")) {
			param.setOpeningDateStr(null);
		}
		if (param.getShutdownDateStr().equals("NaN-NaN-NaN")) {
			param.setShutdownDateStr(null);
		}
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingBaseShopService.updateSettingBaseShop(param);
		return ok(result);
	}

	/**
	 * @Description 更新门店基础信息状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新门店基础信息状态", notes = "更新门店基础信息状态")
	@PostMapping("updateSettingBaseShopStatus")
	public Result<Integer> updateSettingBaseShopStatus(@RequestBody SettingBaseShopDto param) {
		FastUtils.checkParams(param.getId(), param.getStatus());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingBaseShopService.updateSettingBaseShopStatus(param);
		return ok(result);
	}

}

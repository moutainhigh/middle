package com.njwd.reportdata.controller;

import com.njwd.entity.admin.User;
import com.njwd.entity.reportdata.dto.SettingYoyDto;
import com.njwd.entity.reportdata.vo.SettingYoyVo;
import com.njwd.reportdata.service.SettingYoyService;
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
 * @Description 设置 同比环比阀值 controller
 * @Date 2020/3/4 14:58
 * @Author 郑勇浩
 */
@Api(value = "SettingYoyController", tags = "设置-同比环比阀值")
@RestController
@RequestMapping("settingYoy")
public class SettingYoyController extends BaseController {

	@Resource
	private SettingYoyService settingYoyService;

	/**
	 * @Description 查询同比环比阀值信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询同比环比阀值信息", notes = "查询同比环比阀值信息")
	@PostMapping("findSettingYoy")
	public Result<SettingYoyVo> findSettingYoy(@RequestBody SettingYoyDto param) {
		FastUtils.checkParams(param.getId());
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
//		param.setEnteId("999");
		SettingYoyVo result = settingYoyService.findSettingYoy(param);
		return ok(result);
	}

	/**
	 * @Description 查询同比环比阀值列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询同比环比阀值列表", notes = "查询同比环比阀值列表")
	@PostMapping("findSettingYoyList")
	public Result<List<SettingYoyVo>> findSettingYoyList(@RequestBody SettingYoyDto param) {
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
//		param.setEnteId("999");
		List<SettingYoyVo> result = settingYoyService.findSettingYoyList(param);
		return ok(result);
	}

	/**
	 * @Description 更新同比环比阀值状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新同比环比阀值状态", notes = "更新同比环比阀值状态")
	@PostMapping("updateSettingYoyStatus")
	public Result<Integer> updateSettingYoyStatus(@RequestBody SettingYoyDto param) {
		FastUtils.checkParams(param.getId(), param.getStatus());
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
//		param.setEnteId("999");
		Integer result = settingYoyService.updateSettingYoyStatus(param);
		return ok(result);
	}

}

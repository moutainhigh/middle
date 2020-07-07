package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.User;
import com.njwd.entity.reportdata.dto.SettingBackDto;
import com.njwd.entity.reportdata.vo.SettingBackVo;
import com.njwd.reportdata.service.SettingBackService;
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

/**
 * @Description 设置 退赠优免安全阀值 controller
 * @Date 2020/3/4 14:58
 * @Author 郑勇浩
 */
@Api(value = "SettingBackController", tags = "设置-退赠")
@RestController
@RequestMapping("settingBack")
public class SettingBackController extends BaseController {

	@Resource
	private SettingBackService settingBackService;

	/**
	 * @Description 查询退赠优免安全阀值信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询退赠优免安全阀值信息", notes = "查询退赠优免安全阀值信息")
	@PostMapping("findSettingBack")
	public Result<SettingBackVo> findSettingBack(@RequestBody SettingBackDto param) {
		FastUtils.checkParams(param.getId());
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId());
//		param.setEnteId(999L);
		SettingBackVo result = settingBackService.findSettingBack(param);
		return ok(result);
	}

	/**
	 * @Description 查询退赠优免安全阀值列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	@ApiOperation(value = "查询退赠优免安全阀值列表", notes = "查询退赠优免安全阀值列表")
	@PostMapping("findSettingBackList")
	public Result<Page<SettingBackVo>> findSettingBackList(@RequestBody SettingBackDto param) {
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId());
//		param.setEnteId(999L);
		return ok(settingBackService.findSettingBackList(param));
	}

	/**
	 * @Description 更新退赠优免安全阀值
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新退赠优免安全阀值", notes = "更新退赠优免安全阀值")
	@PostMapping("updateSettingBack")
	public Result<Integer> updateSettingBack(@RequestBody SettingBackDto param) {
		FastUtils.checkParams(param.getId(), param.getThreshold());
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId());
		Integer result = settingBackService.updateSettingBack(param);
		return ok(result);
	}

	/**
	 * @Description 更新退赠优免安全阀值状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:39
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "更新退赠优免安全阀值状态", notes = "更新退赠优免安全阀值状态")
	@PostMapping("updateSettingBackStatus")
	public Result<Integer> updateSettingBackStatus(@RequestBody SettingBackDto param) {
		FastUtils.checkParams(param.getId(), param.getStatus());
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId());
		Integer result = settingBackService.updateSettingBackStatus(param);
		return ok(result);
	}

}

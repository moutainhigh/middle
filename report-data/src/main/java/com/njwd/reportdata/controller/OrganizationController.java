package com.njwd.reportdata.controller;

import com.njwd.entity.admin.User;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseBrandVo;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.reportdata.service.OrganizationService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/12/30 9:33
 */
@Api(value = "organization", tags = "门店组织机构")
@RestController
@RequestMapping("organization")
public class OrganizationController extends BaseController {

	@Autowired
	private OrganizationService organizationService;

	/**
	 * @Author ZhuHC
	 * @Date 2019/12/30 11:42
	 * @Param [baseShopDto]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.basedata.vo.BaseBrandVo>>
	 * @Description 查询集团机构数据
	 */
	@ApiOperation(value = "查询集团机构数据", notes = "根据集团获取品牌区域下的门店信息")
	@PostMapping("findShopInfo")
	public Result<List<BaseBrandVo>> findShopInfo(@RequestBody BaseShopDto baseShopDto) {
		FastUtils.checkParams(baseShopDto.getEnteId());
		FastUtils.checkParams(baseShopDto.getShopTypeIdList());
		return ok(organizationService.findAllShopInfo(baseShopDto));

	}

	/**
	 * @Description 门店下拉查询
	 * @Author 郑勇浩
	 * @Data 2020/3/5 11:00
	 * @Param [baseShopDto]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.basedata.vo.BaseBrandVo>>
	 */
	@ApiOperation(value = "查询所有门店下拉", notes = "查询所有门店下拉")
	@PostMapping("findShopList")
	public Result<List<BaseShopVo>> findShopList(@RequestBody BaseShopDto param) {
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId().toString());
//		param.setEnteId("999");
		return ok(organizationService.findShopList(param));
	}

}

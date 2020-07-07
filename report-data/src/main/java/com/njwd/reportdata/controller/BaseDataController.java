package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.dto.TableObjDto;
import com.njwd.entity.admin.vo.TableObjVo;
import com.njwd.reportdata.service.BaseDataService;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 基础数据 - 主数据
 * @Date 2020/3/9 14:58
 * @Author 郑勇浩
 */
@Api(value = "BaseDataController", tags = "基础-主数据")
@RestController
@RequestMapping("baseData")
public class BaseDataController extends BaseController {

	@Resource
	private BaseDataService baseDataService;

	/**
	 * @Description 查询主数据展示模块
	 * @Author 郑勇浩
	 * @Data 2020/3/9 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "查询主数据展示模块", notes = "查询主数据展示模块")
	@PostMapping("findTableObj")
	public Result<TableObjVo> findTableObj(@RequestBody TableObjDto param) {
		if (param.getDataType() == null) {
			return ok(new TableObjVo());
		}
		return ok(baseDataService.findTableObj(param));
	}

	/**
	 * @Description 查询主数据展示模块列表
	 * @Author 郑勇浩
	 * @Data 2020/3/9 17:04
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "查询主数据展示模块列表", notes = "查询主数据展示模块列表")
	@PostMapping("findTableObjList")
	public Result<List<TableObjVo>> findTableObjList(@RequestBody TableObjDto param) {
		return ok(baseDataService.findTableObjList(param));
	}

	/**
	 * @Description 查询主数据展示模块内容列表
	 * @Author 郑勇浩
	 * @Data 2020/3/9 17:19
	 * @Param [param]
	 * @return com.njwd.support.Result<java.util.Map < java.lang.String, java.lang.String>>
	 */
	@ApiOperation(value = "查询主数据展示模块内容列表", notes = "查询主数据展示模块内容列表")
	@PostMapping("findTableObjContentList")
	public Result<Page<HashMap<String, String>>> findTableObjContentList(@RequestBody TableObjDto param) {
		if (param.getDataType() == null) {
			return ok(new Page<>());
		}
		param.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		param.setEnteId("999");
		return ok(baseDataService.findTableObjContentList(param));
	}

}

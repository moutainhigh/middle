package com.njwd.reportdata.controller;

import com.alibaba.excel.util.CollectionUtils;
import com.njwd.basedata.service.impl.BaseReportItemSetServiceImpl;
import com.njwd.entity.admin.User;
import com.njwd.entity.basedata.dto.BaseReportItemSetDto;
import com.njwd.entity.basedata.excel.ExcelRequest;
import com.njwd.entity.basedata.excel.ExcelResult;
import com.njwd.entity.basedata.vo.BaseReportItemSetVo;
import com.njwd.entity.reportdata.dto.SettingModelContentDto;
import com.njwd.entity.reportdata.dto.SettingModelDto;
import com.njwd.entity.reportdata.vo.SettingModelContentVo;
import com.njwd.entity.reportdata.vo.SettingModelVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.service.SettingModelService;
import com.njwd.service.FileService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 设置模块 Controller.
 *
 * @Description: SettingController
 * @Author: ZhengYongHao
 * @Date: 2020-02-05 10:37
 */
@Api(value = "settingModelController", tags = "设置模块")
@RestController
@RequestMapping("settingModel")
public class SettingModelController extends BaseController {

	@Resource
	private SettingModelService settingModelService;
	@Resource
	private FileService fileService;
	@Resource
	private BaseReportItemSetServiceImpl baseReportItemSetService;

	/**
	 * @Description 查询模块规则, 对应列是否可修改
	 * @Author 郑勇浩
	 * @Data 2020/2/16 14:15
	 * @Param [settingModelName]
	 * @return com.njwd.support.Result<com.njwd.entity.reportdata.vo.SettingModelVo>
	 */
	@ApiOperation(value = "查询模块规则", notes = "查询模块规则")
	@PostMapping("findSettingModelByName")
	public Result<SettingModelVo> findSettingModelByName(@RequestBody SettingModelDto param) {
		FastUtils.checkParams(param.getModelName());
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId());
		SettingModelVo result = settingModelService.findSettingModelByName(param.getModelName());
		return ok(result);
	}

	/**
	 * @Description 查询简单设置模块列表
	 * @Author 郑勇浩
	 * @Data 2020/2/6 14:34
	 * @Param [param]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.SettingModelVo>>
	 */
	@ApiOperation(value = "查询简单设置模块列表", notes = "查询简单设置模块列表")
	@PostMapping("findSampleSettingModelList")
	public Result<List<SettingModelVo>> findSampleSettingModelList(@RequestBody SettingModelDto param) {
		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId());
		List<SettingModelVo> resultList = settingModelService.findSampleSettingModelList(param);
		return ok(resultList);
	}

	/**
	 * @Description 查询设置模块内容列表
	 * @Author 郑勇浩
	 * @Data 2020/2/7 15:07
	 * @Param [param]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.SettingModelContentVo>>
	 */
	@ApiOperation(value = "查询设置模块内容列表", notes = "查询设置模块内容列表")
	@PostMapping("findSettingModelContentList")
	public Result<List<SettingModelContentVo>> findSettingModelContentList(@RequestBody SettingModelContentDto param) {
		FastUtils.checkParams(param.getSettingModelId());
		List<SettingModelContentVo> resultList = settingModelService.findSettingModelContentList(param);
		return ok(resultList);
	}

	/**
	 * @Description 查询设置模块对应数据
	 * @Author 郑勇浩
	 * @Data 2020/2/16 15:27
	 * @Param [param]
	 * @return com.njwd.support.Result<java.util.HashMap < java.lang.String, java.lang.Object>>
	 */
	@ApiOperation(value = "查询设置模块对应数据", notes = "查询设置模块对应数据")
	@PostMapping("findSettingModelData")
	public Result<List<Map<String, Object>>> findSettingModelData(@RequestBody SettingModelDto param) {
		FastUtils.checkParams(param.getModelName());
		return ok(settingModelService.findSettingModelData(param));
	}

	/**
	 * @Description 创建设置分类新模块
	 * @Author 郑勇浩
	 * @Data 2020/2/5 14:28
	 * @Param [baseShopTypeDto]
	 * @return List<BaseShopTypeVo>
	 */
	@ApiOperation(value = "新增设置模块", notes = "新增设置模块")
	@PostMapping("createSettingModel")
	public Result<Integer> createSettingModel(@RequestBody SettingModelDto param) {
		FastUtils.checkParams(param.getModelName(), param.getModelTitle(), param.getTableName());
		if (CollectionUtils.isEmpty(param.getSettingModelContentVoList())) {
			throw new ServiceException(ResultCode.PARAMS_NOT);
		}
		for (SettingModelContentVo content : param.getSettingModelContentVoList()) {
			FastUtils.checkParams(content.getColName(), content.getColTitle(), content.getTableColName(), content.getFormat(), content.getMaxLength());
		}

		User user = getCurrLoginUserInfo();
		param.setEnteId(user.getRootEnterpriseId());
		Integer result = settingModelService.insectSettingModel(param);
		return ok(result);
	}

	/**
	 * @Description 启用设置模块
	 * @Author 郑勇浩
	 * @Data 2020/2/11 15:56
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "启用设置模块", notes = "启用设置模块")
	@PostMapping("enableSettingModel")
	public Result<Integer> enableSettingModel(@RequestBody SettingModelDto param) {
		FastUtils.checkParams(param.getId());
		Integer result = settingModelService.enableSettingModel(param);
		return ok(result);
	}

	/**
	 * @Description 生成设置模块模板
	 * @Author 郑勇浩
	 * @Data 2020/2/11 15:56
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "生成设置模块模板", notes = "生成设置模块模板")
	@PostMapping("createSettingModelTemplate")
	public Result<Integer> createSettingModelTemplate(@RequestBody SettingModelDto param) {
		FastUtils.checkParams(param.getId());
		Integer result = settingModelService.createSettingModelTemplate(param);
		return ok(result);
	}

	/**
	 * @Description 修改设置模块数据
	 * @Author 郑勇浩
	 * @Data 2020/2/17 16:52
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "修改设置模块数据", notes = "修改设置模块数据")
	@PostMapping("updateSettingModelData")
	public Result<Integer> updateSettingModelData(@RequestBody Map<String, String> param) {
		//必填校验
		if (param.get("id") == null && param.get("modelName") == null) {
			throw new ServiceException(ResultCode.PARAMS_NOT);
		}
		Integer result = settingModelService.updateSettingModelData(param);
		return ok(result);
	}


	/**
	 * @Description 修改设置模块数据状态
	 * @Author 郑勇浩
	 * @Data 2020/2/17 16:52
	 * @Param [param]
	 * @return com.njwd.support.Result<java.lang.Integer>
	 */
	@ApiOperation(value = "修改设置模块数据状态", notes = "修改设置模块数据状态")
	@PostMapping("updateSettingModelDataStatus")
	public Result<Integer> updateSettingModelDataStatus(@RequestBody SettingModelContentDto param) {
		FastUtils.checkParams(param.getModelName(), param.getId(), param.getStatus());
		Integer result = settingModelService.updateSettingModelDataStatus(param);
		return ok(result);
	}

	/**
	 * @Description 上传excel
	 * @Author 郑勇浩
	 * @Data 2020/2/19 16:31
	 * @Param [file, settingModelName]
	 * @return com.njwd.support.Result
	 */
	@ApiOperation(value = "Excel文件上传", notes = "Excel文件上传")
	@PostMapping("uploadExcel")
	public Result<String> uploadExcel(@RequestParam(value = "file") MultipartFile file, String settingModelName) {
		//非空验证
		FastUtils.checkParams(settingModelName);
		return ok(settingModelService.uploadExcel(file, settingModelName));
	}

	/**
	 * @Description 校验Excel
	 * @Author 郑勇浩
	 * @Data 2020/2/19 16:31
	 * @Param [file, settingModelName]
	 * @return com.njwd.support.Result
	 */
	@ApiOperation(value = "Excel文件校验", notes = "Excel文件校验")
	@PostMapping("checkExcel")
	public Result<ExcelResult> checkExcel(@RequestBody SettingModelDto param) {
		//非空验证
		FastUtils.checkParams(param.getTemplateName(), param.getModelName());
		return ok(settingModelService.checkExcel(param.getTemplateName(), param.getModelName()));
	}


	/**
	 * @Description Excel文件上传校验
	 * @Author 郑勇浩
	 * @Data 2020/2/13 14:25
	 * @Param [file, settingModelId]
	 * @return com.njwd.support.Result<com.njwd.entity.basedata.excel.ExcelResult>
	 */
	@ApiOperation(value = "Excel文件上传校验", notes = "Excel文件上传校验")
	@PostMapping("uploadAndCheckExcel")
	public Result<ExcelResult> uploadAndCheckExcel(@RequestParam(value = "file") MultipartFile file, String settingModelName) {
		//非空验证
		FastUtils.checkParams(settingModelName);
		//校验excel类型
		if (file.getOriginalFilename() == null || !file.getOriginalFilename().matches(".+\\.(xls|xlsx)")) {
			throw new ServiceException(ResultCode.FILE_MUST_IS_EXCEL);
		}
		return ok(settingModelService.uploadAndCheckExcel(file, settingModelName));
	}

	/**
	 * @Description 导入Excel
	 * @Author 郑勇浩
	 * @Data 2020/2/14 17:28
	 * @Param [excelRequest]
	 * @return com.njwd.support.Result<com.njwd.entity.basedata.excel.ExcelResult>
	 */
	@ApiOperation(value = "Excel文件上传", notes = "Excel文件上传")
	@PostMapping("importExcel")
	public Result<Integer> importExcel(@RequestBody ExcelRequest excelRequest) {
		FastUtils.checkParams(excelRequest.getUuid());
		return ok(settingModelService.importExcel(excelRequest.getUuid()));
	}

	/**
	 * @Description 下载excel模板
	 * @Author 郑勇浩
	 * @Data 2020/2/19 15:14
	 * @Param [response, settingModelName]
	 */
	@ApiOperation(value = "Excel模板下载", notes = "Excel模板下载")
	@PostMapping("downloadExcelTemplate")
	public void downloadExcelTemplate(HttpServletResponse response, @RequestParam String settingModelName) throws IOException {
		settingModelService.downloadExcelTemplate(response, settingModelName);
	}

	/**
	 * @Description 下载excel结果
	 * @Author 郑勇浩
	 * @Data 2020/2/19 17:34
	 * @Param [uuid]
	 * @return org.springframework.http.ResponseEntity<byte [ ]>
	 */
	@ApiOperation(value = "下载excel结果", notes = "下载excel结果")
	@PostMapping("downloadExcelResult")
	public ResponseEntity<byte[]> downloadExcelResult(String uuid) {
		return fileService.downloadExcelResult(uuid);
	}

	/**
	 * @Description 根据报表id查询项目配置项
	 * @Author 郑勇浩
	 * @Data 2020/3/4 13:59
	 * @Param [baseReportItemSetDto]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.basedata.vo.BaseReportItemSetVo>>
	 */
	@ApiOperation(value = "根据报表id查询项目配置项", notes = "根据报表id查询项目配置项")
	@PostMapping("findBaseReportItemSetVoByReportId")
	public Result<List<BaseReportItemSetVo>> findBaseReportItemSetVoByReportId(@RequestBody BaseReportItemSetDto baseReportItemSetDto) {
		FastUtils.checkParams(baseReportItemSetDto.getReportId());
		return ok(baseReportItemSetService.findBaseReportItemSetVoByReportId(baseReportItemSetDto));
	}

}

package com.njwd.reportdata.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.basedata.excel.ExcelResult;
import com.njwd.entity.basedata.excel.ExcelRowData;
import com.njwd.entity.basedata.excel.ExcelTemplate;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingModelContent;
import com.njwd.entity.reportdata.dto.SettingModelContentDto;
import com.njwd.entity.reportdata.dto.SettingModelDto;
import com.njwd.entity.reportdata.vo.SettingBaseShopVo;
import com.njwd.entity.reportdata.vo.SettingModelContentVo;
import com.njwd.entity.reportdata.vo.SettingModelVo;
import com.njwd.excel.utils.ExcelUtil;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.fileexcel.check.SampleExcelCheck;
import com.njwd.fileexcel.read.ExcelRead;
import com.njwd.fileexcel.read.SampleExcelRead;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.mapper.SettingModelContentMapper;
import com.njwd.reportdata.mapper.SettingModelMapper;
import com.njwd.reportdata.service.*;
import com.njwd.utils.DateUtils;
import com.njwd.utils.RedisUtils;
import com.njwd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description 设置模块 Service Impl
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Service
public class SettingModelServiceImpl implements SettingModelService {

	@Value("${constant.file.excelRootPath}")
	private String excelRootPath;

	@Resource
	private SettingModelMapper settingModelMapper;
	@Resource
	private SettingModelContentMapper settingModelContentMapper;
	@Resource
	private UserController userController;

	@Resource
	private SettingBaseShopService settingBaseShopService;
	@Resource
	private SettingDailyService settingDailyService;
	@Resource
	private SettingBackService settingBackService;
	@Resource
	private SettingProfitService settingProfitService;
	@Resource
	private SettingEvaluateService settingEvaluateService;
	@Resource
	private SettingYoyService settingYoyService;
	@Resource
	private SettingChangeService settingChangeService;
	@Resource
	private SettingEntryFreeService settingEntryFreeService;
	@Resource
	private FinReportConfigService finReportConfigService;

	String[] modeNames = {"storeInfo_index", "daily_index", "back_index", "profit_index", "evaluate_index", "value_index", "wage_index", "beer_index", "finReportConfig_index"};

	/**
	 * @Description 查询设置模块 通过id
	 * @Author 郑勇浩
	 * @Data 2020/2/12 11:00
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingModelVo
	 */
	@Override
	public SettingModelVo findSettingModelById(String settingModelId) {
		SettingModelDto param = new SettingModelDto();
		param.setId(settingModelId);
		param.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId());
		SettingModelVo settingModelVo = settingModelMapper.findSettingModel(param);
		if (settingModelVo == null || !settingModelVo.getStatus().equals(Constant.Number.ONE)) {
			return null;
		}
		return settingModelVo;
	}

	/**
	 * @Description 查询设置模块 通过模块名称
	 * @Author 郑勇浩
	 * @Data 2020/2/16 13:44
	 * @Param [settingModelName]
	 * @return com.njwd.entity.reportdata.vo.SettingModelVo
	 */
	@Override
	public SettingModelVo findSettingModelByName(String settingModelName) {
		SettingModelDto param = new SettingModelDto();
		param.setModelName(settingModelName);
		param.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId());
		SettingModelVo settingModelVo = settingModelMapper.findSettingModel(param);
		if (settingModelVo == null || !settingModelVo.getStatus().equals(Constant.Number.ONE)) {
			return null;
		}
		return settingModelVo;
	}

	/**
	 * @Description 查询简单设置模块列表
	 * @Author 郑勇浩
	 * @Data 2020/2/6 14:31
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingModelVo>
	 */
	@Override
	public List<SettingModelVo> findSampleSettingModelList(SettingModelDto param) {
		return settingModelMapper.findSampleSettingModelList(param);
	}

	/**
	 * @Description 查询设置模块内容列表
	 * @Author 郑勇浩
	 * @Data 2020/2/7 15:06
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingModelContentVo>
	 */
	@Override
	public List<SettingModelContentVo> findSettingModelContentList(SettingModelContentDto param) {
		return settingModelContentMapper.findSettingModelContentList(param);
	}

	/**
	 * @Description 设置模块数据
	 * @Author 郑勇浩
	 * @Data 2020/2/20 16:29
	 * @Param [param]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	@Override
	public List<Map<String, Object>> findSettingModelData(SettingModelDto param) {
		param.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId());
//		param.setEnteId(999L);
		SettingModelVo settingModelVo = settingModelMapper.findSettingModel(param);
		if (settingModelVo == null || !settingModelVo.getStatus().equals(Constant.Number.ONE)) {
			return null;
		}
		//传入格式类型 0 字符 1 数字 2日期
		int queryType = 0;
		if (param.getQuery() != null) {
			if (StringUtil.isNumeric(param.getQuery())) {
				queryType = 1;
			} else if (DateUtils.isValidDate(param.getQuery())) {
				queryType = 2;
			}
		}

		Object value;
		List<Map<String, Object>> resultList = settingModelMapper.findSettingModelData(settingModelVo, param.getQuery(), queryType);
		for (Map<String, Object> map : resultList) {
			for (String key : map.keySet()) {
				map.putIfAbsent(key, "");
				value = map.get(key);
				// 日期格式
				if (value instanceof Date) {
					map.put(key, value.toString().substring(0, 10));
				}
			}
		}

		return resultList;
	}

	/**
	 * @Description 查询重复的设置模块数据
	 * @Author 郑勇浩
	 * @Data 2020/2/20 16:31
	 * @Param [tableName, conditionList]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	@Override
	public List<Map<String, String>> findSettingModelData2(String tableName, Map<String, String> conditionMap) {
		return settingModelMapper.findSettingModelData2(tableName, conditionMap);
	}

	/**
	 * @Description 查询需要转化的列对应值
	 * @Author 郑勇浩
	 * @Data 2020/2/26 23:24
	 * @Param [enteId, tableMap]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	@Override
	public List<ConvertData> findConvertDataList(Long enteId, Map<Integer, SettingModelContent> tableMap,
												 Map<Integer, String> nameMap) {
		return settingModelMapper.findConvertDataList(enteId, tableMap, nameMap);
	}

	/**
	 * @Description 查询重复的列内容
	 * @Author 郑勇浩
	 * @Data 2020/2/27 11:18
	 * @Param [enteId, tableName, titleList, valueList]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	@Override
	public List<Map<String, String>> findDuplicateMap(Long enteId, String tableName, List<SettingModelContent> titleList, List<Map<String, String>> valueList) {
		return settingModelMapper.findDuplicateData(enteId, tableName, titleList, valueList);
	}

	/**
	 * @Description 添加设置模块
	 * @Author 郑勇浩qq
	 * @Data 2020/2/11 15:54
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer insectSettingModel(SettingModelDto param) {
		// 唯一校验
		Integer result = settingModelMapper.findSettingModelTable(param);
		if (result > 0) {
			throw new ServiceException(ResultCode.TABLE_EXISTS);
		}
		// 生成主表ID 新增主表信息
		param.setId(StringUtil.genUniqueKey());
		param.setStatus(Constant.Number.ZERO);
		if (param.getSort() == null) {
			param.setSort(Constant.Number.ZERO);
		}
		if (param.getCanImport() == null) {
			param.setCanImport(Constant.Number.ONE);
		}
		if (param.getCanUpdate() == null) {
			param.setCanUpdate(Constant.Number.ONE);
		}
		if (param.getCanForbidden() == null) {
			param.setCanForbidden(Constant.Number.ONE);
		}
		if (param.getCanEnable() == null) {
			param.setCanEnable(Constant.Number.ONE);
		}
		settingModelMapper.insectSettingModel(param);
		//内容数据
		for (SettingModelContentVo content : param.getSettingModelContentVoList()) {
			content.setId(StringUtil.genUniqueKey());
			if (content.getSort() == null) {
				content.setSort(Constant.Number.ZERO);
			}
			if (content.getIsShow() == null) {
				content.setIsShow(Constant.Number.ONE);
			}
			if (content.getCanUpdate() == null) {
				content.setCanUpdate(Constant.Number.ZERO);
			}
			if (content.getIsQuery() == null) {
				content.setIsQuery(Constant.Number.ZERO);
			}
		}
		result = settingModelContentMapper.insectBatch(param.getEnteId(), param.getId(), param.getSettingModelContentVoList());
		return result;
	}

	/**
	 * @Description 启用设置模块
	 * @Author 郑勇浩
	 * @Data 2020/2/12 10:55
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer enableSettingModel(SettingModelDto param) {
		param.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId());
		SettingModelVo settingModelVo = settingModelMapper.findSettingModel(param);
		if (settingModelVo == null) {
			return 0;
		}

		// 如果状态为已启用
		if (settingModelVo.getStatus().equals(Constant.Number.ONE)) {
			throw new ServiceException(ResultCode.SETTING_MODEL_IS_ENABLE);
		}

		//获取对应的表数据类型
		for (SettingModelContentVo contentVo : settingModelVo.getSettingModelContentVoList()) {
			setTableFormat(contentVo);
		}
		// 创建EXCEL模板
		String templateName = createExcelTemplate(settingModelVo.getModelTitle().trim(), settingModelVo.getSettingModelContentVoList());
		// 创建表
		settingModelMapper.createNewTable(settingModelVo.getTableName(), settingModelVo.getSettingModelContentVoList());
		param.setTemplateName(templateName);
		param.setStatus(Constant.Number.ONE);
		//更新当前状态为 启用
		return settingModelMapper.updateSettingModelStatus(param);
	}

	/**
	 * @Description 生成设置模块模板
	 * @Author 郑勇浩
	 * @Data 2020/2/26 13:32
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer createSettingModelTemplate(SettingModelDto param) {
		param.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId());
		SettingModelVo settingModelVo = settingModelMapper.findSettingModel(param);
		if (settingModelVo == null) {
			return 0;
		}
		//获取对应的表数据类型
		for (SettingModelContentVo contentVo : settingModelVo.getSettingModelContentVoList()) {
			setTableFormat(contentVo);
		}
		// 创建EXCEL模板
		String templateName = createExcelTemplate(settingModelVo.getModelTitle().trim(), settingModelVo.getSettingModelContentVoList());
		param.setTemplateName(templateName);
		param.setStatus(Constant.Number.ONE);
		//更新当前状态为 启用
		return settingModelMapper.updateSettingModelStatus(param);
	}

	/**
	 * @Description 修改设置模块数据
	 * @Author 郑勇浩
	 * @Data 2020/2/17 17:18
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingModelData(Map<String, String> param) {
		//获取设置模块ID 对应的数据
		SettingModelDto settingModelParam = new SettingModelDto();
		settingModelParam.setModelName(param.get("modelName"));
		settingModelParam.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId());
		SettingModelVo settingModelVo = settingModelMapper.findSettingModel(settingModelParam);
		if (settingModelVo == null) {
			return null;
		}
		//未启用
		if (settingModelVo.getStatus().equals(Constant.Number.ZERO)) {
			throw new ServiceException(ResultCode.SETTING_MODEL_IS_DISABLE);
		}
		//判断值是否可以修改
		Map<String, String> changeMap = new HashMap<>();
		for (SettingModelContentVo contentVo : settingModelVo.getSettingModelContentVoList()) {
			//没有对应列名则跳过
			if (param.get(contentVo.getColName()) == null) {
				continue;
			}
			//如果列不允许修改则跳过
			if (contentVo.getCanUpdate() == null || contentVo.getCanUpdate().equals(Constant.Number.ZERO)) {
				continue;
			}

			//日期格式
			if (contentVo.getFormat() == 2 || contentVo.getFormat() == 4) {
				param.put(contentVo.getColName(), param.get(contentVo.getColName()).substring(0, 10));
			} else if (contentVo.getFormat() == 3) {
				param.put(contentVo.getColName(), param.get(contentVo.getColName()).replace("T", " ").replace("Z", ""));
			}
			changeMap.put(contentVo.getTableColName(), param.get(contentVo.getColName()));

		}
		if (changeMap.size() == 0) {
			return 0;
		}
		return settingModelMapper.updateSettingModelData(settingModelVo.getTableName(), changeMap, param.get("id"));
	}

	/**
	 * @Description 修改设置模块数据状态
	 * @Author 郑勇浩
	 * @Data 2020/2/17 16:55
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer updateSettingModelDataStatus(SettingModelContentDto param) {
		//获取设置模块ID 对应的数据
		SettingModelDto settingModelParam = new SettingModelDto();
		settingModelParam.setModelName(param.getModelName());
		settingModelParam.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId());
		SettingModelVo settingModelVo = settingModelMapper.findSettingModel(settingModelParam);
		if (settingModelVo == null) {
			return null;
		}
		//未启用
		if (settingModelVo.getStatus().equals(Constant.Number.ZERO)) {
			throw new ServiceException(ResultCode.SETTING_MODEL_IS_DISABLE);
		}
		//更新
		Integer result = settingModelMapper.updateSettingModelDataStatus(settingModelVo.getTableName(), param.getStatus(), param.getId());
		if (result.equals(Constant.Number.ZERO)) {
			throw new ServiceException(ResultCode.SETTING_MODEL_DATA_UPDATE_ERROR);
		}
		return result;
	}

	/**
	 * @Description Excel文件上传校验
	 * @Author 郑勇浩
	 * @Data 2020/2/16 13:47
	 * @Param [file, sett]
	 * @return com.njwd.entity.basedata.excel.ExcelResult
	 */
	@Override
	public ExcelResult uploadAndCheckExcel(MultipartFile file, String settingModelName) {
		ExcelResult result = new ExcelResult();
		ExcelData excelData = new ExcelData();

		ExcelTemplate excelTemplate = new ExcelTemplate();
		excelTemplate.setName(settingModelName);
		excelData.setExcelTemplate(excelTemplate);
		excelData.setFileName(file.getOriginalFilename());
		ExcelRead excelRead = new SampleExcelRead();
		try {
			excelRead.read(file.getInputStream(), excelData);
		} catch (Exception e) {
			result.setMessage(ResultCode.EXCEL_NOT_CORRECT.message);
			result.setIsOk(Constant.Is.NO);
			return result;
		}
		//缓存解析结果
		String uuid = UUID.randomUUID().toString();
		RedisUtils.set(excelCacheKey(uuid), excelData, 10, TimeUnit.MINUTES);
		result.setUuid(uuid);
		if (!excelData.getExcelErrorList().isEmpty()) {
			result.setIsOk(Constant.Is.NO);
			result.setMessage(String.format("导入发生错误，其中可导入成功%d条，导入失败%d条", excelData.getExcelRowDataList().size(), excelData.getExcelErrorList().size()));
		} else {
			result.setIsOk(Constant.Is.YES);
		}
		return result;
	}

	/**
	 * @Description 导入EXCEL
	 * @Author 郑勇浩
	 * @Data 2020/2/14 17:29
	 * @Param [uuId]
	 * @return com.njwd.entity.basedata.excel.ExcelResult
	 */
	@Override
	public Integer importExcel(String uuid) {
		// 获取需要导入的数据
		ExcelData excelData = (ExcelData) RedisUtils.getObj(excelCacheKey(uuid));
		if (excelData == null || excelData.getExcelRowDataList().size() == 0) {
			return 1;
		}

		Integer result = 0;
		// 批量新增数据
		if (excelData.getExcelTemplate().getType().equals(modeNames[0])) {
			result = settingBaseShopService.insertExcelBatch(excelData);
		} else if (excelData.getExcelTemplate().getType().equals(modeNames[1])) {
//			result = settingDailyService.insertExcelBatch(excelData);
		} else if (excelData.getExcelTemplate().getType().equals(modeNames[2])) {
			result = settingBackService.insertExcelBatch(excelData);
		} else if (excelData.getExcelTemplate().getType().equals(modeNames[3])) {
			result = settingProfitService.insertExcelBatch(excelData);
		} else if (excelData.getExcelTemplate().getType().equals(modeNames[4])) {
			result = settingEvaluateService.insertExcelBatch(excelData);
		} else if (excelData.getExcelTemplate().getType().equals(modeNames[5])) {
			result = settingYoyService.insertExcelBatch(excelData);
		} else if (excelData.getExcelTemplate().getType().equals(modeNames[6])) {
			result = settingChangeService.insertExcelBatch(excelData);
		} else if (excelData.getExcelTemplate().getType().equals(modeNames[7])) {
			result = settingEntryFreeService.insertExcelBatch(excelData);
		} else if (excelData.getExcelTemplate().getType().equals(modeNames[8])) {
			result = finReportConfigService.insertExcelBatch(excelData);
		}
		return result;
	}

	/**
	 * @Description Excel模板下载
	 * @Author 郑勇浩
	 * @Data 2020/2/16 14:00
	 * @Param [settingModelName]
	 * @return org.springframework.http.ResponseEntity<byte [ ]>
	 */
	@Override
	public void downloadExcelTemplate(HttpServletResponse response, String settingModelName) {
		String templateName = null;

		if (settingModelName.equals(modeNames[0])) {
			templateName = "storeInfo_index.xlsx";
		} else if (settingModelName.equals(modeNames[1])) {
			templateName = "daily_index.xlsx";
		} else if (settingModelName.equals(modeNames[2])) {
			templateName = "back_index.xlsx";
		} else if (settingModelName.equals(modeNames[3])) {
			templateName = "profit_index.xlsx";
		} else if (settingModelName.equals(modeNames[4])) {
			templateName = "evaluate_index.xlsx";
		} else if (settingModelName.equals(modeNames[5])) {
			templateName = "value_index.xlsx";
		} else if (settingModelName.equals(modeNames[6])) {
			templateName = "wage_index.xlsx";
		} else if (settingModelName.equals(modeNames[7])) {
			templateName = "beer_index.xlsx";
		} else if (settingModelName.equals(modeNames[8])) {
			templateName = "finReportConfig_index.xlsx";
		}

		try {
			File file = new File(excelRootPath + templateName);
			InputStream inputStream = new FileInputStream(file);
			//强制下载不打开
			response.setContentType("application/force-download");
			OutputStream out = response.getOutputStream();
			//使用URLEncoder来防止文件名乱码或者读取错误
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(templateName, "UTF-8"));
			int b = 0;
			byte[] buffer = new byte[1000000];
			while (b != -1) {
				b = inputStream.read(buffer);
				if (b != -1) {
					out.write(buffer, 0, b);
				}
			}
			inputStream.close();
			out.close();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description Excel文件上传
	 * @Author 郑勇浩
	 * @Data 2020/2/19 16:32
	 * @Param [file, settingModelName]
	 * @return java.lang.Object
	 */
	@Override
	public String uploadExcel(MultipartFile file, String settingModelName) {
		//校验excel类型
		if (!Objects.requireNonNull(file.getOriginalFilename()).matches(".+\\.(xls|xlsx)")) {
			throw new ServiceException(ResultCode.FILE_MUST_IS_EXCEL);
		}
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.substring(uuid.length() - 12);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String fileName;
		try {
			String sourceFileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
			String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			fileName = String.format("%s%s-%s%s%s", settingModelName, sdf.format(new Date()), uuid, sourceFileName, suffix);
			File dest = new File(new File(excelRootPath), fileName);
			if (!dest.getParentFile().exists()) {
				dest.getParentFile().mkdirs();
			}
			file.transferTo(dest);
			//文件读取
			ExcelData excelData = new ExcelData();
			ExcelRead excelRead = new SampleExcelRead();
			excelRead.read(new FileInputStream(dest), excelData);
			RedisUtils.set(excelCacheKey(fileName), excelData, 10, TimeUnit.MINUTES);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), ResultCode.UPLOAD_EXCEPTION);
		}
		return fileName;
	}

	/**
	 * @Description Excel模板校验
	 * @Author 郑勇浩
	 * @Data 2020/2/19 16:41
	 * @Param [templateName, modelName]
	 * @return java.lang.Object
	 */
	@Override
	public ExcelResult checkExcel(String templateName, String modelName) {
		ExcelResult result = new ExcelResult();
		ExcelData excelData = (ExcelData) RedisUtils.getObj(excelCacheKey(templateName));
		if (excelData == null) {
			result.setMessage(ResultCode.EXCEL_DATA_NOT_EXISTS.message);
			result.setIsOk(Constant.Is.NO);
			return result;
		}
		if (excelData.getExcelRowDataList().size() > 0) {
			if (excelData.getExcelRowDataList().get(0).getExcelCellDataList().size() == 0) {
				result.setMessage(ResultCode.EXCEL_NOT_CORRECT.message);
				result.setIsOk(Constant.Is.NO);
				return result;
			}
		}

		if (excelData.getExcelRowDataList() == null) {
			excelData.setExcelRowDataList(new ArrayList<>());
		}
		if (excelData.getExcelErrorList() == null) {
			excelData.setExcelErrorList(new ArrayList<>());
		}

		//缓存解析结果
		ExcelTemplate excelTemplate = new ExcelTemplate();

		if (modelName.equals(modeNames[0])) {
			excelTemplate.setType(modeNames[0]);
			settingBaseShopService.checkData(excelData);
			this.checkShopBrandRegion(excelData);
		} else if (modelName.equals(modeNames[1])) {
			excelTemplate.setType(modeNames[1]);
//			settingDailyService.checkData(excelData);
			this.checkShopBrandRegion(excelData);
		} else if (modelName.equals(modeNames[2])) {
			excelTemplate.setType(modeNames[2]);
			settingBackService.checkData(excelData);
			this.checkShopBrandRegion(excelData);
		} else if (modelName.equals(modeNames[3])) {
			excelTemplate.setType(modeNames[3]);
			settingProfitService.checkData(excelData);
			this.checkShopBrandRegion(excelData);
		} else if (modelName.equals(modeNames[4])) {
			excelTemplate.setType(modeNames[4]);
			settingEvaluateService.checkData(excelData);
		} else if (modelName.equals(modeNames[5])) {
			excelTemplate.setType(modeNames[5]);
			settingYoyService.checkData(excelData);
		} else if (modelName.equals(modeNames[6])) {
			excelTemplate.setType(modeNames[6]);
			settingChangeService.checkData(excelData);
		} else if (modelName.equals(modeNames[7])) {
			excelTemplate.setType(modeNames[7]);
			settingEntryFreeService.checkData(excelData);
			this.checkShopBrandRegion(excelData);
		} else if (modelName.equals(modeNames[8])) {
			excelTemplate.setType(modeNames[8]);
			finReportConfigService.checkData(excelData);
		}

		excelData.setExcelTemplate(excelTemplate);
		String uuid = UUID.randomUUID().toString();
		RedisUtils.set(excelCacheKey(uuid), excelData, 10, TimeUnit.MINUTES);
		result.setUuid(uuid);
		if (excelData.getExcelErrorList() == null || excelData.getExcelErrorList().size() > 0) {
			result.setIsOk(Constant.Is.NO);
			result.setMessage(String.format("导入发生错误，其中可导入成功%d条，导入失败%d条", excelData.getExcelRowDataList().size(), excelData.getExcelErrorList().size()));
		} else {
			result.setIsOk(Constant.Is.YES);
		}
		return result;
	}

	/**
	 * @Description 设置格式对应的数据库格式类型
	 * @Author 郑勇浩
	 * @Data 2020/2/11 17:13
	 * @Param [content]
	 * @return void
	 */
	void setTableFormat(SettingModelContentVo content) {
		//默认值
		Integer maxLength = content.getMaxLength();
		//0 数字 1 字符 2 日期 3 时间
		if (content.getFormat().equals(Constant.Number.ZERO)) {
			// 小数 短整数 整数 长整数
			if (content.getDecimal() != null && content.getDecimal() > 0) {
				Integer decimal = content.getDecimal();
				content.setTableFormat(ReportDataConstant.TableFormat.DECIMAL + "(" + (maxLength + 1) + "," + decimal + ")");
			} else if (content.getMaxLength() < 5) {
				content.setTableFormat(ReportDataConstant.TableFormat.SMALLINT);
			} else if (content.getMaxLength() < 10) {
				content.setTableFormat(ReportDataConstant.TableFormat.INTEGER);
			} else {
				content.setTableFormat(ReportDataConstant.TableFormat.BIGINT);
			}
		} else if (content.getFormat().equals(Constant.Number.TWO) || content.getFormat().equals(Constant.Number.FOUR)) {
			//日期
			content.setTableFormat(ReportDataConstant.TableFormat.DATE);
		} else if (content.getFormat().equals(Constant.Number.THREE)) {
			//时间
			content.setTableFormat(ReportDataConstant.TableFormat.TIMESTAMP);
		} else {
			//字符
			content.setTableFormat(ReportDataConstant.TableFormat.CHARACTER + "(" + maxLength + ")");
		}
	}

	/**
	 * @Description 生成EXCEL模板
	 * @Author 郑勇浩
	 * @Data 2020/2/12 15:27
	 * @Param [templateName, contentList]
	 * @return void
	 */
	private String createExcelTemplate(String templateName, List<SettingModelContentVo> contentList) {
		List<String> title = new ArrayList<>();
		// 是否是应该导入的列
		for (SettingModelContentVo content : contentList) {
			if (content.getIsImport().equals(Constant.Number.ONE) || content.getIsImport().equals(Constant.Number.THREE)) {
				title.add(content.getColTitle());
			}
		}

		templateName = templateName + ExcelTypeEnum.XLSX.getValue();
		try {
			File dir = new File(excelRootPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ExcelUtil.writeSampleExcel(title, null, excelRootPath + templateName, null, ExcelTypeEnum.XLSX);
		} catch (IOException e) {
			throw new ServiceException(ResultCode.EXCEL_TEMPLATE_CREATE_ERROR);
		}
		return templateName;
	}

	/**
	 * @Description 检测品牌门店区域是否对应上
	 * @Author 郑勇浩
	 * @Data 2020/3/5 0:30
	 * @Param [excelData]
	 * @return void
	 */
	private void checkShopBrandRegion(ExcelData excelData) {
		if (excelData.getExcelRowDataList().size() == 0) {
			return;
		}
		//取出shopIdList
		List<String> shopIdList = new ArrayList<>();
		for (ExcelRowData rowData : excelData.getExcelRowDataList()) {
			if (!rowData.getExcelCellDataList().get(Constant.Number.TWO).getOldData().equals(Constant.Character.String_ZERO)) {
				shopIdList.add(rowData.getExcelCellDataList().get(Constant.Number.TWO).getOldData().toString());
			}
		}
		if (shopIdList.size() == 0) {
			return;
		}
		List<SettingBaseShopVo> shopList = settingModelMapper.findShopBrandRegionInfo(userController.getCurrLoginUserInfo().getRootEnterpriseId(), shopIdList);
//		List<SettingBaseShopVo> shopList = settingModelMapper.findShopBrandRegionInfo(999L, shopIdList);

		List<ExcelRowData> excelRowData = excelData.getExcelRowDataList();
		ExcelRowData excelRowDatum;
		//非空验证
		boolean isFind;
		for (int i = 0; i < excelRowData.size(); i++) {
			isFind = false;
			excelRowDatum = excelRowData.get(i);
			for (SettingBaseShopVo shopVo : shopList) {
				//如果品牌区域门店都相同则证明对的
				if (shopVo.getBrandId().equals(excelRowDatum.getExcelCellDataList().get(0).getOldData().toString())
						&& shopVo.getRegionId().equals(excelRowDatum.getExcelCellDataList().get(1).getOldData().toString())
						&& shopVo.getShopId().equals(excelRowDatum.getExcelCellDataList().get(2).getOldData().toString())) {
					isFind = true;
				}
			}
			if (!isFind) {
				excelData.getExcelErrorList().add(SampleExcelCheck.excelError(excelRowDatum, excelRowDatum.getExcelCellDataList().get(2), "该门店不属于对应品牌或区域，请检查是否输入错误"));
				excelData.getExcelRowDataList().remove(i);
				i--;
			}
		}
	}

	/**
	 * @Description Excel缓存KEY
	 * @Author 郑勇浩
	 * @Data 2020/2/14 17:16
	 * @Param [uuid]
	 * @return java.lang.String
	 */
	private String excelCacheKey(String uuid) {
		return String.format(Constant.ExcelConfig.EXCEL_KEY_PREFIX, uuid);
	}

}

package com.njwd.reportdata.service;

import com.njwd.entity.basedata.excel.ExcelResult;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingModelContent;
import com.njwd.entity.reportdata.dto.SettingModelContentDto;
import com.njwd.entity.reportdata.dto.SettingModelDto;
import com.njwd.entity.reportdata.vo.SettingModelContentVo;
import com.njwd.entity.reportdata.vo.SettingModelVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description 设置模块 Service
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
public interface SettingModelService {

	/**
	 * @Description 查询设置模块 ID
	 * @Author 郑勇浩
	 * @Data 2020/2/12 10:59
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingModelVo
	 */
	SettingModelVo findSettingModelById(String settingModelId);

	/**
	 * @Description 查询设置模块 NAME
	 * @Author 郑勇浩
	 * @Data 2020/2/16 13:42
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingModelVo
	 */
	SettingModelVo findSettingModelByName(String settingModelName);

	/**
	 * @Description 查询简单设置模块列表
	 * @Author 郑勇浩
	 * @Data 2020/2/6 14:28
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingModelVo>
	 */
	List<SettingModelVo> findSampleSettingModelList(SettingModelDto param);

	/**
	 * @Description 查询设置模块内容列表
	 * @Author 郑勇浩
	 * @Data 2020/2/7 15:06
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingModelContentVo>
	 */
	List<SettingModelContentVo> findSettingModelContentList(SettingModelContentDto param);

	/**
	 * @Description 查询设置模块数据
	 * @Author 郑勇浩
	 * @Data 2020/2/16 14:13
	 * @Param [param]
	 * @return java.util.HashMap<java.lang.String, java.lang.Object>
	 */
	List<Map<String, Object>> findSettingModelData(SettingModelDto param);

	/**
	 * @Description 查询重复的设置模块数据
	 * @Author 郑勇浩
	 * @Data 2020/2/20 16:31
	 * @Param [tableName, conditionList]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	List<Map<String, String>> findSettingModelData2(String tableName, Map<String, String> conditionMap);

	/**
	 * @Description 查询需要转化的列对应值
	 * @Author 郑勇浩
	 * @Data 2020/2/26 23:24
	 * @Param [enteId, tableMap]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	List<ConvertData> findConvertDataList(Long enteId, Map<Integer, SettingModelContent> tableMap, Map<Integer, String> nameMap);

	/**
	 * @Description 查询重复的列内容
	 * @Author 郑勇浩
	 * @Data 2020/2/27 11:18
	 * @Param [enteId, tableName, titleList, valueList]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	List<Map<String, String>> findDuplicateMap(Long enteId, String tableName, List<SettingModelContent> titleList, List<Map<String, String>> valueList);

	/**
	 * @Description 添加设置模块
	 * @Author 郑勇浩
	 * @Data 2020/2/6 15:07
	 * @Param [param]
	 * @return int
	 */
	Integer insectSettingModel(SettingModelDto param);

	/**
	 * @Description 启用设置模块
	 * @Author 郑勇浩
	 * @Data 2020/2/12 10:31
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer enableSettingModel(SettingModelDto param);

	/**
	 * @Description 生成设置模块模板
	 * @Author 郑勇浩
	 * @Data 2020/2/12 10:31
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer createSettingModelTemplate(SettingModelDto param);

	/**
	 * @Description 更新设置模块数据
	 * @Author 郑勇浩
	 * @Data 2020/2/17 17:17
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingModelData(Map<String, String> param);

	/**
	 * @Description 修改设置模块数据状态
	 * @Author 郑勇浩
	 * @Data 2020/2/17 16:54
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingModelDataStatus(SettingModelContentDto param);

	/**
	 * @Description Excel文件上传校验
	 * @Author 郑勇浩
	 * @Data 2020/2/16 13:47
	 * @Param [file, settingModelName]
	 * @return com.njwd.entity.basedata.excel.ExcelResult
	 */
	ExcelResult uploadAndCheckExcel(MultipartFile file, String settingModelName);

	/**
	 * @Description Excel文件导入
	 * @Author 郑勇浩
	 * @Data 2020/2/15 17:26
	 * @Param []
	 * @return com.njwd.entity.basedata.excel.ExcelResult
	 */
	Integer importExcel(String uuid);

	/**
	 * @Description Excel模板下载
	 * @Author 郑勇浩
	 * @Data 2020/2/16 13:58
	 * @Param [templateType]
	 * @return org.springframework.http.ResponseEntity<byte [ ]>
	 */
	void downloadExcelTemplate(HttpServletResponse response, String settingModelName) throws IOException;

	/**
	 * @Description Excel文件上传
	 * @Author 郑勇浩
	 * @Data 2020/2/19 16:32
	 * @Param [file, settingModelName]
	 * @return java.lang.Object
	 */
	String uploadExcel(MultipartFile file, String settingModelName);

	/**
	 * @Description Excel模板校验
	 * @Author 郑勇浩
	 * @Data 2020/2/19 16:41
	 * @Param [templateName, modelName]
	 * @return java.lang.Object
	 */
	ExcelResult checkExcel(String templateName, String modelName);

}

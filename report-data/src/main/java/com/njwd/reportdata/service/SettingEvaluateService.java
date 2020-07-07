package com.njwd.reportdata.service;

import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.SettingEvaluate;
import com.njwd.entity.reportdata.dto.SettingEvaluateDto;
import com.njwd.entity.reportdata.vo.SettingEvaluateVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 设置 评价汇总 Service
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
public interface SettingEvaluateService {

	/**
	 * @Description 评价汇总信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingEvaluateVo
	 */
	SettingEvaluateVo findSettingEvaluate(@Param("param") SettingEvaluateDto param);

	/**
	 * @Description 查询评价汇总列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	List<SettingEvaluateVo> findSettingEvaluateList(@Param("param") SettingEvaluateDto param);

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingEvaluate> dailyList);

	/**
	 * @Description 更新评价汇总
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingEvaluate(@Param("param") SettingEvaluateDto param);

	/**
	 * @Description 更新评价汇总状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingEvaluateStatus(@Param("param") SettingEvaluateDto param);

	/**
	 * @Description 导入数据监测
	 * @Author 郑勇浩
	 * @Data 2020/3/4 19:38
	 * @Param []
	 * @return void
	 */
	void checkData(ExcelData excelData);

	/**
	 * @Description 批量新增表格数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:14
	 * @Param [excelData]
	 * @return java.lang.Integer
	 */
	Integer insertExcelBatch(ExcelData excelData);

}

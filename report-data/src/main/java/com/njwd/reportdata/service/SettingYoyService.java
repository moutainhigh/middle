package com.njwd.reportdata.service;

import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.SettingYoy;
import com.njwd.entity.reportdata.dto.SettingYoyDto;
import com.njwd.entity.reportdata.vo.SettingYoyVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 设置 同比环比阀值 Service
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
public interface SettingYoyService {

	/**
	 * @Description 同比环比阀值信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingYoyVo
	 */
	SettingYoyVo findSettingYoy(@Param("param") SettingYoyDto param);

	/**
	 * @Description 查询同比环比阀值列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	List<SettingYoyVo> findSettingYoyList(@Param("param") SettingYoyDto param);

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingYoy> dailyList);

	/**
	 * @Description 更新同比环比阀值状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingYoyStatus(@Param("param") SettingYoyDto param);

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

package com.njwd.reportdata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.SettingProfit;
import com.njwd.entity.reportdata.dto.SettingProfitDto;
import com.njwd.entity.reportdata.vo.SettingProfitVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 设置 实时利润预算 Service
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
public interface SettingProfitService {

	/**
	 * @Description 经营实时利润预算
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingProfitVo
	 */
	SettingProfitVo findSettingProfit(@Param("param") SettingProfitDto param);

	/**
	 * @Description 查询实时利润预算列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	Page<SettingProfitVo> findSettingProfitList(@Param("param") SettingProfitDto param);

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingProfit> dailyList);

	/**
	 * @Description 更新实时利润预算
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingProfit(@Param("param") SettingProfitDto param);

	/**
	 * @Description 更新实时利润预算状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingProfitStatus(@Param("param") SettingProfitDto param);

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

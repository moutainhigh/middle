package com.njwd.reportdata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.SettingEntryFree;
import com.njwd.entity.reportdata.dto.SettingEntryFreeDto;
import com.njwd.entity.reportdata.vo.SettingEntryFreeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 设置 啤酒进场费 Service
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
public interface SettingEntryFreeService {

	/**
	 * @Description 啤酒进场费信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingEntryFreeVo
	 */
	SettingEntryFreeVo findSettingEntryFree(@Param("param") SettingEntryFreeDto param);

	/**
	 * @Description 查询啤酒进场费列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	Page<SettingEntryFreeVo> findSettingEntryFreeList(@Param("param") SettingEntryFreeDto param);

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingEntryFree> dailyList);

	/**
	 * @Description 更新啤酒进场费
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingEntryFree(@Param("param") SettingEntryFreeDto param);

	/**
	 * @Description 更新啤酒进场费状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingEntryFreeStatus(@Param("param") SettingEntryFreeDto param);

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

	/**
	 * @Description: 查询配置的啤酒进场费
	 * @Param: [settingEntryFreeDto]
	 * @return: java.util.List<com.njwd.entity.reportdata.vo.SettingEntryFreeVo>
	 * @Author: LuoY
	 * @Date: 2020/3/27 18:14
	 */
	List<SettingEntryFreeVo> findBearSettingInfo(SettingEntryFreeDto settingEntryFreeDto);

}

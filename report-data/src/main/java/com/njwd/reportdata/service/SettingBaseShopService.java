package com.njwd.reportdata.service;

import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.SettingBaseShop;
import com.njwd.entity.reportdata.dto.SettingBaseShopDto;
import com.njwd.entity.reportdata.vo.SettingBaseShopVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 设置 门店基础资料 Service
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
public interface SettingBaseShopService {


	/**
	 * @Description 门店基础资料信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingBaseShopVo
	 */
	SettingBaseShopVo findSettingBaseShop(@Param("param") SettingBaseShopDto param);

	/**
	 * @Description 查询门店基础资料列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	List<SettingBaseShopVo> findSettingBaseShopList(@Param("param") SettingBaseShopDto param);

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingBaseShop> dailyList);

	/**
	 * @Description 更新门店基础资料
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingBaseShop(@Param("param") SettingBaseShopDto param);

	/**
	 * @Description 更新门店基础资料状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingBaseShopStatus(@Param("param") SettingBaseShopDto param);

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

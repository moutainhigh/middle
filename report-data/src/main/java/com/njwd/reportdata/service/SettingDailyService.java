package com.njwd.reportdata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.SettingDaily;
import com.njwd.entity.reportdata.dto.SettingDailyDto;
import com.njwd.entity.reportdata.vo.SettingDailyVo;
import com.njwd.support.BatchResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 设置 经营日报 Service
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
public interface SettingDailyService {

	/**
	 * @Description 查询经营日报列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	List<SettingDailyVo> findDailyList(@Param("param") SettingDailyDto param);

	/**
	 * @Description 查询经营日报列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	Page<SettingDailyVo> findSettingDailyList(@Param("param") SettingDailyDto param);

	/**
	 * @Description 批量查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:33
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(String enteId, List<SettingDaily> dailyList);

	/**
	 * @Description 批量新增经营日报
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer insertSettingDailyBatch(@Param("param") SettingDailyDto param);

	/**
	 * @Description 更新经营日报
	 * @Author 郑勇浩
	 * @Data 2020/4/27 9:40
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingDaily(@Param("param") SettingDailyDto param);

	/**
	 * @Description 批量更新经营日报
	 * @Author 郑勇浩
	 * @Data 2020/4/27 9:40
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	BatchResult updateSettingDailyBatch(@Param("param") SettingDailyDto param);

	/**
	 * @Description 更新经营日报状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingDailyStatus(@Param("param") SettingDailyDto param);

	/**
	 * @Description 更新经营日报状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:37
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	BatchResult updateSettingDailyStatusBatch(@Param("param") SettingDailyDto param);

}

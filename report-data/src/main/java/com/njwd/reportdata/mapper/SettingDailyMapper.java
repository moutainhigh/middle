package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.SettingDaily;
import com.njwd.entity.reportdata.dto.SettingDailyDto;
import com.njwd.entity.reportdata.vo.SettingDailyVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author 设置模块 - 经营日报
 * @Date 2019/11/27 14:31
 * @Description
 */
@Repository
public interface SettingDailyMapper extends BaseMapper<SettingDaily> {

	/**
	 * @Description 经营日报信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingDailyVo
	 */
	SettingDailyVo findSettingDaily(@Param("param") SettingDailyDto param);

	/**
	 * @Description 经营日报信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	Page<SettingDailyVo> findSettingDailyList(Page<SettingDailyVo> page, @Param("param") SettingDailyDto param);

	/**
	 * @Description 经营日报信息状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	SettingDailyVo findSettingDailyStatus(@Param("param") SettingDailyDto param);

	/**
	 * @Description 经营日报信息列表状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	List<SettingDailyVo> findSettingDailyListStatus(@Param("param") SettingDailyDto param);

	/**
	 * @Description 查询重复数据数量
	 * @Author 郑勇浩
	 * @Data 2020/4/1 9:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer findDuplicateDataCount(@Param("param") SettingDailyDto param);

	/**
	 * @Description 查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(@Param("enteId") String enteId, @Param("valueList") List<SettingDaily> dailyList);

	/**
	 * @Description 更新经营日报
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingDaily(@Param("param") SettingDailyDto param);

	/**
	 * @Description 更新经营日报
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingDailyBatch(@Param("param") SettingDailyDto param);

	/**
	 * @Description 更新经营日报状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingDailyStatus(@Param("param") SettingDailyDto param);

	/**
	 * @Description 批量更新经营日报状态
	 * @Author 郑勇浩
	 * @Data 2020/4/27 11:06
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingDailyStatusBatch(@Param("param") SettingDailyDto param);

	/**
	 * @Description 批量新增
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:22
	 * @Param []
	 * @return java.lang.Integer
	 */
	Integer insertDataBatch(@Param("dataList") List<SettingDailyVo> dataList);

	/**
	 * @Description 批量删除符合条件的数据
	 * @Author 郑勇浩
	 * @Data 2020/4/27 11:42
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer deleteSettingDailyList(@Param("param") SettingDailyDto param);

}

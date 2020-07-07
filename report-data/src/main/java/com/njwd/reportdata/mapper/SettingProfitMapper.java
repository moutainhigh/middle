package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingProfit;
import com.njwd.entity.reportdata.dto.SettingProfitDto;
import com.njwd.entity.reportdata.vo.SettingProfitVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author 设置模块 - 实时利润预算
 * @Date 2019/11/27 14:31
 * @Description
 */
@Repository
public interface SettingProfitMapper extends BaseMapper<SettingProfit> {

	/**
	 * @Description 实时利润预算信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingProfitVo
	 */
	SettingProfitVo findSettingProfit(@Param("param") SettingProfitDto param);

	/**
	 * @Description 实时利润预算信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	Page<SettingProfitVo> findSettingProfitList(Page<SettingProfitVo> page, @Param("param") SettingProfitDto param);

	/**
	 * @Description 获取对应的转化值
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:03
	 * @Param [enteId]
	 * @return java.util.List<com.njwd.entity.reportdata.ConvertData>
	 */
	List<ConvertData> findConvertDataList(@Param("enteId") String enteId);

	/**
	 * @Description 查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/4/22 17:32
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingProfitVo>
	 */
	List<SettingProfitVo> findDuplicateData(@Param("param") SettingProfitDto param);

	/**
	 * @Description 查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(@Param("enteId") String enteId, @Param("valueList") List<SettingProfit> dailyList);

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
	 * @Data 2020/3/4 17:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingProfitStatus(@Param("param") SettingProfitDto param);

	/**
	 * @Description 批量新增
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:22
	 * @Param []
	 * @return java.lang.Integer
	 */
	Integer insertDataBatch(@Param("dataList") List<SettingProfitVo> dataList);

}

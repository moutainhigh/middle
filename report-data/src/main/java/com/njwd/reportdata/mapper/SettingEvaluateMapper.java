package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingEvaluate;
import com.njwd.entity.reportdata.dto.SettingEvaluateDto;
import com.njwd.entity.reportdata.vo.SettingEvaluateVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author 设置模块 - 评价汇总阀值预算
 * @Date 2019/11/27 14:31
 * @Description
 */
@Repository
public interface SettingEvaluateMapper extends BaseMapper<SettingEvaluate> {

	/**
	 * @Description 评价汇总阀值预算信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingEvaluateVo
	 */
	SettingEvaluateVo findSettingEvaluate(@Param("param") SettingEvaluateDto param);

	/**
	 * @Description 评价汇总阀值预算信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	List<SettingEvaluateVo> findSettingEvaluateList(@Param("param") SettingEvaluateDto param);

	/**
	 * @Description 查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(@Param("enteId") String enteId, @Param("valueList") List<SettingEvaluate> valueList);

	/**
	 * @Description 更新评价汇总阀值预算
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingEvaluate(@Param("param") SettingEvaluateDto param);

	/**
	 * @Description 更新评价汇总阀值预算状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingEvaluateStatus(@Param("param") SettingEvaluateDto param);

	/**
	 * @Description 批量新增
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:22
	 * @Param []
	 * @return java.lang.Integer
	 */
	Integer insertDataBatch(@Param("dataList") List<SettingEvaluateVo> dataList);

}

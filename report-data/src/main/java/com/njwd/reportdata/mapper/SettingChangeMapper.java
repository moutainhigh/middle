package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingChange;
import com.njwd.entity.reportdata.dto.SettingChangeDto;
import com.njwd.entity.reportdata.vo.SettingChangeVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author 设置模块 - 异动工资
 * @Date 2019/11/27 14:31
 * @Description
 */
@Repository
public interface SettingChangeMapper extends BaseMapper<SettingChange> {

	/**
	 * @Description 异动工资信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingChangeVo
	 */
	SettingChangeVo findSettingChange(@Param("param") SettingChangeDto param);

	/**
	 * @Description 异动工资信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	Page<SettingChangeVo> findSettingChangeList(Page<SettingChangeVo> page, @Param("param") SettingChangeDto param);

	/**
	 * @Description 获取对应的转化值
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:03
	 * @Param [enteId]
	 * @return java.util.List<com.njwd.entity.reportdata.ConvertData>
	 */
	List<ConvertData> findConvertDataList(@Param("enteId") String enteId);

	/**
	 * @Description 查询重复数据数量
	 * @Author 郑勇浩
	 * @Data 2020/4/1 9:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer findDuplicateDataCount(@Param("param") SettingChangeDto param);

	/**
	 * @Description 查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(@Param("enteId") String enteId, @Param("valueList") List<SettingChange> dailyList);

	/**
	 * @Description 更新异动工资
	 * @Author 郑勇浩
	 * @Data 2020/4/1 9:28
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingChange(@Param("param") SettingChangeDto param);

	/**
	 * @Description 更新异动工资状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingChangeStatus(@Param("param") SettingChangeDto param);

	/**
	 * @Description 批量新增
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:22
	 * @Param []
	 * @return java.lang.Integer
	 */
	Integer insertDataBatch(@Param("dataList") List<SettingChangeVo> dataList);

}

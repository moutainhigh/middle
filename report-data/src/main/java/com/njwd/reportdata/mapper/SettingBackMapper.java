package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingBack;
import com.njwd.entity.reportdata.dto.SettingBackDto;
import com.njwd.entity.reportdata.vo.SettingBackVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author 设置模块 - 退赠优免安全阀值
 * @Date 2019/11/27 14:31
 * @Description
 */
@Repository
public interface SettingBackMapper extends BaseMapper<SettingBack> {

	/**
	 * @Description 退赠优免安全阀值信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingBackVo
	 */
	SettingBackVo findSettingBack(@Param("param") SettingBackDto param);

	/**
	 * @Description 退赠优免安全阀值信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	Page<SettingBackVo> findSettingBackList(Page<SettingBackVo> page, @Param("param") SettingBackDto param);

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
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(@Param("enteId") Long enteId, @Param("valueList") List<SettingBack> dailyList);

	/**
	 * @Description 更新退赠优免安全阀值
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingBack(@Param("param") SettingBackDto param);

	/**
	 * @Description 更新退赠优免安全阀值状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingBackStatus(@Param("param") SettingBackDto param);

	/**
	 * @Description 批量新增
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:22
	 * @Param []
	 * @return java.lang.Integer
	 */
	Integer insertDataBatch(@Param("dataList") List<SettingBackVo> dataList);

}

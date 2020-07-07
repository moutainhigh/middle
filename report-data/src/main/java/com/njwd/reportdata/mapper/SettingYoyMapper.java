package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.SettingYoy;
import com.njwd.entity.reportdata.dto.SettingYoyDto;
import com.njwd.entity.reportdata.vo.SettingYoyVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author 设置模块 - 同比环比阀值
 * @Date 2019/11/27 14:31
 * @Description
 */
@Repository
public interface SettingYoyMapper extends BaseMapper<SettingYoy> {

	/**
	 * @Description 同比环比阀值信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingYoyVo
	 */
	SettingYoyVo findSettingYoy(@Param("param") SettingYoyDto param);

	/**
	 * @Description 同比环比阀值信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	List<SettingYoyVo> findSettingYoyList(@Param("param") SettingYoyDto param);

	/**
	 * @Description 查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(@Param("enteId") String enteId, @Param("valueList") List<SettingYoy> valueList);

	/**
	 * @Description 更新同比环比阀值状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingYoyStatus(@Param("param") SettingYoyDto param);

	/**
	 * @Description 批量新增
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:22
	 * @Param []
	 * @return java.lang.Integer
	 */
	Integer insertDataBatch(@Param("dataList") List<SettingYoyVo> dataList);

}

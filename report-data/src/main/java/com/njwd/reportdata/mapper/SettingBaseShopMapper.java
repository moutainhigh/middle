package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingBaseShop;
import com.njwd.entity.reportdata.dto.SettingBaseShopDto;
import com.njwd.entity.reportdata.vo.SettingBaseShopVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author 设置模块 - 门店基础信息
 * @Date 2019/11/27 14:31
 * @Description
 */
@Repository
public interface SettingBaseShopMapper extends BaseMapper<SettingBaseShop> {

	/**
	 * @Description 经营门店基础信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingBaseShopVo
	 */
	SettingBaseShopVo findSettingBaseShop(@Param("param") SettingBaseShopDto param);

	/**
	 * @Description 经营门店基础信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	List<SettingBaseShopVo> findSettingBaseShopList(@Param("param") SettingBaseShopDto param);

	/**
	 * @Description 获取对应的转化值
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:03
	 * @Param [enteId]
	 * @return java.util.List<com.njwd.entity.reportdata.ConvertData>
	 */
	List<ConvertData> findConvertDataList(@Param("enteId") Long enteId);

	/**
	 * @Description 查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, dailyList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(@Param("enteId") String enteId, @Param("valueList") List<SettingBaseShop> dailyList);

	/**
	 * @Description 更新经营日报
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:53
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingBaseShop(@Param("param") SettingBaseShopDto param);

	/**
	 * @Description 更新经营日报状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingBaseShopStatus(@Param("param") SettingBaseShopDto param);

	/**
	 * @Description 批量新增
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:22
	 * @Param []
	 * @return java.lang.Integer
	 */
	Integer insertDataBatch(@Param("dataList") List<SettingBaseShopVo> dataList);
}

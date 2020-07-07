package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingEntryFree;
import com.njwd.entity.reportdata.dto.SettingEntryFreeDto;
import com.njwd.entity.reportdata.vo.SettingEntryFreeVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author 设置模块 - 啤酒入场费
 * @Date 2019/11/27 14:31
 * @Description
 */
@Repository
public interface SettingEntryFreeMapper extends BaseMapper<SettingEntryFree> {

	/**
	 * @Description 啤酒入场费信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:57
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingEntryFreeVo
	 */
	SettingEntryFreeVo findSettingEntryFree(@Param("param") SettingEntryFreeDto param);

	/**
	 * @Description 啤酒入场费信息列表
	 * @Author 郑勇浩
	 * @Data 2020/3/4 15:46
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	Page<SettingEntryFreeVo> findSettingEntryFreeList(Page<SettingEntryFreeVo> page, @Param("param") SettingEntryFreeDto param);

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
	List<SettingEntryFreeVo> findDuplicateData(@Param("param") SettingEntryFreeDto param);

	/**
	 * @Description 查询重复数据
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, settingEntryList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findDuplicateDataList(@Param("enteId") String enteId, @Param("valueList") List<SettingEntryFree> settingEntryList);

	/**
	 * @Description 查询是否存在供应商信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, settingEntryList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findSupplierInfo(@Param("enteId") String enteId, @Param("valueList") List<SettingEntryFreeVo> settingEntryList);

	/**
	 * @Description 查询是否存在物料信息
	 * @Author 郑勇浩
	 * @Data 2020/3/4 22:31
	 * @Param [enteId, settingEntryList]
	 * @return java.util.HashMap<java.lang.String, java.lang.String>
	 */
	List<Map<String, String>> findMaterialInfo(@Param("enteId") String enteId, @Param("valueList") List<SettingEntryFreeVo> settingEntryList);

	/**
	 * @Description 更新啤酒入场费
	 * @Author 郑勇浩
	 * @Data 2020/4/22 17:21
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingEntryFree(@Param("param") SettingEntryFreeDto param);

	/**
	 * @Description 更新啤酒入场费状态
	 * @Author 郑勇浩
	 * @Data 2020/3/4 17:36
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingEntryFreeStatus(@Param("param") SettingEntryFreeDto param);

	/**
	 * @Description 批量新增
	 * @Author 郑勇浩
	 * @Data 2020/3/4 23:22
	 * @Param []
	 * @return java.lang.Integer
	 */
	Integer insertDataBatch(@Param("dataList") List<SettingEntryFreeVo> dataList);

	/**
	 * @Description: 查询啤酒进场费配置
	 * @Param: [settingEntryFreeDto]
	 * @return: java.util.List<com.njwd.entity.reportdata.vo.SettingEntryFreeVo>
	 * @Author: LuoY
	 * @Date: 2020/3/27 18:15
	 */
	List<SettingEntryFreeVo> findBearSettingInfo(SettingEntryFreeDto settingEntryFreeDto);

}

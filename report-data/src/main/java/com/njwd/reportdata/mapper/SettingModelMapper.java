package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.entity.reportdata.SettingModel;
import com.njwd.entity.reportdata.SettingModelContent;
import com.njwd.entity.reportdata.dto.SettingModelDto;
import com.njwd.entity.reportdata.vo.SettingBaseShopVo;
import com.njwd.entity.reportdata.vo.SettingModelContentVo;
import com.njwd.entity.reportdata.vo.SettingModelVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 设置模块 mapper.
 *
 * @Description: SettingController
 * @Author: ZhengYongHao
 * @Date: 2020-02-05 10:37
 */
@Repository
public interface SettingModelMapper extends BaseMapper<SettingModel> {

	/**
	 * @Description 查询设置模块
	 * @Author 郑勇浩
	 * @Data 2020/2/12 11:01
	 * @Param [param]
	 * @return com.njwd.entity.reportdata.vo.SettingModelVo
	 */
	SettingModelVo findSettingModel(@Param("param") SettingModelDto param);

	/**
	 * @Description 查询简单设置模块列表
	 * @Author 郑勇浩
	 * @Data 2020/2/6 14:26
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingModelVo>
	 */
	List<SettingModelVo> findSampleSettingModelList(@Param("param") SettingModelDto param);

	/**
	 * @Description 查询设置模块对应数据
	 * @Author 郑勇浩
	 * @Data 2020/2/27 13:52
	 * @Param [param]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	List<Map<String, Object>> findSettingModelData(@Param("param") SettingModelVo param,
												   @Param("query") Object query,
												   @Param("queryType") Integer queryType);

	/**
	 * @Description 查询设置模块对应数据(查重用)
	 * @Author 郑勇浩
	 * @Data 2020/2/16 15:25
	 * @Param []
	 * @return java.util.HashMap<java.lang.String, java.lang.Object>
	 */
	List<Map<String, String>> findSettingModelData2(@Param("tableName") String tableName,
													@Param("conditionMap") Map<String, String> conditionMap);

	/**
	 * @Description 查询需要转化的列
	 * @Author 郑勇浩
	 * @Data 2020/2/26 23:22
	 * @Param [enteId, tableMap]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	List<ConvertData> findConvertDataList(@Param("enteId") Long enteId,
										  @Param("tableMap") Map<Integer, SettingModelContent> tableMap,
										  @Param("nameMap") Map<Integer, String> nameMap);

	/**
	 * @Description 查询重复
	 * @Author 郑勇浩
	 * @Data 2020/2/27 11:11
	 * @Param [enteId, enteId, tableMap, nameMap]
	 * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
	 */
	List<Map<String, String>> findDuplicateData(@Param("enteId") Long enteId,
												@Param("tableName") String tableName,
												@Param("titleList") List<SettingModelContent> titleList,
												@Param("valueList") List<Map<String, String>> valueList);


	/**
	 * @Description 查询设置模块的表是否存在
	 * @Author 郑勇浩
	 * @Data 2020/2/11 11:01
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer findSettingModelTable(@Param("param") SettingModelDto param);


	/**
	 * @Description 查询门店对应的品牌和区域
	 * @Author 郑勇浩
	 * @Data 2020/3/6 11:42
	 * @Param [enteId, shopIdList]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingBaseShopVo>
	 */
	List<SettingBaseShopVo> findShopBrandRegionInfo(@Param("enteId") Long enteId, @Param("shopIdList") List<String> shopIdList);

	/**
	 * @Description 新增简单设置模块
	 * @Author 郑勇浩
	 * @Data 2020/2/6 15:07
	 * @Param [param]
	 * @return int
	 */
	Integer insectSettingModel(@Param("param") SettingModelDto param);

	/**
	 * @Description 批量新增模块内容数据
	 * @Author 郑勇浩
	 * @Data 2020/2/14 17:40
	 * @Param [tableName, titleList, rowList]
	 * @return java.lang.Integer
	 */
	Integer insectSettingModelDataBatch(@Param("tableName") String tableName, @Param("titleList") List<String> titleList, @Param("rowList") List<List<Object>> rowList);

	/**
	 * @Description 更新设置模块状态信息
	 * @Author 郑勇浩
	 * @Data 2020/2/12 15:57
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateSettingModelStatus(@Param("param") SettingModelDto param);

	/**
	 * @Description 创建表
	 * @Author 郑勇浩
	 * @Data 2020/2/12 10:55
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer createNewTable(@Param("tableName") String tableName, @Param("contentList") List<SettingModelContentVo> contentList);

	/**
	 * @Description 修改数据
	 * @Author 郑勇浩
	 * @Data 2020/2/17 17:23
	 * @Param [tableName, changeMap, id]
	 * @return java.lang.Integer
	 */
	Integer updateSettingModelData(@Param("tableName") String tableName, @Param("changeMap") Map<String, String> changeMap, @Param("id") String id);

	/**
	 * @Description 修改数据状态
	 * @Author 郑勇浩
	 * @Data 2020/2/17 16:47
	 * @Param [tableName, status, id]
	 * @return java.lang.Integer
	 */
	Integer updateSettingModelDataStatus(@Param("tableName") String tableName, @Param("status") Integer status, @Param("id") String id);
}

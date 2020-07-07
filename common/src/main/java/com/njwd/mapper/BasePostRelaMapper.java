package com.njwd.mapper;

import com.njwd.entity.kettlejob.dto.BasePostRelaDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 乐才 HR 薪资数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface BasePostRelaMapper {

	/**
	 * @Description 批量新增或更新数据
	 * @Author 郑勇浩
	 * @Data 2020/2/22 16:22
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer insertOrUpdateBatch(@Param("sqlParam") BasePostRelaDto sqlParam);

	/**
	 * @Description 更新部门对应的组织信息
	 * @Author 郑勇浩
	 * @Data 2020/3/26 10:59
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updatePostOrgBacth(@Param("enteId") String enteId, @Param("appId") String appId);

}

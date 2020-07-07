package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.JoyHrOrgChangeDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 乐才 HR 调动数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface JoyHrOrgChangeMapper {

	/**
	 * @Description 最后更新时间
	 * @Author 郑勇浩
	 * @Data 2020/4/9 16:05
	 * @Param [enteId, appId]
	 * @return java.util.Date
	 */
	String findLastUpdateTime(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 批量新增或更新数据
	 * @Author 郑勇浩
	 * @Data 2020/2/22 16:22
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer insertOrUpdateBatch(@Param("sqlParam") JoyHrOrgChangeDto sqlParam);

	/**
	 * @Description 批量更新调动老组织信息
	 * @Author 郑勇浩
	 * @Data 2020/3/26 10:59
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateOrgChangeOld(@Param("sqlParam") JoyHrOrgChangeDto sqlParam);

	/**
	 * @Description 批量更新调动老组织信息
	 * @Author 郑勇浩
	 * @Data 2020/4/17 11:44
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer updateOrgChangeNew(@Param("sqlParam") JoyHrOrgChangeDto sqlParam);

	/**
	 * @Description 批量更新调动用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/20 14:50
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer updateOrgChangeUser(@Param("sqlParam") JoyHrOrgChangeDto sqlParam);

}

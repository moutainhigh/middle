package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.JoyHrTravelDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 乐才 HR 出差数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface JoyHrTravelMapper {

	/**
	 * @Description 最后更新时间
	 * @Author 郑勇浩
	 * @Data 2020/4/9 17:03
	 * @Param [enteId, appId]
	 * @return java.lang.String
	 */
	String findLastUpdateTime(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 批量新增或更新数据
	 * @Author 郑勇浩
	 * @Data 2020/2/22 16:22
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer insertOrUpdateBatch(@Param("sqlParam") JoyHrTravelDto sqlParam);

	/**
	 * @Description 批量更新出差数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/26 10:59
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateTravelUserBatch(@Param("enteId") String enteId, @Param("appId") String appId);

}

package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.JoyHrOvertimeDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 乐才 HR 加班数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface JoyHrOvertimeMapper {

	/**
	 * @Description 查询最后更新时间
	 * @Author 郑勇浩
	 * @Data 2020/4/9 16:31
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
	Integer insertOrUpdateBatch(@Param("sqlParam") JoyHrOvertimeDto sqlParam);

	/**
	 * @Description 更新加班数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 14:53
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateOvertimeUserBatch(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 最后更新时间
	 * @Author 郑勇浩
	 * @Data 2020/5/15 17:05
	 * @Param [enteId, appId, lastUpdateTime]
	 * @return java.lang.Integer
	 */
	Integer deleteOverTime(@Param("enteId") String enteId, @Param("appId") String appId, @Param("lastUpdateTime") String lastUpdateTime);
}

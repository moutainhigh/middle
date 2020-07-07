package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.JoyHrScheduleDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 乐才 HR 排版数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface JoyHrScheduleMapper {

	/**
	 * @Description 最后更新时间
	 * @Author 郑勇浩
	 * @Data 2020/4/15 11:43
	 * @Param []
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
	Integer insertOrUpdateBatch(@Param("sqlParam") JoyHrScheduleDto sqlParam);

	/**
	 * @Description 批量更新排班数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:20
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateScheduleUserBatch(@Param("enteId") String enteId, @Param("appId") String appId);

}

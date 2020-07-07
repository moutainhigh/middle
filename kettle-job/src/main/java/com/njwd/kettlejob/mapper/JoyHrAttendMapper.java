package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.JoyHrAttendDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @Description 乐才 HR 出勤数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface JoyHrAttendMapper {

	/**
	 * @Description 获取最后更新时间
	 * @Author 郑勇浩
	 * @Data 2020/4/2 9:17
	 * @Param [enteId, appId]
	 * @return java.util.Date
	 */
	String findLastDate(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 批量新增或更新数据
	 * @Author 郑勇浩
	 * @Data 2020/2/22 16:22
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer insertOrUpdateBatch(@Param("sqlParam") JoyHrAttendDto sqlParam);

	/**
	 * @Description 批量更新出勤数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/26 10:59
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateAttendUserBatch(@Param("param") JoyHrAttendDto param);

}

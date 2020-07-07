package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.JoyHrLeaveDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @Description 乐才 HR 请假数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface JoyHrLeaveMapper {

	/**
	 * @Description 最后更新时间
	 * @Author 郑勇浩
	 * @Data 2020/4/9 16:05
	 * @Param [enteId, appId]
	 * @return java.util.Date
	 */
	Date findLastUpdateTime(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 批量新增或更新数据
	 * @Author 郑勇浩
	 * @Data 2020/2/22 16:22
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer insertOrUpdateBatch(@Param("sqlParam") JoyHrLeaveDto sqlParam);

	/**
	 * @Description 批量更新请假数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/26 10:59
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateLeaveUserBatch(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 删除请假数据
	 * @Author 郑勇浩
	 * @Data 2020/5/15 16:59
	 * @Param [enteId, appId, lastUpdateTime]
	 * @return java.lang.Integer
	 */
	Integer deleteLeave(@Param("enteId") String enteId, @Param("appId") String appId,@Param("lastUpdateTime") String lastUpdateTime);
}

package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.JoyHrBrushRequireDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 乐才 HR 补卡数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface JoyHrBrushRequireMapper {

	/**
	 * @Description 查询最后更新时间
	 * @Author 郑勇浩
	 * @Data 2020/4/9 16:52
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
	Integer insertOrUpdateBatch(@Param("sqlParam") JoyHrBrushRequireDto sqlParam);

	/**
	 * @Description 批量更新补卡数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/26 10:59
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateBrushRequireUserBatch(@Param("enteId") String enteId, @Param("appId") String appId);

}

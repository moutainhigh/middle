package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.JoyHrShiftTimeDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 乐才 HR 班次数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface JoyHrShiftTimeMapper {

	/**
	 * @Description 批量删除
	 * @Author 郑勇浩
	 * @Data 2020/2/22 16:22
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer deleteBatch(@Param("sqlParam") JoyHrShiftTimeDto sqlParam);

	/**
	 * @Description 批量新增
	 * @Author 郑勇浩
	 * @Data 2020/3/21 14:28
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer insertBatch(@Param("sqlParam") JoyHrShiftTimeDto sqlParam);

}

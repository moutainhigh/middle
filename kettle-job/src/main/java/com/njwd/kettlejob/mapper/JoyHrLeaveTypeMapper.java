package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.JoyHrLeaveTypeDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 乐才 HR 请假数据 Mapper
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface JoyHrLeaveTypeMapper {

	/**
	 * @Description 批量新增或更新数据
	 * @Author 郑勇浩
	 * @Data 2020/2/22 16:22
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer insertOrUpdateBatch(@Param("sqlParam") JoyHrLeaveTypeDto sqlParam);

}

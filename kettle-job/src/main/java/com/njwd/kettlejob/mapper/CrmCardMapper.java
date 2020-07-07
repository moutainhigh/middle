package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.CrmWebHookUserDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description Crm Card Mapper 对应 crm_card
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface CrmCardMapper {

	/**
	 * @Description 新增Crm会员卡信息
	 * @Author 郑勇浩
	 * @Data 2020/5/11 16:03
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer insertCrmCard(@Param("param") CrmWebHookUserDto param);

	/**
	 * @Description 更新Crm会员卡信息
	 * @Author 郑勇浩
	 * @Data 2020/5/12 14:15
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateCrmCard(@Param("param") CrmWebHookUserDto param);


}

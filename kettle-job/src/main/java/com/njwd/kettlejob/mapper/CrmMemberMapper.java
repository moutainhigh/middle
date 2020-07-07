package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.dto.CrmWebHookUserDto;
import org.apache.ibatis.annotations.Param;

/**
 * @Description Crm Member Mapper 对应 crm_member
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface CrmMemberMapper {

	/**
	 * @Description 查询Crm会员信息是否存在
	 * @Author 郑勇浩
	 * @Data 2020/5/9 14:58
	 * @Param []
	 * @return java.lang.String
	 */
	Integer findCrmMemberCount(@Param("param") CrmWebHookUserDto param);

	/**
	 * @Description 新增Crm会员信息
	 * @Author 郑勇浩
	 * @Data 2020/5/11 16:03
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer insertCrmMember(@Param("param") CrmWebHookUserDto param);

	/**
	 * @Description 更新Crm会员信息
	 * @Author 郑勇浩
	 * @Data 2020/5/12 14:15
	 * @Param [param]
	 * @return java.lang.Integer
	 */
	Integer updateCrmMember(@Param("param") CrmWebHookUserDto param);

}

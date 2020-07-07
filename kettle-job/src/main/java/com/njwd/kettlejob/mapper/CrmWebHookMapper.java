package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.vo.CrmWebHookVo;

import java.util.List;

/**
 * @Description Crm WebHook Mapper 对应 crm_web_hook
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
public interface CrmWebHookMapper {

	/**
	 * @Description 查询所有的秘钥
	 * @Author 郑勇浩
	 * @Data 2020/5/13 16:36
	 * @Param []
	 * @return java.util.List<com.njwd.entity.kettlejob.vo.CrmWebHookVo>
	 */
	List<CrmWebHookVo> findCrmWebHookList();

}

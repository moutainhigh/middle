package com.njwd.kettlejob.service;

import com.njwd.entity.kettlejob.CrmWebHookResult;
import com.njwd.entity.kettlejob.vo.CrmWebHookUserVo;
import com.njwd.entity.kettlejob.vo.CrmWebHookVo;

import java.util.List;

/**
 * @Description Crm WebHook 接口 service
 * @Date 2020/5/8 14:03
 * @Author 郑勇浩
 */
public interface CrmWebHookService {

	/**
	 * @Description 获取所有的webHook key secret对应合集
	 * @Author 郑勇浩
	 * @Data 2020/5/13 16:56
	 * @Param []
	 * @return java.util.List<com.njwd.entity.kettlejob.vo.CrmWebHookVo>
	 */
	List<CrmWebHookVo> getCrmWebHookVoList();

	/**
	 * @Description 添加会员信息
	 * @Author 郑勇浩
	 * @Data 2020/5/9 14:24
	 * @Param [response]
	 * @return com.njwd.entity.kettlejob.CrmWebHookResult
	 */
	CrmWebHookResult addUser(CrmWebHookUserVo userVo);

	/**
	 * @Description 更新会员信息
	 * @Author 郑勇浩
	 * @Data 2020/5/11 17:47
	 * @Param [response]
	 * @return com.njwd.entity.kettlejob.CrmWebHookResult
	 */
	CrmWebHookResult updateUser(CrmWebHookUserVo userVo);

	/**
	 * @Description 删除会员信息
	 * @Author 郑勇浩
	 * @Data 2020/5/12 17:50
	 * @Param [userVo]
	 * @return com.njwd.entity.kettlejob.CrmWebHookResult
	 */
	CrmWebHookResult deleteUser(CrmWebHookUserVo userVo);

	/**
	 * @Description 会员取消关注
	 * @Author 郑勇浩
	 * @Data 2020/5/12 17:52
	 * @Param [userVo]
	 * @return com.njwd.entity.kettlejob.CrmWebHookResult
	 */
	CrmWebHookResult cancelSubscribe(CrmWebHookUserVo userVo);
}

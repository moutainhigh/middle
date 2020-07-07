package com.njwd.kettlejob.controller;

import com.njwd.common.Constant;
import com.njwd.common.KettleJobConstant;
import com.njwd.entity.kettlejob.CrmWebHookResponse;
import com.njwd.entity.kettlejob.CrmWebHookResult;
import com.njwd.entity.kettlejob.vo.CrmWebHookUserVo;
import com.njwd.exception.ResultCode;
import com.njwd.kettlejob.service.CrmWebHookService;
import com.njwd.utils.RedisUtils;
import com.njwd.utils.StringUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description Crm WebHook 接口 Controller
 * @Date 2020/5/8 14:03
 * @Author 郑勇浩
 */
@RestController
@RequestMapping("CrmWebHook")
public class CrmWebHookController {

	@Resource
	private CrmWebHookService crmWebHookService;

	/**
	 * @Description 获取WebHook内容
	 * @Author 郑勇浩
	 * @Data 2020/5/9 11:35
	 * @Param [response]
	 */
	@PostMapping("webhook")
	public CrmWebHookResult webhook(HttpServletRequest request, @RequestBody CrmWebHookResponse response) {
		CrmWebHookResult returnResult = new CrmWebHookResult();
		response.setEnteId(request.getAttribute(KettleJobConstant.KettleJobParam.enteId).toString());
		response.setAppId(request.getAttribute(KettleJobConstant.KettleJobParam.appId).toString());

		// 必填校验
		if (StringUtil.isBlank(response.getEvent()) || StringUtil.isBlank(response.getAction()) || StringUtil.isBlank(response.getFunction())) {
			returnResult.setErrcode(ResultCode.PARAMS_NOT.code);
			returnResult.setErrmsg(ResultCode.PARAMS_NOT.message);
			return returnResult;
		}
		//根据请求分类调用方法
		returnResult.setErrcode(ResultCode.OPERATION_FAILURE.code);
		returnResult.setErrmsg(ResultCode.OPERATION_FAILURE.message);
		// redis同步锁操作
		switch (response.getEvent()) {
			case KettleJobConstant.WebHookEvent.USER:
				// 用户事件推送
				return this.userInfoAction(returnResult, response);
			case KettleJobConstant.WebHookEvent.CHARGE:
				// 储值事件推送
				break;
			case KettleJobConstant.WebHookEvent.CREDIT:
				// 积分事件推送
				break;
			case KettleJobConstant.WebHookEvent.COUPON:
				// 券事件推送
				break;
			case KettleJobConstant.WebHookEvent.CONSUME:
				// 消费事件推送
				break;
		}
		return returnResult;
	}

	/**
	 * @Description 操作用户信息
	 * @Author 郑勇浩
	 * @Data 2020/5/11 17:53
	 * @Param []
	 * @return com.njwd.entity.kettlejob.CrmWebHookResult
	 */
	private CrmWebHookResult userInfoAction(CrmWebHookResult returnResult, CrmWebHookResponse response) {
		// 需要修改的用户信息
		CrmWebHookUserVo changeUserVo = response.getContent().get(KettleJobConstant.WebHookContent.USER_INFO).toJavaObject(CrmWebHookUserVo.class);
		if (changeUserVo == null) {
			returnResult.setErrcode(ResultCode.PARAMS_NOT.code);
			returnResult.setErrmsg(ResultCode.PARAMS_NOT.message);
			return returnResult;
		}

		changeUserVo.setEnteId(response.getEnteId());
		changeUserVo.setAppId(response.getAppId());
		CrmWebHookUserVo userVo;
		String redisKey;

		// 用户事件推送
		switch (response.getFunction()) {
			case KettleJobConstant.WebHookFunction.ADD_USER:
				// 非空
				if (StringUtil.isBlank(changeUserVo.getMemberId()) || StringUtil.isBlank(changeUserVo.getThirdShopId())) {
					returnResult.setErrcode(ResultCode.PARAMS_NOT.code);
					returnResult.setErrmsg(ResultCode.PARAMS_NOT.message);
					return returnResult;
				}
				// 新增
				redisKey = String.format(KettleJobConstant.LockKey.CRM_WEB_HOOK_USER, KettleJobConstant.WebHookEvent.USER, changeUserVo.getMemberId());
				return RedisUtils.lock(redisKey, Constant.SysConfig.REDIS_LOCK_TIMEOUT, () -> crmWebHookService.addUser(changeUserVo));
			case KettleJobConstant.WebHookFunction.UPDATE_USER:
				userVo = response.getChange().get(KettleJobConstant.WebHookContent.USER_INFO).toJavaObject(CrmWebHookUserVo.class);
				// 非空
				if (StringUtil.isBlank(userVo.getMemberId())) {
					returnResult.setErrcode(ResultCode.PARAMS_NOT.code);
					returnResult.setErrmsg(ResultCode.PARAMS_NOT.message);
					return returnResult;
				}
				// 修改
				changeUserVo.setMemberId(userVo.getMemberId());
				if (userVo.getUNo() != null) {
					changeUserVo.setUNo(userVo.getUNo());
				}
				redisKey = String.format(KettleJobConstant.LockKey.CRM_WEB_HOOK_USER, KettleJobConstant.WebHookEvent.USER, changeUserVo.getMemberId());
				return RedisUtils.lock(redisKey, Constant.SysConfig.REDIS_LOCK_TIMEOUT, () -> crmWebHookService.updateUser(changeUserVo));
			case KettleJobConstant.WebHookFunction.DELETE_USER:
				// 删除
				redisKey = String.format(KettleJobConstant.LockKey.CRM_WEB_HOOK_USER, KettleJobConstant.WebHookEvent.USER, changeUserVo.getMemberId());
				return RedisUtils.lock(redisKey, Constant.SysConfig.REDIS_LOCK_TIMEOUT, () -> crmWebHookService.deleteUser(changeUserVo));
			case KettleJobConstant.WebHookFunction.CANCEL_SUBSCRIBE:
				userVo = response.getChange().get(KettleJobConstant.WebHookContent.USER_INFO).toJavaObject(CrmWebHookUserVo.class);
				// 非空
				if (StringUtil.isBlank(userVo.getMemberId())) {
					returnResult.setErrcode(ResultCode.PARAMS_NOT.code);
					returnResult.setErrmsg(ResultCode.PARAMS_NOT.message);
					return returnResult;
				}
				// 取消关注
				changeUserVo.setMemberId(userVo.getMemberId());
				if (userVo.getUNo() != null) {
					changeUserVo.setUNo(userVo.getUNo());
				}
				redisKey = String.format(KettleJobConstant.LockKey.CRM_WEB_HOOK_USER, KettleJobConstant.WebHookEvent.USER, changeUserVo.getMemberId());
				return RedisUtils.lock(redisKey, Constant.SysConfig.REDIS_LOCK_TIMEOUT, () -> crmWebHookService.cancelSubscribe(changeUserVo));
		}
		return returnResult;
	}

}

package com.njwd.common;

/**
 * kettleJob通用查询参数
 *
 * @author luoY
 * @since 2019/10/30
 */
public interface KettleJobConstant {
	/**
	 * schedule任务类型
	 */
	interface ScheduleJobType {
		String KTR = "KTR";
		String KJB = "KJB";
		String JAVABEAN = "JAVABEAN";
	}

	/**
	 * schedule任务类型
	 */
	interface KettleJobParam {
		String enteId = "enteId";
		String appId = "appId";
		String businessType = "businessType";
		String dataType = "dataType";
	}

	/**
	 * schedule任务类型
	 */
	interface KettleException {
		String SUCCESS = "success";
		String FAIL = "fail";
		String ERROR = "error";
	}

	/**
	 * schedule异常
	 */
	interface ScheduleError {
		/**
		 * 数据异常
		 */
		String DATA_ERR_INFO = "DATA_ERR_INFO";

		/**
		 * task任务参数信息异常
		 */
		String TASK_ERR_PARAM_ERROR = "TASK_ERR_PARAM_ERROR";

		/**
		 * 应用服务异常
		 */
		String BUS_ERR_SERVER = "BUS_ERR_SERVER";
	}

	/**
	 * 同步锁
	 */
	interface LockKey{
		/**
		 * crm webHook 同步锁 %s 事件类型 %s 事件id
		 */
		String CRM_WEB_HOOK_USER = "lock:crmWebHook:%s:%s";
	}

	/**
	 * webhook 常量
	 */
	interface WebHook {
		String webHookKey = "crmWebHookList";
		String SIGNATURE = "X-Hub-Signature";
		String CONTENT_TYPE = "application/json; charset=utf-8";
		String MD5 = "md5";
	}

	/**
	 * webhook 事件
	 */
	interface WebHookEvent {
		/**
		 * 用户事件推送
		 */
		String USER = "user";
		/**
		 * 储值事件推送
		 */
		String CHARGE = "charge";
		/**
		 * 积分事件推送
		 */
		String CREDIT = "credit";
		/**
		 * 券事件推送
		 */
		String COUPON = "coupon";
		/**
		 * 消费事件推送
		 */
		String CONSUME = "consume";
	}

	/**
	 * webhook 操作类型
	 */
	interface WebHookAction {
		/**
		 * 新增
		 */
		String ADDED = "added";
		/**
		 * 修改
		 */
		String EDITED = "edited";
		/**
		 * 删除
		 */
		String DELETED = "deleted";
	}

	/**
	 * webhook 业务操作类型
	 */
	interface WebHookFunction {
		/**
		 * 新增用户
		 */
		String ADD_USER = "addUser";
		/**
		 * 删除用户
		 */
		String DELETE_USER = "deleteUser";
		/**
		 * 修改用户
		 */
		String UPDATE_USER = "updateUser";
		/**
		 * 取消关注
		 */
		String CANCEL_SUBSCRIBE = "cancelSubscribe";
	}

	/**
	 * webhook 业务内容
	 */
	interface WebHookContent {
		/**
		 * 用户信息
		 */
		String USER_INFO = "userInfo";
	}

	/**
	 * webhook 返回成功内容
	 */
	interface WebHookSuccess {
		Integer ERRCODE = 200;
		String ERRMSG = "SUCCESS";
	}

}

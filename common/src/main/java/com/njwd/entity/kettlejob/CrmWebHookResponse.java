package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @Description 微生活 响应实体
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class CrmWebHookResponse {

	/**
	 * 商户id
	 */
	private Long bid;

	/**
	 * 事件
	 */
	private String event;

	/**
	 * 动作
	 */
	private String action;

	/**
	 * 变更的内容 json格式
	 */
	private Map<String, JSONObject> content;

	/**
	 * 历史的记录，用于对比数据变化
	 */
	private Map<String, JSONObject> change;

	/**
	 * 异常推送发生的时间
	 */
	@JSONField(name = "RedoUpdatetime")
	private Date redoUpdateTime;

	/**
	 * 推送的次数统计，告知第三方我们已经发了多少次
	 */
	private int countSent;

	/**
	 * 会员信息
	 */
	@JSONField(name = "UserInfo")
	private Object userInfo;

	/**
	 * 业务操作类型
	 */
	private String function;

	/**
	 * enteId
	 */

	private String enteId;
	/**
	 * appId
	 */
	private String appId;
}

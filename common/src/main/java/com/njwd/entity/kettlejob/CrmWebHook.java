package com.njwd.entity.kettlejob;

import lombok.Data;

/**
 * @Description 微生活 webHook 返回实体
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class CrmWebHook {

	/**
	 * 未加密秘钥
	 */
	private String key;

	/**
	 * 加密后秘钥
	 */
	private String secret;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * appId
	 */
	private String appId;


}

package com.njwd.entity.kettlejob.vo;

import com.njwd.entity.kettlejob.CrmWebHookUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Description 微生活 user 返回实体 vo
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CrmWebHookUserVo extends CrmWebHookUser {

	/**
	 * 门店ID
	 */
	private String shopId;

	/**
	 * 身份证号
	 */
	private String cid;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 会员卡类型ID
	 */
	private String cardTypeId;

	/**
	 * ente id
	 */
	private String enteId;

	/**
	 * app id
	 */
	private String appId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}

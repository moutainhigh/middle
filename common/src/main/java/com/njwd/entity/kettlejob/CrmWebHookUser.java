package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 微生活 user 返回实体
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class CrmWebHookUser {

	/**
	 * 会员id
	 */
	@JSONField(name = "uid")
	private Long memberId;

	/**
	 * 会员类型，1:微信电子卡；2、3、4，实体卡会员；6：口碑卡会员；10:爱奇艺会员；
	 */
	@JSONField(name = "uType")
	private Integer memberTypeId;

	/**
	 * 会员名称
	 */
	@JSONField(name = "uName")
	private String memberName;

	/**
	 * 会员手机号
	 */
	@JSONField(name = "uPhone")
	private String mobile;

	/**
	 * 会员头像
	 */
	@JSONField(name = "uAvatar")
	private String uAvatar;

	/**
	 * 性别，1男；2女；0未知
	 */
	@JSONField(name = "uGender")
	private Integer sex;

	/**
	 * 会员生日日期
	 */
	@JSONField(name = "uBirthDay")
	private String birthday;

	/**
	 * 用户城市
	 */
	@JSONField(name = "uCid")
	private Long uCid;

	/**
	 * 商户bid
	 */
	@JSONField(name = "bid")
	private Long bid;

	/**
	 * 扫描二维码id
	 */
	@JSONField(name = "qrid")
	private String qrid;

	/**
	 * 会员卡号
	 */
	@JSONField(name = "uNo")
	private String uNo;

	/**
	 * openid
	 */
	@JSONField(name = "openid")
	private String openid;

	/**
	 * 注册时间
	 */
	@JSONField(name = "uRegistered")
	private Date registerTime;

	/**
	 * 取消关注时间
	 */
	@JSONField(name = "uUnRegistered")
	private Date unRegisterTime;

	/**
	 * 会员信息状态；0未验证；1已绑定手机号；2已完善资料
	 */
	@JSONField(name = "uUserInfoStatus")
	private Integer uUserInfoStatus;

	/**
	 * 消费次数
	 */
	@JSONField(name = "uConsumeNum")
	private Integer uConsumeNum;

	/**
	 * 消费金额
	 */
	@JSONField(name = "uConsumeAmount")
	private Integer uConsumeAmount;

	/**
	 * 会员卡状态;1正常状态；2开卡；3取消开卡；9删除
	 */
	@JSONField(name = "uCardStatus")
	private Integer uCardStatus;

	/**
	 * 激活状态；1:未消费；2:新会员；4:老会员；8:活跃会员；16:沉寂会员；
	 */
	@JSONField(name = "uActivityStatus")
	private Integer uActivityStatus;

	/**
	 * 用户状态；1：普通状态；9：已删除
	 */
	@JSONField(name = "uStatus")
	private Integer uStatus;

	/**
	 * 实体卡号
	 */
	@JSONField(name = "uActualNo")
	private String uActualNo;

	/**
	 * 卡包code
	 */
	@JSONField(name = "uWeixinCardFlag")
	private String uWeixinCardFlag;

	/**
	 * 对接商家卡号
	 */
	@JSONField(name = "uCrmNo")
	private String uCrmNo;

	/**
	 * 通卡原始wxuin
	 */
	@JSONField(name = "uCrmUid")
	private String uCrmUid;

	/**
	 * 用户首次完善资料时间
	 */
	@JSONField(name = "uUserInfoCompleted")
	private Date uUserInfoCompleted;

	/**
	 * 会员微信昵称
	 */
	@JSONField(name = "uWXNickname")
	private String uWXNickname;

	/**
	 * 是否是阴历生日
	 */
	@JSONField(name = "uIsLunarBirth")
	private Integer uIsLunarBirth;

	/**
	 * 会员密码md5加密
	 */
	@JSONField(name = "uPayPassword")
	private String uPayPassword;

	/**
	 * 自定义项
	 */
	@JSONField(name = "uCustomFields")
	private String uCustomFields;

	/**
	 * 最后一次关注时间
	 */
	@JSONField(name = "uLastRegistered")
	private Date uLastRegistered;

	/**
	 * 锁定状态，1锁定0未锁
	 */
	@JSONField(name = "uLock")
	private Integer uLock;

	/**
	 * 关联卡种类
	 */
	@JSONField(name = "ccid")
	private Integer ccid;

	/**
	 * 收银员自定义消费使用储值验证方式
	 */
	@JSONField(name = "uCustomChargePay")
	private Integer uCustomChargePay;

	/**
	 * 更新时间
	 */
	@JSONField(name = "UpdateTime")
	private Date UpdateTime;

	/**
	 * 用户最后一次设置生日的时间
	 */
	@JSONField(name = "uSetBirthDayTime")
	private Date uSetBirthDayTime;

	/**
	 * 用户手机号区号
	 */
	@JSONField(name = "uPhonePrefix")
	private String uPhonePrefix;

	/**
	 * 外卡导入-旧系统卡号
	 */
	@JSONField(name = "uFromOtherCNo")
	private String uFromOtherCNo;

	/**
	 * 外卡导入-旧系统储值余额（单位：分）
	 */
	@JSONField(name = "uFromOtherCMoney")
	private Integer uFromOtherCMoney;

	/**
	 * 外卡导入-旧系统积分余额
	 */
	@JSONField(name = "uFromOtherCCredit")
	private Integer uFromOtherCCredit;

	/**
	 * 对应支付宝id
	 */
	@JSONField(name = "uAlipayid")
	private String uAlipayid;

	/**
	 * unionid
	 */
	@JSONField(name = "unionid")
	private String unionid;

	/**
	 * 口碑用户唯一识别
	 */
	@JSONField(name = "koubeiId")
	private String koubeiId;

	/**
	 * 初始开卡源id
	 */
	@JSONField(name = "qridSrc")
	private String qridSrc;

	/**
	 * 门店ID，0表示未指定门店一般是线上开卡
	 */
	@JSONField(name = "sid")
	private Long thirdShopId;
}

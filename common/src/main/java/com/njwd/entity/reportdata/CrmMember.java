package com.njwd.entity.reportdata;

import java.util.Date;
import lombok.Data;

@Data
public class CrmMember {
    /**
    * 会员ID
    */
    private String memberId;

    /**
    * 应用id
    */
    private String appId;

    /**
    * 企业ID
    */
    private String enteId;

    /**
    * 名称
    */
    private String memberName;

    /**
    * 出身日期
    */
    private Date birthday;

    /**
    * 性别
    */
    private String sex;

    /**
    * 会员类型    wx:微信卡 dp:点评卡 ph:实体卡 actual:实体卡
    */
    private String memberTypeId;

    /**
    * 手机号
    */
    private String mobile;

    /**
    * 邮箱
    */
    private String email;

    /**
    * 地址
    */
    private String address;

    /**
    * 身份证
    */
    private String cid;

    /**
    * 线上申请ID   微信openid（微信关注用户）
    */
    private String openid;

    /**
    * 注册时间
    */
    private Date registerTime;

    /**
    * 取消关注时间（线上用户用）
    */
    private Date unRegisterTime;

    /**
    * 注册门店id
    */
    private String shopId;
}
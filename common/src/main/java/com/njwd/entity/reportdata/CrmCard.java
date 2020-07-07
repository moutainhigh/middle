package com.njwd.entity.reportdata;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
/**
 * @Author ZhuHC
 * @Date  2020/2/14 9:23
 * @Description  会员卡信息
 */
@Data
public class CrmCard {
    /**
    * 会员卡ID
    */
    private String cardId;

    /**
    * 应用id
    */
    private String appId;

    /**
    * 企业ID
    */
    private String enteId;

    /**
    * 对应crm_member表的会员ID
    */
    private String memberId;

    /**
    * 会员卡号
    */
    private String cardNo;

    /**
    * 实体卡号
    */
    private String entityCardNo;

    /**
    * 会员卡类型ID
    */
    private String cardTypeId;

    /**
    * 会员状态0 未激活； 1，正常；2 注销 3挂失
    */
    private String status;

    /**
    * 开卡日期
    */
    private Date activateTime;

    /**
    * 办卡门店id
    */
    private String shopId;
}
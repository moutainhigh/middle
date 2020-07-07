package com.njwd.entity.reportdata;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class CrmCardVar {
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
    * 余额
    */
    private BigDecimal money;

    /**
    * 储值实收余额
    */
    private BigDecimal prepaidMoney;

    /**
    * 储值返现余额
    */
    private BigDecimal largessMoney;

    /**
    * 累计储值金额
    */
    private BigDecimal totalMoney;

    /**
    * 累计储值实收金额
    */
    private BigDecimal totalPrepaidMoney;

    /**
    * 累计储值返现余额
    */
    private BigDecimal totalLargessMoney;

    /**
    * 会员卡积分
    */
    private Integer integral;

    /**
    * 累计积分
    */
    private Integer totalIntegral;

    /**
    * 累计消费次数
    */
    private Integer totalConsumeNum;

    /**
    * 累计消费金额
    */
    private BigDecimal totalConsumeMoney;

    /**
    * 最后一次消费时间
    */
    private Date lastHandleTime;

    /**
    * 办卡门店id
    */
    private String shopId;
}
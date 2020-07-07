package com.njwd.entity.reportdata;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class CrmPrepaidPayType {
    private String prepaidPayTypeId;

    /**
    * 充值记录id
    */
    private String prepaidId;

    /**
    * 支付方式id
    */
    private String payTypeId;

    /**
    * 充值金额
    */
    private BigDecimal money;

    /**
    * 门店id
    */
    private String shopId;

    /**
    * 应用id
    */
    private String appId;

    /**
    * 集团id
    */
    private String enteId;

    /**
    * 充值时间
    */
    private Date prepaidTime;
}
package com.njwd.entity.reportdata.vo.fin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *@description: VoucherVo
 *@author: fancl
 *@create: 2020-01-10 
 */
@Getter
@Setter
public class FinVoucherVo implements Serializable {
    //品牌
    private String brandId;
    //区域
    private String regionId;
    //门店
    private String shopId;
    //企业id
    private String enteId;
    //凭证id
    private String voucherId;
    //凭证日期 date格式
    private Date voucherDate;
    //凭证日期 字符串 yyyy-mm-dd格式
    private String voucherDay;
    //借方金额
    private BigDecimal debitAmount;
    //借方金额
    private BigDecimal creditAmount;
    //发生额
    private BigDecimal amount;

}

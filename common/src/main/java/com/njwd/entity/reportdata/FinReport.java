package com.njwd.entity.reportdata;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *@description: 财务报表基准数据对象
 *@author: fancl
 *@create: 2020-01-10 
 */
@Setter
@Getter
public class FinReport implements Serializable {
    //企业id
    private String enteId;
    //交易时间 Date格式
    private Date transTime;
    //交易日期 String 格式
    private String transDay;
    //记账日期
    private Date bookingTime;
    //品牌id
    private String brandId;
    //区域id
    private String regionId;
    //门店id
    private String shopId;
    //科目id
    private String accountSubjectId;
    //科目名称
    private String accountSubjectName;
    //科目code
    private String accountSubjectCode;

    //借方金额
    private BigDecimal debitAmount;
    //贷方金额
    private BigDecimal creditAmount;
    //发生额
    private BigDecimal amount;
    //类型标识
    private String flag;

    //重写toString方法
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("enteId=").append(enteId)
                .append(",transDay=").append(transDay)
                .append(",shopId=").append(shopId)
                .append(",accountSubjectId=").append(accountSubjectId)
                .append(",flag=").append(flag).toString();

    }
}

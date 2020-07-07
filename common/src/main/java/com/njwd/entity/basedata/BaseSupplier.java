package com.njwd.entity.basedata;

import java.util.Date;
import lombok.Data;

/**
* @Description: 供应商
* @Author: LuoY
* @Date: 2020/3/27 14:21
*/
@Data
public class BaseSupplier {
    /**
    * 供应商ID
    */
    private String supplierId;

    /**
    * 企业id
    */
    private String enteId;

    /**
    * 名称
    */
    private String supplierName;

    /**
    * 创建组织内码
    */
    private String createOrgId;

    /**
    * 使用组织内码
    */
    private String useOrgId;

    /**
    * 数据状态
    */
    private String documentStatus;

    /**
    * 禁用状态 A = 正常; B = 禁用
    */
    private String forbitStatus;

    /**
    * 编码
    */
    private String number;

    /**
    * 供应商分组
    */
    private Integer primaryGroup;

    /**
    * 对应组织
    */
    private String correspondOrgId;

    /**
    * 描述
    */
    private String description;

    /**
    * 银行类型
    */
    private String bankType;

    /**
    * 开户行名称
    */
    private String openBankName;

    /**
    * 开户行编号
    */
    private String openBankCode;

    /**
    * 银行账户
    */
    private Integer bankCode;

    /**
    * 银行基础资料ID
    */
    private Integer baseBankId;

    /**
    * 创建日期
    */
    private Date createDate;

    /**
    * 修改日期
    */
    private Date modifyDate;
}
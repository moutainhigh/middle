package com.njwd.entity.reportdata.vo.fin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 *@description: 财务报表返回数据实体
 *@author: fancl
 *@create: 2020-01-12 
 */
@Getter
@Setter
public class FinSubjectVo implements Serializable {
    //类型 shop 为门店 brand 品牌 region区域
    private String type ;
    private String brandId;
    private String brandName;
    private String regionId;
    private String regionName;
    private String shopId;
    private String shopName;

    //科目名称
    private String accountSubjectName;
    //金额
    private BigDecimal amount;
    //总金额
    private BigDecimal allAmount;
    //比例
    private BigDecimal percent;
    //总比例
    private BigDecimal allPercent;
    //包含科目数据的map
    private LinkedHashMap<String, FinSubjectInner> subjectMap;
}

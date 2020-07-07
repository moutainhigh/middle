package com.njwd.entity.reportdata.vo.fin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *@description: 实时利润分析预算设置实体
 *@author: liBao
 *@create: 2020-03-2
 */
@Getter
@Setter
@ToString
public class RealProfitBudgetVo implements Serializable {
    //Id
    private String id;

    //品牌Id
    private String brandId ;
    //区域ID
    private String regionId;
    //门店Id
    private String shopId ;

    //项目名称
    private String project;

    //预算
    private BigDecimal budget ;

    //有效期开始时间
    private String beginDate;
    //有效期结束时间
    private String endDate;

    //状态
    private Integer status;

    //企业ID
    private String enteId ;



}

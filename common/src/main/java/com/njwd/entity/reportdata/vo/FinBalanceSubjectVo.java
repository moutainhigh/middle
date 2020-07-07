package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.FinBalanceSubject;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Author lj
 * @Description 科目余额表
 * @Date:15:29 2020/1/10
 **/
@Getter
@Setter
public class FinBalanceSubjectVo extends FinBalanceSubject {

    /**
     *科目编码
     **/
    private String accountSubjectCode;

    /**
     *科目名称
     **/
    private String accountSubjectName;


    /**
     *科目方向
     **/
    private Integer accountSubjectDirection;

    /**
     * 科目本期发生额
     **/
    private BigDecimal subjectAmount;

    /**
     * 科目本年累计发生额
     **/
    private BigDecimal subjectTotalAmount;
    /**
     * 门店id
     **/
    private String shopId;
    /**
     * 门店名称
     **/
    private String shopName;
    /**
     * 品牌id
     **/
    private String brandId;
    /**
     * 品牌名称
     **/
    private String brandName;

    /**
     * 区域id
     **/
    private String regionId;
    /**
     * 区域名称
     **/
    private String regionName;

    /**
     * 期初
     */
    private BigDecimal openBalanceY;

    /**
     * 期末
     */
    private BigDecimal closeBalanceY;



}

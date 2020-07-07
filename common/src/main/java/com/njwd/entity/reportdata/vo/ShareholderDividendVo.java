package com.njwd.entity.reportdata.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author lj
 * @Description 股东分红表
 * @Date:13:35 2020/2/29
 **/
@Getter
@Setter
public class ShareholderDividendVo implements Serializable {

    private static final long serialVersionUID = -8396021120514217376L;
    //类型 shop 为门店 brand 品牌 region区域
    private String type ;
    private String brandId;
    private String brandName;
    private String regionId;
    private String regionName;
    private String shopId;
    private String shopName;
    /**
     * 序号
     **/
    private Integer lineNum;

    /**
     * 股东
     **/
    private String userName;

    /**
     * 分红比例
     **/
    private BigDecimal percent;

    /**
     * 净利润
     **/
    private BigDecimal netProfit;

    /**
     * 可分利润
     **/
    private BigDecimal cutProfit;

}

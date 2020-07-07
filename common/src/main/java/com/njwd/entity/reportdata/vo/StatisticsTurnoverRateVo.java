package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/12/30 14:30
 */
@Data
public class StatisticsTurnoverRateVo {
    private static final long serialVersionUID = -2013399713650663306L;
    /**
     * 类型(shop 门店 brand 品牌 region区域)
     */
    private String type;
    /**
     * 桌数
     */
    private Integer deskNum=0;

    /**
     * 开台数
     */
    private Integer stationsNum =0;
    /**
     * 翻台率
     */
    private BigDecimal statisticsTurnoverPercentage;
    /**
     * 企业ID
     */
    private String enteId;
    /**
     * 品牌id
     */
    private String brandId;
    /**
     * 品牌code
     */
    private String brandCode;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 区域id
     */
    private String regionId;
    /**
     * 区域code
     */
    private String regionCode;
    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 门店id
     */
    private String shopId;
    /**
     * 门店编码
     */
    private String shopNo;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 营业天数
     */
    private BigDecimal businessDays= new BigDecimal(0.00);
}

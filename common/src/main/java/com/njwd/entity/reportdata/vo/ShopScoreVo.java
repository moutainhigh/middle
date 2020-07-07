package com.njwd.entity.reportdata.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jds
 * @Description 门店评分报表
 * @create 2019/11/26 9:27
 */
@Data
@ToString(callSuper = true)
public class ShopScoreVo  {

    /**
     * 本期评分
     */
    private BigDecimal score;


    /**
     * 上期评分
     */
    private BigDecimal scorePrior;

    /**
     * 去年同期评分
     */
    private BigDecimal scoreLastYear;

    /**
     * 环比
     */
    private BigDecimal linkRatio;

    /**
     * 同比
     */
    private BigDecimal overYear;


    /**
     * 项目类型id
     */
    private String typeId;

    /**
     * 项目类型名称（小）
     */
    private String itemName;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 区域name
     */
    private String regionName;

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 品牌name
     */
    private String brandName;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 门店类型id
     */
    private String shopTypeId;

    /**
     * 区域内排名
     */
    private Integer rankRegion;

    /**
     * 品牌内排名
     */
    private Integer rankBrand;

    /**
     * 集团内排名
     */
    private Integer rankEnte;

    /**
     * 集团id
     */
    private String enteId;

    /**
     * 门店编制人数
     */
    private Integer shopPersonNum;

    /**
     * 项目类型
     */
    private String type;

    /**
     * 项目 评分 集合
     */
    private List<ShopScoreVo> itemScoreList;

    /**
     * 项目 评分总和 集合
     */
    private List<ShopScoreVo> itemSumScoreList;
}

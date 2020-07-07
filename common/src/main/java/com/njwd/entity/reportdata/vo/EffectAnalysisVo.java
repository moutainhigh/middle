package com.njwd.entity.reportdata.vo;

import com.njwd.common.ReportDataConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ljc
 * @Description 人效分析vo
 * @create 2020/03/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EffectAnalysisVo  extends BaseScopeOfQueryType implements Serializable {
    /**
     * 数据类型：用于区分品牌/区域/门店
     */
    private String type;
    /**
     * 数据类型id
     */
    private String typeId;
    /**
     * 企业ID
     */
    @ApiModelProperty(name="enteId",value = "企业id")
    private String enteId;
    /**
     * 品牌ID
     */
    @ApiModelProperty(name="brandId",value = "品牌ID")
    private String brandId;

    /**
     * 品牌
     */
    @ApiModelProperty(name="brandName",value = "品牌")
    private String brandName;


    /**
     * 区域ID
     */
    @ApiModelProperty(name="regionId",value = "区域ID")
    private String regionId;

    /**
     * 区域
     */
    @ApiModelProperty(name="regionName",value = "区域")
    private String regionName;

    /**
     * 门店ID
     */
    @ApiModelProperty(name="shopId",value = "门店ID")
    private String shopId;

    /**
     * 门店
     */
    @ApiModelProperty(name="shopName",value = "门店")
    private String shopName;

    /**
     * 人员ID
     */
    @ApiModelProperty(name="userId",value = "人员ID")
    private String userId;
    /**
     * 本期营业额
     */
    @ApiModelProperty(name="currAmount",value = "本期-营业额")
    private BigDecimal currAmount;

    /**
     * 本期员工人数
     */
    @ApiModelProperty(name="currStaffNum",value = "本期-员工人数")
    private Integer currStaffNum;

    /**
     * 本期人效(元/人)
     */
    @ApiModelProperty(name="currEffect",value = "本期-人效(元/人)")
    private BigDecimal currEffect;

    /**
     * 上期营业额
     */
    @ApiModelProperty(name="prevAmount",value = "上期-营业额")
    private BigDecimal prevAmount;

    /**
     * 上期员工人数
     */
    @ApiModelProperty(name="prevStaffNum",value = "上期-员工人数")
    private Integer prevStaffNum;

    /**
     * 上期人效(元/人)
     */
    @ApiModelProperty(name="prevEffect",value = "上期-人效(元/人)")
    private BigDecimal prevEffect;

    /**
     * 去年同期营业额
     */
    @ApiModelProperty(name="lastYearAmount",value = "去年同期-营业额")
    private BigDecimal lastYearAmount;

    /**
     * 去年同期员工人数
     */
    @ApiModelProperty(name="lastYearStaffNum",value = "去年同期-员工人数")
    private Integer lastYearStaffNum;

    /**
     * 去年同期人效(元/人)
     */
    @ApiModelProperty(name="lastYearEffect",value = "去年同期-人效(元/人)")
    private BigDecimal lastYearEffect;

    /**
     * 同比-较上年-员工人数(%)
     */
    @ApiModelProperty(name="overYearStaffNum",value = "较上年-员工人数(%)")
    private BigDecimal overYearStaffNum;

    /**
     * 同比-较上年-人效(元/人)(%)
     */
    @ApiModelProperty(name="overYearEffect",value = "较上年-人效(元/人)(%)")
    private BigDecimal overYearEffect;
    /**
     * 环比-较上期-员工人数(%)
     */
    @ApiModelProperty(name="linkRatioStaffNum",value = "较上期-员工人数(%)")
    private BigDecimal linkRatioStaffNum;
    /**
     * 环比-较上期-人效(元/人)(%)
     */
    @ApiModelProperty(name="linkRatioEffect",value = "较上期-人效(元/人)(%)")
    private BigDecimal linkRatioEffect;
}

package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Description: 会员画像
 * @Author ljc
 * @Date 2019/12/26
 */
@Getter
@Setter
public class MemberPortraitVo {

    /**
     * 门店id
     */
    @ApiModelProperty(name="shopId",value = "门店id")
    private String shopId;
    /**
     * 门店名称
     */
    @ApiModelProperty(name="shopName",value = "门店名称")
    private String shopName;
    /**
     * 区域id
     */
    @ApiModelProperty(name="regionId",value = "区域id")
    private String regionId;
    /**
     * 区域名称
     */
    @ApiModelProperty(name="regionName",value = "区域名称")
    private String regionName;
    /**
     * 品牌id
     */
    @ApiModelProperty(name="brandId",value = "品牌id")
    private String brandId;
    /**
     * 品牌名称
     */
    @ApiModelProperty(name="brandName",value = "品牌名称")
    private String brandName;
    /**
     * 会员id
     */
    @ApiModelProperty(name="memberId",value = "会员id")
    private String memberId;
    /**
     * 会员名称
     */
    @ApiModelProperty(name="memberName",value = "会员名称")
    private String memberName;
    /**
     * 会员卡id
     */
    @ApiModelProperty(name="cardId",value = "会员卡id")
    private String cardId;
    /**
     * 会员卡号
     */
    @ApiModelProperty(name="cardNo",value = "会员卡号")
    private String cardNo;
    /**
     * 消费时段
     */
    @ApiModelProperty(name="consumePeriod",value = "消费时段")
    private String consumePeriod;
    /**
     * 消费频次
     */
    @ApiModelProperty(name="consumeFrequency",value = "消费频次")
    private Integer consumeFrequency;

    /**
     * 年龄阶段
     */
    @ApiModelProperty(name="agePeriod",value = "年龄阶段")
    private String agePeriod;

    /**
     * 累计消费金额
     */
    @ApiModelProperty(name="totalConsumeMoney",value = "累计消费金额")
    private BigDecimal totalConsumeMoney;
    /**
     * 代金券使用张数
     */
    @ApiModelProperty(name="couponUseNum",value = "代金券使用张数")
    private Integer  couponUseNum;
    /**
     * 代金券使用金额
     */
    @ApiModelProperty(name="couponUseMoney",value = "代金券使用金额")
    private BigDecimal  couponUseMoney;
    /**
     * 生日
     */
    @ApiModelProperty(name="birthday",value = "会员生日")
    private String  birthday;

    /**
     * 性别
     */
    @ApiModelProperty(name="sex",value = "性别")
    private String  sex;
}

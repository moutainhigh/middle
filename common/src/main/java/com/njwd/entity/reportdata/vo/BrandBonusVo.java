package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author ljc
 * @Description 品牌vo
 * @create 2020/04/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandBonusVo extends BaseScopeOfQueryType implements Serializable {

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
     * 员工id
     */
    @ApiModelProperty(name="userId",value = "员工id")
    private String userId;
    /**
     * 员工姓名
     */
    @ApiModelProperty(name="userName",value = "员工姓名")
    private String userName;

    /**
     * 基数(净利润)
     */
    @ApiModelProperty(name="netProfit",value = "基数")
    private BigDecimal netProfit;

    /**
     * 人事比例
     */
    @ApiModelProperty(name="personnelRatio",value = "人事比例")
    private BigDecimal personnelRatio;

    /**
     * 奖励比例
     */
    @ApiModelProperty(name="rewardRatio",value = "奖励比例")
    private BigDecimal rewardRatio;

    /**
     * 缺勤天数
     */
    @ApiModelProperty(name="absence",value = "缺勤")
    private BigDecimal absence;

    /**
     * 总额
     */
    @ApiModelProperty(name="amount",value = "总额")
    private BigDecimal amount;

    /**
     * 年终待发
     */
    @ApiModelProperty(name="endYearAmount",value = "年终待发")
    private BigDecimal endYearAmount;

    /**
     * 当期实发
     */
    @ApiModelProperty(name="currentAmount",value = "当期实发")
    private BigDecimal currentAmount;

    /**
     * 转正时间
     */
    @ApiModelProperty(name="positiveTime",value = "转正时间")
    private Date positiveTime;

    /**
     * 请假时长
     */
    @ApiModelProperty(name="leaveHour",value = "请假时长")
    private BigDecimal leaveHour;

    /**
     * 出勤时长
     */
    @ApiModelProperty(name="attendHour",value = "出勤时长")
    private BigDecimal attendHour;

    /**
     * 入职时间
     */
    private Date hiredate;

    /**
     * 标准工时
     */
    @ApiModelProperty(name="standardHour",value = "标准工时")
    private BigDecimal standardHour;
}

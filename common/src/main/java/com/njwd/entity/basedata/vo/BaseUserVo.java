package com.njwd.entity.basedata.vo;

import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.BaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/10 13:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseUserVo extends BaseUser {
    private static final long serialVersionUID = -2633410527426481825L;
    /**
     * 门店id
     */
    private String shopId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 区域ID
     */
    private String regionId;
    /**
     * 品牌ID
     */
    private String brandId;

    /**
     * 是否当期入职  0否 1是
     */
    private Integer isCurrentEntry;

    /**
     *  年龄类型
     */
    private Integer ageType;

    /**
     *  年龄
     */
    private Integer age;

    /**
     *  工龄类型
     */
    private Integer standingType;

    /**
     * 学历类型
     */
    private Integer eduLevelType = ReportDataConstant.EducationalBackground.PRIMARY_DEGREE;
    /**
     * 是否管理岗  0否 1是
     */
    private Integer isManager;
}

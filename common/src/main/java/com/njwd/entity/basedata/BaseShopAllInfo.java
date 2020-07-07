package com.njwd.entity.basedata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 门店
 * @Author LuoY
 * @Date 2019/12/3
 */
@Data
public class BaseShopAllInfo {
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
     * 门店机构类型
     */
    private String shopTypeId;

    /**
     * 经营模式类型
     */
    private String patternTypeId;

    /**
     * 门店地址
     */
    private String address;

    /**
     * 纬度
     */
    private String shopLat;

    /**
     * 经度
     */
    private String shopLon;

    /**
     * 门店联系人姓名
     */
    private String linkMan;

    /**
     * 联系人电话
     */
    private String linkTele;

    /**
     * 联系人手机
     */
    private String linkMobile;

    /**
     * 面积
     */
    private BigDecimal shopArea;

    /**
     * 城市
     */
    private String city;

    /**
     * 集团ID
     */
    private String enteId;

    /**
     * 公司id
     */
    private String companyId;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 状态0正常，1关店
     */
    private String status;

    /**
     * 关停时间
     */
    private Date shutdownDate;

    /**
     * 开业时间
     */
    private Date openingDate;

}

package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/3/24 18:00
 */
@Data
public class UserCountVo implements Serializable {

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
     * 门店部门编码 0 前厅 1 后厨
     */
    private String shopDeptNo;
    /**
     * 人数
     */
    private Integer peopleNum;
}

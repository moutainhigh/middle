package com.njwd.entity.reportdata.vo;

import com.njwd.common.ReportDataConstant;
import lombok.Data;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/11 11:12
 */
@Data
public class BaseScopeOfQueryType {
    /**
     * 查询 范围类型
     * 类型 shop 为门店 brand 品牌 region区域 all 全部
     */
    private String type = ReportDataConstant.Finance.TYPE_SHOP;
    /**
     * 企业ID
     */
    private String enteId;
    /**
     * 门店ID
     */
    private String shopId;
    /**
     * 区域ID
     */
    private String regionId;
    /**
     * 品牌ID
     */
    private String brandId;
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
}

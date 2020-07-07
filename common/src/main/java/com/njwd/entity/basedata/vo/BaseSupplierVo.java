package com.njwd.entity.basedata.vo;

import com.njwd.entity.basedata.dto.BaseShopTypeDto;
import com.njwd.entity.basedata.dto.BaseSupplierDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 供应商信息
 * @Author: LuoY
 * @Date: 2020/3/27 14:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseSupplierVo extends BaseSupplierDto {
    /**
     * 区域ID
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 品牌ID
     */
    private String brandId;

    /**
     * 品牌名称
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
}

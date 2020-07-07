package com.njwd.entity.basedata.vo;

import com.njwd.entity.basedata.BaseRegion;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/12/30 10:31
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class BaseRegionVo extends BaseRegion {
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 品牌集合
     */
    private List<BaseBrandVo> regionList;
    /**
     * 门店集合
     */
    private List<BaseShopVo> shopList;
}

package com.njwd.entity.basedata.vo;

import com.njwd.entity.basedata.BaseBrand;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/12/30 10:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseBrandVo extends BaseBrand {
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 区域集合
     */
    private List<BaseRegionVo> regionList;
    /**
     * 门店集合
     */
    private List<BaseShopVo> shopList;
}

package com.njwd.entity.admin.vo;

import lombok.Data;

/**
 * @Description:组织机构门店
 * @Author: yuanman
 * @Date: 2020/1/7 15:08
 */
@Data
public class OrganShopVo {
    /**
     *门店id
     */
    private String shopId;
    /**
     *门店编码
     */
    private String shopNo;
    /**
     * 门店名称
     */
    private String shopName;
}

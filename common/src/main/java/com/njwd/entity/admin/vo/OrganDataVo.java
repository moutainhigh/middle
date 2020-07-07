package com.njwd.entity.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:组织机构数据
 * @Author: yuanman
 * @Date: 2020/1/7 11:14
 */
@Data
public class OrganDataVo {
    /**
     * 区域集合
     */
    List<OrganRegionVo> regions;
    /**
     * 品牌集合
     */
    List<OrganBrandVo> brands;

}

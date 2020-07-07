package com.njwd.entity.basedata.vo;

import com.njwd.entity.basedata.BaseAccountBook;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseAccountBookVo extends BaseAccountBook {

    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 门店机构类型
     */
    private String shopTypeId;
}

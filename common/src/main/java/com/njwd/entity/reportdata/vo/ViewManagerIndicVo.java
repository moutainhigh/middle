package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 看板（店长视角）查询数据（指标源数据）
 *
 * @author shenhf
 * @date 2020-03-25 18:02
 */
@Data
public class ViewManagerIndicVo {
    private static final long serialVersionUID = 8293579388252687383L;
    /*
     * 品牌id
     * */
    private String brandId = "";
    /*
    * 门店id
    * */
    private String shopId = "";
    /*
     * 门店名称
     * */
    private String shopName = "";
    /**
     * 指标源数据
     */
    private BigDecimal indicAmount = new BigDecimal(0);


}


package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
* @Description: 啤酒进厂费Vo
* @Author: LuoY
* @Date: 2020/3/27 11:43
*/
@Data
public class BearIntoFactoryVo {
    /**
    * 数据类型
    */
    private String type;
    /**
    * 品牌编码
    */
    private String brandNo;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 大区编码
     */
    private String regionNo;

    /**
     * 大区名称
     */
    private String regionName;

    /**
    * 门店编码
    */
    private String shopNo;

    /**
     * 门店名称
     */
    private String shopName;

    /**
    * 门店合计进场费
    */
    private BigDecimal countMoney;

    /**
    * 供应商信息
    */
    List<BearIntoSupperInfoVo> bearIntoSupperInfoVos;
}

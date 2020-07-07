package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
* @Description: 啤酒进场费供应商信息
* @Author: LuoY
* @Date: 2020/3/27 13:34
*/
@Data
public class BearIntoFactorySupplierVo {
    /**
     * 物料编码
     */
    private String materialCode;

    /**
     * 物料名称
     */
    private String materialName;

    /**
     * 物料数量
     */
    private BigDecimal materialNum;

    /**
     * 物料进场费
     */
    private BigDecimal materialIntoMoney;

    /**
    * 数据类型
    */
    private Integer dataType;
}

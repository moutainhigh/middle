package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
* @Description: 啤酒进厂费供应商信息
* @Author: LuoY
* @Date: 2020/3/27 15:34
*/
@Data
public class BearIntoSupperInfoVo {
    /**
    * 供应商id
    */
    private String supplierId;

    /**
     * 供应商编码
     */
    private String supplierNo;

    /**
    * 供应商名称
    */
    private String supplierName;

    /**
     * 数量合计
     */
    private BigDecimal materialCount;

    /**
     * 进场费
     */
    private BigDecimal bearIntoMoneySum;

    /**
     * 数量数据类型
     */
    private Integer numDataType;

    /**
     * 数量数据类型
     */
    private Integer moneyDataType;

    /**
     * 供应商物料明细信息
     */
    List<BearIntoFactorySupplierVo> bearIntoFactorySupplierVos;
}

package com.njwd.basedata.service;

import com.njwd.entity.basedata.dto.BaseSupplierDto;
import com.njwd.entity.basedata.vo.BaseSupplierVo;

import java.util.List;

/**
* @Description: 供应商server
* @Author: LuoY
* @Date: 2020/3/27 14:36
*/
public interface BaseSupplierService{
    /**
    * @Description: 根据门店id和供应商编码查询供应商信息
    * @Param: [supplierDto]
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseSupplierVo>
    * @Author: LuoY
    * @Date: 2020/3/27 14:37
    */
    List<BaseSupplierVo> findSupplierByShopAndSupplierCode(BaseSupplierDto supplierDto);
}

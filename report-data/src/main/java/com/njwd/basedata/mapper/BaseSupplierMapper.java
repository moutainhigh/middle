package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.BaseSupplier;
import com.njwd.entity.basedata.dto.BaseSupplierDto;
import com.njwd.entity.basedata.vo.BaseSupplierVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @Description:供应商mapper
* @Author: LuoY
* @Date: 2020/3/27 14:26
*/
@Repository
public interface BaseSupplierMapper extends BaseMapper<BaseSupplier> {
    /** 
    * @Description: 根据供应商编码和门店信息查询供应商数据
    * @Param: [supplierDto] 
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseSupplierVo> 
    * @Author: LuoY
    * @Date: 2020/3/27 14:40
    */ 
    List<BaseSupplierVo> findBaseSupplierByShopIdAndSupplierCode(BaseSupplierDto supplierDto);
}
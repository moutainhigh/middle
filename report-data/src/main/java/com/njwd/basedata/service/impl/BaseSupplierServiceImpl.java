package com.njwd.basedata.service.impl;

import com.njwd.entity.basedata.dto.BaseSupplierDto;
import com.njwd.entity.basedata.vo.BaseSupplierVo;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.njwd.basedata.mapper.BaseSupplierMapper;
import com.njwd.basedata.service.BaseSupplierService;

import java.util.List;

/**
* @Description:供应商service实现
* @Author: LuoY
* @Date: 2020/3/27 14:28
*/
@Service
public class BaseSupplierServiceImpl implements BaseSupplierService{

    @Resource
    private BaseSupplierMapper baseSupplierMapper;

    @Override
    public List<BaseSupplierVo> findSupplierByShopAndSupplierCode(BaseSupplierDto supplierDto) {
        return baseSupplierMapper.findBaseSupplierByShopIdAndSupplierCode(supplierDto);
    }
}

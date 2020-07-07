package com.njwd.basedata.service.impl;

import com.njwd.basedata.service.BaseShopTypeService;
import com.njwd.basedata.mapper.BaseShopTypeMapper;
import com.njwd.entity.basedata.dto.BaseShopTypeDto;
import com.njwd.entity.basedata.vo.BaseOrderTypeVo;
import com.njwd.entity.basedata.vo.BaseShopTypeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description: 门店类型实现类
* @Author: LuoY
* @Date: 2020/1/7 17:40
*/
@Service
public class BaseShopTypeServiceImpl implements BaseShopTypeService {

    @Resource
    private BaseShopTypeMapper baseShopTypeMapper;

    /** 
    * @Description: 根据企业id查询门店类型 
    * @Param: [baseShopTypeDto] 
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseShopTypeVo> 
    * @Author: LuoY
    * @Date: 2020/1/7 19:35
    */ 
    @Override
    public List<BaseShopTypeVo> findBaseShopTypeByEnteId(BaseShopTypeDto baseShopTypeDto) {
        return baseShopTypeMapper.findAllBaseShopTypeByEnteId(baseShopTypeDto);
    }
    /**
     * @Description: 根据企业id查询门店类型
     * @Param: [enteId]
     * @return: java.util.List<com.njwd.entity.basedata.vo.BaseOrderTypeVo>
     * @Author: shenhf
     * @Date: 2020/1/7 17:55
     */
    @Override
    public List<BaseOrderTypeVo> findOrderTypeInfoByEnteId(String enteId) {
        return baseShopTypeMapper.findOrderTypeInfoByEnteId(enteId);
    }
}

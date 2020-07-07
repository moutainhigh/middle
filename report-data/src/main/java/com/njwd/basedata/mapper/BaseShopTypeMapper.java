package com.njwd.basedata.mapper;

import com.njwd.entity.basedata.dto.BaseShopTypeDto;
import com.njwd.entity.basedata.vo.BaseOrderTypeVo;
import com.njwd.entity.basedata.vo.BaseShopTypeVo;

import java.util.List;

/**
* @Description: 门店类型mapper
* @Author: LuoY
* @Date: 2020/1/7 17:41
*/
public interface BaseShopTypeMapper {
    /** 
    * @Description: 根据企业id查询门店类型
    * @Param: [baseShopTypeDto] 
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseShopTypeVo> 
    * @Author: LuoY
    * @Date: 2020/1/7 17:49
    */ 
    List<BaseShopTypeVo> findAllBaseShopTypeByEnteId(BaseShopTypeDto baseShopTypeDto);
    /**
     * @Description: 根据企业id查询门店类型
     * @Param: [enteId]
     * @return: java.util.List<com.njwd.entity.basedata.vo.BaseOrderTypeVo>
     * @Author: shenhf
     * @Date: 2020/1/7 17:55
     */
    List<BaseOrderTypeVo> findOrderTypeInfoByEnteId(String enteId);

}
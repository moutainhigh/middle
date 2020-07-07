package com.njwd.basedata.service;

import com.njwd.entity.basedata.dto.BaseShopTypeDto;
import com.njwd.entity.basedata.vo.BaseOrderTypeVo;
import com.njwd.entity.basedata.vo.BaseShopTypeVo;

import java.util.List;

/**
* @Description: 门店类型接口
* @Author: LuoY
* @Date: 2020/1/7 17:40
*/
public interface BaseShopTypeService{
    /**
    * @Description: 根据企业id查询门店类型
    * @Param: [baseShopTypeDto]
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseShopTypeVo>
    * @Author: LuoY
    * @Date: 2020/1/7 17:55
    */
    List<BaseShopTypeVo> findBaseShopTypeByEnteId(BaseShopTypeDto baseShopTypeDto);
    /**
     * @Description: 根据企业id查询门店类型
     * @Param: [enteId]
     * @return: java.util.List<com.njwd.entity.basedata.vo.BaseOrderTypeVo>
     * @Author: shenhf
     * @Date: 2020/1/7 17:55
     */
    List<BaseOrderTypeVo> findOrderTypeInfoByEnteId(String  enteId);
}

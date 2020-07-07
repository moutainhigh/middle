package com.njwd.basedata.service;

import com.njwd.entity.basedata.dto.BasePayCategoryDto;
import com.njwd.entity.basedata.vo.BasePayCategoryVo;

import java.util.List;

/**
 * @Description: 支付方式
 * @Author LuoY
 * @Date 2019/12/9
 */
public interface BasePayCategoryService {

    /**
     * @Author LuoY
     * @Description 根据企业id查询所有支付方式
     * @Date 2019/12/9 11:53
     * @Param [basePayCategoryDto]
     * @return java.util.List<com.njwd.entity.basedata.vo.BasePayCategoryVo>
     **/
    List<BasePayCategoryVo> findBasePayCategoryInfoByEnteId(BasePayCategoryDto basePayCategoryDto);
}

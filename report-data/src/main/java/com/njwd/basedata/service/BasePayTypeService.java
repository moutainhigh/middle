package com.njwd.basedata.service;

import com.njwd.entity.basedata.dto.BasePayTypeDto;
import com.njwd.entity.basedata.vo.BasePayTypeVo;

import java.util.List;

/**
 * @Description: 支付方式
 * @Author LuoY
 * @Date 2019/12/4
 */
public interface BasePayTypeService {

    /**
     * @Author LuoY
     * @Description 根据企业id查询支付方式
     * @Date 2019/12/4 11:39
     * @Param [basePayTypeDto]
     * @return java.util.List<com.njwd.entity.kettlejob.vo.BasePayTypeRelaVo>
     **/
    List<BasePayTypeVo> findBasePayTypeInfoByEnteId(BasePayTypeDto basePayTypeDto);
}

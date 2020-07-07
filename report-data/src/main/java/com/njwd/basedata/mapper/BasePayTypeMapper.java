package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.BasePayType;
import com.njwd.entity.basedata.dto.BasePayTypeDto;
import com.njwd.entity.basedata.vo.BasePayTypeVo;

import java.util.List;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/12/4
 */
public interface BasePayTypeMapper extends BaseMapper<BasePayType> {
    /**
     * @Author LuoY
     * @Description 根据企业id查询支付方式
     * @Date 2019/12/4 11:39
     * @Param [basePayTypeDto]
     * @return java.util.List<com.njwd.entity.kettlejob.vo.BasePayTypeRelaVo>
     **/
    List<BasePayTypeVo> findBasePayTypeInfoByEnteId(BasePayTypeDto basePayTypeDto);
}

package com.njwd.basedata.service.impl;

import com.njwd.basedata.mapper.BasePayTypeMapper;
import com.njwd.basedata.service.BasePayTypeService;
import com.njwd.entity.basedata.dto.BasePayTypeDto;
import com.njwd.entity.basedata.vo.BasePayTypeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 支付方式
 * @Author LuoY
 * @Date 2019/12/4
 */
@Service
public class BasePayTypeServiceImpl implements BasePayTypeService {
    @Resource
    private BasePayTypeMapper basePayTypeMapper;

    @Override
    public List<BasePayTypeVo> findBasePayTypeInfoByEnteId(BasePayTypeDto basePayTypeDto) {
        return basePayTypeMapper.findBasePayTypeInfoByEnteId(basePayTypeDto);
    }
}

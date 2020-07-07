package com.njwd.basedata.service.impl;

import com.njwd.basedata.mapper.BasePayCategoryMapper;
import com.njwd.basedata.service.BasePayCategoryService;
import com.njwd.entity.basedata.dto.BasePayCategoryDto;
import com.njwd.entity.basedata.vo.BasePayCategoryVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 支付方式
 * @Author LuoY
 * @Date 2019/12/9
 */
@Service
public class BasePayCategoryServiceImpl implements BasePayCategoryService {
    @Resource
    private BasePayCategoryMapper basePayCategoryMapper;
    /**
     * @Author LuoY
 * @Description 根据企业id查询支付类型想信息
     * @Date 2019/12/9 12:00
     * @Param [basePayCategoryDto]
     * @return java.util.List<com.njwd.entity.basedata.vo.BasePayCategoryVo>
     **/
    @Override
    public List<BasePayCategoryVo> findBasePayCategoryInfoByEnteId(BasePayCategoryDto basePayCategoryDto) {
        return basePayCategoryMapper.findBasePayCategoryInfoByEnteId(basePayCategoryDto);
    }
}

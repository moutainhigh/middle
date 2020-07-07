package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.BasePayCategory;
import com.njwd.entity.basedata.dto.BasePayCategoryDto;
import com.njwd.entity.basedata.vo.BasePayCategoryVo;

import java.util.List;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/12/9
 */
public interface BasePayCategoryMapper extends BaseMapper<BasePayCategory> {
    /**
     * @Author LuoY
     * @Description 根据企业id查询支付类型信息
     * @Date 2019/12/9 11:57
     * @Param [basePayCategoryDto]
     * @return java.util.List<com.njwd.entity.basedata.vo.BasePayCategoryVo>
     **/
    List<BasePayCategoryVo> findBasePayCategoryInfoByEnteId(BasePayCategoryDto basePayCategoryDto);
}

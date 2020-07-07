package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.ThreRetreatFood;
import com.njwd.entity.basedata.dto.ThreRetreatFoodDto;

import java.util.List;

/**
* @Description:  退菜指标mapper
* @Author: LuoY
* @Date: 2020/1/20 17:57
*/
public interface ThreRetreatFoodMapper extends BaseMapper<ThreRetreatFood> {
    /**
     * @Description: 初始化退菜明细指标
     * @Param: [retreatFoodDto]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/1/20 18:47
     */
    List<ThreRetreatFood> findThreRetreaFoodInfo(ThreRetreatFoodDto retreatFoodDto);
}
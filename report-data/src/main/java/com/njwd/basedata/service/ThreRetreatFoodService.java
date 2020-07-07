package com.njwd.basedata.service;

import com.njwd.entity.basedata.ThreRetreatFood;
import com.njwd.entity.basedata.dto.ThreRetreatFoodDto;

import java.util.List;

/**
* @Description: 退菜指標
* @Author: LuoY
* @Date: 2020/1/20 17:46
*/
public interface ThreRetreatFoodService {
    /** 
    * @Description: 初始化退菜指标
    * @Param: [threRetreatFoodDto] 
    * @return: void 
    * @Author: LuoY
    * @Date: 2020/1/20 17:56
    */ 
    void initWdThreRetreaFoodInfo(ThreRetreatFoodDto threRetreatFoodDto);
}

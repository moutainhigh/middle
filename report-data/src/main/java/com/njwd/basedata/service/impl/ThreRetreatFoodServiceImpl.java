package com.njwd.basedata.service.impl;

import com.njwd.basedata.mapper.ThreRetreatFoodMapper;
import com.njwd.entity.basedata.ThreRetreatFood;
import com.njwd.entity.basedata.dto.ThreRetreatFoodDto;
import org.springframework.stereotype.Service;
import com.njwd.basedata.service.ThreRetreatFoodService;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
* @Description: 退菜指标
* @Author: LuoY
* @Date: 2020/1/20 18:02
*/
@Service
public class ThreRetreatFoodServiceImpl implements ThreRetreatFoodService {
    @Resource
    private ThreRetreatFoodMapper threRetreatFoodMapper;

    /** 
    * @Description: 初始化退菜指标
    * @Param: [threRetreatFoodDto] 
    * @return: void 
    * @Author: LuoY
    * @Date: 2020/1/20 17:58
    */ 
    @Override
    public void initWdThreRetreaFoodInfo(ThreRetreatFoodDto threRetreatFoodDto) {
        //先查询指标
        List<ThreRetreatFood> threRetreatFoods = threRetreatFoodMapper.findThreRetreaFoodInfo(threRetreatFoodDto);
        List<ThreRetreatFoodDto> threRetreatFoodDtos = new LinkedList<>();
        threRetreatFoods.forEach(data->{
            threRetreatFoodMapper.insert(data);
        });
    }
}

package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.RepPosDetailFoodDto;
import com.njwd.entity.reportdata.vo.RepPosDetailFoodVo;

import java.util.List;

/**
* @Description: 销售菜品明细表
* @Author: LuoY
* @Date: 2020/1/2 19:05
*/
public interface RepPosDetailFoodService{
    /** 
    * @Description: 查询销售菜品明细
    * @Param: [repPosDetailFoodDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailFoodVo> 
    * @Author: LuoY
    * @Date: 2020/1/2 19:21
    */ 
    List<RepPosDetailFoodVo> findRepPosDetailFoodByCondition(RepPosDetailFoodDto repPosDetailFoodDto);
    
    /** 
    * @Description: 查询销售菜品金额
    * @Param: [repPosDetailFoodDto] 
    * @return: com.njwd.entity.reportdata.vo.RepPosDetailFoodVo 
    * @Author: LuoY
    * @Date: 2020/1/15 15:25
    */ 
    RepPosDetailFoodVo findFoodAllPrice(RepPosDetailFoodDto repPosDetailFoodDto);

    /** 
    * @Description: 查询菜品赠送金额
    * @Param: [repPosDetailFoodDto] 
    * @return: com.njwd.entity.reportdata.vo.RepPosDetailFoodVo 
    * @Author: LuoY
    * @Date: 2020/1/15 17:34
    */ 
    RepPosDetailFoodVo findGiveFoodPrice(RepPosDetailFoodDto repPosDetailFoodDto);

    /**
     * @Description: 查询销售菜品明细根据门店分组
     * @Param: [repPosDetailFoodDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailFoodVo>
     * @Author: liBao
     * @Date: 2020/3/1 19:21
     */
    List<RepPosDetailFoodVo> findRepPosDetailFoodList(RepPosDetailFoodDto repPosDetailFoodDto);
}

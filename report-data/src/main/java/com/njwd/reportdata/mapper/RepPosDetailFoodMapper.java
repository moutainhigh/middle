package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.RepPosDetailFood;
import com.njwd.entity.reportdata.dto.RepPosDetailFoodDto;
import com.njwd.entity.reportdata.vo.RepPosDetailFoodVo;

import java.util.List;

/**
* @Description: 销售菜品明细表
* @Author: LuoY
* @Date: 2020/1/2 17:40
*/
public interface RepPosDetailFoodMapper extends BaseMapper<RepPosDetailFood> {
    /** 
    * @Description: 查询销售菜品明细表
    * @Param: [repPosDetailPayDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailFoodVo> 
    * @Author: LuoY
    * @Date: 2020/1/2 17:47
    */ 
    List<RepPosDetailFoodVo> findRepPosDetailFoodByCondition(RepPosDetailFoodDto repPosDetailFoodDto);

    /** 
    * @Description: 查询菜品收入
    * @Param: [repPosDetailFoodDto] 
    * @return: com.njwd.entity.reportdata.vo.RepPosDetailFoodVo 
    * @Author: LuoY
    * @Date: 2020/1/15 15:29
    */ 
    RepPosDetailFoodVo findFoodAllPrice(RepPosDetailFoodDto repPosDetailFoodDto);

    /**
    * @Description: 查询菜品赠送金额
    * @Param: [repPosDetailFoodDto]
    * @return: com.njwd.entity.reportdata.vo.RepPosDetailFoodVo
    * @Author: LuoY
    * @Date: 2020/1/15 17:24
    */
    RepPosDetailFoodVo findGiveFoodPrice(RepPosDetailFoodDto repPosDetailFoodDto);

    /**
     * @Description: 查询销售菜品明细表
     * @Param: [repPosDetailPayDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailFoodVo>
     * @Author: liBao
     * @Date: 2020/2/29 17:47
     */
    List<RepPosDetailFoodVo> findRepPosDetailFoodList(RepPosDetailFoodDto repPosDetailFoodDto);
}
package com.njwd.reportdata.service.impl;

import com.njwd.entity.reportdata.dto.RepPosDetailFoodDto;
import com.njwd.entity.reportdata.vo.RepPosDetailFoodVo;
import com.njwd.reportdata.mapper.RepPosDetailFoodMapper;
import com.njwd.reportdata.service.RepPosDetailFoodService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description:  菜品明细报表表
* @Author: LuoY
* @Date: 2020/1/15 17:26
*/
@Service
public class RepPosDetailFoodServiceImpl implements RepPosDetailFoodService {

    @Resource
    private RepPosDetailFoodMapper repPosDetailFoodMapper;

    @Override
    public List<RepPosDetailFoodVo> findRepPosDetailFoodByCondition(RepPosDetailFoodDto repPosDetailFoodDto) {
        return repPosDetailFoodMapper.findRepPosDetailFoodByCondition(repPosDetailFoodDto);
    }

    @Override
    public RepPosDetailFoodVo findFoodAllPrice(RepPosDetailFoodDto repPosDetailFoodDto) {
        return repPosDetailFoodMapper.findFoodAllPrice(repPosDetailFoodDto);
    }

    @Override
    public RepPosDetailFoodVo findGiveFoodPrice(RepPosDetailFoodDto repPosDetailFoodDto) {
        return repPosDetailFoodMapper.findGiveFoodPrice(repPosDetailFoodDto);
    }

    /**
     * @param repPosDetailFoodDto
     * @Description: 查询销售菜品明细根据门店分组
     * @Param: [repPosDetailFoodDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailFoodVo>
     * @Author: LuoY
     * @Date: 2020/1/2 19:21
     */
    @Override
    public List<RepPosDetailFoodVo> findRepPosDetailFoodList(RepPosDetailFoodDto repPosDetailFoodDto) {
        return repPosDetailFoodMapper.findRepPosDetailFoodList(repPosDetailFoodDto);
    }

}

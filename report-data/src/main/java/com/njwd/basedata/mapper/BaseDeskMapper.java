package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.BaseDesk;
import com.njwd.entity.basedata.dto.BaseDeskDto;
import com.njwd.entity.basedata.vo.BaseDeskVo;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;

import java.util.List;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/12/7
 */
public interface BaseDeskMapper extends BaseMapper<BaseDesk> {
    /**
     * @Author LuoY
     * @Description 根据组织查询台位数
     * @Date 2019/12/7 14:26
     * @Param [baseDeskDto]
     * @return int
     **/
    List<BaseDeskVo> findDeskCountByOrgId(BaseDeskDto baseDeskDto);

    /** 
    * @Description: 根据门店id查询桌数
    * @Param: [baseDeskDto] 
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseDeskVo> 
    * @Author: LuoY
    * @Date: 2020/1/9 10:06
    */ 
    BaseDeskVo findBaseDeskCountByShopId(BaseDeskDto baseDeskDto);

    /**
     * @Description: 根据门店查询销售额
     * @Param: [queryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.FinRentAccountedForVo>
     * @Author: liBao
     * @Date: 2020/3/2
     */
    List<FinRentAccountedForVo> findSaleByCondition(FinQueryDto queryDto);
}

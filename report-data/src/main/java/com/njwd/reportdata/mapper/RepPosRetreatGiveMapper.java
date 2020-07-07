package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.RepPosRetreatGive;
import com.njwd.entity.reportdata.dto.RepPosRetreatGiveDto;
import com.njwd.entity.reportdata.vo.RepPosRetreatGiveVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @Description: 退增统计表
* @Author: LuoY
* @Date: 2020/3/18 13:47
*/
@Repository
public interface RepPosRetreatGiveMapper extends BaseMapper<RepPosRetreatGive> {
    /** 
    * @Description: 根据条件查询门店的赠送金额
    * @Param: [repPosRetreatGiveDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosRetreatGiveVo> 
    * @Author: LuoY
    * @Date: 2020/3/18 11:30
    */ 
    List<RepPosRetreatGiveVo> findWaitAndOutTimeMoney(RepPosRetreatGiveDto repPosRetreatGiveDto);
}

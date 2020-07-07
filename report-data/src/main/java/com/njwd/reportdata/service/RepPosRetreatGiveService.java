package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.RepPosRetreatGiveDto;
import com.njwd.entity.reportdata.vo.RepPosRetreatGiveVo;

import java.util.List;

/**
* @Description: pos赠送报表表
* @Author: LuoY
* @Date: 2020/3/18 11:18
*/
public interface RepPosRetreatGiveService {
    /**
    * @Description: 根据条件查询赠送金额
    * @Param: [repPosRetreatGiveDto]
    * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosRetreatGiveVo>
    * @Author: LuoY
    * @Date: 2020/3/18 11:26
    */
    List<RepPosRetreatGiveVo> findWaitAndOutTimeMoney(RepPosRetreatGiveDto repPosRetreatGiveDto);
}

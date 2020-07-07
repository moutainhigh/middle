package com.njwd.reportdata.service.impl;

import com.njwd.entity.reportdata.dto.RepPosRetreatGiveDto;
import com.njwd.entity.reportdata.vo.RepPosRetreatGiveVo;
import com.njwd.reportdata.mapper.RepPosRetreatGiveMapper;
import com.njwd.reportdata.service.RepPosRetreatGiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description: 赠退报表表
* @Author: LuoY
* @Date: 2020/3/18 11:25
*/
@Service
public class RepPosRetreatGiveServiceImpl implements RepPosRetreatGiveService {
    @Resource
    private RepPosRetreatGiveMapper repPosRetreatGiveMapper;

    /** 
    * @Description: 根据条件查询具体门店的赠送金额明细
    * @Param: [repPosRetreatGiveDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosRetreatGiveVo> 
    * @Author: LuoY
    * @Date: 2020/3/18 11:30
    */ 
    @Override
    public List<RepPosRetreatGiveVo> findWaitAndOutTimeMoney(RepPosRetreatGiveDto repPosRetreatGiveDto) {
        return repPosRetreatGiveMapper.findWaitAndOutTimeMoney(repPosRetreatGiveDto);
    }
}

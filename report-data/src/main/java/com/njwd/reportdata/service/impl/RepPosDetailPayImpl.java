package com.njwd.reportdata.service.impl;

import com.njwd.entity.reportdata.dto.RepPosDetailPayDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.RepPosDetailPayVo;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;
import com.njwd.reportdata.mapper.RepPosDetailPayMapper;
import com.njwd.reportdata.service.RepPosDetailPayService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description: 支付方式明细表
* @Author: LuoY
* @Date: 2020/1/2 15:18
*/
@Service
public class RepPosDetailPayImpl implements RepPosDetailPayService {
    @Resource
    private RepPosDetailPayMapper repPosDetailPayMapper;
    
    /** 
    * @Description: 查询支付方式明细表
    * @Param: [repPosDetailPayDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailPayVo> 
    * @Author: LuoY
    * @Date: 2020/1/2 15:19
    */ 
    @Override
    public List<RepPosDetailPayVo> findRepPosDetailPayVoInfoByCondition(RepPosDetailPayDto repPosDetailPayDto) {
        return repPosDetailPayMapper.findRepPosDetailPayInfoByCondition(repPosDetailPayDto);
    }

    @Override
    public List<FinRentAccountedForVo> findRepPosPayVoInfoByCondition(FinQueryDto queryDto) {
        return repPosDetailPayMapper.findRepPosPayVoInfoByCondition(queryDto);
    }


}

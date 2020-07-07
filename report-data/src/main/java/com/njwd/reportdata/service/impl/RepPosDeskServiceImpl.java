package com.njwd.reportdata.service.impl;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.vo.StatisticsTurnoverRateVo;
import com.njwd.reportdata.mapper.RepPosDeskMapper;
import com.njwd.reportdata.service.RepPosDeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/12/30 14:55
 */
@Service
public class RepPosDeskServiceImpl implements RepPosDeskService{

    @Autowired
    private RepPosDeskMapper repPosDeskMapper;

    @Override
    public List<StatisticsTurnoverRateVo> findDeskNumByShop(BaseQueryDto baseQueryDto) {
        return repPosDeskMapper.findDeskNumByShop(baseQueryDto);
    }

    @Override
    public List<StatisticsTurnoverRateVo> findStationsNumByShop(BaseQueryDto baseQueryDto) {
        return repPosDeskMapper.findStationsNumByShop(baseQueryDto);
    }
}

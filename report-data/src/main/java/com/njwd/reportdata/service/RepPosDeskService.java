package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.vo.StatisticsTurnoverRateVo;

import java.util.List;

public interface RepPosDeskService {

    List<StatisticsTurnoverRateVo> findDeskNumByShop(BaseQueryDto baseQueryDto);

    List<StatisticsTurnoverRateVo> findStationsNumByShop(BaseQueryDto baseQueryDto);
}

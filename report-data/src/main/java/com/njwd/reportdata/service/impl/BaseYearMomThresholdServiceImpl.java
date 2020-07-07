package com.njwd.reportdata.service.impl;

import com.njwd.entity.reportdata.dto.BaseYearMomThresholdDto;
import com.njwd.entity.reportdata.vo.BaseYearMomThresholdVo;
import com.njwd.reportdata.mapper.BaseYearMomThresholdMapper;
import com.njwd.reportdata.service.BaseYearMomThresholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/1/11 18:27
 */
@Service
public class BaseYearMomThresholdServiceImpl implements BaseYearMomThresholdService {

    @Autowired
    private BaseYearMomThresholdMapper baseYearMomThresholdMapper;

    /**
     * @Author ZhuHC
     * @Date  2020/1/11 20:55
     * @Param [dto]
     * @return com.njwd.entity.reportdata.vo.BaseYearMomThresholdVo
     * @Description 查询同比环比阀值
     */
    @Override
    public BaseYearMomThresholdVo findBaseYearMomThresholdByCode(BaseYearMomThresholdDto dto) {
        return baseYearMomThresholdMapper.findBaseYearMomThresholdByCode(dto);
    }
}

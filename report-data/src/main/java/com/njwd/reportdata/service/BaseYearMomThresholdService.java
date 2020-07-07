package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.BaseYearMomThresholdDto;
import com.njwd.entity.reportdata.vo.BaseYearMomThresholdVo;

/**
 * @Author ZhuHC
 * @Date  2020/1/11 18:26
 * @Description
 */
public interface BaseYearMomThresholdService {

    /**
     * @Author ZhuHC
     * @Date  2020/1/11 20:55
     * @Param [dto]
     * @return BaseYearMomThresholdVo
     * @Description 查询同比环比阀值
     */
    BaseYearMomThresholdVo findBaseYearMomThresholdByCode(BaseYearMomThresholdDto dto);

}

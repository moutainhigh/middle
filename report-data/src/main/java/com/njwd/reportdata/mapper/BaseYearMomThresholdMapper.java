package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.dto.BaseYearMomThresholdDto;
import com.njwd.entity.reportdata.vo.BaseYearMomThresholdVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author ZhuHC
 * @Date  2020/1/11 18:28
 * @Description
 */
@Repository
public interface BaseYearMomThresholdMapper {

    /**
     * @Author ZhuHC
     * @Date  2020/1/11 20:55
     * @Param [dto]
     * @return BaseYearMomThresholdVo
     * @Description 查询同比环比阀值
     */
    BaseYearMomThresholdVo findBaseYearMomThresholdByCode(@Param("dto")BaseYearMomThresholdDto dto);
}

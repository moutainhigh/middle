package com.njwd.basedata.service;

import com.njwd.entity.basedata.dto.BaseDateUtilInfoDto;
import com.njwd.entity.basedata.vo.BaseDateUtilInfoVo;

import java.util.List;

/**
 * @Description: 节假日信息
 * @Author LuoY
 * @Date 2019/12/5
 */
public interface BaseDateUtilInfoService {
    /**
     * @Author LuoY
     * @Description 根据条件查询时间信息
     * @Date 2019/12/5 13:51
     * @Param [baseDateUtilInfoDto]
     * @return java.util.List<com.njwd.entity.basedata.vo.BaseDateUtilInfoVo>
     **/
    List<BaseDateUtilInfoVo> findBaseDateUtilInfoByCondition(BaseDateUtilInfoDto baseDateUtilInfoDto);
}

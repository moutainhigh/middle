package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.BaseDateUtilInfo;
import com.njwd.entity.basedata.dto.BaseDateUtilInfoDto;
import com.njwd.entity.basedata.vo.BaseDateUtilInfoVo;

import java.util.List;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/12/5
 */
public interface BaseDateUtilInfoMapper extends BaseMapper<BaseDateUtilInfo> {

    /**
     * @Author LuoY
     * @Description 查询指定范围时间
     * @Date 2019/12/5 13:41
     * @Param [baseDateUtilInfoDto]
     * @return java.util.List<com.njwd.entity.basedata.vo.BaseDateUtilInfoVo>
     **/
    List<BaseDateUtilInfoVo> findBaseUtilInfoByCondition(BaseDateUtilInfoDto baseDateUtilInfoDto);
}

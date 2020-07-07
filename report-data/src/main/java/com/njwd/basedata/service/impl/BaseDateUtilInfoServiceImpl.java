package com.njwd.basedata.service.impl;

import com.njwd.basedata.mapper.BaseDateUtilInfoMapper;
import com.njwd.basedata.service.BaseDateUtilInfoService;
import com.njwd.entity.basedata.dto.BaseDateUtilInfoDto;
import com.njwd.entity.basedata.vo.BaseDateUtilInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/12/5
 */
@Service
public class BaseDateUtilInfoServiceImpl implements BaseDateUtilInfoService {
    @Resource
    private BaseDateUtilInfoMapper baseDateUtilInfoMapper;

    @Override
    public List<BaseDateUtilInfoVo> findBaseDateUtilInfoByCondition(BaseDateUtilInfoDto baseDateUtilInfoDto){
       return baseDateUtilInfoMapper.findBaseUtilInfoByCondition(baseDateUtilInfoDto);
    }
}

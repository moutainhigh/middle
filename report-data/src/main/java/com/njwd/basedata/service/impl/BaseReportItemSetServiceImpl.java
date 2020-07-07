package com.njwd.basedata.service.impl;

import com.njwd.basedata.mapper.BaseReportItemSetMapper;
import com.njwd.basedata.service.BaseReportItemSetService;
import com.njwd.entity.basedata.dto.BaseReportItemSetDto;
import com.njwd.entity.basedata.vo.BaseReportItemSetVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description: 报表项目配置项impl
* @Author: LuoY
* @Date: 2019/12/29 13:57
*/
@Service
public class BaseReportItemSetServiceImpl implements BaseReportItemSetService {
    @Resource
    private BaseReportItemSetMapper baseReportItemSetMapper;
    
    /** 
    * @Description: 根据报表id查询项目配置项
    * @Param: [baseReportItemSetDto] 
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseReportItemSetVo> 
    * @Author: LuoY
    * @Date: 2019/12/29 14:09
    */ 
    @Override
    public List<BaseReportItemSetVo> findBaseReportItemSetVoByReportId(BaseReportItemSetDto baseReportItemSetDto) {
        return baseReportItemSetMapper.findBaseReportItemSetByCondition(baseReportItemSetDto);
    }
}

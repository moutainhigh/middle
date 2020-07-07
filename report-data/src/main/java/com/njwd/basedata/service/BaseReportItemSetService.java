package com.njwd.basedata.service;

import com.njwd.entity.basedata.dto.BaseReportItemSetDto;
import com.njwd.entity.basedata.vo.BaseReportItemSetVo;

import java.util.List;

/**
* @Description: 报表项目配置表
* @Author: LuoY
* @Date: 2019/12/29 13:26
*/
public interface BaseReportItemSetService {
    /** 
    * @Description: 根据报表id查询报表项目配置项
    * @Param: [baseReportItemSetDto] 
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseReportItemSetVo> 
    * @Author: LuoY
    * @Date: 2019/12/29 14:55
    */ 
    List<BaseReportItemSetVo> findBaseReportItemSetVoByReportId(BaseReportItemSetDto baseReportItemSetDto);
}

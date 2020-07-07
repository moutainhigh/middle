package com.njwd.reportdata.service;

import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.dto.ScmReportDto;
import com.njwd.entity.reportdata.vo.ScmReportVo;

import java.util.List;

/**
* @Description: 供应链报表配置表
* @Author: LuoY
* @Date: 2020/3/27 13:51
*/
public interface ScmReportService {
    /** 
    * @Description: 根据报表id查询报表配置
    * @Param: [scmReportDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.ScmReportVo> 
    * @Author: LuoY
    * @Date: 2020/3/27 14:02
    */ 
    List<ScmReportVo> findScmReportVoByReportId(ScmReportDto scmReportDto);
}

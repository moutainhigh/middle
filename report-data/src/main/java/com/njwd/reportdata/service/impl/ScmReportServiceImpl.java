package com.njwd.reportdata.service.impl;

import com.njwd.entity.reportdata.dto.ScmReportDto;
import com.njwd.entity.reportdata.vo.ScmReportVo;
import com.njwd.reportdata.mapper.ScmReportMapper;
import com.njwd.reportdata.service.ScmReportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description: 供应链报表配置表
* @Author: LuoY
* @Date: 2020/3/27 13:52
*/
@Service
public class ScmReportServiceImpl implements ScmReportService {
    @Resource
    private ScmReportMapper scmReportMapper;

    /** 
    * @Description: 根据报表id查询报表配置
    * @Param: [scmReportDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.ScmReportVo> 
    * @Author: LuoY
    * @Date: 2020/3/27 14:03
    */ 
    @Override
    public List<ScmReportVo> findScmReportVoByReportId(ScmReportDto scmReportDto){
        return scmReportMapper.findScmReportVoByReportId(scmReportDto);
    }
}

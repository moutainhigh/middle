package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.ScmReport;
import com.njwd.entity.reportdata.dto.ScmReportDto;
import com.njwd.entity.reportdata.vo.ScmReportVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @Description: 供应链报表配置表
* @Author: LuoY
* @Date: 2020/3/27 13:58
*/
@Repository
public interface ScmReportMapper extends BaseMapper<ScmReport> {
    /** 
    * @Description: 根据报表id查询报表配置
    * @Param: [scmReportDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.ScmReportVo> 
    * @Author: LuoY
    * @Date: 2020/3/27 14:04
    */ 
    List<ScmReportVo> findScmReportVoByReportId(ScmReportDto scmReportDto);
}

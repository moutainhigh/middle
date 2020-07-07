package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.BaseReportItemSet;
import com.njwd.entity.basedata.dto.BaseReportItemSetDto;
import com.njwd.entity.basedata.vo.BaseReportItemSetVo;

import java.util.List;

/**
* @Description: 报表项目配置项
* @Author: LuoY
* @Date: 2019/12/29 13:45
*/
public interface BaseReportItemSetMapper extends BaseMapper<BaseReportItemSet> {
    /**
    * @Description:
    * @Param: [baseReportItemSetDto]
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseReportItemSetVo>
    * @Author: LuoY
    * @Date: 2019/12/29 13:46
    */
    List<BaseReportItemSetVo>  findBaseReportItemSetByCondition(BaseReportItemSetDto baseReportItemSetDto);
}

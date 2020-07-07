package com.njwd.entity.reportdata.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *@description: 财务配置表Dto
 *@author: 周鹏
 *@create: 2020-04-20
 */
@Getter
@Setter
public class FinReportConfigDto extends FinReportConfigVo {

    private Page<FinReportConfigVo> page = new Page<>();

    /**
     * id集合
     */
    List<String> idList ;
}

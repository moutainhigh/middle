package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.admin.dto.BenchmarkDto;
import com.njwd.entity.admin.vo.BenchmarkConfigVo;
import com.njwd.entity.admin.vo.BenchmarkVo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 *@description: 报表transfer实体
 *@author: fancl
 *@create: 2020-01-04 
 */
@Setter
@Getter
public class TransferReportDto extends TransferReportSimpleDto {

    //开始时间
    Date startTime;
    //基准配置对象
    BenchmarkVo benchmarkVo;
    //基准config对象
    List<BenchmarkConfigVo> configList;


}

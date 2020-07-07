package com.njwd.entity.reportdata.dto.querydto;

import com.njwd.entity.reportdata.dto.scm.SimpleFoodDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *@description: 简单查询实体
 *@author: fancl
 *@create: 2020-03-28 
 */
@Getter
@Setter
public class SimpleScmQueryDto {
    //央厨code
    String centerCode;
    //组织id
    List<String> orgIds;
    //菜品信息
    List<SimpleFoodDto> foods;
    //是否是央厨
    Boolean isCenter ;

}

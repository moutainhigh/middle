package com.njwd.entity.reportdata.dto.querydto;

import com.njwd.entity.reportdata.dto.scm.SimpleFoodDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *@description: 供应链查询实体
 *@author: fancl
 *@create: 2020-03-25 
 */
@Getter
@Setter
public class ScmQueryDto extends BaseQueryDto{
    //菜品种类名称
    String foodStyleName;
    //子物料id
    List<String> subMaterialIds;
    //子物料编码
    List<String> materialNumbers;
    //包含菜品编码 菜品名称的list
    List<SimpleFoodDto> foods;
    //央厨编码
    String centerCode;
    //本期,同比,环比标识: current(本期)  the_same(同比) chain(环比)
    String chainType;
    //日志开关
    Boolean needLog;
}

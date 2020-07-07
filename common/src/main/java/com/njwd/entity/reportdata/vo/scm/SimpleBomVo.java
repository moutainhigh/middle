package com.njwd.entity.reportdata.vo.scm;

import lombok.Getter;
import lombok.Setter;

/**
 *@description: 简单bomVo
 *@author: fancl
 *@create: 2020-03-24 
 */
@Getter
@Setter
public class SimpleBomVo {
    //菜品no
    String foodNo;
    //菜品名称
    String foodName;
    //父物料id
    String materialId;
    //子物料id
    String subMaterialId;

}

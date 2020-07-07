package com.njwd.entity.reportdata;

import lombok.Getter;
import lombok.Setter;

/**
 *@description: 财务报表配置
 *@author: fancl
 *@create: 2020-01-10 
 */
@Setter
@Getter
public class FinReportConfig {
    //企业id
    private String enteId;
    //品牌名称
    private String brandName;
    //一级组
    private String finGroup;
    //类型 subjects为全部科目,其余为各报表类型那个
    private String finType;
    //科目的列表,以逗号分割
    private String codes;
    //科目类型(自定义),用于代码层面控制
    private String codesType;
    //备注
    private String mem;
    /**
     * 主键id
     */
    private String id;
}

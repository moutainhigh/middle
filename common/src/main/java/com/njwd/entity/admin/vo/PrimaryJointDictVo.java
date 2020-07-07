package com.njwd.entity.admin.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * @Author XiaFq
 * @Description PrimaryJointDictVo 数据统一规则字典数据
 * @Date 2019/11/20 2:06 下午
 * @Version 1.0
 */
@Data
public class PrimaryJointDictVo {

    // 中台字段下拉列表数据
    private List<TableAttributeVo> baseColumnList;

    // rela表字段下拉列表
    private List<TableAttributeVo> relaColumnList;

    // 逻辑关系
    private HashMap<String, String> logicMap;
}

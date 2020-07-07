package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description DependentInfoDto 查询基础表依赖关系信息
 * @Date 2019/11/22 4:54 下午
 * @Version 1.0
 */
@Data
public class DependentInfoDto {
    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 基础表名
     */
    private String baseTableName;
}

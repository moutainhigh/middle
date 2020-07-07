package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description MasterDataAppDto 主数据统一查询字段dto类
 * @Date 2019/11/18 5:06 下午
 * @Version 1.0
 */
@Data
public class MasterDataUnifiedFieldDto {

    /**
     * 表别名
     */
    private String aliasName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 数据类型
     */
    private String dataType;
}

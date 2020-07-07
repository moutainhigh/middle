package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description DataMatchBatch 数据批量匹配 后台任务执行
 * @Date 2019/11/27 2:12 下午
 * @Version 1.0
 */
@Data
public class DataMatchBatchTaskDto {
    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * rela表名
     */
    private String relaTable;

    /**
     * base表名
     */
    private String baseTable;

    /**
     * base表查询字段
     */
    private String baseQueryColumn;

    /**
     * base条件
     */
    private String baseQueryCondition;

    /**
     * rela查询条件
     */
    private String relaQueryCondition;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * on 条件
     */
    private String leftJoinOnCondition;
}

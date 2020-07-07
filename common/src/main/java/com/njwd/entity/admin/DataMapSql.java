package com.njwd.entity.admin;

import lombok.Data;

/**
 * @Description:用于往mybatis传递参数用
 * @Author: yuanman
 * @Date: 2019/11/25 11:38
 */
@Data
public class DataMapSql {
    /**
     *源表
     */
    private String sourceTable;
    /**
     *目标表
     */
    private String targetTable;
    /**
     *关系表
     */
    private String mapTable;
    /**
     *需查询的源表字段
     */
    private String sourceColumns;
    /**
     *需查询的目标表字段
     */
    private String targetColumns;
    /**
     *源表与关系表关联条件
     */
    private String SMCondition;
    /**
     *目标表与关系表关联条件
     */
    private String TMCondition;
    /**
     *主表查询条件
     */
    private String primrayCondition;
    /**
     *groupBy
     */
    private String groupBy;
    /**
     *插入时的值
     */
    private String values;
    /**
     *插入时的字段
     */
    private String columns;
    /**
     *企业id
     */
    private String enteId;
}

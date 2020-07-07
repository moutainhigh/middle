package com.njwd.entity.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:数据映射菜单
 * @Author: yuanman
 * @Date: 2019/11/25 10:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataMap extends DataMapKey {
    /**
     *源表：一对多中的多，或者少对多中的多
     */
    private String sourceTable;
    /**
     *目标表：一对多种的一，或者少对多种的少
     */
    private String targetTable;
    /**
     *映射表：保存源表和映射表映射数据的表
     */
    private String datamapTable;
    /**
     *源表中文名
     */
    private String sourceTableName;
    /**
     *目标表中文名
     */
    private String targetTableName;
    /**
     *源表的主键，多个用逗号拼接
     */
    private String sourceTableKey;
    /**
     *目标表的主键，多个用逗号拼接
     */
    private String targetTableKey;
    /**
     *源表用于展示的name字段
     */
    private String sourceTableColumn;
    /**
     *目标表用户展示的name字段
     */
    private String targetTableColumn;
    /**
     * 类型：一对多ONE_MANY,多对多MANY_MANY,一对一ONE_ONE
     */
    private String type;
    /**
     * 源表用于展示的code字段
     */
    private String sourceTableCode;
    /**
     * 目标表用于展示的code字段
     */
    private String targetTableCode;

}
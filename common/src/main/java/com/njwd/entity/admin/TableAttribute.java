package com.njwd.entity.admin;

import lombok.Data;

/**
 * @program: middle-data
 * @description: 表属性
 * @author: Chenfulian
 * @create: 2019-11-19 09:53
 **/
@Data
public class TableAttribute {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列描述
     */
    private String columnDesc;
    /**
     * 展示标识 0-不展示，1-展示
     */
    private String displayFlag;

    /**
     * 类别名
     */
    private String columnAlias;

    /**
     * 展示顺序
     */
    private Integer displayOrder;

    /**
     * 关联表名
     */
    private String joinTable;

    /**
     * 关联的字段名
     */
    private String joinColId;

    /**
     * 关联出的结果列
     */
    private String joinColName;

    @Override
    public String toString() {
        return "TableAttribute{" +
                "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", columnDesc='" + columnDesc + '\'' +
                ", displayFlag='" + displayFlag + '\'' +
                ", columnAlias='" + columnAlias + '\'' +
                '}';
    }
}
